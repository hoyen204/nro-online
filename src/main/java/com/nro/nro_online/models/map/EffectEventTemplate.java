package com.nro.nro_online.models.map;

import lombok.Builder;
import lombok.Getter;

/**
 * @build by arriety
 */
@Getter
@Builder
public class EffectEventTemplate {
    private int mapId;
    private int eventId;
    private int effId;
    private int layer;
    private int x;
    private int y;
    private int loop;
    private int delay;
}
