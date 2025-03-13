package com.nro.nro_online.models.dragon;

import java.io.DataOutputStream;
import com.nro.nro_online.dialog.MenuDialog;
import com.nro.nro_online.dialog.MenuRunnable;
import com.nro.nro_online.manager.NamekBallManager;
import com.nro.nro_online.models.clan.Buff;
import com.nro.nro_online.models.clan.Clan;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.Service;

/**
 * @build by arriety
 */
public class Poruga extends AbsDragon {
    private static final String[] WISHES = {"Tăng 20%\nsức đánh", "Tăng 20% HP", "Tăng 20% KI", "Tăng 10%\nchí mạng"};
    private static final String CONTENT = "Ta sẽ ban cho ngươi điều ước, ngươi có 5 phút, hãy suy nghĩ thật kĩ trước khi quyết định, tác dụng của chúc phúc sẽ có hiệu lực đến 6h AM";

    public Poruga(Player player) {
        super(player);
        setWishes(WISHES);
        setTutorial("");
        setContent(CONTENT);
        setName("Rồng thần Namek");
    }

    @Override
    public void openMenu() {
        // Empty for now, ready for action later! 😛
    }

    @Override
    public void summon() {
        setAppear(true);
        callDragon();
        showWishes();
        setLastTimeAppear(System.currentTimeMillis());
        new Thread(this).start();
        sendNotify();
    }

    @Override
    public void reSummon() {
        // Placeholder for future dragon vibes! 🐉
    }

    @Override
    public void showWishes() {
        Clan clan = getSummoner().clan;
        MenuRunnable wishHandler = new MenuRunnable() {
            @Override
            public void run() {
                Buff buff;
                switch (getIndexSelected()) {
                case 0 -> buff = Buff.BUFF_ATK;
                case 1 -> buff = Buff.BUFF_HP;
                case 2 -> buff = Buff.BUFF_KI;
                case 3 -> buff = Buff.BUFF_CRIT;
                default -> { return; } // Invalid choice, bail out! 😬
                }
                clan.setBuff(buff);
                Service service = Service.getInstance();
                for (Player player : clan.membersInGame) {
                    player.setBuff(buff);
                    service.point(player);
                    service.sendThongBao(player, "Bạn vừa nhận được chúc phúc của rồng thần Poruga");
                }
                leave();
            }
        };
        new MenuDialog(CONTENT, WISHES, wishHandler).show(getSummoner());
    }

    @Override
    public void callDragon() {
        try (Message msg = new Message(-83);
                DataOutputStream ds = msg.writer()) {
            boolean appear = isAppear();
            ds.writeByte(appear ? 0 : 1);
            if (appear) {
                Zone z = getSummoner().zone;
                ds.writeShort(z.map.mapId);
                ds.writeShort(z.map.bgId);
                ds.writeByte(z.zoneId);
                ds.writeInt((int) getSummonerID());
                ds.writeUTF("");
                ds.writeShort(getSummoner().location.x);
                ds.writeShort(getSummoner().location.y);
                ds.writeByte(1);
            }
            Service.getInstance().sendMessAllPlayer(msg);
        } catch (Exception e) {
            System.err.println("Dragon call crashed, F in chat: " + e.getMessage());
        }
    }

    @Override
    public void leave() {
        NpcService.gI().createTutorial(getSummoner(), -1, "Điều ước của ngươi đã thành hiện thực\nHẹn gặp lại, ta đi ngủ đây, bái bai!");
        setAppear(false);
        callDragon();
        NamekBallManager.gI().initFossil();
    }
}