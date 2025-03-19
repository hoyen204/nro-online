package com.nro.nro_online.models.player;

import com.nro.nro_online.manager.TopWhis;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;

public class UpdateEffChar {

    private static final UpdateEffChar instance = new UpdateEffChar();

    public static UpdateEffChar gI() {
        return instance;
    }

    public void updateEff(Player player) {
        try {
            if (player.isPl()) {
                int playerIdInt = (int) player.id;
                if (TopWhis.TOP_ONE == player.id) {
                    Service.getInstance().addEffectChar(player, 58, 1, -1, -1, -1);
                }
                if (TopWhis.TOP_THREE == player.id) {
                    Service.getInstance().addEffectChar(player, 57, 1, -1, -1, -1);
                }
                if (TopWhis.TOP_TWO == player.id) {
                    Service.getInstance().addEffectChar(player, 56, 1, -1, -1, -1);
                }
                if (playerIdInt == 16803) {
                    Service.getInstance().addEffectChar(player, 80, 1, -1, -1, 1);
                }
            }
        } catch (Exception e) {
            Log.error(UpdateEffChar.class, e);
        }
    }
}
