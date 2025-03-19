package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.NPoint;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.OpenPowerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.CombineServiceNew;
import com.nro.nro_online.utils.Util;

public class ToSu extends Npc {

    public ToSu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        createOtherMenu(player, ConstNpc.BASE_MENU,
                "Con muốn nâng giới hạn sức mạnh cho đệ tử?",
                "Đệ tử", "Nâng cấp\nđồ Thần Linh", "Từ chối");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.BASE_MENU -> handleBaseMenu(player, select);
            case ConstNpc.OPEN_POWER_PET -> handleOpenPowerPet(player, select);
            case ConstNpc.MENU_START_COMBINE -> handleUpgradeThanLinh(player, select);
        }
    }

    private void handleBaseMenu(Player player, int select) {
        switch (select) {
            case 0 -> openPowerPetMenu(player);
            case 1 -> CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_THAN_LINH);
        }
    }

    private void openPowerPetMenu(Player player) {
        if (player.pet == null) {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }

        if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
            createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                    "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                            + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                    "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng",
                    "Đóng");
        } else {
            createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Sức mạnh của đệ con đã đạt tới giới hạn", "Đóng");
        }
    }

    private void handleOpenPowerPet(Player player, int select) {
        if (select != 0)
            return;

        int cost = OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
        if (player.inventory.gold < cost) {
            Service.getInstance().sendThongBao(player,
                    "Bạn không đủ vàng để mở, còn thiếu " + Util.numberToMoney(cost - player.inventory.gold) + " vàng");
            return;
        }

        if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
            player.inventory.gold -= cost;
            Service.getInstance().sendMoney(player);
        }
    }

    private void handleUpgradeThanLinh(Player player, int select) {
        if (select == 0 && player.combineNew.typeCombine == CombineServiceNew.UPGRADE_THAN_LINH) {
            CombineServiceNew.gI().startCombine(player);
        }
    }
}
