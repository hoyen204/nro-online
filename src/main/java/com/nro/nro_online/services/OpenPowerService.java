package com.nro.nro_online.services;

import com.nro.nro_online.models.player.NPoint;
import com.nro.nro_online.models.player.Player;
import nro.models.player.NPoint;
import nro.models.player.Pet;
import nro.models.player.Player;

/**
 * @Build by Arriety
 */
public class OpenPowerService {

    public static final int COST_SPEED_OPEN_LIMIT_POWER = 1000000000;

    private static OpenPowerService i;

    private OpenPowerService() {

    }

    public static OpenPowerService gI() {
        if (i == null) {
            i = new OpenPowerService();
        }
        return i;
    }

    public boolean openPowerBasic(Player player) {
        byte curLimit = player.nPoint.limitPower;
        if (curLimit < NPoint.MAX_LIMIT) {
            if (player.nPoint.limitPower == 10 && !player.inventory.itemsBody.stream().limit(1).allMatch(it -> it.isNotNullItem()
                    && it.template.name.contains("Thiên Sứ"))) {
                if (!player.isPet) {
                    Service.getInstance().sendThongBao(player, "Yêu cầu 5 đồ Thiên Sứ");
                } else {
                    Service.getInstance().sendThongBao(((Pet) player).master, "Yêu cầu 5 đồ Thiên Sứ");
                }
                return false;
            }
            if (player.nPoint.limitPower == 9 && !player.inventory.itemsBody.stream().limit(1).allMatch(it -> it.isNotNullItem()
                    && it.template.name.contains("Hủy Diệt"))) {
                if (!player.isPet) {
                    Service.getInstance().sendThongBao(player, "Yêu cầu 5 đồ Hủy Diệt");
                } else {
                    Service.getInstance().sendThongBao(((Pet) player).master, "Yêu cầu 5 đồ Hủy Diệt");
                }
                return false;
            }
            if (!player.itemTime.isOpenPower && player.nPoint.canOpenPower()) {
                player.itemTime.isOpenPower = true;
                player.itemTime.lastTimeOpenPower = System.currentTimeMillis();
                ItemTimeService.gI().sendAllItemTime(player);
                return true;
            } else {
                Service.getInstance().sendThongBao(player, "Sức mạnh của bạn không đủ để thực hiện");
                return false;
            }
        } else {
            Service.getInstance().sendThongBao(player, "Sức mạnh của bạn đã đạt tới mức tối đa");
            return false;
        }
    }

    public boolean openPowerSpeed(Player player) {
        if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
            if (player.nPoint.limitPower == 10
                    && !player.inventory.itemsBody.stream().limit(1).allMatch(it -> it.isNotNullItem()
                    && it.template.name.contains("Thiên Sứ"))) {
                if (!player.isPet) {
                    Service.getInstance().sendThongBao(player, "Yêu cầu 5 đồ Thiên Sứ");
                } else {
                    Service.getInstance().sendThongBao(((Pet) player).master, "Yêu cầu 5 đồ Thiên Sứ");
                }
                return false;
            }
            if (player.nPoint.limitPower == 9 
                    && !player.inventory.itemsBody.stream().limit(1).allMatch(it -> it.isNotNullItem()
                    && it.template.name.contains("Hủy Diệt"))) {
                if (!player.isPet) {
                    Service.getInstance().sendThongBao(player, "Yêu cầu 5 đồ Hủy Diệt");
                } else {
                    Service.getInstance().sendThongBao(((Pet) player).master, "Yêu cầu 5 đồ Hủy Diệt");
                }
                return false;
            }
            player.nPoint.limitPower++;
            if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                player.nPoint.limitPower = NPoint.MAX_LIMIT;
            }
            player.nPoint.initPowerLimit();
            if (!player.isPet) {
                Service.getInstance().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
            } else {
                Service.getInstance().sendThongBao(((Pet) player).master, "Giới hạn sức mạnh của đệ tử đã được tăng lên 1 bậc");
            }
            return true;
        } else {
            if (!player.isPet) {
                Service.getInstance().sendThongBao(player, "Sức mạnh của bạn đã đạt tới mức tối đa");
            } else {
                Service.getInstance().sendThongBao(((Pet) player).master, "Sức mạnh của đệ tử đã đạt tới mức tối đa");
            }
            return false;
        }
    }

}
