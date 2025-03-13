package com.nro.nro_online.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lombok.Getter;
import nro.jdbc.DBService;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.services.ItemService;

/**
 * @author Arriety
 */
public class TopToTask {

    @Getter
    private List<Player> list = new ArrayList<>();
    private static final TopToTask INSTANCE = new TopToTask();

    public static TopToTask getInstance() {
        return INSTANCE;
    }

    public List<Player> load() {
        list.clear();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("SELECT id, name, head, gender, data_point, items_body, CAST(REPLACE(SUBSTRING_INDEX(SUBSTRING_INDEX (data_task, ',', 2), ',',-1),']','') AS UNSIGNED) AS top_nv FROM player ORDER BY top_nv DESC LIMIT 0, 20;");
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                Player player = new Player();

                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");

                // dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                player.nPoint.power = rs.getLong("top_nv");

                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBody.add(item);
                }
                dataArray.clear();
                dataObject.clear();

                list.add(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Log.error(PlayerDAO.class, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
        return list;
    }
}
