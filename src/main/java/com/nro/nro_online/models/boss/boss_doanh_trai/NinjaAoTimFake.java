package com.nro.nro_online.models.boss.boss_doanh_trai;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.phoban.DoanhTrai;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.utils.Util;

/**
 *
 * Arriety
 *
 */
public class NinjaAoTimFake extends NinjaAoTim {

    public NinjaAoTimFake(byte id, DoanhTrai doanhTrai) {
        super(id, BossData.NINJA_AO_TIM_FAKE, doanhTrai);
        this.typePk = ConstPlayer.PK_ALL;
    }

    @Override
    public void attack() {
        try {
            if (!useSpecialSkill()) {
                if (Util.isTrue(30, ConstRatio.PER100)) {
                    this.talk();
                }
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(50, ConstRatio.PER100)) {
                        goToXY(pl.location.x + Util.nextInt(-20, 20), Util.nextInt(pl.location.y - 80,
                                this.zone.map.yPhysicInTop(pl.location.x, 0)), false);
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }

    @Override
    public void rewards(Player pl) {
    }

}
