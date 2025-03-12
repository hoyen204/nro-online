package com.nro.nro_online.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtils {

    public static void writeFile(String fileName, String text) {
        try {
            File folder = new File("log");
            if (!folder.exists()) {
                folder.mkdir();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("log/" + fileName + ".txt"))) {
                bw.write(text);
                bw.flush();
            }
            Log.success("Ghi file xong, ngon lành! 📝");
        } catch (IOException e) {
            Log.error(FileUtils.class, e,"Lỗi ghi file, xui vl: " + e.getMessage() + " 😭");
        }
    }

    public static long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        int count = files != null ? files.length : 0;
        for (int i = 0; i < count; i++) {
            length += files[i].isFile() ? files[i].length() : getFolderSize(files[i]);
        }
        return length;
    }

    public static String cutPng(String str) {
        return str.contains(".png") ? str.replace(".png", "") : str;
    }

    public static void addPath(List<File> list, File file) {
        if(file == null)
            return;

        if (file.isFile()) {
            list.add(file);
        } else {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                addPath(list, f);
            }
        }
    }
}