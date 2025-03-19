package com.nro.nro_online.models.whis;

import java.time.LocalDateTime;

import com.nro.nro_online.models.player.Player;

public class TopWhisModel {

    public long player_id;
    public float time_skill;
    public int level;
    public int rank;
    public LocalDateTime last_time_attack;
    public Player player;
}
