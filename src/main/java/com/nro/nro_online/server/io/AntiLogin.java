package com.nro.nro_online.server.io;

import com.nro.nro_online.utils.Util;

public class AntiLogin {
    private static final byte MAX_WRONG = 5;
    private static final int TIME_ANTI = 900_000; // 15 phút, thêm underscore cho dễ đọc 😜

    private volatile long lastTimeLogin = -1; // Thread-safe cho server multi-user
    private volatile int timeCanLogin;
    private volatile byte wrongLogin; // Byte nhỏ gọn, đủ dùng

    public synchronized boolean canLogin() { // Đồng bộ để tránh race condition
        if (lastTimeLogin != -1 && !Util.canDoWithTime(lastTimeLogin, timeCanLogin)) {
            return false; // Chưa hết thời gian khóa, cút! 😤
        }
        reset(); // Hết khóa thì reset luôn
        return wrongLogin < MAX_WRONG;
    }

    public synchronized void wrong() {
        wrongLogin++;
        if (wrongLogin >= MAX_WRONG) {
            lastTimeLogin = System.currentTimeMillis();
            timeCanLogin = TIME_ANTI;
        }
    }

    public synchronized void reset() {
        wrongLogin = 0;
        lastTimeLogin = -1;
        timeCanLogin = 0;
    }

    public String getNotifyCannotLogin() {
        int remainingSeconds = (int) ((lastTimeLogin + timeCanLogin - System.currentTimeMillis()) / 1000);
        return remainingSeconds > 0
                ? "Sai quá nhiều rồi, chờ " + remainingSeconds + " giây nữa nhé!"
                : "Bạn đã đăng nhập sai quá nhiều, thử lại sau ít phút!";
    }
}