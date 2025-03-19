package com.nro.nro_online.models.boss.tieudoisatthu;

import java.util.Calendar;

import com.nro.nro_online.models.boss.*;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.utils.Util;

/**
 *
 * Arriety
 *
 */
public class So4 extends FutureBoss {

    public So4() {
        super(BossFactory.SO4, BossData.SO4);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
//        generalRewards(pl);
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{""};
        this.textTalkMidle = new String[]{"Oải rồi hả?", "Ê cố lên nhóc",
            "Chán", "Đại ca Fide có nhầm không nhỉ"};
    }

    @Override
    public void leaveMap() {
        Boss bossSO3 = BossManager.gI().getBossById(BossFactory.SO3);
        if (bossSO3 != null) {
            bossSO3.changeToAttack();
        }
        super.leaveMap();
        BossManager.gI().removeBoss(this);
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
            if (hour >= 14 && hour < 17) {
                String bossName = this.name;
                int requiredTaskIndex = -1;

                switch (bossName) {
                    case "Số 4":
                        requiredTaskIndex = 1;
                        break;
                    case "Số 3":
                        requiredTaskIndex = 2;
                        break;
                    case "Số 2":
                        requiredTaskIndex = 3;
                        break;
                    case "Số 1":
                        requiredTaskIndex = 4;
                        break;
                    case "Tiểu đội trưởng":
                        requiredTaskIndex = 5;
                        break;
                }
                if (plAtt != null && plAtt.playerTask.taskMain.id != 20) {
                    damage = 0;
                    Service.getInstance().sendThongBao(plAtt, "Bây giờ là giờ nhiệm vụ, không phải nhiệm vụ hiện tại của bạn, boss miễn nhiễm sát thương");
                } else if (plAtt != null && plAtt.playerTask.taskMain.id == 20) {
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
