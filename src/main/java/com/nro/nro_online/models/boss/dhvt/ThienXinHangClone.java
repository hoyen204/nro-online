package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.challenge.MartialCongressService;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class ThienXinHangClone extends BossDHVT {

    private static final int LIVE_TIME_SECONDS = 10;
    private static final long UPDATE_INTERVAL_MS = 1000;

    private int timeLive;
    private long lastUpdate;

    public ThienXinHangClone(byte id, Player player) {
        super(id, BossData.THIEN_XIN_HANG_CLONE);
        this.playerAtt = player;
        this.timeLive = LIVE_TIME_SECONDS;
        this.status = JUST_JOIN_MAP; // Đổi từ 1 thành hằng số có ý nghĩa
        MartialCongressService.gI().sendTypePK(player, this);
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_PVP);
    }

    @Override
    public void update() {
        try {
            updateCombat();
            updateLifeTime();
        } catch (Exception e) {
            Log.error(ThienXinHangClone.class, e);
        }
    }

    private void updateCombat() {
        if (effectSkill.isHaveEffectSkill() || effectSkill.isCharging) return;

        switch (this.status) {
        case JUST_JOIN_MAP -> {
            joinMap();
            if (this.zone != null) changeStatus(ATTACK);
        }
        case ATTACK -> {
            this.talk();
            if (!isPlayerPreparingSpecialSkill()) {
                this.attack();
            }
        }
        }
    }

    private void updateLifeTime() {
        if (!Util.canDoWithTime(lastUpdate, UPDATE_INTERVAL_MS)) return;

        lastUpdate = System.currentTimeMillis();
        if (--timeLive <= 0) {
            super.leaveMap();
        }
    }

    private boolean isPlayerPreparingSpecialSkill() {
        return playerSkill.prepareTuSat || playerSkill.prepareLaze || playerSkill.prepareQCKK;
    }
}