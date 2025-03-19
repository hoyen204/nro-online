package com.nro.nro_online.services.func;

import com.nro.nro_online.card.Card;
import com.nro.nro_online.card.CollectionBook;
import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RadaService {

    private static final Logger LOGGER = Logger.getLogger(RadaService.class.getName());
    private static final RadaService INSTANCE = new RadaService();

    private RadaService() {
    }

    public static RadaService getInstance() {
        return INSTANCE;
    }

    public void controller(Player player, Message msg) {
        try (var reader = msg.reader()) {
            byte type = reader.readByte();
            int id = reader.available() > 0 ? reader.readShort() : -1;
            switch (type) {
                case 0 -> viewCollectionBook(player);
                case 1 -> cardAction(player, id);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing controller message", e);
        }
    }

    public void cardAction(Player player, int id) {
        CollectionBook book = player.getCollectionBook();
        Card card = book.findWithItemID(id);
        if (card == null || card.getLevel() <= 0)
            return;

        if (!card.isUse() && book.getCards().values().stream().filter(Card::isUse).count() >= 3)
            return;

        byte auraOld = player.getAura();
        card.setUse(!card.isUse());
        byte auraNew = player.getAura();

        useCard(player, card);
        player.nPoint.calPoint();
        Service.getInstance().point(player);
        if (auraOld != auraNew)
            setIDAuraEff(player, auraNew);
    }

    public void useCard(Player player, Card card) {
        sendMessage(player, Cmd.RADA_CARD, ds -> {
            ds.writeByte(1);
            ds.writeShort(card.getCardTemplate().getItemID());
            ds.writeBoolean(card.isUse());
        });
    }

    public void viewCollectionBook(Player player) {
        CollectionBook book = player.getCollectionBook();
        sendMessage(player, Cmd.RADA_CARD, ds -> {
            ds.writeByte(0);
            var cards = book.getCards().values();
            ds.writeShort(cards.size());
            for (Card card : cards) {
                var cardT = card.getCardTemplate();
                ds.writeShort(cardT.getItemID());
                ds.writeShort(cardT.getIcon());
                ds.writeByte(cardT.getRank());
                ds.writeByte(card.getAmount());
                ds.writeByte(cardT.getMaxAmount());
                ds.writeByte(cardT.getType());
                if (cardT.getType() == 0) {
                    ds.writeShort(cardT.getMobID());
                } else {
                    ds.writeShort(cardT.getHead());
                    ds.writeShort(cardT.getBody());
                    ds.writeShort(cardT.getLeg());
                    ds.writeShort(cardT.getBag());
                }
                ds.writeUTF(cardT.getName());
                ds.writeUTF(cardT.getInfo());
                ds.writeByte(card.getLevel());
                ds.writeBoolean(card.isUse());
                var options = cardT.getOptions();
                ds.writeByte(options.size());
                for (ItemOption option : options) {
                    ds.writeByte(option.optionTemplate.id);
                    ds.writeShort(option.param);
                    ds.writeByte(option.activeCard);
                }
            }
        });
    }

    public void useItemCard(Player player, Item item) {
        CollectionBook book = player.getCollectionBook();
        Card card = book.findWithItemID(item.template.id);
        if (card == null)
            return;

        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        int oldLevel = card.getLevel();
        card.addAmount(1);
        int newLevel = card.getLevel();

        if (oldLevel != newLevel && card.isUse()) {
            player.nPoint.calPoint();
            Service.getInstance().point(player);
        }
        setCardLevel(player, card);
    }

    public void setCardLevel(Player player, Card card) {
        sendMessage(player, Cmd.RADA_CARD, ds -> {
            ds.writeByte(2);
            ds.writeShort(card.getCardTemplate().getItemID());
            ds.writeByte(card.getLevel());
        });
    }

    public void setIDAuraEff(Player player, byte aura) {
        sendMessageToAllInMap(player, Cmd.RADA_CARD, ds -> {
            ds.writeByte(4);
            ds.writeInt((int) player.id);
            ds.writeShort(aura);
        });
    }

    private void sendMessage(Player player, int cmd, MessageWriter writer) {
        try (Message msg = new Message(cmd)) {
            writer.write(msg.writer());
            msg.writer().flush();
            player.sendMessage(msg);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message to player", e);
        }
    }

    private void sendMessageToAllInMap(Player player, int cmd, MessageWriter writer) {
        try (Message msg = new Message(cmd)) {
            writer.write(msg.writer());
            msg.writer().flush();
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message to all players in map", e);
        }
    }

    @FunctionalInterface
    private interface MessageWriter {
        void write(DataOutputStream ds) throws IOException;
    }
}