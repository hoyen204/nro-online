package com.nro.nro_online.models.boss.fide;

import java.util.Calendar;

import com.nro.nro_online.models.boss.*;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.server.ServerNotify;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

/**
 *
 * Arriety
 *
 */
public class FideDaiCa3 extends FutureBoss {

    public FideDaiCa3() {
        super(BossFactory.FIDE_DAI_CA_3, BossData.FIDE_DAI_CA_3);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }


    @Override
    public void rewards(Player pl) {
        if (Util.isTrue(1, 10)) {
            int[] tempId = new int[]{138, 142, 146, 150, 154, 158, 162, 166, 170, 174, 178, 182, 186};
            ItemMap itemMap = new ItemMap(this.zone, tempId[Util.nextInt(0, tempId.length - 1)],
                    1, pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            RewardService.gI().initStarOption(itemMap, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) 1, 1, 2),
                new RewardService.RatioStar((byte) 2, 1, 3),
                new RewardService.RatioStar((byte) 3, 1, 4),
                new RewardService.RatioStar((byte) 4, 1, 5),
                new RewardService.RatioStar((byte) 5, 1, 50),
                new RewardService.RatioStar((byte) 6, 1, 100)
            });
            Service.getInstance().dropItemMap(this.zone, itemMap);
        }
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

    }

    @Override
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.FIDE_DAI_CA_1).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void joinMap() {
        if (this.zone != null) {
            ChangeMapService.gI().changeMap(this, zone, this.location.x, this.location.y);
            ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
        }
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
