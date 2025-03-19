package com.nro.nro_online.models.boss.boss_doanh_trai;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.map.phoban.DoanhTrai;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class NinjaAoTim extends BossDoanhTrai {

    public NinjaAoTim(DoanhTrai doanhTrai) {
        super(BossFactory.NINJA_AO_TIM, BossData.NINJA_AO_TIM, doanhTrai);
    }

    public NinjaAoTim(byte id, BossData bossData, DoanhTrai doanhTrai) {
        super(id, bossData, doanhTrai);
    }

    @Override
    public void attack() {
        try {
            if (!useSpecialSkill()) {
                if (Util.isTrue(10, ConstRatio.PER100)) {
                    this.talk();
                }
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(50, ConstRatio.PER100)) {
                        goToXY(pl.location.x + Util.nextInt(-20, 20), Util.nextInt(pl.location.y
                                - 80, this.zone.map.yPhysicInTop(pl.location.x, 0)), false);
                    }
                    if (Util.isTrue(30, ConstRatio.PER100)) {
                        this.phanThan();
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
        }
    }

    private int maxCountIdle;
    private int countIdle;

    @Override
    public void idle() {
        if (countIdle >= maxCountIdle) {
            this.countIdle = 0;
            this.changeAttack();
        } else {
            if (Util.isTrue(30, ConstRatio.PER100)) {
                this.talk();
            }
            this.countIdle++;
            int xMove = this.location.x += Util.nextInt(-200, 200);
            if (xMove < 50) {
                xMove = 50;
            } else if (xMove > this.zone.map.mapWidth - 50) {
                xMove = this.zone.map.mapWidth - 50;
            }
            PlayerService.gI().playerMove(this, xMove, this.zone.map.yPhysicInTop(xMove, 100));
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

    private boolean phanThan;

    private void phanThan() {
        if (!phanThan) {
            doanhTrai.bosses.add(new NinjaAoTimFake(BossFactory.NINJA_AO_TIM_FAKE_1, this.doanhTrai));
            doanhTrai.bosses.add(new NinjaAoTimFake(BossFactory.NINJA_AO_TIM_FAKE_2, this.doanhTrai));
            doanhTrai.bosses.add(new NinjaAoTimFake(BossFactory.NINJA_AO_TIM_FAKE_3, this.doanhTrai));
            doanhTrai.bosses.add(new NinjaAoTimFake(BossFactory.NINJA_AO_TIM_FAKE_4, this.doanhTrai));
            doanhTrai.bosses.add(new NinjaAoTimFake(BossFactory.NINJA_AO_TIM_FAKE_5, this.doanhTrai));
            doanhTrai.bosses.add(new NinjaAoTimFake(BossFactory.NINJA_AO_TIM_FAKE_6, this.doanhTrai));
            phanThan = true;
        }
    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{"Hahaha..", "Ngươi nghĩ có thể bắt kịp cựu ninja như ta sao",
            "Còn chậm lắm nhóc ơi", "Chịu thua đi..", "Con già vợ yếu đang chờ ngươi ở nhà kìa!"};
    }

}
