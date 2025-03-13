package com.nro.nro_online.power;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.player.Player;
import lombok.Getter;

public class CaptionManager {

    private static final CaptionManager instance = new CaptionManager();

    public static CaptionManager getInstance() {
        return instance;
    }

    @Getter
    private final List<Caption> captions = new ArrayList<>();

    public void load() {
        try (Connection con = DBService.gI().getConnectionForGame();
                PreparedStatement ps = con.prepareStatement("SELECT id, earth, saiya, namek, power FROM `caption`");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                captions.add(Caption.builder()
                        .id(rs.getShort("id"))
                        .earth(rs.getString("earth"))
                        .saiya(rs.getString("saiya"))
                        .namek(rs.getString("namek"))
                        .power(rs.getLong("power"))
                        .build());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void add(Caption caption) {
        captions.add(caption);
    }

    public void remove(Caption caption) {
        captions.remove(caption);
    }

    public Caption find(int id) {
        return captions.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public int getLevel(Player player) {
        long power = player.nPoint.power;
        for (int i = captions.size() - 1; i >= 0; i--) {
            if (power >= captions.get(i).getPower()) return i;
        }
        return 0;
    }
}