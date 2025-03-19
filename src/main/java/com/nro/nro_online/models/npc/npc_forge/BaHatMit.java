package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.func.CombineServiceNew;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.ShopService;

public class BaHatMit extends Npc {

    public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5:
                    createMenuForMap5(player);
                    break;
                case 121:
                    createMenuForMap121(player);
                    break;
                default:
                    createMenuForDefaultMap(player);
                    break;
            }
        }
    }

    private void createMenuForMap5(Player player) {
        createOtherMenu(player, ConstNpc.BASE_MENU,
                "Ngươi tìm ta có việc gì?",
                "Ép sao\ntrang bị",
                "Pha lê\nhóa\ntrang bị",
                "Nâng cấp\nĐệ tử Super",
                "Trade Black GâuKu",
                "Pha lê hóa\nKiếm Hasakii",
                "Pha lê hóa\nCải trang",
                "Hành tinh\nĐịa ngục");
    }

    private void createMenuForMap121(Player player) {
        createOtherMenu(player, ConstNpc.BASE_MENU,
                "Ngươi tìm ta có việc gì?",
                "Về đảo\nrùa");
    }

    private void createMenuForDefaultMap(Player player) {
        createOtherMenu(player, ConstNpc.BASE_MENU,
                "Ngươi tìm ta có việc gì?",
                "Cửa hàng\nBùa",
                "Nâng cấp\nVật phẩm",
                "Nâng cấp\nBông tai\nPorata",
                "Nâng cấp\nChỉ số\nBông tai",
                "Nhập\nNgọc Rồng",
                "Nâng cấp\nBông tai\nPorata 3",
                "Mở\nChỉ số\nBông tai 3");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5:
                    handleMenuForMap5(player, select);
                    break;
                case 112:
                    handleMenuForMap112(player, select);
                    break;
                case 42:
                case 43:
                case 44:
                    handleMenuForMap42_43_44(player, select);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleMenuForMap5(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                    break;
                case 1:
                    createOtherMenu(player, ConstNpc.MENU_PHA_LE_HOA_TRANG_BI,
                            "Ngươi muốn pha lê hóa trang bị bằng cách nào?", "Một Lần", "Từ chối");
                    break;
                case 2:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_PET);
                    break;
                case 3:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.TRADE_PET);
                    break;
                case 4:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_DISGUISE);
                    break;
                case 5:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_CAI_TRANG);
                    break;
                case 6:
                    if (player.playerTask.taskMain.id < 26) {
                        npcChat(player, "Vui lòng hoàn thành xong nhiệm vụ Xên bọ hung mới được vào map");
                        return;
                    }
                    ChangeMapService.gI().changeMap(player, 208, -1, 685, 432);
                    break;
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHA_LE_HOA_TRANG_BI) {
            if (select == 0) {
                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
            if (select == 0) {
                CombineServiceNew.gI().startCombine(player);
            }
        }
    }

    private void handleMenuForMap112(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
            }
        }
    }

    private void handleMenuForMap42_43_44(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0:
                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                    break;
                case 1:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                    break;
                case 2:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                    break;
                case 3:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI);
                    break;
                case 4:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                    break;
                case 5:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI_CAP3);
                    break;
                case 6:
                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.DAP_BONG_TAI_CAP_3);
                    break;
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
            switch (select) {
                case 0:
                    ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_0, 0);
                    break;
                case 1:
                    ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_1, 1);
                    break;
                case 2:
                    ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_2, 2);
                    break;
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
            if (select == 0) {
                CombineServiceNew.gI().startCombine(player);
            }
        }
    }
}
