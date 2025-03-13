/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.specialnpc;

import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.PetService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class BillEgg {

    private static final long DEFAULT_TIME_DONE = 1209600000;

    private Player player;
    public long lastTimeCreate;
    public long timeDone;

    private final short id = 50;

    public BillEgg(Player player, long lastTimeCreate, long timeDone) {
        this.player = player;
        this.lastTimeCreate = lastTimeCreate;
        this.timeDone = timeDone;
    }

    public static void createBillEgg(Player player) {
        player.billEgg = new BillEgg(player, System.currentTimeMillis(), DEFAULT_TIME_DONE);
    }

    public void sendBillEgg() {
        Message msg;
        try {
            msg = new Message(-122);
            msg.writer().writeShort(this.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(15073);
            msg.writer().writeByte(0);
            msg.writer().writeInt(this.getSecondDone());
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public int getSecondDone() {
        int seconds = (int) ((lastTimeCreate + timeDone - System.currentTimeMillis()) / 1000);
        return seconds > 0 ? seconds : 0;
    }

    public void openEggBill(int gender) {
        if (this.player.pet != null) {
            try {
                destroyEggBill();
                Thread.sleep(4000);
                if (this.player.pet == null) {
                    PetService.gI().createBerusPet(this.player, gender);
                } else {
                    PetService.gI().changeBillPet(this.player, gender);
                }
                ChangeMapService.gI().changeMapInYard(this.player, this.player.gender * 7, -1, Util.nextInt(300, 500));
                player.billEgg = null;
            } catch (Exception e) {
            }
        } else {
            Service.getInstance().sendThongBao(player, "Yêu cầu phải có đệ tử");
        }
    }

    public void destroyEggBill() {
        try {
            Message msg = new Message(-117);
            msg.writer().writeByte(101);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        this.player.billEgg = null;
    }

    public void subTimeDone(int d, int h, int m, int s) {
        this.timeDone -= ((d * 24 * 60 * 60 * 1000) + (h * 60 * 60 * 1000) + (m * 60 * 1000) + (s * 1000));
        this.sendBillEgg();
    }

    public void dispose() {
        this.player = null;
    }
}
