package com.nro.nro_online.models.item;

import com.nro.nro_online.models.player.NPoint;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.ItemTimeService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class ItemTime {
    // Constants
    public static final byte DOANH_TRAI = 0;
    public static final byte BAN_DO_KHO_BAU = 1;
    public static final byte TOP_DAME_ATTACK_BOSS = 2;

    public static final int TIME_ITEM = 600_000;
    public static final int TIME_OPEN_POWER = 86_400_000;
    public static final int TIME_MAY_DO = 1_800_000;
    public static final int TIME_EAT_MEAL = 600_000;

    private Player player;

    public boolean isUseBoHuyet2, isUseBoKhi2, isUseGiapXen2, isUseCuongNo2, isDuoiKhi;
    public boolean isMaTroi;
    public long lastTimeMaTroi;
    public int iconMaTroi;
    public boolean rateDragonHit;
    public long lastTimerateHit;
    public boolean rateDame;
    public long lastTimeDameDr;
    public boolean rateHPKI;
    public long lastTimerateHPKI;
    public boolean isBanhTrungThu1Trung, isBanhTrungThu2Trung;
    public long lastTimeBanhTrungThu1Trung, lastTimeBanhTrungThu2Trung;
    public long lastTimeBoHuyet2, lastTimeBoKhi2, lastTimeGiapXen2, lastTimeCuongNo2, lastTimeDuoiKhi;
    public boolean isUseBanhChung, isUseBanhTet;
    public long lastTimeBanhChung, lastTimeBanhTet;
    public boolean isUseBoHuyet, isUseBoKhi, isUseGiapXen, isUseCuongNo, isUseAnDanh;
    public long lastTimeBoHuyet, lastTimeBoKhi, lastTimeGiapXen, lastTimeCuongNo, lastTimeAnDanh;
    public boolean isUseMayDo;
    public long lastTimeUseMayDo;
    public boolean isMayDo;
    public long timeMayDo;
    public boolean isOpenPower;
    public long lastTimeOpenPower;
    public boolean isUseTDLT;
    public long lastTimeUseTDLT;
    public int timeTDLT;
    public boolean isEatMeal;
    public long lastTimeEatMeal;
    public int iconMeal;

    // Map để quản lý buff và tối ưu logic
    private final Map<String, BuffInfo> buffMap = new HashMap<>();

    public ItemTime(Player player) {
        this.player = player;
        initBuffMap();
    }

    private void initBuffMap() {
        buffMap.put("BoHuyet", new BuffInfo(() -> isUseBoHuyet, v -> isUseBoHuyet = v, () -> lastTimeBoHuyet, TIME_ITEM));
        buffMap.put("BoKhi", new BuffInfo(() -> isUseBoKhi, v -> isUseBoKhi = v, () -> lastTimeBoKhi, TIME_ITEM));
        buffMap.put("GiapXen", new BuffInfo(() -> isUseGiapXen, v -> isUseGiapXen = v, () -> lastTimeGiapXen, TIME_ITEM));
        buffMap.put("CuongNo", new BuffInfo(() -> isUseCuongNo, v -> isUseCuongNo = v, () -> lastTimeCuongNo, TIME_ITEM));
        buffMap.put("AnDanh", new BuffInfo(() -> isUseAnDanh, v -> isUseAnDanh = v, () -> lastTimeAnDanh, TIME_ITEM));
        buffMap.put("BoHuyet2", new BuffInfo(() -> isUseBoHuyet2, v -> isUseBoHuyet2 = v, () -> lastTimeBoHuyet2, TIME_ITEM));
        buffMap.put("BoKhi2", new BuffInfo(() -> isUseBoKhi2, v -> isUseBoKhi2 = v, () -> lastTimeBoKhi2, TIME_ITEM));
        buffMap.put("GiapXen2", new BuffInfo(() -> isUseGiapXen2, v -> isUseGiapXen2 = v, () -> lastTimeGiapXen2, TIME_ITEM));
        buffMap.put("CuongNo2", new BuffInfo(() -> isUseCuongNo2, v -> isUseCuongNo2 = v, () -> lastTimeCuongNo2, TIME_ITEM));
        buffMap.put("DuoiKhi", new BuffInfo(() -> isDuoiKhi, v -> isDuoiKhi = v, () -> lastTimeDuoiKhi, TIME_MAY_DO));
        buffMap.put("BanhChung", new BuffInfo(() -> isUseBanhChung, v -> isUseBanhChung = v, () -> lastTimeBanhChung, TIME_ITEM));
        buffMap.put("BanhTet", new BuffInfo(() -> isUseBanhTet, v -> isUseBanhTet = v, () -> lastTimeBanhTet, TIME_ITEM));
        buffMap.put("BanhTrungThu1Trung", new BuffInfo(() -> isBanhTrungThu1Trung, v -> isBanhTrungThu1Trung = v, () -> lastTimeBanhTrungThu1Trung, TIME_ITEM));
        buffMap.put("BanhTrungThu2Trung", new BuffInfo(() -> isBanhTrungThu2Trung, v -> isBanhTrungThu2Trung = v, () -> lastTimeBanhTrungThu2Trung, TIME_ITEM));
        buffMap.put("EatMeal", new BuffInfo(() -> isEatMeal, v -> isEatMeal = v, () -> lastTimeEatMeal, TIME_EAT_MEAL));
        buffMap.put("MayDo", new BuffInfo(() -> isMayDo, v -> isMayDo = v, () -> timeMayDo, TIME_MAY_DO));
        buffMap.put("UseMayDo", new BuffInfo(() -> isUseMayDo, v -> isUseMayDo = v, () -> lastTimeUseMayDo, TIME_MAY_DO));
        buffMap.put("RateDragonHit", new BuffInfo(() -> rateDragonHit, v -> rateDragonHit = v, () -> lastTimerateHit, TIME_MAY_DO));
        buffMap.put("RateDame", new BuffInfo(() -> rateDame, v -> rateDame = v, () -> lastTimeDameDr, TIME_MAY_DO));
        buffMap.put("RateHPKI", new BuffInfo(() -> rateHPKI, v -> rateHPKI = v, () -> lastTimerateHPKI, TIME_MAY_DO));
        buffMap.put("MaTroi", new BuffInfo(() -> isMaTroi, v -> isMaTroi = v, () -> lastTimeMaTroi, TIME_ITEM));
        buffMap.put("OpenPower", new BuffInfo(() -> isOpenPower, v -> isOpenPower = v, () -> lastTimeOpenPower, TIME_OPEN_POWER));
        buffMap.put("UseTDLT", new BuffInfo(() -> isUseTDLT, v -> isUseTDLT = v, () -> lastTimeUseTDLT, 0));
    }

    public void update() {
        boolean update = false;

        for (Map.Entry<String, BuffInfo> entry : buffMap.entrySet()) {
            String buffName = entry.getKey();
            BuffInfo buff = entry.getValue();

            if (buff.isActive()) {
                long duration = buffName.equals("UseTDLT") ? timeTDLT : buff.duration;
                if (Util.canDoWithTime(buff.getLastTime(), duration)) {
                    buff.deactivate();
                    update |= handleBuffExpiration(buffName);
                }
            }
        }

        if (update) {
            Service.getInstance().point(player);
        }
    }

    private boolean handleBuffExpiration(String buffName) {
        switch (buffName) {
        case "OpenPower":
            player.nPoint.limitPower++;
            if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                player.nPoint.limitPower = NPoint.MAX_LIMIT;
            }
            player.nPoint.initPowerLimit();
            Service.getInstance().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
            return false;
        case "UseTDLT":
            ItemTimeService.gI().sendCanAutoPlay(player);
            return false;
        case "MaTroi":
            Service.getInstance().Send_Caitrang(player);
            return false;
        default:
            return true;
        }
    }

    public void dispose() {
        this.player = null;
    }

    // Inner class để quản lý buff mà không thay đổi biến public
    private static class BuffInfo {
        private final BooleanSupplier isActiveGetter;
        private final BooleanConsumer isActiveSetter;
        private final LongSupplier lastTimeGetter;
        private final int duration;

        BuffInfo(BooleanSupplier isActiveGetter, BooleanConsumer isActiveSetter,
                LongSupplier lastTimeGetter, int duration) {
            this.isActiveGetter = isActiveGetter;
            this.isActiveSetter = isActiveSetter;
            this.lastTimeGetter = lastTimeGetter;
            this.duration = duration;
        }

        boolean isActive() {
            return isActiveGetter.get();
        }

        void deactivate() {
            isActiveSetter.accept(false);
        }

        long getLastTime() {
            return lastTimeGetter.get();
        }
    }

    // Functional interfaces để truy cập biến public
    @FunctionalInterface
    private interface BooleanSupplier {
        boolean get();
    }

    @FunctionalInterface
    private interface BooleanConsumer {
        void accept(boolean value);
    }

    @FunctionalInterface
    private interface LongSupplier {
        long get();
    }

    @FunctionalInterface
    private interface LongConsumer {
        void accept(long value);
    }
}