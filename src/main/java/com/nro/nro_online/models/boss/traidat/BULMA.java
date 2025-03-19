package com.nro.nro_online.models.boss.traidat;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

/**
 * Arriety
 */
public class BULMA extends Boss {

    public BULMA() {
        super(BossFactory.BULMA, BossData.BULMA);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        // Cải trang thỏ
        int[] tempIds1 = new int[]{1041};
        int[] tempIds2 = new int[]{17};

        int tempId = -1;
        if (Util.isTrue(1, 10)) {
            tempId = tempIds1[Util.nextInt(0, tempIds1.length - 1)];
        } else {
            tempId = tempIds2[Util.nextInt(0, tempIds2.length - 1)];
        }
        if (Manager.EVENT_SEVER == 4 && tempId == -1) {
//            tempId = ConstItem.LIST_ITEM_NLSK_TET_2023[Util.nextInt(0, ConstItem.LIST_ITEM_NLSK_TET_2023.length - 1)];
        }
        if (tempId != -1) {
            ItemMap itemMap = new ItemMap(this.zone, tempId, 1,
                    pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
            if (tempId >= 2027 && tempId <= 2038) {
                itemMap.options.add(new ItemOption(74, 0));
            } else if (tempId == 1041) {
                itemMap.options.add(new ItemOption(77, Util.nextInt(20, 40)));
                itemMap.options.add(new ItemOption(103, Util.nextInt(20, 40)));
                itemMap.options.add(new ItemOption(50, Util.nextInt(20, 40)));
                itemMap.options.add(new ItemOption(117, Util.nextInt(20, 30)));
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
        this.textTalkMidle = new String[]{"Oải rồi hả?", "Ê cố lên nhóc",
                "Chán", "Ta có nhầm không nhỉ"};

    }

    @Override
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.BULMA).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
