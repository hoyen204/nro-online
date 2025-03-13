package com.nro.nro_online.resources;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nro.nro_online.resources.entity.EffectData;
import com.nro.nro_online.resources.entity.ImageByName;
import com.nro.nro_online.resources.entity.MobData;
import com.nro.nro_online.utils.FileUtils;
import com.nro.nro_online.utils.Log;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbsResources {

    private File folder;
    private HashMap<String, byte[]> datas = new HashMap<>();
    private int[] dataVersion;
    private byte[][] smallVersion;
    private byte[][] backgroundVersion;
    private HashMap<String, ImageByName> imageByNames;
    private List<MobData> mobDatas;
    private List<EffectData> effectDatas;
    private static final Gson GSON = new Gson();

    public void init() {
        initDataVersion();
        initBGSmallVersion();
        initSmallVersion();
        initIBN();
        initMobData();
        initEffectData();
    }

    public void initEffectData() {
        effectDatas = loadJsonFiles(new File(folder, "effect_data"), EffectData.class);
    }

    public void initMobData() {
        mobDatas = loadJsonFiles(new File(folder, "monster_data"), MobData.class);
        mobDatas.forEach(MobData::setData);
    }

    private <T> List<T> loadJsonFiles(File dir, Class<T> type) {
        List<T> result = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) return result;
        for (File file : files) {
            try {
                String json = Files.readString(file.toPath());
                if (!json.isEmpty()) result.add(GSON.fromJson(json, type));
            } catch (IOException e) {
                Log.error(AbsResources.class, e, "Lỗi đọc file: " + file.getPath());
            }
        }
        return result;
    }

    public void initIBN() {
        String json = readString("ibn.json");
        if (!json.isEmpty()) {
            try {
                Type type = new TypeToken<List<ImageByName>>(){}.getType();
                List<ImageByName> ibnList = GSON.fromJson(json, type);
                imageByNames = new HashMap<>();
                for (ImageByName data : ibnList) {
                    imageByNames.put(data.getFilename(), ImageByName.builder()
                            .filename(data.getFilename())
                            .nFrame(data.getNFrame())
                            .build());
                }
            } catch (Exception e) {
                Log.error(AbsResources.class, e, "Lỗi parse ibn.json");
            }
        }
    }

    public void initDataVersion() {
        dataVersion = new int[4];
        File dataFolder = new File(folder, "data");
        for (int i = 0; i < 4; i++) {
            dataVersion[i] = (int) FileUtils.getFolderSize(dataFolder);
        }
    }

    public void initBGSmallVersion() {
        backgroundVersion = new byte[4][];
        for (int i = 0; i < 4; i++) {
            File dir = new File(folder, "image/" + (i + 1) + "/bg/");
            backgroundVersion[i] = initVersionArray(dir);
        }
    }

    public void initSmallVersion() {
        smallVersion = new byte[4][];
        for (int i = 0; i < 4; i++) {
            File dir = new File(folder, "image/" + (i + 1) + "/icon/");
            smallVersion[i] = initVersionArray(dir);
        }
    }

    private byte[] initVersionArray(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return new byte[0];
        int max = 0;
        for (File f : files) {
            int id = Integer.parseInt(FileUtils.cutPng(f.getName()));
            if (id > max) max = id;
        }
        byte[] version = new byte[max + 1];
        for (File f : files) {
            try {
                int id = Integer.parseInt(FileUtils.cutPng(f.getName()));
                version[id] = (byte) (Files.readAllBytes(f.toPath()).length % 127);
            } catch (IOException e) {
                Log.error(AbsResources.class, e, "Lỗi đọc file: " + f.getPath());
            }
        }
        return version;
    }

    protected void setFolder(String folder) {
        this.folder = new File("resources", folder);
    }

    public byte[] readAllBytes(String path) {
        try {
            return Files.readAllBytes(new File(folder, path).toPath());
        } catch (IOException e) {
            Log.error(AbsResources.class, e, "Lỗi đọc file: " + path);
            return new byte[0];
        }
    }

    public List<String> readAllLines(String path) {
        try {
            return Files.readAllLines(new File(folder, path).toPath());
        } catch (IOException e) {
            Log.error(AbsResources.class, e, "Lỗi đọc lines: " + path);
            return List.of();
        }
    }

    public String readString(String path) {
        try {
            return Files.readString(new File(folder, path).toPath());
        } catch (IOException e) {
            Log.error(AbsResources.class, e, "Lỗi đọc string: " + path);
            return "";
        }
    }

    public byte[] getRawIconData(int zoomLevel, int iconID) {
        return readAllBytes("image/" + zoomLevel + "/icon/" + iconID + ".png");
    }

    public byte[] getRawBGData(int zoomLevel, int bg) {
        return readAllBytes("image/" + zoomLevel + "/bg/" + bg + ".png");
    }

    public byte[] getRawIBNData(int zoomLevel, String filename) {
        return readAllBytes("image/" + zoomLevel + "/imgbyname/" + filename + ".png");
    }

    public byte[] getRawMobData(int zoomLevel, int id) {
        return readAllBytes("image/" + zoomLevel + "/monster/" + id + ".png");
    }

    public byte[] getRawEffectData(int zoomLevel, int id) {
        return readAllBytes("image/" + zoomLevel + "/effect/" + id + ".png");
    }

    public void putData(String key, byte[] data) {
        datas.put(key, data);
    }

    public byte[] getData(String key) {
        return datas.get(key);
    }

    public ImageByName getIBN(String key) {
        return imageByNames != null ? imageByNames.get(key) : null;
    }

    public MobData getMobData(int id) {
        return mobDatas == null ? null : mobDatas.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public EffectData getEffectData(int id) {
        return effectDatas == null ? null : effectDatas.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }
}