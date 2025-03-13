package com.nro.nro_online.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

import com.nro.nro_online.jdbc.DBService;
import nro.jdbc.DBService;

public class CongTien {

    public static boolean isNumber(String a) {
        try {
            Long.valueOf(a);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try (Connection con = DBService.gI().getConnection();
                    Scanner sc = new Scanner(System.in)) {
                while (true) {
                    String line = sc.nextLine();
                    if (line != null && !line.isEmpty()) {
                        String[] a = line.split("-");
                        if (a.length == 2) { // Kiểm tra input hợp lệ
                            String userName = a[0];
                            if (isNumber(a[1])) {
                                long money = Long.parseLong(a[1]);
                                String query = "UPDATE `account` SET `vnd` = `vnd` + ?, `tongnap` = `tongnap` + ?, `pointNap` = `pointNap` + ? WHERE `username` = ?";
                                try (PreparedStatement ps = con.prepareStatement(query)) {
                                    ps.setLong(1, money);
                                    ps.setLong(2, money);
                                    ps.setLong(3, money);
                                    ps.setString(4, userName);
                                    if (ps.executeUpdate() == 1) {
                                        System.out.println("Success " + userName);
                                    }
                                }
                            } else {
                                System.out.println("Error: Số tiền không hợp lệ cho " + userName);
                            }
                        } else {
                            System.out.println("Error: Input sai định dạng (dùng username-money)");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi cmnr: " + e.getMessage());
            }
        }, "Active line").start();
    }
}