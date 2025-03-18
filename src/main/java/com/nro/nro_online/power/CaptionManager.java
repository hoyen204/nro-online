package com.nro.nro_online.power;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.player.Player;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CaptionManager {
    private static final CaptionManager INSTANCE = new CaptionManager();
    @Getter private final Map<Integer, Caption> captionsById = new ConcurrentHashMap<>();
    @Getter private final NavigableMap<Long, Caption> captionsByPower = new TreeMap<>(); // Để tìm level theo power

    private CaptionManager() {
        load();
    }

    public static CaptionManager getInstance() {
        return INSTANCE;
    }

    private void load() {
        try (Connection con = DBService.gI().getConnectionForGame();
                PreparedStatement ps = con.prepareStatement("SELECT id, earth, saiya, namek, power FROM `caption`");
                ResultSet rs = ps.executeQuery()) {
            captionsById.clear();
            captionsByPower.clear();
            while (rs.next()) {
                Caption caption = Caption.builder()
                        .id(rs.getShort("id"))
                        .earth(rs.getString("earth"))
                        .saiya(rs.getString("saiya"))
                        .namek(rs.getString("namek"))
                        .power(rs.getLong("power"))
                        .build();
                captionsById.put(caption.getId(), caption);
                captionsByPower.put(caption.getPower(), caption);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void add(Caption caption) {
        captionsById.put(caption.getId(), caption);
        captionsByPower.put(caption.getPower(), caption);
    }

    public void remove(Caption caption) {
        captionsById.remove(caption.getId());
        captionsByPower.remove(caption.getPower());
    }

    public Caption find(int id) {
        return captionsById.get(id);
    }

    public int getLevel(Player player) {
        if (player == null) return 0;
        long power = player.nPoint.power;

        Map.Entry<Long, Caption> entry = captionsByPower.floorEntry(power);
        if (entry == null) return 0;

        // Tìm index dựa trên id của caption
        return captionsById.values().stream()
                .filter(c -> c.getId() <= entry.getValue().getId())
                .mapToInt(Caption::getId)
                .max()
                .orElse(0);
    }
}