package com.nro.nro_online.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.models.JsonResponse;
import com.nro.nro_online.models.Part;
import com.nro.nro_online.models.PartManager;
import com.nro.nro_online.models.Transaction;
import com.nro.nro_online.models.TransactionHistory;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;

public class ChuyenKhoanManager {

    public static void insertTransaction(long player_id, long amount, String description) {
        String sql = "INSERT INTO transaction_banking (player_id, amount, DESCRIPTION, STATUS, is_recieve, last_time_check, created_date) VALUES (?, ?, ?, 0, 0, NULL, NOW());";
        PreparedStatement ps = null;
        try {
            try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
                ps = con.prepareStatement(sql);
                ps.setLong(1, player_id);
                ps.setLong(2, amount);
                ps.setString(3, description);

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

    public static void showTransaction(Player player) {
        List<Transaction> list = getTransaction((int) player.id);
        Message msg = new Message(Cmd.TOP);
        try {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Lịch sử giao dịch");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                Transaction top = list.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(Math.toIntExact(top.id));
                msg.writer().writeShort(player.getHead());
                if (player.isVersionAbove(220)) {
                    Part part = PartManager.getInstance().find(player.getHead());
                    msg.writer().writeShort(part.getIcon(0));
                }
                msg.writer().writeShort(player.getBody());
                msg.writer().writeShort(player.getLeg());
                msg.writer().writeUTF(player.name);

                msg.writer().writeUTF(Util.formatCurrency(top.amount));

                msg.writer().writeUTF("Nội dung: " + top.description + "\n"
                        + "Trạng thái giao dịch: " + (top.status ? "Đã thanh toán" : "Chờ thanh toán") + "\n"
                        + "Trạng thái nhận quà " + (top.isReceive ? "Đã nhận quà" : "Chờ nhận quà") + "\n"
                        + "Ngày giao dịch " + (Util.formatLocalDateTime(top.createdDate)) + "\n");
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Transaction> getTransactionAuto() {
        List<Transaction> result = new ArrayList<>();
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT * FROM transaction_banking WHERE status = FALSE AND created_date >= NOW() - INTERVAL 60 MINUTE ORDER BY created_date DESC;";
            ps = con.prepareCall(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();

                transaction.id = rs.getLong("id");
                transaction.playerId = rs.getLong("player_id");
                transaction.amount = rs.getLong("amount");
                transaction.description = rs.getString("description");
                transaction.status = rs.getBoolean("status");
                transaction.isReceive = rs.getBoolean("is_recieve");
                transaction.lastTimeCheck = rs.getTimestamp("last_time_check").toLocalDateTime();
                transaction.createdDate = rs.getTimestamp("created_date").toLocalDateTime();

                result.add(transaction);
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

    public static List<Transaction> getTransactionDoneAuto() {
        List<Transaction> result = new ArrayList<>();
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT * FROM transaction_banking WHERE status = true AND is_recieve = FALSE AND created_date >= NOW() - INTERVAL 60 MINUTE ORDER BY created_date DESC;;";
            ps = con.prepareCall(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();

                transaction.id = rs.getLong("id");
                transaction.playerId = rs.getLong("player_id");
                transaction.amount = rs.getLong("amount");
                transaction.description = rs.getString("description");
                transaction.status = rs.getBoolean("status");
                transaction.isReceive = rs.getBoolean("is_recieve");
                transaction.lastTimeCheck = rs.getTimestamp("last_time_check").toLocalDateTime();
                transaction.createdDate = rs.getTimestamp("created_date").toLocalDateTime();

                result.add(transaction);
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

    public static List<Transaction> getTransaction(int player_id) {
        List<Transaction> result = new ArrayList<>();
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT * FROM transaction_banking WHERE player_id = ? ORDER BY created_date DESC;";
            ps = con.prepareCall(sql);
            ps.setDouble(1, player_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();

                transaction.id = rs.getLong("id");
                transaction.playerId = rs.getLong("player_id");
                transaction.amount = rs.getLong("amount");
                transaction.description = rs.getString("description");
                transaction.status = rs.getBoolean("status");
                transaction.isReceive = rs.getBoolean("is_recieve");
                transaction.lastTimeCheck = rs.getTimestamp("last_time_check").toLocalDateTime();
                transaction.createdDate = rs.getTimestamp("created_date").toLocalDateTime();

                result.add(transaction);
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

    public static Transaction getTransactionById(long player_id, int id) {
        Transaction result = null;
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT * FROM transaction_banking WHERE id = ? AND player_id = ? ORDER BY created_date DESC LIMIT 0, 1;";
            ps = con.prepareCall(sql);
            ps.setDouble(1, id);
            ps.setDouble(2, player_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = new Transaction();

                result.id = rs.getLong("id");
                result.playerId = rs.getLong("player_id");
                result.amount = rs.getLong("amount");
                result.description = rs.getString("description");
                result.status = rs.getBoolean("status");
                result.isReceive = rs.getBoolean("is_recieve");
                result.lastTimeCheck = rs.getTimestamp("last_time_check").toLocalDateTime();
                result.createdDate = rs.getTimestamp("created_date").toLocalDateTime();
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

    public static Transaction getTransactionLast(long player_id) {
        Transaction result = null;
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "SELECT * FROM transaction_banking WHERE player_id = ? ORDER BY created_date DESC LIMIT 0, 1;";
            ps = con.prepareCall(sql);
            ps.setDouble(1, player_id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                result = new Transaction();

                result.id = rs.getLong("id");
                result.playerId = rs.getLong("player_id");
                result.amount = rs.getLong("amount");
                result.description = rs.getString("description");
                result.status = rs.getBoolean("status");
                result.isReceive = rs.getBoolean("is_recieve");
                result.lastTimeCheck = rs.getTimestamp("last_time_check").toLocalDateTime();
                result.createdDate = rs.getTimestamp("created_date").toLocalDateTime();
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

    public static LocalDateTime getLasTimeCreateTransaction(Player player) {
        LocalDateTime result = null;

        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT player_id, created_date FROM transaction_banking WHERE player_id = ? ORDER BY created_date DESC LIMIT 0, 1");
            ps.setLong(1, player.id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result = rs.getTimestamp("created_date").toLocalDateTime();
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static LocalDateTime getLastimeCheckTransaction(Player player) {
        LocalDateTime result = null;

        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT player_id, MAX(last_time_check) AS last_time_check FROM transaction_banking WHERE player_id = ? ORDER BY created_date DESC LIMIT 0, 1");
            ps.setLong(1, player.id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp time = rs.getTimestamp("last_time_check");
                if (time != null) {
                    result = rs.getTimestamp("last_time_check").toLocalDateTime();
                }
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void updateLastTimeCheck(long player_id, int transactionId) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE transaction_banking set last_time_check = NOW() WHERE id = ? AND player_id = ?;");
            ps.setInt(1, transactionId);
            ps.setLong(2, player_id);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(SieuHangManager.class, e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void updateGift(long player_id, Long transactionId) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE transaction_banking set status = 1, is_recieve = 1 WHERE id = ? AND player_id = ?;");
            ps.setLong(1, transactionId);
            ps.setLong(2, player_id);
            ps.executeUpdate();
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

    public static void updateDoneNap(long player_id, Long transactionId) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE transaction_banking set status = 1 WHERE id = ? AND player_id = ?;");
            ps.setLong(1, transactionId);
            ps.setLong(2, player_id);
            ps.executeUpdate();
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

    public static void updateDoneReceive(long player_id, Long transactionId) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE transaction_banking set is_recieve = 1 WHERE id = ? AND player_id = ?;");
            ps.setLong(1, transactionId);
            ps.setLong(2, player_id);
            ps.executeUpdate();
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

    public static void updatePointNap(long player_id, double point) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE account a INNER JOIN player b ON a.id = b.account_id SET a.pointNap = a.pointNap + ? WHERE b.id = ?;");
            ps.setDouble(1, point);
            ps.setLong(2, player_id);
            ps.executeUpdate();
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

    public static void handleTransaction(Player player, int transactionId) {
        boolean canCheck = false;
        LocalDateTime lastTimeCheck = getLastimeCheckTransaction(player);
        Transaction transaction = null;
        long timeDifference = 0;

        if (lastTimeCheck == null) {
            canCheck = true;
            return;
        } else {
            LocalDateTime now = LocalDateTime.now();

            timeDifference = TimeUtil.calculateTimeDifferenceInSeconds(lastTimeCheck, now);

            if (timeDifference > 10) {
                canCheck = true;
            }

            transaction = getTransactionById(player.id, transactionId);

            if (transaction != null && (transaction.status || transaction.isReceive)) {
                Service.getInstance().sendThongBao(player, "Bạn thanh toán giao dịch này và đã nhận được ruby!");
                return;
            }
        }

        if (transaction == null) {
            return;
        }

        if (canCheck) {
            transaction = getTransactionById(player.id, transactionId);
            String history = getTransactionOnline("https://api.web2m.com/historyapimb/Tuanbeo@12345/02147019062000/F3CAC210-DAC1-BD7F-7A26-A2643A5B3DD7");

            JsonResponse response = parseApiResponse(history);

            updateLastTimeCheck(player.id, transactionId);

            if (response != null && response.getData() != null && response.getData().size() > 0) {
                for (TransactionHistory transactionHistory : response.getData()) {
                    if (Double.parseDouble(transactionHistory.getCreditAmount()) == transaction.amount && Util.containsSubstring(transactionHistory.getDescription(), transaction.description)) {
                        double ruby = transaction.amount;

                        PlayerDAO.addVnd(player, (int) transaction.amount);
                        player.getSession().vnd += ruby;
                        player.getSession().poinCharging += ruby;
                        System.out.println("Add ruby: " + ruby);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được tiền là: " + ruby);
                        updateGift(player.id, (long) transactionId);
                        updatePointNap(player.id, ruby);
                        return;
                    }
                }
            }
            Service.getInstance().sendThongBao(player, "Tài khoản của admin chưa nhận được tiền hoặc bạn chuyển khoản sai nội dung!");
        } else {
            Service.getInstance().sendThongBao(player, "Bạn cần đợi " + (10 - timeDifference) + " giây nữa để được check giao dịch");
        }
    }

    public static void handleTransactionAuto() {
        List<Transaction> transactions = getTransactionAuto();

        if (!transactions.isEmpty()) {
            String history = getTransactionOnline("https://api.web2m.com/historyapimb/Tuanbeo@12345/02147019062000/F3CAC210-DAC1-BD7F-7A26-A2643A5B3DD7");
            JsonResponse response = parseApiResponse(history);

            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                for (TransactionHistory transactionHistory : response.getData()) {
                    for (Transaction transaction : transactions) {
                        if (Double.parseDouble(transactionHistory.getCreditAmount()) == transaction.amount && Util.containsSubstring(transactionHistory.getDescription(), transaction.description)) {
                            updateDoneNap(transaction.playerId, transaction.id);
                            updatePointNap(transaction.playerId, transaction.amount);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void handleTransactionAddMoneyAuto() {
        List<Transaction> transactions = getTransactionDoneAuto();

        if (!transactions.isEmpty()) {
            for (Transaction transaction : transactions) {
                Player player = Client.gI().getPlayer(transaction.getPlayerId());
                if (player != null) {
                    PlayerDAO.addVnd(player, (int) transaction.amount);
                    player.getSession().vnd += (int) transaction.amount;
                    System.out.println("Add ruby: " + transaction.amount);
                    Service.getInstance().sendThongBao(player, "Bạn nhận được tiền là: " + transaction.amount);
                    updateDoneReceive(player.id, transaction.id);
                }
            }
        }
    }

    private static String getTransactionOnline(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JsonResponse parseApiResponse(String apiResponse) {
        Gson gson = new Gson();
        return gson.fromJson(apiResponse, JsonResponse.class);
    }
}
