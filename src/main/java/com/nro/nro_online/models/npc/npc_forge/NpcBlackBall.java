package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.war.BlackBallWar;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class NpcBlackBall extends Npc {

    public NpcBlackBall(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        if (player.isHoldBlackBall) {
            createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?", "Phù hộ", "Từ chối");
        } else {
            createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.MENU_PHU_HP -> {
                if (select == 0)
                    showPhuHpMenu(player);
            }
            case ConstNpc.MENU_OPTION_GO_HOME -> {
                if (select == 0)
                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
            }
            case ConstNpc.MENU_OPTION_PHU_HP -> handlePhuHpSelection(player, select);
        }
    }

    private void showPhuHpMenu(Player player) {
        createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                "Từ chối");
    }

    private void handlePhuHpSelection(Player player, int select) {
        switch (select) {
            case 0 -> BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
            case 1 -> BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
            case 2 -> BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
            case 3 -> npcChat(player, "Để ta xem ngươi trụ được bao lâu");
        }
    }
}