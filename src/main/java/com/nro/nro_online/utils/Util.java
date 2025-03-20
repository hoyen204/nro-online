package com.nro.nro_online.utils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Pet;
import com.nro.nro_online.models.player.Player;

public class Util {
    private static final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
            'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
            'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
            'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
            'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
            'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
            'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
            'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
            'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
            'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
            'ữ', 'Ự', 'ự',};

    private static final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
            'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
            'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
            'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
            'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
            'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
            'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', 'U', 'u',};
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

    public static int getDistance(Pet pet, Mob mob) {
        return (pet == null || pet.location == null || mob == null || mob.location == null) ? Integer.MAX_VALUE :
                getDistance(pet.location.x, pet.location.y, mob.location.x, mob.location.y);
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

    public static boolean containsSubstring(String inputString, String pattern) {
        String cleanedString = inputString.replaceAll("[^a-zA-Z0-9]", "");
        return cleanedString.contains(pattern);
    }

    public static String formatTime(long time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H' giờ 'm' phút'")
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(time));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortHashMapByValue(Map<K, V> hashMap, boolean ascending) {
        return hashMap.entrySet().stream()
                .sorted(ascending ? Map.Entry.comparingByValue() : Map.Entry.<K, V>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortHashMapByValue(Map<K, V> hashMap) {
        return sortHashMapByValue(hashMap, true);
    }

    public static Object getPropertyByName(Object myObject, String propertyName) {
        Object result = null;
        Class<?> myObjectClass = myObject.getClass();
        Field[] fields = myObjectClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (Objects.equals(fieldName, propertyName)) {
                try {
                    field.setAccessible(true);
                    result = field.get(myObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return result;
    }

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static boolean haveSpecialCharacter(String text) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        return b || text.contains(" ");
    }

    public static int[] pickNRandInArr(int[] array, int n) {
        if (n > array.length) {
            throw new IllegalArgumentException(
                    "Đại ca đòi " + n + " mà mảng có " + array.length + " thôi, tham vừa thôi! 😛");
        }
        int[] result = Arrays.copyOf(array, array.length); // Sao chép nhanh, khỏi lằng nhằng
        shuffleArray(result); // Xáo trộn ngay trong mảng
        result = Arrays.copyOfRange(result, 0, n); // Lấy n phần tử đầu
        Arrays.sort(result); // Sort luôn
        return result;
    }

    private static void shuffleArray(int[] arr) {
        Random rand = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
