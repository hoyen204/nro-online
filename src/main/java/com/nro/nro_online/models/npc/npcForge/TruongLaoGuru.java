package com.nro.nro_online.models.npc.npcForge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.models.DragonNamecWar.TranhNgoc;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ShopService;
import com.nro.nro_online.utils.TimeUtil;

public class TruongLaoGuru extends Npc {

    public TruongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player) || TaskService.gI().checkDoneTaskTalkNpc(player, this)) return;
        Item mcl = InventoryService.gI().findItemBagByTemp(player, 2000);
        int slMCL = mcl != null ? mcl.quantity : 0;
        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân",
                "Tham gia", "Đổi điểm\nThưởng\n[" + slMCL + "]", "Bảng\nxếp hạng", "Từ chối");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;
        int menuIndex = player.iDMark.getIndexMenu();
        TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);

        if (menuIndex == ConstNpc.BASE_MENU) {
            if (select == 0) {
                if (TranhNgoc.isTimeRegWar()) {
                    String message = tn != null
                            ? "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\nPhe Cadic: " + tn.getPlayersCadic().size() + "\nPhe Fide: " + tn.getPlayersFide().size()
                            : "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân";
                    tn = (tn == null) ? (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() == 0 ? new TranhNgoc() : ServerManager.gI().getTranhNgocManager().getAvableTranhNgoc()) : tn;
                    this.createOtherMenu(player, tn == null ? ConstNpc.REGISTER_TRANH_NGOC : ConstNpc.LOG_OUT_TRANH_NGOC, message,
                            tn == null ? new String[]{"Tham gia phe Cadic", "Tham gia phe Fide", "Đóng"} : new String[]{"Hủy\nĐăng Ký", "Đóng"});
                    return;
                }
                if (TranhNgoc.isTimeStartWar() && tn != null && !tn.isClosed()) {
                    this.createOtherMenu(player, ConstNpc.REIN_TRANH_NGOC, "Bạn có muốn quay lại tranh ngọc không?", "Có", "Đóng");
                    return;
                }
                Service.getInstance().sendPopUpMultiLine(player, 0, 7184, "Sự kiện sẽ mở đăng ký vào lúc " + TimeUtil.showTime(TranhNgoc.HOUR_REGISTER, TranhNgoc.MIN_REGISTER) + "\nSự kiện sẽ bắt đầu vào " + TimeUtil.showTime(TranhNgoc.HOUR_OPEN, TranhNgoc.MIN_OPEN) + " và kết thúc vào " + TimeUtil.showTime(TranhNgoc.HOUR_CLOSE, TranhNgoc.HOUR_CLOSE));
            } else if (select == 1) {
                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_CHIEN_LUC, 0, -1);
            } else if (select == 2) {
                Service.getInstance().sendThongBao(player, "Update coming soon");
            }
        } else if (menuIndex == ConstNpc.REGISTER_TRANH_NGOC && player.getSession().actived) {
            if (select == 0 || select == 1) {
                boolean isCadic = select == 0;
                int numTranhNgoc = ServerManager.gI().getTranhNgocManager().numOfTranhNgoc();
                if (numTranhNgoc == 0 || !ServerManager.gI().getTranhNgocManager().register(player, isCadic) && numTranhNgoc < 25) {
                    TranhNgoc tranhNgoc = new TranhNgoc();
                    if (isCadic) tranhNgoc.addPlayersCadic(player);
                    else tranhNgoc.addPlayersFide(player);
                    Service.getInstance().sendThongBao(player, "Đăng ký vào phe " + (isCadic ? "Cadic" : "Fide") + " thành công");
                } else if (ServerManager.gI().getTranhNgocManager().register(player, isCadic)) {
                    Service.getInstance().sendThongBao(player, "Đăng ký vào phe " + (isCadic ? "Cadic" : "Fide") + " thành công");
                } else {
                    Service.getInstance().sendThongBao(player, "Sự kiện đang quá tải, vui lòng tải lại xong!");
                }
            }
        } else if (menuIndex == ConstNpc.LOG_OUT_TRANH_NGOC && select == 0) {
            tn.removePlayersCadic(player);
            tn.removePlayersFide(player);
            Service.getInstance().sendThongBao(player, "Hủy đăng ký thành công");
        } else if (menuIndex == ConstNpc.REIN_TRANH_NGOC && select == 0 && tn != null && player.zone.map.mapId != ConstTranhNgocNamek.MAP_ID) {
            tn.joinMap(player, tn.isCadic(player) ? 1 : 2);
        }
    }
}
