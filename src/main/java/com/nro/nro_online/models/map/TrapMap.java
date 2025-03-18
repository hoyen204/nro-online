package com.nro.nro_online.models.map;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.func.EffectMapService;
import com.nro.nro_online.utils.Util;

public class TrapMap {
    public int x;
    public int y;
    public int w;
    public int h;
    public int effectId;
    public int dame;

    private static final int AN_XIEN_EFFECT_ID = 49; // Hiệu ứng ăn xiên, hardcode cho vui 😜
    private static final int COOLDOWN_AN_XIEN = 1000; // 1 giây cooldown, nhanh gọn lẹ!

    public void doPlayer(Player player) {
        if (this.effectId != AN_XIEN_EFFECT_ID) return; // Chỉ xử lý effect 49, kệ mấy cái khác 😅

        if (player.isDie() || player.isMiniPet || !Util.canDoWithTime(player.lastTimeAnXienTrapBDKB, COOLDOWN_AN_XIEN)) {
            return; // Chết, là pet, hoặc chưa hết cooldown thì nghỉ nha! 😛
        }

        int damageVariation = Util.nextInt(-10, 10) * dame / 100; // Biến động dame, random cho drama 😂
        player.injured(null, dame + damageVariation, false, false);
        PlayerService.gI().sendInfoHp(player); // Cập nhật HP ngay, không chờ ai hết! ⚡
        EffectMapService.gI().sendEffectMapToAllInMap(player.zone, effectId, 2, 1, player.location.x - 32, 1040, 1);
        player.lastTimeAnXienTrapBDKB = System.currentTimeMillis(); // Đánh dấu thời gian, xong việc rồi!
    }
}