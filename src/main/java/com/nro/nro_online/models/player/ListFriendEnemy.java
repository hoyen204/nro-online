package com.nro.nro_online.models.player;

import java.util.ArrayList;

/**
 *
 * Arriety
 *
 */
public class ListFriendEnemy<T> extends ArrayList<T> {

    public final Player player;

    public ListFriendEnemy(Player player) {
        this.player = player;
    }

}
