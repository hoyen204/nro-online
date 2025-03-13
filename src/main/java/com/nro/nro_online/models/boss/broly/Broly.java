package com.nro.nro_online.models.boss.broly;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.ServerNotify;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 *
 * Arriety
 *
 */
public class Broly extends Boss {

    boolean xhpnext;

    static final int MAX_HP = 16777080;
    private static final int DIS_ANGRY = 100;

    private static final int HP_CREATE_SUPER_1 = 1000000;
    private static final int HP_CREATE_SUPER_2 = 2000000;
    private static final int HP_CREATE_SUPER_3 = 4000000;
    private static final int HP_CREATE_SUPER_4 = 6000000;
    private static final int HP_CREATE_SUPER_5 = 7000000;
    private static final int HP_CREATE_SUPER_6 = 10000000;
    private static final int HP_CREATE_SUPER_7 = 13000000;
    private static final int HP_CREATE_SUPER_8 = 14000000;
    private static final int HP_CREATE_SUPER_9 = 15000000;
    private static final int HP_CREATE_SUPER_10 = 16000000;

    private static final byte RATIO_CREATE_SUPER_10 = 10;
    private static final byte RATIO_CREATE_SUPER_20 = 20;
    private static final byte RATIO_CREATE_SUPER_30 = 30;
    private static final byte RATIO_CREATE_SUPER_40 = 40;
    private static final byte RATIO_CREATE_SUPER_50 = 50;
    private static final byte RATIO_CREATE_SUPER_60 = 60;
    private static final byte RATIO_CREATE_SUPER_70 = 70;
    private static final byte RATIO_CREATE_SUPER_80 = 80;
    private static final byte RATIO_CREATE_SUPER_90 = 90;
    private static final byte RATIO_CREATE_SUPER_100 = 100;

    private final Map angryPlayers;
    private final List<Player> playersAttack;

    public Broly() {
        super(BossFactory.BROLY, BossData.BROLY);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }

    protected Broly(byte id, BossData bossData) {
        super(id, bossData);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }

    @Override
    public void initTalk() {
        this.textTalkAfter = new String[]{"Các ngươi chờ đấy, ta sẽ quay lại sau"};
    }

    @Override
    public void attack() {
        try {
            if (!charge()) {
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                    this.effectCharger();
                    try {
                        SkillService.gI().useSkill(this, pl, null);
                    } catch (Exception e) {
                        Log.error(Broly.class, e);
                    }
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
                if (Util.isTrue(5, ConstRatio.PER100)) {
                    this.changeIdle();
                }
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
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int mstChuong = this.nPoint.mstChuong;
        int giamst = this.nPoint.tlGiamst;
        if (!this.isDie()) {
            if (mstChuong > 0 && SkillUtil.isUseSkillChuong(plAtt)) {
                PlayerService.gI().hoiPhuc(this, 0, damage * mstChuong / 100);
                damage = 0;
            }
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (!piercing) {
                if ((plAtt.playerSkill.skillSelect.template.id == Skill.ANTOMIC || plAtt.playerSkill.skillSelect.template.id == Skill.KAMEJOKO || plAtt.playerSkill.skillSelect.template.id == Skill.MASENKO)) {
                    Service.getInstance().chat(plAtt, "Trời ơi, chưởng hoàn toàn vô hiệu lực với hắn..");
                    damage = 0;
                }
                if (!(plAtt.playerSkill.skillSelect.template.id == Skill.TU_SAT || plAtt.playerSkill.skillSelect.template.id == Skill.MAKANKOSAPPO || plAtt.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI)) {
                    if (damage >= this.nPoint.hpMax / 100) {
                        damage = this.nPoint.hpMax / 100;
                    }
                }
            }
            if (giamst > 0) {
                damage -= nPoint.calPercent(damage, giamst);
            }

            if (plAtt != null && plAtt.getSession() != null && plAtt.isAdmin()) {
                damage = nPoint.hpMax;
            }

            this.nPoint.subHP(damage);

            if (!xhpnext && this.nPoint.hp > this.nPoint.hpMax * 2 / 3) {
                xhpnext = true;
            }
            if (this.nPoint.hp <= this.nPoint.hpMax / 2 && xhpnext) {
                this.nPoint.hpMax *= 1.5;
                this.nPoint.dame = this.nPoint.dame + this.nPoint.hpMax / 50;
                xhpnext = false;
            }

            if (isDie()) {
                setDie(plAtt);
                die();

                boolean isSuper = false;

                if (this instanceof SuperBroly) {
                    isSuper = true;
                }
                if (this.nPoint.hpMax >= 1_000_000 && !isSuper) {
                    int hpbroly = (this.nPoint.hpMax / 100) * 150;
                    if (hpbroly > 18_000_000) {
                        hpbroly = 18_000_000;
                    }
                    int damebroly = (this.nPoint.dame / 100) * 150;
                    if (damebroly > 1_000_000) {
                        damebroly = 1_000_000;
                    }
                    BossData superBroly = new BossData(
                            "Super Broly %1", //name
                            ConstPlayer.XAYDA, //gender
                            Boss.DAME_NORMAL, //type dame
                            Boss.HP_NORMAL, //type hp
                            damebroly, //dame
                            new int[][]{{hpbroly}}, //hp
                            new short[]{294, 295, 296}, //outfit
                            new short[]{(short) this.zone.map.mapId}, //map join
                            new int[][]{ //skill
                                {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                                {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                                {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
                                {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
                                {Skill.TAI_TAO_NANG_LUONG, 1, 15000}, {Skill.TAI_TAO_NANG_LUONG, 3, 25000}, {Skill.TAI_TAO_NANG_LUONG, 7, 50000}
                            },
                            1
                    );
                    new SuperBroly(BossFactory.SUPER_BROLY, superBroly);
                } else {
                    int hpbroly = this.nPoint.hpMax + Util.nextInt(50_000, 100_000);
                    if (hpbroly < 500) {
                        hpbroly = 500;
                    }
                    int damebroly = this.nPoint.dame / 10;
                    if (damebroly < 50) {
                        damebroly = 50;
                    }
                    BossData brolythuong = new BossData(
                            "Broly %1", //name
                            ConstPlayer.XAYDA, //gender
                            Boss.DAME_PERCENT_HP_HUND, //type dame
                            Boss.HP_NORMAL, //type hp
                            damebroly, //dame
                            new int[][]{{hpbroly}}, //hp
                            new short[]{291, 292, 293}, //outfit
                            new short[]{5, 6, 27, 28, 29, 30, 13, 10, 31, 32, 33, 34, 20, 19, 35, 36, 37, 38}, //map join
                            new int[][]{ //skill
                                {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                                {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                                {Skill.TAI_TAO_NANG_LUONG, 1, 15000}, {Skill.TAI_TAO_NANG_LUONG, 3, 25000}, {Skill.TAI_TAO_NANG_LUONG, 5, 25000},
                                {Skill.TAI_TAO_NANG_LUONG, 6, 30000}, {Skill.TAI_TAO_NANG_LUONG, 7, 50000}
                            },
                            1 //số giây nghỉ
                    );
                    if (isSuper) {
                        brolythuong.hp = new int[][]{{100, 1000}, {1000, 100000}, {100000, 1000000}, {1000000, 2000000}};
                        rewards(plAtt);
                    }
                    new Broly(BossFactory.BROLY, brolythuong);
                }
            }
            return damage;
        } else {
            return 0;
        }
    }

    private int maxCountResetPoint;
    private int countResetPoint;

    private void resetPoint(int damageInjured) {
        if (this.nPoint.hpg < MAX_HP && this.countResetPoint++ >= maxCountResetPoint) {
            this.nPoint.hpg += damageInjured;
            if (this.nPoint.hpg > MAX_HP) {
                this.nPoint.hpg = MAX_HP;
            }
            switch (this.typeDame) {
                case DAME_PERCENT_HP_HUND:
                    this.nPoint.dameg = this.nPoint.hpg * this.percentDame / 100;
                    break;
                case DAME_PERCENT_MP_HUND:
                    this.nPoint.dameg = this.nPoint.hpg * this.percentDame / 100;
                    break;
                case DAME_PERCENT_HP_THOU:
                    this.nPoint.dameg = this.nPoint.hpg * this.percentDame / 1000;
                    break;
                case DAME_PERCENT_MP_THOU:
                    this.nPoint.dameg = this.nPoint.mpg * this.percentDame / 1000;
                    break;
            }
            maxCountResetPoint = Util.nextInt(3, 7);
            countResetPoint = 0;
        }
    }

    protected boolean charge() {
        if (this.effectSkill.isCharging && Util.isTrue(15, 100)) {
            this.effectSkill.isCharging = false;
            return false;
        }
        if (Util.isTrue(1, 20)) {
            for (Skill skill : this.playerSkill.skills) {
                if (skill.template.id == Skill.TAI_TAO_NANG_LUONG) {
                    this.playerSkill.skillSelect = skill;
                    if (this.nPoint.getCurrPercentHP() < Util.nextInt(0, 100) && SkillService.gI().canUseSkillWithCooldown(this)
                            && SkillService.gI().useSkill(this, null, null)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        if (this.zone != null && this.zone.map != null) {
            int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
            ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 0));
            ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName + "");
        } else {
            BossManager.gI().removeBoss(this);
        }
    }

    @Override
    public void respawn() {
        super.respawn();
        this.plAttack = null;
        if (this.playersAttack != null) {
            this.playersAttack.clear();
        }
        if (this.angryPlayers != null) {
            this.angryPlayers.clear();
        }
    }

    @Override
    public Zone getMapCanJoin(int mapId) {
        return super.getMapCanJoin(mapId);
    }

    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void die() {
        this.secondTimeRestToNextTimeAppear = Util.nextInt(20, 30);
        super.die();
    }

    @Override
    public void rewards(Player pl) {
        if (true) {
            BossFactory.createBoss(BossFactory.SUPER_BROLY);
            return;
        }
        generalRewards(pl);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

}
