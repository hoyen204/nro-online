/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.CombineServiceNew;
import nro.services.func.ShopService;

/**
 *
 * @author Arriety
 */
public class BunmaTuongLai extends Npc {

    public BunmaTuongLai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 102) {
                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?",
                            "Cửa hàng", "Đóng");
                }
            } else if (this.mapId == 104) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!",
                        "Cửa hàng", "Nâng cấp linh thú", "Hướng dẫn", "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 102:
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                        }
                    }
                    break;
                case 104:
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BUNMA_LINH_THU, 1, -1);
                                break;
                            case 1:
                                if (player.egglinhthu == null) {
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_LINHTHU);
                                    break;
                                } else {
                                    Service.getInstance().sendThongBao(player, "Hãy nở trứng trước!");
                                    break;
                                }
                            case 2:
                                Service.getInstance().LinkService(player, 2752, "|8|Chào bạn tôi là Bunma\n|6|tôi sẽ giúp đỡ bạn cách làm sao để nâng cấp trứng linh thú\n"
                                        + "|8|Cửa hàng:\n|6|nơi bán các vận dụng phục vụ cho việc nâng cấp cấp trứng linh thú\n"
                                        + "|8|Nâng cấp linh thú:\n|6|Đây sẽ là 1 bàn hóa phép giúp bạn nâng cấp con linh thú đó lên chỉ số víp",
                                        "https://nrokimkan.online/", "Video hướng dẫn");
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineServiceNew.UPGRADE_LINHTHU:
                                if (select == 0) {
                                    CombineServiceNew.gI().startCombine(player);
                                }
                                break;
                        }
                    }
                    break;
            }
        }
    }
}
