package com.nro.nro_online.models.item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.utils.Util;

public class ItemOption {

    private static final Map<String, String> optionStringMap = new HashMap<>();

    public int param;

    public ItemOptionTemplate optionTemplate;
    public byte activeCard;

    public ItemOption() {
    }

    public ItemOption(ItemOption io) {
        this.param = io.param;
        this.optionTemplate = io.optionTemplate;
    }

    public ItemOption(int tempId, int param) {
        this.optionTemplate = ItemService.gI().getItemOptionTemplate(tempId);
        this.param = param;
    }

    public ItemOption(ResultSet rs) throws SQLException {
        this.optionTemplate = ItemService.gI().getItemOptionTemplate(rs.getInt(2));
        this.param = rs.getInt(3);
    }

    public String getOptionString() {
        String key = this.optionTemplate.name + "#" + this.param + "#";
        return optionStringMap.computeIfAbsent(key, k -> Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param)));
    }

    public ItemOption format() {
        int id = optionTemplate.id;
        if (this.param > Short.MAX_VALUE) {
            boolean changed = switch (id) {
                case 6 -> {
                    id = 22;
                    param /= 1000;
                    yield true;
                }
                case 7 -> {
                    id = 23;
                    param /= 1000;
                    yield true;
                }
                case 31 -> {
                    id = 171;
                    param /= 1000;
                    yield true;
                }
                case 48 -> {
                    id = 2;
                    param /= 1000;
                    yield true;
                }
                default -> false;
            };
            if (changed) {
                return new ItemOption(id, param);
            }
        }
        return this;
    }

    public void dispose() {
        this.optionTemplate = null;
    }
}
