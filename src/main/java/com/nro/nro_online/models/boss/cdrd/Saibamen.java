/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.boss.cdrd;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.dungeon.SnakeRoad;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.EffectSkillService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

/**
 *
 * @Build by Arriety
 */
public class Saibamen extends CBoss {

    private boolean selfExplosion;

    public Saibamen(long id, short x, short y, SnakeRoad dungeon, BossData data) {
        super(id, x, y, dungeon, data);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {

    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{};
        this.textTalkAfter = new String[]{};
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int hp = nPoint.hp;
        if (!selfExplosion) {
            if (hp > 1) {
                if (damage > hp) {
                    damage = hp - 1;
                    selfExplosion = true;
                    chat("He he he");
                    if (plAtt != null) {
                        Service.getInstance().chat(plAtt, "Trời ơi muộn mất rồi");
                        Service.getInstance().sendThongBao(plAtt, plAtt.name + " coi chừng đấy!");
                        EffectSkillService.gI().setBlindDCTT(plAtt, System.currentTimeMillis(), 3000);
                        EffectSkillService.gI().sendEffectPlayer(this, plAtt, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.BLIND_EFFECT);
                    }
                    selfExplosion();
                }
            } else {
                damage = 0;
            }
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    private void selfExplosion() {
        try {
            this.nPoint.hpMax = 1000000000;
            this.playerSkill.skillSelect = this.getSkillById(Skill.TU_SAT);
            SkillService.gI().useSkill(this, null, null);
            Util.setTimeout(() -> {
                SkillService.gI().useSkill(this, null, null);
            }, 2000);
        } catch (Exception e) {
            Log.error(Saibamen.class, e);
        }
    }

}
