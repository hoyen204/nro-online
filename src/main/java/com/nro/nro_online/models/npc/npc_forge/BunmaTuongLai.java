package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.CombineServiceNew;
import com.nro.nro_online.services.func.ShopService;

public class BunmaTuongLai extends Npc {

    public BunmaTuongLai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        if (mapId == 102 && TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
        } else if (mapId == 104) {
            createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Nâng cấp linh thú",
                    "Hướng dẫn", "Đóng");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        if (player.iDMark.isBaseMenu()) {
            if (mapId == 102 && select == 0) {
                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
            } else if (mapId == 104) {
                handleMap104Menu(player, select);
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE
                && player.combineNew.typeCombine == CombineServiceNew.UPGRADE_LINHTHU
                && select == 0) {
            CombineServiceNew.gI().startCombine(player);
        }
    }

    private void handleMap104Menu(Player player, int select) {
        switch (select) {
            case 0 -> ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BUNMA_LINH_THU, 1, -1);
            case 1 -> {
                if (player.egglinhthu == null) {
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_LINHTHU);
                } else {
                    Service.getInstance().sendThongBao(player, "Hãy nở trứng trước!");
                }
            }
            case 2 -> Service.getInstance().LinkService(player, 2752,
                    "|8|Chào bạn tôi là Bunma\n|6|tôi sẽ giúp đỡ bạn cách làm sao để nâng cấp trứng linh thú\n"
                            + "|8|Cửa hàng:\n|6|nơi bán các vận dụng phục vụ cho việc nâng cấp cấp trứng linh thú\n"
                            + "|8|Nâng cấp linh thú:\n|6|Đây sẽ là 1 bàn hóa phép giúp bạn nâng cấp con linh thú đó lên chỉ số víp",
                    "http://localhost:8080/", "Video hướng dẫn");
        }
    }
}