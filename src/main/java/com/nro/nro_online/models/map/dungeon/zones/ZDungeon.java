package com.nro.nro_online.models.map.dungeon.zones;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.MapTemplate;
import com.nro.nro_online.models.map.WayPoint;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.dungeon.Dungeon;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.mob.MobTemplate;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;

import lombok.Getter;

@Getter
public abstract class ZDungeon extends Zone {

    protected Dungeon dungeon;

    public ZDungeon(Map map, Dungeon dungeon) {
        super(map, 0, 20);
        map.addZone(this);
        this.dungeon = dungeon;
        init();
    }

    public void init() {
        MapTemplate template = Manager.getMapTemplate(map.mapId);
        if (template != null) {
            for (int i = 0; i < template.mobTemp.length; i++) {
                MobTemplate temp = Manager.getMobTemplateByTemp(template.mobTemp[i]);
                Mob mob = new Mob();
                mob.tempId = template.mobTemp[i];
                mob.level = template.mobLevel[i];
                mob.point.setHpFull(template.mobHp[i]);
                mob.point.setHP(template.mobHp[i]);
                mob.location.x = template.mobX[i];
                mob.location.y = template.mobY[i];
                mob.pDame = temp.percentDame;
                mob.pTiemNang = temp.percentTiemNang;
                mob.setTiemNang();
                mob.status = 5;
                mob.zone = this;
                initMob(mob);
                addMob(mob);
            }
        }
    }

    public abstract void initMob(Mob mob);

    public void setTextTime() {
        try (Message msg = new Message(Cmd.MESSAGE_TIME)) {
            msg.writer().writeByte(dungeon.getType());
            msg.writer().writeUTF(dungeon.getTitle());
            msg.writer().writeShort(dungeon.getCountDown());
            Service.getInstance().sendMessAllPlayerInMap(this, msg);
        } catch (Exception e) {
            Log.error(ZDungeon.class, e);
        }
    }

    public void enter(Player player, int x, int y) {
        ChangeMapService.gI().changeMap(player, this, x, y);
        setTextTime();
    }

    @Override
    public void changeMapWaypoint(Player player) {
        WayPoint wp = MapService.gI().getWaypointPlayerIn(player);
        if (wp != null) {
            ZDungeon z = dungeon.find(wp.goMap);
            if (z != null) {
                int xGo = wp.goX;
                int yGo = wp.goY;
                z.enter(player, xGo, yGo);
            }
        }
    }

    public void close() {
        map.removeZone(this);
    }

}
