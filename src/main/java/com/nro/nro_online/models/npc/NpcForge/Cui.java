/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstTask;
import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 *
 * @author Administrator
 */
public class Cui extends Npc {

    private final int COST_FIND_BOSS = 20000000;

    public Cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player pl) {
        if (canOpenNpc(pl)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                if (pl.playerTask.taskMain.id == 7) {
                    NpcService.gI().createTutorial(pl, this.avartar,
                            "Hãy lên đường cứu đứa bé nhà tôi\n"
                            + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                } else {
                    switch (this.mapId) {
                        case 19:
                            int taskId = TaskService.gI().getIdTask(pl);
                            switch (taskId) {
                                case ConstTask.TASK_19_0:
                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                            "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS)
                                            + " vàng)",
                                            "Đến Cold", "Đến\nNappa", "Từ chối");
                                    break;
                                case ConstTask.TASK_19_1:
                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                            "Đến chỗ\nMập đầu đinh\n("
                                            + Util.numberToMoney(COST_FIND_BOSS) + " vàng)",
                                            "Đến Cold", "Đến\nNappa", "Từ chối");
                                    break;
                                case ConstTask.TASK_19_2:
                                    this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                            "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS)
                                            + " vàng)",
                                            "Đến Cold", "Đến\nNappa", "Từ chối");
                                    break;
                                default:
                                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                            "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                            "Đến Cold", "Đến\nNappa", "Từ chối");

                                    break;
                            }
                            break;
                        case 68:
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                            break;
                        default:
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                    + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                    "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 26) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                            break;
                        case 2:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                            break;
                    }
                }
            }
            if (this.mapId == 19) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                    switch (select) {
                        case 0:
                            Boss boss = BossManager.gI().getBossById(BossFactory.KUKU);
                            if (boss != null && !boss.isDie()) {
                                if (player.inventory.gold >= COST_FIND_BOSS) {
                                    player.inventory.gold -= COST_FIND_BOSS;
                                    ChangeMapService.gI().changeMap(player, boss.zone,
                                            boss.location.x, boss.location.y);
                                    Service.getInstance().sendMoney(player);
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Không đủ vàng, còn thiếu "
                                            + Util.numberToMoney(
                                                    COST_FIND_BOSS - player.inventory.gold)
                                            + " vàng");
                                }
                            }
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                            break;
                        case 2:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                    switch (select) {
                        case 0:
                            Boss boss = BossManager.gI().getBossById(BossFactory.MAP_DAU_DINH);
                            if (boss != null && !boss.isDie()) {
                                if (player.inventory.gold >= COST_FIND_BOSS) {
                                    player.inventory.gold -= COST_FIND_BOSS;
                                    ChangeMapService.gI().changeMap(player, boss.zone,
                                            boss.location.x, boss.location.y);
                                    Service.getInstance().sendMoney(player);
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Không đủ vàng, còn thiếu "
                                            + Util.numberToMoney(
                                                    COST_FIND_BOSS - player.inventory.gold)
                                            + " vàng");
                                }
                            }
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                            break;
                        case 2:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                    switch (select) {
                        case 0:
                            Boss boss = BossManager.gI().getBossById(BossFactory.RAMBO);
                            if (boss != null && !boss.isDie()) {
                                if (player.inventory.gold >= COST_FIND_BOSS) {
                                    player.inventory.gold -= COST_FIND_BOSS;
                                    ChangeMapService.gI().changeMap(player, boss.zone,
                                            boss.location.x, boss.location.y);
                                    Service.getInstance().sendMoney(player);
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Không đủ vàng, còn thiếu "
                                            + Util.numberToMoney(
                                                    COST_FIND_BOSS - player.inventory.gold)
                                            + " vàng");
                                }
                            }
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                            break;
                        case 2:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                            break;
                    }
                }
            }
            if (this.mapId == 68) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                            break;
                    }
                }
            }
        }
    }

}
