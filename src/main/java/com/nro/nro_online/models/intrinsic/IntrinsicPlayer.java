package com.nro.nro_online.models.intrinsic;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.IntrinsicService;
import nro.models.player.Player;
import nro.services.IntrinsicService;

/**
 *
 * Arriety
 *
 */
public class IntrinsicPlayer {

    private Player player;

    public byte countOpen;

    public Intrinsic intrinsic;

    public IntrinsicPlayer(Player player) {
        this.player = player;
        this.intrinsic = IntrinsicService.gI().getIntrinsicById(0);
    }

    public void dispose() {
        this.player = null;
        this.intrinsic = null;
    }
}
