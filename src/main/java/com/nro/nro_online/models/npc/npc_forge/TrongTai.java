package com.nro.nro_online.models.npc.npc_forge;

import java.sql.Timestamp;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.manager.SieuHangManager;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SieuHangService;
import com.nro.nro_online.services.func.ChangeMapService;

public class TrongTai extends Npc {

    private static final int CLONE_COOLDOWN_MINUTES = 5;

    public TrongTai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        int turn = SieuHangManager.GetFreeTurn(player);
        String message = "Đại hội võ thuật Siêu hạng diễn ra 24/7, kể cả ngày lễ và chủ nhật.\n"
                + "Hãy thi đấu ngay để khẳng định đẳng cấp của mình nhé.";

        String[] options = turn == 0
                ? new String[] { "Top 100\nCao thủ", "Hướng\ndẫn\nthêm", "Ưu tiên\nđấu ngay", "Tạo bản sao siêu hạng",
                        "Về\nĐại Hội\nVõ Thuật" }
                : new String[] { "Top 100\nCao thủ", "Hướng\ndẫn\nthêm", "Miễn phí\nCòn " + turn + " vé",
                        "Ưu tiên\nđấu ngay", "Lưu\ntrạng thái\nchiến đấu", "Về\nĐại Hội\nVõ Thuật" };

        createOtherMenu(player, ConstNpc.BASE_MENU, message, options);
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        int turn = SieuHangManager.GetFreeTurn(player);
        if (turn == 0 && select >= 2)
            select++; // Adjust selection for no free turn scenario

        switch (select) {
            case 0 -> handleShowTop(player);
            case 1 -> handleTutorial(player);
            case 2 -> handleFreeTurn(player, turn);
            case 3 -> handlePriorityMatch(player);
            case 4 -> handleCreateClone(player);
            case 5 -> handleChangeMap(player);
        }
    }

    private void handleShowTop(Player player) {
        SieuHangService.ShowTop(player, 0);
    }

    private void handleTutorial(Player player) {
        String tutorialText = """
                Giải đấu thể hiện đẳng cấp thực sự.
                Các trận đấu diễn ra liên tục bất kể ngày đêm.
                Bạn hãy tham gia thi đấu để nâng hạng và nhận giải thưởng khủng nhé.

                Cơ cấu giải thưởng như sau:
                - Top 1: 100 ngọc
                - Top 2-10: 20 ngọc
                - Top 11-100: 5 ngọc
                - Top 101-1000: 1 ngọc

                Mỗi ngày bạn nhận 1 vé miễn phí (tối đa 3 vé). Khi thua sẽ mất 1 vé.
                Hết vé? Bạn phải trả 1 ngọc để đấu tiếp (trừ ngọc sau trận đấu).

                Bạn không thể thi đấu với đối thủ có hạng thấp hơn mình.
                Chúc bạn may mắn, đoàn kết và quyết thắng!
                """;
        NpcService.gI().createTutorial(player, ConstNpc.TRONG_TAI, -1, tutorialText);
    }

    private void handleFreeTurn(Player player, int turn) {
        if (turn <= 0) {
            Service.getInstance().sendThongBao(player, "Bạn đã hết lượt miễn phí");
        } else {
            SieuHangService.ShowTop(player, 1);
        }
    }

    private void handlePriorityMatch(Player player) {
        SieuHangService.ShowTop(player, 1);
    }

    private void handleCreateClone(Player player) {
        Timestamp lastModifiedTime = SieuHangManager.GetLastTimeCreateClone(player);
        if (lastModifiedTime == null)
            return;

        long elapsedMinutes = (System.currentTimeMillis() - lastModifiedTime.getTime()) / (60 * 1000);
        if (elapsedMinutes > CLONE_COOLDOWN_MINUTES) {
            SieuHangManager.CreateClone(player);
            Service.getInstance().sendThongBao(player, "Tạo bản sao thành công");
        } else {
            Service.getInstance().sendThongBao(player, "5 phút mới có thể lưu bản sao 1 lần");
        }
    }

    private void handleChangeMap(Player player) {
        ChangeMapService.gI().changeMapNonSpaceship(player, ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
    }
}
