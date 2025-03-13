/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.manager.TopWhis;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.TopWhisService;
import nro.services.func.ChangeMapService;
import nro.services.func.CombineServiceNew;

/**
 *
 * @author Arriety
 */
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
                    int level = TopWhis.GetLevel(player.id);
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
                                    int level = TopWhis.GetLevel(player.id);
                                    int whisId = TopWhis.GetMaxPlayerId();
                                    int coin = 1000;
                                    if (player.inventory.ruby < coin) {
                                        this.npcChat(player, "Mày chưa đủ xền");
                                        return;
                                    }
                                    player.inventory.ruby -= coin;
                                    Service.getInstance().sendMoney(player);
                                    TopWhis.SwitchToWhisBoss(player, whisId, level);
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
