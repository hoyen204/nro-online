/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.NPoint;
import nro.models.player.Player;
import nro.services.OpenPowerService;
import nro.services.Service;
import nro.services.func.CombineServiceNew;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class ToSu extends Npc {

    public ToSu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                "Con muốn nâng giới hạn sức mạnh cho đệ tử?", "Đệ tử", "Nâng cấp\nđồ Thần Linh",
                "Từ chối");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        if (player.pet != null) {
                            if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                        "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                        + Util.numberToMoney(
                                                player.pet.nPoint.getPowerNextLimit()),
                                        "Nâng ngay\n" + Util.numberToMoney(
                                                OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER)
                                        + " vàng",
                                        "Đóng");
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Sức mạnh của đệ con đã đạt tới giới hạn", "Đóng");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                        }
                        break;
                    case 1:
                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_THAN_LINH);
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                if (select == 0) {
                    if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                        if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                            player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                            Service.getInstance().sendMoney(player);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player,
                                "Bạn không đủ vàng để mở, còn thiếu " + Util
                                        .numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER
                                                - player.inventory.gold))
                                + " vàng");
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                switch (player.combineNew.typeCombine) {
                    case CombineServiceNew.UPGRADE_THAN_LINH:
                        if (select == 0) {
                            CombineServiceNew.gI().startCombine(player);
                        }
                        break;
                }
            }
        }
    }
}
