/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.specialnpc;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class EggLinhThu {

    private static final long DEFAULT_TIME_DONE = 1209600000;

    private Player player;
    public long lastTimeCreate;
    public long timeDone;

    private final short id = 50; //id npc

    public EggLinhThu(Player player, long lastTimeCreate, long timeDone) {
        this.player = player;
        this.lastTimeCreate = lastTimeCreate;
        this.timeDone = timeDone;

    }

    public static void createEggLinhThu(Player player) {
        player.egglinhthu = new EggLinhThu(player, System.currentTimeMillis(), DEFAULT_TIME_DONE);
    }

    public void sendEggLinhThu() {
        Message msg;
        try {
            msg = new Message(-122);
            msg.writer().writeShort(this.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(15074);
            msg.writer().writeByte(0);
            msg.writer().writeInt(this.getSecondDone());
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSecondDone() {
        int seconds = (int) ((lastTimeCreate + timeDone - System.currentTimeMillis()) / 1000);
        return seconds > 0 ? seconds : 0;
    }

    public void openEgg() {
        if (InventoryService.gI().getCountEmptyBag(this.player) > 0) {
            try {
                destroyEgg();
                Thread.sleep(4000);
                int[] list_linh_thu = new int[]{2014, 2015, 2016, 2017, 2018};
                Item linhThu = ItemService.gI().createNewItem((short) list_linh_thu[Util.nextInt(list_linh_thu.length)]);
                laychiso(player, linhThu, 0);
                InventoryService.gI().addItemBag(player, linhThu, 0);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Chúc mừng bạn nhận được Linh thú " + linhThu.template.name);
                ChangeMapService.gI().changeMapInYard(this.player, this.player.gender * 7, -1, Util.nextInt(300, 500));
                player.egglinhthu = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Service.getInstance().sendThongBao(player, "Hành trang không đủ chỗ trống");
        }
    }

    public void destroyEgg() {
        try {
            Message msg = new Message(-117);
            msg.writer().writeByte(101);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.player.egglinhthu = null;
    }

    public void subTimeDone(int d, int h, int m, int s) {
        this.timeDone -= ((d * 24 * 60 * 60 * 1000) + (h * 60 * 60 * 1000) + (m * 60 * 1000) + (s * 1000));
        this.sendEggLinhThu();
    }

    public void dispose() {
        this.player = null;
    }

    private void laychiso(Player player, Item linhthu, int lvnow) {
        switch (linhthu.template.id) {
            case 2014:// hoa
                linhthu.itemOptions.add(new ItemOption(50, Util.nextInt(7, 21)));
                linhthu.itemOptions.add(new ItemOption(168, 0));
                break;
            case 2015:
                linhthu.itemOptions.add(new ItemOption(94, Util.nextInt(7, 21)));
                linhthu.itemOptions.add(new ItemOption(192, 0));
                break;
            case 2016:
                linhthu.itemOptions.add(new ItemOption(77, Util.nextInt(7, 21)));
                linhthu.itemOptions.add(new ItemOption(80, Util.nextInt(21, 30)));
                break;
            case 2017:
                linhthu.itemOptions.add(new ItemOption(108, Util.nextInt(7, 21)));
                linhthu.itemOptions.add(new ItemOption(111, 0));
                break;
            case 2018:
                linhthu.itemOptions.add(new ItemOption(103, Util.nextInt(7, 21)));
                linhthu.itemOptions.add(new ItemOption(173, Util.nextInt(21, 30)));
                break;
        }
        InventoryService.gI().sendItemBags(player);
    }
}
