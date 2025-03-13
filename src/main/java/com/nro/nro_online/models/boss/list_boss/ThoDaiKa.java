package com.nro.nro_online.models.boss.list_boss;

import nro.models.boss.*;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.SkillService;
import nro.utils.Util;

/**
 *
 * @author Arriety
 *
 */
public class ThoDaiKa extends FutureBoss {

    public ThoDaiKa() {
        super(BossFactory.THO_DAI_KA, BossData.THODAIKA);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
            SkillService.gI().useSkill(this, plAtt, null);
        } else {
            return 0;
        }
        return super.injured(plAtt, 5000, piercing, isMobAttack);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        int slcarot = Util.nextInt(10, 20);
        for (int i = 0; i < slcarot; i++) {
            ItemMap carot = new ItemMap(zone, 462, 1, 10 * i + this.location.x, zone.map.yPhysicInTop(this.location.x, 0), -1);
            carot.options.add(new ItemOption(30, 0));
            carot.options.add(new ItemOption(93, 30));
            Service.getInstance().dropItemMap(this.zone, carot);
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
