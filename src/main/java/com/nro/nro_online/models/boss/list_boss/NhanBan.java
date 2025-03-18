package com.nro.nro_online.models.boss.list_boss;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

public class NhanBan extends Boss {
    private static final short BINH_ITEM_ID = 638;
    private static final int JOIN_X = 358;
    private static final int JOIN_Y = 336;

    public NhanBan(Player plAttack, BossData data) {
        super(BossFactory.CLONE_NHAN_BAN, data);
        this.playerAtt = plAttack;
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        Item binh = ItemService.gI().createNewItem(BINH_ITEM_ID);
        InventoryService.gI().addItemBag(pl, binh, 0);
        InventoryService.gI().sendItemBags(pl);
        Service.getInstance().sendThongBao(pl, "Bạn nhận được " + binh.template.name);
    }

    @Override
    public void idle() {}

    @Override
    public void initTalk() {}

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            if (plAtt != null) rewards(plAtt);
            return 0;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    @Override
    public void attack() {
        if (playerAtt == null || playerAtt.zone == null || !this.zone.equals(playerAtt.zone) || this.isDie()) {
            this.leaveMap();
            return;
        }

        try {
            this.playerSkill.skillSelect = this.getSkillAttack();
            int distance = Util.getDistance(this, playerAtt);
            if (distance <= this.getRangeCanAttackWithSkillSelect()) {
                if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                    goToXY(playerAtt.location.x + Util.getOne(-1, 1) * Util.nextInt(20, 80),
                            playerAtt.location.y - (Util.nextInt(10) % 2 == 0 ? 0 : Util.nextInt(0, 50)), false);
                }
                SkillService.gI().useSkill(this, playerAtt, null);
                checkPlayerDie(playerAtt);
            } else {
                goToPlayer(playerAtt, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.effectSkill.isHaveEffectSkill() || this.effectSkill.isCharging) return;

        try {
            this.immortalMp();
            switch (this.status) {
            case JUST_JOIN_MAP:
                joinMap();
                if (this.zone != null) changeStatus(ATTACK);
                break;
            case ATTACK:
                this.talk();
                if (!this.playerSkill.prepareTuSat && !this.playerSkill.prepareLaze && !this.playerSkill.prepareQCKK) {
                    this.attack();
                }
                break;
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
            ChangeMapService.gI().changeMapYardrat(this, this.zone, JOIN_X, JOIN_Y);
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

    private void loadPlayer(Player pl) {
        if (pl.zone != null) {
            pl.location.x = JOIN_X;
            pl.location.y = JOIN_Y;
            pl.zone.mapInfo(pl);
            pl.zone.loadAnotherToMe(pl);
            pl.zone.load_Me_To_Another(pl);
        }
    }
}