/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.map;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.consts.ConstMap;

/**
 *
 * @author Kitak
 */
public class MapTemplate {

    public int id;
    public String name;

    public byte type;
    public byte planetId;
    public byte bgType;
    public byte tileId;
    public byte bgId;

    public byte zones;
    public byte maxPlayerPerZone;
    public List<WayPoint> wayPoints;

    public byte[] mobTemp;
    public byte[] mobLevel;
    public int[] mobHp;
    public short[] mobX;
    public short[] mobY;

    public byte[] npcId;
    public short[] npcX;
    public short[] npcY;
    public short[] npcAvatar;
    public List<EffectMap> effectMaps;

    public MapTemplate() {
        this.wayPoints = new ArrayList<>();
        this.effectMaps = new ArrayList<>();
    }

    public boolean isMapOffline() {
        return this.type == ConstMap.MAP_OFFLINE;
    }
}
