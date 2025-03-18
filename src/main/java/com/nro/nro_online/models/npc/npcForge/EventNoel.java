/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.npcForge;

import java.time.Duration;
import java.time.LocalDateTime;

import nro.consts.ConstNpc;
import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.event.noel.NoelBossOne;
import nro.models.boss.event.noel.NoelBossTwo;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;

/**
 * @author Arriety
 */
public class EventNoel extends Npc {

    private LocalDateTime last_time_summon_boss;
    private final int CLEAN_COST = 100;
    private final int SUMON_COST = 200;

    public EventNoel(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    private Boolean CheckBossAlive() {
        Boss bossOne = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_ONE);
        Boss bossTwo = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_TWO);
        return bossOne != null || bossTwo != null;
    }

    @Override
    public void openBaseMenu(Player player) {
        if (CheckBossAlive()) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Rửa tay loại bỏ lời nguyền băng giá để được đấm boss?",
                    "Có", "Gọi Boss", "Nói chuyện");
        } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Bạn có muốn gọi Boss Noel & Noel Videl không?",
                    "Có", "Gọi Boss", "Nói chuyện");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0:
                    Item dongPi = InventoryService.gI().findItemBagByTemp(player, 2008);
                    Boss bossOne = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_ONE);
                    Boss bossTwo = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_TWO);
                    if (dongPi == null || dongPi.quantity < CLEAN_COST) {
                        Service.getInstance().sendThongBao(player, "Bạn không có đủ " + CLEAN_COST + " " + (dongPi != null ? dongPi.getName() : "đồng pi") + " để tham chiến.");
                        return;
                    }
                    InventoryService.gI().subQuantityItemsBag(player, dongPi, CLEAN_COST);
                    Service.getInstance().sendThongBao(player, "Bạn đã rửa tay Thành Công!");
                    if (bossOne != null && !bossOne.isDie()) {
                        ((NoelBossOne) bossOne).AddPlayerCanAttack(player);
                    }
                    if (bossTwo != null && !bossTwo.isDie()) {
                        ((NoelBossTwo) bossTwo).AddPlayerCanAttack(player);
                    }
                    break;
                case 1:
                    Boss one = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_ONE);
                    Boss two = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_TWO);
                    if (one != null || two != null) {
                        this.npcChat(player, "Boss vẫn còn sống");
                        break;
                    }
                    Item pi = InventoryService.gI().findItemBagByTemp(player, 2008);
                    Duration duration = null;
                    if (this.last_time_summon_boss != null) {
                        duration = Duration.between(this.last_time_summon_boss, LocalDateTime.now());
                    }
                    if (this.last_time_summon_boss != null && duration.toMinutes() < 5) {
                        Duration khoangThoiGianMoi = duration.minusMinutes(5).abs();

                        long phut = khoangThoiGianMoi.toMinutes();
                        long giay = khoangThoiGianMoi.minusMinutes(phut).getSeconds();

                        Service.getInstance().sendThongBao(player, "Cần chờ " + phut + " phút và " + giay + " giây để có thể tiếp tục gọi boss");
                        return;
                    }
                    if (pi == null || pi.quantity < SUMON_COST) {
                        Service.getInstance().sendThongBao(player, "Bạn không có đủ " + SUMON_COST + " " + (pi != null ? pi.getName() : "đồng pi") + " để tham chiến.");
                        return;
                    }
                    String thongbao = "Bạn đã gọi Boss thành công ở Map: " + player.zone.map.mapName + " khu: " + player.zone.zoneId;
                    InventoryService.gI().subQuantityItemsBag(player, pi, SUMON_COST);
                    Service.getInstance().sendThongBao(player, thongbao);
                    this.last_time_summon_boss = LocalDateTime.now();
                    BossFactory.createNoelBoss(BossFactory.NOEL_BOSS_ONE, player);
                    BossFactory.createNoelBoss(BossFactory.NOEL_BOSS_TWO, player);
                    player.zone.loadAnotherToMe(player);
                    player.zone.load_Me_To_Another(player);
                    break;
                case 2:
                    this.createOtherMenu(player, ConstNpc.MENU_NOI_CHUYEN,
                            "|8|Chào bạn, tôi rất vui khi gặp bạn\n|6|Tôi sẽ cho bạn 2 lựa chọn!\n"
                            + "|6|Đổi hoa lấy x5 Băng tâm thần thủy\n"
                            + "|6|Đổi Carrot lấy x1 Băng tâm thần thủy",
                            "Lựa chọn 1", "Lựa chọn 2");
                    break;
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NOI_CHUYEN) {
            switch (select) {
                case 0:
                    Item hoa = InventoryService.gI().findItem(player.inventory.itemsBag, 610);
                    if (hoa == null) {
                        this.npcChat(player, "Con giống Oprah Winfrey vậy, không có 1 bông hoa nào cả...");
                        break;
                    }
                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                        Item hop = ItemService.gI().createNewItem((short) 2009);
                        hop.itemOptions.add(new ItemOption(74, 0));
                        hop.quantity = 5;
                        InventoryService.gI().addItemBag(player, hop, 999);
                        InventoryService.gI().subQuantityItemsBag(player, hoa, 1);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Trao đổi thành công!");
                    } else {
                        Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                    }
                    break;
                case 1:
                    Item carot = InventoryService.gI().findItem(player.inventory.itemsBag, 610);
                    if (carot == null) {
                        this.npcChat(player, "Thiếu carrot");
                        break;
                    }
                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                        Item hop = ItemService.gI().createNewItem((short) 2009);
                        hop.itemOptions.add(new ItemOption(74, 0));
                        InventoryService.gI().addItemBag(player, hop, 0);
                        InventoryService.gI().subQuantityItemsBag(player, carot, 1);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Trao đổi thành công!");
                    } else {
                        Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                    }
                    break;
            }
        }
    }
}
