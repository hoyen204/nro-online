/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;

import static nro.models.npc.NpcFactory.PLAYERID_OBJECT;

import nro.consts.ConstNpc;
import nro.manager.ChuyenKhoanManager;
import nro.models.Transaction;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.server.ServerManager;
import nro.services.BanDoKhoBauService;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;
import nro.services.func.Input;
import nro.utils.TimeUtil;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class QuyLaoKame extends Npc {

    public QuyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Chào con, con muốn ta giúp gì nào?", "Nói chuyện", "Nạp tiền",
                        "Từ chối");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        this.createOtherMenu(player, ConstNpc.NOI_CHUYEN,
                                "Chào con, ta rất vui khi gặp con\nCon muốn làm gì nào?",
                                "Nhiệm vụ", "Học\nKỹ nặng", "Về khu\nvực bang",
                                "Giản tán\nBang hội", "Kho báu\ndưới biển");
                        break;
                    case 1:
                        this.createOtherMenu(player, ConstNpc.CHUYEN_KHOAN, "Nghe nói con muốn chuyển khoản",
                                "Tạo giao dịch", "Xem lịch sử\ngiao dịch", "Nạp thẻ");
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NOI_CHUYEN) {
                switch (select) {
                    case 0:
                        NpcService.gI().createTutorial(player, 564, "Nhiệm vụ tiếp theo của bạn là: "
                                + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
                        break;
                    case 1:
                        this.openShopLearnSkill(player, ConstNpc.SHOP_LEARN_SKILL, 0);
                        break;
                    case 2:
                        if (player.clan == null) {
                            Service.getInstance().sendThongBao(player, "Chưa có bang hội");
                            return;
                        }
                        ChangeMapService.gI().changeMap(player, player.clan.getClanArea(), 910,
                                190);
                        break;
                    case 3:
                        Clan clan = player.clan;
                        if (clan != null) {
                            ClanMember cm = clan.getClanMember((int) player.id);
                            if (cm != null) {
                                if (clan.members.size() > 1) {
                                    Service.getInstance().sendThongBao(player, "Bang phải còn một người");
                                    break;
                                }
                                if (!clan.isLeader(player)) {
                                    Service.getInstance().sendThongBao(player, "Phải là bảng chủ");
                                    break;
                                }
                                NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                        "Yes you do!", "Từ chối!");
                            }
                            break;
                        }
                        Service.getInstance().sendThongBao(player, "Có bang hội đâu ba!!!");
                        break;
                    case 4:
                        if (player.clan != null) {
                            if (player.clan.banDoKhoBau != null) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                        "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                        + player.clan.banDoKhoBau.level
                                        + "\nCon có muốn đi theo không?",
                                        "Đồng ý", "Từ chối");
                            } else {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                        "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\n"
                                        + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                        "Chọn\ncấp độ", "Từ chối");
                            }
                        } else {
                            NpcService.gI().createTutorial(player, 564, "Con phải có bang hội ta mới có thể cho con đi");
                        }
                        break;

                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
                switch (select) {
                    case 0:
                        if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                            ChangeMapService.gI().goToDBKB(player);
                        } else {
                            this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                    + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                        }
                        break;

                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
                switch (select) {
                    case 0:
                        if (player.isAdmin()
                                || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                            Input.gI().createFormChooseLevelBDKB(player);
                        } else {
                            this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                    + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                        }
                        break;
                }

            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
                switch (select) {
                    case 0:
                        BanDoKhoBauService.gI().openBanDoKhoBau(player,
                                Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                        break;
                }

            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DOI_THE) {
                switch (select) {
                    case 0: {
                        if (player.isAdmin()) {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "|2|CHỨC NĂNG ĐANG ĐƯỢC PHÁT TRIỂN", "Dạ", "Vâng Ạ");
                        }
                        this.createOtherMenu(player, ConstNpc.NAP_THE,
                                "|2|Lựa chọn loại thẻ", "VIETTEL", "VINAPHONE");
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
                switch (select) {
                    case 0:
                        this.createOtherMenu(player, ConstNpc.VIETTEL,
                                "|7|Loại thẻ con đang chọn là Viettel\n|2|Chọn đúng mệnh giá của thẻ, nhập đầy đủ Seri và Mã thẻ. Sau khi nhập đủ bấm OK và chờ hệ thống xử lý", "10k", "20k", "50k", "100k", "200k", "500k", "1tr");
                        break;
                    case 1:
                        this.createOtherMenu(player, ConstNpc.VINAPHONE, "|7|Loại thẻ con đang chọn là VinaPhone\n|2|Chọn đúng mệnh giá của thẻ, nhập đầy đủ Seri và Mã thẻ. Sau khi nhập đủ bấm OK và chờ hệ thống xử lý", "10k", "20k", "50k", "100k", "200k", "500k", "1tr");
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.VINAPHONE) {
                switch (select) {
                    case 0:
                        Input.gI().createFormNapThe(player, "VINAPHONE", "10000");
                        break;
                    case 1:
                        Input.gI().createFormNapThe(player, "VINAPHONE", "20000");
                        break;
                    case 2:
                        Input.gI().createFormNapThe(player, "VINAPHONE", "50000");
                        break;
                    case 3:
                        Input.gI().createFormNapThe(player, "VINAPHONE", "100000");
                        break;
                    case 4:
                        Input.gI().createFormNapThe(player, "VINAPHONE", "200000");
                        break;
                    case 5:
                        Input.gI().createFormNapThe(player, "VINAPHONE", "500000");
                        break;
                    case 6:
                        Input.gI().createFormNapThe(player, "VINAPHONE", "1000000");
                        break;
                }

            } else if (player.iDMark.getIndexMenu() == ConstNpc.VIETTEL) {
                switch (select) {
                    case 0:
                        Input.gI().createFormNapThe(player, "VIETTEL", "10000");
                        break;
                    case 1:
                        Input.gI().createFormNapThe(player, "VIETTEL", "20000");
                        break;
                    case 2:
                        Input.gI().createFormNapThe(player, "VIETTEL", "50000");
                        break;
                    case 3:
                        Input.gI().createFormNapThe(player, "VIETTEL", "100000");
                        break;
                    case 4:
                        Input.gI().createFormNapThe(player, "VIETTEL", "200000");
                        break;
                    case 5:
                        Input.gI().createFormNapThe(player, "VIETTEL", "500000");
                        break;
                    case 6:
                        Input.gI().createFormNapThe(player, "VIETTEL", "1000000");
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.CHUYEN_KHOAN) {
                switch (select) {
                    case 0:
                        boolean canCreate = false;
                        long timeDifference = 0;
                        LocalDateTime lastTimeCreate = ChuyenKhoanManager.GetLastimeCreateTransaction(player);

                        if (lastTimeCreate == null) {
                            canCreate = true;
                        } else {
                            LocalDateTime now = LocalDateTime.now();
                            timeDifference = TimeUtil.calculateTimeDifferenceInSeconds(lastTimeCreate, now);

                            if (timeDifference > 10) {
                                canCreate = true;
                            }
                        }

                        if (player.isAdmin()) {
                            canCreate = true;
                        }

                        if (canCreate) {
                            Input.gI().createFormChuyenKhoan(player);
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn cần đợi " + (10 - timeDifference) + " giây nữa để được tạo giao dịch mới");
                        }

                        break;
                    case 1:
                        ChuyenKhoanManager.ShowTransaction(player);
                        break;
                    case 2:
                        if (true) {
                            this.npcChat(player, "Chức năng bảo trì vui lòng nạp ATM ");
                            break;
                        }
                        this.createOtherMenu(player, ConstNpc.MENU_DOI_THE, "Nạp thẻ cào tự động " + ServerManager.name + " \n" + "|1|Số dư trong tài khoản: " + Util.numberToMoney(player.getSession().vnd) + "VNĐ\n"
                                + "|8|Số hồng ngọc trong tài khoản: " + player.inventory.ruby + "\n"
                                + "|2|Lưu ý: Chọn đúng mệnh giá thẻ. Nếu sai mệnh giá sẽ không nhận được tiền nạp\n"
                                + "|7|ADMIN không chịu trách nhiệm với lỗi sai mệnh giá thẻ\n"
                                + "|2|Để mở thành viên hãy quy đổi tiền sang thỏi vàng sau đó đến gặp Santa nhé", "Nạp thẻ\ncào", "Từ chối");
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.CONTENT_CHUYEN_KHOAN) {
                switch (select) {
                    case 1:
                        Transaction transaction = ChuyenKhoanManager.GetTransactionLast(player.id);
                        Service.getInstance().LinkService(player, 11039, "QUET QR CODE DJTME", "https://img.vietqr.io/image/MB-02147019062000-compact2.png?amount=" + transaction.amount + "&addInfo=" + transaction.description, "Quét\nQR");
//                        openLink("https://img.vietqr.io/image/MB-02147019062000-compact2.png?amount=" + transaction.amount + "&addInfo=" + transaction.description);
                        break;
                }
            }
        }
    }

    private static void openLink(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();

            // Kiểm tra xem máy tính có hỗ trợ mở liên kết không
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            } else {
                // Nếu không hỗ trợ, bạn có thể xử lý tùy ý ở đây
                System.out.println("Không thể mở liên kết trên thiết bị này.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
