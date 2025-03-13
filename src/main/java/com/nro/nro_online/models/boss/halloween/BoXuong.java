/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.halloween;

import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.ItemTime;
import nro.models.player.Player;
import nro.services.ItemTimeService;
import nro.services.Service;
import nro.services.SkillService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 *
 * @author Administrator
 */
public class BoXuong extends Boss {

    public BoXuong() {
        super(BossFactory.BO_XUONG, BossData.BO_XUONG);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
            SkillService.gI().useSkill(this, plAtt, null);
        } else {
            return 0;
        }
        return super.injured(plAtt, 1, piercing, isMobAttack);
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl == null || pl.isDie() || pl.isMiniPet || pl.effectSkin.isVoHinh) {
                return;
            }
            this.playerSkill.skillSelect = this.getSkillAttack();
            if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                if (Util.isTrue(15, ConstRatio.PER100)) {
                    if (SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    } else {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                }
                if (!pl.itemTime.isMaTroi) {
                    pl.itemTime.isMaTroi = true;
                    pl.itemTime.iconMaTroi = 5101;
                    pl.itemTime.lastTimeMaTroi = System.currentTimeMillis();
                    ItemTimeService.gI().sendItemTime(pl, pl.itemTime.iconMaTroi, (int) ((ItemTime.TIME_ITEM - (System.currentTimeMillis() - pl.itemTime.lastTimeMaTroi)) / 1000));
                    Service.getInstance().Send_Caitrang(pl);
                    Service.getInstance().point(pl);
                }
                SkillService.gI().useSkill(this, pl, null);
                checkPlayerDie(pl);
            } else {
                goToPlayer(pl, false);
            }
        } catch (Exception ex) {
            Log.error(Boss.class, ex);
        }
    }

    @Override
    public void rewards(Player pl) {
        if (pl != null) {
            this.dropItemReward(2043, (int) pl.id);
        }
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
    }

}
