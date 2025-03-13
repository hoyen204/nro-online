package com.nro.nro_online.models.boss.iboss;

import nro.models.player.Player;

/**
 *
 * Arriety
 *
 */
public interface BossInterface extends IBossStatus {

    void update();

    void rewards(Player pl); //phần thưởng sau khi bị chết

    Player getPlayerAttack() throws Exception; //lấy ra 1 player để đánh

    void joinMap();

    void leaveMap();

    boolean talk();

    void generalRewards(Player player);
}
