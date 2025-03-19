package com.nro.nro_online.models.boss.mabu_war;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.mabu.MabuWar;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

/**
 * @build by arriety
 */
public class Drabula_Tang6 extends BossMabuWar {

    public Drabula_Tang6(int mapID, int zoneId) {
        super(BossFactory.DRABULA_TANG6, BossData.DRABULA_TANG6);
        this.mapID = mapID;
        this.zoneId = zoneId;
    }

    @Override
    public void idle() {

    }

    @Override
    public void attack() {
        if (this.isDie()) {
            int percent = ((100 - this.nPoint.getCurrPercentHP()) * 2);
            this.zone.percentMabuEgg = (byte) (percent > 100 ? 100 : percent);
            die();
            return;
        }
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                if (!useSpecialSkill()) {
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
                        SkillService.gI().useSkill(this, pl, null);
                        checkPlayerDie(pl);
                    } else {
                        goToPlayer(pl, false);
                    }
                }
            }
        } catch (Exception ex) {
            Log.error(Drabula_Tang6.class, ex);
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int percent = ((100 - this.nPoint.getCurrPercentHP()) * 2);
        this.zone.percentMabuEgg = (byte) (percent > 100 ? 100 : percent);
        if (this.isDie()) {
            return 0;
        } else {
            if (plAtt != null) {
                if (Util.isTrue(20, 100)) {
                    plAtt.addPercentPowerPoint(1);
                    Service.getInstance().sendPowerInfo(plAtt, "%", plAtt.getPercentPowerPont());
                }
                int skill = plAtt.playerSkill.skillSelect.template.id;
                if (skill == Skill.KAMEJOKO || skill == Skill.ANTOMIC || skill == Skill.MASENKO || skill == Skill.LIEN_HOAN) {
                    damage = 1;
                    Service.getInstance().chat(this, "Chưởng trúng cho con bò..");
                } else if (Util.isTrue(25, 100)) {
                    damage = 1;
                    Service.getInstance().chat(this, "Xí hụt..");
                }
            }
            int dame = super.injuredNotCheckDie(plAtt, damage, piercing);
            if (this.isDie()) {
                rewards(plAtt);
            }
            if (this.zone.percentMabuEgg == 100) {
                MabuWar.gI().BabidayTalk(this, "Mabư ! Hãy theo lệnh ta,giết hết bọn chúng đi");
                MabuWar.gI().initMabu(this.zone);
                this.leaveMap();
            }
            return dame;
        }
    }

    @Override
    public void die() {
        MabuWar.gI().BabidayTalk(this, "Mabư ! Hãy theo lệnh ta,giết hết bọn chúng đi");
        MabuWar.gI().initMabu(this.zone);
        this.leaveMap();
    }

    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
    }

    @Override
    public void checkPlayerDie(Player pl) {
        pl.getPowerPoint();
    }

    @Override
    public void joinMap() {
        this.zone = getMapCanJoin(mapID);
        int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
        ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 0));
        this.nPoint.wearingDrabula = true;
    }

    @Override
    public Zone getMapCanJoin(int mapId) {
        Zone map = MapService.gI().getZoneJoinByMapIdAndZoneId(this, mapId, zoneId);
//        if (map.isBossCanJoin(this)) {
        return map;
//        } else {
//            return getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
//        }
    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{};
        this.textTalkAfter = new String[]{};
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        this.changeToIdle();
    }
}
