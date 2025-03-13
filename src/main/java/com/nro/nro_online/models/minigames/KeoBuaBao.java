/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.minigames;

import nro.consts.ConstMiniGame;
import nro.consts.ConstNpc;
import nro.models.player.Player;
import nro.server.Client;
import nro.services.InventoryService;
import nro.services.NpcService;
import nro.services.Service;
import nro.utils.Util;

/**
 *
 * @author Administrator
 */
public class KeoBuaBao {

    public Player player1;
    public Player player2;
    public long lastTimeStart;
    public boolean isFinish;
    public long goldKeoBuaBao;

    public KeoBuaBao(Player pl, Player pl2) {
        player1 = pl;
        player2 = pl2;
        lastTimeStart = System.currentTimeMillis();
        isFinish = false;
    }

    public void start(long g) {
        goldKeoBuaBao = g;
        KeoBuaBaoManager.gI().add(this);
        NpcService.gI().createMenuConMeo(player1,
                ConstNpc.KEO_BUA_BAO, -1,
                String.format("|1|Mức vàng cược %s\n|1|Hãy chọn Kéo, Búa hoặc Bao", Util.powerToString(goldKeoBuaBao)),
                "Kéo", "Búa", "Bao");
        NpcService.gI().createMenuConMeo(player2,
                ConstNpc.KEO_BUA_BAO, -1,
                String.format("|1|Mức vàng cược %s\n|1|Hãy chọn Kéo, Búa hoặc Bao", Util.powerToString(goldKeoBuaBao)),
                "Kéo", "Búa", "Bao");
    }

    public void update() {
        try {
            if (Client.gI().getPlayer(player1.id) == null && Client.gI().getPlayer(player2.id) == null) {
                dispose();
                return;
            }
            if (!isFinish) {
                if (Client.gI().getPlayer(player1.id) == null) {
                    sendResult((byte) 4);
                } else if (Client.gI().getPlayer(player2.id) == null) {
                    sendResult((byte) 5);
                } else if (Util.canDoWithTime(lastTimeStart, ConstMiniGame.TIME_DONE_KEO_BUA_BAO)) {
                    if (player1.iDMark.KeoBuaBao == -1 && player2.iDMark.KeoBuaBao == -1) {
                        sendResult((byte) 0);
                    } else if (player1.iDMark.KeoBuaBao == -1) {
                        sendResult((byte) 1);
                    } else if (player2.iDMark.KeoBuaBao == -1) {
                        sendResult((byte) 2);
                    } else {
                        sendResult((byte) 3);
                    }
                } else if (player1.iDMark.KeoBuaBao != -1 && player2.iDMark.KeoBuaBao != -1) {
                    sendResult((byte) 3);
                }
            }
        } catch (Exception e) {
        }
    }

    public void sendResult(byte type) {
        if (isFinish) {
            return;
        }
        switch (type) {
            case 0:
                isFinish = true;
                NpcService.gI().createMenuConMeo(player1, ConstNpc.IGNORE_MENU, -1,
                        "Trò chơi bị hủy!", "Ok");
                NpcService.gI().createMenuConMeo(player2, ConstNpc.IGNORE_MENU, -1,
                        "Trò chơi bị hủy!", "Ok");
                dispose();
                break;
            case 1:
                player1.iDMark.KeoBuaBao = (byte) Util.nextInt(0, 2);
                break;
            case 2:
                player2.iDMark.KeoBuaBao = (byte) Util.nextInt(0, 2);
                break;
            case 3:
                isFinish = true;
                byte resultPlayer1 = ConstMiniGame.TABLE_RESULT[player1.iDMark.KeoBuaBao][player2.iDMark.KeoBuaBao];
                byte resultPlayer2 = ConstMiniGame.TABLE_RESULT[player2.iDMark.KeoBuaBao][player1.iDMark.KeoBuaBao];
                Player plWin = resultPlayer1 < resultPlayer2 ? player2 : player1;
                Player plLose = player1.equals(plWin) ? player2 : player1;
                NpcService.gI().createMenuConMeo(player1, ConstNpc.IGNORE_MENU, -1,
                        String.format("|2|%s ra cái %s \n|2|Bạn ra cái %s|2|Kết quả %s", player2.name, getString(player2.iDMark.KeoBuaBao), getString(player1.iDMark.KeoBuaBao), resultPlayer1 == resultPlayer2 ? "Hòa" : resultPlayer1 < resultPlayer2 ? "Thua" : "Thắng"), "Ok");
                NpcService.gI().createMenuConMeo(player2, ConstNpc.IGNORE_MENU, -1,
                        String.format("|2|%s ra cái %s \n|2|Bạn ra cái %s|2|Kết quả %s", player1.name, getString(player1.iDMark.KeoBuaBao), getString(player2.iDMark.KeoBuaBao), resultPlayer1 == resultPlayer2 ? "Hòa" : resultPlayer1 < resultPlayer2 ? "Thắng" : "Thua"), "Ok");
                Service.getInstance().sendMoney(plWin);
                Service.getInstance().sendMoney(plLose);
                dispose();
                break;
            case 4:
                isFinish = true;
                NpcService.gI().createMenuConMeo(player2, ConstNpc.IGNORE_MENU, -1,
                        "Trò chơi bị hủy!", "Ok");
                dispose();
                break;
            case 5:
                isFinish = true;
                NpcService.gI().createMenuConMeo(player1, ConstNpc.IGNORE_MENU, -1,
                        "Trò chơi bị hủy!", "Ok");
                dispose();
                break;
        }
    }

    public void SwitchReward(Player player1, Player player2) {

        InventoryService.gI().sendItemBody(player1);
        InventoryService.gI().sendItemBags(player2);
    }

    public String getString(byte type) {
        switch (type) {
            case 0:
                return "Kéo";
            case 1:
                return "Búa";
            default:
                return "Bao";
        }
    }

    private void dispose() {
        if (player1 != null) {
            player1.iDMark.KeoBuaBao = -1;
        }
        if (player2 != null) {
            player2.iDMark.KeoBuaBao = -1;
        }
        player1 = null;
        player2 = null;
        isFinish = false;
        goldKeoBuaBao = 0;
        KeoBuaBaoManager.gI().remove(this);
    }
}
