/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.baconsoi;

import java.util.Random;

import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.boss.FutureBoss;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerNotify;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

/**
 *
 * @author Administrator
 */
public class Lavender extends FutureBoss {

    public Lavender() {
        super(BossFactory.LAVENDER, BossData.LAVENDER);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player plKill) {
        int[] itemDos = new int[]{
            555, 557, 559,
            562, 564, 566,
            563, 565, 567};
        int randomDo = new Random().nextInt(itemDos.length);
        if (Util.isTrue(30, 70)) {
            if (Util.isTrue(1, 5)) {
                Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, 561, 1, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else {
            int slRuby = Util.nextInt(10, 20);
            for (int i = 0; i < slRuby; i++) {
                ItemMap ruby = new ItemMap(zone, 861, Util.nextInt(100, 500), 10 * i + this.location.x, zone.map.yPhysicInTop(this.location.x, 0), -1);
                Service.getInstance().dropItemMap(this.zone, ruby);
            }
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
        this.textTalkMidle = new String[]{"Xem bản lĩnh của ngươi như nào đã", "Các ngươi tới số mới gặp phải ta"};

    }

    @Override
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.BASIL).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void joinMap() {
        if (this.zone != null) {
            ChangeMapService.gI().changeMap(this, zone, this.location.x, this.location.y);
            ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
        }
    }
}
