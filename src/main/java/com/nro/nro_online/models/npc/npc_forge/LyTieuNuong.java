package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.Service;

public class LyTieuNuong extends Npc {

    public LyTieuNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        createOtherMenu(player, ConstNpc.BASE_MENU,
                "Xin chào, tôi có 1 sự kiện đặc biệt bạn có muốn tham gia không?\n" +
                        "Số tiền nạp tích lũy của bạn hiện tại là: [" + player.getSession().poinCharging + "]",
                "1 hộp quà\n[10.000 điểm]\nSkien New",
                "12 hộp quà\n[100.000 điểm]\nSkien New",
                "1 hộp quà\n[10.000 điểm]",
                "12 hộp quà\n[100.000 điểm]");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || this.mapId != 5 || !player.iDMark.isBaseMenu())
            return;

        if (!hasEmptyBagSpace(player)) {
            Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
            return;
        }

        switch (select) {
            case 0 -> exchangeGift(player, 10_000, 397, 1);
            case 1 -> exchangeGiftWithExtras(player, 100_000, 397, 12);
            case 2 -> exchangeGift(player, 10_000, 398, 1);
            case 3 -> exchangeGiftWithExtras(player, 100_000, 398, 12);
        }
    }

    private boolean hasEmptyBagSpace(Player player) {
        return InventoryService.gI().getCountEmptyBag(player) > 0;
    }

    private void exchangeGift(Player player, int poinCost, int itemId, int quantity) {
        if (!checkAndDeductPoin(player, poinCost))
            return;

        Item gift = createGiftItem(itemId, quantity);
        InventoryService.gI().addItemBag(player, gift, 0);
        completeExchange(player);
    }

    private void exchangeGiftWithExtras(Player player, int poinCost, int itemId, int quantity) {
        if (!checkAndDeductPoin(player, poinCost))
            return;

        Item gift = createGiftItem(itemId, quantity);
        addExtraItems(player);
        InventoryService.gI().addItemBag(player, gift, 0);
        completeExchange(player);
    }

    private boolean checkAndDeductPoin(Player player, int poinCost) {
        if (player.getSession().poinCharging < poinCost) {
            Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
            return false;
        }
        if (!PlayerDAO.subPoin(player, poinCost)) {
            npcChat(player, "Lỗi vui lòng báo admin...");
            return false;
        }
        return true;
    }

    private Item createGiftItem(int itemId, int quantity) {
        Item item = ItemService.gI().createNewItem((short) itemId);
        item.itemOptions.add(new ItemOption(74, 0));
        item.itemOptions.add(new ItemOption(30, 0));
        item.quantity = quantity;
        return item;
    }

    private void addExtraItems(Player player) {
        for (int i = 2045; i <= 2051; i++) {
            Item item = ItemService.gI().createNewItem((short) i);
            item.quantity = 3;
            InventoryService.gI().addItemBag(player, item, 0);
        }
        Item dabaove = ItemService.gI().createNewItem((short) 1143);
        dabaove.quantity = 10;
        InventoryService.gI().addItemBag(player, dabaove, 0);
    }

    private void completeExchange(Player player) {
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendThongBao(player, "Success");
    }
}