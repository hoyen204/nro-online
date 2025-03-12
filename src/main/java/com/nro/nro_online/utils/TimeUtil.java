package com.nro.nro_online.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Build Arriety
 */
public class TimeUtil {

    public enum TimeUnit {
        SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
    }

    public static long diffDate(LocalDateTime d1, LocalDateTime d2, TimeUnit unit) {
        return switch (unit) {
            case SECOND -> ChronoUnit.SECONDS.between(d1, d2);
            case MINUTE -> ChronoUnit.MINUTES.between(d1, d2);
            case HOUR -> ChronoUnit.HOURS.between(d1, d2);
            case DAY -> ChronoUnit.DAYS.between(d1, d2);
            case WEEK -> ChronoUnit.WEEKS.between(d1, d2);
            case MONTH -> ChronoUnit.MONTHS.between(d1, d2);
            case YEAR -> ChronoUnit.YEARS.between(d1, d2);
        };
    }

    /** Kiểm tra thời gian hiện tại có nằm trong khoảng không */
    public static boolean isTimeNowInRange(String d1, String d2, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime time1 = LocalDateTime.parse(d1, formatter);
        LocalDateTime time2 = LocalDateTime.parse(d2, formatter);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(time1) && now.isBefore(time2);
    }

    /** Lấy ngày hiện tại trong tuần (1-7) */
    public static int getCurrDay() {
        return LocalDate.now().getDayOfWeek().getValue();
    }

    /** Lấy giờ hiện tại */
    public static int getCurrHour() {
        return LocalTime.now().getHour();
    }

    /** Lấy phút hiện tại */
    public static int getCurrMin() {
        return LocalTime.now().getMinute();
    }

    /** Hiển thị thời gian dưới dạng "HH:mm" */
    public static String showTime(int hour, int minute) {
        if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
            return String.format("%02d:%02d", hour, minute);
        }
        return "Giờ hoặc phút không hợp lệ";
    }

    /** Tính thời gian còn lại */
    public static String getTimeLeft(long lastTime, int secondTarget) {
        int secondsLeft = Math.max(secondTarget - (int) ((System.currentTimeMillis() - lastTime) / 1000), 0);
        return secondsLeft > 60 ? (secondsLeft / 60) + " phút" : secondsLeft + " giây";
    }

    /** Tính số phút còn lại (làm tròn lên) */
    public static int getMinLeft(long lastTime, int secondTarget) {
        int secondsLeft = Math.max(secondTarget - (int) ((System.currentTimeMillis() - lastTime) / 1000), 0);
        return (secondsLeft > 0) ? (secondsLeft + 59) / 60 : 0;
    }

    /** Tính số giây còn lại */
    public static int getSecondLeft(long lastTime, int secondTarget) {
        return Math.max(secondTarget - (int) ((System.currentTimeMillis() - lastTime) / 1000), 0);
    }

    /** Chuyển chuỗi thời gian thành LocalDateTime */
    public static LocalDateTime getTime(String time, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, formatter);
    }

    /** Lấy thời gian hiện tại theo định dạng */
    public static String getTimeNow(String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now().format(formatter);
    }

    /** Lấy thời gian trước hiện tại một khoảng giây */
    public static String getTimeBeforeCurrent(int subTimeInSeconds, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.now().minusSeconds(subTimeInSeconds).format(formatter);
    }

    /** Định dạng thời gian */
    public static String formatTime(LocalDateTime time, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return time.format(formatter);
    }

    /** Chuyển thời gian còn lại thành chuỗi mô tả */
    public static String getTimeAgo(int timeRemainS) {
        int minutes = timeRemainS / 60;
        int hours = minutes / 60;
        int days = hours / 24;
        if (days > 0) {
            return days + " ngày " + (hours % 24) + " giờ";
        } else if (hours > 0) {
            return hours + " giờ " + (minutes % 60) + " phút";
        }
        return Math.max(minutes, 1) + " phút";
    }

    /** Chuyển giây thành định dạng thời gian */
    public static String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        int hours = minutes / 60;
        minutes %= 60;
        if (hours >= 24) {
            int days = hours / 24;
            hours %= 24;
            return String.format("%d days %02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static long calculateTimeDifferenceInSeconds(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return ChronoUnit.SECONDS.between(dateTime1, dateTime2);
    }
}