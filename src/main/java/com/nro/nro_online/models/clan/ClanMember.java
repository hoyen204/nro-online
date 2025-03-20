package com.nro.nro_online.models.clan;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.google.gson.annotations.SerializedName;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.utils.TimeUtil;

public class ClanMember {

    public transient Clan clan;
    public int id;
    public short head;
    public short leg;
    public short body;
    public String name;
    public byte role;
    @SerializedName("power")
    public long powerPoint;
    public int donate;
    @SerializedName("receive_donate")
    public int receiveDonate;
    @SerializedName("member_point")
    public int memberPoint;
    @SerializedName("clan_point")
    public int clanPoint;
    @SerializedName("last_request")
    public int lastRequest;
    @SerializedName("join_time")
    public int joinTime;
    @SerializedName("ask_pea_time")
    public long timeAskPea;

    public ClanMember() {
    }

    public ClanMember(Player player, Clan clan, byte role) {
        this.clan = clan;
        this.id = (int) player.id;
        this.head = player.getHead();
        this.body = player.getBody();
        this.leg = player.getLeg();
        this.name = player.name;
        this.role = role;
        this.powerPoint = player.nPoint.power;
        this.donate = 0;
        this.receiveDonate = 0;
        this.memberPoint = 0;
        this.clanPoint = 0;
        this.lastRequest = 0;
        this.joinTime = (int) System.currentTimeMillis() / 1000;
    }

    public int getNumDateFromJoinTimeToToday() {
        ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());
        return (int) TimeUtil.diffDate(LocalDateTime.now(),
                LocalDateTime.ofEpochSecond(this.joinTime, 0, offset),
                TimeUtil.TimeUnit.DAY);
    }
}