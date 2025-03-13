package com.nro.nro_online.models.npc;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ShopService;
import com.nro.nro_online.utils.Util;
import nro.consts.ConstNpc;
import nro.models.map.Map;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.Client;
import nro.server.Manager;
import nro.server.io.Message;
import nro.services.MapService;
import nro.services.Service;
import nro.services.func.ShopService;
import nro.utils.Log;
import nro.utils.Util;

/**
 *
 * @Stole Arriety
 *
 */
public abstract class Npc implements IAtionNpc {

    public int mapId;
    public Map map;

    public int status;

    public int cx;

    public int cy;

    public int tempId;

    public int avartar;

    public BaseMenu baseMenu;

    protected Npc(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        this.map = MapService.gI().getMapById(mapId);
        this.mapId = mapId;
        this.status = status;
        this.cx = cx;
        this.cy = cy;
        this.tempId = tempId;
        this.avartar = avartar;
        Manager.NPCS.add(this);
    }

    public void initBaseMenu(String text) {
        text = text.substring(1);
        String[] data = text.split("\\|");
        baseMenu = new BaseMenu();
        baseMenu.npcId = tempId;
        baseMenu.npcSay = data[0].replaceAll("<>", "\n");
        baseMenu.menuSelect = new String[data.length - 1];
        for (int i = 0; i < baseMenu.menuSelect.length; i++) {
            baseMenu.menuSelect[i] = data[i + 1].replaceAll("<>", "\n");
        }
    }

    public void createOtherMenu(Player player, int indexMenu, String npcSay, String... menuSelect) {
        Message msg;
        try {
            player.iDMark.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createOtherMenu(Player player, int indexMenu, String npcSay, String[] menuSelect, Object object) {
        NpcFactory.PLAYERID_OBJECT.put(player.id, object);
        Message msg;
        try {
            player.iDMark.setIndexMenu(indexMenu);
            msg = new Message(32);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(npcSay);
            msg.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                msg.writer().writeUTF(menu);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
            try {
                if (baseMenu != null) {
                    baseMenu.openMenu(player);
                } else {
                    Message msg;
                    msg = new Message(32);
                    msg.writer().writeShort(tempId);
                    msg.writer().writeUTF("Bạn gặp tôi có việc gì vậy?");
                    msg.writer().writeByte(1);
                    msg.writer().writeUTF("Đóng");
                    player.sendMessage(msg);
                    msg.cleanup();
                }
            } catch (Exception e) {
                Log.error(Npc.class, e);
            }
        }
    }

    public void npcChat(Player player, String text) {
        Message msg;
        try {
            msg = new Message(124);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(text);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public long LastTimeAutoChat = 0;

    public void AutoChat() {
        try {
            switch (tempId) {
                case 13:
                    for (int i = Client.gI().getPlayers().size() - 1; i >= 0; i--) {
                        try {
                            Player pl = Client.gI().getPlayers().get(i);
                            if (pl != null && pl.zone != null && pl.zone.map != null) {
                                if (pl.zone.map.mapId == mapId && pl.isPl()) {
                                    if (System.currentTimeMillis() - LastTimeAutoChat > 5000) {
                                        LastTimeAutoChat = System.currentTimeMillis();
                                    }
                                    npcChat(pl, getText(tempId));
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getText(int id) {
        switch (id) {
            case 13:
                return textQuyLao[Util.nextInt(0, textQuyLao.length - 1)];
            default:
                return "";
        }
    }

    private static final String[] textQuyLao = new String[]{
        "Lá Là La...",
        "Ngày tươi đẹp nhất là ngày được may mắn nhìn thấy em",
        "Tình yêu không cần phải hoàn hảo, chỉ cần sự chân thật. ",
        "Tôi là một ngọn lửa cháy lên, nhưng sẽ không thể bị lụi tàn. "
    };

    public void npcChat(Zone zone, String text) {
        Message msg;
        try {
            msg = new Message(124);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(text);
            Service.getInstance().sendMessAllPlayerInMap(zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void npcChat(String text) {
        Message msg;
        try {
            msg = new Message(124);
            msg.writer().writeShort(tempId);
            msg.writer().writeUTF(text);
            for (Zone zone : map.zones) {
                Service.getInstance().sendMessAllPlayerInMap(zone, msg);
            }
            msg.cleanup();
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void openShopLearnSkill(Player player, int shopId, int order) {
        ShopService.gI().openShopLearnSkill(player, this, shopId, order, player.gender);
    }

    public void openShopWithGender(Player player, int shopId, int order) {
        ShopService.gI().openShopNormal(player, this, shopId, order, player.gender);
    }

    public void openShop(Player player, int shopId, int order) {
        ShopService.gI().openShopNormal(player, this, shopId, order, -1);
    }

    public boolean canOpenNpc(Player player) {
        if (this.tempId == ConstNpc.DAU_THAN) {
            if (player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23) {
                return true;
            } else {
                Service.getInstance().hideWaitDialog(player);
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                return false;
            }
        }
        if (player.zone.map.mapId == this.mapId
                && Util.getDistance(this.cx, this.cy, player.location.x, player.location.y) <= 60) {
            return true;
        } else {
            Service.getInstance().hideWaitDialog(player);
            Service.getInstance().sendThongBao(player, "Không thể thực hiện khi đứng quá xa");
            return false;
        }
    }

}
