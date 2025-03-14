package com.nro.nro_online.models.npc.NpcForge;

import java.time.LocalDateTime;

import static com.nro.nro_online.models.npc.NpcFactory.PLAYERID_OBJECT;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.clan.Clan;
import com.nro.nro_online.models.clan.ClanMember;
import com.nro.nro_online.models.map.phoban.BanDoKhoBau;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.BanDoKhoBauService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.Input;
import com.nro.nro_online.utils.Util;

public class QuyLaoKame extends Npc {

    public QuyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player) && !TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Chào con, ta là Quy Lão Kame đây! Muốn gì thì nói lẹ đi nào 😏",
                    "Nói chuyện", "Chuyển khoản", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        if (player.iDMark.isBaseMenu()) {
            switch (select) {
            case 0: // Nói chuyện
                this.createOtherMenu(player, ConstNpc.NOI_CHUYEN,
                        "Chào con, ta vui lắm khi gặp con! Chọn đi nào?",
                        "Nhiệm vụ", "Học\nKỹ năng", "Về khu\nvực bang",
                        "Giải tán\nBang hội", "Kho báu\ndưới biển");
                break;
            case 1: // Chuyển khoản
                this.createOtherMenu(player, ConstNpc.CHUYEN_KHOAN,
                        "Muốn chuyển khoản hả? Làm lẹ đi nhé 😜",
                        "Tạo giao dịch", "Xem lịch sử\ngiao dịch", "Từ chối");
                break;
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.NOI_CHUYEN) {
            switch (select) {
            case 0: // Nhiệm vụ
                NpcService.gI().createTutorial(player, 564, "Nhiệm vụ tiếp theo: " +
                        player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
                break;
            case 1: // Học kỹ năng
                this.openShopLearnSkill(player, ConstNpc.SHOP_LEARN_SKILL, 0);
                break;
            case 2: // Về khu vực bang
                if (player.clan == null) {
                    Service.getInstance().sendThongBao(player, "Chưa có bang hội nhé con! 😛");
                    return;
                }
                ChangeMapService.gI().changeMap(player, player.clan.getClanArea(), 910, 190);
                break;
            case 3: // Giải tán bang hội
                Clan clan = player.clan;
                if (clan == null) {
                    Service.getInstance().sendThongBao(player, "Chưa có bang hội mà đòi giải tán hả? 😕");
                    return;
                }
                ClanMember cm = clan.getClanMember((int) player.id);
                if (cm == null || clan.members.size() > 1) {
                    Service.getInstance().sendThongBao(player, "Bang phải còn 1 người thôi nhé! 😅");
                    return;
                }
                if (!clan.isLeader(player)) {
                    Service.getInstance().sendThongBao(player, "Chỉ bang chủ mới giải tán được nha! 😤");
                    return;
                }
                NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1,
                        "Chắc chắn muốn giải tán bang hội không con?",
                        "Đồng ý", "Từ chối");
                break;
            case 4: // Kho báu dưới biển
                if (player.clan == null) {
                    NpcService.gI().createTutorial(player, 564, "Phải có bang hội ta mới cho đi nhé! 😛");
                    return;
                }
                if (player.clan.banDoKhoBau != null) {
                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                            "Bang của con đang tìm kho báu cấp " + player.clan.banDoKhoBau.level +
                                    "\nMuốn đi theo không nào?",
                            "Đồng ý", "Từ chối");
                } else {
                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                            "Đây là bản đồ kho báu dưới biển\nChọn cấp độ vừa sức nhé con!",
                            "Chọn\ncấp độ", "Từ chối");
                }
                break;
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
            if (select == 0) {
                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                    ChangeMapService.gI().goToDBKB(player);
                } else {
                    this.npcChat(player, "Con cần ít nhất " + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB) +
                            " sức mạnh nhé!");
                }
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
            if (select == 0) {
                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                    Input.gI().createFormChooseLevelBDKB(player);
                } else {
                    this.npcChat(player, "Con cần ít nhất " + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB) +
                            " sức mạnh nhé!");
                }
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
            if (select == 0) {
                BanDoKhoBauService.gI().openBanDoKhoBau(player,
                        Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
            }
        } else if (player.iDMark.getIndexMenu() == ConstNpc.CHUYEN_KHOAN) {
            this.npcChat(player, "Đang được bảo trì nhé con.");
        }
    }
}