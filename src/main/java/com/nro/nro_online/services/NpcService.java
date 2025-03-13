package com.nro.nro_online.services;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.NpcFactory;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;

public class NpcService {

    private static final NpcService i = new NpcService();

    public static NpcService gI() {
        return i;
    }

    public void createMenuRongThieng(Player player, int indexMenu, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, ConstNpc.RONG_THIENG, -1, npcSay, menuSelect);
    }

    public void createOtherMenu(Player player, int tempId, int indexMenu, String npcSay, String... menuSelect) {
        sendMenu(player, tempId, indexMenu, npcSay, -1, menuSelect);
    }

    public void createMenuConMeo(Player player, int indexMenu, int avatar, String npcSay, String... menuSelect) {
        createMenu(player, indexMenu, ConstNpc.CON_MEO, avatar, npcSay, menuSelect);
    }

    public void createMenuConMeo(Player player, int indexMenu, int avatar, String npcSay, String[] menuSelect, Object object) {
        NpcFactory.PLAYERID_OBJECT.put(player.id, object);
        createMenuConMeo(player, indexMenu, avatar, npcSay, menuSelect);
    }

    private void createMenu(Player player, int indexMenu, byte npcTempId, int avatar, String npcSay, String... menuSelect) {
        sendMenu(player, npcTempId, indexMenu, npcSay, avatar, menuSelect);
    }

    private void sendMenu(Player player, int npcTempId, int indexMenu, String npcSay, int avatar, String... menuSelect) {
        if (player == null) return;
        player.iDMark.setIndexMenu(indexMenu);
        try (Message msg = new Message(32)) {
            msg.writer().writeShort(npcTempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) msg.writer().writeUTF(menu);
            if (avatar != -1) msg.writer().writeShort(avatar);
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTutorial(Player player, int npcId, int avatar, String npcSay) {
        sendTutorial(player, npcId, avatar, npcSay);
    }

    public void createTutorial(Player player, int avatar, String npcSay) {
        sendTutorial(player, ConstNpc.CON_MEO, avatar, npcSay);
    }

    private void sendTutorial(Player player, int npcId, int avatar, String npcSay) {
        try (Message msg = new Message(38)) {
            msg.writer().writeShort(npcId);
            msg.writer().writeUTF(npcSay);
            if (avatar != -1) msg.writer().writeShort(avatar);
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAvatar(int npcId) {
        return Manager.NPCS.stream()
                .filter(npc -> npc.tempId == npcId)
                .findFirst()
                .map(npc -> npc.avartar)
                .orElse(1139);
    }
}