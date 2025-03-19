package com.nro.nro_online.models.boss.cell;

import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.*;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerNotify;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

/**
 * @Build Arrriety
 */
public class SieuBoHung extends FutureBoss {

    public static boolean tuSat;

    public SieuBoHung() {
        super(BossFactory.SIEU_BO_HUNG, BossData.SIEU_BO_HUNG);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Log.error(SieuBoHung.class, ex);
        }
    }

    @Override
    public void leaveMap() {
        BossManager.gI().getBossById(BossFactory.XEN_BO_HUNG).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void joinMap() {
        int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
        ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 0));
        ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
    }

    @Override
    public void idle() {
    }

    @Override
    public void rewards(Player pl) {
        if (pl != null) {
            ItemMap itemMap = null;
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            if (Util.isTrue(1, 100)) {
                int[] set1 = {562, 564, 566, 561};
                itemMap = new ItemMap(this.zone, set1[Util.nextInt(0, set1.length - 1)], 1, x, y, pl.id);
                RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            } else if (Util.isTrue(1, 30)) {
                int[] set2 = {555, 556, 563, 557, 558, 565, 559, 567, 560};
                itemMap = new ItemMap(this.zone, set2[Util.nextInt(0, set2.length - 1)], 1, x, y, pl.id);
                RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            } else if (Util.isTrue(1, 5)) {
                itemMap = new ItemMap(this.zone, 15, 1, x, y, pl.id);
            } else if (Util.isTrue(1, 2)) {
                itemMap = new ItemMap(this.zone, 16, 1, x, y, pl.id);
            }
            if (itemMap != null) {
                Service.getInstance().dropItemMap(zone, itemMap);
            }
            notifyPlayeKill(pl);
        }
        generalRewards(pl);
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{"Kame Kame Haaaaa!!", "Mi khá đấy nhưng so với ta chỉ là hạng tôm tép",
            "Tất cả nhào vô hết đi", "Cứ chưởng tiếp đi. haha", "Các ngươi yếu thế này sao hạ được ta đây. haha",
            "Khi công pháo!!", "Cho mi biết sự lợi hại của ta"};
        this.textTalkAfter = new String[]{"Các ngươi được lắm", "Hãy đợi đấy thời gian tới ta sẽ quay lại.."};
    }

}
