package com.nro.nro_online.models.boss.bosstuonglai;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerNotify;
import com.nro.nro_online.services.EffectSkillService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

/**
 * Arriety
 */
public class Superblackgoku extends Boss {

    private final Map<Player, Integer> angryPlayers;
    private final List<Player> playersAttack;

    public Superblackgoku() {
        super(BossFactory.SUPERBLACKGOKU, BossData.SUPERBLACKGOKU);
        this.angryPlayers = new HashMap<>();
        this.playersAttack = new LinkedList<>();
    }

    protected Superblackgoku(byte id, BossData bossData) {
        super(id, bossData);
        this.angryPlayers = new HashMap<>();
        this.playersAttack = new LinkedList<>();
    }

    @Override
    public void initTalk() {
        this.textTalkAfter = new String[]{"Các ngươi chờ đấy, ta sẽ quay lại sau"};
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

    protected void effectCharger() {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
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
        BossManager.gI().getBossById(BossFactory.BLACKGOKU).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void die() {
        this.secondTimeRestToNextTimeAppear = Util.nextInt(20, 30);
        super.die();
    }

    @Override
    public void rewards(Player plKill) {
        if (Util.isTrue(10, 90)) {
            this.dropItemReward(992, (int) plKill.id);
        } else {
            this.dropItemReward(16, (int) plKill.id);
        }
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

}
