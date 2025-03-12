package com.nro.nro_online.attr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.utils.Log;

public class AttributeTemplateManager {

    private static final AttributeTemplateManager instance = new AttributeTemplateManager();

    public static AttributeTemplateManager gI() {
        return instance;
    }

    private final Map<Integer, AttributeTemplate> templateMap = new HashMap<>();

    public void load() {
        try (PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT id, name FROM `attribute_template`");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                add(new AttributeTemplate(rs));
            }
            Log.success("Load attribute template xong, mượt vl! 🚀");
        } catch (SQLException ex) {
            Log.error(AttributeTemplateManager.class, ex, "Load attribute template fail, đau lòng ghê 😢");
        }
    }

    public void add(AttributeTemplate at) {
        if (at != null) templateMap.put(at.getId(), at);
    }

    public void remove(AttributeTemplate at) {
        if (at != null) templateMap.remove(at.getId());
    }

    public AttributeTemplate find(int id) {
        return templateMap.getOrDefault(id, null);
    }
}