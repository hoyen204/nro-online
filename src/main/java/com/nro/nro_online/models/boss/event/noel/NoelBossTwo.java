/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.boss.event.noel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.consts.ConstRatio;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.Util;

public class NoelBossTwo extends NoelBoss {

    private LocalDateTime last_time_attack;

    public NoelBossTwo() {
        super(BossFactory.NOEL_BOSS_TWO, new BossData(
                "Quy Lão Noel", //name
                ConstPlayer.XAYDA, //gender
                Boss.DAME_NORMAL, //type dame
                Boss.HP_NORMAL, //type hp
                1_000_000_000, //dame
                new int[][]{{100}}, //hp
                new short[]{657, 658, 659}, //outfit
                new short[]{106}, //map join
                new int[][]{ //skill
                    {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                    {Skill.THAI_DUONG_HA_SAN, 1, 7_000},},
                _15_PHUT
        ));
    }

    @Override
    public void rewards(Player pl) {
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.nPoint.hp <= 0) {
            Service.getInstance().sendThongBao(pl, "Hãy quay lại khi mạnh hơn");
            Player player = this.zone.getRandomPlayerInMap();
            if (player != null) {
                this.players.clear();
                AddPlayerCanAttack(player);
            } else {
                leaveMap();
            }
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!players.contains(plAtt)) {
            Service.getInstance().sendThongBao(plAtt, "Tấn công vô hiệu do bạn chưa rửa tay");
            return 0;
        }
        damage = 1;
        this.nPoint.subHP(damage);
        if (isDie()) {
            rewards(plAtt);
            notifyPlayeKill(plAtt);
            setDie(plAtt);
            leaveMap();
        }
        return damage;
    }

    @Override
    public void joinMap() {
        try {
            if (this.zone != null) {
                MapService.gI().goToMap(this, this.zone);
            } else {
                BossManager.gI().removeBoss(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl == null || pl.isDie() || pl.isMiniPet || pl.effectSkin.isVoHinh) {
                return;
            }
            this.playerSkill.skillSelect = this.getSkillAttack();
            if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                if (this.last_time_attack == null) {
                    this.last_time_attack = LocalDateTime.now();
                }
                if (ChronoUnit.SECONDS.between(this.last_time_attack, LocalDateTime.now()) >= 10) {
                    List<Player> playerInMaps = new ArrayList<>();
                    List<Player> playerCanAttack = new ArrayList<>();

                    for (Player player : this.players) {
                        if (player != null && player.zone != null && player.zone.map.mapId == 106) {
                            playerInMaps.add(player);
                        }
                    }
                    if (playerInMaps.size() >= 5) {
                        Collections.shuffle(playerInMaps);

                        playerCanAttack = playerInMaps.subList(0, 5);
                    } else {
                        Random random = new Random();
                        for (int i = 0; i < 5; i++) {
                            if (i < playerInMaps.size()) {
                                playerCanAttack.add(playerInMaps.get(i));
                            } else {
                                playerCanAttack.add(playerInMaps.get(random.nextInt(playerInMaps.size())));
                            }
                        }
                    }
                    NoelBossBall ball;
                    Boss heal = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_TWO);
                    if (heal != null) {
                        ball = BossFactory.createNoelBossBall(BossFactory.NOEL_BOSS_BALL, heal, this);
                    } else {
                        ball = BossFactory.createNoelBossBall(BossFactory.NOEL_BOSS_BALL, playerCanAttack.get(0), this);
                    }
                    NoelBossBall ball1 = BossFactory.createNoelBossBall(BossFactory.NOEL_BOSS_BALL_2, playerCanAttack.get(1), this);
                    NoelBossBall ball2 = BossFactory.createNoelBossBall(BossFactory.NOEL_BOSS_BALL_3, playerCanAttack.get(2), this);
                    NoelBossBall ball3 = BossFactory.createNoelBossBall(BossFactory.NOEL_BOSS_BALL_4, playerCanAttack.get(3), this);
                    NoelBossBall ball4 = BossFactory.createNoelBossBall(BossFactory.NOEL_BOSS_BALL_5, playerCanAttack.get(4), this);
                    Service.getInstance().chat(this, "Tinh hà vẫn lạc");
                    for (Player play : this.zone.getPlayers()) {
                        BossXuatHien(play, ball, pl);
                        BossXuatHien(play, ball1, pl);
                        BossXuatHien(play, ball2, pl);
                        BossXuatHien(play, ball3, pl);
                        BossXuatHien(play, ball4, pl);
                    }
                    this.last_time_attack = LocalDateTime.now();
                } else {
                    if (Util.isTrue(15, ConstRatio.PER100)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        } else {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null);
                    checkPlayerDie(pl);
                }
            } else {
                goToPlayer(pl, false);
            }
        } catch (Exception ex) {
            Log.error(Boss.class, ex);
        }
    }

    private static void BossXuatHien(Player play, NoelBossBall ball, Player pl) {
        ball.zone.loadAnotherToMe(play);
        ball.zone.loadMeToAnother(play);
        new Thread(() -> {
            try {
                Thread.sleep(50);
                for (int i = 0; i < 4; i++) {
                    new Thread(() -> {
                        ball.goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(30, 500)),
                                pl.location.y - Util.nextInt(50, 500), false);
                    }).start();
                    Thread.sleep(1000);
                }
                ball.goToXY(pl.location.x, pl.location.y, false);
                ball.setCan_attack(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
