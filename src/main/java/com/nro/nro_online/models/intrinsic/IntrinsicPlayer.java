package com.nro.nro_online.models.intrinsic;

import com.nro.nro_online.services.IntrinsicService;

/**
 *
 * Arriety
 *
 */
public class IntrinsicPlayer {

    public byte countOpen;

    public Intrinsic intrinsic;

    public IntrinsicPlayer() {
        this.intrinsic = IntrinsicService.gI().getIntrinsicById(0);
    }

    public void dispose() {
        this.intrinsic = null;
    }
}
