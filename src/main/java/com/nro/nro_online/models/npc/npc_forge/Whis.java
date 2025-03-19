package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.manager.TopWhis;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TopWhisService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.CombineServiceNew;

public class Whis extends Npc {

    public Whis(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 48:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chưa tới giờ thi đấu, xem hướng dẫn để biết thêm chi tiết",
                            "Hướng\ndẫn\nthêm", "Từ chối");
                    break;
                case 154:
                    int level = TopWhis.getLevel(player.id);
                    this.createOtherMenu(player, ConstNpc.MENU_WHIS_200,
                            "Ngươi muốn gì nào",
                            new String[]{"Nói chuyện",
                                "Hành tinh\nBill",
                                "Top 100", "[LV:" + level + "]"});
                    break;
                case 200:
                    this.createOtherMenu(player, ConstNpc.MENU_WHIS,
                            "Ngươi muốn gì nào",
                            "Nói chuyện",
                            "Học\n Tuyệt kĩ");
                    break;
                default:
                    super.openBaseMenu(player);
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 48:
                    break;
                case 200:
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_WHIS:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ta sẽ giúp ngươi chế tạo trang bị Thiên Sứ!",
                                            "OK", "Hành tinh\nWhis", "Đóng");
                                    break;
                            }
                            break;
                        case ConstNpc.BASE_MENU:
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 154, -1, 336);
                                    break;
                            }
                            break;
                        case ConstNpc.MENU_NANG_CAP_DO_TS:
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().startCombine(player);
                                    break;
                            }
                            break;
                    }
                    break;
                case 154:
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_WHIS_200:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ta sẽ giúp ngươi chế tạo trang bị Thiên Sứ!",
                                            "OK", "Đóng");
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 200, -1, 336);
                                    break;
                                case 2:
                                    TopWhisService.ShowTop(player);
                                    break;
                                case 3:
                                    int level = TopWhis.getLevel(player.id);
                                    int whisId = TopWhis.getMaxPlayerId();
                                    int coin = 1000;
                                    if (player.inventory.ruby < coin) {
                                        this.npcChat(player, "Mày chưa đủ xền");
                                        return;
                                    }
                                    player.inventory.ruby -= coin;
                                    Service.getInstance().sendMoney(player);
                                    TopWhis.switchToWhisBoss(player, whisId, level);
                                    break;
                            }
                            break;
                        case ConstNpc.BASE_MENU:
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                            }
                            break;
                        case ConstNpc.MENU_NANG_CAP_DO_TS:
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().startCombine(player);
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }
    }
}
