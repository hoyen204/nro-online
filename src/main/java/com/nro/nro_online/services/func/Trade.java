package com.nro.nro_online.services.func;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.jdbc.daos.HistoryTransactionDAO;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class Trade {

    private static final int TIME_TRADE = 180000;

    private Player player1;
    private Player player2;

    private long gold1Before;
    private long gold2Before;
    private List<Item> bag1Before;
    private List<Item> bag2Before;

    private List<Item> itemsBag1;
    private List<Item> itemsBag2;

    private List<Item> itemsTrade1;
    private List<Item> itemsTrade2;
    private int goldTrade1;
    private int goldTrade2;

    public byte accept;

    private long lastTimeStart;
    private boolean start;

    public Trade(Player pl1, Player pl2) {
        this.player1 = pl1;
        this.player2 = pl2;
        this.gold1Before = pl1.inventory.gold;
        this.gold2Before = pl2.inventory.gold;
        this.bag1Before = InventoryService.gI().copyItemsBag(player1);
        this.bag2Before = InventoryService.gI().copyItemsBag(player2);
        this.itemsBag1 = InventoryService.gI().copyItemsBag(player1);
        this.itemsBag2 = InventoryService.gI().copyItemsBag(player2);
        this.itemsTrade1 = new ArrayList<>();
        this.itemsTrade2 = new ArrayList<>();
        TransactionService.PLAYER_TRADE.put(pl1, this);
        TransactionService.PLAYER_TRADE.put(pl2, this);
    }

    public void openTabTrade() {
        this.lastTimeStart = System.currentTimeMillis();
        this.start = true;
        try (Message msg1 = new Message(-86)) {
            msg1.writer().writeByte(1);
            msg1.writer().writeInt((int) player1.id);
            player2.sendMessage(msg1);
        } catch (IOException e) {
        }

        try (Message msg2 = new Message(-86)) {
            msg2.writer().writeByte(1);
            msg2.writer().writeInt((int) player2.id);
            player1.sendMessage(msg2);
        } catch (IOException e) {
        }

        Service.getInstance().hideWaitDialog(player1);
        Service.getInstance().hideWaitDialog(player2);
    }

    public void addItemTrade(Player pl, byte index, int quantity) {
//        System.out.println("quantity: " + quantity);
        if (pl.getSession().actived) {
//        if (!pl.isAdmin()) {
//        if (pl.nPoint.power >= 40_000_000_000L) {
            if (index == -1) {
                if (pl.equals(this.player1)) {
                    goldTrade1 = quantity;
                } else {
                    goldTrade2 = quantity;
                }
            } else {
                Item item = null;
                if (pl.equals(this.player1)) {
                    item = itemsBag1.get(index);
                } else {
                    item = itemsBag2.get(index);
                }
                if (quantity > item.quantity || quantity < 0) {
                    return;
                }
                if (isItemCannotTran(item)) {
                    removeItemTrade(pl, index);
                } else {
                    if (quantity > 99) {
                        int n = quantity / 99;
                        int left = quantity % 99;
                        for (int i = 0; i < n; i++) {
                            Item itemTrade = ItemService.gI().copyItem(item);
                            itemTrade.quantity = 99;
                            if (pl.equals(this.player1)) {
                                InventoryService.gI().subQuantityItem(itemsBag1, item, itemTrade.quantity);
                                itemsTrade1.add(itemTrade);
                            } else {
                                InventoryService.gI().subQuantityItem(itemsBag2, item, itemTrade.quantity);
                                itemsTrade2.add(itemTrade);
                            }
                        }
                        if (left > 0) {
                            Item itemTrade = ItemService.gI().copyItem(item);
                            itemTrade.quantity = left;
                            if (pl.equals(this.player1)) {
                                InventoryService.gI().subQuantityItem(itemsBag1, item, itemTrade.quantity);
                                itemsTrade1.add(itemTrade);
                            } else {
                                InventoryService.gI().subQuantityItem(itemsBag2, item, itemTrade.quantity);
                                itemsTrade2.add(itemTrade);
                            }
                        }
                    } else {
                        Item itemTrade = ItemService.gI().copyItem(item);
                        itemTrade.quantity = quantity != 0 ? quantity : 1;
                        if (pl.equals(this.player1)) {
                            InventoryService.gI().subQuantityItem(itemsBag1, item, itemTrade.quantity);
                            itemsTrade1.add(itemTrade);
                        } else {
                            InventoryService.gI().subQuantityItem(itemsBag2, item, itemTrade.quantity);
                            itemsTrade2.add(itemTrade);
                        }
                    }
                }
            }
        } else {
            Service.getInstance().sendThongBaoFromAdmin(pl,
                    "|2|Vui Lòng Kích Hoạt Tài Khoản Để Sửa Dụng Chức Năng!");
            removeItemTrade(pl, index);
        }
    }

    private void removeItemTrade(Player pl, byte index) {
        try (Message msg = new Message(-86)) {
            msg.writer().writeByte(2);
            msg.writer().write(index);
            pl.sendMessage(msg);
            Service.getInstance().sendThongBao(pl, "Không thể giao dịch vật phẩm này");
        } catch (IOException e) {
            // Lỗi thì kệ, player2 chịu trận 😅
        }
    }


    private boolean isItemCannotTran(Item item) {
        if (item.template.id == 2039
                || item.template.id == 2000
                || item.template.id == 2042
                || item.template.id == 457
                || item.template.id == 2045
                || item.template.id == 2046
                || item.template.id == 2047
                || item.template.id == 2048
                || item.template.id == 2049
                || item.template.id == 2050
                || item.template.id == 2051
                || item.template.id == 574
                || item.template.id == 611
                || item.template.id == 1231
                || item.template.id == 2011
                || item.template.id == 2012
                || item.template.id == 1143
                || item.template.id == 1995
                || item.template.id == 2063
                || item.template.id == 2052) {
            return true;
        }
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 30) {
                return true;
            }
        }
        switch (item.template.type) {
            case 27:
                if (item.template.id != 590 && item.template.id == 921) {
                    return true;
                } else {
                    return false;
                }
            case 72:
            case 5: //cải trang
            case 6: //đậu thần
            case 7: //sách skill
            case 8: //vật phẩm nhiệm vụ
            case 11: //flag bag
            case 13: //bùa
            case 22: //vệ tinh
            case 23: //ván bay
            case 24: //ván bay vip
            case 28: //cờ
            case 31: //bánh trung thu, bánh tết
            case 32: //giáp tập luyện
            case 33: // thẻ rada
            case 21:// mini pet
                return true;
            default:
                return false;
        }
    }

    public void cancelTrade() {
        String notifiText = "Giao dịch bị hủy bỏ";
        Service.getInstance().sendThongBao(player1, notifiText);
        Service.getInstance().sendThongBao(player2, notifiText);
        closeTab();
        dispose();
    }

    private void closeTab() {
        try (Message msg = new Message(-86)) {
            msg.writer().writeByte(7);
            player1.sendMessage(msg);
            player2.sendMessage(msg);
        } catch (IOException e) {
        }
    }

    public void dispose() {
        player1.playerTradeId = -1;
        player2.playerTradeId = -1;
        TransactionService.PLAYER_TRADE.put(player1, null);
        TransactionService.PLAYER_TRADE.put(player2, null);
        this.player1 = null;
        this.player2 = null;
        this.itemsBag1 = null;
        this.itemsBag2 = null;
        this.itemsTrade1 = null;
        this.itemsTrade2 = null;
    }

    public void lockTran(Player pl) {
        try (Message msg = new Message(-86)) {
            DataOutputStream ds = msg.writer();
            ds.writeByte(6);
            if (pl.equals(player1)) {
                ds.writeInt(goldTrade1);
                ds.writeByte(itemsTrade1.size());
                for (Item item : itemsTrade1) {
                    ds.writeShort(item.template.id);
                    if (player2.isVersionAbove(222)) {
                        ds.writeInt(item.quantity);
                    } else {
                        ds.writeByte(item.quantity);
                    }
                    List<ItemOption> itemOptions = item.getDisplayOptions();
                    ds.writeByte(itemOptions.size());
                    for (ItemOption io : itemOptions) {
                        ds.writeByte(io.optionTemplate.id);
                        ds.writeShort(io.param);
                    }
                }
                ds.flush();
                player2.sendMessage(msg);
            } else {
                ds.writeInt(goldTrade2);
                ds.writeByte(itemsTrade2.size());
                for (Item item : itemsTrade2) {
                    ds.writeShort(item.template.id);
                    if (player1.isVersionAbove(222)) {
                        ds.writeInt(item.quantity);
                    } else {
                        ds.writeByte(item.quantity);
                    }
                    List<ItemOption> itemOptions = item.getDisplayOptions();
                    ds.writeByte(itemOptions.size());
                    for (ItemOption io : itemOptions) {
                        ds.writeByte(io.optionTemplate.id);
                        ds.writeShort(io.param);
                    }
                }
                ds.flush();
                player1.sendMessage(msg);
            }
        } catch (IOException e) {
            Log.error(Trade.class, e);
        }
    }

    public void acceptTrade() {
        this.accept++;
        if (this.accept == 2) {
            this.startTrade();
        }
    }

    private void startTrade() {
        byte tradeStatus = SUCCESS;
        if (player1.inventory.gold + goldTrade2 > player1.inventory.getGoldLimit()) {
            tradeStatus = FAIL_MAX_GOLD_PLAYER1;
        } else if (player2.inventory.gold + goldTrade1 > player2.inventory.getGoldLimit()) {
            tradeStatus = FAIL_MAX_GOLD_PLAYER2;
        }
        if (tradeStatus != SUCCESS) {
            sendNotifyTrade(tradeStatus);
        } else {
            for (Item item : itemsTrade1) {
                if (!InventoryService.gI().addItemList(itemsBag2, item, 0)) {
                    tradeStatus = FAIL_NOT_ENOUGH_BAG_P1;
                    break;
                }
            }
            if (tradeStatus != SUCCESS) {
                sendNotifyTrade(tradeStatus);
            } else {
                for (Item item : itemsTrade2) {
                    if (!InventoryService.gI().addItemList(itemsBag1, item, 0)) {
                        tradeStatus = FAIL_NOT_ENOUGH_BAG_P2;
                        break;
                    }
                }
                if (tradeStatus == SUCCESS) {
                    player1.inventory.gold += goldTrade2;
                    player2.inventory.gold += goldTrade1;
                    player1.inventory.gold -= goldTrade1;
                    player2.inventory.gold -= goldTrade2;
                    player1.inventory.itemsBag = itemsBag1;
                    player2.inventory.itemsBag = itemsBag2;

                    InventoryService.gI().sendItemBags(player1);
                    InventoryService.gI().sendItemBags(player2);
                    PlayerService.gI().sendInfoHpMpMoney(player1);
                    PlayerService.gI().sendInfoHpMpMoney(player2);

                    HistoryTransactionDAO.insert(player1, player2, goldTrade1, goldTrade2, itemsTrade1, itemsTrade2,
                            bag1Before, bag2Before, this.player1.inventory.itemsBag, this.player2.inventory.itemsBag,
                            gold1Before, gold2Before, this.player1.inventory.gold, this.player2.inventory.gold);
                    PreparedStatement ps = null;
                    ResultSet rs = null;
                    try (Connection con = DBService.gI().getConnectionForSaveHistory();) {
                        PlayerDAO.saveBag(con, player1);
                        PlayerDAO.saveBag(con, player2);
                    } catch (Exception e) {
                    } finally {
                        try {
                            if (rs != null) {
                                rs.close();
                            }
                            if (ps != null) {
                                ps.close();
                            }
                        } catch (SQLException ex) {
                        }
                    }
                }
                sendNotifyTrade(tradeStatus);
            }
        }

    }

    private static final byte SUCCESS = 0;
    private static final byte FAIL_MAX_GOLD_PLAYER1 = 1;
    private static final byte FAIL_MAX_GOLD_PLAYER2 = 2;
    private static final byte FAIL_NOT_ENOUGH_BAG_P1 = 3;
    private static final byte FAIL_NOT_ENOUGH_BAG_P2 = 4;

    private void sendNotifyTrade(byte status) {
        switch (status) {
            case SUCCESS:
                Service.getInstance().sendThongBao(player1, "Giao dịch thành công");
                Service.getInstance().sendThongBao(player2, "Giao dịch thành công");
                break;
            case FAIL_MAX_GOLD_PLAYER1:
                Service.getInstance().sendThongBao(player1, "Giao dịch thất bại do số lượng vàng sau giao dịch vượt tối đa");
                Service.getInstance().sendThongBao(player2, "Giao dịch thất bại do số lượng vàng " + player1.name + " sau giao dịch vượt tối đa");
                break;
            case FAIL_MAX_GOLD_PLAYER2:
                Service.getInstance().sendThongBao(player2, "Giao dịch thất bại do số lượng vàng sau giao dịch vượt tối đa");
                Service.getInstance().sendThongBao(player1, "Giao dịch thất bại do số lượng vàng " + player2.name + " sau giao dịch vượt tối đa");
                break;
            case FAIL_NOT_ENOUGH_BAG_P1:
                Service.getInstance().sendThongBao(player1, "Giao dịch thất bại do không đủ ô trống hành trang");
                Service.getInstance().sendThongBao(player2, "Giao dịch thất bại do " + player1.name + " không đủ chỗ ô hành trang");
                break;
            case FAIL_NOT_ENOUGH_BAG_P2:
                Service.getInstance().sendThongBao(player2, "Giao dịch thất bại do không đủ ô trống hành trang");
                Service.getInstance().sendThongBao(player1, "Giao dịch thất bại do " + player2.name + " không đủ chỗ ô hành trang");
                break;
        }
    }

    public void update() {
        if (this.start && Util.canDoWithTime(lastTimeStart, TIME_TRADE)) {
            this.cancelTrade();
        }
    }
}
