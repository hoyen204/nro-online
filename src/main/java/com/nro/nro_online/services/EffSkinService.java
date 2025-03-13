package com.nro.nro_online.services;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.utils.Log;

public class EffSkinService {

    public static final byte TURN_ON_EFFECT = 1;
    public static final byte TURN_OFF_EFFECT = 0;
    public static final byte TURN_OFF_ALL_EFFECT = 2;
    public static final byte BLIND_EFFECT = 40;
    public static final byte SLEEP_EFFECT = 41;
    public static final byte STONE_EFFECT = 42;
    private static final EffSkinService i = new EffSkinService();

    public static EffSkinService gI() {
        return i;
    }

    public void setInvisible(Player pl, long lastTimeInvisible, int timeInvisible) {
        pl.effectSkin.timeInvisible = timeInvisible;
        pl.effectSkin.lastTimeInvisible = lastTimeInvisible;
        pl.isInvisible = true;
        Service.getInstance().sendPlayerInfo(pl);
    }

    public void removeInvisible(Player pl) {
        pl.isInvisible = false;
        Service.getInstance().sendPlayerInfo(pl);
    }

    public void setSlow(Player pl, long lastTimeSlow, int timeSlow) {
        pl.effectSkin.lastTimeSlow = lastTimeSlow;
        pl.effectSkin.timeSlow = timeSlow;
        pl.effectSkin.isSlow = true;
        Service.getInstance().point(pl);
    }

    public void removeSlow(Player pl) {
        pl.effectSkin.isSlow = false;
        Service.getInstance().point(pl);
    }

    public void setSocola(Player pl, long lastTimeSocola, int timeSocola) {
        pl.effectSkin.lastTimeSocola = lastTimeSocola;
        pl.effectSkin.timeSocola = timeSocola;
        pl.effectSkin.isSocola = true;
        Service.getInstance().point(pl);
    }

    public void removeSocola(Player pl) {
        pl.effectSkin.isSocola = false;
        Service.getInstance().point(pl);
        Service.getInstance().Send_Caitrang(pl);
    }

    public void setHoaDa(Player pl, long lastTimeHoaDa, int timeHoaDa) {
        pl.effectSkin.isHoaDa = true;
        pl.effectSkin.lastTimeHoaDa = lastTimeHoaDa;
        pl.effectSkin.timeHoaDa = timeHoaDa;
    }

    public void removeHoaDa(Player pl) {
        pl.effectSkin.isHoaDa = false;
        Service.getInstance().Send_Caitrang(pl);
        sendEffectPlayer(pl, pl, TURN_OFF_EFFECT, STONE_EFFECT);
    }

    public void setCuongNo(Player pl, long lastTimeCuongNo, int timeCuongNo) {
        pl.effectSkin.isNezuko = true;
        pl.effectSkin.lastTimeNezuko = lastTimeCuongNo;
        pl.effectSkin.timeNezuko = timeCuongNo;
        Service.getInstance().point(pl);
    }

    public void removeCuongNo(Player pl) {
        pl.effectSkin.isNezuko = false;
        Service.getInstance().point(pl);
    }

    public void setInosuke(Player pl, long lastTimeTanjiro, int timeTanjiro) {
        pl.effectSkin.isInosuke = true;
        pl.effectSkin.lastTimeInosuke = lastTimeTanjiro;
        pl.effectSkin.timeInosuke = timeTanjiro;
        Service.getInstance().point(pl);
    }

    public void removeInosuke(Player pl) {
        pl.effectSkin.isInosuke = false;
        Service.getInstance().point(pl);
    }

    public void setTanjiro(Player pl, long lastTimeTanjiro, int timeTanjiro) {
        pl.effectSkin.isTanjiro = true;
        pl.effectSkin.lastTimeTanjiro = lastTimeTanjiro;
        pl.effectSkin.timeTanjiro = timeTanjiro;
        Service.getInstance().point(pl);
    }

    public void removeTanjiro(Player pl) {
        pl.effectSkin.isTanjiro = false;
        Service.getInstance().point(pl);
    }

    public void setZenitsu(Player pl, long lastTimeZenitsu, int timeZenitsu) {
        pl.effectSkin.isZenitsu = true;
        pl.effectSkin.lastTimeZenitsu = lastTimeZenitsu;
        pl.effectSkin.timeZenitsu = timeZenitsu;
        Service.getInstance().point(pl);
    }

    public void removeZenitsu(Player pl) {
        pl.effectSkin.isZenitsu = false;
        Service.getInstance().point(pl);
    }

    public void setInoHashi(Player pl, long lastTimeInoHashi, int timeInoHashi) {
        pl.effectSkin.isInoHashi = true;
        pl.effectSkin.lastTimeInoHashi = lastTimeInoHashi;
        pl.effectSkin.timeInoHashi = timeInoHashi;
        Service.getInstance().point(pl);
    }

    public void removeInoHashi(Player pl) {
        pl.effectSkin.isInoHashi = false;
        Service.getInstance().point(pl);
    }

    public void sendEffectPlayer(Player plUseSkill, Player plTarget, byte toggle, byte effect) {
        try (Message msg = new Message(-124)) {
            msg.writer().writeByte(toggle);
            msg.writer().writeByte(0);
            if (toggle == TURN_OFF_ALL_EFFECT) {
                msg.writer().writeInt((int) plTarget.id);
            } else {
                msg.writer().writeByte(effect);
                msg.writer().writeInt((int) plTarget.id);
                msg.writer().writeInt((int) plUseSkill.id);
            }
            Service.getInstance().sendMessAllPlayerInMap(plUseSkill, msg);
        } catch (Exception e) {
            Log.error(EffectSkillService.class, e);
        }
    }
}