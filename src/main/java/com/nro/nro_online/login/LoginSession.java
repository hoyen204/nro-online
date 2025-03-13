package com.nro.nro_online.login;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import lombok.Getter;

public class LoginSession {

    @Getter
    private boolean connected;
    private LoginController controller;
    @Getter
    private LoginService service;
    public boolean isStopSend = false;
    private DataOutputStream dos;
    public DataInputStream dis;
    public Socket sc;
    public boolean connecting;
    private final Sender sender = new Sender();
    public Thread initThread;
    public Thread collectorThread;
    public int sendByteCount;
    public int recvByteCount;
    boolean getKeyComplete;
    public byte[] key = null;
    private byte curR, curW;
    long timeConnected;
    public String strRecvByteCount = "";
    public boolean isCancel;
    private Vector<Message> sendingMessage;
    private String host;
    private int port;

    public LoginSession() {
        this.controller = new LoginController(this);
        this.service = new LoginService(this);
    }

    public void connect(String host, int port) {
        if (connected || connecting) return;
        getKeyComplete = false;
        sc = null;
        this.host = host;
        this.port = port;
        initThread = new Thread(new NetworkInit());
        initThread.start();
    }

    public void reconnect() {
        Log.log("Kết nối lại!");
        connect(host, port);
    }

    class NetworkInit implements Runnable {
        public void run() {
            isCancel = false;
            new Thread(() -> {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {}
                if (connecting) {
                    try {
                        if (sc != null) sc.close();
                    } catch (IOException e) {}
                    isCancel = true;
                    connecting = false;
                    connected = false;
                    controller.onConnectionFail();
                }
            }).start();
            connecting = true;
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            try {
                doConnect();
                controller.onConnectOK();
            } catch (Exception e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {}
                if (!isCancel && controller != null) {
                    close();
                    controller.onConnectionFail();
                }
            }
        }

        private void doConnect() throws IOException {
            sc = new Socket(host, port);
            dos = new DataOutputStream(sc.getOutputStream());
            dis = new DataInputStream(sc.getInputStream());
            new Thread(sender).start();
            collectorThread = new Thread(new MessageCollector());
            collectorThread.start();
            timeConnected = System.currentTimeMillis();
            doSendMessage(new Message(-27));
            connecting = false;
            connected = true;
        }
    }

    public void sendMessage(Message message) {
        sender.AddMessage(message);
    }

    private synchronized void doSendMessage(Message m) throws IOException {
        byte[] data = m.getData();
        if (getKeyComplete) {
            dos.writeByte(writeKey(m.command));
        } else {
            dos.writeByte(m.command);
        }
        if (data != null) {
            int size = data.length;
            if (m.command == -31) {
                dos.writeShort(size);
            } else if (getKeyComplete) {
                dos.writeByte(writeKey((byte) (size >> 8)));
                dos.writeByte(writeKey((byte) (size & 0xFF)));
            } else {
                dos.writeShort(size);
            }
            if (getKeyComplete) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = writeKey(data[i]);
                }
            }
            dos.write(data);
            sendByteCount += (5 + data.length);
        } else {
            dos.writeShort(0);
            sendByteCount += 5;
        }
        dos.flush();
    }

    private byte readKey(byte b) {
        byte i = (byte) ((key[curR++] & 0xff) ^ (b & 0xff));
        if (curR >= key.length) curR %= key.length;
        return i;
    }

    private byte writeKey(byte b) {
        byte i = (byte) ((key[curW++] & 0xff) ^ (b & 0xff));
        if (curW >= key.length) curW %= key.length;
        return i;
    }

    private class Sender implements Runnable {
        public Sender() {
            sendingMessage = new Vector();
        }

        public void AddMessage(Message message) {
            sendingMessage.addElement(message);
        }

        public void run() {
            while (connected) {
                try {
                    if (getKeyComplete && !sendingMessage.isEmpty()) {
                        Message m = (Message) sendingMessage.removeFirst();
                        doSendMessage(m);
                    }
                    Thread.sleep(10);
                } catch (IOException | InterruptedException e) {
                    Log.error(LoginSession.class, e);
                }
            }
        }
    }

    class MessageCollector implements Runnable {
        public void run() {
            try {
                while (connected) {
                    Message message = readMessage();
                    if (message == null) break;
                    try {
                        if (message.command == -27) getKey(message);
                        else controller.process(message);
                    } catch (Exception e) {
                        Log.error(LoginSession.class, e);
                    }
                }
            } catch (Exception e) {
                Log.error(LoginSession.class, e);
            }
            if (connected && controller != null) {
                if (System.currentTimeMillis() - timeConnected > 500) {
                    controller.onDisconnected();
                } else {
                    controller.onConnectionFail();
                }
                cleanNetwork();
            }
        }

        private void getKey(Message message) throws IOException {
            byte keySize = message.reader().readByte();
            key = new byte[keySize];
            for (int i = 0; i < keySize; i++) {
                key[i] = message.reader().readByte();
            }
            for (int i = 0; i < key.length - 1; i++) {
                key[i + 1] ^= key[i];
            }
            getKeyComplete = true;
        }

        private Message readMessage() throws IOException {
            byte cmd = getKeyComplete ? readKey(dis.readByte()) : dis.readByte();
            int size;
            if (cmd == -32) {
                cmd = getKeyComplete ? readKey(dis.readByte()) : dis.readByte();
                byte b1 = readKey(dis.readByte());
                byte b2 = readKey(dis.readByte());
                byte b3 = readKey(dis.readByte());
                byte b4 = readKey(dis.readByte());
                size = ((b1 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 8) | (b4 & 0xff);
            } else if (getKeyComplete) {
                size = (readKey(dis.readByte()) & 0xff) << 8 | (readKey(dis.readByte()) & 0xff);
            } else {
                size = dis.readUnsignedShort();
            }
            byte[] data = new byte[size];
            int byteRead = 0;
            while (byteRead < size) {
                int len = dis.read(data, byteRead, size - byteRead);
                if (len == -1) return null;
                byteRead += len;
                recvByteCount += (5 + byteRead);
                int kb = (recvByteCount + sendByteCount);
                strRecvByteCount = kb / 1024 + "." + kb % 1024 / 102 + "Kb";
            }
            if (getKeyComplete) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = readKey(data[i]);
                }
            }
            return new Message(cmd, data);
        }
    }

    public void close() {
        cleanNetwork();
    }

    private void cleanNetwork() {
        key = null;
        curR = 0;
        curW = 0;
        connected = false;
        connecting = false;
        try {
            if (sc != null) sc.close();
            if (dos != null) dos.close();
            if (dis != null) dis.close();
            sc = null;
            dos = null;
            dis = null;
            collectorThread = null;
            System.gc();
        } catch (IOException e) {
            Log.error(LoginSession.class, e);
        }
    }
}