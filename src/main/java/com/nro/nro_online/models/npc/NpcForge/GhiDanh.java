package com.nro.nro_online.models.npc.NpcForge;

import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.challenge.MartialCongressService;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;

public class GhiDanh extends Npc {

    private static final int RUBY_COST = 2000;

    public GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;

        if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Chào mừng bạn đến với đại hội võ thuật",
                    "Đại Hội\nVõ Thuật\nLần Thứ\n23", "Giải\nSiêu Hạng");
        } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
            String[] menu = (player.levelWoodChest == 0) ?
                    new String[]{"Thi đấu\n2000 ruby", "Về\nĐại Hội\nVõ Thuật"} :
                    new String[]{"Thi đấu\n2000 ruby", "Nhận thưởng\nRương cấp\n" + player.levelWoodChest, "Về\nĐại Hội\nVõ Thuật"};
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm, ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào",
                    menu, "Từ chối");
        } else {
            super.openBaseMenu(player);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || player.iDMark.getIndexMenu() != ConstNpc.BASE_MENU) return;

        if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
            handleMainCongress(player, select);
        } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
            handleChallengeCongress(player, select);
        }
    }

    private void handleMainCongress(Player player, int select) {
        int targetMap = (select == 0) ? ConstMap.DAI_HOI_VO_THUAT_129 : ConstMap.DAI_HOI_VO_THUAT_113;
        ChangeMapService.gI().changeMapNonSpaceship(player, targetMap, player.location.x, 360);
    }

    private void handleChallengeCongress(Player player, int select) {
        boolean hasChestReward = player.levelWoodChest > 0;
        int menuSize = hasChestReward ? 3 : 2;

        if (select == 0) {
            startChallenge(player);
        } else if (hasChestReward && select == 1) {
            awardWoodChest(player);
        } else if (select == (hasChestReward ? 2 : 1)) {
            ChangeMapService.gI().changeMapNonSpaceship(player, ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
        }
    }

    private void startChallenge(Player player) {
        if (!InventoryService.gI().finditemWoodChest(player)) {
            Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
            return;
        }
        if (player.inventory.getRuby() < RUBY_COST) {
            Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu 2000 ruby");
            return;
        }

        MartialCongressService.gI().startChallenge(player);
        player.inventory.subRuby(RUBY_COST);
        PlayerService.gI().sendInfoHpMpMoney(player);
    }

    private void awardWoodChest(Player player) {
        if (player.receivedWoodChest) {
            Service.getInstance().sendThongBao(player, "Mỗi ngày chỉ có thể nhận rương báu 1 lần");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            this.npcChat(player, "Hành trang đã đầy");
            return;
        }

        Item chest = ItemService.gI().createNewItem((short) ConstItem.RUONG_GO);
        chest.itemOptions.add(new ItemOption(72, player.levelWoodChest));
        chest.itemOptions.add(new ItemOption(30, 0));
        chest.createTime = System.currentTimeMillis();

        InventoryService.gI().addItemBag(player, chest, 0);
        InventoryService.gI().sendItemBags(player);
        player.receivedWoodChest = true;
        player.levelWoodChest = 0;
        Service.getInstance().sendThongBao(player, "Bạn nhận được rương gỗ");
    }
}