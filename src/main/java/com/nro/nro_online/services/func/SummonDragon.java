package com.nro.nro_online.services.func;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.*;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

import java.io.IOException;
import java.util.*;

public class SummonDragon {
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    public static final byte WISHED = 0;
    public static final byte TIME_UP = 1;

    public enum DragonType {
        SHENRON(0, ConstNpc.SUMMON_SHENRON, "Rồng Thần", new int[] { 0, 7, 14 }, false),
        BLACK_SHENRON(2, ConstNpc.SUMMON_BLACK_SHENRON, "Rồng Siêu Cấp", new int[] { 5 }, true),
        ICE_SHENRON(3, ConstNpc.SUMMON_ICE_SHENRON, "Rồng Siêu Cấp", null, true);

        final int id;
        final int summonMenuId;
        final String name;
        final int[] allowedMaps;
        final boolean isNewDragon;

        DragonType(int id, int summonMenuId, String name, int[] allowedMaps, boolean isNewDragon) {
            this.id = id;
            this.summonMenuId = summonMenuId;
            this.name = name;
            this.allowedMaps = allowedMaps;
            this.isNewDragon = isNewDragon;
        }
    }

    // Dragon Ball IDs
    public static final short NGOC_RONG_1_SAO = 14;
    public static final short NGOC_RONG_2_SAO = 15;
    public static final short NGOC_RONG_3_SAO = 16;
    public static final short NGOC_RONG_4_SAO = 17;
    public static final short NGOC_RONG_5_SAO = 18;
    public static final short NGOC_RONG_6_SAO = 19;
    public static final short NGOC_RONG_7_SAO = 20;
    public static final short NGOC_RONG_SIEU_CAP = 1015;
    public static final short[] NGOC_RONG_BANG = { 2045, 2046, 2047, 2048, 2049, 2050, 2051 };

    public static final Map<DragonType, short[]> REQUIRED_BALLS = new HashMap<>();

    static {
        REQUIRED_BALLS.put(DragonType.SHENRON,
                new short[] { NGOC_RONG_1_SAO, NGOC_RONG_2_SAO, NGOC_RONG_3_SAO, NGOC_RONG_4_SAO, NGOC_RONG_5_SAO, NGOC_RONG_6_SAO, NGOC_RONG_7_SAO });
        REQUIRED_BALLS.put(DragonType.BLACK_SHENRON, new short[] { NGOC_RONG_SIEU_CAP });
        REQUIRED_BALLS.put(DragonType.ICE_SHENRON, NGOC_RONG_BANG);
    }

    // Wish Menus
    public static final String SHENRON_SAY = "Ta sẽ ban cho người 1 điều ước, ngươi có 5 phút, hãy suy nghĩ thật kỹ trước khi quyết định";
    public static final String BLACK_SHENRON_SAY = SHENRON_SAY + "\n1) Cải trang Gohan Siêu Nhân (60 ngày)\n2) Cải trang Biden Siêu Nhân (60 ngày)\n3) Cải trang Cô nương Siêu Nhân (60 ngày)\n4) Pet Thỏ Ốm (60 ngày)\n5) Pet Thỏ Mập (60 ngày)";
    public static final String ICE_SHENRON_SAY = SHENRON_SAY + "\n1) Tăng 50% TNSM trong 30p (bản thân + đệ)\n2) 5k hồng ngọc\n3) Đổi skill 2-3 đệ\n4) Đổi skill 3-4 đệ\n5) Tăng 5% sức đánh hợp thể trong 30p\n6) Tăng 5% HP/KI hợp thể trong 30p";

    public static final String[] SHENRON_1_STAR_WISHES_1 = { "Giàu có\n+2 Tỏi\nVàng", "Găng tay\nđang mang\nlên 1 cấp", "Chí mạng\nGốc +2%",
            "Thay\nChiêu 2-3\nĐệ tử", "Điều ước\nkhác" };
    public static final String[] SHENRON_1_STAR_WISHES_2 = { "Đẹp trai\nnhất\nVũ trụ", "Giàu có\n+5000\nNgọc hồng", "+200 Tr\nSức mạnh\nvà tiềm\nnăng",
            "Găng tay đệ\nđang mang\nlên 1 cấp", "Điều ước\nkhác" };
    public static final String[] SHENRON_2_STARS_WISHES = { "Giàu có\n+2000\nNgọc hồng", "+20 Tr\nSức mạnh\nvà tiềm năng", "Giàu có\n+200 Tr\nVàng" };
    public static final String[] SHENRON_3_STARS_WISHES = { "Giàu có\n+200\nNgọc hồng", "+2 Tr\nSức mạnh\nvà tiềm năng", "Giàu có\n+20 Tr\nVàng" };
    public static final String[] BLACK_SHENRON_WISHES = { "Điều\nước 1", "Điều\nước 2", "Điều\nước 3", "Điều\nước 4", "Điều\nước 5" };
    public static final String[] ICE_SHENRON_WISHES = { "Điều\nước 1", "Điều\nước 2", "Điều\nước 3", "Điều\nước 4", "Điều\nước 5", "Điều\nước 6" };
    public static final String SUMMON_SHENRON_TUTORIAL
            = "Có 3 cách gọi rồng thần. Gọi từ ngọc 1 sao, gọi từ ngọc 2 sao, hoặc gọi từ ngọc 3 sao\n"
            + "Các ngọc 4 sao đến 7 sao không thể gọi rồng thần được\n"
            + "Để gọi rồng 1 sao cần ngọc từ 1 sao đến 7 sao\n"
            + "Để gọi rồng 2 sao cần ngọc từ 2 sao đến 7 sao\n"
            + "Để gọi rồng 3 sao cần ngọc từ 3 sao đến 7sao\n"
            + "Điều ước rồng 3 sao: Capsule 3 sao, hoặc 2 triệu sức mạnh, hoặc 200k vàng\n"
            + "Điều ước rồng 2 sao: Capsule 2 sao, hoặc 20 triệu sức mạnh, hoặc 2 triệu vàng\n"
            + "Điều ước rồng 1 sao: Capsule 1 sao, hoặc 200 triệu sức mạnh, hoặc 20 triệu vàng, hoặc đẹp trai, hoặc....\n"
            + "Ngọc rồng sẽ mất ngay khi gọi rồng dù bạn có ước hay không\n"
            + "Quá 5 phút nếu không ước rồng thần sẽ bay mất";

    // -------------------------------------------------------------------------
    // Instance Variables
    // -------------------------------------------------------------------------

    private static SummonDragon instance;
    public final Map<Player, Byte> playerDragonStars = new HashMap<>();
    public final int resummonCooldown = 300_000; // 5 minutes
    public final int wishTimeout = 300_000; // 5 minutes
    public long lastSummonTime;
    public long lastWishTime;
    public boolean isDragonActive;
    public DragonType activeDragonType;
    public Player summoner;
    public int summonerId = -1;
    public Zone summonZone;
    public byte shenronStar = -1;
    public int currentMenu = -1;
    public byte currentWish = -1;
    public boolean isPlayerDisconnected;

    private final Thread updateThread;
    private volatile boolean isRunning;

    // -------------------------------------------------------------------------
    // Constructor and Singleton
    // -------------------------------------------------------------------------

    private SummonDragon() {
        updateThread = new Thread(this::updateLoop);
        isRunning = true;
        updateThread.start();
    }

    public static SummonDragon gI() {
        if (instance == null) {
            instance = new SummonDragon();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Summoning Logic
    // -------------------------------------------------------------------------

    public void openSummonMenu(Player player, byte dragonStar, DragonType dragonType) {
        playerDragonStars.put(player, dragonStar);
        String menuText = dragonType == DragonType.SHENRON ? "Gọi\n" + dragonType.name + "\n" + dragonStar + " Sao" : "Đồng ý";
        NpcService.gI().createMenuConMeo(player, dragonType.summonMenuId, -1, "Bạn muốn gọi " + dragonType.name + "?",
                dragonType == DragonType.SHENRON ? new String[] { "Hướng\ndẫn thêm\n(mới)", menuText } : new String[] { menuText, "Từ chối" });
    }

    public void summonDragon(Player player, DragonType dragonType) {
        if (!isValidMap(player, dragonType)) {
            Service.getInstance().sendThongBao(player, "Chỉ được gọi " + dragonType.name + " ở " + getAllowedMapNames(dragonType));
            return;
        }
        if (!checkDragonBalls(player, dragonType))
            return;
        if (isDragonActive) {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (!Util.canDoWithTime(lastSummonTime, resummonCooldown)) {
            int timeLeft = (int) ((resummonCooldown - (System.currentTimeMillis() - lastSummonTime)) / 1000);
            Service.getInstance().sendThongBao(player, "Vui lòng đợi " + (timeLeft < 60 ? timeLeft + " giây" : timeLeft / 60 + " phút") + " nữa");
            return;
        }

        summoner = player;
        summonerId = (int) player.id;
        summonZone = player.zone;
        shenronStar = playerDragonStars.getOrDefault(player, (byte) -1);
        removeDragonBalls(player, dragonType);
        InventoryService.gI().sendItemBags(player);
        sendSummonNotification(dragonType);
        activateDragon(dragonType, true);
        showWishMenu(player);
    }

    private boolean isValidMap(Player player, DragonType dragonType) {
        int mapId = player.zone.map.mapId;
        if (dragonType.allowedMaps == null)
            return MapService.gI().isMapBill(mapId);
        for (int allowedMap : dragonType.allowedMaps) {
            if (mapId == allowedMap)
                return true;
        }
        return false;
    }

    private String getAllowedMapNames(DragonType dragonType) {
        if (dragonType == DragonType.SHENRON)
            return "ngôi làng trước nhà";
        if (dragonType == DragonType.BLACK_SHENRON)
            return "Đảo Kame";
        return "map Bill";
    }

    private boolean checkDragonBalls(Player player, DragonType dragonType) {
        short[] requiredBalls = REQUIRED_BALLS.get(dragonType);
        if (dragonType == DragonType.SHENRON) {
            byte star = playerDragonStars.getOrDefault(player, (byte) 1);
            int startIndex = switch (star) {
                case 1 -> 0;
                case 2 -> 1;
                default -> 2;
            };
            for (int i = startIndex; i < requiredBalls.length; i++) {
                if (!InventoryService.gI().existItemBag(player, requiredBalls[i])) {
                    Service.getInstance().sendThongBao(player, "Bạn còn thiếu 1 viên ngọc rồng " + (i + 1) + " sao");
                    return false;
                }
            }
        } else {
            for (short ball : requiredBalls) {
                if (!InventoryService.gI().existItemBag(player, ball)) {
                    Service.getInstance().sendThongBao(player,
                            "Bạn còn thiếu 1 viên ngọc rồng " + (dragonType == DragonType.ICE_SHENRON ? (ball - 2044) + " sao" : "siêu cấp"));
                    return false;
                }
            }
        }
        return true;
    }

    private void removeDragonBalls(Player player, DragonType dragonType) {
        short[] requiredBalls = REQUIRED_BALLS.get(dragonType);
        if (dragonType == DragonType.SHENRON) {
            byte star = playerDragonStars.getOrDefault(player, (byte) 1);
            int startIndex = switch (star) {
                case 1 -> 0;
                case 2 -> 1;
                default -> 2;
            };
            for (int i = startIndex; i < requiredBalls.length; i++) {
                InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBagByTemp(player, requiredBalls[i]), 1);
            }
        } else {
            for (short ball : requiredBalls) {
                InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBagByTemp(player, ball), 1);
            }
        }
    }

    private void sendSummonNotification(DragonType dragonType) {
        try (Message msg = new Message(-25)) {
            msg.writer().writeUTF(summoner.name + " vừa gọi " + dragonType.name + " tại " + summoner.zone.map.mapName + " khu vực " + summoner.zone.zoneId);
            Service.getInstance().sendMessAllPlayerIgnoreMe(summoner, msg);
        } catch (Exception e) {
            Log.error(SummonDragon.class, e);
        }
    }

    private void activateDragon(DragonType dragonType, boolean appear) {
        isDragonActive = appear;
        activeDragonType = appear ? dragonType : null;
        lastWishTime = appear ? System.currentTimeMillis() : 0;
        if (dragonType.isNewDragon) {
            activateNewDragon(appear, dragonType == DragonType.BLACK_SHENRON ? (byte) 60 : (byte) 59);
        } else {
            activateShenron(appear);
        }
    }

    private void activateShenron(boolean appear) {
        try (Message msg = new Message(-83)) {
            msg.writer().writeByte(appear ? 0 : 1);
            if (appear) {
                writeDragonData(msg);
                msg.writer().writeByte(DragonType.SHENRON.id);
            }
            Service.getInstance().sendMessAllPlayerInMap(summoner, msg);
        } catch (Exception e) {
            Log.error(SummonDragon.class, e);
        }
    }

    private void activateNewDragon(boolean appear, byte effect) {
        try (Message msg = new Message(com.nro.nro_online.consts.Cmd.CALL_DRAGON)) {
            msg.writer().writeByte(appear ? 0 : 1);
            if (appear) {
                writeDragonData(msg);
                msg.writer().writeByte(1);
                summonZone.effDragon = effect;
            }
            Service.getInstance().sendMessAllPlayerInMap(summoner, msg);
        } catch (Exception e) {
            Log.error(SummonDragon.class, e);
        }
    }

    private void writeDragonData(Message msg) throws IOException {
        msg.writer().writeShort(summonZone.map.mapId);
        msg.writer().writeShort(summonZone.map.bgId);
        msg.writer().writeByte(summonZone.zoneId);
        msg.writer().writeInt(summonerId);
        msg.writer().writeUTF("");
        msg.writer().writeShort(summoner.location.x);
        msg.writer().writeShort(summoner.location.y);
    }

    // -------------------------------------------------------------------------
    // Wish Handling
    // -------------------------------------------------------------------------

    private void showWishMenu(Player player) {
        byte star = playerDragonStars.getOrDefault(player, shenronStar);
        if (activeDragonType == DragonType.SHENRON) {
            if (star == 1)
                NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
            else if (star == 2)
                NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_2, SHENRON_SAY, SHENRON_2_STARS_WISHES);
            else if (star == 3)
                NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_3, SHENRON_SAY, SHENRON_3_STARS_WISHES);
        } else if (activeDragonType == DragonType.BLACK_SHENRON) {
            NpcService.gI().createMenuRongThieng(player, ConstNpc.BLACK_SHENRON, BLACK_SHENRON_SAY, BLACK_SHENRON_WISHES);
        } else if (activeDragonType == DragonType.ICE_SHENRON) {
            NpcService.gI().createMenuRongThieng(player, ConstNpc.ICE_SHENRON, ICE_SHENRON_SAY, ICE_SHENRON_WISHES);
        }
    }

    public void showConfirmWish(Player player, int menu, byte select) {
        this.currentMenu = menu;
        this.currentWish = select;
        String wishText = getWishText(menu, select);
        NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_CONFIRM, "Ngươi có chắc muốn ước?", wishText, "Từ chối");
    }

    private String getWishText(int menu, int select) {
        switch (menu) {
        case ConstNpc.SHENRON_1_1:
            return SHENRON_1_STAR_WISHES_1[select];
        case ConstNpc.SHENRON_1_2:
            return SHENRON_1_STAR_WISHES_2[select];
        case ConstNpc.SHENRON_2:
            return SHENRON_2_STARS_WISHES[select];
        case ConstNpc.SHENRON_3:
            return SHENRON_3_STARS_WISHES[select];
        case ConstNpc.BLACK_SHENRON:
            return BLACK_SHENRON_WISHES[select];
        case ConstNpc.ICE_SHENRON:
            return ICE_SHENRON_WISHES[select];
        default:
            return "Không xác định";
        }
    }

    public void confirmWish() {
        if (summoner == null)
            return;
        switch (currentMenu) {
        case ConstNpc.SHENRON_1_1 -> handleShenron1Wish1();
        case ConstNpc.SHENRON_1_2 -> handleShenron1Wish2();
        case ConstNpc.SHENRON_2 -> handleShenron2Wish();
        case ConstNpc.SHENRON_3 -> handleShenron3Wish();
        case ConstNpc.BLACK_SHENRON -> handleBlackShenronWish();
        case ConstNpc.ICE_SHENRON -> handleIceShenronWish();
        }
        dragonLeave(WISHED);
    }

    private void handleShenron1Wish1() {
        switch (currentWish) {
        case 0:
            summoner.inventory.addGold(2_000_000_000);
            PlayerService.gI().sendInfoHpMpMoney(summoner);
            break;
        case 1:
            upgradeGloves(summoner, summoner);
            break;
        case 2:
            if (summoner.nPoint.critg < 9)
                summoner.nPoint.critg += 2;
            else
                reOpenWishMenu("Chí mạng đã tối đa");
            break;
        case 3:
            if (summoner.pet != null && summoner.pet.playerSkill.skills.get(1).skillId != -1) {
                summoner.pet.openSkill2();
                if (summoner.pet.playerSkill.skills.get(2).skillId != -1)
                    summoner.pet.openSkill3();
            } else
                reOpenWishMenu("Đệ tử cần chiêu 2");
            break;
        }
    }

    private void handleShenron1Wish2() {
        switch (currentWish) {
        case 0:
            addAvatarItem(summoner);
            break;
        case 1:
            summoner.inventory.ruby += 5_000;
            PlayerService.gI().sendInfoHpMpMoney(summoner);
            break;
        case 2:
            Service.getInstance().addSMTN(summoner, (byte) 2, 200_000_000, false);
            break;
        case 3:
            if (summoner.pet != null)
                upgradeGloves(summoner.pet, summoner);
            else
                reOpenWishMenu("Không có đệ tử");
            break;
        }
    }

    private void handleShenron2Wish() {
        switch (currentWish) {
        case 0:
            summoner.inventory.gem += 2_000;
            PlayerService.gI().sendInfoHpMpMoney(summoner);
            break;
        case 1:
            Service.getInstance().addSMTN(summoner, (byte) 2, 20_000_000, false);
            break;
        case 2:
            summoner.inventory.addGold(2_000_000);
            PlayerService.gI().sendInfoHpMpMoney(summoner);
            break;
        }
    }

    private void handleShenron3Wish() {
        switch (currentWish) {
        case 0:
            summoner.inventory.gem += 200;
            PlayerService.gI().sendInfoHpMpMoney(summoner);
            break;
        case 1:
            Service.getInstance().addSMTN(summoner, (byte) 2, 2_000_000, false);
            break;
        case 2:
            summoner.inventory.addGold(200_000);
            PlayerService.gI().sendInfoHpMpMoney(summoner);
            break;
        }
    }

    private void handleBlackShenronWish() {
        short[] itemIds = { 989, 990, 991, 1039, 1040 };
        int index = currentWish;
        if (index >= 0 && index < itemIds.length)
            addRandomStatItem(summoner, itemIds[index], 60, new int[] { 20, 30 }, new int[] { 5, 15 });
    }

    private void handleIceShenronWish() {
        switch (currentWish) {
        case 1:
            summoner.inventory.ruby += 5_000;
            PlayerService.gI().sendInfoHpMpMoney(summoner);
            break;
        case 2:
            if (summoner.pet != null && summoner.pet.playerSkill.skills.get(1).skillId != -1 && summoner.pet.playerSkill.skills.get(2).skillId != -1) {
                summoner.pet.openSkill2();
                summoner.pet.openSkill3();
            } else
                reOpenWishMenu("Đệ tử cần chiêu 3");
            break;
        case 3:
            if (summoner.pet != null && summoner.pet.playerSkill.skills.get(2).skillId != -1 && summoner.pet.playerSkill.skills.get(3).skillId != -1) {
                summoner.pet.openSkill3();
                summoner.pet.openSkill4();
            } else
                reOpenWishMenu("Đệ tử cần chiêu 4");
            break;
        case 4:
            activateBuff(summoner, true, false);
            break;
        case 5:
            activateBuff(summoner, false, true);
            break;
        }
    }

    private void upgradeGloves(Player target, Player summoner) {
        Item gloves = target.inventory.itemsBody.get(2);
        if (!gloves.isNotNullItem()) {
            Service.getInstance().sendThongBao(summoner, target == summoner ? "Ngươi không đeo găng" : "Đệ ngươi không đeo găng");
            reOpenWishMenu(null);
            return;
        }
        int level = gloves.itemOptions.stream().filter(io -> io.optionTemplate.id == 72).findFirst().map(io -> io.param).orElse(0);
        if (level >= 7) {
            Service.getInstance().sendThongBao(summoner, "Găng tay đã đạt cấp tối đa");
            reOpenWishMenu(null);
            return;
        }
        if (level == 0)
            gloves.itemOptions.add(new ItemOption(72, 1));
        else
            gloves.itemOptions.stream().filter(io -> io.optionTemplate.id == 72).forEach(io -> io.param++);
        gloves.itemOptions.stream().filter(io -> io.optionTemplate.id == 0).forEach(io -> io.param += io.param * 10 / 100);
        if (target == summoner)
            InventoryService.gI().sendItemBody(summoner);
        else
            Service.getInstance().point(summoner);
    }

    private void addAvatarItem(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            reOpenWishMenu("Hành trang đã đầy");
            return;
        }
        short itemId = (short) (player.gender == ConstPlayer.TRAI_DAT ? 227 : player.gender == ConstPlayer.NAMEC ? 228 : 229);
        Item avatar = ItemService.gI().createNewItem(itemId);
        avatar.itemOptions.add(new ItemOption(97, Util.nextInt(5, 10)));
        avatar.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
        InventoryService.gI().addItemBag(player, avatar, 0);
        InventoryService.gI().sendItemBags(player);
    }

    private void addRandomStatItem(Player player, short itemId, int days, int[] mainRange, int[] secondaryRange) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            reOpenWishMenu("Hành trang đã đầy");
            return;
        }
        Item item = ItemService.gI().createNewItem(itemId);
        item.itemOptions.add(new ItemOption(50, Util.nextInt(mainRange[0], mainRange[1])));
        item.itemOptions.add(new ItemOption(77, Util.nextInt(mainRange[0], mainRange[1])));
        item.itemOptions.add(new ItemOption(103, Util.nextInt(mainRange[0], mainRange[1])));
        if (itemId < 1000)
            item.itemOptions.add(new ItemOption(5, Util.nextInt(mainRange[0], mainRange[1])));
        item.itemOptions.add(new ItemOption(47, Util.nextInt(secondaryRange[0], secondaryRange[1])));
        item.itemOptions.add(new ItemOption(93, days));
        InventoryService.gI().addItemBag(player, item, 0);
        InventoryService.gI().sendItemBags(player);
    }

    private void activateBuff(Player player, boolean rateDame, boolean rateHPKI) {
        if (rateDame) {
            player.itemTime.lastTimeDameDr = System.currentTimeMillis();
            player.itemTime.rateDame = true;
        } else {
            player.itemTime.lastTimerateHPKI = System.currentTimeMillis();
            player.itemTime.rateHPKI = true;
        }
        Service.getInstance().point(player);
        ItemTimeService.gI().sendAllItemTime(player);
    }

    public void reOpenWishMenu(String errorMessage) {
        if (errorMessage != null)
            Service.getInstance().sendThongBao(summoner, errorMessage);
        switch (currentMenu) {
        case ConstNpc.SHENRON_1_1:
            NpcService.gI().createMenuRongThieng(summoner, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
            break;
        case ConstNpc.SHENRON_1_2:
            NpcService.gI().createMenuRongThieng(summoner, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
            break;
        case ConstNpc.SHENRON_2:
            NpcService.gI().createMenuRongThieng(summoner, ConstNpc.SHENRON_2, SHENRON_SAY, SHENRON_2_STARS_WISHES);
            break;
        case ConstNpc.SHENRON_3:
            NpcService.gI().createMenuRongThieng(summoner, ConstNpc.SHENRON_3, SHENRON_SAY, SHENRON_3_STARS_WISHES);
            break;
        case ConstNpc.BLACK_SHENRON:
            NpcService.gI().createMenuRongThieng(summoner, ConstNpc.BLACK_SHENRON, BLACK_SHENRON_SAY, BLACK_SHENRON_WISHES);
            break;
        case ConstNpc.ICE_SHENRON:
            NpcService.gI().createMenuRongThieng(summoner, ConstNpc.ICE_SHENRON, ICE_SHENRON_SAY, ICE_SHENRON_WISHES);
            break;
        }
    }

    public void dragonLeave(byte type) {
        if (summoner == null)
            return;
        String message = type == WISHED ? "Điều ước của ngươi đã trở thành sự thật\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai" :
                "Ta buồn ngủ quá rồi\nHẹn gặp ngươi lần sau, ta đi đây, bái bai";
        NpcService.gI().createMenuRongThieng(summoner, type == WISHED ? -1 : ConstNpc.IGNORE_MENU, message);
        activateDragon(activeDragonType, false);
        resetState();
    }

    private void resetState() {
        lastSummonTime = System.currentTimeMillis();
        isDragonActive = false;
        activeDragonType = null;
        summoner = null;
        summonerId = -1;
        summonZone = null;
        shenronStar = -1;
        currentMenu = -1;
        currentWish = -1;
        if (summonZone != null)
            summonZone.effDragon = -1;
    }

    // -------------------------------------------------------------------------
    // update Loop
    // -------------------------------------------------------------------------

    private void updateLoop() {
        while (isRunning) {
            try {
                if (isDragonActive && Util.canDoWithTime(lastWishTime, wishTimeout)) {
                    dragonLeave(TIME_UP);
                } else if (isDragonActive && isPlayerDisconnected) {
                    handlePlayerReconnect();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.error(SummonDragon.class, e);
            }
        }
    }

    private void handlePlayerReconnect() {
        synchronized (summonZone.getPlayers()) {
            for (Player player : summonZone.getPlayers()) {
                if (player.id == summonerId) {
                    summoner = player;
                    activateDragon(activeDragonType, true);
                    showWishMenu(player);
                    isPlayerDisconnected = false;
                    break;
                }
            }
        }
    }

    public void setPlayerDisconnected(boolean disconnected) {
        this.isPlayerDisconnected = disconnected;
    }
}