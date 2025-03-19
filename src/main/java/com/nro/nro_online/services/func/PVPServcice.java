package com.nro.nro_online.services.func;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstTranhNgocNamek;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.pvp.ChallengePVP;
import com.nro.nro_online.models.pvp.PVP;
import com.nro.nro_online.models.pvp.RevengePVP;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PVPServcice implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(PVPServcice.class.getName());
    private static final int[] GOLD_CHALLENGE = { 1_000_000, 10_000_000, 100_000_000 };
    private static final byte OPEN_GOLD_SELECT = 0;
    private static final byte ACCEPT_PVP = 1;
    private static final long MIN_POWER = 40_000_000_000L;
    private static final int REVENGE_RUBY_COST = 10;

    private final String[] optionsGoldChallenge;
    private final List<PVP> pvps = Collections.synchronizedList(new ArrayList<>());
    private final Map<Player, Player> playerVsPlayer = new ConcurrentHashMap<>();
    private final Map<Player, PVP> playerPvp = new ConcurrentHashMap<>();
    private final Map<Player, Integer> playerGold = new ConcurrentHashMap<>();

    private static final PVPServcice INSTANCE = new PVPServcice();

    private PVPServcice() {
        optionsGoldChallenge = Arrays.stream(GOLD_CHALLENGE)
                .mapToObj(Util::numberToMoney)
                .map(gold -> gold + " vàng")
                .toArray(String[]::new);
    }

    public static PVPServcice gI() {
        return INSTANCE;
    }

    public void controller(Player player, Message message) {
        try (var reader = message.reader()) {
            byte action = reader.readByte();
            byte type = reader.readByte();
            int playerId = reader.readInt();
            Player opponent = player.zone.getPlayerInMap(playerId);
            if (opponent == null)
                return;

            playerVsPlayer.put(player, opponent);
            playerVsPlayer.put(opponent, player);

            if (action == OPEN_GOLD_SELECT)
                openSelectGold(player, opponent);
            else if (action == ACCEPT_PVP)
                acceptPVP(player);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing PVP controller", e);
        }
    }

    private void openSelectGold(Player player, Player opponent) {
        if (isPlayerInPvp(player) || isPlayerInPvp(opponent)) {
            Service.getInstance().hideWaitDialog(player);
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        NpcService.gI().createMenuConMeo(player, ConstNpc.MAKE_MATCH_PVP, -1,
                opponent.name + " (sức mạnh " + Util.numberToMoney(opponent.nPoint.power)
                        + ")\nBạn muốn cược bao nhiêu vàng?",
                optionsGoldChallenge);
    }

    public void sendInvitePVP(Player player, byte selectGold) {
        if (player.nPoint.power < MIN_POWER) {
            Service.getInstance().sendThongBaoFromAdmin(player,
                    "|5|Bạn chưa đủ 40 tỷ để có thể sử dụng chức năng thách đấu");
            return;
        }

        Player opponent = playerVsPlayer.get(player);
        if (opponent == null || opponent.nPoint.power < MIN_POWER) {
            Service.getInstance().sendThongBao(player,
                    opponent == null ? "Đối thủ không tồn tại" : "Đối thủ chưa đủ sức mạnh!");
            return;
        }

        int gold = GOLD_CHALLENGE[selectGold];
        if (player.inventory.gold < gold) {
            Service.getInstance().sendThongBao(player,
                    "Bạn chỉ có " + Util.numberToMoney(player.inventory.gold) + " vàng, không đủ tiền cược");
            return;
        }
        if (opponent.inventory.gold < gold) {
            Service.getInstance().sendThongBao(player,
                    "Đối thủ chỉ có " + Util.numberToMoney(opponent.inventory.gold) + " vàng, không đủ tiền cược");
            return;
        }

        playerGold.put(player, gold);
        try (Message msg = new Message(-59)) {
            msg.writer().writeByte(3);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(gold);
            msg.writer().writeUTF(player.name + " (sức mạnh " + Util.numberToMoney(player.nPoint.power)
                    + ") muốn thách đấu bạn với mức cược " + gold);
            opponent.sendMessage(msg);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending PVP invite", e);
        }
    }

    private void acceptPVP(Player player) {
        Player opponent = playerVsPlayer.get(player);
        if (opponent == null || !player.zone.equals(opponent.zone)) {
            Service.getInstance().sendThongBao(player,
                    opponent == null ? "Đối thủ không tồn tại" : "Đối thủ đã rời khỏi map");
            return;
        }
        if (isPlayerInPvp(player) || isPlayerInPvp(opponent)) {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }

        int gold = playerGold.getOrDefault(opponent, 0);
        if (player.inventory.gold < gold || opponent.inventory.gold < gold)
            return;

        ChallengePVP pvp = new ChallengePVP(player, opponent);
        pvp.gold = gold;
        pvps.add(pvp);
        playerPvp.put(player, pvp);
        playerPvp.put(opponent, pvp);
        pvp.start();
    }

    public PVP findPvp(Player player) {
        return playerPvp.get(player);
    }

    public void removePVP(PVP pvp) {
        if (pvp == null)
            return;
        Optional.ofNullable(pvp.player1).ifPresent(p -> removePlayerData(p));
        Optional.ofNullable(pvp.player2).ifPresent(p -> removePlayerData(p));
        pvps.remove(pvp);
    }

    private void removePlayerData(Player player) {
        playerVsPlayer.remove(player);
        playerGold.remove(player);
        playerPvp.remove(player);
    }

    public void finishPVP(Player loser, byte typeLose) {
        PVP pvp = findPvp(loser);
        if (pvp != null)
            pvp.finishPVP(loser, typeLose);
    }

    public void openSelectRevenge(Player player, Player enemy) {
        if (isPlayerInPvp(player) || isPlayerInPvp(enemy)) {
            Service.getInstance().hideWaitDialog(player);
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        playerVsPlayer.put(player, enemy);
        NpcService.gI().createMenuConMeo(player, ConstNpc.REVENGE, -1,
                "Bạn muốn đến ngay chỗ hắn, phí là 10 hồng ngọc và được tìm thoải mái trong 5 phút nhé", "Ok",
                "Từ chối");
    }

    public void acceptRevenge(Player player) {
        if (player.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
            Service.getInstance().sendPopUpMultiLine(player, 0, 7184, "Không thể thực hiện");
            return;
        }
        if (player.inventory.getRuby() < REVENGE_RUBY_COST) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu 10 hồng ngọc nữa");
            return;
        }

        Player enemy = playerVsPlayer.get(player);
        if (enemy == null || enemy.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
            Service.getInstance().sendPopUpMultiLine(player, 0, 7184, "Không thể thực hiện");
            return;
        }

        Zone targetZone = ChangeMapService.gI().checkMapCanJoin(player, enemy.zone);
        if (targetZone == null || targetZone.isFullPlayer()) {
            Service.getInstance().sendThongBao(player, "Không thể tới khu vực này, vui lòng đợi sau ít phút");
            return;
        }

        player.inventory.subRuby(REVENGE_RUBY_COST);
        RevengePVP pvp = new RevengePVP(player, enemy);
        playerPvp.put(player, pvp);
        playerPvp.put(enemy, pvp);
        pvps.add(pvp);
        ChangeMapService.gI().changeMap(player, targetZone, enemy.location.x + Util.nextInt(-5, 5), enemy.location.y);
        pvp.lastTimeGoToMapEnemy = System.currentTimeMillis();
        pvp.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                long start = System.currentTimeMillis();
                pvps.forEach(PVP::update);
                Thread.sleep(Math.max(0, 1000 - (System.currentTimeMillis() - start)));
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "PVP service thread interrupted", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean isPlayerInPvp(Player player) {
        return playerPvp.containsKey(player);
    }
}