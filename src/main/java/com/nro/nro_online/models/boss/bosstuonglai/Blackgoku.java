package com.nro.nro_online.models.boss.bosstuonglai;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nro.consts.ConstMap;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.ServerNotify;
import nro.services.EffectSkillService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 * @stole Arriety
 */
public class Blackgoku extends Boss {

    private final Map angryPlayers;
    private final List<Player> playersAttack;

    public Blackgoku() {
        super(BossFactory.BLACKGOKU, BossData.BLACKGOKU);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }

    protected Blackgoku(byte id, BossData bossData) {
        super(id, bossData);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }

    @Override
    public void initTalk() {
        this.textTalkAfter = new String[]{"Các ngươi chờ đấy, ta sẽ quay lại sau"};
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            return 0;
        } else {
            if (plAtt != null) {
                if (Util.isTrue(20, 70)) {
                    damage = 1;
                    Service.getInstance().chat(this, "Xí hụt..");
                }
            }
            int dame = super.injured(plAtt, damage, piercing, isMobAttack);
//            if (this.isDie()) {
//                rewards(plAtt);
//            }
            return dame;
        }
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            this.playerSkill.skillSelect = this.getSkillAttack();
            if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                    goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                            Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                }
                try {
                    SkillService.gI().useSkill(this, pl, null);
                } catch (Exception e) {
                    Log.error(Blackgoku.class, e);
                }
                checkPlayerDie(pl);
            } else {
                goToPlayer(pl, false);
            }
            if (Util.isTrue(5, ConstRatio.PER100)) {
                this.changeIdle();
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void idle() {
        if (this.countIdle >= this.maxIdle) {
            this.maxIdle = Util.nextInt(0, 3);
            this.countIdle = 0;
            this.changeAttack();
        } else {
            this.countIdle++;
        }
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        if (countChangePlayerAttack < targetCountChangePlayerAttack
                && plAttack != null && plAttack.zone != null && plAttack.zone.equals(this.zone)
                && !plAttack.effectSkin.isVoHinh) {
            if (!plAttack.isDie()) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            plAttack = this.zone.getRandomPlayerInMap();
        }
        return plAttack;
    }

    @Override
    public void goToXY(int x, int y, boolean isTeleport) {
        EffectSkillService.gI().stopCharge(this);
        super.goToXY(x, y, isTeleport);
    }


    private boolean isInListPlayersAttack(Player player) {
        for (Player pl : playersAttack) {
            if (player.equals(pl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa nha " + plAttack.name + " động vào ta chỉ có chết.");
            this.angryPlayers.put(pl, 0);
            this.playersAttack.remove(pl);
            this.plAttack = null;
        }
    }

    @Override
    public void joinMap() {
        this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
        ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 0));
        ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName + "");
    }

    @Override
    public Zone getMapCanJoin(int mapId) {
        return super.getMapCanJoin(mapId);
    }

    @Override
    public void leaveMap() {
        Boss Superblackgoku = BossFactory.createBoss(BossFactory.SUPERBLACKGOKU);
        Superblackgoku.zone = this.zone;
        this.setJustRestToFuture();
        super.leaveMap();
    }

    @Override
    public void die() {
        this.secondTimeRestToNextTimeAppear = Util.nextInt(20, 30);
        super.die();
    }

    @Override
    public void rewards(Player plKill) {
        if (Util.isTrue(10, 90)) {
            this.dropItemRewardWithOption(992, (int) plKill.id, false, 1);
        } else {
            this.dropItemReward(16, (int) plKill.id);
        }
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }
    
    public void dropItemRewardWithOption(int tempId, int playerId, boolean canTran, int... quantity) {
        if (!this.zone.map.isMapOffline && this.zone.map.type == ConstMap.MAP_NORMAL) {
            int x = this.location.x + Util.nextInt(-30, 30);
            if (x < 30) {
                x = 30;
            } else if (x > zone.map.mapWidth - 30) {
                x = zone.map.mapWidth - 30;
            }
            int y = this.location.y;
            if (y > 24) {
                y = this.zone.map.yPhysicInTop(x, y - 24);
            }
            ItemMap itemMap = new ItemMap(this.zone, tempId, (quantity != null && quantity.length == 1) ? quantity[0] : 1, x, y, playerId);
            if (!canTran) {
                itemMap.options.add(new ItemOption(30, 0));
            }
            Service.getInstance().dropItemMap(itemMap.zone, itemMap);
        }
    }
}
