package com.nro.nro_online.models.boss.NgucTu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.FutureBoss;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

/**
 * @author Duy Beos
 */
public class Cumber extends FutureBoss {

    public Cumber() {
        super(BossFactory.CUMBER, BossData.CUMBER);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        List<Map.Entry<Long, Integer>> entryList = new ArrayList<>(Util.sortHashMapByValue(topDame).entrySet());
        if (!entryList.isEmpty()) {
            Player playerTop = Client.gI().getPlayer(entryList.get(0).getKey());
            if (playerTop != null) {
                Item da = ItemService.gI().createNewItem((short) 2040);
                da.quantity = 2;
                playerTop.inventory.ruby += 5000;
                InventoryService.gI().addItemBag(playerTop, da, 0);
                InventoryService.gI().sendItemBags(playerTop);
                Service.getInstance().sendMoney(pl);
                Service.getInstance().sendThongBao(playerTop, "Bạn nhận được " + da.template.name);
            }
            if (entryList.size() > 1) {
                Player playerTop2 = Client.gI().getPlayer(entryList.get(1).getKey());
                if (playerTop2 != null) {
                    Item da = ItemService.gI().createNewItem((short) 2040);
                    Item bdkb = ItemService.gI().createNewItem((short) 611);
                    InventoryService.gI().addItemBag(playerTop2, da, 0);
                    InventoryService.gI().addItemBag(playerTop2, bdkb, 0);
                    InventoryService.gI().sendItemBags(playerTop2);
                }
            }
            if (entryList.size() > 2) {
                Player playerTop3 = Client.gI().getPlayer(entryList.get(2).getKey());
                if (playerTop3 != null) {
                    Item mewmew = ItemService.gI().createNewItem((short) 2040);
                    InventoryService.gI().addItemBag(playerTop3, mewmew, 0);
                    InventoryService.gI().sendItemBags(playerTop3);
                    Service.getInstance().sendThongBao(playerTop3, "Bạn nhận được " + mewmew.template.name);
                }
            }
        }
        entryList.clear();
        // do than 1/20
        int[] tempIds1 = new int[]{555, 556, 563, 557, 558, 565, 559, 567, 560};
        // Nhan, gang than 1/30
        int[] tempIds2 = new int[]{562, 564, 566, 561};

//        int[] tempIds3 = new int[]{2045};
        int tempId = -1;
        if (Util.isTrue(1, 40)) {
            tempId = tempIds1[Util.nextInt(0, tempIds1.length - 1)];
        } else if (Util.isTrue(1, 100)) {
            tempId = tempIds2[Util.nextInt(0, tempIds2.length - 1)];
        }
        if (tempId != -1) {
            ItemMap itemMap = new ItemMap(this.zone, tempId, 1,
                    pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
//            if (tempId == 2045) {
//                itemMap.options.add(new ItemOption(77, 35));
//                itemMap.options.add(new ItemOption(103, 35));
//                itemMap.options.add(new ItemOption(50, 35));
//                itemMap.options.add(new ItemOption(93, Util.nextInt(1, 15)));
//                itemMap.options.add(new ItemOption(199, 0));
//            } else {
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            RewardService.gI().initStarOption(itemMap, new RewardService.RatioStar[]{
                new RewardService.RatioStar((byte) 1, 1, 2),
                new RewardService.RatioStar((byte) 2, 1, 3),
                new RewardService.RatioStar((byte) 3, 1, 4),
                new RewardService.RatioStar((byte) 4, 1, 5),
                new RewardService.RatioStar((byte) 5, 1, 6),});
//            }
            Service.getInstance().dropItemMap(this.zone, itemMap);
        }
        generalRewards(pl);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            return 0;
        } else {
            if (plAtt != null) {
                if (Util.isTrue(25, 70)) {
                    damage = 1;
                    Service.getInstance().chat(this, "Xí hụt..");
                }
            }
            int dame = super.injured(plAtt, damage, piercing, isMobAttack);
            if (this.isDie()) {
                rewards(plAtt);
            }
            return dame;
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
        textTalkMidle = new String[]{"Ta chính là đệ nhất vũ trụ cao thủ"};
    }

    @Override
    public void leaveMap() {
        this.topDame.clear();
        Service.getInstance().sendTextTime(this, (byte) 10, "", (short) 0);
        Service.getInstance().sendTextTime(this, (byte) 11, "", (short) 0);
        Service.getInstance().sendTextTime(this, (byte) 12, "", (short) 0);
        Boss cumber2 = BossFactory.createBoss(BossFactory.CUMBER2);
        cumber2.zone = this.zone;
        this.setJustRestToFuture();
        super.leaveMap();
    }
}
