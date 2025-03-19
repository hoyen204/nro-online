package com.nro.nro_online.models.boss.event.noel;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import lombok.Setter;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class NoelBossBall extends NoelBoss {

    private Player target;

    @Setter
    private Boolean can_attack = false;

    public NoelBossBall(long id, Player player) {
        super(id, new BossData(
                "$", // name
                ConstPlayer.XAYDA, // gender
                Boss.DAME_NORMAL, // type dame
                Boss.HP_NORMAL, // type hp
                2000, // dame
                new int[][] { { 500 } }, // hp
                new short[] { 609, 610, 610 }, // outfit
                new short[] { 106 }, // map join
                new int[][] { // skill
                        { Skill.DRAGON, 1, 1000 }, { Skill.DRAGON, 2, 2000 }, { Skill.DRAGON, 3, 3000 },
                        { Skill.DRAGON, 7, 2000 }, { Skill.MASENKO, 7, 2000 }, { Skill.MASENKO, 1, 2000 } },
                _15_PHUT));
        target = player;
    }

    @Override
    public synchronized void attack() {
        try {
            if (target == null || target.zone == null || target.zone.map == null || target.isDie() || target.isMiniPet
                    || target.effectSkin.isVoHinh || target.zone.map.mapId != 106) {
                this.leaveMap();
                return;
            }
            this.playerSkill.skillSelect = this.getSkillAttack();
            if (Util.getDistance(this, target) <= 30) {
                if (can_attack) {
                    if (target instanceof NoelBoss) {
                        target.nPoint.setHp(target.nPoint.hpMax);
                        target.nPoint.setMp(target.nPoint.mpMax);
                        PlayerService.gI().sendInfoHpMp(target);
                        Service.getInstance().sendInfoPlayerEatPea(target);
                        Service.getInstance().chat(target,
                                "Vậy ra đây là năng lượng của thiên thể sao? Ta cảm thấy thật tràn trề năng lượng!!!");
                        this.leaveMap();
                    } else {
                        target.isDie();
                        target.setDie(target);
                        PlayerService.gI().sendInfoHpMpMoney(target);
                        Service.getInstance().Send_Info_NV(target);
                        if (isDie()) {
                            setDie(this);
                        }
                        checkPlayerDie(target);
                        this.leaveMap();
                    }
                }
            } else {
                goToPlayer(target, false);
            }
        } catch (Exception ex) {
            Log.error(Boss.class, ex);
        }
    }
}
