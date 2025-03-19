package com.nro.nro_online.models.npc.specialnpc;

import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;

public class EggLinhThu extends SpecialEgg {
    private static final long DEFAULT_TIME_DONE = 1_209_600_000L; // 14 ngày, lâu phết 😜
    private static final short EGG_ICON = 15074; // Icon trứng Linh Thú, xịn lắm!
    private static final int[] LINH_THU_IDS = {2014, 2015, 2016, 2017, 2018};

    public EggLinhThu(Player player, long lastTimeCreate, long timeDone) {
        super(player, lastTimeCreate, timeDone);
    }

    public static void createEggLinhThu(Player player) {
        player.egglinhthu = new EggLinhThu(player, System.currentTimeMillis(), DEFAULT_TIME_DONE);
        player.egglinhthu.sendEgg();
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
        if (InventoryService.gI().getCountEmptyBag(this.player) == 0) {
            Service.getInstance().sendThongBao(player, "Hành trang full rồi bro, dọn đi nha! 😛");
            return;
        }
        try {
            destroyEgg();
            Thread.sleep(4000); // Chờ chút cho hồi hộp 😂
            Item linhThu = ItemService.gI().createNewItem((short) LINH_THU_IDS[Util.nextInt(LINH_THU_IDS.length)]);
            addLinhThuOptions(linhThu);
            InventoryService.gI().addItemBag(player, linhThu, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Chúc mừng bro nhận được Linh Thú " + linhThu.template.name + "! 🎉");
            ChangeMapService.gI().changeMapInYard(this.player, this.player.gender * 7, -1, Util.nextInt(300, 500));
        } catch (Exception e) {
        }
    }

    @Override
    protected String getEggName() {
        return "egglinhthu";
    }

    private void addLinhThuOptions(Item linhThu) {
        switch (linhThu.template.id) {
        case 2014: // Hoa
            linhThu.itemOptions.add(new ItemOption(50, Util.nextInt(7, 21)));
            linhThu.itemOptions.add(new ItemOption(168, 0));
            break;
        case 2015: // Lửa
            linhThu.itemOptions.add(new ItemOption(94, Util.nextInt(7, 21)));
            linhThu.itemOptions.add(new ItemOption(192, 0));
            break;
        case 2016: // Gió
            linhThu.itemOptions.add(new ItemOption(77, Util.nextInt(7, 21)));
            linhThu.itemOptions.add(new ItemOption(80, Util.nextInt(21, 30)));
            break;
        case 2017: // Đất
            linhThu.itemOptions.add(new ItemOption(108, Util.nextInt(7, 21)));
            linhThu.itemOptions.add(new ItemOption(111, 0));
            break;
        case 2018: // Nước
            linhThu.itemOptions.add(new ItemOption(103, Util.nextInt(7, 21)));
            linhThu.itemOptions.add(new ItemOption(173, Util.nextInt(21, 30)));
            break;
        }
    }
}