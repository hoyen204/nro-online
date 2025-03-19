package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.Service;

/**
 *
 * @author Arriety
 */
public class GokuSSJ extends Npc {

    public GokuSSJ(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player) && this.mapId == 133) {
            int biKiepQuantity = getBiKiepQuantity(player);
            String menuText = "Bạn đang có " + biKiepQuantity + " bí kiếp.\n"
                    + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart";

            if (biKiepQuantity >= 10000) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, menuText, "Học dịch\nchuyển", "Đóng");
            } else {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, menuText, "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player) && this.mapId == 133) {
            int biKiepQuantity = getBiKiepQuantity(player);
            if (biKiepQuantity >= 10000 && InventoryService.gI().getCountEmptyBag(player) > 0) {
                Item yardart = createYardartItem(player);
                InventoryService.gI().addItemBag(player, yardart, 0);
                InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItem(player.inventory.itemsBag, 590), 10000);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được trang phục tộc Yardart");
            }
        }
    }

    private int getBiKiepQuantity(Player player) {
        Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
        return biKiep != null ? biKiep.quantity : 0;
    }

    private Item createYardartItem(Player player) {
        Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
        yardart.itemOptions.add(new ItemOption(47, 400));
        yardart.itemOptions.add(new ItemOption(108, 10));
        return yardart;
    }
}
