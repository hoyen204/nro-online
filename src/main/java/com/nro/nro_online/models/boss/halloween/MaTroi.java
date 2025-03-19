package com.nro.nro_online.models.boss.halloween;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.item.ItemTime;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.ItemTimeService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

public class MaTroi extends Boss {

    private static final int MA_TROI_ICON = 6091;

    public MaTroi() {
        super(BossFactory.MA_TROI, BossData.MA_TROI);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            selectRandomSkill();
            SkillService.gI().useSkill(this, plAtt, null);
            return super.injured(plAtt, 1, piercing, isMobAttack);
        }
        return 0;
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (!isValidTarget(pl))
                return;

            selectRandomSkill();
            if (Util.getDistance(this, pl) <= getRangeCanAttackWithSkillSelect()) {
                moveNearPlayer(pl);
                applyMaTroiEffect(pl);
                SkillService.gI().useSkill(this, pl, null);
                checkPlayerDie(pl);
            } else {
                goToPlayer(pl, false);
            }
        } catch (Exception ex) {
            Log.error(MaTroi.class, ex);
        }
    }

    private void selectRandomSkill() {
        this.playerSkill.skillSelect = this.playerSkill.skills.get(
                Util.nextInt(0, this.playerSkill.skills.size() - 1));
    }

    private boolean isValidTarget(Player pl) {
        return pl != null && !pl.isDie() && !pl.isMiniPet && !pl.effectSkin.isVoHinh;
    }

    private void moveNearPlayer(Player pl) {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            int offsetX = Util.getOne(-1, 1)
                    * (SkillUtil.isUseSkillChuong(this) ? Util.nextInt(20, 80) : Util.nextInt(10, 30));
            int offsetY = Util.nextInt(10) % 2 == 0 ? 0 : -Util.nextInt(0, 50);
            goToXY(pl.location.x + offsetX, pl.location.y + offsetY, false);
        }
    }

    private void applyMaTroiEffect(Player pl) {
        if (!pl.itemTime.isMaTroi) {
            pl.itemTime.isMaTroi = true;
            pl.itemTime.iconMaTroi = MA_TROI_ICON;
            pl.itemTime.lastTimeMaTroi = System.currentTimeMillis();
            ItemTimeService.gI().sendItemTime(pl, MA_TROI_ICON, ItemTime.TIME_ITEM / 1000);
            Service.getInstance().Send_Caitrang(pl);
            Service.getInstance().point(pl);
        }
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        if (pl != null) {
            dropItemReward(2043, (int) pl.id);
        }
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