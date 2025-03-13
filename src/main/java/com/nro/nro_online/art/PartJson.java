/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.art;

/**
 *
 * @author Arriety
 * @param Arriety Debug
 * @return
 */

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PartJson {

    public static final String FILE_PATH = "C:\\Users\\HT\\AppData\\LocalLow\\Team\\Dragonboy231\\NR_part";// tim duong dan part
    public static final String OUTPUT_FILE_PATH = "C:\\Users\\HT\\Desktop\\ConCac\\part.sql";// duong dan save part

    public static void main(String[] args) {
        List<PartInfo> parts = readParts(FILE_PATH);
        String sql = createSql(parts);
        writeSqlToFile(sql, OUTPUT_FILE_PATH);
        System.out.println("Done!");
    }

    /**
     * @param Lệnh Remove Spaces VSCODE: \s+
     * @param shortcuts Find: Ctrl + F
     * @param shortcuts Alt + R
     * @param filePath
     * @return
     */
    private static List<PartInfo> readParts(String filePath) {// read part
        List<PartInfo> parts = new ArrayList<>();

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath)); PrintWriter writer = new PrintWriter("arriety.txt")) {// xuất tinh ra text && save du lieu tren cmd ra folder src
            for (int num = dis.readShort(), i = 0; i < num; ++i) {
                int type = dis.readByte();
                int n = 0;
                switch (type) {
                    case 0:
                        n = 3;
                        break;
                    case 1:
                        n = 17;
                        break;
                    case 2:
                        n = 14;
                        break;
                    case 3:
                        n = 2;
                        break;
                    default:
                        break;
                }

                List<PartInfo> partInfos = new ArrayList<>();
                for (int k = 0; k < n; ++k) {
                    int dx = dis.readShort();
                    int dy = dis.readByte();
                    int icon = dis.readByte();
                    partInfos.add(new PartInfo(dx, dy, icon));
                }

                parts.addAll(partInfos);

                String output = partInfos.toString();
                System.out.println(output);
                writer.println(output);// chạy hàm xuất tinh
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parts;
    }

    private static String createSql(List<PartInfo> parts) {// tao cau truc du lieu
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("USE part;\n\n");
        sqlBuilder.append("DROP TABLE IF EXISTS part;\n");
        sqlBuilder.append("CREATE TABLE part (\n");
        sqlBuilder.append("  id INT NOT NULL,\n");
        sqlBuilder.append("  type INT NOT NULL,\n");
        sqlBuilder.append("  DATA JSON NOT NULL,\n");
        sqlBuilder.append("  PRIMARY KEY (id)\n");
        sqlBuilder.append(");\n\n");

        int i = 0;
        for (PartInfo part : parts) {
            sqlBuilder.append("INSERT INTO part (id, type, DATA) VALUES (");
            sqlBuilder.append(i).append(", "); // using i
            sqlBuilder.append("'").append(part.toString()).append("'");
            sqlBuilder.append(");\n");
            i++; // con cac
        }

        return sqlBuilder.toString();
    }

    private static void writeSqlToFile(String sql, String filePath) {// xuat file sql
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"))) {
            writer.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class PartInfo {// info

        private int dx;
        private int dy;
        private int icon;

        public PartInfo(int dx, int dy, int icon) {
            this.dx = dx;
            this.dy = dy;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return "{\"dx\":" + icon + ",\"dy\":" + dy + ",\"icon\":" + dx + "}";
        }
    }
}
