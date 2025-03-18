package com.nro.nro_online.jdbc.daos;

import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.item.ItemShop;
import com.nro.nro_online.models.shop.Shop;
import com.nro.nro_online.models.shop.TabShop;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShopDAO {
    private ShopDAO() {}

    public static List<Shop> getShops(Connection con) throws SQLException {
        List<Shop> shops = new ArrayList<>();
        String sql = "SELECT id, npc_id, shop_order FROM shop ORDER BY npc_id, shop_order";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Shop shop = new Shop(rs);
                loadShopTab(con, shop);
                shops.add(shop);
            }
        } catch (SQLException e) {
            Log.error(ShopDAO.class, e);
        }
        return shops;
    }

    private static void loadShopTab(Connection con, Shop shop) throws SQLException {
        String sql = "SELECT id, name FROM shop_tab WHERE shop_id = ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, shop.id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TabShop tab = new TabShop(rs, shop);
                    loadItemShop(con, tab);
                    shop.tabShops.add(tab);
                }
            }
        } catch (SQLException e) {
            Log.error(ShopDAO.class, e);
        }
    }

    private static void loadItemShop(Connection con, TabShop tabShop) throws SQLException {
        String sql = "SELECT id, temp_id, gold, gem, is_new, item_exchange, quantity_exchange " +
                "FROM item_shop WHERE tab_id = ? ORDER BY create_time DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tabShop.id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemShop itemShop = new ItemShop(rs, tabShop);
                    if (itemShop.itemExchange != -1) {
                        itemShop.iconSpec = ItemService.gI().getTemplate(itemShop.itemExchange).iconID;
                        itemShop.costSpec = rs.getInt("quantity_exchange"); // Dùng alias cho rõ ràng hơn
                    }
                    loadItemShopOption(con, itemShop);
                    tabShop.itemShops.add(itemShop);
                }
            }
        } catch (SQLException e) {
            Log.error(ShopDAO.class, e);
        }
    }

    private static void loadItemShopOption(Connection con, ItemShop itemShop) throws SQLException {
        String sql = "SELECT option_id, param FROM item_shop_option WHERE item_shop_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, itemShop.id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    itemShop.options.add(new ItemOption(rs));
                }
            }
        } catch (SQLException e) {
            Log.error(ShopDAO.class, e);
        }
    }
}