package com.nro.nro_online.models.mob;

import com.nro.nro_online.models.player.Player;

public interface IBigBoss {

    public void attack(Player player);

    public void move(int x, int y);

}
