package com.nro.nro_online.card;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nro.nro_online.entities.ItemOptionEntity;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.utils.Log;
import lombok.Getter;

@Getter
public class CardManager {
    private CardManager() {}

    private static final CardManager instance = new CardManager();
    private static final Gson GSON = new Gson();

    public static CardManager gI() {
        return instance;
    }

    private final Map<Integer, CardTemplate> cardTemplates = new HashMap<>();

    public void load() {
        try (PreparedStatement ps = DBService.gI().getConnectionForGame()
                .prepareStatement("SELECT id, item_id, name, info, icon, `rank`, max_amount, type, mob_id, head, body, leg, bag, options, aura FROM `collection_book`");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getShort("id");
                List<ItemOption> options = parseOptions(rs.getString("options"));
                CardTemplate card = CardTemplate.builder()
                        .id(id)
                        .itemID(rs.getShort("item_id"))
                        .name(rs.getString("name"))
                        .info(rs.getString("info"))
                        .maxAmount(rs.getByte("max_amount"))
                        .icon(rs.getShort("icon"))
                        .rank(rs.getByte("rank"))
                        .type(rs.getByte("type"))
                        .mobID(rs.getShort("mob_id"))
                        .head(rs.getShort("head"))
                        .body(rs.getShort("body"))
                        .leg(rs.getShort("leg"))
                        .bag(rs.getShort("bag"))
                        .aura(rs.getShort("aura"))
                        .options(options)
                        .build();
                add(card);
            }
        } catch (Exception ex) {
            Log.error(this.getClass(), ex);
        }
    }

    private List<ItemOption> parseOptions(String json) {
        if (json == null || json.isEmpty()) return new ArrayList<>();
        try {
            Type listType = new TypeToken<List<ItemOptionEntity>>(){}.getType();
            List<ItemOptionEntity> optionList = GSON.fromJson(json, listType);
            List<ItemOption> options = new ArrayList<>();
            for (ItemOptionEntity data : optionList) {
                ItemOption itemOption = new ItemOption(data.getId(), data.getParam());
                itemOption.activeCard = (byte) data.getActiveCard();
                options.add(itemOption);
            }
            return options;
        } catch (Exception e) {
            Log.error(this.getClass(), e, "Parse options fail: " + json);
            return new ArrayList<>();
        }
    }

    public void add(CardTemplate cardTemplate) {
        cardTemplates.put(cardTemplate.getId(), cardTemplate);
    }

    public void remove(CardTemplate cardTemplate) {
        cardTemplates.remove(cardTemplate.getId());
    }

    public CardTemplate find(int id) {
        return cardTemplates.get(id);
    }
}