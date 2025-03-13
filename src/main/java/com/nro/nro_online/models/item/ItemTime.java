package com.nro.nro_online.models.item;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.utils.Util;

public class ItemTime {

    //id item text
    public static final byte DOANH_TRAI = 0;
    public static final byte BAN_DO_KHO_BAU = 1;

    public static final byte TOP_DAME_ATTACK_BOSS = 2;

    public static final int TIME_ITEM = 600_000;
    public static final int TIME_OPEN_POWER = 86400000;
    public static final int TIME_MAY_DO = 1800000;
    public static final int TIME_EAT_MEAL = 600000;

    private Player player;
    public boolean isUseBoHuyet2;
    public boolean isUseBoKhi2;
    public boolean isUseGiapXen2;
    public boolean isUseCuongNo2;
    public boolean isDuoiKhi;

    public boolean isMaTroi;
    public long lastTimeMaTroi;
    public int iconMaTroi;

    public boolean rateDragonHit;
    public long lastTimerateHit;

    public boolean rateDame;
    public long lastTimeDameDr;

    public boolean rateHPKI;
    public long lastTimerateHPKI;

    public boolean isBanhTrungThu1Trung;
    public boolean isBanhTrungThu2Trung;

    public long lastTimeBanhTrungThu1Trung;
    public long lastTimeBanhTrungThu2Trung;

    public long lastTimeBoHuyet2;
    public long lastTimeBoKhi2;
    public long lastTimeGiapXen2;
    public long lastTimeCuongNo2;
    public long lastTimeDuoiKhi;

    public boolean isUseBanhChung;
    public boolean isUseBanhTet;
    public long lastTimeBanhChung;
    public long lastTimeBanhTet;
    public boolean isUseBoHuyet;
    public boolean isUseBoKhi;
    public boolean isUseGiapXen;
    public boolean isUseCuongNo;
    public boolean isUseAnDanh;
    public long lastTimeBoHuyet;
    public long lastTimeBoKhi;
    public long lastTimeGiapXen;
    public long lastTimeCuongNo;
    public long lastTimeAnDanh;

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

    public ItemTime(Player player) {
        this.player = player;
    }

    public void update() {
        boolean update = false;
        if (rateDragonHit) {
            if (Util.canDoWithTime(lastTimerateHit, TIME_MAY_DO)) {
                rateDragonHit = false;
                update = true;
            }
        }
        if (rateDame) {
            if (Util.canDoWithTime(lastTimeDameDr, TIME_MAY_DO)) {
                rateDame = false;
                update = true;
            }
        }
        if (rateHPKI) {
            if (Util.canDoWithTime(lastTimerateHPKI, TIME_MAY_DO)) {
                rateHPKI = false;
                update = true;
            }
        }
        if (isBanhTrungThu1Trung) {
            if (Util.canDoWithTime(lastTimeBanhTrungThu1Trung, TIME_ITEM)) {
                isBanhTrungThu1Trung = false;
                update = true;
            }
        }
        if (isBanhTrungThu2Trung) {
            if (Util.canDoWithTime(lastTimeBanhTrungThu2Trung, TIME_ITEM)) {
                isBanhTrungThu2Trung = false;
                update = true;
            }
        }
        if (isDuoiKhi) {
            if (Util.canDoWithTime(lastTimeDuoiKhi, TIME_MAY_DO)) {
                isDuoiKhi = false;
                update = true;
            }
        }
        if (isEatMeal) {
            if (Util.canDoWithTime(lastTimeEatMeal, TIME_EAT_MEAL)) {
                isEatMeal = false;
                update = true;
            }
        }
        if (isUseBoHuyet) {
            if (Util.canDoWithTime(lastTimeBoHuyet, TIME_ITEM)) {
                isUseBoHuyet = false;
                update = true;
            }
        }
        if (isUseBoKhi) {
            if (Util.canDoWithTime(lastTimeBoKhi, TIME_ITEM)) {
                isUseBoKhi = false;
                update = true;
            }
        }
        if (isUseGiapXen) {
            if (Util.canDoWithTime(lastTimeGiapXen, TIME_ITEM)) {
                isUseGiapXen = false;
            }
        }
        if (isUseCuongNo) {
            if (Util.canDoWithTime(lastTimeCuongNo, TIME_ITEM)) {
                isUseCuongNo = false;
                update = true;
            }
        }
        if (isUseAnDanh) {
            if (Util.canDoWithTime(lastTimeAnDanh, TIME_ITEM)) {
                isUseAnDanh = false;
            }
        }
        if (isUseBanhChung) {
            if (Util.canDoWithTime(lastTimeBanhChung, TIME_ITEM)) {
                isUseBanhChung = false;
            }
        }
        if (isUseBanhTet) {
            if (Util.canDoWithTime(lastTimeBanhTet, TIME_ITEM)) {
                isUseBanhTet = false;
            }
        }
        if (isUseBoHuyet2) {
            if (Util.canDoWithTime(lastTimeBoHuyet2, TIME_ITEM)) {
                isUseBoHuyet2 = false;
                update = true;
            }
        }
        if (isUseBoKhi2) {
            if (Util.canDoWithTime(lastTimeBoKhi2, TIME_ITEM)) {
                isUseBoKhi2 = false;
                update = true;
            }
        }
        if (isUseGiapXen2) {
            if (Util.canDoWithTime(lastTimeGiapXen2, TIME_ITEM)) {
                isUseGiapXen2 = false;
            }
        }
        if (isUseCuongNo2) {
            if (Util.canDoWithTime(lastTimeCuongNo2, TIME_ITEM)) {
                isUseCuongNo2 = false;
                update = true;
            }
        }
        if (isOpenPower) {
            if (Util.canDoWithTime(lastTimeOpenPower, TIME_OPEN_POWER)) {
                player.nPoint.limitPower++;
                if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                    player.nPoint.limitPower = NPoint.MAX_LIMIT;
                }
                player.nPoint.initPowerLimit();
                Service.getInstance().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
                isOpenPower = false;
            }
        }
        if (isUseMayDo) {
            if (Util.canDoWithTime(lastTimeUseMayDo, TIME_MAY_DO)) {
                isUseMayDo = false;
            }
        }
        if (isMayDo) {
            if (Util.canDoWithTime(timeMayDo, TIME_MAY_DO)) {
                isMayDo = false;
            }
        }
        if (isUseTDLT) {
            if (Util.canDoWithTime(lastTimeUseTDLT, timeTDLT)) {
                this.isUseTDLT = false;
                ItemTimeService.gI().sendCanAutoPlay(this.player);
            }
        }
        if (isUseBanhChung) {
            if (Util.canDoWithTime(lastTimeBanhChung, TIME_ITEM)) {
                isUseBanhChung = false;
                update = true;
            }
        }
        if (isUseBanhTet) {
            if (Util.canDoWithTime(lastTimeBanhTet, TIME_ITEM)) {
                isUseBanhTet = false;
                update = true;
            }
        }
        if (isMaTroi) {
            if (Util.canDoWithTime(lastTimeMaTroi, TIME_ITEM)) {
                isMaTroi = false;
                Service.getInstance().Send_Caitrang(player);
                update = false;
            }
        }
        if (update) {
            Service.getInstance().point(player);
        }
    }

    public void dispose() {
        this.player = null;
    }
}
