package com.nro.nro_online.models.player;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.nro.nro_online.consts.ConstPet;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.lib.Pair;
import com.nro.nro_online.models.item.Costume;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemTimeService;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;
import lombok.Getter;
import lombok.Setter;

public class Pet extends Player {

    @Getter
    @Setter
    private int lever;

    // Constants
    private static final short ARANGE_CAN_ATTACK = 200;
    private static final short ARANGE_ATT_SKILL1 = 50;
    private static final short[][] PET_ID = { { 285, 286, 287 }, { 288, 289, 290 }, { 282, 283, 284 }, { 304, 305, 303 } };
    private static final int TIME_WAIT_AFTER_UNFUSION = 5000;

    // Status constants
    public static final byte FOLLOW = 0;
    public static final byte PROTECT = 1;
    public static final byte ATTACK = 2;
    public static final byte GOHOME = 3;
    public static final byte FUSION = 4;
    public static final byte FOREVER_FUSION = 5;

    // Instance variables
    public static boolean ANGRY;
    public Player master;
    public byte status = FOLLOW;
    public byte typePet;
    public boolean isTransform;
    public long lastTimeDie;
    private boolean goingHome;
    private Mob mobAttack;
    private Player playerAttack;
    private long lastTimeUnfusion;
    private long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;
    private long lastTimeAskPea;
    private int countTTNL;
    private byte directAtHome = -1;

    public Pet(Player master) {
        this.master = master;
        this.isPet = true;
    }

    @Override
    public int version() {
        return 214;
    }

    // -------------------------------------------------------------------------
    // Status and Fusion Methods
    // -------------------------------------------------------------------------

    public void changeStatus(byte status) {
        if (goingHome || master.fusion.typeFusion != 0 || (isDie() && status == FUSION) || master.zone.map.mapId == 128) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        Service.getInstance().chatJustForMe(master, this, getTextStatus(status));
        switch (status) {
        case GOHOME -> goHome();
        case FUSION -> fusion(false);
        case FOREVER_FUSION -> assimilate();
        }
        this.status = status;
    }

    private String getTextStatus(byte status) {
        return switch (status) {
            case FOLLOW -> "Ok con theo sư phụ";
            case PROTECT -> "Ok con sẽ bảo vệ sư phụ";
            case ATTACK -> "Ok sư phụ để con lo cho";
            case GOHOME -> "Ok con về, bibi sư phụ";
            default -> "";
        };
    }

    public void fusion(boolean porata) {
        performFusion(ConstPlayer.HOP_THE_PORATA, porata);
    }

    public void fusion2(boolean porata) {
        performFusion(ConstPlayer.HOP_THE_PORATA2, porata);
    }

    public void fusion3(boolean porata) {
        performFusion(ConstPlayer.HOP_THE_PORATA3, porata);
    }

    private void performFusion(byte fusionType, boolean isPorata) {
        if (isDie()) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        if (Util.canDoWithTime(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION)) {
            if (isPorata) {
                master.fusion.typeFusion = fusionType;
            } else {
                master.fusion.lastTimeFusion = System.currentTimeMillis();
                master.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
                ItemTimeService.gI().sendItemTime(master, master.gender == ConstPlayer.NAMEC ? 3901 : 3790, Fusion.TIME_FUSION / 1000);
            }
            this.status = FUSION;
            exitMapFusion();
            fusionEffect(master.fusion.typeFusion);
            Service.getInstance().Send_Caitrang(master);
            master.nPoint.calPoint();
            master.nPoint.setFullHpMp();
            Service.getInstance().point(master);
        } else {
            Service.getInstance().sendThongBao(master, "Vui lòng đợi " + TimeUtil.getTimeLeft(lastTimeUnfusion, TIME_WAIT_AFTER_UNFUSION / 1000) + " nữa");
        }
    }

    private void assimilate() {
        if (master.gender != ConstPlayer.NAMEC || isDie()) {
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        master.fusion.typeFusion = ConstPlayer.LUONG_LONG_NHAT_THE;
        exitMapFusion();
        if (master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            master.pet.unFusion();
        }
        MapService.gI().exitMap(master.pet);
        master.pet.dispose();
        master.pet = null;
        fusionEffect(master.fusion.typeFusion);
        Service.getInstance().Send_Caitrang(master);
        long power = this.nPoint.power;
        PlayerService.gI().sendTNSM(master, (byte) 0, power);
        master.nPoint.tiemNangUp(power);
    }

    public void unFusion() {
        master.fusion.typeFusion = 0;
        this.status = PROTECT;
        Service.getInstance().point(master);
        joinMapMaster();
        fusionEffect(master.fusion.typeFusion);
        Service.getInstance().Send_Caitrang(master);
        Service.getInstance().point(master);
        this.lastTimeUnfusion = System.currentTimeMillis();
    }

    private void fusionEffect(int type) {
        try (Message msg = new Message(125)) {
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) master.id);
            Service.getInstance().sendMessAllPlayerInMap(master, msg);
        } catch (Exception e) {
        }
    }

    private void exitMapFusion() {
        if (this.zone != null) {
            MapService.gI().exitMap(this);
        }
    }

    // -------------------------------------------------------------------------
    // Movement Methods
    // -------------------------------------------------------------------------

    public void joinMapMaster() {
        if (!MapService.gI().isMapVS(master.zone.map.mapId) && status != GOHOME && status != FUSION && !isDie()) {
            this.location.x = master.location.x + Util.nextInt(-10, 10);
            this.location.y = master.location.y;
            MapService.gI().goToMap(this, master.zone);
            this.zone.loadMeToAnother(this);
        }
    }

    public void goHome() {
        if (this.status == GOHOME)
            return;
        goingHome = true;
        new Thread(() -> {
            try {
                this.status = ATTACK;
                Thread.sleep(2000);
                MapService.gI().goToMap(this, MapService.gI().getMapCanJoin(this, master.gender + 21));
                this.zone.loadMeToAnother(this);
                this.status = GOHOME;
                goingHome = false;
            } catch (Exception e) {
                // Handle exception silently
            }
        }).start();
    }

    private void moveIdle() {
        if (status == GOHOME || status == FUSION || !idle || !Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle))
            return;
        int dir = this.location.x <= master.location.x ? 1 : -1;
        PlayerService.gI().playerMove(this, master.location.x + Util.nextInt(dir == -1 ? 30 : -50, dir == -1 ? 50 : 30), master.location.y);
        lastTimeMoveIdle = System.currentTimeMillis();
        timeMoveIdle = Util.nextInt(5000, 8000);
    }

    public void followMaster() {
        if (isDie() || effectSkill.isHaveEffectSkill())
            return;
        if (this.status == ATTACK) {
            if (ANGRY)
                followMaster(80);
            else if (mobAttack != null && Util.getDistance(this, master) <= 500)
                return;

            followMaster(60);
        } else if (this.status == FOLLOW || this.status == PROTECT) {
            followMaster(60);
        }
    }

    private void followMaster(int dis) {
        int mX = master.location.x;
        int mY = master.location.y;
        double distance = Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2));
        if (distance >= dis) {
            this.location.x = mX + (this.location.x < mX ? Util.nextInt(0, dis) : -Util.nextInt(0, dis));
            this.location.y = mY;
            PlayerService.gI().playerMove(this, this.location.x, this.location.y);
        }
    }

    // -------------------------------------------------------------------------
    // Combat Methods
    // -------------------------------------------------------------------------

    private Mob findMobAttack() {
        int minDistance = ARANGE_CAN_ATTACK;
        Mob targetMob = null;
        for (Mob mob : zone.mobs) {
            if (!mob.isDie()) {
                int distance = Util.getDistance(this, mob);
                if (distance <= minDistance) {
                    minDistance = distance;
                    targetMob = mob;
                }
            }
        }
        return targetMob;
    }

    private void attackMob() {
        if (useSkill3() || useSkill4() || useSkill5())
            return;
        mobAttack = findMobAttack();
        if (mobAttack == null) {
            idle = true;
            return;
        }

        int disToMob = Util.getDistance(this, mobAttack);
        if (disToMob <= ARANGE_ATT_SKILL1) {
            this.playerSkill.skillSelect = getSkill(1);
            if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                SkillService.gI().useSkill(this, null, mobAttack);
            } else {
                askPea();
            }
        } else {
            this.playerSkill.skillSelect = getSkill(2);
            if (this.playerSkill.skillSelect.skillId != -1 && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, mobAttack);
            } else {
                askPea();
            }
        }
    }

    private boolean useSkill3() {
        this.playerSkill.skillSelect = getSkill(3);
        if (this.playerSkill.skillSelect.skillId == -1)
            return false;

        if (this.playerSkill.skillSelect.template.id == Skill.THAI_DUONG_HA_SAN) {
            if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, null);
                Service.getInstance().chatJustForMe(master, this, "Thái dương hạ san");
                return true;
            }
            return false;
        } else if (this.playerSkill.skillSelect.template.id == Skill.TAI_TAO_NANG_LUONG) {
            if (this.effectSkill.isCharging && this.countTTNL < Util.nextInt(3, 5)) {
                this.countTTNL++;
                return true;
            }
            if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this) &&
                    (this.nPoint.getCurrPercentHP() <= 20 || this.nPoint.getCurrPercentMP() <= 20)) {
                SkillService.gI().useSkill(this, null, null);
                this.countTTNL = 0;
                return true;
            }
            return false;
        } else if (this.playerSkill.skillSelect.template.id == Skill.KAIOKEN) {
            if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                mobAttack = findMobAttack();
                if (mobAttack == null)
                    return false;
                int dis = Util.getDistance(this, mobAttack);
                if (dis > ARANGE_ATT_SKILL1) {
                    PlayerService.gI().playerMove(this, mobAttack.location.x, mobAttack.location.y);
                } else {
                    PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                }
                SkillService.gI().useSkill(this, playerAttack, mobAttack);
                getSkill(1).lastTimeUseThisSkill = System.currentTimeMillis();
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean useSkill4() {
        this.playerSkill.skillSelect = getSkill(4);
        if (this.playerSkill.skillSelect.skillId == -1)
            return false;

        if (this.playerSkill.skillSelect.template.id == Skill.BIEN_KHI) {
            if (!this.effectSkill.isMonkey && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, null);
                return true;
            }
            return false;
        } else if (this.playerSkill.skillSelect.template.id == Skill.KHIEN_NANG_LUONG) {
            if (!this.effectSkill.isShielding && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, null);
                return true;
            }
            return false;
        } else if (this.playerSkill.skillSelect.template.id == Skill.DE_TRUNG) {
            if (this.mobMe == null && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, null);
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean useSkill5() {
        this.playerSkill.skillSelect = getSkill(4); // Note: This seems to be a bug in original code, should be getSkill(5)
        if (this.playerSkill.skillSelect.skillId == -1)
            return false;

        if (this.playerSkill.skillSelect.template.id == Skill.DICH_CHUYEN_TUC_THOI) {
            if (SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, null);
                return true;
            }
            return false;
        } else if (this.playerSkill.skillSelect.template.id == Skill.HUYT_SAO) {
            if (this.effectSkill.tiLeHPHuytSao == 0 && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, null);
                return true;
            }
            return false;
        } else if (this.playerSkill.skillSelect.template.id == Skill.TU_SAT) {
            if (!this.playerSkill.prepareTuSat && SkillService.gI().canUseSkillWithCooldown(this) && SkillService.gI().canUseSkillWithMana(this)) {
                SkillService.gI().useSkill(this, null, null);
                return true;
            }
            return false;
        }
        return false;
    }

    public void askPea() {
        if (this.typePet == 1 && master.charms.tdDeTuMabu > System.currentTimeMillis()) {
            InventoryService.gI().eatPea(master);
        } else if (Util.canDoWithTime(lastTimeAskPea, 10000)) {
            Service.getInstance().chatJustForMe(master, this, "Sư phụ ơi cho con đậu thần");
            lastTimeAskPea = System.currentTimeMillis();
        }
    }

    public void angry(Player plAtt) {
        ANGRY = true;
        if (plAtt != null) {
            this.playerAttack = plAtt;
            Service.getInstance().chatJustForMe(master, this, "Mi làm ta nổi giận rồi " + plAtt.name.replace("$", ""));
        }
    }

    // -------------------------------------------------------------------------
    // Skill Management Methods
    // -------------------------------------------------------------------------

    private void increasePoint() {
        if (status != FUSION) {
            this.nPoint.increasePoint((byte) Util.nextInt(0, 2), (short) 10);
        }
    }

    private void updatePower() {
        if (this.playerSkill == null)
            return;
        byte sizeSkill = this.playerSkill.getSizeSkill();
        if (sizeSkill == 1) {
            if (this.nPoint.power >= 150_000_000)
                openSkill2();
        } else if (sizeSkill == 2) {
            if (this.nPoint.power >= 1_500_000_000)
                openSkill3();
        } else if (sizeSkill == 3) {
            if (this.nPoint.power >= 40_000_000_000L)
                openSkill4();
        } else if (sizeSkill == 4 && this.nPoint.power >= 100_000_000_000L) {
            openSkill5();
            this.nPoint.calPoint();
        }
    }

    public void openSkill2() {
        this.playerSkill.skills.set(1, getRandomSkill(Map.of(
                25, SkillUtil.createSkill(Skill.KAMEJOKO, 1),  // 25%
                55, SkillUtil.createSkill(Skill.ANTOMIC, 1),   // 30%
                100, SkillUtil.createSkill(Skill.MASENKO, 1)   // 45%
        )));
    }

    public void openSkill3() {
        this.playerSkill.skills.set(2, getRandomFromList(List.of(
                SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 1), // 30%
                SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 1),
                SkillUtil.createSkill(Skill.THAI_DUONG_HA_SAN, 1),
                SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1), // 40%
                SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1),
                SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1),
                SkillUtil.createSkill(Skill.TAI_TAO_NANG_LUONG, 1),
                SkillUtil.createSkill(Skill.KAIOKEN, 1), // 30%
                SkillUtil.createSkill(Skill.KAIOKEN, 1),
                SkillUtil.createSkill(Skill.KAIOKEN, 1)
        )));
    }

    public void openSkill4() {
        this.playerSkill.skills.set(3, getRandomSkill(Map.of(
                20, SkillUtil.createSkill(Skill.BIEN_KHI, 1),    // 20%
                75, SkillUtil.createSkill(Skill.DE_TRUNG, 1),   // 55%
                100, SkillUtil.createSkill(Skill.KHIEN_NANG_LUONG, 1)  // 25%
        )));
    }

    public void openSkill5() {
        this.playerSkill.skills.set(4, getRandomSkill(Map.of(
                15, SkillUtil.createSkill(Skill.DICH_CHUYEN_TUC_THOI, 1),  // 15%
                45, SkillUtil.createSkill(Skill.HUYT_SAO, 1),  // 30%
                100, SkillUtil.createSkill(Skill.TU_SAT, 1)  // 55%
        )));
    }

    private Skill getRandomSkill(Map<Integer, Skill> skillMap) {
        TreeMap<Integer, Skill> weightedMap = new TreeMap<>(skillMap);
        int rand = Util.nextInt(1, 100);
        return weightedMap.ceilingEntry(rand).getValue();
    }

    private Skill getRandomFromList(List<Skill> skillPool) {
        return skillPool.get(Util.nextInt(0, skillPool.size() - 1));
    }

    private Skill getSkill(int indexSkill) {
        return this.playerSkill.skills.get(indexSkill - 1);
    }

    // -------------------------------------------------------------------------
    // Appearance Methods
    // -------------------------------------------------------------------------

    public short getAvatar() {
        return switch (this.typePet) {
            case ConstPet.MABU -> 297;
            case ConstPet.BILL -> 508;
            case ConstPet.VIDEL -> 810;
            case ConstPet.WHIS -> 838;
            case ConstPet.SUPER -> (this.nPoint.power < 10_000_000_000L) ? (short) 550 : (short) 553;
            default -> PET_ID[3][this.gender];
        };
    }

    @Override
    public short getHead() {
        if (effectSkill.isMonkey)
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        if (effectSkill.isSocola || effectSkin.isSocola)
            return 412;
        if (effectSkin.isHoaDa)
            return 454;
        if (!isTransform) {
            if (typePet == ConstPet.MABU) {
                return 297;
            } else if (typePet == ConstPet.BILL) {
                return 508;
            } else if (typePet == ConstPet.VIDEL) {
                return 810;
            } else if (typePet == ConstPet.WHIS) {
                return 838;
            } else if (typePet == ConstPet.SUPER) {
                return (this.nPoint.power < 10_000_000_000L) ? (short) 550 : (short) 553;
            }
        }
        if (inventory.itemsBody.get(5).isNotNullItem()) {
            Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (ct != null)
                return (short) (ct.getId()[0] != -1 ? ct.getId()[0] : inventory.itemsBody.get(5).template.part);
        }
        return (this.nPoint.power < 1_500_000) ? PET_ID[this.gender][0] : PET_ID[3][this.gender];
    }

    @Override
    public short getBody() {
        if (effectSkill.isMonkey)
            return 193;
        if (effectSkill.isSocola || effectSkin.isSocola)
            return 413;
        if (effectSkin.isHoaDa)
            return 455;
        if (!isTransform) {
            if (typePet == ConstPet.MABU) {
                return 298;
            } else if (typePet == ConstPet.BILL) {
                return 509;
            } else if (typePet == ConstPet.VIDEL) {
                return 811;
            } else if (typePet == ConstPet.WHIS) {
                return 839;
            } else if (typePet == ConstPet.SUPER) {
                return 551;
            }
        }
        if (inventory.itemsBody.get(5).isNotNullItem()) {
            Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getId()[1] != -1)
                return (short) ct.getId()[1];
        }
        if (inventory.itemsBody.get(0).isNotNullItem())
            return inventory.itemsBody.get(0).template.part;
        return (this.nPoint.power < 1_500_000) ? PET_ID[this.gender][1] : (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
    }

    @Override
    public short getLeg() {
        if (effectSkill.isMonkey)
            return 194;
        if (effectSkill.isSocola || effectSkin.isSocola)
            return 414;
        if (effectSkin.isHoaDa)
            return 456;
        if (!isTransform) {
            if (typePet == ConstPet.MABU) {
                return 299;
            } else if (typePet == ConstPet.BILL) {
                return 510;
            } else if (typePet == ConstPet.VIDEL) {
                return 812;
            } else if (typePet == ConstPet.WHIS) {
                return 840;
            } else if (typePet == ConstPet.SUPER) {
                return 552;
            }
        }
        if (inventory.itemsBody.get(5).isNotNullItem()) {
            Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getId()[2] != -1)
                return (short) ct.getId()[2];
        }
        if (inventory.itemsBody.get(1).isNotNullItem())
            return inventory.itemsBody.get(1).template.part;
        return (this.nPoint.power < 1_500_000) ? PET_ID[this.gender][2] : (short) (gender == ConstPlayer.NAMEC ? 60 : 58);
    }

    // -------------------------------------------------------------------------
    // Transformation Method
    // -------------------------------------------------------------------------

    public void transform() {
        this.isTransform = !this.isTransform;
        Service.getInstance().Send_Caitrang(this);
        String chatMessage = (typePet == ConstPet.MABU) ? "Bư bư bư...." : "Phép thuật winx enchantix....";
        Service.getInstance().chat(this, chatMessage);
    }

    // -------------------------------------------------------------------------
    // update Method
    // -------------------------------------------------------------------------

    @Override
    public void update() {
        try {
            super.update();
            increasePoint();
            updatePower();

            if (isDie()) {
                if (System.currentTimeMillis() - lastTimeDie > 50000) {
                    Service.getInstance().hsChar(this, nPoint.hpMax, nPoint.mpMax);
                }
                return;
            }

            if (justRevived && this.zone == master.zone) {
                Service.getInstance().chatJustForMe(master, this, "Sư phụ ơi, con đây nè!");
                justRevived = false;
            }

            if (this.zone == null || this.zone != master.zone) {
                joinMapMaster();
                return;
            }

            if (master.isDie() || effectSkill.isHaveEffectSkill())
                return;

            moveIdle();

            switch (status) {
            case FOLLOW -> followMaster(60);
            case PROTECT -> {
                followMaster(60);
                attackMob();
            }
            case ATTACK -> attackMob();
            case GOHOME -> goHome();
            }
        } catch (Exception ignored) {
        }
    }
}