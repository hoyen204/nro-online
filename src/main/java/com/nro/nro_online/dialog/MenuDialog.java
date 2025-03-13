package com.nro.nro_online.dialog;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import lombok.Getter;

public class MenuDialog extends ConfirmDialog {

    public String[] menu;

    @Getter
    private MenuRunnable runnable;

    public MenuDialog(String content, String[] menu, MenuRunnable run) {
        super();
        this.content = content;
        this.menu = menu;
        this.runnable = run;
    }

    @Override
    public void show(Player player) {
        player.iDMark.setIndexMenu(ConstNpc.CONFIRM_DIALOG);
        player.setConfirmDialog(this);
        try (Message msg = new Message(32)) {
            msg.writer().writeShort(ConstNpc.CON_MEO);
            msg.writer().writeUTF(content);
            msg.writer().writeByte(menu.length);
            for (String str : menu) {
                msg.writer().writeUTF(str);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(MenuDialog.class, e);
        }
    }

    @Override
    public void run() {
        runnable.run();
    }
}