package com.nro.nro_online.models.npc.specialnpc;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;

public abstract class SpecialEgg {
    protected static final short EGG_ID = 50; // ID NPC chung

    protected Player player;
    public long lastTimeCreate;
    public long timeDone;

    public SpecialEgg(Player player, long lastTimeCreate, long timeDone) {
        this.player = player;
        this.lastTimeCreate = lastTimeCreate;
        this.timeDone = timeDone;
    }

    protected abstract long getDefaultTimeDone(); // Thời gian mặc định khác nhau
    protected abstract short getEggIcon(); // Icon khác nhau
    protected abstract void openEggInternal(int gender); // Logic mở trứng khác nhau
    protected abstract String getEggName(); // Tên trứng để log hoặc thông báo

    public void sendEgg() {
        try (Message msg = new Message(-122)) {
            msg.writer().writeShort(EGG_ID);
            msg.writer().writeByte(1);
            msg.writer().writeShort(getEggIcon());
            msg.writer().writeByte(0);
            msg.writer().writeInt(getSecondDone());
            this.player.sendMessage(msg);
        } catch (Exception e) {
        }
    }

    public int getSecondDone() {
        long timeLeft = lastTimeCreate + timeDone - System.currentTimeMillis();
        return timeLeft > 0 ? (int) (timeLeft / 1000) : 0;
    }

    public void openEgg(int gender) {
        openEggInternal(gender);
        this.player.setEgg(null, getEggName()); // Dọn trứng sau khi mở
    }

    public void destroyEgg() {
        try (Message msg = new Message(-117)) {
            msg.writer().writeByte(101);
            player.sendMessage(msg);
        } catch (Exception e) {
            // Phá trứng lỗi thì cũng kệ 😂
        }
        this.player.setEgg(null, getEggName());
    }

    public void subTimeDone(int d, int h, int m, int s) {
        long subtractTime = (d * 86_400_000L) + (h * 3_600_000L) + (m * 60_000L) + (s * 1000L);
        this.timeDone -= subtractTime;
        this.sendEgg();
    }

    public void setTimeDone(long timeDone) {
        this.timeDone = timeDone;
        this.sendEgg();
    }

    public void dispose() {
        this.player = null; // Dọn dẹp nhẹ nhàng 👋
    }
}