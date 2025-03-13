package com.nro.nro_online.resources.entity;

import java.io.IOException;

import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import lombok.Getter;

@Getter
public class EffectData {

    private int id;
    private byte type;
    private Sprite[] sprites;
    private Frame[][] frames;
    private short[] animations;

    public byte[] getData(int version) {
        try (Message ms = new Message()) {
            ms.writer().writeByte(sprites.length);
            for (Sprite sprite : sprites) {
                ms.writer().writeByte(sprite.getId());
                if (type == 0 || type == 1 || version < 220) {
                    ms.writer().writeByte(sprite.getX());
                    ms.writer().writeByte(sprite.getY());
                } else {
                    ms.writer().writeShort(sprite.getX());
                    ms.writer().writeShort(sprite.getY());
                }
                ms.writer().writeByte(sprite.getW());
                ms.writer().writeByte(sprite.getH());
            }
            ms.writer().writeShort(frames.length);
            for (Frame[] a : frames) {
                ms.writer().writeByte(a.length);
                for (Frame frame : a) {
                    ms.writer().writeShort(frame.getDx());
                    ms.writer().writeShort(frame.getDy());
                    ms.writer().writeByte(frame.getSpriteID());
                }
            }
            ms.writer().writeShort(animations.length);
            for (short a : animations) {
                ms.writer().writeShort(a);
            }
            return ms.getData();
        } catch (IOException e) {
            Log.error(EffectData.class, e);
            return new byte[0];
        }
    }
}
