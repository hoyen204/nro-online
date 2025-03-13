package com.nro.nro_online.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;

public class ServerNotify extends Thread {

    private final List<String> notifies = new ArrayList<>();
    private static ServerNotify i;

    private ServerNotify() {
        this.start();
    }

    public static ServerNotify gI() {
        if (i == null) i = new ServerNotify();
        return i;
    }

    @Override
    public void run() {
        while (!Maintenance.isRuning) {
            try {
                if (!notifies.isEmpty()) sendServerNotification(notifies.removeFirst());
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.error("Server notify interrupted");
            }
        }
    }

    private void sendServerNotification(String text) {
        try (Message msg = new Message(93)) {
            msg.writer().writeUTF(text);
            Service.getInstance().sendMessAllPlayer(msg);
        } catch (IOException e) {
            Log.error(this.getClass(), e);
        }
    }

    public void notify(String text) {
        this.notifies.add(text);
    }
}