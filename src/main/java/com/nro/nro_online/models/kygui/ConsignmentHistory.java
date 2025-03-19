/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.kygui;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.utils.Log;

/**
 *
 * @author by Arriety
 */
public class ConsignmentHistory {

    public static void logSellItemShop(String uid, String nameItem, long idPlayer, String namePlayer, byte monneyType,
            int quantity) {
        String UPDATE_PASS = "INSERT INTO consignment_history (uid, item_name, id_user ,name_player, type_buy , quantity, time) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBService.gI().getConnectionForGetPlayer().prepareStatement(UPDATE_PASS)) {
            ps.setString(1, uid);
            ps.setString(1, uid);
            ps.setString(2, nameItem);
            ps.setLong(3, idPlayer);
            ps.setString(4, namePlayer);
            ps.setByte(5, monneyType);
            ps.setInt(6, quantity);
            ps.setString(7, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")));

            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(ConsignmentHistory.class, e);
        }
    }

    public static void LogBuyItemShop(String uid, String nameItem, long idPlayer, String namePlayer, byte monneyType,
            int quantity) {
        String UPDATE_PASS = "INSERT INTO consignment_history (uid, item_name, id_user ,name_player, type_buy , quantity, time) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBService.gI().getConnectionForGetPlayer().prepareStatement(UPDATE_PASS)) {
            ps.setString(1, uid);
            ps.setString(2, nameItem);
            ps.setLong(3, idPlayer);
            ps.setString(4, namePlayer);
            ps.setByte(5, monneyType);
            ps.setInt(6, quantity);
            ps.setString(7, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")));

            ps.executeUpdate();
        } catch (SQLException e) {
            Log.error(ConsignmentHistory.class, e);
        }
    }

}
