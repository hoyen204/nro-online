package com.nro.nro_online.power;

import com.nro.nro_online.jdbc.DBService;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages power limits with efficient lookup by ID and power-based ordering.
 * @author Arriety
 */
public class PowerLimitManager {
    private static final PowerLimitManager INSTANCE = new PowerLimitManager();

    public static PowerLimitManager getInstance() {
        return INSTANCE;
    }

    @Getter private final Map<Integer, PowerLimit> powersById = new ConcurrentHashMap<>();
    private final TreeMap<Long, PowerLimit> powersByPower = new TreeMap<>();

    private PowerLimitManager() {
        load();
    }

    public void load() {
        try (PreparedStatement ps = DBService.gI().getConnectionForGame()
                .prepareStatement("SELECT id, power, hp, mp, damage, defense, critical FROM `power_limit`");
                ResultSet rs = ps.executeQuery()) {
            powersById.clear();
            powersByPower.clear();
            while (rs.next()) {
                PowerLimit powerLimit = PowerLimit.builder()
                        .id(rs.getShort("id"))
                        .power(rs.getLong("power"))
                        .hp(rs.getInt("hp"))
                        .mp(rs.getInt("mp"))
                        .damage(rs.getInt("damage"))
                        .defense(rs.getInt("defense"))
                        .critical(rs.getInt("critical"))
                        .build();
                powersById.put(powerLimit.getId(), powerLimit);
                powersByPower.put(powerLimit.getPower(), powerLimit);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void add(PowerLimit powerLimit) {
        powersById.put(powerLimit.getId(), powerLimit);
        powersByPower.put(powerLimit.getPower(), powerLimit);
    }

    public void remove(PowerLimit powerLimit) {
        powersById.remove(powerLimit.getId());
        powersByPower.remove(powerLimit.getPower());
    }

    public PowerLimit get(int id) {
        return powersById.get(id);
    }
}