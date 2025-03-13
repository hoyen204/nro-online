package com.nro.nro_online.models.boss.hell;

import java.util.ArrayList;
import java.util.List;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.Item;
import nro.models.map.mabu.MabuWar;
import nro.models.player.Player;
import nro.server.Client;
import nro.services.*;
import nro.utils.SkillUtil;
import nro.utils.Util;

public class SatanKing extends Boss {

    private List<Long> playerAttack;

    public SatanKing() {
        super(BossFactory.SATAN_KING, BossData.SATAN_KING);
        playerAttack = new ArrayList<>();
    }

    protected SatanKing(byte id, BossData bossData) {
        super(id, bossData);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player plKill) {
        for (Long id : playerAttack) {
            Player player = Client.gI().getPlayer(id);

            if (player != null) {
                Item bdd = ItemService.gI().createNewItem((short) 2062);
                bdd.quantity = 10;
                InventoryService.gI().addItemBag(player, bdd, 999);

                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Bạn nhận được " + bdd.template.name);
            }
        }

        playerAttack = new ArrayList<>();
    }

    @Override
    public void initTalk() {
        this.textTalkAfter = new String[]{"Các ngươi chờ đấy, ta sẽ quay lại sau"};
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
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa nha " + plAttack.name + " động vào ta chỉ có chết.");
            this.plAttack = null;
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (plAtt == null) {
            return 0;
        }
        if (!playerAttack.contains(plAtt.id)) {
            playerAttack.add(plAtt.id);
        }

        int mstChuong = this.nPoint.mstChuong;
        int giamst = this.nPoint.tlGiamst;

        if (!this.isDie()) {
            if (this.isMiniPet) {
                return 0;
            }
            if (plAtt != null) {
                if (!this.isBoss && plAtt.nPoint.xDameChuong && SkillUtil.isUseSkillChuong(plAtt)) {
                    damage = plAtt.nPoint.tlDameChuong * damage;
                    plAtt.nPoint.xDameChuong = false;
                }
                if (mstChuong > 0 && SkillUtil.isUseSkillChuong(plAtt)) {
                    PlayerService.gI().hoiPhuc(this, 0, damage * mstChuong / 100);
                    damage = 0;
                }
            }
            if (!SkillUtil.isUseSkillBoom(plAtt)) {
                if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                    return 0;
                }
            }
            if (isMobAttack && (this.charms.tdBatTu > System.currentTimeMillis() || this.itemTime.isMaTroi) && damage >= this.nPoint.hp) {
                damage = this.nPoint.hp - 1;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                damage = this.nPoint.hp - 1;
            }
            if (giamst > 0) {
                damage -= nPoint.calPercent(damage, giamst);
            }
            if (this.effectSkill.isHoldMabu) {
                damage = 1;
            }

            if (damage > 10_000_000) {
                damage = 10_000_000;
            }

            if (plAtt.getSession() != null && plAtt.isAdmin()) {
                damage = this.nPoint.hpMax / 3;
            }

            this.nPoint.subHP(damage);
            if (this.effectSkill.isHoldMabu && Util.isTrue(30, 150)) {
                Service.getInstance().removeMabuEat(this);
            }
            if (isDie()) {
                if (plAtt != null && plAtt.zone != null) {
                    if (MapService.gI().isMapMabuWar(plAtt.zone.map.mapId) && MabuWar.gI().isTimeMabuWar()) {
                        plAtt.addPowerPoint(5);
                        Service.getInstance().sendPowerInfo(plAtt, "TL", plAtt.getPowerPoint());
                    }
                }
                setDie(plAtt);
                rewards(plAtt);
                notifyPlayeKill(plAtt);
                die();
            }
            return damage;
        } else {
            return 0;
        }
    }
}
