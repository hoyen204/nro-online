package com.nro.nro_online.jdbc.daos;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nro.nro_online.card.Card;
import com.nro.nro_online.card.CollectionBook;
import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.manager.AchieveManager;
import com.nro.nro_online.manager.PetFollowManager;
import com.nro.nro_online.models.clan.Clan;
import com.nro.nro_online.models.clan.ClanMember;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.item.ItemTime;
import com.nro.nro_online.models.npc.specialnpc.BillEgg;
import com.nro.nro_online.models.npc.specialnpc.EggLinhThu;
import com.nro.nro_online.models.npc.specialnpc.MabuEgg;
import com.nro.nro_online.models.npc.specialnpc.MagicTree;
import com.nro.nro_online.models.player.*;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.models.task.Achievement;
import com.nro.nro_online.models.task.AchievementTemplate;
import com.nro.nro_online.models.task.TaskMain;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.AntiLogin;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.services.*;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.TimeUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

public class GodGK {
    private static final Gson GSON = new Gson();

    public static boolean login(Session session, AntiLogin al) {
        try (Connection conn = DBService.gI().getConnectionForLogin();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM account WHERE username = ? AND password = ? LIMIT 1")) {
            ps.setString(1, session.uu);
            ps.setString(2, session.pp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    session.userId = rs.getInt("account.id");
                    Session plInGame = Client.gI().getSession(session);
                    if (plInGame != null) {
                        Service.getInstance().sendThongBaoOK(plInGame, "Máy chủ tắt hoặc mất sóng!");
                        Client.gI().kickSession(plInGame);
                        Service.getInstance().sendThongBaoOK(session, "Máy chủ tắt hoặc mất sóng!");
                        return false;
                    }

                    session.isAdmin = rs.getBoolean("is_admin");
                    session.lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                    session.actived = rs.getBoolean("active");
                    session.goldBar = rs.getInt("account.thoi_vang");
                    session.vnd = rs.getInt("account.vnd");
                    session.poinCharging = rs.getInt("account.pointNap");
                    session.dataReward = rs.getString("reward");

                    if (rs.getTimestamp("last_time_login").getTime() > session.lastTimeLogout) {
                        Service.getInstance().sendThongBaoOK(session, "Tài khoản đang đăng nhập máy chủ khác!");
                        return false;
                    }

                    if (session.version < 225) {
                        Service.getInstance().sendThongBaoOK(session, "Hãy tải phiên bản từ Trang Chủ");
                        return false;
                    } else if (rs.getBoolean("ban")) {
                        Service.getInstance().sendThongBaoOK(session, "Tài khoản đã bị khóa do vi phạm điều khoản!");
                        return false;
                    } else {
                        long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                        int secondsPass = (int) ((System.currentTimeMillis() - lastTimeLogout) / 1000);
                        if (secondsPass < Manager.SECOND_WAIT_LOGIN && !session.isAdmin) {
                            Service.getInstance().sendThongBaoOK(session, "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass) + " giây để đăng nhập lại.");
                            return false;
                        }
                    }
                    al.reset();
                    return true;
                } else {
                    Service.getInstance().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                    al.wrong();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Player loadPlayer(Session session) {
        try (Connection conn = DBService.gI().getConnectionForLogin();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM player WHERE account_id = ? LIMIT 1")) {
            ps.setInt(1, session.userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Player player = new Player();
                    player.id = rs.getInt("id");
                    player.name = rs.getString("name");
                    player.head = rs.getShort("head");
                    player.gender = rs.getByte("gender");
                    player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                    player.server = session.server;

                    loadClan(player, rs);
                    loadEventData(player, rs, session);
                    loadInventory(player, rs);
                    loadLocation(player, rs);
                    loadPoints(player, rs);
                    loadMagicTree(player, rs);
                    loadBlackBallRewards(player, rs);
                    loadItems(player, rs, "items_body", player.inventory.itemsBody);
                    loadItems(player, rs, "items_bag", player.inventory.itemsBag);
                    loadItems(player, rs, "items_box", player.inventory.itemsBox);
                    loadItems(player, rs, "items_box_lucky_round", player.inventory.itemsBoxCrackBall);
                    loadFriends(player, rs);
                    loadEnemies(player, rs);
                    loadIntrinsic(player, rs);
                    loadMayDoTime(player, rs);
                    loadNewItemTime(player, rs);
                    loadItemTime(player, rs);
                    loadTasks(player, rs);
                    loadSideTasks(player, rs);
                    loadAchievements(player, rs);
                    loadEggs(player, rs);
                    loadCharms(player, rs);
                    loadSkills(player, rs);
                    loadSkillShortcut(player, rs);
                    loadCollectionBook(player, rs);
                    loadBodyItemsExtras(player);
                    loadFirstTimeLogin(player, rs);
                    loadBuyLimit(player, rs);
                    loadRewardLimit(player, rs);
                    loadChallenge(player, rs);
                    loadPet(player, rs);

                    player.nPoint.hp = getIntFromJsonArray(rs.getString("data_point"), 6); // plHp
                    player.nPoint.mp = getIntFromJsonArray(rs.getString("data_point"), 1); // plMp
                    session.player = player;

                    updateAccountLogin(conn, session);
                    PlayerService.gI().dailyLogin(player);
                    return player;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.dataLoadFailed = true;
        }
        return null;
    }

    private static int getIntFromJsonArray(String json, int index) {
        return GSON.fromJson(json, int[].class)[index];
    }

    private static void loadClan(Player player, ResultSet rs) throws SQLException {
        int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
        if (clanId != -1) {
            Clan clan = ClanService.gI().getClanById(clanId);
            if (clan != null) {
                for (ClanMember cm : clan.getMembers()) {
                    if (cm.id == player.id) {
                        clan.addMemberOnline(player);
                        player.clan = clan;
                        player.clanMember = cm;
                        player.setBuff(clan.getBuff());
                        break;
                    }
                }
            }
        }
    }

    private static void loadEventData(Player player, ResultSet rs, Session session) throws SQLException {
        player.event.setEventPoint(rs.getInt("event_point"));
        int[] skTet = GSON.fromJson(rs.getString("sk_tet"), int[].class);
        player.event.setTimeCookTetCake(skTet[0]);
        player.event.setTimeCookChungCake(skTet[1]);
        player.event.setCookingTetCake(skTet[2] == 1);
        player.event.setCookingChungCake(skTet[3] == 1);
        player.event.setReceivedLuckyMoney(skTet[4] == 1);
        player.event.setDiemTichLuy(session.diemTichNap);
        player.event.setMocNapDaNhan(rs.getInt("moc_nap"));
    }

    private static void loadInventory(Player player, ResultSet rs) throws SQLException {
        long[] inventoryData = GSON.fromJson(rs.getString("data_inventory"), long[].class);
        player.inventory.gold = inventoryData[0];
        player.inventory.gem = (int) inventoryData[1];
        player.inventory.ruby = (int) inventoryData[2];
        if (inventoryData.length >= 4) {
            player.inventory.goldLimit = inventoryData[3];
        }
    }

    private static void loadLocation(Player player, ResultSet rs) throws SQLException {
        int[] locationData = GSON.fromJson(rs.getString("data_location"), int[].class);
        player.location.x = locationData[0];
        player.location.y = locationData[1];
        int mapId = locationData[2];
        if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId) ||
                MapService.gI().isMapBanDoKhoBau(mapId) || mapId == 126 || mapId == ConstMap.CON_DUONG_RAN_DOC ||
                mapId == ConstMap.CON_DUONG_RAN_DOC_142 || mapId == ConstMap.CON_DUONG_RAN_DOC_143 || mapId == ConstMap.HOANG_MAC) {
            mapId = player.gender + 21;
            player.location.x = 300;
            player.location.y = 336;
        }
        player.zone = MapService.gI().getMapCanJoin(player, mapId);
    }

    private static void loadPoints(Player player, ResultSet rs) throws SQLException {
        long[] points = GSON.fromJson(rs.getString("data_point"), long[].class);
        player.nPoint.mpg = (int) points[2];
        player.nPoint.critg = (byte) points[3];
        player.nPoint.limitPower = (byte) points[4];
        player.nPoint.stamina = (short) points[5];
        player.nPoint.defg = (int) points[7];
        player.nPoint.tiemNang = points[8];
        player.nPoint.maxStamina = (short) points[9];
        player.nPoint.dameg = (int) points[10];
        player.nPoint.power = points[11];
        player.nPoint.hpg = (int) points[12];
    }

    private static void loadMagicTree(Player player, ResultSet rs) throws SQLException {
        long[] magicTreeData = GSON.fromJson(rs.getString("data_magic_tree"), long[].class);
        player.magicTree = new MagicTree(player, (byte) magicTreeData[2], (byte) magicTreeData[4],
                magicTreeData[3], magicTreeData[0] == 1, magicTreeData[1]);
    }

    private static void loadBlackBallRewards(Player player, ResultSet rs) throws SQLException {
        long[][] blackBallData = GSON.fromJson(rs.getString("data_black_ball"), long[][].class);
        for (int i = 0; i < blackBallData.length; i++) {
            player.rewardBlackBall.timeOutOfDateReward[i] = blackBallData[i][0];
            player.rewardBlackBall.lastTimeGetReward[i] = blackBallData[i][1];
        }
    }

    private static void loadItems(Player player, ResultSet rs, String column, List<Item> itemList) throws SQLException {
        List<HashMap<String, Object>> items = GSON.fromJson(rs.getString(column), new TypeToken<List<HashMap<String, Object>>>() {}.getType());
        for (HashMap<String, Object> data : items) {
            short tempId = ((Number) data.get("temp_id")).shortValue();
            Item item = tempId != -1 ? ItemService.gI().createNewItem(tempId, ((Number) data.get("quantity")).intValue()) :
                    ItemService.gI().createNullItem();
            if (tempId != -1) {
                List<int[]> options = (List<int[]>) data.get("option");
                for (int[] opt : options) {
                    item.itemOptions.add(new ItemOption(opt[0], opt[1]));
                }
                item.createTime = ((Number) data.get("create_time")).longValue();
                if (ItemService.gI().isOutOfDateTime(item)) {
                    item = ItemService.gI().createNullItem();
                }
            }
            itemList.add(item);
        }
    }

    private static void loadFriends(Player player, ResultSet rs) throws SQLException {
        List<Friend> friends = GSON.fromJson(rs.getString("friends"), new TypeToken<List<Friend>>() {}.getType());
        if (friends != null) player.friends.addAll(friends);
    }

    private static void loadEnemies(Player player, ResultSet rs) throws SQLException {
        List<Enemy> enemies = GSON.fromJson(rs.getString("enemies"), new TypeToken<List<Enemy>>() {}.getType());
        if (enemies != null) player.enemies.addAll(enemies);
    }

    private static void loadIntrinsic(Player player, ResultSet rs) throws SQLException {
        int[] intrinsicData = GSON.fromJson(rs.getString("data_intrinsic"), int[].class);
        player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById((byte) intrinsicData[0]);
        player.playerIntrinsic.intrinsic.param1 = (short) intrinsicData[1];
        player.playerIntrinsic.countOpen = (byte) intrinsicData[2];
        player.playerIntrinsic.intrinsic.param2 = (short) intrinsicData[3];
    }

    private static void loadMayDoTime(Player player, ResultSet rs) throws SQLException {
        int[] mayDoData = GSON.fromJson(rs.getString("time_may_do"), int[].class);
        int mayCoin = mayDoData[0];
        player.itemTime.timeMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - mayCoin);
        player.itemTime.isMayDo = mayCoin != 0;
    }

    private static void loadNewItemTime(Player player, ResultSet rs) throws SQLException {
        int[] newItemTime = GSON.fromJson(rs.getString("item_new_time"), int[].class);
        player.itemTime.lastTimeDuoiKhi = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - newItemTime[0]);
        player.itemTime.lastTimeBanhTrungThu1Trung = System.currentTimeMillis() - (ItemTime.TIME_ITEM - newItemTime[1]);
        player.itemTime.lastTimeBanhTrungThu2Trung = System.currentTimeMillis() - (ItemTime.TIME_ITEM - newItemTime[2]);
        player.itemTime.isDuoiKhi = newItemTime[0] != 0;
        player.itemTime.isBanhTrungThu1Trung = newItemTime[1] != 0;
        player.itemTime.isBanhTrungThu2Trung = newItemTime[2] != 0;

        if (newItemTime.length >= 7) {
            player.itemTime.lastTimerateHit = System.currentTimeMillis() - (ItemTime.TIME_ITEM - newItemTime[3]);
            player.itemTime.lastTimeDameDr = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - newItemTime[4]);
            player.itemTime.lastTimerateHPKI = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - newItemTime[5]);
            player.itemTime.lastTimeMaTroi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - newItemTime[6]);
            player.itemTime.rateDragonHit = newItemTime[3] != 0;
            player.itemTime.rateDame = newItemTime[4] != 0;
            player.itemTime.rateHPKI = newItemTime[5] != 0;
            player.itemTime.isMaTroi = newItemTime[6] != 0;
        }
    }

    private static void loadItemTime(Player player, ResultSet rs) throws SQLException {
        int[] itemTimeData = GSON.fromJson(rs.getString("data_item_time"), int[].class);
        player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[5]);
        player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[0]);
        player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[8]);
        player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[3]);
        player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[1]);
        player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - itemTimeData[2]);
        player.itemTime.isUseBoHuyet = itemTimeData[5] != 0;
        player.itemTime.isUseBoKhi = itemTimeData[0] != 0;
        player.itemTime.isUseGiapXen = itemTimeData[8] != 0;
        player.itemTime.isUseCuongNo = itemTimeData[3] != 0;
        player.itemTime.isUseAnDanh = itemTimeData[1] != 0;
        player.itemTime.isOpenPower = itemTimeData[2] != 0;

        if (itemTimeData.length >= 9) {
            player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - itemTimeData[4]);
            player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - itemTimeData[7]);
            player.itemTime.iconMeal = itemTimeData[6];
            player.itemTime.isUseMayDo = itemTimeData[4] != 0;
            player.itemTime.isEatMeal = itemTimeData[7] != 0;
        }

        if (itemTimeData.length >= 15) {
            player.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[14]);
            player.itemTime.lastTimeBoKhi2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[11]);
            player.itemTime.lastTimeGiapXen2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[12]);
            player.itemTime.lastTimeCuongNo2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM - itemTimeData[13]);
            player.itemTime.lastTimeBanhChung = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - itemTimeData[9]);
            player.itemTime.lastTimeBanhTet = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - itemTimeData[10]);
            player.itemTime.isUseBoHuyet2 = itemTimeData[14] != 0;
            player.itemTime.isUseBoKhi2 = itemTimeData[11] != 0;
            player.itemTime.isUseGiapXen2 = itemTimeData[12] != 0;
            player.itemTime.isUseCuongNo2 = itemTimeData[13] != 0;
            player.itemTime.isUseBanhChung = itemTimeData[9] != 0;
            player.itemTime.isUseBanhTet = itemTimeData[10] != 0;
        }
    }

    private static void loadTasks(Player player, ResultSet rs) throws SQLException {
        int[] taskData = GSON.fromJson(rs.getString("data_task"), int[].class);
        TaskMain taskMain = TaskService.gI().getTaskMainById(player, (byte) taskData[1]);
        taskMain.subTasks.get(taskData[2]).count = (short) taskData[0];
        taskMain.index = (byte) taskData[2];
        player.playerTask.taskMain = taskMain;
    }

    private static void loadSideTasks(Player player, ResultSet rs) throws SQLException {
        String sideTaskJson = rs.getString("data_side_task");
        if (sideTaskJson != null && !sideTaskJson.isEmpty()) {
            long[] sideTaskData = GSON.fromJson(sideTaskJson, long[].class);
            String format = "dd-MM-yyyy";
            LocalDateTime date = LocalDateTime.ofEpochSecond(sideTaskData[4], 0, ZoneOffset.UTC);
            if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(LocalDateTime.now(), format))) {
                player.playerTask.sideTask.level = (int) sideTaskData[0];
                player.playerTask.sideTask.count = (int) sideTaskData[1];
                player.playerTask.sideTask.leftTask = (int) sideTaskData[2];
                player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById((int) sideTaskData[3]);
                player.playerTask.sideTask.maxCount = (int) sideTaskData[5];
                player.playerTask.sideTask.receivedTime = sideTaskData[4];
            }
        }
    }

    private static void loadAchievements(Player player, ResultSet rs) throws SQLException {
        List<Achievement> achievements = GSON.fromJson(rs.getString("achivements"), new TypeToken<List<Achievement>>() {}.getType());
        if (achievements != null) {
            player.playerTask.achievements.addAll(achievements);
            List<AchievementTemplate> templates = AchieveManager.getInstance().getList();
            if (achievements.size() < templates.size()) {
                for (int i = achievements.size(); i < templates.size(); i++) {
                    AchievementTemplate template = templates.get(i);
                    Achievement achievement = new Achievement();
                    achievement.setId(template.getId());
                    achievement.setCount(0);
                    achievement.setFinish(false);
                    achievement.setReceive(false);
                    achievement.setName(template.getName());
                    achievement.setDetail(template.getDetail());
                    achievement.setMaxCount(template.getMaxCount());
                    achievement.setMoney(template.getMoney());
                    player.playerTask.achievements.add(achievement);
                }
            }
        }
    }

    private static void loadEggs(Player player, ResultSet rs) throws SQLException {
        HashMap<String, Long> mabuEgg = GSON.fromJson(rs.getString("data_mabu_egg"), new TypeToken<HashMap<String, Long>>() {}.getType());
        if (mabuEgg != null && mabuEgg.get("create_time") != null) {
            player.mabuEgg = new MabuEgg(player, mabuEgg.get("create_time"), mabuEgg.get("time_done"));
        }

        HashMap<String, Long> linhThuEgg = GSON.fromJson(rs.getString("data_egg_linhthu"), new TypeToken<HashMap<String, Long>>() {}.getType());
        if (linhThuEgg != null && linhThuEgg.get("create_time") != null) {
            player.egglinhthu = new EggLinhThu(player, linhThuEgg.get("create_time"), linhThuEgg.get("time_done"));
        }

        HashMap<String, Long> billEgg = GSON.fromJson(rs.getString("data_bill_egg"), new TypeToken<HashMap<String, Long>>() {}.getType());
        if (billEgg != null && billEgg.get("create_time") != null) {
            player.billEgg = new BillEgg(player, billEgg.get("create_time"), billEgg.get("time_done"));
        }
    }

    private static void loadCharms(Player player, ResultSet rs) throws SQLException {
        long[] charms = GSON.fromJson(rs.getString("data_charm"), long[].class);
        player.charms.tdTriTue = charms[0];
        player.charms.tdManhMe = charms[1];
        player.charms.tdDaTrau = charms[2];
        player.charms.tdOaiHung = charms[3];
        player.charms.tdBatTu = charms[4];
        player.charms.tdDeoDai = charms[5];
        player.charms.tdThuHut = charms[6];
        player.charms.tdDeTu = charms[7];
        player.charms.tdTriTue3 = charms[8];
        player.charms.tdTriTue4 = charms[9];
        if (charms.length >= 11) {
            player.charms.tdDeTuMabu = charms[10];
        }
    }

    private static void loadSkills(Player player, ResultSet rs) throws SQLException {
        List<long[]> skills = GSON.fromJson(rs.getString("skills"), new TypeToken<List<long[]>>() {}.getType());
        for (long[] skillData : skills) {
            Skill skill = skillData[2] != 0 ? SkillUtil.createSkill((int) skillData[0], (byte) skillData[2]) :
                    SkillUtil.createSkillLevel0((int) skillData[0]);
            skill.lastTimeUseThisSkill = skillData[1];
            player.playerSkill.skills.add(skill);
        }
    }

    private static void loadSkillShortcut(Player player, ResultSet rs) throws SQLException {
        byte[] shortcuts = GSON.fromJson(rs.getString("skills_shortcut"), byte[].class);
        System.arraycopy(shortcuts, 0, player.playerSkill.skillShortCut, 0, shortcuts.length);
        for (int i : player.playerSkill.skillShortCut) {
            Skill skill = player.playerSkill.getSkillbyId(i);
            if (skill != null && skill.damage > 0) {
                player.playerSkill.skillSelect = skill;
                break;
            }
        }
        if (player.playerSkill.skillSelect == null) {
            player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(
                    player.gender == ConstPlayer.TRAI_DAT ? Skill.DRAGON :
                            (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
        }
    }

    private static void loadCollectionBook(Player player, ResultSet rs) throws SQLException {
        List<Card> cards = GSON.fromJson(rs.getString("collection_book"), new TypeToken<List<Card>>() {}.getType());
        CollectionBook book = new CollectionBook(player);
        Map<Integer, Card> cardMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(cards)) {
            cardMap = cards.stream().collect(Collectors.toMap(Card::getId, Function.identity()));
        }
        book.setCards(cardMap);
        book.init();
        player.setCollectionBook(book);
    }

    private static void loadBodyItemsExtras(Player player) {
        List<Item> itemsBody = player.inventory.itemsBody;
        while (itemsBody.size() < 11) {
            itemsBody.add(ItemService.gI().createNullItem());
        }
        if (itemsBody.get(9).isNotNullItem()) {
            MiniPet.callMiniPet(player, itemsBody.get(9).template.id);
        }
        if (itemsBody.get(10).isNotNullItem()) {
            PetFollow pet = PetFollowManager.gI().findById(itemsBody.get(10).getId());
            player.setPetFollow(pet);
        }
    }

    private static void loadFirstTimeLogin(Player player, ResultSet rs) throws SQLException {
        player.canGetFirstTimeLogin = false;
        player.firstTimeLogin = rs.getTimestamp("firstTimeLogin").toLocalDateTime();
    }

    private static void loadBuyLimit(Player player, ResultSet rs) throws SQLException {
        byte[] buyLimit = GSON.fromJson(rs.getString("buy_limit"), byte[].class);
        System.arraycopy(buyLimit, 0, player.buyLimit, 0, buyLimit.length);
    }

    private static void loadRewardLimit(Player player, ResultSet rs) throws SQLException {
        byte[] rewardLimit = GSON.fromJson(rs.getString("reward_limit"), byte[].class);
        player.rewardLimit = rewardLimit;
    }

    private static void loadChallenge(Player player, ResultSet rs) throws SQLException {
        int[] challenge = GSON.fromJson(rs.getString("challenge"), int[].class);
        player.goldChallenge = challenge[0];
        player.levelWoodChest = challenge[1];
        player.receivedWoodChest = challenge[2] == 1;
    }

    private static void loadPet(Player player, ResultSet rs) throws SQLException {
        HashMap<String, Object> petInfo = GSON.fromJson(rs.getString("pet_info"), new TypeToken<HashMap<String, Object>>() {}.getType());
        if (petInfo != null && !petInfo.isEmpty()) {
            Pet pet = new Pet(player);
            pet.id = -player.id;
            pet.gender = ((Number) petInfo.get("gender")).byteValue();
            pet.typePet = ((Number) petInfo.get("is_mabu")).byteValue();
            pet.name = (String) petInfo.get("name");
            player.fusion.typeFusion = ((Number) petInfo.get("type_fusion")).byteValue();
            player.fusion.lastTimeFusion = System.currentTimeMillis() - (Fusion.TIME_FUSION - ((Number) petInfo.get("left_fusion")).intValue());
            pet.status = ((Number) petInfo.get("status")).byteValue();
            pet.setLever(petInfo.containsKey("level") ? ((Number) petInfo.get("level")).intValue() : 0);

            HashMap<String, Long> petPoint = GSON.fromJson(rs.getString("pet_point"), new TypeToken<HashMap<String, Long>>() {}.getType());
            pet.nPoint.stamina = petPoint.get("stamina").shortValue();
            pet.nPoint.maxStamina = petPoint.get("max_stamina").shortValue();
            pet.nPoint.hpg = petPoint.get("hpg").intValue();
            pet.nPoint.mpg = petPoint.get("mpg").intValue();
            pet.nPoint.dameg = petPoint.get("damg").intValue();
            pet.nPoint.defg = petPoint.get("defg").intValue();
            pet.nPoint.critg = petPoint.get("critg").intValue();
            pet.nPoint.power = petPoint.get("power");
            pet.nPoint.tiemNang = petPoint.get("tiem_nang");
            pet.nPoint.limitPower = petPoint.get("limit_power").byteValue();
            pet.nPoint.hp = petPoint.get("hp").intValue();
            pet.nPoint.mp = petPoint.get("mp").intValue();

            loadItems(player, rs, "pet_body", pet.inventory.itemsBody);
            while (pet.inventory.itemsBody.size() < 8) {
                pet.inventory.itemsBody.add(ItemService.gI().createNullItem());
            }

            List<int[]> petSkills = GSON.fromJson(rs.getString("pet_skill"), new TypeToken<List<int[]>>() {}.getType());
            for (int[] skillData : petSkills) {
                Skill skill = skillData[1] != 0 ? SkillUtil.createSkill(skillData[0], (byte) skillData[1]) :
                        SkillUtil.createSkillLevel0(skillData[0]);
                switch (skill.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                    skill.coolDown = 1000;
                    break;
                }
                pet.playerSkill.skills.add(skill);
            }
            while (pet.playerSkill.skills.size() < 5) {
                pet.playerSkill.skills.add(SkillUtil.createSkillLevel0(-1));
            }

            player.pet = pet;
        }
    }

    private static void updateAccountLogin(Connection conn, Session session) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE account SET last_time_login = ?, ip_address = ? WHERE id = ?")) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setString(2, session.ipAddress);
            ps.setInt(3, session.userId);
            ps.executeUpdate();
        }
    }
}