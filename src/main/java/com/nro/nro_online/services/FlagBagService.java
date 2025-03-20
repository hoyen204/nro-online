package com.nro.nro_online.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.item.FlagBag;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;

public class FlagBagService {

    private List<FlagBag> flagClan = new ArrayList<>();
    private static FlagBagService i;

    public static FlagBagService gI() {
        if (i == null) {
            i = new FlagBagService();
        }
        return i;
    }

    public void sendIconFlagChoose(Player player, int id) {
        FlagBag fb = getFlagBag(id);
        if (fb != null) {
            try (Message msg = new Message(-62)) {
                msg.writer().writeByte(fb.id);
                msg.writer().writeByte(1);
                msg.writer().writeShort(fb.iconId);

                player.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace(); // Log the exception for debugging purposes
            }
        }
    }

    public void sendIconEffectFlag(Player player, int id) {
        if (player == null) {
            System.out.println("Player is null"); // Log if player is null
            return;
        }
        FlagBag fb = getFlagBag(id);
        if (fb != null) {
            try (Message msg = new Message(-63)) {
                msg.writer().writeByte(fb.id);
                msg.writer().writeByte(fb.iconEffect.length);
                for (Short iconId : fb.iconEffect) {
                    msg.writer().writeShort(iconId);
                }
                player.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace(); // Log the exception for debugging purposes
            }
        }
    }

    public void sendListFlagClan(Player pl) {
        List<FlagBag> list = getFlagsForChooseClan();
        try (Message msg = new Message(-46)) {
            msg.writer().writeByte(1); //type
            msg.writer().writeByte(list.size());
            for (FlagBag fb : list) {
                msg.writer().writeByte(fb.id);
                msg.writer().writeUTF(fb.name);
                msg.writer().writeInt(fb.gold);
                msg.writer().writeInt(fb.gem);
            }
            pl.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace(); // Log the exception for debugging purposes
        }
    }

    public FlagBag getFlagBag(int id) {
        for (FlagBag fb : Manager.FLAGS_BAGS) {
            if (fb.id == id) {
                return fb;
            }
        }
        return null;
    }

    public FlagBag getFlagBagByName(String name) {
        for (FlagBag fb : Manager.FLAGS_BAGS) {
            if (fb.name.equals(name)) {
                return fb;
            }
        }
        return null;
    }

    public List<FlagBag> getFlagsForChooseClan() {
        if (flagClan.isEmpty()) {
            int[] flagsId = {0, 8, 7, 6, 5, 4, 3, 2, 1, 18, 17, 16, 15, 14, 13,
                12, 11, 10, 9, 27, 26, 25, 24, 23, 36, 32, 33, 34, 35, 19, 22, 21, 20, 29};
            for (int i = 0; i < flagsId.length; i++) {
                flagClan.add(getFlagBag(flagsId[i]));
            }
        }
        return flagClan;
    }
}
