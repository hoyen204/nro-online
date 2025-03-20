package com.nro.nro_online.models.shop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Shop {

    public int id;
    public byte npcId;
    public byte shopOrder;
    public List<TabShop> tabShops;

    public Shop() {
        this.tabShops = new ArrayList<>();
    }

    public Shop(Shop shop, int gender) {
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.shopOrder = shop.shopOrder;
        this.tabShops = new ArrayList<>();
        for (TabShop tabShop : shop.tabShops) {
            this.tabShops.add(new TabShop(tabShop, gender));
        }
    }

    public Shop(Shop shop) {
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.shopOrder = shop.shopOrder;
        this.tabShops = new ArrayList<>(shop.tabShops.size());
        for (TabShop tabShop : shop.tabShops) {
            this.tabShops.add(new TabShop(tabShop));
        }
    }

    public Shop(ResultSet rs) throws SQLException {
        this.id = rs.getInt(1);
        this.npcId = rs.getByte(2);
        this.shopOrder = rs.getByte(3);
        this.tabShops = new ArrayList<>();
    }
}