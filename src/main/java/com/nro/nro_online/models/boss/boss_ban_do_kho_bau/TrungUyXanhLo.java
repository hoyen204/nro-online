package com.nro.nro_online.models.boss.boss_ban_do_kho_bau;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.consts.MapName;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.map.phoban.BanDoKhoBau;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

import java.util.List;

public class TrungUyXanhLo extends BossBanDoKhoBau {
    private static final int JOIN_X = 1065;
    private static final int ATTACK_TRIGGER_X = 820;
    private boolean activeAttack;

    public TrungUyXanhLo(BanDoKhoBau banDoKhoBau) {
        super(BossFactory.TRUNG_UY_XANH_LO, BossData.TRUNG_UY_XANH_LO_2, banDoKhoBau);
    }

    @Override
    public void attack() {
        if (!activeAttack) {
            checkActivation();
            return;
        }

        try {
            if (useSpecialSkill()) return;

            Player pl = getPlayerAttack();
            this.playerSkill.skillSelect = this.getSkillAttack();
            int distance = Util.getDistance(this, pl);
            if (distance <= this.getRangeCanAttackWithSkillSelect()) {
                if (Util.isTrue(10, ConstRatio.PER100)) {
                    goToXY(pl.location.x + Util.nextInt(-20, 20),
                            Util.nextInt(pl.location.y - 80, this.zone.map.yPhysicInTop(pl.location.x, 0)), false);
                }
                SkillService.gI().useSkill(this, pl, null);
                checkPlayerDie(pl);
            } else {
                goToPlayer(pl, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkActivation() {
        List<Player> notBosses = this.zone.getNotBosses();
        if (notBosses != null) {
            for (Player pl : notBosses) {
                if (pl.location.x >= ATTACK_TRIGGER_X && !pl.effectSkin.isVoHinh) {
                    this.activeAttack = true;
                    break;
                }
            }
        }
    }

    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null);
            return true;
        }
        return false;
    }

    @Override
    public void joinMap() {
        this.zone = banDoKhoBau.getMapById(MapName.DONG_KHO_BAU);
        if (this.zone != null) {
            ChangeMapService.gI().changeMap(this, this.zone, JOIN_X, this.zone.map.yPhysicInTop(JOIN_X, 0));
        }
    }
}