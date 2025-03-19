package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.map.war.BlackBallWar;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Log;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

public class RongOmega extends Npc {

    public RongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player) || !isValidMap(this.mapId))
            return;

        try {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(BlackBallWar.TIME_OPEN) && now.isBefore(BlackBallWar.TIME_CLOSE)) {
                createOtherMenu(player, ConstNpc.MENU_OPEN_BDW,
                        "Đường đến với ngọc rồng sao đen đã mở, ngươi có muốn tham gia không?",
                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
            } else {
                showRewardOrDefaultMenu(player);
            }
        } catch (Exception ex) {
            Log.error("Lỗi mở menu rồng Omega");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.MENU_REWARD_BDW -> player.rewardBlackBall.getRewardSelect((byte) select);
            case ConstNpc.MENU_OPEN_BDW -> handleOpenBdwMenu(player, select);
            case ConstNpc.MENU_NOT_OPEN_BDW -> {
                if (select == 0)
                    showTutorial(player);
            }
        }
    }

    private boolean isValidMap(int mapId) {
        return mapId == 24 || mapId == 25 || mapId == 26;
    }

    private void showRewardOrDefaultMenu(Player player) {
        String[] rewardOptions = getAvailableRewards(player);
        if (rewardOptions.length > 0) {
            String[] options = Arrays.copyOf(rewardOptions, rewardOptions.length + 1);
            options[options.length - 1] = "Từ chối";
            createOtherMenu(player, ConstNpc.MENU_REWARD_BDW,
                    "Ngươi có một vài phần thưởng ngọc rồng sao đen đây!", options);
        } else {
            createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                    "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
        }
    }

    private String[] getAvailableRewards(Player player) {
        return Arrays.stream(player.rewardBlackBall.timeOutOfDateReward)
                .filter(time -> time > LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .mapToInt(i -> Arrays.asList(player.rewardBlackBall.timeOutOfDateReward).indexOf(i) + 1)
                .mapToObj(i -> "Nhận thưởng\n" + i + " sao")
                .toArray(String[]::new);
    }

    private void handleOpenBdwMenu(Player player, int select) {
        if (select == 0) {
            showTutorial(player);
        } else if (select == 1) {
            player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
            ChangeMapService.gI().openChangeMapTab(player);
        }
    }

    private void showTutorial(Player player) {
        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
    }
}