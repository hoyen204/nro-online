package com.nro.nro_online.server.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.nro.nro_online.utils.Log;

public class Message {
    public byte command;
    public boolean isBigMsg;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    private DataInputStream dis;

    public Message() {
        os = new ByteArrayOutputStream();
        dos = new DataOutputStream(os);
    }

    public Message(int command) {
        this((byte) command);
    }

    public Message(byte command) {
        this.command = command;
        os = new ByteArrayOutputStream();
        dos = new DataOutputStream(os);
    }

    public Message(byte command, byte[] data) {
        this.command = command;
        is = new ByteArrayInputStream(data);
        dis = new DataInputStream(is);
    }

    public DataOutputStream writer() {
        return dos;
    }

    public DataInputStream reader() {
        return dis;
    }

    public byte[] getData() {
        return os.toByteArray();
    }

    public void transformData() {
    }

    public void cleanup() {
        try {
            if (dis != null)
                dis.close();
            if (dos != null)
                dos.close();
        } catch (IOException e) {
            Log.error(this.getClass(), e);
        }
    }

    public void dispose() {
        dis = null;
        is = null;
        dos = null;
        os = null;
    }
}