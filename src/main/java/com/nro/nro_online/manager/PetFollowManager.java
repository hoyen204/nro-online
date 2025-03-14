package com.nro.nro_online.manager;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.player.PetFollow;
import com.nro.nro_online.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PetFollowManager extends AbsManager<PetFollow> {

    private static final PetFollowManager INSTANCE = new PetFollowManager();
    private static final String SELECT_QUERY = "SELECT id_temp, icon, width, height, frame FROM pet_follow";

    public static PetFollowManager gI() {
        return INSTANCE;
    }

    public void load() {
        try (Connection conn = DBService.gI().getConnectionForGame();
                PreparedStatement ps = conn.prepareStatement(SELECT_QUERY);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_temp");
                int iconID = rs.getInt("icon");
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                byte nFrame = rs.getByte("frame");
                add(new PetFollow(id, iconID, width, height, nFrame));
            }
            Log.log("Load " + list.size() + " pet follow xong! 😎");
        } catch (SQLException e) {
            Log.error(PetFollowManager.class, e, "Load pet follow lỗi! 😿");
        }
    }

    @Override
    public void add(PetFollow petFollow) {
        if (petFollow == null || findById(petFollow.getId()) != null) {
            return;
        }
        list.add(petFollow);
    }

    @Override
    public void remove(PetFollow petFollow) {
        if (petFollow != null) {
            list.remove(petFollow);
        }
    }

    @Override
    public PetFollow findById(int id) {
        return list.stream()
                .filter(pet -> pet.getId() == id)
                .findFirst()
                .orElse(null);
    }
}