package com.nro.nro_online.dialog;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConfirmDialog {

    protected String content;
    private Runnable run;
    private Runnable cancel;

    public ConfirmDialog(String content, Runnable run) {
        this.content = content;
        this.run = run;
    }

    public ConfirmDialog(String content, Runnable run, Runnable cancel) {
        this.content = content;
        this.run = run;
        this.cancel = cancel;
    }

    public void show(Player player) {
        player.iDMark.setIndexMenu(ConstNpc.CONFIRM_DIALOG);
        player.setConfirmDialog(this);
        try (Message msg = new Message(32)) {
            msg.writer().writeShort(ConstNpc.CON_MEO);
            msg.writer().writeUTF(content);
            msg.writer().writeByte(2);
            msg.writer().writeUTF("Đồng ý");
            msg.writer().writeUTF("Từ chối");
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(ConfirmDialog.class, e);
        }
    }

    public void run() {
        run.run();
    }

    public void cancel() {
        if (cancel != null) cancel.run();
    }
}