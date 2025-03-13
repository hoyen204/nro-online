/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstTranhNgocNamek;
import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.phuban.DragonNamecWar.TranhNgoc;
import nro.models.player.Player;
import nro.server.ServerManager;
import nro.services.InventoryService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.ShopService;
import nro.utils.TimeUtil;

/**
 *
 * @author Arriety
 */
public class TruongLaoGuru extends Npc {

    public TruongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        Item mcl = InventoryService.gI().findItemBagByTemp(player, 2000);
        int slMCL = (mcl == null) ? 0 : mcl.quantity;
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân",
                        "Tham gia", "Đổi điểm\nThưởng\n[" + slMCL + "]", "Bảng\nxếp hạng", "Từ chối");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU: {
                    switch (select) {
                        case 0:
                            TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
                            if (TranhNgoc.isTimeRegWar()) {
                                String message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân";
                                if (tn == null) {
                                    if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() == 0) {
                                        tn = new TranhNgoc();
                                    } else {
                                        tn = ServerManager.gI().getTranhNgocManager().getAvableTranhNgoc();
                                    }
                                    if (tn == null) {
                                        message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân";
                                    } else {
                                        message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\nPhe Cadic: " + tn.getPlayersCadic().size() + "\nPhe Fide: " + tn.getPlayersFide().size();
                                    }
                                    this.createOtherMenu(player, ConstNpc.REGISTER_TRANH_NGOC,
                                            message,
                                            "Tham gia phe Cadic", "Tham gia phe Fide", "Đóng");
                                } else {
                                    if (tn != null) {
                                        message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\nPhe Cadic: " + tn.getPlayersCadic().size() + "\nPhe Fide: " + tn.getPlayersFide().size();
                                    }
                                    this.createOtherMenu(player, ConstNpc.LOG_OUT_TRANH_NGOC,
                                            message,
                                            "Hủy\nĐăng Ký", "Đóng");
                                }
                                return;
                            } else {
                                if (TranhNgoc.isTimeStartWar() && !tn.isClosed()) {
                                    this.createOtherMenu(player, ConstNpc.REIN_TRANH_NGOC, // Xuân lol dm béo im mồm
                                            "Bạn có muốn quay lại tranh ngọc không?",
                                            "Có", "Đóng");
                                }
                            }
                            Service.getInstance().sendPopUpMultiLine(player, 0, 7184, "Sự kiện sẽ mở đăng ký vào lúc " + TimeUtil.ShowTime(TranhNgoc.HOUR_REGISTER, TranhNgoc.MIN_REGISTER) + "\nSự kiện sẽ bắt đầu vào " + TimeUtil.ShowTime(TranhNgoc.HOUR_OPEN, TranhNgoc.MIN_OPEN) + " và kết thúc vào " + TimeUtil.ShowTime(TranhNgoc.HOUR_CLOSE, TranhNgoc.HOUR_CLOSE));
                            break;
                        case 1:// Shop
                            ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_CHIEN_LUC, 0, -1);
                            break;
                        case 2:
                            Service.getInstance().sendThongBao(player, "Update coming soon");
                            break;
                    }
                    break;
                }
                case ConstNpc.REGISTER_TRANH_NGOC: {
                    TranhNgoc tranhNgoc;
                    if (!player.getSession().actived) {
                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                        return;
                    }

                    switch (select) {
                        case 0:
                            if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() == 0) {
                                tranhNgoc = new TranhNgoc();
                                tranhNgoc.addPlayersCadic(player);
                                Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                            } else {
                                boolean registerStatus = ServerManager.gI().getTranhNgocManager().register(player, true);
                                if (registerStatus) {
                                    Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                                } else {
                                    if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() >= 25) {
                                        Service.getInstance().sendThongBao(player, "Sự kiện đang quá tải, vui lòng tải lại xong!");
                                    } else {
                                        tranhNgoc = new TranhNgoc();
                                        tranhNgoc.addPlayersCadic(player);
                                        Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                                    }
                                }
                            }
                            break;
                        case 1:
                            if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() == 0) {
                                tranhNgoc = new TranhNgoc();
                                tranhNgoc.addPlayersFide(player);
                                Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                            } else {
                                boolean registerStatus = ServerManager.gI().getTranhNgocManager().register(player, false);

                                if (registerStatus) {
                                    Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                                } else {
                                    if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() >= 25) {
                                        Service.getInstance().sendThongBao(player, "Sự kiện đang quá tải, vui lòng tải lại xong!");
                                    } else {
                                        tranhNgoc = new TranhNgoc();
                                        tranhNgoc.addPlayersFide(player);
                                        Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                                    }
                                }
                            }
                            break;
                    }
                    break;
                }
                case ConstNpc.LOG_OUT_TRANH_NGOC: {
                    switch (select) {
                        case 0:
                            TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
                            tn.removePlayersCadic(player);
                            tn.removePlayersFide(player);
                            Service.getInstance().sendThongBao(player, "Hủy đăng ký thành công");
                            break;
                    }
                    break;
                }
                case ConstNpc.REIN_TRANH_NGOC: {
                    switch (select) {
                        case 0:
                            TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
                            if (player != null && player.zone.map.mapId != ConstTranhNgocNamek.MAP_ID && tn != null) {
                                if (tn.isCadic(player)) {
                                    tn.JoinMap(player, 1);
                                } else {
                                    tn.JoinMap(player, 2);
                                }
                            }
                            break;
                    }
                }
            }
        }
    }
}
