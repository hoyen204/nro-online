package com.nro.nro_online.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIO {
    private FileIO(){}

    public static byte[] readFile(String url) {
        try (FileInputStream fis = new FileInputStream(url);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            Log.error(FileIO.class, e, "Lỗi đọc file: " + url);
            return new byte[0];
        }
    }

    public static ByteArrayOutputStream loadFile(String url) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(url)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream;
        } catch (IOException e) {
            Log.error(FileIO.class, e, "Lỗi load file: " + url);
            return byteArrayOutputStream;
        }
    }

    public static void writeFile(String url, byte[] data) {
        File f = new File(url);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(data);
        } catch (IOException e) {
            Log.error(FileIO.class, e, "Lỗi ghi file: " + url);
        }
    }
}