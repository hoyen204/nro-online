/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.list_boss;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.manager.TopWhis;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;
import lombok.Getter;
import nro.consts.ConstPlayer;
import nro.manager.TopWhis;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.MapService;
import nro.services.Service;
import nro.utils.Util;

/**
 *
 * @author by Arriety
 */
public class WhisTop extends Boss {

    public static final int _15_PHUT = 900;

    @Getter
    private long player_id;

    private int level;

    public WhisTop() {
        super(BossFactory.WHIS, BossData.WHIS);
    }

    public WhisTop(long bossId, int level, long player_id) {
        super(bossId, new BossData(
                "Thiên Sứ Whis", //name
                ConstPlayer.XAYDA, //gender
                Boss.DAME_NORMAL, //type dame
                Boss.HP_NORMAL, //type hp
                100_000 * level, //dame
                new int[][] { { 15_000_000 * level } }, //hp
                new short[] { 838, 839, 840 }, //outfit
                new short[] { 154 }, //map join
                new int[][] { //skill
                        { Skill.DRAGON, 1, 1000 }, { Skill.DRAGON, 2, 2000 }, { Skill.DRAGON, 3, 3000 }, { Skill.DRAGON, 7, 7000 },
                        { Skill.ANTOMIC, 1, 1000 }, { Skill.ANTOMIC, 2, 1200 }, { Skill.ANTOMIC, 4, 1500 }, { Skill.ANTOMIC, 5, 1700 },
                        { Skill.MASENKO, 1, 1000 }, { Skill.MASENKO, 2, 1200 }, { Skill.MASENKO, 4, 1500 }, { Skill.MASENKO, 5, 1700 },
                        { Skill.GALICK, 1, 1000 }
                },
                _15_PHUT
        ));
        this.level = level;
        this.player_id = player_id;
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int dame = 0;
        if (this.isDie()) {
            return dame;
        } else {
            if (Util.isTrue(100 - level, 100)) {
                dame = super.injured(plAtt, damage, piercing, isMobAttack);
                if (level > 50) {
                    dame = dame / 100 * (100 - level);
                }
                if (this.isDie()) {
                    finish(plAtt);
                    TopWhis.addHistory(plAtt);
                    leaveMap();
                }
            } else {
                //                if (plAtt.nPoint.eatOsin) {
                //                    dame = dame / 100 * (100 - level);
                //                    return dame;
                //                }
                Service.getInstance().sendThongBao(plAtt, "Hụt");
            }
            return dame;
        }
    }

    private void finish(Player plAtt) {
        if (plAtt.zone != null) {
            plAtt.location.x = 716;
            plAtt.location.y = 312;
            plAtt.zone.mapInfo(plAtt);
            plAtt.zone.loadAnotherToMe(plAtt);
            plAtt.zone.load_Me_To_Another(plAtt);
        }
    }

    @Override
    public void rewards(Player pl) {
    }

    @Override
    public void idle() {
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.nPoint.hp <= 0) {
            Service.getInstance().sendThongBao(pl, "Hãy quay lại khi mạnh hơn");
            leaveMap();
            finish(pl);
        }
    }

    @Override
    public void initTalk() {
    }

    @Override
    public void joinMap() {
        try {
            if (this.zone != null) {
                MapService.gI().goToMap(this, this.zone);
            } else {
                BossManager.gI().removeBoss(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        if (countChangePlayerAttack < targetCountChangePlayerAttack && plAttack != null && plAttack.zone != null && plAttack.zone.equals(this.zone)) {
            if (!plAttack.isDie() && !plAttack.effectSkin.isVoHinh && !plAttack.isMiniPet) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            try {
                if (plAttack != null && !plAttack.isDie() && plAttack.effectSkin.isVoHinh) {
                    plAttack = null;
                }
                this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
                this.countChangePlayerAttack = 0;
                if (this.zone != null) {
                    plAttack = this.zone.getPlayerInMap(player_id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Loi Bosss:" + this.name);
            }
        }
        return plAttack;
    }
}
