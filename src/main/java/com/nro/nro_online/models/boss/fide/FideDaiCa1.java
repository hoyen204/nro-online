package com.nro.nro_online.models.boss.fide;

import java.util.Calendar;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.FutureBoss;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.utils.Util;

public class FideDaiCa1 extends FutureBoss {

    public FideDaiCa1() {
        super(BossFactory.FIDE_DAI_CA_1, BossData.FIDE_DAI_CA_1);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }


    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
        generalRewards(pl);
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{"Xem bản lĩnh của ngươi như nào đã", "Các ngươi tới số mới gặp phải ta"};
        this.textTalkAfter = new String[]{"Ác quỷ biến hình, hêy aaa......."};
    }

    @Override
    public void leaveMap() {
        Boss fd2 = BossFactory.createBoss(BossFactory.FIDE_DAI_CA_2);
        fd2.zone = this.zone;
        fd2.location.x = this.location.x;
        fd2.location.y = this.location.y;
        super.leaveMap();
        this.setJustRestToFuture();
    }

     @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int dame = 0;
        if (this.isDie()) {
            return dame;
        } else {
            if (Util.isTrue(1, 5) && plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.TU_SAT:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                        break;
                    default:
                        return 0;
                }
            }
            final Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(11);
            if (hour >= 17 && hour < 20) {
                String bossName = this.name;
                int requiredTaskIndex = -1;

                switch (bossName) {
                    case "Fide đại ca 1":
                        requiredTaskIndex = 1;
                        break;
                    case "Fide đại ca 2":
                        requiredTaskIndex = 2;
                        break;
                    case "Fide đại ca 3":
                        requiredTaskIndex = 3;
                        break;
                }
                if (plAtt != null && plAtt.playerTask.taskMain.id != 21) {
                    damage = 0;
                    Service.getInstance().sendThongBao(plAtt, "Bây giờ là giờ nhiệm vụ, không phải nhiệm vụ hiện tại của bạn, boss miễn nhiễm sát thương");
                } else if (plAtt != null && plAtt.playerTask.taskMain.id == 21) {
                    if (plAtt.playerTask.taskMain.index != requiredTaskIndex) {
                        damage = 0;
                        Service.getInstance().sendThongBao(plAtt, "Bây giờ là giờ nhiệm vụ, không phải nhiệm vụ hiện tại của bạn, boss miễn nhiễm sát thương");
                    }
                }
            }
            dame = super.injured(plAtt, damage, piercing, isMobAttack);
            if (this.isDie()) {
                rewards(plAtt);
                notifyPlayeKill(plAtt);
                die();
            }
            return dame;
        }
    }
}
