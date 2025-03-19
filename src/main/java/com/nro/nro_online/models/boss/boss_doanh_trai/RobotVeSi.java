package com.nro.nro_online.models.boss.boss_doanh_trai;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.phoban.DoanhTrai;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class RobotVeSi extends BossDoanhTrai {

    public RobotVeSi(byte id, DoanhTrai doanhTrai) {
        super(id, BossData.ROBOT_VE_SI, doanhTrai);
    }

    @Override
    public void attack() {
        try {
            if (!useSpecialSkill()) {
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(20, ConstRatio.PER100)) {
                        goToXY(pl.location.x + Util.nextInt(-20, 20), Util.nextInt(pl.location.y - 80,
                                this.zone.map.yPhysicInTop(pl.location.x, 0)), false);
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.doanhTrai.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
            ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 100));
        } catch (Exception e) {

        }
    }

}
