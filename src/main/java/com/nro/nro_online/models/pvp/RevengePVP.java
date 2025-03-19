package com.nro.nro_online.models.pvp;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.FriendAndEnemyService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.PVPServcice;
import com.nro.nro_online.utils.Util;

/**
 *
 * Arriety
 *
 */
public class RevengePVP extends PVP {

    private static final int TIME_WAIT = 30000;

    private boolean changeTypePk;
    public long lastTimeGoToMapEnemy;

    public RevengePVP(Player player, Player enemy) {
        this.player1 = player;
        this.player2 = enemy;
        this.typePVP = TYPE_PVP_REVENGE;
    }

    @Override
    public void update() {
        if (!changeTypePk && Util.canDoWithTime(lastTimeGoToMapEnemy, TIME_WAIT)) {
            changeTypePk = true;
            if (player1.zone.equals(player2.zone)) {
                Service.getInstance().chat(player1, "Mau đền tội");
                Service.getInstance().sendThongBao(player2, "Có người tìm bạn trả thù");
                super.start();
                PlayerService.gI().changeAndSendTypePK(this.player1, ConstPlayer.PK_PVP);
                PlayerService.gI().changeAndSendTypePK(this.player2, ConstPlayer.PK_PVP);
            } else {
                PVPServcice.gI().removePVP(this);
                return;
            }
        }
        super.update();
    }

    @Override
    public void sendResultMatch(Player winer, Player loser, byte typeWin) {
        switch (typeWin) {
            case PVP.TYPE_DIE:
                Service.getInstance().chat(winer, "Chừa nha " + loser.name);
                Service.getInstance().chat(loser, "Cay quá");
                FriendAndEnemyService.gI().removeEnemy(winer, (int) loser.id);
                break;
            case PVP.TYPE_LEAVE_MAP:
                Service.getInstance().chat(winer, loser.name + " suy cho cùng cũng chỉ là con gà");
                break;
        }
    }

    @Override
    public void reward(Player plWin) {
    }

}
