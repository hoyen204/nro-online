package com.nro.nro_online.event;

import com.nro.nro_online.consts.*;
import com.nro.nro_online.dialog.ConfirmDialog;
import com.nro.nro_online.lib.RandomCollection;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.event.Beetle;
import com.nro.nro_online.models.boss.event.EscortedBoss;
import com.nro.nro_online.models.boss.event.NightLord;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Inventory;
import com.nro.nro_online.models.player.Pet;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.EffectSkillService;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.ItemTimeService;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

import java.util.Arrays;
import java.util.List;

public class SummerEvent extends Event {

    private int uniqueID = -99999;

    @Override
    public void init() {
        initNpc();
    }

    private byte generateUniqueID() {
        return (byte) uniqueID++;
    }

    @Override
    public void initNpc() {
        Map map = MapService.gI().getMapById(ConstMap.LANG_ARU);
        if (map == null) return;

        Npc npc = new Npc(map.mapId, 1, 731, 432, ConstNpc.EVENT, 10812) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Yo yo, sự kiện hè 2023 đang nóng bỏng!\nPHÁ TAN CƠN NÓNG, chơi hết mình nào!",
                            "Đổi Quà\nSự Kiện", "Tắm\nNước Nóng", "Bắt\nSâu Bọ", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                int menuID = player.iDMark.getIndexMenu();
                if (!player.iDMark.isBaseMenu()) handleSubMenu(player, menuID, select);
                else handleBaseMenu(player, select);
            }

            private void handleBaseMenu(Player player, int select) {
                switch (select) {
                case 0 -> createOtherMenu(player, ConstNpc.ORTHER_MENU,
                        "Đổi quà xịn nè, chọn đi!", "Đổi\nVỏ Ốc", "Đổi\nVỏ Sò",
                        "Đổi\nCon Cua", "Đổi\nSao Biển", "Đổi Quà\nĐặc Biệt", "Từ Chối");
                case 1 -> createOtherMenu(player, ConstNpc.ORTHER_MENU1,
                        "Tắm nước nóng sảng khoái nè!", "Chế Tạo\nBồn Tắm Gỗ",
                        "Chế Tạo\nBồn Tắm Vàng", "Từ chối");
                case 2 -> createOtherMenu(player, ConstNpc.ORTHER_MENU2,
                        "Bắt sâu bọ đi, vui lắm!", "Tặng\nBọ Cánh Cứng",
                        "Tặng\nNgài Đêm", "Từ chối");
                }
            }

            private void handleSubMenu(Player player, int menuID, int select) {
                switch (menuID) {
                case ConstNpc.ORTHER_MENU -> handleExchange(player, select);
                case ConstNpc.ORTHER_MENU1 -> handleCraftBath(player, select);
                case ConstNpc.ORTHER_MENU2 -> handleInsectGift(player, select);
                }
            }

            private void handleExchange(Player player, int select) {
                int[] itemsNeeded = select < 3 ? new int[]{select == 0 ? ConstItem.VO_OC : select == 1 ? ConstItem.VO_SO : ConstItem.CON_CUA}
                        : new int[]{ConstItem.VO_OC, ConstItem.VO_SO, ConstItem.CON_CUA, ConstItem.SAO_BIEN};
                if (!checkAndRemoveItems(player, itemsNeeded)) return;

                RandomCollection<Integer> rewards = getExchangeRewards(select);
                Item reward = createRewardItem(rewards.next(), select >= 3);
                addItemToBag(player, reward, "Nhận quà xịn: " + reward.template.name + " 😎");
            }

            private void handleCraftBath(Player player, int select) {
                if (select > 1) return;
                int[] costs = select == 0 ? new int[]{150000000, 0} : new int[]{300000000, 15};
                String msg = buildCraftDialog(select, player.inventory);
                ConfirmDialog dialog = new ConfirmDialog(msg, () -> craftBath(player, select, costs));
                dialog.show(player);
            }

            private void handleInsectGift(Player player, int select) {
                if (select > 1) return;
                int itemId = select == 0 ? ConstItem.BO_CANH_CUNG : ConstItem.NGAI_DEM;
                Item item = InventoryService.gI().findItem(player, itemId, 1);
                if (item == null) {
                    Service.getInstance().sendThongBao(player, "Không đủ " + (select == 0 ? "Bọ Cánh Cứng" : "Ngài Đêm") + " nha! 😛");
                    return;
                }
                InventoryService.gI().subQuantityItemsBag(player, item, 1);
                Item reward = createRewardItem(getInsectGiftRewards().next(), false);
                addItemToBag(player, reward, "Nhận được " + reward.template.name + ", ngon lành! 😜");
            }

            private boolean checkAndRemoveItems(Player player, int[] itemIds) {
                for (int id : itemIds) {
                    if (InventoryService.gI().findItem(player, id, 99) == null) {
                        Service.getInstance().sendThongBao(player, "Thiếu đồ rồi bro, kiếm thêm đi! 😅");
                        return false;
                    }
                }
                for (int id : itemIds) {
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player, id, 99), 99);
                }
                return true;
            }

            private boolean checkAndRemoveItems(Player player, int[] itemIds, int[] quantities) {
                if (itemIds.length != quantities.length) {
                    Service.getInstance().sendThongBao(player, "Lỗi hệ thống, báo admin ngay! 😱");
                    return false;
                }

                for (int i = 0; i < itemIds.length; i++) {
                    Item item = InventoryService.gI().findItem(player, itemIds[i], quantities[i]);
                    if (item == null) {
                        Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu rồi, đi kiếm thêm đi! 😛");
                        return false;
                    }
                }

                for (int i = 0; i < itemIds.length; i++) {
                    Item item = InventoryService.gI().findItem(player, itemIds[i], quantities[i]);
                    InventoryService.gI().subQuantityItemsBag(player, item, quantities[i]);
                }
                return true;
            }

            private RandomCollection<Integer> getExchangeRewards(int select) {
                RandomCollection<Integer> rd = new RandomCollection<>();
                switch (select) {
                case 0 -> { rd.add(1, ConstItem.BO_HOA_HONG); rd.add(1, ConstItem.BO_HOA_VANG); }
                case 1 -> { rd.add(1, ConstItem.PET_BO_CANH_CUNG); rd.add(1, ConstItem.PET_NGAI_DEM); }
                case 2 -> { rd.add(1, 1144); rd.add(1, 897); }
                case 3 -> { rd.add(1, ConstItem.MANH_AO); rd.add(1, ConstItem.MANH_QUAN); rd.add(1, ConstItem.MANH_GIAY); }
                default -> { rd.add(1, ConstItem.CAI_TRANG_AO_VIT_CAM); rd.add(1, ConstItem.CAI_TRANG_AO_TRANG_HOA); rd.add(1, ConstItem.CAI_TRANG_NON_ROM_MUA_HE); }
                }
                return rd;
            }

            private RandomCollection<Integer> getInsectGiftRewards() {
                RandomCollection<Integer> rd = new RandomCollection<>();
                rd.add(1, 1252); rd.add(1, 1253); rd.add(1, ConstItem.CAY_KEM);
                rd.add(1, ConstItem.CA_HEO); rd.add(1, ConstItem.DIEU_RONG); rd.add(1, ConstItem.CON_DIEU);
                return rd;
            }

            private Item createRewardItem(int itemId, boolean isSpecial) {
                Item item = ItemService.gI().createNewItem((short) itemId);
                int type = item.template.type;
                item.itemOptions.add(new ItemOption(50, Util.nextInt(isSpecial ? 20 : 5, isSpecial ? 40 : 15)));
                item.itemOptions.add(new ItemOption(77, Util.nextInt(isSpecial ? 20 : 5, isSpecial ? 40 : 15)));
                item.itemOptions.add(new ItemOption(103, Util.nextInt(isSpecial ? 20 : 5, isSpecial ? 40 : 15)));
                if (type != 11 && type != 23) item.itemOptions.add(new ItemOption(199, 0));
                addExpirationOption(item);
                return item;
            }

            private void addExpirationOption(Item item) {
                if (Util.isTrue(1, 30)) item.itemOptions.add(new ItemOption(174, 2023));
                else {
                    item.itemOptions.add(new ItemOption(174, 2023));
                    item.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }
            }

            private String buildCraftDialog(int select, Inventory inv) {
                String bathType = select == 0 ? "Gỗ" : "Vàng";
                return String.format("|2|Chế tạo Bồn Tắm %s\n|1|Cành khô: %d/50\nNước suối: %d/20\nGỗ lớn: %d/20\nQue đốt: %d/2\nGiá vàng: %d\n%s",
                        bathType, inv.getQuantity(ConstItem.CANH_KHO), inv.getQuantity(ConstItem.NUOC_SUOI_TINH_KHIET),
                        inv.getQuantity(ConstItem.GO_LON), inv.getQuantity(ConstItem.QUE_DOT), select == 0 ? 150000000 : 300000000,
                        select == 1 ? "Giá hồng ngọc: 15" : "");
            }

            private void craftBath(Player player, int select, int[] costs) {
                Inventory inv = player.inventory;
                if (!checkAndRemoveItems(player, new int[]{ConstItem.CANH_KHO, ConstItem.NUOC_SUOI_TINH_KHIET, ConstItem.GO_LON, ConstItem.QUE_DOT}, new int[]{50, 20, 20, 2})
                        || inv.gold < costs[0] || (select == 1 && inv.ruby < costs[1])) {
                    Service.getInstance().sendThongBao(player, "Thiếu tiền hoặc đồ, nghèo quá hả? 😂");
                    return;
                }
                inv.subGold(costs[0]);
                if (select == 1) inv.subRuby(costs[1]);
                int itemId = select == 0 ? ConstItem.BON_TAM_GO : ConstItem.BON_TAM_VANG;
                addItemToBag(player, ItemService.gI().createNewItem((short) itemId), "Nhận Bồn Tắm " + (select == 0 ? "Gỗ" : "Vàng") + " nè! 😜");
            }

            private void addItemToBag(Player player, Item item, String message) {
                InventoryService.gI().addItemBag(player, item, 1);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, message);
            }
        };
        map.addNpc(npc);
    }

    @Override
    public void initMap() {

    }

    @Override
    public void dropItem(Player player, Mob mob, List<ItemMap> list, int x, int yEnd) {
        Zone zone = mob.zone;
        byte[] rwLimit = player instanceof Pet ? ((Pet) player).master.getRewardLimit() : player.getRewardLimit();
        int itemId = getDropItemId(zone.map.mapId, mob.tempId, rwLimit);
        if (itemId != -1) {
            list.add(new ItemMap(zone, itemId, 1, x, yEnd, player.id));
            rwLimit[getRewardLimitIndex(itemId)]++;
        }
        if (Util.isTrue(1, 50) && player.event.isUseQuanHoa()) {
            list.add(new ItemMap(zone, getRandomEventItem(), 1, x, yEnd, player.id));
        }
    }

    private void addItemToBag(Player player, Item item, String message) {
        InventoryService.gI().addItemBag(player, item, 1);
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendThongBao(player, message);
    }

    private int getDropItemId(int mapId, int mobId, byte[] rwLimit) {
        if (!Util.isTrue(1, 50)) return -1;
        if (mapId == ConstMap.DONG_SONG_BANG && rwLimit[ConstRewardLimit.NUOC_SUOI_TINH_KHIET] < 100) return ConstItem.NUOC_SUOI_TINH_KHIET;
        if (mapId == ConstMap.DAO_KAME && rwLimit[ConstRewardLimit.GO_LON] < 100) return ConstItem.GO_LON;
        if ((mapId == ConstMap.RUNG_BAMBOO || mapId == ConstMap.RUNG_DUONG_XI) && rwLimit[ConstRewardLimit.BO_KIEN_VUONG_HAI_SUNG] < 100) return ConstItem.BO_KIEN_VUONG_HAI_SUNG;
        if ((mapId == ConstMap.RUNG_NGUYEN_SINH || mapId == ConstMap.RUNG_THONG_XAYDA) && rwLimit[ConstRewardLimit.BO_HUNG_TE_GIAC] < 100) return ConstItem.BO_HUNG_TE_GIAC;
        if ((mapId == ConstMap.DOI_NAM_TIM || mapId == ConstMap.THUNG_LUNG_NAMEC) && rwLimit[ConstRewardLimit.BO_KEP_KIM] < 100) return ConstItem.BO_KEP_KIM;
        if (mobId == ConstMob.MOC_NHAN && rwLimit[ConstRewardLimit.CANH_KHO] < 100) return ConstItem.CANH_KHO;
        return -1;
    }

    private int getRewardLimitIndex(int itemId) {
        return switch (itemId) {
            case ConstItem.NUOC_SUOI_TINH_KHIET -> ConstRewardLimit.NUOC_SUOI_TINH_KHIET;
            case ConstItem.GO_LON -> ConstRewardLimit.GO_LON;
            case ConstItem.BO_KIEN_VUONG_HAI_SUNG -> ConstRewardLimit.BO_KIEN_VUONG_HAI_SUNG;
            case ConstItem.BO_HUNG_TE_GIAC -> ConstRewardLimit.BO_HUNG_TE_GIAC;
            case ConstItem.BO_KEP_KIM -> ConstRewardLimit.BO_KEP_KIM;
            case ConstItem.CANH_KHO -> ConstRewardLimit.CANH_KHO;
            default -> -1;
        };
    }

    private int getRandomEventItem() {
        RandomCollection<Integer> rd = new RandomCollection<>();
        rd.add(1, ConstItem.VO_OC); rd.add(1, ConstItem.VO_SO);
        rd.add(1, ConstItem.CON_CUA); rd.add(1, ConstItem.SAO_BIEN);
        return rd.next();
    }

    @Override
    public boolean useItem(Player player, Item item) {
        return switch (item.template.id) {
            case ConstItem.BON_TAM_GO, ConstItem.BON_TAM_VANG -> { useBonTam(player, item); yield true; }
            case ConstItem.HU_MAT_ONG, ConstItem.VOT_BAT_BO -> { insectTrapping(player, item); yield true; }
            default -> false;
        };
    }

    private void useBonTam(Player player, Item item) {
        if (!player.zone.map.isMapLang()) {
            Service.getInstance().sendThongBaoOK(player, "Chỉ tắm được ở làng thôi bro! 😅");
            return;
        }
        if (player.event.isUseBonTam()) {
            Service.getInstance().sendThongBaoOK(player, "Đang tắm rồi, đừng ham hố! 😂");
            return;
        }
        int delay = item.template.id == ConstItem.BON_TAM_GO ? 3 : 1;
        RandomCollection<Integer> rewards = getBathRewards(item.template.id);
        Item reward = createBathReward(rewards.next());

        ItemTimeService.gI().sendItemTime(player, 3779, 60 * delay);
        EffectSkillService.gI().startStun(player, System.currentTimeMillis(), 60000 * delay);
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBags(player);
        player.event.setUseBonTam(true);
        Util.setTimeout(() -> {
            player.event.setUseBonTam(false);
            addItemToBag(player, reward, "Tắm xong nhận " + reward.template.name + ", phê quá! 😎");
        }, 60000 * delay);
    }

    private RandomCollection<Integer> getBathRewards(int itemId) {
        RandomCollection<Integer> rd = new RandomCollection<>();
        rd.add(1, ConstItem.QUAT_BA_TIEU); rd.add(1, ConstItem.CAY_KEM);
        rd.add(1, ConstItem.CA_HEO); rd.add(1, ConstItem.DIEU_RONG);
        if (itemId == ConstItem.BON_TAM_GO) rd.add(1, ConstItem.CON_DIEU);
        else {
            rd.add(1, ConstItem.XIEN_CA); rd.add(1, ConstItem.PHONG_LON);
            rd.add(1, ConstItem.CAI_TRANG_POC_BIKINI_2023);
            rd.add(1, ConstItem.CAI_TRANG_PIC_THO_LAN_2023);
            rd.add(1, ConstItem.CAI_TRANG_KING_KONG_SANH_DIEU_2023);
        }
        return rd;
    }

    private Item createBathReward(int itemId) {
        Item item = ItemService.gI().createNewItem((short) itemId);
        boolean isSpecial = Arrays.asList(ConstItem.XIEN_CA, ConstItem.PHONG_LON, ConstItem.CAI_TRANG_POC_BIKINI_2023,
                ConstItem.CAI_TRANG_PIC_THO_LAN_2023, ConstItem.CAI_TRANG_KING_KONG_SANH_DIEU_2023).contains(itemId);
        item.itemOptions.add(new ItemOption(50, Util.nextInt(isSpecial ? 20 : 5, isSpecial ? 40 : 15)));
        item.itemOptions.add(new ItemOption(77, Util.nextInt(isSpecial ? 20 : 5, isSpecial ? 40 : 15)));
        item.itemOptions.add(new ItemOption(103, Util.nextInt(isSpecial ? 20 : 5, isSpecial ? 40 : 15)));
        if (item.template.type != 11) item.itemOptions.add(new ItemOption(199, 0));
        item.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
        if (isSpecial && Util.isTrue(1, 30)) item.itemOptions.add(new ItemOption(174, 2023));
        return item;
    }

    private void insectTrapping(Player player, Item item) {
        int templateId = item.template.id;
        int mapId = player.zone.map.mapId;
        if (!Arrays.asList(ConstMap.DOI_NAM_TIM, ConstMap.THUNG_LUNG_NAMEC, ConstMap.RUNG_THONG_XAYDA,
                ConstMap.RUNG_BAMBOO, ConstMap.RUNG_DUONG_XI).contains(mapId)) {
            Service.getInstance().sendBigMessage(player, 0, buildInsectDialog(player, false, templateId == ConstItem.HU_MAT_ONG));
            return;
        }
        ConfirmDialog dialog = new ConfirmDialog(buildInsectDialog(player, true, templateId == ConstItem.HU_MAT_ONG), () -> spawnInsectBoss(player, item));
        dialog.show(player);
    }

    private String buildInsectDialog(Player player, boolean isValidMap, boolean isHuMatOng) {
        int n1 = InventoryService.gI().getQuantity(player, ConstItem.BO_KIEN_VUONG_HAI_SUNG);
        int n2 = InventoryService.gI().getQuantity(player, ConstItem.BO_HUNG_TE_GIAC);
        int n3 = InventoryService.gI().getQuantity(player, ConstItem.BO_KEP_KIM);
        return String.format("|1|Làm mồi nhử %s\n|%d|Bọ Kiến Vương: %d/10\n|%d|Bọ Hung Tê Giác: %d/10\n|%d|Bọ Kẹp Kìm: %d/10\n|%d|Chỉ dùng ở map sự kiện",
                isHuMatOng ? "Hũ mật ong" : "Vợt bắt bọ",
                n1 < 10 ? ConstTextColor.BLUE : ConstTextColor.RED, n1,
                n2 < 10 ? ConstTextColor.BLUE : ConstTextColor.RED, n2,
                n3 < 10 ? ConstTextColor.BLUE : ConstTextColor.RED, n3,
                isValidMap ? ConstTextColor.BLUE : ConstTextColor.RED);
    }

    private void spawnInsectBoss(Player player, Item item) {
        if (!checkAndRemoveItems(player, new int[]{ConstItem.BO_KIEN_VUONG_HAI_SUNG, ConstItem.BO_HUNG_TE_GIAC, ConstItem.BO_KEP_KIM}, 10)) return;
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBags(player);
        BossData bossData = item.template.id == ConstItem.HU_MAT_ONG ? createBeetleData() : createNightLordData();
        EscortedBoss boss = createBoss(item.template.id, generateUniqueID(), bossData, player);
        Service.getInstance().sendThongBao(player, "Boss " + boss.name + " đã xuất hiện, săn thôi nào! 😈");
    }

    private EscortedBoss createBoss(int itemId, byte id, BossData data, Player player) {
        if (itemId == ConstItem.HU_MAT_ONG) {
            return new Beetle(id, data, player);
        } else {
            return new NightLord(id, data, player);
        }
    }

    private BossData createBeetleData() {
        return BossData.builder()
                .name("Bọ Cánh Cứng")
                .gender(ConstPlayer.TRAI_DAT)
                .typeDame(Boss.DAME_NORMAL)
                .typeHp(Boss.HP_NORMAL)
                .dame(1)
                .hp(new int[][]{{1500}})
                .outfit(new short[]{1245, 1246, 1247})
                .skillTemp(new int[][]{{Skill.DRAGON, 1, 100}})
                .secondsRest(BossData._0_GIAY)
                .build();
    }

    private BossData createNightLordData() {
        return BossData.builder()
                .name("Ngài Đêm")
                .gender(ConstPlayer.TRAI_DAT)
                .typeDame(Boss.DAME_NORMAL)
                .typeHp(Boss.HP_NORMAL)
                .dame(1)
                .hp(new int[][]{{1500}})
                .outfit(new short[]{1248, 1249, 1250})
                .skillTemp(new int[][]{{Skill.DRAGON, 1, 100}})
                .secondsRest(BossData._0_GIAY)
                .build();
    }

    private boolean checkAndRemoveItems(Player player, int[] itemIds, int quantity) {
        for (int id : itemIds) {
            if (InventoryService.gI().findItem(player, id, quantity) == null) {
                Service.getInstance().sendThongBao(player, "Thiếu nguyên liệu rồi, đi kiếm thêm đi! 😛");
                return false;
            }
        }
        for (int id : itemIds) {
            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player, id, quantity), quantity);
        }
        return true;
    }
}