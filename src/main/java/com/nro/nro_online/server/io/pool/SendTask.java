package com.nro.nro_online.server.io.pool;

import com.nro.nro_online.server.io.Session;
import java.util.Objects;

public class SendTask implements Runnable {
    public static boolean check = false;
    private final Session session;

    public SendTask(Session session) {
        this.session = session; // Null check ngay từ đầu 😤
    }

    @Override
    public void run() {
//        if (!session.messages.isEmpty()) {
//            session.messages.forEach(session::doSendMessage); // Dùng forEach cho gọn, nhanh! 💨
//            session.messages.clear(); // Xóa hết một lần, không lằng nhằng vòng lặp
//            if (check && session.player != null && "girlkun75".equals(session.player.name)) {
//                System.out.println("Time sent to me: " + System.currentTimeMillis());
//            }
//        }
    }
}