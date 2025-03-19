package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.Service;

import static com.nro.nro_online.models.npc.NpcFactory.getMenuLamBanh;

public class NoiBanh extends Npc {

    private static final int COST_DOI_BANH = 1_000_000_000;

    public NoiBanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào " + player.name + "\nTôi là nồi nấu bánh\nTôi có thể giúp gì cho bạn",
                    "Làm\nBánh 1 Trứng", "Làm\nBánh 2 Trứng", getMenuLamBanh(player, 0),
                    getMenuLamBanh(player, 1), "Đổi capsule\nTrung thu");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || player.iDMark.getIndexMenu() != ConstNpc.BASE_MENU) return;

        switch (select) {
        case 0 -> makeCake(player, 1, (short) 465, "Bánh trung thu 1 trứng");
        case 1 -> makeCake(player, 2, (short) 466, "Bánh trung thu 2 trứng");
        case 4 -> exchangeCapsule(player);
        }
    }

    private void makeCake(Player player, int eggCount, short itemId, String cakeName) {
        int qty = eggCount * 10;
        Item dauxanh = InventoryService.gI().findItem(player, 889, qty); // Đậu xanh
        Item ga = InventoryService.gI().findItem(player, 887, qty);      // Gà
        Item trung = InventoryService.gI().findItem(player, 886, qty);   // Trứng
        Item botmi = InventoryService.gI().findItem(player, 888, qty);   // Bột mì

        if (dauxanh == null || ga == null || trung == null || botmi == null) {
            Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
            return;
        }

        InventoryService.gI().subQuantityItemsBag(player, dauxanh, qty);
        InventoryService.gI().subQuantityItemsBag(player, ga, qty);
        InventoryService.gI().subQuantityItemsBag(player, trung, qty);
        InventoryService.gI().subQuantityItemsBag(player, botmi, qty);

        Item cake = ItemService.gI().createNewItem(itemId);
        cake.itemOptions.add(new ItemOption(74, 0));
        InventoryService.gI().addItemBag(player, cake, 0);
        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendThongBao(player, "Bạn nhận được " + cakeName);
    }

    private void exchangeCapsule(Player player) {
        Item carot = InventoryService.gI().findItem(player, 462, 99);
        if (carot == null || player.inventory.gold < COST_DOI_BANH) {
            Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu hoặc 1 tỷ vàng để đổi");
            return;
        }

        InventoryService.gI().subQuantityItemsBag(player, carot, 99);
        player.inventory.gold -= COST_DOI_BANH;

        Item capsule = ItemService.gI().createNewItem((short) 737);
        capsule.itemOptions.add(new ItemOption(30, 0));
        capsule.itemOptions.add(new ItemOption(74, 0));
        InventoryService.gI().addItemBag(player, capsule, 0);

        InventoryService.gI().sendItemBags(player);
        Service.getInstance().sendMoney(player);
        Service.getInstance().sendThongBao(player, "Bạn nhận được Capsule Trung Thu");
    }
}