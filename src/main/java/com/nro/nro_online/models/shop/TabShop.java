package com.nro.nro_online.models.shop;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.item.ItemShop;

public class TabShop {

    public Shop shop;
    public int id;
    public String name;
    public List<ItemShop> itemShops;

    public TabShop() {
        this.itemShops = new ArrayList<>();
    }

    public TabShop(TabShop tabShop, int gender) {
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;
        this.itemShops = new ArrayList<>(tabShop.itemShops.size());
        for (ItemShop itemShop : tabShop.itemShops) {
            if (itemShop.temp.gender == gender || itemShop.temp.gender > 2) {
                this.itemShops.add(itemShop);
            }
        }
    }

    public TabShop(TabShop tabShop) {
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;
        this.itemShops = new ArrayList<>(tabShop.itemShops.size());
        for (ItemShop itemShop : tabShop.itemShops) {
            this.itemShops.add(new ItemShop(itemShop));
        }
    }

    public TabShop(ResultSet rs, Shop shop) throws SQLException {
        this.id = rs.getInt(1);
        this.name = rs.getString(2);
        this.shop = shop;
        this.itemShops = new ArrayList<>();
    }
}