/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.list_boss;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.SkillService;
import nro.utils.Util;

/**
 *
 * @author Administrator
 */
public class Raity extends Boss {

    public Raity() {
        super(BossFactory.RAITY, BossData.RAITY);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
            SkillService.gI().useSkill(this, plAtt, null);
        } else {
            return 0;
        }
        return super.injured(plAtt, 1, piercing, isMobAttack);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        int qtt = Util.nextInt(10, 20);
        for (int i = 0; i < qtt; i++) {
            ItemMap item = new ItemMap(zone, 1260, 1, 10 * i + this.location.x, zone.map.yPhysicInTop(this.location.x, 0), -1);
            Service.getInstance().dropItemMap(this.zone, item);
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
