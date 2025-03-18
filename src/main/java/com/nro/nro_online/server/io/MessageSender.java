package com.nro.nro_online.server.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static com.nro.nro_online.server.io.Session.KEYS;

import com.nro.nro_online.utils.Log;

public class MessageSender implements Runnable {
    private Session session;
    private ArrayList<Message> sendingMessage;
    DataOutputStream dos;

    public int getNumMessage() {
        return sendingMessage.size();
    }

    public MessageSender(Session session, Socket socket) {
        sendingMessage = new ArrayList<>();
        try {
            this.session = session;
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            Log.error(MessageSender.class, e);
        }
    }

    public void addMessage(Message message) {
        try {
            sendingMessage.add(message);
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        Message message;
        while (session != null && session.connected) {
            try {
                while (!(sendingMessage.isEmpty()) && (message = sendingMessage.removeFirst()) != null) {
                    doSendMessage(message);
                }
            } catch (Exception e) {
            }
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
    }

    public void doSendMessage(Message msg) {
        try {
            byte[] data = msg.getData();
            dos.writeByte(session.connected ? writeKey(msg.command) : msg.command);
            if (data != null) {
                int size = data.length;
                if (msg.command == -32 || msg.command == -66 || msg.command == -74 || msg.command == 11 ||
                        msg.command == -67 || msg.command == -87 || msg.command == 66) {
                    dos.writeByte(writeKey((byte) size) - 128);
                    dos.writeByte(writeKey((byte) (size >> 8)) - 128);
                    dos.writeByte(writeKey((byte) (size >> 16)) - 128);
                } else if (session.connected) {
                    dos.writeByte(writeKey((byte) (size >> 8)));
                    dos.writeByte(writeKey((byte) (size & 255)));
                } else {
                    dos.writeShort(size);
                }
                if (session.connected) {
                    for (int i = 0; i < data.length; i++)
                        data[i] = writeKey(data[i]);
                }
                dos.write(data);
            } else {
                dos.writeShort(0);
            }
            dos.flush();
            msg.close();
        } catch (Exception e) {
        }
    }

    private byte writeKey(byte b) {
        byte i = (byte) ((KEYS[session.curW++] & 255) ^ (b & 255));
        if (session.curW >= KEYS.length)
            session.curW %= KEYS.length;
        return i;
    }

    public void sendSessionKey() {

        try (Message msg = new Message(-27)) {
            msg.writer().writeByte(KEYS.length);
            msg.writer().writeByte(KEYS[0]);
            for (int i = 1; i < KEYS.length; i++) {
                msg.writer().writeByte(KEYS[i] ^ KEYS[i - 1]);
            }
            msg.writer().writeUTF("localhost");
            msg.writer().writeInt(14445);
            msg.writer().writeBoolean(false);
            doSendMessage(msg);
            session.connected = true;
            session.sendThread.start();
        } catch (Exception e) {
        }
    }

    void close() throws IOException {
        if (dos != null)
            dos.close();
        dos = null;
        session = null;
        if (sendingMessage != null)
            sendingMessage.clear();
        sendingMessage = null;
    }
}