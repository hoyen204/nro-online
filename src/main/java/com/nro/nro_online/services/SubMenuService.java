package com.nro.nro_online.services;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;

/**
 * @Build Arriety
 */
public class SubMenuService {

    public static final int BAN = 500;
    public static final int BUFF_PET = 501;

    private static SubMenuService i;

    private SubMenuService() {
    }

    public static SubMenuService gI() {
        if (i == null) {
            i = new SubMenuService();
        }
        return i;
    }

    public void controller(Player player, int playerTarget, int menuId) {
        Player plTarget = Client.gI().getPlayer(playerTarget);
        switch (menuId) {
            case BAN:
                if (plTarget != null) {
                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                            "Bạn có chắc chắn muốn ban " + plTarget.name, selects, plTarget);
                }
                break;
            case BUFF_PET:
                if (plTarget != null) {
                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.BUFF_PET, -1,
                            "Bạn có chắc chắn muốn phát đệ tử cho " + plTarget.name, selects, plTarget);
                }
                break;
        }
        Service.getInstance().hideWaitDialog(player);
    }

    public void showMenuForAdmin(Player player) {
        showSubMenu(player, new SubMenu(BAN, "Ban người chơi", ""));
    }

    public void showSubMenu(Player player, SubMenu... subMenus) {
        try (Message msg = Service.getInstance().messageSubCommand((byte) 63)) {
            msg.writer().writeByte(subMenus.length);
            for (SubMenu subMenu : subMenus) {
                msg.writer().writeUTF(subMenu.caption1);
                msg.writer().writeUTF(subMenu.caption2);
                msg.writer().writeShort((short) subMenu.id);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(SubMenuService.class, e);
        }
    }

    public static class SubMenu {

        private int id;
        private String caption1;
        private String caption2;

        public SubMenu(int id, String caption1, String caption2) {
            this.id = id;
            this.caption1 = caption1;
            this.caption2 = caption2;
        }
    }
}
