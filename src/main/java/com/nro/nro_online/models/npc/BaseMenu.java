package com.nro.nro_online.models.npc;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;

public class BaseMenu {

    public int npcId;
    public String npcSay;
    public String[] menuSelect;
    private final Message message = new Message(32);

    public void openMenu(Player player) {
        try {
            message.writer().writeShort(npcId);
            message.writer().writeUTF(npcSay);
            message.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                message.writer().writeUTF(menu);
            }
            player.sendMessage(message);
        } catch (Exception e) {
            Log.error(this.getClass(), e);
        }
    }
}
