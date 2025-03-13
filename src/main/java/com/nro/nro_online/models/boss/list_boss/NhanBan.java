/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.list_boss;

import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.item.Item;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.func.ChangeMapService;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class NhanBan extends Boss {

    public NhanBan(Player plAttack, BossData data) {
        super(BossFactory.CLONE_NHAN_BAN, data);
        playerAtt = plAttack;
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        Item binh = ItemService.gI().createNewItem((short) 638);
        InventoryService.gI().addItemBag(pl, binh, 0);
        InventoryService.gI().sendItemBags(pl);
        Service.getInstance().sendThongBao(pl, "Bạn nhận được " + binh.template.name);
    }

    @Override
    public void idle() {
    }

    @Override
    public void initTalk() {
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            return 0;
        } else {
            if (this.isDie()) {
                rewards(plAtt);
            }
            return super.injured(plAtt,damage, piercing, isMobAttack);
        }
    }

    @Override
    public void attack() {
        try {
            if (playerAtt != null && playerAtt.zone != null && this.zone != null && this.zone.equals(playerAtt.zone)) {
                if (this.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, playerAtt) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? playerAtt.location.y : playerAtt.location.y - Util.nextInt(0, 50), false);
                    }
                    SkillService.gI().useSkill(this, playerAtt, null);
                    checkPlayerDie(playerAtt);
                } else {
                    goToPlayer(playerAtt, false);
                }
            } else {
                this.leaveMap();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update() {
        super.update();
        try {
            if (!this.effectSkill.isHaveEffectSkill()
                    && !this.effectSkill.isCharging) {
                this.immortalMp();
                switch (this.status) {
                    case JUST_JOIN_MAP:
                        joinMap();
                        if (this.zone != null) {
                            changeStatus(ATTACK);
                        }
                        break;
                    case ATTACK:
                        this.talk();
                        if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze || this.playerSkill.prepareQCKK) {
                            break;
                        } else {
                            this.attack();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void immortalMp() {
        this.nPoint.mp = this.nPoint.mpg;
    }

    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            ChangeMapService.gI().changeMapYardrat(this, this.zone, 358, 336);
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.nPoint.hp <= 0) {
            Service.getInstance().sendThongBao(pl, "Phải tập luyện nhiều hơn nữa!");
            leaveMap();
            loadPlayer(pl);
        }
    }

    private void loadPlayer(Player plAtt) {
        if (plAtt.zone != null) {
            plAtt.location.x = 358;
            plAtt.location.y = 336;
            plAtt.zone.mapInfo(plAtt);
            plAtt.zone.loadAnotherToMe(plAtt);
            plAtt.zone.load_Me_To_Another(plAtt);
        }
    }
}
