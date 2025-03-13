package com.nro.nro_online.models.whis;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import nro.models.player.Player;

/**
 *
 * @author by Arriety
 */
@Getter
@Setter
public class TopWhisModel {

    public long player_id;
    public float time_skill;
    public int level;
    public int rank;
    public LocalDateTime last_time_attack;
    public Player player = new Player();
}
