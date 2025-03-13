package com.nro.nro_online.models.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nro.nro_online.models.player.Player;
import nro.models.player.Player;
import nro.server.Client;
import nro.services.Service;
import nro.utils.Util;

/**
 *
 * @stole Arriety
 */
public abstract class FutureBoss extends Boss {

    public HashMap<Long, Integer> topDame = new HashMap<>();

    public FutureBoss(byte id, BossData data) {
        super(id, data);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (plAtt == null) {
            return 0;
        }
        damage = (damage / 100) * 80;
        int da = super.injured(plAtt, damage, piercing, isMobAttack);
        if (this.id == BossFactory.CUMBER || this.id == BossFactory.CUMBER2) {
            int d = da;
            if (topDame.containsKey(plAtt.id)) {
                d += topDame.get(plAtt.id);
            }
            topDame.put(plAtt.id, d);
            HashMap<Long, Integer> hashMap = Util.sortHashMapByValue(topDame);
            List<Map.Entry<Long, Integer>> entryList = new ArrayList<>(hashMap.entrySet());
            for (int i = 0; i < 3; i++) {
                if (i < entryList.size()) {
                    Map.Entry<Long, Integer> entry = entryList.get(i);
                    if (entry != null) {
                        Player pl = Client.gI().getPlayer(entry.getKey());
                        if (pl != null) {
                            Service.getInstance().sendTextTime(plAtt, (byte) (i + 10), String.format("#%s %s [%s", i + 1, pl.name, Math.ceil((double) entry.getValue() / (double) this.nPoint.hpMax * 100)) + " %]", (short) -1);
                        }
                    }
                }
            }
            entryList.clear();
        }
        return da;
    }
}
