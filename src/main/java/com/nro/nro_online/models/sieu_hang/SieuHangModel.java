package com.nro.nro_online.models.sieu_hang;

import com.nro.nro_online.models.player.Player;
import nro.models.player.Player;

public class SieuHangModel {

    public long player_id;
    public int dame;
    public int defend;
    public int rank;
    public String message;
    public Player player = new Player();
}
