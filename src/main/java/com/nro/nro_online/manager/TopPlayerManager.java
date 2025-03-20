package com.nro.nro_online.manager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.TopPlayer;

public class TopPlayerManager {

    public static List<TopPlayer> GetTopNap() {
        List<TopPlayer> result = new ArrayList<>();
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT a.id, a.name, COALESCE(a.tongnap, 0) +  COALESCE(b.tong_nap, 0) AS `amount` FROM (SELECT b.id, b.name, a.tongnap FROM account a INNER JOIN player b ON a.id = b.account_id WHERE tongnap > 0) a LEFT JOIN (SELECT player_id, SUM(amount) AS tong_nap FROM transaction_banking WHERE status = 1 GROUP BY player_id) b ON a.id = b.player_id ORDER BY tongnap DESC LIMIT 0, 10;";
            ps = con.prepareCall(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TopPlayer top = new TopPlayer();

                top.id = rs.getLong("id");
                top.name = rs.getString("name");
                top.amount = rs.getLong("amount");

                result.add(top);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static List<TopPlayer> GetTopPower() {
        List<TopPlayer> result = new ArrayList<>();
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT id, power as `amount`, name FROM player WHERE name <> 'admin' ORDER BY power DESC LIMIT 50;";
            ps = con.prepareCall(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TopPlayer top = new TopPlayer();

                top.id = rs.getLong("id");
                top.name = rs.getString("name");
                top.amount = rs.getLong("amount");

                result.add(top);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static List<TopPlayer> GetTopRuby() {
        List<TopPlayer> result = new ArrayList<>();
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT a.id, a.name, COALESCE(a.tongnap, 0) +  COALESCE(b.tong_nap, 0) AS `amount` FROM (SELECT b.id, b.name, a.tongnap FROM account a INNER JOIN player b ON a.id = b.account_id WHERE tongnap > 0) a LEFT JOIN (SELECT player_id, SUM(amount) AS tong_nap FROM transaction_banking WHERE status = 1 GROUP BY player_id) b ON a.id = b.player_id ORDER BY tongnap DESC LIMIT 0, 10;";
            ps = con.prepareCall(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TopPlayer top = new TopPlayer();

                top.id = rs.getLong("id");
                top.name = rs.getString("name");
                top.amount = rs.getLong("amount");

                result.add(top);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
