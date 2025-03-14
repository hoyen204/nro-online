package com.nro.nro_online.login;

import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class LoginSession {

    @Getter private boolean connected;
    private LoginController controller;
    @Getter private LoginService service;
    private DataOutputStream dos;
    private DataInputStream dis;
    private Socket sc;
    private boolean connecting;
    private final Sender sender = new Sender();
    private Thread collectorThread;
    public int sendByteCount;
    public int recvByteCount;
    private boolean getKeyComplete;
    private byte[] key;
    private byte curR, curW;
    private long timeConnected;
    public String strRecvByteCount = "";
    private boolean isCancel;
    private String host;
    private int port;

    public LoginSession() {
        this.controller = new LoginController(this);
        this.service = new LoginService(this);
    }

    public void connect(String host, int port) {
        if (connected || connecting) {
            Log.log("Đã kết nối rồi, chill đi bro! 😛");
            return;
        }
        this.host = host;
        this.port = port;
        getKeyComplete = false;
        new Thread(new NetworkInit()).start();
    }

    public void reconnect() {
        Log.log("Reconnect đây, chờ tí nha! 🔄");
        connect(host, port);
    }

    private class NetworkInit implements Runnable {
        @Override
        public void run() {
            isCancel = false;
            startTimeoutWatcher();
            connecting = true;
            try {
                doConnect();
                controller.onConnectOK();
            } catch (IOException e) {
                handleConnectionFailure();
            }
        }

        private void startTimeoutWatcher() {
            new Thread(() -> {
                Util.sleep(20000);
                if (connecting && !isCancel) {
                    closeSocket();
                    isCancel = true;
                    connecting = false;
                    connected = false;
                    controller.onConnectionFail();
                    Log.warning("Timeout kết nối, chậm quá trời ơi! ⏳");
                }
            }).start();
        }

        private void doConnect() throws IOException {
            sc = new Socket(host, port);
            dos = new DataOutputStream(sc.getOutputStream());
            dis = new DataInputStream(sc.getInputStream());
            new Thread(sender).start();
            collectorThread = new Thread(new MessageCollector());
            collectorThread.start();
            timeConnected = System.currentTimeMillis();
            sendMessage(new Message(-27));
            connecting = false;
            connected = true;
            Log.log("Kết nối server thành công, ngon lành! 🌟");
        }

        private void handleConnectionFailure() {
            Util.sleep(500);
            if (!isCancel) {
                close();
                controller.onConnectionFail();
                Log.error("Kết nối fail, mạng lag hả bro? 🌩️");
            }
        }

        private void closeSocket() {
            try {
                if (sc != null) sc.close();
            } catch (IOException e) {
                Log.error(LoginSession.class, e, "Đóng socket lỗi, khổ ghê! 😵");
            }
        }
    }

    public void sendMessage(Message message) {
        sender.addMessage(message);
    }

    private void doSendMessage(Message m) throws IOException {
        byte[] data = m.getData();
        dos.writeByte(getKeyComplete ? writeKey(m.command) : m.command);
        if (data != null) {
            int size = data.length;
            writeSize(size, m.command == -31);
            if (getKeyComplete) encryptData(data);
            dos.write(data);
            sendByteCount += (data.length + 3);
        } else {
            dos.writeShort(0);
            sendByteCount += 3;
        }
        dos.flush();
    }

    private void writeSize(int size, boolean isSpecialCmd) throws IOException {
        if (isSpecialCmd) {
            dos.writeShort(size);
        } else if (getKeyComplete) {
            dos.writeByte(writeKey((byte) (size >> 8)));
            dos.writeByte(writeKey((byte) (size & 0xFF)));
        } else {
            dos.writeShort(size);
        }
    }

    private void encryptData(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = writeKey(data[i]);
        }
    }

    private byte readKey(byte b) {
        byte result = (byte) ((key[curR++] & 0xFF) ^ (b & 0xFF));
        curR %= key.length;
        return result;
    }

    private byte writeKey(byte b) {
        byte result = (byte) ((key[curW++] & 0xFF) ^ (b & 0xFF));
        curW %= key.length;
        return result;
    }

    private class Sender implements Runnable {
        private final Vector<Message> sendingMessage = new Vector<>();

        public void addMessage(Message message) {
            sendingMessage.add(message);
        }

        @Override
        public void run() {
            while (connected) {
                try {
                    if (getKeyComplete && !sendingMessage.isEmpty()) {
                        doSendMessage(sendingMessage.remove(0));
                    }
                    Thread.sleep(10);
                } catch (IOException | InterruptedException e) {
                    Log.error(LoginSession.class, e, "Gửi message lỗi, mạng có vấn đề hả? 📡");
                }
            }
        }
    }

    private class MessageCollector implements Runnable {
        @Override
        public void run() {
            try {
                while (connected) {
                    Message message = readMessage();
                    if (message == null) break;
                    processMessage(message);
                }
            } catch (IOException e) {
                Log.error(LoginSession.class, e, "Đọc message lỗi, mạng đứt rồi! 📴");
            }
            handleDisconnect();
        }

        private void processMessage(Message message) {
            try {
                if (message.command == -27) getKey(message);
                else controller.process(message);
            } catch (Exception e) {
                Log.error(LoginSession.class, e, "Xử lý message " + message.command + " lỗi, khổ ghê! 😭");
            }
        }

        private void getKey(Message message) throws IOException {
            byte keySize = message.reader().readByte();
            key = new byte[keySize];
            message.reader().read(key);
            for (int i = 1; i < key.length; i++) {
                key[i] ^= key[i - 1];
            }
            getKeyComplete = true;
            Log.info("Lấy key mã hóa xong, an toàn rồi nha! 🔑");
        }

        private Message readMessage() throws IOException {
            byte cmd = getKeyComplete ? readKey(dis.readByte()) : dis.readByte();
            int size = cmd == -32 ? readExtendedSize() : readNormalSize();
            byte[] data = new byte[size];
            int bytesRead = 0;
            while (bytesRead < size) {
                int len = dis.read(data, bytesRead, size - bytesRead);
                if (len == -1) return null;
                bytesRead += len;
            }
            recvByteCount += (size + (cmd == -32 ? 5 : 3));
            updateRecvStats();
            if (getKeyComplete) decryptData(data);
            return new Message(cmd, data);
        }

        private int readExtendedSize() throws IOException {
            dis.readByte(); // Skip extra cmd byte
            return (readKey(dis.readByte()) & 0xFF) << 24 |
                    (readKey(dis.readByte()) & 0xFF) << 16 |
                    (readKey(dis.readByte()) & 0xFF) << 8 |
                    (readKey(dis.readByte()) & 0xFF);
        }

        private int readNormalSize() throws IOException {
            return getKeyComplete ?
                    (readKey(dis.readByte()) & 0xFF) << 8 | (readKey(dis.readByte()) & 0xFF) :
                    dis.readUnsignedShort();
        }

        private void decryptData(byte[] data) {
            for (int i = 0; i < data.length; i++) {
                data[i] = readKey(data[i]);
            }
        }

        private void updateRecvStats() {
            int kb = (recvByteCount + sendByteCount) / 1024;
            int remainder = (recvByteCount + sendByteCount) % 1024 / 102;
            strRecvByteCount = kb + "." + remainder + "Kb";
        }

        private void handleDisconnect() {
            if (connected) {
                long timeSinceConnected = System.currentTimeMillis() - timeConnected;
                if (timeSinceConnected > 500) controller.onDisconnected();
                else controller.onConnectionFail();
                cleanNetwork();
            }
        }
    }

    public void close() {
        cleanNetwork();
        Log.log("Đóng session, tạm biệt nha! 👋");
    }

    private void cleanNetwork() {
        connected = false;
        connecting = false;
        try {
            if (sc != null) sc.close();
            if (dos != null) dos.close();
            if (dis != null) dis.close();
            sc = null;
            dos = null;
            dis = null;
            key = null;
            curR = curW = 0;
            collectorThread = null;
            Log.log("Dọn mạng xong, sạch bong! 🧹");
        } catch (IOException e) {
            Log.error(LoginSession.class, e, "Đóng mạng lỗi, khổ ghê! 😵");
        }
    }
}