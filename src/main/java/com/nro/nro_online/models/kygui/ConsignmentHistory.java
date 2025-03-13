/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.kygui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nro.jdbc.DBService;

/**
 *
 * @author by Arriety
 */
public class ConsignmentHistory {

    public static void logSellItemShop(String uid, String nameItem, long idPlayer, String namePlayer, byte monneyType, int quantity) {
        String UPDATE_PASS = "INSERT INTO consignment_history (uid, item_name, id_user ,name_player, type_buy , quantity, time) VALUES(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
                ps = con.prepareStatement(UPDATE_PASS);
                ps.setString(1, uid);
                ps.setString(2, nameItem);
                ps.setLong(3, idPlayer);
                ps.setString(4, namePlayer);
                ps.setByte(5, monneyType);
                ps.setInt(6, quantity);
                ps.setString(7, new SimpleDateFormat("HH:mm - dd/MM/yyyy").format(new Date()));

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void LogBuyItemShop(String uid, String nameItem, long idPlayer, String namePlayer, byte monneyType, int quantity) {
        String UPDATE_PASS = "INSERT INTO consignment_history (uid, item_name, id_user ,name_player, type_buy , quantity, time) VALUES(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
                ps = con.prepareStatement(UPDATE_PASS);
                ps.setString(1, uid);
                ps.setString(2, nameItem);
                ps.setLong(3, idPlayer);
                ps.setString(4, namePlayer);
                ps.setByte(5, monneyType);
                ps.setInt(6, quantity);
                ps.setString(7, new SimpleDateFormat("HH:mm - dd/MM/yyyy").format(new Date()));

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}
