package com.nro.nro_online.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.NamekBall;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.war.NamekBallWar;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.ItemMapService;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

public class NamekBallManager extends AbsManager<NamekBall> {

    private static final NamekBallManager INSTANCE = new NamekBallManager();

    public static NamekBallManager gI() {
        return INSTANCE;
    }

    public void initBall() {
        Set<Map> usedMaps = HashSet.newHashSet(7);
        List<Integer> mapIds = new ArrayList<>(Arrays.asList(7, 8, 9, 10, 11, 12, 13));
        Collections.shuffle(mapIds); // Random thứ tự
        for (int i = 0; i < 7; i++) {
            Map map = MapService.gI().getMapById(mapIds.get(i));
            usedMaps.add(map);

            Zone zone = map.zones.get(Util.nextInt(0, map.zones.size() - 1));
            int y = map.yPhysicInTop(map.mapWidth / 2, map.mapHeight / 2);
            NamekBall ball = new NamekBall(zone, ConstItem.NGOC_RONG_NAMEK_1_SAO + i, 1, map.mapWidth / 2, y, -1);
            ball.itemMapId = -9999 + i;
            ball.setIndex(i);
            add(ball);
        }
    }

    public void initFossil() {
        Player[] holders = NamekBallWar.gI().getHolders();
        for (Player p : holders) {
            if (p != null) {
                p.isHoldNamecBall = false;
                Service.getInstance().sendFlagBag(p);
                PlayerService.gI().changeAndSendTypePK(p, ConstPlayer.NON_PK);
            }
        }
        for (NamekBall ball : getList()) {
            ItemMapService.gI().removeItemMap(ball);
            ItemMapService.gI().sendItemMapDisappear(ball);
        }
        for (int i = 0; i < 7; i++) {
            Map m = MapService.gI().getMapById(Util.nextInt(7, 13));
            Zone z = m.zones.get(Util.nextInt(0, m.zones.size()));
            int y = m.yPhysicInTop(m.mapWidth / 2, m.mapHeight / 2);
            NamekBall ball = new NamekBall(z, ConstItem.HOA_THACH_NGOC_RONG, 1, m.mapWidth / 2, y, -1);
            ball.setStone(true);
            add(ball);
            Service.getInstance().dropItemMap(z, ball);
        }
        getList().clear();
    }

    @Override
    public NamekBall findById(int id) {
        return list.get(getIndex(id));
    }

    public NamekBall findByIndex(int index) {// debug
        if (!list.isEmpty() && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

//    public NamekBall findByIndex(int index) {
//        if (list.size() >= index) {
//            return list.get(index);
//        }
//        return null;
//    }

    public int getIndex(int id) {
        return id - 353;
    }
}
