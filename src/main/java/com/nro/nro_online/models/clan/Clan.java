package com.nro.nro_online.models.clan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.dungeon.SnakeRoad;
import com.nro.nro_online.models.map.phoban.BanDoKhoBau;
import com.nro.nro_online.models.map.phoban.DoanhTrai;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.ClanService;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;
import lombok.Getter;
import lombok.Setter;

public class Clan {

    public static int NEXT_ID = 0;
    public int clanMessageId = 0;
    private final List<ClanMessage> clanMessages = new ArrayList<>();
    private static final Gson GSON = new Gson();

    public static final byte LEADER = 0;
    public static final byte DEPUTY = 1;
    public static final byte MEMBER = 2;

    public int id;
    public int imgId;
    public String name;
    public String slogan;
    public int createTime;
    public long powerPoint;
    public byte maxMember;
    public int level;
    public int clanPoint;
    public boolean active;
    @Getter
    public final List<ClanMember> members = new ArrayList<>();
    public final List<Player> membersInGame = new ArrayList<>();
    public boolean haveGoneDoanhTrai;
    public DoanhTrai doanhTrai;
    public Player playerOpenDoanhTrai;
    public long timeOpenDoanhTrai;
    public BanDoKhoBau banDoKhoBau;
    public SnakeRoad snakeRoad;
    public Player playerOpenBanDoKhoBau;
    public long timeOpenBanDoKhoBau;
    public boolean isLeader;
    @Setter
    @Getter
    private Buff buff;
    @Getter
    private Zone clanArea;

    public Clan() {
        this.id = NEXT_ID++;
        this.name = "";
        this.slogan = "";
        this.maxMember = 20;
        this.createTime = (int) (System.currentTimeMillis() / 1000);
        initialize();
    }

    private void initialize() {
        Map map = MapService.gI().getMapById(153);
        this.clanArea = new Zone(map, 0, 50);
        Zone z = map.zones.getFirst();
        if (z != null) {
            for (Mob m : z.mobs) {
                Mob mob = new Mob(m);
                mob.zone = clanArea;
                clanArea.addMob(mob);
            }
        }
    }

    public ClanMember getLeader() {
        return members.stream().filter(cm -> cm.role == LEADER).findFirst().orElseGet(() -> {
            ClanMember cm = new ClanMember();
            cm.name = "Bang chủ";
            return cm;
        });
    }

    public byte getRole(Player player) {
        return (byte) members.stream().filter(cm -> cm.id == player.id).mapToInt(cm -> cm.role).findFirst().orElse(-1);
    }

    public boolean isLeader(Player player) {
        return members.stream().anyMatch(cm -> cm.id == player.id && cm.role == LEADER);
    }

    public boolean isDeputy(Player player) {
        return members.stream().anyMatch(cm -> cm.id == player.id && cm.role == DEPUTY);
    }

    public void addSMTNClan(Player plOri, long param) {
        membersInGame.forEach(pl -> {
            if (!plOri.equals(pl) && plOri.zone != null && plOri.zone.equals(pl.zone)) {
                Service.getInstance().addSMTN(pl, (byte) 1, param, false);
            }
        });
    }

    public void sendMessageClan(ClanMessage cmg) {
        try (Message msg = new Message(-51)) {
            msg.writer().writeByte(cmg.type);
            msg.writer().writeInt(cmg.id);
            msg.writer().writeInt(cmg.playerId);
            msg.writer().writeUTF(cmg.type == 2 ? cmg.playerName + " (" + Util.numberToMoney(cmg.playerPower) + ")" : cmg.playerName);
            msg.writer().writeByte(cmg.role);
            msg.writer().writeInt(cmg.time);
            if (cmg.type == 0) {
                msg.writer().writeUTF(cmg.text);
                msg.writer().writeByte(cmg.color);
            } else if (cmg.type == 1) {
                msg.writer().writeByte(cmg.receiveDonate);
                msg.writer().writeByte(cmg.maxDonate);
                msg.writer().writeByte(cmg.isNewMessage);
            }
            membersInGame.forEach(pl -> pl.sendMessage(msg));
        } catch (Exception e) {
            Log.error(Clan.class, e);
        }
    }

    public void addClanMessage(ClanMessage cmg) {
        this.clanMessages.add(0, cmg);
    }

    public ClanMessage getClanMessage(int clanMessageId) {
        return clanMessages.stream().filter(cmg -> cmg.id == clanMessageId).findFirst().orElse(null);
    }

    public List<ClanMessage> getCurrClanMessages() {
        return clanMessages.size() <= 20 ? new ArrayList<>(clanMessages) : clanMessages.subList(0, 20);
    }

    public void sendRemoveClanForAllMember() {
        membersInGame.forEach(pl -> {
            if (pl != null) ClanService.gI().sendRemoveClan(pl);
        });
    }

    public void sendMyClanForAllMember() {
        membersInGame.forEach(pl -> {
            if (pl != null) ClanService.gI().sendMyClan(pl);
        });
    }

    public void sendRemoveForAllMember() {
        membersInGame.forEach(pl -> {
            if (pl != null) Service.getInstance().sendThongBao(pl, "Bang Hội của bạn đã bị giải tán.");
        });
    }

    public void sendFlagBagForAllMember() {
        membersInGame.forEach(pl -> {
            if (pl != null) Service.getInstance().sendFlagBag(pl);
        });
    }

    public void addMemberOnline(Player player) {
        this.membersInGame.add(player);
    }

    public void removeMemberOnline(ClanMember cm, Player player) {
        if (player != null) membersInGame.remove(player);
        if (cm != null) membersInGame.removeIf(p -> p.id == cm.id);
    }

    public Player getPlayerOnline(int playerId) {
        return membersInGame.stream().filter(p -> p.id == playerId).findFirst().orElse(null);
    }

    public void addClanMember(ClanMember cm) {
        this.members.add(cm);
    }

    public void addClanMember(Player player, byte role) {
        ClanMember cm = new ClanMember(player, this, role);
        this.members.add(cm);
        player.clanMember = cm;
    }

    public void removeClanMember(ClanMember cm) {
        this.members.remove(cm);
    }

    public byte getCurrMembers() {
        return (byte) this.members.size();
    }

    public ClanMember getClanMember(int memberId) {
        return members.stream().filter(cm -> cm.id == memberId).findFirst().orElse(null);
    }

    public void reloadClanMember() {
        members.forEach(cm -> {
            Player pl = Client.gI().getPlayer(cm.id);
            if (pl != null) cm.powerPoint = pl.nPoint.power;
        });
    }

    public void insert() {
        try (Connection con = DBService.gI().getConnectionForClan();
                PreparedStatement ps = con.prepareStatement("INSERT INTO clan_sv" + Manager.SERVER + "(id, name, slogan, img_id, power_point, max_member, clan_point, level, members) VALUES (?,?,?,?,?,?,?,?,?)")) {
            ps.setInt(1, this.id);
            ps.setString(2, this.name);
            ps.setString(3, this.slogan);
            ps.setInt(4, this.imgId);
            ps.setLong(5, this.powerPoint);
            ps.setByte(6, this.maxMember);
            ps.setInt(7, this.clanPoint);
            ps.setInt(8, this.level);
            ps.setString(9, GSON.toJson(members));
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(Clan.class, e);
        }
    }

    public void update() {
        try (Connection con = DBService.gI().getConnectionForClan();
                PreparedStatement ps = con.prepareStatement("UPDATE clan_sv" + Manager.SERVER + " SET slogan=?, img_id=?, power_point=?, max_member=?, clan_point=?, level=?, members=? WHERE id=? LIMIT 1")) {
            ps.setString(1, this.slogan);
            ps.setInt(2, this.imgId);
            ps.setLong(3, this.powerPoint);
            ps.setByte(4, this.maxMember);
            ps.setInt(5, this.clanPoint);
            ps.setInt(6, this.level);
            ps.setString(7, GSON.toJson(members));
            ps.setInt(8, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(Clan.class, e);
        }
    }
}