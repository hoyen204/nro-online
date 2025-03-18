package com.nro.nro_online.models.npc.specialnpc;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.PetService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class MabuEgg extends SpecialEgg {
    private static final long DEFAULT_TIME_DONE = 86_400_000L; // 1 ngày, nhanh gọn 😜
    private static final short EGG_ICON = 4664; // Icon trứng Mabu, đẹp trai lắm!

    public MabuEgg(Player player, long lastTimeCreate, long timeDone) {
        super(player, lastTimeCreate, timeDone);
    }

    public static void createMabuEgg(Player player) {
        player.mabuEgg = new MabuEgg(player, System.currentTimeMillis(), DEFAULT_TIME_DONE);
        player.mabuEgg.sendEgg();
    }

    @Override
    protected long getDefaultTimeDone() {
        return DEFAULT_TIME_DONE;
    }

    @Override
    protected short getEggIcon() {
        return EGG_ICON;
    }

    @Override
    protected void openEggInternal(int gender) {
        if (this.player.pet == null) {
            Service.getInstance().sendThongBao(player, "Bro ơi, cần đệ tử để nở trứng nha! 😛");
            return;
        }
        try {
            destroyEgg();
            Thread.sleep(4000); // Chờ chút cho drama 😂
            if (this.player.pet == null) {
                PetService.gI().createMabuPet(this.player, gender);
            } else {
                PetService.gI().changeMabuPet(this.player, gender);
            }
            ChangeMapService.gI().changeMapInYard(this.player, this.player.gender * 7, -1, Util.nextInt(300, 500));
        } catch (Exception e) {
            // Lỗi thì kệ, chill đi 😅
        }
    }

    @Override
    protected String getEggName() {
        return "mabuEgg";
    }
}