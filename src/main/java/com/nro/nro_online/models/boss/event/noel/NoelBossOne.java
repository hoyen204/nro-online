/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.event.noel;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.PetService;

/**
 * @author by Arriety
 */
public class NoelBossOne extends NoelBoss {

    public NoelBossOne() {
        super(BossFactory.NOEL_BOSS_ONE, new BossData(
                "Videl Noel", //name
                ConstPlayer.XAYDA, //gender
                Boss.DAME_NORMAL, //type dame
                Boss.HP_NORMAL, //type hp
                1_000_000_000, //dame
                new int[][]{{1_515_000_000}}, //hp
                new short[]{810, 811, 812}, //outfit
                new short[]{106}, //map join
                new int[][]{ //skill
                    {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                    {Skill.THAI_DUONG_HA_SAN, 1, 7_000},},
                _15_PHUT
        ));
    }

    @Override
    public void rewards(Player pl) {
        PetService.gI().createVidelPet(pl, pl.gender);
    }

}
