package com.nro.nro_online.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @build by arriety
 */
@Setter
@Getter
public class GameConfig {

    private boolean isOpenPrisonPlanet;
    private boolean isOpenSuperMarket;
    private String event;
    public GameConfig() {
        load();
    }

    public void load() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config/game.properties"));
            properties.forEach((key, value) -> {
                System.out.println("[Gameconfig]: " + key + " : " + value);
            });
            isOpenPrisonPlanet = Boolean.parseBoolean(properties.getProperty("open.prisonplanet"));
            isOpenSuperMarket = Boolean.parseBoolean(properties.getProperty("open.supermarket"));
            event = String.valueOf(properties.getProperty("event"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
