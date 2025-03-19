package com.nro.nro_online.manager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.whis.TopWhisModel;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;

public class TopWhis {

    public static List<Long> TOP_ID = new ArrayList<>();

    public static long TOP_ONE = 0;
    public static long TOP_TWO = 0;
    public static long TOP_THREE = 0;

    public static void update() {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.getHour() == 0 && currentTime.getMinute() == 0 && currentTime.getSecond() == 0) {
            List<TopWhisModel> tops = getTop(3);
            for (TopWhisModel top : tops) {
                if (top.rank == 1) {
                    TOP_ONE = top.player_id;
                } else if (top.rank == 2) {
                    TOP_TWO = top.player_id;
                } else if (top.rank == 3) {
                    TOP_THREE = top.player_id;
                }
            }
            truncateTable("top_whis");
        }
    }

    public static int getMaxPlayerId() {
        try (Connection con = DBService.gI().getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT MAX(id) + 1000 AS id FROM player");
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            Log.error(TopWhis.class, e);
        }
        return 1;
    }

    public static int getLevel(long playerId) {
        try (Connection con = DBService.gI().getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT COALESCE(MAX(level) + 1, 1) AS level FROM top_whis WHERE player_id = ?")) {
            ps.setLong(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt("level");
            }
        } catch (SQLException e) {
            Log.error(TopWhis.class, e);
        }
        return 1;
    }

    public static void truncateTable(String tableName) {
        try (Connection con = DBService.gI().getConnectionForGame();
                Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE " + tableName);
        } catch (SQLException e) {
            Log.error(TopWhis.class, e);
        }
    }

    public static List<TopWhisModel> getTop() {
        List<TopWhisModel> result = new ArrayList<>();

        try {
            TOP_ID = new ArrayList<>();
            TopWhisModel top;

            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("""
                        SELECT
                          a.player_id,
                          a.time_kill,
                          a.level,
                          a.last_time_attack,
                          ROW_NUMBER() OVER (ORDER BY a.level DESC, a.time_kill) AS rank,
                          b.head,
                          b.items_body,
                          b.name
                        FROM (
                          SELECT
                            a.player_id,
                            a.time_kill,
                            a.level,
                            a.last_time_attack,
                            ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY level DESC, time_kill DESC) AS rank
                          FROM (
                            SELECT
                              *,
                              ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY level DESC, time_kill DESC) AS RowNum
                            FROM top_whis
                          ) a
                          WHERE a.RowNum = 1
                        ) a
                        INNER JOIN player b ON a.player_id = b.id
                        ORDER BY a.level DESC, a.time_kill
                        LIMIT 10;
                    """);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                top = new TopWhisModel();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                top.player_id = rs.getLong("player_id");
                top.time_skill = rs.getFloat("time_kill");
                top.level = rs.getInt("level");
                top.rank = rs.getInt("rank");
                top.last_time_attack = (rs.getTimestamp("last_time_attack")).toLocalDateTime();

                top.player = new Player();

                top.player.name = rs.getString("name");
                top.player.head = rs.getShort("head");

                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.length(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataObject.get("option")).replace("\"", ""));
                        for (int j = 0; j < options.length(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createNullItem();
                        }
                    } else {
                        item = ItemService.gI().createNullItem();
                    }
                    top.player.inventory.itemsBody.add(item);
                }
                dataArray.clear();
                if (dataObject != null)
                    dataObject.clear();

                result.add(top);
            }
        } catch (Exception e) {
            Log.error(TopWhis.class, e);
        }

        return result;
    }

    public static List<TopWhisModel> getTop(int limit) {
        List<TopWhisModel> result = new ArrayList<>();
        try {
            TOP_ID = new ArrayList<>();
            TopWhisModel top;

            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT\n"
                    + "  a.player_id,\n"
                    + "  a.time_kill,\n"
                    + "  a.`level`,\n"
                    + "  a.last_time_attack,\n"
                    + "  ROW_NUMBER() OVER (ORDER BY LEVEL DESC, time_kill) AS `rank`,\n"
                    + "  b.head,\n"
                    + "  b.items_body,\n"
                    + "  b.name,\n"
                    + "  a.last_time_attack\n"
                    + "FROM (SELECT\n"
                    + "    a.player_id,\n"
                    + "    a.time_kill,\n"
                    + "    a.`LEVEL`,\n"
                    + "    a.last_time_attack,\n"
                    + "    ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY LEVEL DESC, time_kill DESC) AS `rank`\n"
                    + "  FROM (SELECT\n"
                    + "      *,\n"
                    + "      ROW_NUMBER() OVER (PARTITION BY player_id ORDER BY LEVEL DESC, time_kill DESC) AS RowNum\n"
                    + "    FROM top_whis) a\n"
                    + "  WHERE a.RowNum = 1) a\n"
                    + "  INNER JOIN player b\n"
                    + "    ON a.player_id = b.id\n"
                    + "ORDER BY a.LEVEL DESC, a.time_kill\n"
                    + "LIMIT 0, " + limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                top = new TopWhisModel();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                top.player_id = rs.getLong("player_id");
                top.time_skill = rs.getFloat("time_kill");
                top.level = rs.getInt("level");
                top.rank = rs.getInt("rank");
                top.last_time_attack = (rs.getTimestamp("last_time_attack")).toLocalDateTime();

                top.player = new Player();

                top.player.name = rs.getString("name");
                top.player.head = rs.getShort("head");

                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.length(); i++) {
                    Item item;
                    dataObject = (JSONObject) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.length(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createNullItem();
                        }
                    } else {
                        item = ItemService.gI().createNullItem();
                    }
                    top.player.inventory.itemsBody.add(item);
                }
                dataArray.clear();
                if (dataObject != null)
                    dataObject.clear();

                result.add(top);
            }
        } catch (Exception e) {
            Log.error(TopWhis.class, e);
        }
        return result;
    }

    public static void addHistory(Player pl) {
        Connection con = null;
        CallableStatement ps = null;
        try {
            Duration duration = Duration.between(pl.getTimeCache(), LocalDateTime.now());
            double seconds = duration.toNanos() / 1_000_000_000.0;

            con = DBService.gI().getConnection();
            String sql = "{CALL Proc_Insert_TopWhis_History(?, ?)}";
            ps = con.prepareCall(sql);
            ps.setDouble(1, pl.id);
            ps.setDouble(2, seconds);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(TopWhis.class, e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void switchToWhisBoss(Player player, int whisId, int level) {
        Service.getInstance().sendEffectHideNPCPlayer(player, (byte) 56, (byte) 0);
        Boss whis = BossFactory.createWhisBoss(whisId + player.id, level, player.id);
        whis.zone = player.zone;
        whis.name = whis.name + "[" + level + "]";
        whis.typePk = ConstPlayer.NON_PK;
        whis.location.x = 370;
        whis.location.y = 360;
        whis.setStatus((byte) 71);
        whis.joinMap();
        if (player.zone != null) {
            player.location.x = 475;
            player.location.y = 360;
            player.zone.mapInfo(player, 56);
            player.zone.loadAnotherToMe(player);
            player.zone.loadMeToAnother(player);
        }
        Service.getInstance().chat(whis, "Ngon thì zô đây nhót!");
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                player.setTimeCache(LocalDateTime.now());
                whis.typePk = ConstPlayer.PK_ALL;
                PlayerService.gI().sendTypePk(whis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            whis.setStatus((byte) 3);
        }).start();
    }
}
