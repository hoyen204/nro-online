package com.nro.nro_online.jdbc.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.item.ItemShop;
import com.nro.nro_online.models.shop.Shop;
import com.nro.nro_online.models.shop.TabShop;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.utils.Log;

public class ShopDAO {

    public static List<Shop> getShops(Connection con) {
        List<Shop> shops = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("select id, npc_id, shop_order from shop order by npc_id , shop_order"); ResultSet rs = ps.executeQuery();){
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

    private static void loadShopTab(Connection con, Shop shop) {
        try (PreparedStatement ps = con.prepareStatement("select id, name from shop_tab where shop_id = ? order by id"); ){
            ps.setInt(1, shop.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TabShop tab = new TabShop(rs, shop);
                loadItemShop(con, tab);
                shop.tabShops.add(tab);
            }
            rs.close();
        } catch (SQLException e) {
            Log.error(ShopDAO.class, e);
        }
    }

    private static void loadItemShop(Connection con, TabShop tabShop) {
        try {
            PreparedStatement ps = con.prepareStatement("select * from item_shop where is_sell = 1 and tab_id = ? order by create_time desc");
            ps.setInt(1, tabShop.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ItemShop itemShop = new ItemShop();
                itemShop.tabShop = tabShop;
                itemShop.id = rs.getInt(1);
                itemShop.temp = ItemService.gI().getTemplate(rs.getShort(3));
                itemShop.gold = rs.getInt(4);
                itemShop.gem = rs.getInt(5);
                itemShop.isNew = rs.getBoolean(6);
                itemShop.itemExchange = rs.getInt("item_exchange");
                if (itemShop.itemExchange != -1) {
                    itemShop.iconSpec = ItemService.gI().getTemplate(itemShop.itemExchange).iconID;
                    itemShop.costSpec = rs.getInt("quantity_exchange");
                }
                loadItemShopOption(con, itemShop);
                tabShop.itemShops.add(itemShop);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            Log.error(ShopDAO.class, e);
        }
    }

    private static void loadItemShopOption(Connection con, ItemShop itemShop) {
        try {
            PreparedStatement ps = con.prepareStatement("select * from item_shop_option where item_shop_id = ?");
            ps.setInt(1, itemShop.id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                itemShop.options.add(new ItemOption(rs.getInt(2), rs.getInt(3)));
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                java.util.logging.Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            Log.error(ShopDAO.class, e);
        }
    }

}
