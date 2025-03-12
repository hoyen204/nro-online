package com.nro.nro_online.server.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import com.nro.nro_online.utils.Log;
import nro.consts.Cmd;
import nro.server.Client;

public class MessageCollector implements Runnable {
    private DataInputStream dis;
    private Session session;

    public MessageCollector(Session session, Socket socket) {
        try {
            this.session = session;
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            Log.error(this.getClass(), e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = readMessage();
                session.lastTimeReadMessage = System.currentTimeMillis();
                if (!session.connected && msg.command != Cmd.GET_SESSION_ID) break;
                session.controller.onMessage(session, msg);
                msg.cleanup();
            }
        } catch (Exception ex) {}
        Client.gI().kickSession(session);
    }

    private Message readMessage() throws Exception {
        long st = System.currentTimeMillis();
        byte cmd = dis.readByte();
        if (session.connected) cmd = readKey(cmd);
        int size = session.connected
                ? (readKey(dis.readByte()) & 255) << 8 | readKey(dis.readByte()) & 255
                : dis.readUnsignedShort();
        if (size > 1024) throw new IOException("Data too big");
        byte[] data = new byte[size];
        int byteRead = 0;
        int len;
        while (byteRead < size) {
            len = dis.read(data, byteRead, size - byteRead);
            if (len == -1) break;
            byteRead += len;
        }
        if (session.connected) {
            for (int i = 0; i < data.length; i++) data[i] = readKey(data[i]);
        }
        if (session.logCheck) Log.success("Time read message: " + (System.currentTimeMillis() - st) + " ms");
        return new Message(cmd, data);
    }

    private byte readKey(byte b) {
        byte i = (byte) ((Session.KEYS[session.curR++] & 255) ^ (b & 255));
        if (session.curR >= Session.KEYS.length) session.curR %= (byte) Session.KEYS.length;
        return i;
    }

    void close() throws IOException {
        if (dis != null) dis.close();
        dis = null;
        session = null;
    }
}