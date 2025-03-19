package com.nro.nro_online.models.player;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

/**
 * @build by arriety
 */
public class Referee extends Player {

    private long lastTimeChat;

    public void initReferee() {
        init();
    }

    private Zone z;

    @Override
    public short getHead() {
        return 114;
    }

    @Override
    public short getBody() {
        return 115;
    }

    @Override
    public short getLeg() {
        return 116;
    }

    public void joinMap(Zone z, Player player) {
        MapService.gI().goToMap(player, z);
        z.loadMeToAnother(player);
    }

    @Override
    public int version() {
        return 214;
    }

    @Override
    public void update() {
        if (Util.canDoWithTime(lastTimeChat, 5000)) {
            Service.getInstance().chat(this, "Đại Hội Võ Thuật lần thứ 23 đã chính thức khai mạc");
            Service.getInstance().chat(this, "Còn chờ gì nữa mà không đăng kí tham gia để nhận nhiều phẩn quà hấp dẫn");
            lastTimeChat = System.currentTimeMillis();
        }
    }

    private void init() {
        int id = -1000000;
        for (Map m : Manager.MAPS) {
            if (m.mapId == ConstMap.DAI_HOI_VO_THUAT) {
                for (Zone z : m.zones) {
                    Referee pl = new Referee();
                    pl.name = "Trọng Tài";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 69;
                    pl.nPoint.hpg = 69;
                    pl.nPoint.hp = 69;
                    pl.nPoint.setFullHpMp();
                    pl.location.x = 387;
                    pl.location.y = 336;
                    pl.isMiniPet = true;
                    joinMap(z, pl);
                    z.setReferee(pl);
                }
            } else if (m.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
                for (Zone z : m.zones) {
                    Referee pl = new Referee();
                    pl.name = "Trọng Tài";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 69;
                    pl.nPoint.hpg = 69;
                    pl.nPoint.hp = 69;
                    pl.nPoint.setFullHpMp();
                    pl.location.x = 385;
                    pl.location.y = 264;
                    pl.isMiniPet = true;
                    joinMap(z, pl);
                    z.setReferee(pl);
                }
            }
        }
    }
}
