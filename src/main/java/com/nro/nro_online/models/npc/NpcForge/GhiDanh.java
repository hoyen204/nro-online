/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstItem;
import nro.consts.ConstMap;
import nro.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.challenge.MartialCongressService;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.func.ChangeMapService;

/**
 *
 * @author Arriety
 */
public class GhiDanh extends Npc {

    String[] menuselect = new String[]{};

    public GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case ConstMap.DAI_HOI_VO_THUAT:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào mừng bạn đến với đại hội võ thuật", "Đại Hội\nVõ Thuật\nLần Thứ\n23", "Giải\nSiêu Hạng");
                    break;
                case ConstMap.DAI_HOI_VO_THUAT_129:
                    if (player.levelWoodChest == 0) {
                        menuselect = new String[]{
                            "Thi đấu\2000 ruby",
                            "Về\nĐại Hội\nVõ Thuật"};
                    } else {
                        menuselect = new String[]{
                            "Thi đấu\n2000 ruby",
                            "Nhận thưởng\nRương cấp\n" + player.levelWoodChest,
                            "Về\nĐại Hội\nVõ Thuật"};
                    }
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm,ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào",
                            menuselect, "Từ chối");
                    break;
                default:
                    super.openBaseMenu(player);
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU:
                    if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
                        switch (select) {
                            case 0: {
                                ChangeMapService.gI().changeMapNonSpaceship(player,
                                        ConstMap.DAI_HOI_VO_THUAT_129, player.location.x, 360);
                                break;
                            }
                            case 1: {
                                ChangeMapService.gI().changeMapNonSpaceship(player,
                                        ConstMap.DAI_HOI_VO_THUAT_113, player.location.x, 360);
                                break;
                            }
                        }
                    } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
                        if (player.levelWoodChest == 0) {
                            switch (select) {
                                case 0:
                                    if (InventoryService.gI().finditemWoodChest(player)) {
                                        if (player.inventory.getRuby() >= 2000) {
                                            MartialCongressService.gI().startChallenge(player);
                                            player.inventory.subRuby(2000);
                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Không đủ vàng, còn thiếu 2000 hong ngoc");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player,
                                                "Hãy mở rương báu vật trước");
                                    }
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapNonSpaceship(player,
                                            ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
                                    break;
                            }
                        } else {
                            switch (select) {
                                case 0:
                                    if (InventoryService.gI().finditemWoodChest(player)) {
                                        if (player.inventory.getRuby() >= 2000) {
                                            MartialCongressService.gI().startChallenge(player);
                                            player.inventory.subRuby(2000);
                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Không đủ vàng, còn thiếu 2000 ruby");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player,
                                                "Hãy mở rương báu vật trước");
                                    }
                                    break;
                                case 1:
                                    if (!player.receivedWoodChest) {
                                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                            Item it = ItemService.gI()
                                                    .createNewItem((short) ConstItem.RUONG_GO);
                                            it.itemOptions
                                                    .add(new ItemOption(72, player.levelWoodChest));
                                            it.itemOptions.add(new ItemOption(30, 0));
                                            it.createTime = System.currentTimeMillis();
                                            InventoryService.gI().addItemBag(player, it, 0);
                                            InventoryService.gI().sendItemBags(player);

                                            player.receivedWoodChest = true;
                                            player.levelWoodChest = 0;
                                            Service.getInstance().sendThongBao(player,
                                                    "Bạn nhận được rương gỗ");
                                        } else {
                                            this.npcChat(player, "Hành trang đã đầy");
                                        }
                                    } else {
                                        Service.getInstance().sendThongBao(player,
                                                "Mỗi ngày chỉ có thể nhận rương báu 1 lần");
                                    }
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapNonSpaceship(player,
                                            ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    }
}
