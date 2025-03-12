package com.nro.nro_online.models.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.shop.TabShop;
import com.nro.nro_online.services.ItemService;

public class ItemShop {

    public TabShop tabShop;
    public int id;
    public ItemTemplate temp;
    public int gold;
    public int gem;
    public boolean isNew;
    public List<ItemOption> options;
    public int iconSpec;
    public int costSpec;
    public int itemExchange;

    public ItemShop() {
        this.options = new ArrayList<>();
    }

    public ItemShop(ItemShop itemShop) {
        this.tabShop = itemShop.tabShop;
        this.id = itemShop.id;
        this.temp = itemShop.temp;
        this.gold = itemShop.gold;
        this.gem = itemShop.gem;
        this.isNew = itemShop.isNew;
        this.options = new ArrayList<>(itemShop.options.size());
        for (ItemOption io : itemShop.options) {
            this.options.add(new ItemOption(io));
        }
    }

    public ItemShop(ResultSet rs, TabShop tabShop) throws SQLException {
        this.tabShop = tabShop;
        this.id = rs.getInt(1);
        this.temp = ItemService.gI().getTemplate(rs.getShort(2));
        this.gold = rs.getInt(3);
        this.gem = rs.getInt(4);
        this.isNew = rs.getBoolean(5);
        this.itemExchange = rs.getInt(6);
        this.options = new ArrayList<>();
    }

    public byte getLevelSkill() {
        String[] subName = temp.name.split(""); // Tách từng ký tự
        return Byte.parseByte(subName[subName.length - 1]); // Lấy số cuối
    }

    public long getPowerRequire() {
        return SkillUtil.getSkillByLevel(temp.id, getLevelSkill()).powRequire;
    }
}