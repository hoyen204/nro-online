package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.clan.ClanMember;
import com.nro.nro_online.models.map.phoban.DoanhTrai;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.DoanhTraiService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class LinhCanh extends Npc {

    public LinhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player))
            return;

        if (player.clan == null) {
            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                    "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
            return;
        }

        if (player.clan.getMembers().size() < 2) {
            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                    "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
            return;
        }

        ClanMember clanMember = player.clan.getClanMember((int) player.id);
        if (!checkRequirements(player, clanMember))
            return;

        if (!player.clan.haveGoneDoanhTrai && player.clan.timeOpenDoanhTrai != 0) {
            int timeLeft = TimeUtil.getSecondLeft(player.clan.timeOpenDoanhTrai, DoanhTrai.TIME_DOANH_TRAI / 1000);
            createOtherMenu(player, ConstNpc.MENU_VAO_DT,
                    "Bang hội của ngươi đang đánh trại độc nhãn\nThời gian còn lại là " + timeLeft
                            + ". Ngươi có muốn tham gia không?",
                    "Tham gia", "Không", "Hướng\ndẫn\nthêm");
        } else {
            handleNewDoanhTrai(player, clanMember);
        }
    }

    private boolean checkRequirements(Player player, ClanMember clanMember) {
        if (player.nPoint.dameg < 1_000) {
            NpcService.gI().createTutorial(player, avartar, "Bạn phải đạt 1k sức đánh gốc");
            return false;
        }
        int daysInClan = (int) (((System.currentTimeMillis() / 1000) - clanMember.joinTime) / 60 / 60 / 24);
        if (daysInClan < 2) {
            NpcService.gI().createTutorial(player, avartar,
                    "Chỉ những thành viên gia nhập bang hội tối thiểu 2 ngày mới có thể tham gia");
            return false;
        }
        return true;
    }

    private void handleNewDoanhTrai(Player player, ClanMember clanMember) {
        List<Player> plSameClans = getNearbyClanMembers(player);
        if (plSameClans.size() < 2) {
            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                    "Ngươi phải có ít nhất 2 đồng đội cùng bang đứng gần mới có thể\nvào\ntuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\nHahaha.",
                    "OK", "Hướng\ndẫn\nthêm");
            return;
        }

        if (!player.isAdmin() && clanMember.getNumDateFromJoinTimeToToday() < DoanhTrai.DATE_WAIT_FROM_JOIN_CLAN) {
            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                    "Bang hội chỉ cho phép những người ở trong bang trên 1 ngày. Hẹn ngươi quay lại vào lúc khác",
                    "OK", "Hướng\ndẫn\nthêm");
        } else if (player.clan.haveGoneDoanhTrai) {
            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                    "Bang hội của ngươi đã đi trại lúc " + Util.formatTime(player.clan.timeOpenDoanhTrai)
                            + " hôm nay. Người mở\n(" + player.clan.playerOpenDoanhTrai.name
                            + "). Hẹn ngươi quay lại vào ngày mai",
                    "OK", "Hướng\ndẫn\nthêm");
        } else {
            createOtherMenu(player, ConstNpc.MENU_CHO_VAO_DT,
                    "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\nkhông?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                    "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
        }
    }

    private List<Player> getNearbyClanMembers(Player player) {
        List<Player> plSameClans = new ArrayList<>();
        List<Player> playersMap = player.zone.getPlayers();
        synchronized (playersMap) {
            for (Player pl : playersMap) {
                if (!pl.equals(player) && pl.clan != null && pl.clan.id == player.clan.id
                        && pl.location.x >= 1285 && pl.location.x <= 1645) {
                    plSameClans.add(pl);
                }
            }
        }
        return plSameClans;
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || this.mapId != 27)
            return;

        switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.MENU_KHONG_CHO_VAO_DT -> {
                if (select == 1)
                    showTutorial(player);
            }
            case ConstNpc.MENU_CHO_VAO_DT -> {
                if (select == 0)
                    DoanhTraiService.gI().openDoanhTrai(player);
                else if (select == 2)
                    showTutorial(player);
            }
            case ConstNpc.MENU_VAO_DT -> {
                if (select == 0)
                    ChangeMapService.gI().changeMap(player, 53, 0, 35, 432);
                else if (select == 2)
                    showTutorial(player);
            }
        }
    }

    private void showTutorial(Player player) {
        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
    }
}