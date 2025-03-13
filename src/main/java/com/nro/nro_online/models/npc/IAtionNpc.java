package com.nro.nro_online.models.npc;

import com.nro.nro_online.models.player.Player;

public interface IAtionNpc {

    void openBaseMenu(Player player);

    void confirmMenu(Player player, int select);

}
