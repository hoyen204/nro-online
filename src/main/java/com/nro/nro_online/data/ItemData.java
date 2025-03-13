package com.nro.nro_online.data;

import java.util.Arrays;
import java.util.List;

import com.nro.nro_online.models.item.ItemOptionTemplate;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.utils.Log;

public class ItemData {
    public static final List<Integer> foodIds = Arrays.asList(663, 664, 665, 666, 667);

    public static void updateItem(Session session) {
        updateItemOptionTemplate(session);
        updateItemTemplate(session);
        updateItemTemplate(session, Manager.ITEM_TEMPLATES.size());
    }

    private static void updateItemOptionTemplate(Session session) {
        try (Message msg = new Message(-28)) {
            msg.writer().writeByte(8);
            msg.writer().writeByte(DataGame.vsItem);
            msg.writer().writeByte(0); // update option
            msg.writer().writeByte(Manager.ITEM_OPTION_TEMPLATES.size());
            for (ItemOptionTemplate io : Manager.ITEM_OPTION_TEMPLATES) {
                msg.writer().writeUTF(io.name);
                msg.writer().writeByte(io.type);
            }
            session.doSendMessage(msg);
        } catch (Exception e) {
            Log.error(ItemData.class, e);
        }
    }

    private static void updateItemTemplate(Session session) {
        try (Message msg = new Message(-28)) {
            msg.writer().writeByte(8);
            msg.writer().writeByte(DataGame.vsItem);
            msg.writer().writeByte(1); // reload itemtemplate
            msg.writer().writeShort(750);
            for (int i = 0; i < 750 && i < Manager.ITEM_TEMPLATES.size(); i++) {
                writeItemTemplate(msg, Manager.ITEM_TEMPLATES.get(i));
            }
            session.doSendMessage(msg);
        } catch (Exception e) {
            Log.error(ItemData.class, e);
        }
    }

    private static void updateItemTemplate(Session session, int end) {
        try (Message msg = new Message(-28)) {
            msg.writer().writeByte(8);
            msg.writer().writeByte(DataGame.vsItem);
            msg.writer().writeByte(2); // add itemtemplate
            msg.writer().writeShort(750);
            msg.writer().writeShort(end);
            int max = Math.min(end, Manager.ITEM_TEMPLATES.size());
            for (int i = 750; i < max; i++) {
                writeItemTemplate(msg, Manager.ITEM_TEMPLATES.get(i));
            }
            session.doSendMessage(msg);
        } catch (Exception e) {
            Log.error(ItemData.class, e);
        }
    }

    private static void writeItemTemplate(Message msg, ItemTemplate item) throws IOException {
        msg.writer().writeByte(item.type);
        msg.writer().writeByte(item.gender);
        msg.writer().writeUTF(item.name);
        msg.writer().writeUTF(item.description);
        msg.writer().writeByte(item.level);
        msg.writer().writeInt(item.strRequire);
        msg.writer().writeShort(item.iconID);
        msg.writer().writeShort(item.part);
        msg.writer().writeBoolean(item.isUpToUp);
    }
}