/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.npc.NpcForge;

import java.sql.Timestamp;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import nro.consts.ConstMap;
import nro.consts.ConstNpc;
import nro.manager.SieuHangManager;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.SieuHangService;
import nro.services.func.ChangeMapService;

/**
 *
 * @author by Arriety
 */
public class TrongTai extends Npc {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public TrongTai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        int turn = SieuHangManager.GetFreeTurn(player);

        if (turn == 0) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đại hội võ thuật Siêu hạng\ndiễn ra 24/7 kể cả ngày lễ và chủ nhật\nHãy thi đấu ngay để khẳng định đẳng cấp của mình nhé",
                    "Top 100\nCao thủ",
                    "Hướng\ndẫn\nthêm",
                    "Ưu tiên\nđấu ngay",
                    "Tạo bản sao siêu hạng",
                    "Về\nĐại Hội\nVõ Thuật");
        } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đại hội võ thuật Siêu hạng\ndiễn ra 24/7 kể cả ngày lễ và chủ nhật\nHãy thi đấu ngay để khẳng định đẳng cấp của mình nhé",
                    "Top 100\nCao thủ",
                    "Hướng\ndẫn\nthêm",
                    "Miễn phí\nCòn " + turn + " vé",
                    "Ưu tiên\nđấu ngay",
                    "Lưu\ntrạng thái\nchiến đấu",
                    "Về\nĐại Hội\nVõ Thuật");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            int turn = SieuHangManager.GetFreeTurn(player);
            if (turn == 0 && select >= 2) {
                select++;
            }
            switch (select) {
                case 0: {
                    SieuHangService.ShowTop(player, 0);
                    break;
                }
                case 1: {
                    NpcService.gI().createTutorial(player, ConstNpc.TRONG_TAI, -1,
                            "Giải đấu thể hiện đẳng cấp thực sự\bCác trận đấu diễn ra liên tục bất kể ngày đêm\bBạn hãy tham gia thi đấu để nâng hạng\bvà nhận giải thưởng khủng nhé\nCơ cấu giải thưởng như sau\b(chốt và trao giải ngẫu nhiên từ 20h-23h mỗi ngày)\bTop 1 thưởng 100 ngọc\bTop 2-10 thưởng 20 ngọc\bTop 11-100 thưởng 5 ngọc\bTop 101-1000 thưởng 1 ngọc\nMỗi ngày các bạn được tặng 1 vé tham dự miễn phí\b(tích lũy tối đa 3 vé) khi thua sẽ mất đi 1 vé\bKhi hết vé bạn phải trả 1 ngọc để đấu tiếp\b(trừ ngọc khi trận đấu kết thúc)\nBạn không thể thi đấu với đấu thủ\bcó hạng thấp hơn mình\bChúc bạn may mắn, chào đoàn kết và quyết thắng");
                    break;
                }
                case 2: {
                    if (turn <= 0) {
                        Service.getInstance().sendThongBao(player, "Bạn đã hết lượt miễn phí");
                    } else {
                        SieuHangService.ShowTop(player, 1);
                    }
                    break;
                }
                case 3: {
                    SieuHangService.ShowTop(player, 1);
                    break;
                }
                case 4: {
                    Timestamp lastModifiedTime = SieuHangManager.GetLastTimeCreateClone(player);

                    if (lastModifiedTime != null) {
                        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                        long millisecondsDifference = currentTime.getTime() - lastModifiedTime.getTime();
                        int minutesDifference = (int) (millisecondsDifference / (60 * 1000));

                        if (minutesDifference > 5) {
                            SieuHangManager.CreateClone(player);
                            Service.getInstance().sendThongBao(player, "Tạo bản sao thành công");
                        } else {
                            Service.getInstance().sendThongBao(player, "5p mới có thể lưu bản sao 1 lần");
                        }
                    }
                    break;
                }
                case 5: {
                    ChangeMapService.gI().changeMapNonSpaceship(player, ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
                    break;
                }
            }
        }
    }
}
