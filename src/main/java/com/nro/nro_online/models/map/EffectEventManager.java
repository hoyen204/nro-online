package com.nro.nro_online.models.map;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import nro.jdbc.DBService;

/**
 * @build by arriety
 */
public class EffectEventManager {

    private static final EffectEventManager i = new EffectEventManager();

    public static EffectEventManager gI() {
        return i;
    }

    @Getter
    private final List<EffectEventTemplate> list = new ArrayList<>();

    public void load() {
        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT * FROM `map_template`");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int mapID = rs.getInt("id");
                    JSONArray jar = new JSONArray(rs.getString("eff_event"));
                    for (int j = 0; j < jar.length(); j++) {
                        JSONObject jobj = jar.getJSONObject(j);
                        int evID = jobj.getInt("event_id");
                        int effID = jobj.getInt("eff_id");
                        int layer = jobj.getInt("layer");
                        int x = jobj.getInt("x");
                        int y = jobj.getInt("y");
                        int loop = jobj.getInt("loop");
                        int delay = jobj.getInt("delay");

                        EffectEventTemplate ee = EffectEventTemplate.builder()
                                .mapId(mapID)
                                .eventId(evID)
                                .effId(effID)
                                .layer(layer)
                                .x(x)
                                .y(y)
                                .loop(loop)
                                .delay(delay)
                                .build();
                        add(ee);
                    }
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(EffectEventTemplate ee) {
        list.add(ee);
    }
}
