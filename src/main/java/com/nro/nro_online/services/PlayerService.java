package com.nro.nro_online.services;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.consts.ConstAchive;
import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.jdbc.daos.AccountDAO;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.models.dragon_namec_war.TranhNgocService;
import com.nro.nro_online.models.player.PetFollow;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class PlayerService {

    private static PlayerService i;

    public PlayerService() {
    }

    public static PlayerService gI() {
        if (i == null) {
            i = new PlayerService();
        }
        return i;
    }

    public void dailyLogin(Player player) {
        LocalDateTime now = LocalDateTime.now();
        if (Util.compareDay(now, player.firstTimeLogin)) {
            player.goldChallenge = 5_000;
            player.levelWoodChest = 0;
            player.receivedWoodChest = false;
            player.event.setReceivedLuckyMoney(false);
            player.setRewardLimit(new byte[player.getRewardLimit().length]);
            player.buyLimit = new byte[player.buyLimit.length];
            player.firstTimeLogin = now;
            player.canGetFirstTimeLogin = true;
        }
    }

    public void sendTNSM(Player player, byte type, long param) {
        if (param > 0) {
            try (Message msg = new Message(-3)) {
                msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
                msg.writer().writeInt((int) param);// số tn cần cộng
                player.sendMessage(msg);
            } catch (IOException e) {
                Log.error(this.getClass(), e, "Lỗi send tnsm " + player.id);
            }
        }
        if (param < 0) {
            try (Message msg = new Message(-3)) {
                msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
                msg.writer().writeInt((int) param);// số tn cần cộng
                player.sendMessage(msg);
            } catch (IOException e) {
                Log.error(this.getClass(), e, "Lỗi send tnsm " + player.id);
            }
        }
    }

    public void sendMessageAllPlayer(Message msg) {
        List<Player> fixPlayers = new ArrayList<>(Client.gI().getPlayers());
        for (Player pl : fixPlayers) {
            if (pl != null) {
                pl.sendMessage(msg);
            }
        }
    }

    public void sendMessageIgnore(Player plIgnore, Message msg) {
        for (Player pl : Client.gI().getPlayers()) {
            if (pl != null && !pl.equals(plIgnore)) {
                pl.sendMessage(msg);
            }
        }
    }

    public void sendInfoHp(Player player) {
        try (Message msg = Service.getInstance().messageSubCommand((byte) 5)) {
            msg.writer().writeInt(player.nPoint.hp);
            player.sendMessage(msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi send info hp " + player.id);
        }
    }

    public void sendInfoMp(Player player) {
        try (Message msg = Service.getInstance().messageSubCommand((byte) 6)) {
            msg.writer().writeInt(player.nPoint.mp);
            player.sendMessage(msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi send info mp " + player.id);
        }
    }

    public void sendInfoHpMp(Player player) {
        sendInfoHp(player);
        sendInfoMp(player);
    }

    public void hoiPhuc(Player player, int hp, int mp) {
        if (!player.isDie()) {
            player.nPoint.addHp(hp);
            player.nPoint.addMp(mp);
            Service.getInstance().Send_Info_NV(player);
            if (!player.isPet) {
                PlayerService.gI().sendInfoHpMp(player);
            }
        }
    }

    public void sendInfoHpMpMoney(Player player) {
        Message msg;
        try {
            long gold = player.inventory.getGoldDisplay();
            msg = Service.getInstance().messageSubCommand((byte) 4);
            if (player.isVersionAbove(214)) {
                msg.writer().writeLong(gold);// xu
            } else {
                msg.writer().writeInt((int) gold);// xu
            }
            msg.writer().writeInt(player.inventory.gem);// luong
            msg.writer().writeInt(player.nPoint.hp);// chp
            msg.writer().writeInt(player.nPoint.mp);// cmp
            msg.writer().writeInt(player.inventory.ruby);// ruby
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(PlayerService.class, e);
        }
    }

    public void playerMove(Player player, int x, int y) {
        if (player.zone == null) {
            return;
        }
        player.zone.playerMove(player, x, y);
    }

    public void sendCurrentStamina(Player player) {
        try (Message msg = new Message(-68)) {
            msg.writer().writeShort(player.nPoint.stamina);
            player.sendMessage(msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi send current stamina " + player.id);
        }
    }

    public void sendMaxStamina(Player player) {
        try (Message msg = new Message(-69)) {
            msg.writer().writeShort(player.nPoint.maxStamina);
            player.sendMessage(msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi send max stamina " + player.id);
        }
    }

    public void changeAndSendTypePK(Player player, int type) {
        changeTypePK(player, type);
        sendTypePk(player);
    }

    public void changeTypePK(Player player, int type) {
        player.typePk = (byte) type;
    }

    public void sendTypePk(Player player) {
        try (Message msg = Service.getInstance().messageSubCommand((byte) 35)) {
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(player.typePk);
            Service.getInstance().sendMessAllPlayerInMap(player.zone, msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi send type pk " + player.id);
        }
    }

    public void banPlayer(Player playerBaned) {
        AccountDAO.banAccount(playerBaned.getSession());
        Service.getInstance().sendThongBao(playerBaned,
                "Tài khoản của bạn đã bị khóa\nGame sẽ mất kết nối sau 5 giây...");
        playerBaned.lastTimeBan = System.currentTimeMillis();
        playerBaned.isBan = true;
    }

    private static final int COST_GOLD_HOI_SINH = 20_000_000;

    public void hoiSinh(Player player) {
        if (player.isDie() && player.zone != null) {
            if (MapService.gI().isMapMabuWar14H(player.zone.map.mapId)) {
                Service.getInstance().sendThongBao(player, "You can not hoi sinh");
                return;
            }
            boolean canHs = false;
            if (MapService.gI().isMapBlackBallWar(player.zone.map.mapId)
                    || MapService.gI().isMapMabuWar(player.zone.map.mapId)) {
                if (player.inventory.gold >= COST_GOLD_HOI_SINH) {
                    player.inventory.gold -= COST_GOLD_HOI_SINH;
                    canHs = true;
                } else {
                    Service.getInstance().sendThongBao(player,
                            "Không đủ vàng để thực hiện, còn thiếu " + Util.numberToMoney(COST_GOLD_HOI_SINH
                                    - player.inventory.gold) + " vàng");
                    return;
                }
            }
            if (!canHs) {
                if (player.inventory.gem > 1) {
                    player.inventory.gem -= 1;
                    canHs = true;
                } else {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc xanh để hồi sinh");
                }
            }
            if (canHs) {
                Service.getInstance().sendMoney(player);
                Service.getInstance().hsChar(player, player.nPoint.hpMax, player.nPoint.mpMax);
                if (player.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
                    TranhNgocService.getInstance().sendUpdateLift(player);
                }
                player.playerTask.achievements.get(ConstAchive.THANH_HOI_SINH).count++;
            }
        }
    }

    public boolean createPlayer(Connection con, int userId, String name, int gender, int hair, PreparedStatement ps) {
        PlayerDAO.createNewPlayer(con, userId, name, (byte) gender, hair, ps);
        return true;
    }

    public boolean savePlayer(Player player) {
        try {
            PlayerDAO.updateTimeLogout = true;
            PlayerDAO.updatePlayer(player, DBService.gI().getConnectionForLogout());
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void setPos(Player player, int x, int y, int effID) {
        try (Message msg = new Message(Cmd.SET_POS)) {
            DataOutputStream ds = msg.writer();
            ds.writeInt((int) player.id);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.writeByte(effID);
            ds.flush();
            player.sendMessage(msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi set pos " + player.id);
        }
    }

    public void sendPetFollow(Player player) {
        PetFollow pet = player.getPetFollow();
        int type = 1;
        if (pet == null) {
            type = 0;
        }
        try (Message msg = new Message(Cmd.STATUS_PET)) {
            DataOutputStream ds = msg.writer();
            ds.writeInt((int) player.id);
            ds.writeByte(type);
            if (type == 1) {
                ds.writeShort(pet.getIconID());
                ds.writeByte(1);
                byte nFrames = pet.getNFrame();
                ds.writeByte(nFrames);
                for (int i = 0; i < nFrames; i++) {
                    ds.writeByte(i);
                }
                ds.writeShort(pet.getWidth());
                ds.writeShort(pet.getHeight());
            }
            ds.flush();
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi send pet follow " + player.id);
        }
    }

    public void sendPetFollow(Player me, Player info) {
        PetFollow pet = info.getPetFollow();
        int type = 1;
        if (pet == null) {
            type = 0;
        }
        try (Message msg = new Message(Cmd.STATUS_PET)) {
            DataOutputStream ds = msg.writer();
            ds.writeInt((int) info.id);
            ds.writeByte(type);
            if (type == 1) {
                ds.writeShort(pet.getIconID());
                ds.writeByte(1);
                byte nFrames = pet.getNFrame();
                ds.writeByte(nFrames);
                for (int i = 0; i < nFrames; i++) {
                    ds.writeByte(i);
                }
                ds.writeShort(pet.getWidth());
                ds.writeShort(pet.getHeight());
            }
            ds.flush();
            me.sendMessage(msg);
        } catch (IOException e) {
            Log.error(PlayerService.class, e, "Lỗi send pet follow " + info.id);
        }
    }
}
