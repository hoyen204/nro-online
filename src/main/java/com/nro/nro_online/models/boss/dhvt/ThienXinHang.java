package com.nro.nro_online.models.boss.dhvt;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class ThienXinHang extends BossDHVT {

    private long lastTimePhanThan;

    public ThienXinHang(Player player) {
        super(BossFactory.THIEN_XIN_HANG, BossData.THIEN_XIN_HANG);
        this.playerAtt = player;
    }

    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void attack() {
        try {
            if (!useSpecialSkill()) {
                super.attack();
            }
            if (Util.canDoWithTime(lastTimePhanThan, 20000)) {
                lastTimePhanThan = System.currentTimeMillis();
                phanThan();
            }
        } catch (Exception ex) {
            Log.error(ThienXinHang.class, ex);
        }
    }

    private void phanThan() {
        new ThienXinHangClone(BossFactory.THIEN_XIN_HANG_CLONE, playerAtt);
        new ThienXinHangClone(BossFactory.THIEN_XIN_HANG_CLONE1, playerAtt);
        new ThienXinHangClone(BossFactory.THIEN_XIN_HANG_CLONE2, playerAtt);
        new ThienXinHangClone(BossFactory.THIEN_XIN_HANG_CLONE3, playerAtt);
    }
}