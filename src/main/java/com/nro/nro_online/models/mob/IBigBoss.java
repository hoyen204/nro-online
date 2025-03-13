/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.mob;

import nro.models.player.Player;

/**
 *
 * @Build by Arriety
 */
public interface IBigBoss {

    public void attack(Player player);

    public void move(int x, int y);

}
