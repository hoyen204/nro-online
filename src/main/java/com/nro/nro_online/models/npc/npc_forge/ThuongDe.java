package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.dungeon.SnakeRoad;
import com.nro.nro_online.models.map.dungeon.zones.ZSnakeRoad;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.ShopService;

public class ThuongDe extends Npc {

    public ThuongDe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 45) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn làm gì nào", "Đến Kaio",
                        "Quay số\nmay mắn");
            } else if (player.zone instanceof ZSnakeRoad) {
                if (mapId == ConstMap.CON_DUONG_RAN_DOC) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy lắm lấy tay ta mau",
                            "Về thần điện");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 45) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                            break;
                        case 1:
                            this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                    "Con muốn làm gì nào?", "Quay bằng\nhồng ngọc",
                                    "Rương phụ\n("
                                    + (player.inventory.itemsBoxCrackBall.size()
                                    - InventoryService.gI().getCountEmptyListItem(
                                            player.inventory.itemsBoxCrackBall))
                                    + " món)",
                                    "Xóa hết\ntrong rương", "Đóng");
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                    switch (select) {
                        case 0:
//                                                LuckyRoundService.gI().openCrackBallUI(player,
//                                                        LuckyRoundService.USING_GEM);
                            break;
                        case 1:
                            ShopService.gI().openBoxItemLuckyRound(player);
                            break;
                        case 2:
                            NpcService.gI().createMenuConMeo(player,
                                    ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                    "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                    + "sẽ không thể khôi phục!",
                                    "Đồng ý", "Hủy bỏ");
                            break;
                    }
                }
            } else if (player.zone instanceof ZSnakeRoad) {
                if (mapId == ConstMap.CON_DUONG_RAN_DOC) {
                    ZSnakeRoad zroad = (ZSnakeRoad) player.zone;
                    if (zroad.isKilledAll()) {
                        SnakeRoad road = (SnakeRoad) zroad.getDungeon();
                        ZSnakeRoad egr = (ZSnakeRoad) road.find(ConstMap.THAN_DIEN);
                        egr.enter(player, 360, 408);
                        Service.getInstance().sendThongBao(player, "Hãy xuống gặp thần mèo Karin");
                    } else {
                        Service.getInstance().sendThongBao(player,
                                "Hãy tiêu diệt hết quái vật ở đây!");
                    }
                }
            }
        }
    }
}
