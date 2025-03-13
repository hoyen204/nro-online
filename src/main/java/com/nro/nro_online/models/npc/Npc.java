package com.nro.nro_online.models.npc;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ShopService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public abstract class Npc implements IAtionNpc {

    public int mapId;
    public Map map;
    public int status;
    public int cx;
    public int cy;
    public int tempId;
    public int avartar;
    public BaseMenu baseMenu;
    private final Message message = new Message(32);
    private final Message chatMessage = new Message(124);

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
        try {
            player.iDMark.setIndexMenu(indexMenu);
            message.writer().writeShort(tempId);
            message.writer().writeUTF(npcSay);
            message.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                message.writer().writeUTF(menu);
            }
            player.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createOtherMenu(Player player, int indexMenu, String npcSay, String[] menuSelect, Object object) {
        NpcFactory.PLAYERID_OBJECT.put(player.id, object);
        try {
            player.iDMark.setIndexMenu(indexMenu);
            message.writer().writeShort(tempId);
            message.writer().writeUTF(npcSay);
            message.writer().writeByte(menuSelect.length);
            for (String menu : menuSelect) {
                message.writer().writeUTF(menu);
            }
            player.sendMessage(message);
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
                    message.writer().writeShort(tempId);
                    message.writer().writeUTF("Bạn gặp tôi có việc gì vậy?");
                    message.writer().writeByte(1);
                    message.writer().writeUTF("Đóng");
                    player.sendMessage(message);
                }
            } catch (Exception e) {
                Log.error(Npc.class, e);
            }
        }
    }

    public void npcChat(Player player, String text) {
        try {
            chatMessage.writer().writeShort(tempId);
            chatMessage.writer().writeUTF(text);
            player.sendMessage(chatMessage);
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public long LastTimeAutoChat = 0;

    public void AutoChat() {
        try {
            if (tempId == 13 && System.currentTimeMillis() - LastTimeAutoChat > 5000) {
                LastTimeAutoChat = System.currentTimeMillis();
                for (Player pl : Client.gI().getPlayers()) {
                    if (pl != null && pl.zone != null && pl.zone.map != null && pl.zone.map.mapId == mapId && pl.isPl()) {
                        npcChat(pl, getText(tempId));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getText(int id) {
        if (id == 13) {
            return textQuyLao[Util.nextInt(0, textQuyLao.length - 1)];
        }
        return "";
    }

    private static final String[] textQuyLao = new String[]{
            "Lá Là La...",
            "Ngày tươi đẹp nhất là ngày được may mắn nhìn thấy em",
            "Tình yêu không cần phải hoàn hảo, chỉ cần sự chân thật. ",
            "Tôi là một ngọn lửa cháy lên, nhưng sẽ không thể bị lụi tàn. "
    };

    public void npcChat(Zone zone, String text) {
        try {
            chatMessage.writer().writeShort(tempId);
            chatMessage.writer().writeUTF(text);
            Service.getInstance().sendMessAllPlayerInMap(zone, chatMessage);
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void npcChat(String text) {
        try {
            chatMessage.writer().writeShort(tempId);
            chatMessage.writer().writeUTF(text);
            for (Zone zone : map.zones) {
                Service.getInstance().sendMessAllPlayerInMap(zone, chatMessage);
            }
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
            int playerMapId = player.zone.map.mapId;
            return playerMapId == 21 || playerMapId == 22 || playerMapId == 23;
        }
        return player.zone.map.mapId == this.mapId
                && Util.getDistance(this.cx, this.cy, player.location.x, player.location.y) <= 60;
    }
}