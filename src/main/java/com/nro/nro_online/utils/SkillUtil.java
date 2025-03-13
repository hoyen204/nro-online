package com.nro.nro_online.utils;

import java.util.List;

import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.NClass;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.models.skill.SkillNotFocus;
import com.nro.nro_online.models.skill.SkillTemplate;
import com.nro.nro_online.server.Manager;

public class SkillUtil {

    private static final NClass nClassTD = Manager.NCLASS.getFirst();
    private static final NClass nClassNM = Manager.NCLASS.get(1);
    private static final NClass nClassXD = Manager.NCLASS.get(2);

    public static Skill getSkill(int tempId, int level) {
        try {
            return getSkillFromClass(tempId, level);
        } catch (Exception e) {
            return null;
        }
    }

    public static Skill createSkill(int tempId, int level) {
        Skill skill = getSkill(tempId, level);
        return skill != null && skill.template.id >= 24 ? new SkillNotFocus(skill) : new Skill(skill);
    }

    public static Skill createEmptySkill() {
        Skill skill = new Skill();
        skill.skillId = -1;
        return skill;
    }

    public static Skill createSkillLevel0(int tempId) {
        Skill skill = createEmptySkill();
        skill.template = new SkillTemplate();
        skill.template.id = (byte) tempId;
        return skill;
    }

    public static boolean isUseSkillDam(Player player) {
        int skillId = player.playerSkill.skillSelect.template.id;
        return skillId == Skill.DRAGON || skillId == Skill.DEMON || skillId == Skill.GALICK
                || skillId == Skill.KAIOKEN || skillId == Skill.LIEN_HOAN;
    }

    public static boolean isUseSkillChuong(Player player) {
        int skillId = player.playerSkill.skillSelect.template.id;
        return skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC;
    }

    public static boolean isUseSkillBoom(Player player) {
        return player.playerSkill.skillSelect.template.id == Skill.TU_SAT;
    }

    public static int getTimeMonkey(int level) {
        return (level + 5) * 10000;
    }

    public static int getPercentHpMonkey(int level) {
        return (level + 3) * 10;
    }

    public static int getTimeStun(int level) {
        return (level + 2) * 1000;
    }

    public static int getTimeSocola() {
        return 30000;
    }

    public static int getTimeShield(int level) {
        return (level + 2) * 5000;
    }

    public static int getTimeTroi(int level) {
        return level * 5000;
    }

    public static int getTimeDCTT(int level) {
        return (level + 1) * 500;
    }

    public static int getTimeThoiMien(int level) {
        return (level + 4) * 1000;
    }

    public static int getRangeStun(int level) {
        return 120 + level * 30;
    }

    public static int getRangeBom(int level) {
        return 400 + level * 30;
    }

    public static int getRangeQCKK(int level) {
        return 350 + level * 30;
    }

    public static int getPercentHPHuytSao(int level) {
        return (level + 3) * 10;
    }

    public static int getPercentTriThuong(int level) {
        return (level + 9) * 5;
    }

    public static int getPercentCharge(int level) {
        return level + 3;
    }

    public static int getTempMobMe(int level) {
        int[] temp = {8, 11, 32, 25, 43, 49, 50};
        return temp[level - 1];
    }

    public static int getTimeSurviveMobMe(int level) {
        return getTimeMonkey(level) * 2;
    }

    public static int getHPMobMe(int hpMaxPlayer, int level) {
        int[] perHPs = {30, 40, 50, 60, 70, 80, 90};
        return hpMaxPlayer * perHPs[level - 1] / 100;
    }

    public static Skill getSkillbyId(Player player, int id) {
        return player.playerSkill.skills.stream().filter(s -> s.template.id == id).findFirst().orElse(null);
    }

    public static SkillNotFocus findSkillNotFocus(Player player) {
        return (SkillNotFocus) player.playerSkill.skills.stream().filter(s -> s instanceof SkillNotFocus).findFirst().orElse(null);
    }

    public static boolean upSkillPet(List<Skill> skills, int index) {
        Skill current = skills.get(index);
        int tempId = current.template.id;
        int level = current.point + 1;
        if (level > 7) return false;
        Skill skill = getSkill(tempId, level);
        if (skill == null) return false;
        skill = new Skill(skill);
        if (index == 1) skill.coolDown = 1000;
        skills.set(index, skill);
        return true;
    }

    public static byte getTempSkillSkillByItemID(int id) {
        return switch (id) {
            case 66, 67, 68, 69, 70, 71, 72 -> Skill.DRAGON;
            case 79, 80, 81, 82, 83, 84, 86 -> Skill.DEMON;
            case 87, 88, 89, 90, 91, 92, 93 -> Skill.GALICK;
            case 94, 95, 96, 97, 98, 99, 100 -> Skill.KAMEJOKO;
            case 101, 102, 103, 104, 105, 106, 107 -> Skill.MASENKO;
            case 108, 109, 110, 111, 112, 113, 114 -> Skill.ANTOMIC;
            case 115, 116, 117, 118, 119, 120, 121 -> Skill.THAI_DUONG_HA_SAN;
            case 122, 123, 124, 125, 126, 127, 128 -> Skill.TRI_THUONG;
            case 129, 130, 131, 132, 133, 134, 135 -> Skill.TAI_TAO_NANG_LUONG;
            case 300, 301, 302, 303, 304, 305, 306 -> Skill.KAIOKEN;
            case 307, 308, 309, 310, 311, 312, 313 -> Skill.QUA_CAU_KENH_KHI;
            case 314, 315, 316, 317, 318, 319, 320 -> Skill.BIEN_KHI;
            case 321, 322, 323, 324, 325, 326, 327 -> Skill.TU_SAT;
            case 328, 329, 330, 331, 332, 333, 334 -> Skill.MAKANKOSAPPO;
            case 335, 336, 337, 338, 339, 340, 341 -> Skill.DE_TRUNG;
            case 434, 435, 436, 437, 438, 439, 440 -> Skill.KHIEN_NANG_LUONG;
            case 474, 475, 476, 477, 478, 479, 480 -> Skill.SOCOLA;
            case 481, 482, 483, 484, 485, 486, 487 -> Skill.LIEN_HOAN;
            case 488, 489, 490, 491, 492, 493, 494 -> Skill.DICH_CHUYEN_TUC_THOI;
            case 495, 496, 497, 498, 499, 500, 501 -> Skill.THOI_MIEN;
            case 502, 503, 504, 505, 506, 507, 508 -> Skill.TROI;
            case 509, 510, 511, 512, 513, 514, 515 -> Skill.HUYT_SAO;
            default -> (byte) -1;
        };
    }

    public static Skill getSkillByItemID(Player pl, int tempId) {
        byte skillId = getTempSkillSkillByItemID(tempId);
        return skillId == -1 ? null : getSkillbyId(pl, skillId);
    }

    public static Skill getSkillByLevel(int tempId, byte level) {
        byte skillId = getTempSkillSkillByItemID(tempId);
        return skillId == -1 ? null : getSkill(skillId, level);
    }

    public static void setSkill(Player pl, Skill skill) {
        pl.playerSkill.skills.replaceAll(s -> s.template.id == skill.template.id ? skill : s);
    }

    public static byte getTyleSkillAttack(Skill skill) {
        return switch (skill.template.id) {
            case Skill.TRI_THUONG -> 2;
            case Skill.KAMEJOKO, Skill.MASENKO, Skill.ANTOMIC -> 1;
            default -> 0;
        };
    }

    private static Skill getSkillFromClass(int tempId, int level) {
        try {
            return nClassTD.getSkillTemplate(tempId).skillss.get(level - 1);
        } catch (Exception e) {
            try {
                return nClassNM.getSkillTemplate(tempId).skillss.get(level - 1);
            } catch (Exception ex) {
                return nClassXD.getSkillTemplate(tempId).skillss.get(level - 1);
            }
        }
    }
}