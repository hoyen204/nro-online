package com.nro.nro_online.models.boss.chill;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.FutureBoss;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

/**
 *
 * @Arriety
 *
 */
public class Chill extends FutureBoss {

    public Chill() {
        super(BossFactory.CHILL, BossData.CHILL);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }


    @Override
    public void rewards(Player pl) {
        // do than 1/20
        int[] tempIds1 = new int[]{555, 556, 563, 557, 558, 565, 559, 567, 560};
        // Nhan, gang than 1/30
        int[] tempIds2 = new int[]{562, 564, 566, 561};
        int[] tempIds3 = new int[]{985};
        int tempId = -1;
        if (Util.isTrue(1, 5)) {
            tempId = tempIds3[Util.nextInt(0, tempIds3.length - 1)];
        } else if (Util.isTrue(1, 10)) {
            tempId = tempIds1[Util.nextInt(0, tempIds1.length - 1)];
        } else if (Util.isTrue(1, 100)) {
            tempId = tempIds2[Util.nextInt(0, tempIds2.length - 1)];
        }

        if (tempId != -1) {
            ItemMap itemMap = new ItemMap(this.zone, tempId, 1,
                    pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
            if (tempId == 985) {
                itemMap.options.add(new ItemOption(77, 40));
                itemMap.options.add(new ItemOption(103, 40));
                itemMap.options.add(new ItemOption(50, 35));
                itemMap.options.add(new ItemOption(93, Util.nextInt(1, 30)));
            }
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            Service.getInstance().dropItemMap(this.zone, itemMap);

        }
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
        textTalkMidle = new String[]{"Ta chính là đệ nhất vũ trụ cao thủ"};
        textTalkAfter = new String[]{"Ác quỷ biến hình aaa..."};
    }

    @Override
    public void leaveMap() {
        Boss chill2 = BossFactory.createBoss(BossFactory.CHILL2);
        chill2.zone = this.zone;
        this.setJustRestToFuture();
        super.leaveMap();
    }

}
