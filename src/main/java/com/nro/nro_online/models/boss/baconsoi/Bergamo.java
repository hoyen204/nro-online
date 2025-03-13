/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.baconsoi;

import java.util.Random;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.ServerNotify;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 *
 * @author Administrator
 */
public class Bergamo extends FutureBoss {

    public Bergamo() {
        super(BossFactory.BERGAMO, BossData.BERAMO);
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
        this.textTalkAfter = new String[]{"Ác quỷ biến hình, hêy aaa......."};
    }

    @Override
    public void leaveMap() {
        Boss fd3 = BossFactory.createBoss(BossFactory.LAVENDER);
        fd3.zone = this.zone;
        fd3.location.x = this.location.x;
        fd3.location.y = this.location.y;
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
