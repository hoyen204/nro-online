package com.nro.nro_online.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;

public class Util {
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final Random rand = ThreadLocalRandom.current();

    private Util() {
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static int nextInt(int min, int max) {
        return min + rand.nextInt(max - min + 1);
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (InterruptedException e) {
                Log.error(Util.class, e);
            }
        }).start();
    }

    public static boolean isTrue(int ratio, int typeRatio) {
        int num = Util.nextInt(typeRatio);
        return num < ratio;
    }

    public static boolean isTrue(float ratio, int typeRatio) {
        if (ratio < 1) {
            ratio *= 10;
            typeRatio *= 10;
        }
        int num = Util.nextInt(typeRatio);
        return num < ratio;
    }

    public static String numberToMoney(long power) {
        String[] units = {"", " k", " Tr", " Tỷ"};
        double[] dividers = {1, 1000, 1000000, 1000000000};
        NumberFormat num = NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));
        num.setMaximumFractionDigits(1);
        for (int i = units.length - 1; i >= 0; i--) {
            if (power >= dividers[i]) return num.format(power / dividers[i]) + units[i];
        }
        return num.format(power);
    }

    public static String powerToString(long power) {
        return numberToMoney(power);
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.hypot(x1 - x2, y1 - y2);
    }

    public static int getDistance(Player pl1, Player pl2) {
        if (pl1 == null || pl1.location == null || pl2 == null || pl2.location == null) return Integer.MAX_VALUE;
        return getDistance(pl1.location.x, pl1.location.y, pl2.location.x, pl2.location.y);
    }

    public static int getDistance(Player pl, Npc npc) {
        return (pl == null || pl.location == null || npc == null) ? Integer.MAX_VALUE :
                getDistance(pl.location.x, pl.location.y, npc.cx, npc.cy);
    }

    public static int getDistance(Player pl, Mob mob) {
        return (pl == null || pl.location == null || mob == null || mob.location == null) ? Integer.MAX_VALUE :
                getDistance(pl.location.x, pl.location.y, mob.location.x, mob.location.y);
    }

    public static int getDistance(Mob mob1, Mob mob2) {
        return (mob1 == null || mob1.location == null || mob2 == null || mob2.location == null) ? Integer.MAX_VALUE :
                getDistance(mob1.location.x, mob1.location.y, mob2.location.x, mob2.location.y);
    }

    public static int getDistanceByDir(int x, int x1, int dir) {
        return dir == -1 ? x + x1 : x - x1;
    }

    public static ItemMap ratiItem(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        if (Arrays.asList(555, 557, 559).contains(tempId)) {
            it.options.add(new ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, rand.nextInt(501) + 1000)));
        } else if (Arrays.asList(556, 558, 560).contains(tempId)) {
            it.options.add(new ItemOption(6, highlightsItem(it.itemTemplate.gender == 0, rand.nextInt(10001) + 45000)));
        } else if (Arrays.asList(562, 564, 566).contains(tempId)) {
            it.options.add(new ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, rand.nextInt(1001) + 3500)));
        } else if (Arrays.asList(563, 565, 567).contains(tempId)) {
            it.options.add(new ItemOption(7, highlightsItem(it.itemTemplate.gender == 1, rand.nextInt(10001) + 35000)));
        } else if (tempId == 561) {
            it.options.add(new ItemOption(14, rand.nextInt(4) + 15));
        }

        it.options.add(new ItemOption(21, 15));
        it.options.add(new ItemOption(107, rand.nextInt(7)));
        return it;
    }

    public static int highlightsItem(boolean highlights, int value) {
        double highlightsNumber = 1.1;
        return highlights ? (int) (value * highlightsNumber) : value;
    }

    public static synchronized boolean compareDay(LocalDateTime now, LocalDateTime when) {
        LocalDate date1 = now.toLocalDate();
        LocalDate date2 = when.toLocalDate();
        return !date1.equals(date2) && !date1.isBefore(date2);
    }

    public static String toDateString(LocalDate date) {
        return dateFormat.format(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public static String toDateString(LocalDateTime date) {
        return Util.dateFormat.format(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    public static int getOne(int n1, int n2) {
        return rand.nextInt() % 2 == 0 ? n1 : n2;
    }

    public static void sleep(int delay){
        try{
            Thread.sleep(delay);
        }
        catch(InterruptedException e){}
    }

    public static String formatCurrency(double amount) {
        return new DecimalFormat("#,##0").format(amount).replace(",", ".") + " đ";
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? "Chưa có giờ!" :
                localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " ⏰";
    }
}
