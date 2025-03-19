package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.kygui.ConsignmentShop;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;

public class CuaHangKyGui extends Npc {

    public CuaHangKyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.",
                    "Hướng\ndẫn\nthêm", "Mua bán", "Danh sách\nHết Hạn", "Hủy");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU:
                    switch (select) {
                        case 0:
                            Service.getInstance().sendPopUpMultiLine(player, tempId, avartar, "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\bGiá trị ký gửi 10k-200Tr vàng hoặc 2-2k ngọc\bMột người bán, vạn người mua, mại dô, mại dô");
                            break;
                        case 1:
                            ConsignmentShop.getInstance().show(player);
                            break;
                        case 2:
                            ConsignmentShop.getInstance().showExpiringItems(player);
                            break;
                    }
                    break;
            }
        }
    }

}
