package com.nro.nro_online.models.boss.iboss;

import nro.models.player.Player;

/**
 *
 * Arriety
 *
 */
public interface IBossStatus extends IBossInit {

    void attack(); //attack

    void idle(); //trong lúc attack có thể đứng nghỉ

    void checkPlayerDie(Player pl); //attack player nào đó rồi kiểm tra

    void die();

    void respawn();
}
