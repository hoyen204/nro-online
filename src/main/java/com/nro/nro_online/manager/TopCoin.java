package com.nro.nro_online.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lombok.Getter;
import nro.jdbc.DBService;

/**
 *
 * @author Arriety
 */
public class TopCoin implements Runnable {

    @Getter
    private String namePlayer = "";
    private static final TopCoin INSTANCE = new TopCoin();

    public static TopCoin getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {
        try {
            Connection con = DBService.gI().getConnection();
            while (con != null) {
                try (PreparedStatement ps = con.prepareStatement("SELECT player.id, player.name, player.head, player.gender, player.data_point, tongnap FROM player INNER JOIN account ON account.id = player.account_id WHERE tongnap > 0 ORDER BY tongnap DESC LIMIT 1;"); ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String nameTemp = rs.getString("name");
                        if (!namePlayer.equals(nameTemp)) {
                            namePlayer = nameTemp;
                            rs.getInt("tongnap");
                        }
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
