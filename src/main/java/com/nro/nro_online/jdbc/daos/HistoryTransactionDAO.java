package com.nro.nro_online.jdbc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.TimeUtil;

public class HistoryTransactionDAO {

    public static void insert(Player player1, Player player2, int goldP1, int goldP2,
            List<Item> itemP1, List<Item> itemP2, List<Item> bag1Before, List<Item> bag2Before,
            List<Item> bag1After, List<Item> bag2After, long gold1Before, long gold2Before,
            long gold1After, long gold2After) {
        try (Connection con = DBService.gI().getConnectionForSaveHistory();
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO history_transaction VALUES (?,?,?,?,?,?,?,?,?)")) {
            ps.setString(1, player1.name + " (" + player1.id + ")");
            ps.setString(2, player2.name + " (" + player2.id + ")");
            ps.setString(3, buildTradeItems(goldP1, itemP1));
            ps.setString(4, buildTradeItems(goldP2, itemP2));
            ps.setString(5, buildBagItems(gold1Before, bag1Before));
            ps.setString(6, buildBagItems(gold2Before, bag2Before));
            ps.setString(7, buildBagItems(gold1After, bag1After));
            ps.setString(8, buildBagItems(gold2After, bag2After));
            ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(HistoryTransactionDAO.class, e);
        }
    }

    private static String buildTradeItems(int gold, List<Item> items) {
        StringBuilder sb = new StringBuilder("Gold: ").append(gold);
        for (Item item : items) {
            sb.append(", ").append(item.template.name).append(" (x").append(item.quantity).append(")");
        }
        return sb.toString();
    }

    private static String buildBagItems(long gold, List<Item> items) {
        StringBuilder sb = new StringBuilder("Gold: ").append(gold).append(", ");
        for (Item item : items) {
            if (item.isNotNullItem()) {
                sb.append(item.template.name).append(" [");
                for (ItemOption io : item.itemOptions) {
                    sb.append(io.optionTemplate.name.replace("#", String.valueOf(io.param))).append(",");
                }
                if (!item.itemOptions.isEmpty()) sb.setLength(sb.length() - 1);
                sb.append("] (x").append(item.quantity).append("); ");
            }
        }
        return sb.toString();
    }

    public static void deleteHistory() {
        try (Connection con = DBService.gI().getConnectionForAutoSave();
                PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM history_transaction WHERE created_at < ?")) {
            ps.setString(1, TimeUtil.getTimeBeforeCurrent(3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd"));
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(HistoryTransactionDAO.class, e);
        }
    }
}