package com.nro.nro_online.models.player;

import java.util.ArrayList;
import java.util.List;

import nro.attr.Attribute;
import nro.card.Card;
import nro.card.CollectionBook;
import nro.consts.ConstAttribute;
import nro.consts.ConstPet;
import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.manager.TopWhis;
import nro.models.clan.Buff;
import nro.models.intrinsic.Intrinsic;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.skill.Skill;
import nro.power.PowerLimit;
import nro.power.PowerLimitManager;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.server.ServerNotify;
import nro.services.*;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 * @Stole By Arriety
 */
public class NPoint {

    public static final byte MAX_LIMIT = 10;

    private Player player;
    public boolean isCrit;
    public boolean isCrit100;
    public boolean eatOsin;

    private Intrinsic intrinsic;
    private int percentDameIntrinsic;
    public int dameAfter;

    /*-----------------------Chỉ số cơ bản------------------------------------*/
    public byte numAttack;
    public short stamina, maxStamina;

    public byte limitPower;
    public long power;
    public long tiemNang;

    public int hp, hpMax, hpg;
    public int mp, mpMax, mpg;
    public int dame, dameg;
    public int def, defg;
    public int crit, critg;
    public byte speed = 5;

    public boolean teleport;

    public boolean khangTDHS;

    /**
     * Chỉ số cộng thêm
     */
    public int hpAdd, mpAdd, dameAdd, defAdd, critAdd, hpHoiAdd, mpHoiAdd;

    /**
     * //+#% sức đánh chí mạng
     */
    public List<Integer> tlDameCrit;

    public boolean buffExpSatellite, buffDefenseSatellite;

    /**
     * Tỉ lệ hp, mp cộng thêm
     */
    public List<Integer> tlHp, tlMp;

    /**
     * Tỉ lệ giáp cộng thêm
     */
    public List<Integer> tlDef;

    /**
     * Tỉ lệ sức đánh/ sức đánh khi đánh quái
     */
    public List<Integer> tlDame, tlDameAttMob;

    /**
     * Lượng hp, mp hồi mỗi 30s, mp hồi cho người khác
     */
    public int hpHoi, mpHoi, mpHoiCute;

    /**
     * Tỉ lệ hp, mp hồi cộng thêm
     */
    public short tlHpHoi, tlMpHoi;

    /**
     * Tỉ lệ hp, mp hồi bản thân và đồng đội cộng thêm
     */
    public short tlHpHoiBanThanVaDongDoi, tlMpHoiBanThanVaDongDoi;

    /**
     * Tỉ lệ hút hp, mp khi đánh, hp khi đánh quái
     */
    public short tlHutHp, tlHutMp, tlHutHpMob;

    /**
     * Tỉ lệ hút hp, mp xung quanh mỗi 5s
     */
    public short tlHutHpMpXQ;

    /**
     * Tỉ lệ phản sát thương
     */
    public short tlPST;

    /**
     * Tỉ lệ tiềm năng sức mạnh
     */
    public List<Integer> tlTNSM;
    public int tlTNSMPet;
    private short suphu;
    /**
     * Tỉ lệ vàng cộng thêm
     */
    public short tlGold;

    /**
     * Tỉ lệ né đòn
     */
    public short tlNeDon;

    /**
     * Tỉ lệ sức đánh đẹp cộng thêm cho bản thân và người xung quanh
     */
    public List<Integer> tlSDDep;

    /**
     * Tỉ lệ giảm sức đánh
     */
    public short tlSubSD;
    public List<Integer> tlSpeed;
    public int mstChuong;
    public int tlGiamst;

    /*------------------------Effect skin-------------------------------------*/
    public Item trainArmor;
    public boolean wornTrainArmor;
    public boolean wearingTrainArmor;

    public boolean wearingVoHinh;
    public boolean isKhongLanh;
    public boolean isKhongAnhHuongBoiLoiNguyen;

    public short tlHpGiamODo;

    private PowerLimit powerLimit;
    public boolean wearingDrabula;
    public boolean wearingMabu;
    public boolean wearingBuiBui;

    public boolean wearingNezuko;
    public boolean wearingTanjiro;
    public boolean wearingInosuke;
    public boolean wearingInoHashi;
    public boolean wearingZenitsu;
    public int tlDameChuong;
    public boolean xDameChuong;
    public boolean wearingYacon;
    public boolean wearingRedNoelHat;
    public boolean wearingGrayNoelHat;
    public boolean wearingBlueNoelHat;
    public boolean wearingNoelHat;

    public NPoint(Player player) {
        this.player = player;
        this.tlHp = new ArrayList<>();
        this.tlMp = new ArrayList<>();
        this.tlDef = new ArrayList<>();
        this.tlDame = new ArrayList<>();
        this.tlDameAttMob = new ArrayList<>();
        this.tlSDDep = new ArrayList<>();
        this.tlTNSM = new ArrayList<>();
        this.tlDameCrit = new ArrayList<>();
        this.tlSpeed = new ArrayList<>();
    }

    public void initPowerLimit() {
        powerLimit = PowerLimitManager.getInstance().get(limitPower);
    }

    /*-------------------------------------------------------------------------*/
    /**
     * Tính toán mọi chỉ số sau khi có thay đổi
     */
    public void calPoint() {
        try {
            if (this.player.pet != null) {
                this.player.pet.nPoint.setPointWhenWearClothes();
            }
            this.setPointWhenWearClothes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPoint(ItemOption io) {
        switch (io.optionTemplate.id) {
            case 0: //Tấn công +#
                this.dameAdd += io.param;
                break;
            case 2: //HP, KI+#000
                this.hpAdd += io.param * 1000;
                this.mpAdd += io.param * 1000;
                break;
            case 3: // vô hiệu vả biến st chưởng thành ki
                this.mstChuong += io.param;
                break;
            case 5: //+#% sức đánh chí mạng
                this.tlDameCrit.add(io.param);
                break;
            case 6: //HP+#
                this.hpAdd += io.param;
                break;
            case 7: //KI+#
                this.mpAdd += io.param;
                break;
            case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                this.tlHutHpMpXQ += io.param;
                break;
            case 14: //Chí mạng+#%
                this.critAdd += io.param;
                break;
            case 19: //Tấn công+#% khi đánh quái
                this.tlDameAttMob.add(io.param);
                break;
            case 22: //HP+#K
                this.hpAdd += io.param * 1000;
                break;
            case 23: //MP+#K
                this.mpAdd += io.param * 1000;
                break;
            case 24:
                this.wearingBuiBui = true;
                break;
            case 25:
                this.wearingYacon = true;
                break;
            case 26:
                this.wearingDrabula = true;
                this.player.effectSkin.lastTimeDrabula = System.currentTimeMillis();
                break;
            case 29:
                this.wearingMabu = true;
                break;
            case 27: //+# HP/30s
                this.hpHoiAdd += io.param;
                break;
            case 28: //+# KI/30s
                this.mpHoiAdd += io.param;
                break;
            case 33: //dịch chuyển tức thời
                this.teleport = true;
                break;
            case 47: //Giáp+#
                this.defAdd += io.param;
                break;
            case 48: //HP/KI+#
                this.hpAdd += io.param;
                this.mpAdd += io.param;
                break;
            case 49: //Tấn công+#%
            case 50: //Sức đánh+#%
                this.tlDame.add(io.param);
                break;
            case 77: //HP+#%
                this.tlHp.add(io.param);
                break;
            case 80: //HP+#%/30s
                this.tlHpHoi += io.param;
                break;
            case 81: //MP+#%/30s
                this.tlMpHoi += io.param;
                break;
            case 88: //Cộng #% exp khi đánh quái
                this.tlTNSM.add(io.param);
                break;
            case 94: //Giáp #%
                this.tlDef.add(io.param);
                break;
            case 95: //Biến #% tấn công thành HP
                this.tlHutHp += io.param;
                break;
            case 96: //Biến #% tấn công thành MP
                this.tlHutMp += io.param;
                break;
            case 97: //Phản #% sát thương
                this.tlPST += io.param;
                break;
            case 100: //+#% vàng từ quái
                this.tlGold += io.param;
                break;
            case 101: //+#% TN,SM
                this.tlTNSM.add(io.param);
                break;
            case 103: //KI +#%
                this.tlMp.add(io.param);
                break;
            case 104: //Biến #% tấn công quái thành HP
                this.tlHutHpMob += io.param;
                break;
            case 105: //Vô hình khi không đánh quái và boss
                this.wearingVoHinh = true;
                break;
            case 106: //Không ảnh hưởng bởi cái lạnh
                this.isKhongLanh = true;
                break;
            case 108: //#% Né đòn
                this.tlNeDon += io.param;
                break;
            case 109: //Hôi, giảm #% HP
                this.tlHpGiamODo += io.param;
                break;
            case 114:
                this.tlSpeed.add(io.param);
                break;
            case 116: //Kháng thái dương hạ san
                this.khangTDHS = true;
                break;
            case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                this.tlSDDep.add(io.param);
                break;
            case 147: //+#% sức đánh
                this.tlDame.add(io.param);
                break;
            case 156: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                this.tlSubSD += 50;
                this.tlTNSM.add(io.param);
                this.tlGold += io.param;
                break;
            case 160:
                this.tlTNSMPet += io.param;
                break;
            case 162: //Cute hồi #% KI/s bản thân và xung quanh
                this.mpHoiCute += io.param;
                break;
            case 173: //Phục hồi #% HP và KI cho đồng đội
                this.tlHpHoiBanThanVaDongDoi += io.param;
                this.tlMpHoiBanThanVaDongDoi += io.param;
                break;
            case 189:
                this.wearingNezuko = true;
                break;
            case 190:
                this.wearingTanjiro = true;
                break;
            case 191:
                this.wearingInoHashi = true;
                break;
            case 192:
                this.wearingInosuke = true;
                break;
            case 193:
                this.wearingZenitsu = true;
                break;
            case 194:
                this.tlDameChuong = 3;
                break;
            case 195:
                this.tlDameChuong = 4;
            case 231:
                this.isKhongAnhHuongBoiLoiNguyen = true;
                break;
        }
    }

    private void setPointWhenWearClothes() {
        resetPoint();

        int idbt = 454;
        int countbt = 0;
        for (Item item : this.player.inventory.itemsBag) {
            if (countbt >= 1) {
                break;
            }
            if (item.isNotNullItem()) {
                switch (this.player.fusion.typeFusion) {
                    case ConstPlayer.HOP_THE_PORATA2:
                        idbt = 921;
                        break;
                    case ConstPlayer.HOP_THE_PORATA3:
                        idbt = 1995;
                        break;
                }
                if (item.template.id == idbt) {
                    for (ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 80: //HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 101: //+#% TN,SM
                                this.tlTNSM.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += io.param;
                                break;
                            case 14:// chí mạng
                                this.critAdd += io.param;
                                break;
                        }
                    }
                    countbt++;
                }
            }
        }
        for (Item item : this.player.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                int tempID = item.template.id;
                if (tempID >= 592 && tempID <= 594) {
                    teleport = true;
                }
                for (ItemOption io : item.itemOptions) {
                    setPoint(io);
                }
            }
        }
        List<Item> itemsBody = player.inventory.itemsBody;
        if (Manager.EVENT_SEVER == 3) {
            if (!this.player.isBoss && !this.player.isMiniPet) {
                if (itemsBody.get(5).isNotNullItem()) {
                    int tempID = itemsBody.get(5).getId();
                    switch (tempID) {
                        case 386:
                        case 389:
                        case 392:
                            wearingGrayNoelHat = true;
                            wearingNoelHat = true;
                            break;
                        case 387:
                        case 390:
                        case 393:
                            wearingRedNoelHat = true;
                            wearingNoelHat = true;
                            break;
                        case 388:
                        case 391:
                        case 394:
                            wearingBlueNoelHat = true;
                            wearingNoelHat = true;
                            break;
                        default:
                            wearingRedNoelHat = false;
                            wearingBlueNoelHat = false;
                            wearingGrayNoelHat = false;
                            wearingNoelHat = false;
                    }
                }
            }
        }
        CollectionBook book = player.getCollectionBook();
        if (book != null) {
            List<Card> cards = book.getCards();
            if (cards != null) {
                for (Card c : cards) {
                    if (c.getLevel() > 0) {
                        int index = 0;
                        for (ItemOption o : c.getCardTemplate().getOptions()) {
                            if ((index == 0 || c.isUse()) && c.getLevel() >= o.activeCard) {
                                setPoint(o);
                            }
                            index++;
                        }
                    }
                }
            }
        }
        setDameTrainArmor();
        setBasePoint();
    }

    private void setDameTrainArmor() {
        if (!this.player.isPet && !this.player.isBoss && !this.player.isMiniPet) {
            try {
                Item gtl = this.player.inventory.itemsBody.get(6);
                if (gtl.isNotNullItem()) {
                    this.wearingTrainArmor = true;
                    this.wornTrainArmor = true;
                    this.player.inventory.trainArmor = gtl;
                    this.tlSubSD += ItemService.gI().getPercentTrainArmor(gtl);
                } else {
                    if (this.wornTrainArmor) {
                        this.wearingTrainArmor = false;
                        for (ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                            if (io.optionTemplate.id == 9 && io.param > 0) {
                                this.tlDame.add(ItemService.gI().getPercentTrainArmor(this.player.inventory.trainArmor));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.error("Lỗi get giáp tập luyện " + this.player.name);
            }
        }
    }

    private void setNeDon() {
        //ngọc rồng đen 6 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            this.tlNeDon += RewardBlackBall.R6S;
        }
    }

    private void setHpHoi() {
        this.hpHoi = (int) calPercent(this.hpMax, 1);
        this.hpHoi += this.hpHoiAdd;
        this.hpHoi += calPercent(this.hpMax, this.tlHpHoi);
        this.hpHoi += calPercent(this.hpMax, this.tlHpHoiBanThanVaDongDoi);
        if (this.player.effectSkin.isNezuko) {
            this.hpHoi += calPercent(this.hpMax, 3);
        }
    }

    private void setMpHoi() {
        this.mpHoi = (int) calPercent(this.mpMax, 1);
        this.mpHoi += this.mpHoiAdd;
        this.mpHoi += calPercent(this.mpMax, this.tlMpHoi);
        this.mpHoi += calPercent(this.mpMax, this.tlMpHoiBanThanVaDongDoi);
        if (this.player.effectSkin.isNezuko) {
            this.mpHoi += calPercent(this.mpMax, 3);
        }
    }

    private void setHpMax() {
        this.hpMax = this.hpg;
        this.hpMax += this.hpAdd;
        //đồ
        if (this.player.playerSkill.skills.size() > 4) {
            if (this.player.isPet && this.player.playerSkill.skills.get(4).skillId != -1 && this.player.playerSkill.skills.get(4).template.id == Skill.HUYT_SAO) {
                this.hpMax += hpMax / 100 * (3 * this.player.playerSkill.skills.get(4).point);
            }
        }

        for (Integer tl : this.tlHp) {
            if (tl == null) {
                Service.getInstance().sendThongBao(player, "Đã xảy ra lỗi!");
                continue;
            }
            this.hpMax += calPercent(this.hpMax, tl);
        }
        //set nappa
        if (this.player.setClothes.nappa2 == 5) {
            this.hpMax *= 2;
        }
        if (this.player.setClothes.nappa1 == 5) {
            this.hpMax += ((long) this.hpMax * 50 / 100);
        }
        //ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            this.hpMax += calPercent(this.hpMax, RewardBlackBall.R2S);
        }
        //khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentHpMonkey(player.effectSkill.levelMonkey);
                this.hpMax += calPercent(this.hpMax, percent);
            }
        }
        if (this.player.isPet) {
            int percent = ((Pet) this.player).getLever() * 3;
            if (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                    || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                    || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                switch (((Pet) this.player).typePet) {
                    case ConstPet.MABU:
                        this.hpMax += calPercent(this.hpMax, 5);
                        break;
                    case ConstPet.BILL:
                        this.hpMax += calPercent(this.hpMax, 15);
                        break;
                    case ConstPet.VIDEL:
                        this.hpMax += calPercent(this.hpMax, percent);
                        break;
                    case ConstPet.SUPER:
                        this.hpMax += calPercent(this.hpMax, 15);
                        break;
                    case ConstPet.WHIS:
                        this.hpMax += calPercent(this.hpMax, 30 + percent);
                        break;
                }
            }
        }
        if (this.player.itemTime != null && this.player.itemTime.rateHPKI) {
            this.hpMax += calPercent(this.hpMax, 10);
        }
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.hpMax *= this.player.effectSkin.xHPKI;
        }
        //phù mabu 14h
        if (this.player.zone != null && MapService.gI().isMapMabuWar14H(this.player.zone.map.mapId)) {
            this.hpMax += 1000000;
        }
        //+hp đệ
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += this.player.pet.nPoint.hpMax;
        }
        //huýt sáo
        if (!this.player.isPet
                || (this.player.isPet
                && ((Pet) this.player).status != Pet.FUSION)) {
            if (this.player.effectSkill.tiLeHPHuytSao != 0) {
                this.hpMax += calPercent(this.hpMax, this.player.effectSkill.tiLeHPHuytSao);
            }
        }
        //bổ huyết
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet) {
            this.hpMax *= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isBanhTrungThu1Trung) {
            this.hpMax += calPercent(this.hpMax, 10);
        }
        if (this.player.itemTime != null && this.player.itemTime.isBanhTrungThu2Trung) {
            this.hpMax += calPercent(this.hpMax, 20);
        }
        //bổ huyết 2
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet2) {
            this.hpMax += calPercent(hpMax, 120);
        }
        if (this.player.itemTime != null && this.player.itemTime.isMaTroi) {
            this.hpMax /= 1.5;
        }
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.hpMax /= 2;
        }
        if (!player.isBoss) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.HP);
            if (at != null && !at.isExpired()) {
                hpMax += calPercent(hpMax, at.getValue());
            }
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhTet) {
                hpMax += calPercent(hpMax, 20);
            }
        }
        if (player.getBuff() == Buff.BUFF_HP) {
            hpMax += calPercent(hpMax, 20);
        }
        if (TopWhis.TOP_ONE == player.id) {
            hpMax += calPercent(hpMax, 10);
        }
        if (TopWhis.TOP_THREE == player.id) {
            hpMax += calPercent(hpMax, 6);
        }
        if (TopWhis.TOP_TWO == player.id) {
            hpMax += calPercent(hpMax, 3);
        }
        if (this.player.zone != null && MapService.gI().isMapDiaNguc(this.player.zone.map)
                && !this.isKhongAnhHuongBoiLoiNguyen) {
            this.hpMax /= 3;
        }
    }

    // (hp sư phụ + hp đệ tử ) + 15%
    // (hp sư phụ + 15% +hp đệ tử)
    private void setHp() {
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void setMpMax() {
        this.mpMax = this.mpg;
        this.mpMax += this.mpAdd;
        //đồ
        for (Integer tl : this.tlMp) {
            if (tl == null) {
                Service.getInstance().sendThongBao(player, "Đã xảy ra lỗi!");
                continue;
            }
            this.mpMax += calPercent(this.mpMax, tl);
        }
        if (this.player.itemTime != null && this.player.itemTime.isMaTroi) {
            this.mpMax /= 1.5;
        }

        if (this.player.setClothes.picolo2 == 5) {
            this.mpMax *= 2;
        }
        if (this.player.setClothes.picolo1 == 5) {
            this.mpMax += ((long) this.mpMax * 50 / 100);
        }
        //ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.mpMax += calPercent(this.mpMax, RewardBlackBall.R3S);
        }
        if (this.player.isPet) {
            int percent = ((Pet) this.player).getLever() * 3;
            if (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                    || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                    || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                if (this.player.itemTime != null && this.player.itemTime.rateHPKI) {
                    this.mpMax += calPercent(this.mpMax, 10);
                }
                switch (((Pet) this.player).typePet) {
                    case ConstPet.MABU:
                        this.mpMax += calPercent(this.mpMax, 5);
                        break;
                    case ConstPet.SUPER:
                        this.mpMax += calPercent(this.mpMax, 10);
                        break;
                    case ConstPet.BILL:
                        this.mpMax += calPercent(this.mpMax, 15);
                        break;
                    case ConstPet.VIDEL:
                        this.mpMax += calPercent(this.mpMax, percent);
                        break;
                    case ConstPet.WHIS:
                        this.mpMax += calPercent(this.mpMax, 30 + percent);
                        break;
                }
            }
        }
        //hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.mpMax += this.player.pet.nPoint.mpMax;
        }
        //bổ khí
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi) {
            this.mpMax *= 2;
        }
        //bổ khí 2
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi2) {
            this.mpMax += calPercent(mpMax, 120);
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.mpMax *= this.player.effectSkin.xHPKI;
        }
        //phù mabu 14h
        if (this.player.zone != null && MapService.gI().isMapMabuWar14H(this.player.zone.map.mapId)) {
            this.mpMax += 1000000;
        }
        //xiên cá
        if (this.player.effectFlagBag.useXienCa) {
            this.mpMax += calPercent(this.mpMax, 15);
        }
        //Kiem z
        if (this.player.effectFlagBag.useKiemz) {
            this.mpMax += calPercent(this.mpMax, 20);
        }
        if (this.player.itemTime != null && this.player.itemTime.isBanhTrungThu1Trung) {
            this.mpMax += calPercent(this.mpMax, 10);
        }
        if (this.player.itemTime != null && this.player.itemTime.isBanhTrungThu2Trung) {
            this.mpMax += calPercent(this.mpMax, 20);
        }
        if (this.player.effectFlagBag.useDieuRong) {
            this.mpMax += calPercent(this.mpMax, 30);
        }
        if (this.player.effectFlagBag.useHoaVang || this.player.effectFlagBag.useHoaHong) {
            this.mpMax += calPercent(this.mpMax, 20);
        }
        if (!player.isBoss) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.KI);
            if (at != null && !at.isExpired()) {
                mpMax += calPercent(mpMax, at.getValue());
            }
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhTet) {
                mpMax += calPercent(mpMax, 20);
            }
        }
        if (player.getBuff() == Buff.BUFF_KI) {
            mpMax += calPercent(mpMax, 20);
        }
        if (TopWhis.TOP_ONE == player.id) {
            mpMax += calPercent(mpMax, 10);
        }
        if (TopWhis.TOP_THREE == player.id) {
            mpMax += calPercent(mpMax, 6);
        }
        if (TopWhis.TOP_TWO == player.id) {
            mpMax += calPercent(mpMax, 3);
        }
        if (this.player.zone != null && MapService.gI().isMapDiaNguc(this.player.zone.map)
                && !this.isKhongAnhHuongBoiLoiNguyen) {
            this.mpMax /= 3;
        }
    }

    private void setMp() {
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    private void setDame() {
        this.dame = this.dameg;
        this.dame += this.dameAdd;
        //đồ
        for (Integer tl : this.tlDame) {
            this.dame += calPercent(this.dame, tl);
        }
        if (this.player.playerSkill.skills.size() > 4) {
            if (this.player.isPet && this.player.playerSkill.skills.get(4).skillId != -1 && this.player.playerSkill.skills.get(4).template.id == Skill.DICH_CHUYEN_TUC_THOI) {
                this.dame += dame / 100 * (3 * this.player.playerSkill.skills.get(4).point);
            }
        }
        for (Integer tl : this.tlSDDep) {
            this.dame += calPercent(this.dame, tl);
        }
        if (this.player.isPet) {
            int percent = ((Pet) this.player).getLever() * 3;
            if (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA
                    || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2
                    || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                switch (((Pet) this.player).typePet) {
                    case ConstPet.MABU:
                        this.dame += calPercent(this.dame, 5);
                        break;
                    case ConstPet.SUPER:
                        this.dame += calPercent(this.dame, 10);
                        break;
                    case ConstPet.BILL:
                        this.dame += calPercent(this.dame, 15);
                        break;
                    case ConstPet.WHIS:
                        this.dame += calPercent(this.dame, 30 + percent);
                        break;
                }
            }
        }
        if (this.player.itemTime != null && this.player.itemTime.rateDame) {
            this.dame += calPercent(this.dame, 5);
        }
        //thức ăn
        if (!this.player.isPet && this.player.itemTime.isEatMeal
                || this.player.isPet && ((Pet) this.player).master.itemTime.isEatMeal) {
            this.dame += calPercent(this.dame, 10);
        }
        //hợp thể
        if (this.player.fusion.typeFusion != 0) {
            this.dame += this.player.pet.nPoint.dame;
        }
        //cuồng nộ
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo) {
            this.dame *= 2;
        }
        //cuồng nộ 2
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo2) {
            this.dame += calPercent(dame, 110);
        }

        if (this.player.itemTime != null && this.player.itemTime.isBanhTrungThu1Trung) {
            this.dame += calPercent(this.dame, 10);
        }
        if (this.player.itemTime != null && this.player.itemTime.isBanhTrungThu2Trung) {
            this.dame += calPercent(this.dame, 20);
        }
        //phù mabu 14h
        if (this.player.zone != null && MapService.gI().isMapMabuWar14H(this.player.zone.map.mapId)) {
            this.dame += 10000;
        }
        //giảm dame
        this.dame -= calPercent(this.dame, tlSubSD);
        //map cold
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.dame /= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isMaTroi) {
            this.dame /= 1.5;
        }
        //ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.dame += calPercent(this.dame, RewardBlackBall.R1S);
        }
        if (!player.isBoss) {
            Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.SUC_DANH);
            if (at != null && !at.isExpired()) {
                this.dame += calPercent(dame, at.getValue());
            }
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhChung) {
                dame += calPercent(dame, 20);
            }
        }
        if (player.getBuff() == Buff.BUFF_ATK) {
            dame += calPercent(dame, 20);
        }
        if (player.getBuff() == Buff.BUFF_KI) {
            dame += calPercent(mpMax, 20);
        }
        if (TopWhis.TOP_ONE == player.id) {
            this.dame += calPercent(this.dame, 10);
        }
        if (TopWhis.TOP_THREE == player.id) {
            this.dame += calPercent(this.dame, 6);
        }
        if (TopWhis.TOP_TWO == player.id) {
            this.dame += calPercent(this.dame, 3);
        }
        if (this.player.zone != null && MapService.gI().isMapDiaNguc(this.player.zone.map)
                && !this.isKhongAnhHuongBoiLoiNguyen) {
            this.dame /= 3;
        }
    }

    private void setDef() {
        this.def = this.defg * 4;
        this.def += this.defAdd;
        //đồ
        for (Integer tl : this.tlDef) {
            this.tlGiamst += tl;
        }
        if (tlGiamst > 60) {
            tlGiamst = 60;
        }
        //ngọc rồng đen 5 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            this.def += calPercent(this.def, RewardBlackBall.R5S);
        }
        if (this.player.effectSkin.isInosuke) {
            this.def += calPercent(this.def, 50);
        }
        if (this.player.effectSkin.isInoHashi) {
            this.def += calPercent(this.def, 60);
        }
    }

    private void setCrit() {
        this.crit = this.critg;
        this.crit += this.critAdd;
        //ngọc rồng đen 4 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            this.crit += RewardBlackBall.R4S;
        }
        //biến khỉ
        if (this.player.effectSkill.isMonkey) {
            this.crit = 110;
        }
        if (player.getBuff() == Buff.BUFF_CRIT) {
            crit += 10;
        }
    }

    private void setCritDame() {
        if (this.player.effectSkin.isTanjiro) {
            this.tlDameCrit.add(30);
        }
        if (this.player.itemTime != null) {
            if (this.player.itemTime.isUseBanhChung) {
                this.tlDameCrit.add(15);
            }
        }
    }

    private void setSpeed() {
        for (Integer tl : this.tlSpeed) {
            this.speed += calPercent(this.speed, tl);
        }
        if (this.player.effectSkin.isSlow) {
            this.speed = 1;
        }
    }

    private void resetPoint() {
        this.hpAdd = 0;
        this.mpAdd = 0;
        this.dameAdd = 0;
        this.defAdd = 0;
        this.critAdd = 0;
        this.tlHp.clear();
        this.tlMp.clear();
        this.tlDef.clear();
        this.tlDame.clear();
        this.tlDameAttMob.clear();
        this.tlDameCrit.clear();
        this.tlHpHoiBanThanVaDongDoi = 0;
        this.tlMpHoiBanThanVaDongDoi = 0;
        this.hpHoi = 0;
        this.mpHoi = 0;
        this.mpHoiCute = 0;
        this.tlHpHoi = 0;
        this.tlMpHoi = 0;
        this.tlHutHp = 0;
        this.tlHutMp = 0;
        this.tlHutHpMob = 0;
        this.tlHutHpMpXQ = 0;
        this.tlPST = 0;
        this.tlTNSM.clear();
        this.tlDameAttMob.clear();
        this.tlDameCrit.clear();
        this.tlGold = 0;
        this.tlNeDon = 0;
        this.tlSDDep.clear();
        this.tlSubSD = 0;
        this.tlHpGiamODo = 0;
        this.teleport = false;
        this.tlSpeed.clear();
        this.speed = 5;
        this.mstChuong = 0;
        this.tlGiamst = 0;
        this.tlTNSMPet = 0;

        this.wearingVoHinh = false;
        this.isKhongLanh = false;
        this.wearingDrabula = false;
        this.wearingNezuko = false;
        this.wearingZenitsu = false;
        this.wearingInosuke = false;
        this.wearingInoHashi = false;
        this.wearingTanjiro = false;
        this.wearingMabu = false;
        this.wearingBuiBui = false;
        this.xDameChuong = false;
        this.wearingYacon = false;
        this.khangTDHS = false;
    }

    public void addHp(int hp) {
        this.hp += hp;
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    public void addMp(int mp) {
        this.mp += mp;
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    public void setHp(long hp) {
        if (hp > this.hpMax) {
            this.hp = this.hpMax;
        } else {
            this.hp = (int) hp;
        }
    }

    public void setMp(long mp) {
        if (mp > this.mpMax) {
            this.mp = this.mpMax;
        } else {
            this.mp = (int) mp;
        }
    }

    private void setIsCrit() {
        if (intrinsic != null && intrinsic.id == 25
                && this.getCurrPercentHP() <= intrinsic.param1) {
            isCrit = true;
        } else if (isCrit100) {
            isCrit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(this.crit, ConstRatio.PER100);
        }
    }

    public int getDameAttack(boolean isAttackMob) {
        setIsCrit();
        long dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        int percentDameSkill = 0;
        int percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
                if (intrinsic.id == 1) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAMEJOKO:
                if (intrinsic.id == 2) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.songoku2 == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.songoku1 == 5) {
                    percentXDame = 50;
                }
//                if (this.player.effectSkin.xDameChuong) {
//                    percentXDame += tlDameChuong;
//                    this.player.effectSkin.xDameChuong = false;
//                }
                break;
            case Skill.GALICK:
                if (intrinsic.id == 16) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kakarot2 == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.kakarot1 == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.ANTOMIC:
                if (intrinsic.id == 17) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
//                if (this.player.effectSkin.xDameChuong) {
//                    percentXDame += tlDameChuong;
//                    this.player.effectSkin.xDameChuong = false;
//                }
                break;
            case Skill.DEMON:
                if (intrinsic.id == 8) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.MASENKO:
                if (intrinsic.id == 9) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAIOKEN:
                percentDameSkill = skillSelect.damage;
//                if (this.player.setClothes.kaioken2 == 5) {
//                    percentXDame = 100;
//                }
//                if (this.player.setClothes.kaioken1 == 5) {
//                    percentXDame = 50;
//                }
                break;
            case Skill.LIEN_HOAN:
                if (intrinsic.id == 13) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.lienhoan2 == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.lienhoan1 == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                dameAttack *= 2;
                dameAttack = Util.nextInt((int) (dameAttack - calPercent(dameAttack, 5)),
                        (int) (dameAttack + calPercent(dameAttack, 5)));
                return (int) dameAttack;
            case Skill.MAKANKOSAPPO:
                percentDameSkill = skillSelect.damage;
                int dameSkill = (int) calPercent(this.mpMax, percentDameSkill);
                return dameSkill;
            case Skill.QUA_CAU_KENH_KHI:
                long totalHP = 0;
                if (player.zone != null) {
                    totalHP = player.zone.getTotalHP();
                }
                int damage = (int) ((totalHP / 10) + (this.dame * 10));
                if (this.player.setClothes.kaioken1 == 5) {
                    damage *= 2;
                }
                if (this.player.setClothes.kaioken2 == 5) {
                    damage *= 2;
                }
                return damage;
        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = calPercent(dameAttack, percentDameSkill);
        }
        dameAttack += calPercent(dameAttack, percentDameIntrinsic);
        dameAttack += calPercent(dameAttack, dameAfter);

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += calPercent(dameAttack, tl);
            }
        }
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit) {
            dameAttack *= 2;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += calPercent(dameAttack, tl);
            }
        }
        dameAttack += calPercent(dameAttack, percentXDame);
//        System.out.println(dameAttack);
        dameAttack = Util.nextInt((int) (dameAttack - calPercent(dameAttack, 5)), (int) (dameAttack + calPercent(dameAttack, 5)));

        if (dameAttack >= 50_000_000 && player.isPl()) {
            String skillName = player.playerSkill.skillSelect.template.name;
            String str = player.name + " đã đánh một " + skillName + " với sát thương " + Util.numberToMoney(dameAttack);
            switch (skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.LIEN_HOAN:
                case Skill.GALICK:
                case Skill.DEMON:
                case Skill.DRAGON:
                case Skill.KAIOKEN:
                case Skill.DE_TRUNG:
                case Skill.MAKANKOSAPPO:
                case Skill.TU_SAT:
                case Skill.QUA_CAU_KENH_KHI:
                    ServerNotify.gI().notify(str);
                    break;
                default:
                    break;
            }
        }
        return (int) dameAttack;
    }

    public void powerSub(long point) {
        if (!player.isBoss && !player.isMiniPet) {
            if (this.player.zone == null) {
                Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra");
                return;
            }
            if (MapService.gI().isMapTuongLai(this.player.zone.map.mapId)) {
                long powsub = Math.min(point, this.power - 1);
                long tnsub = Math.min(point, this.tiemNang - 1);
                this.power -= powsub;
                this.tiemNang -= tnsub;
                PlayerService.gI().sendTNSM(player, (byte) 0, -powsub);
                PlayerService.gI().sendTNSM(player, (byte) 1, -tnsub);
            }
        }
    }

    public int getDameAttackSkillNotFocus() {
        setIsCrit();
        long dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        int percentDameSkill = 0;
        int percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {

        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = calPercent(dameAttack, percentDameSkill);
        }
        dameAttack += calPercent(dameAttack, percentDameIntrinsic);
        dameAttack += calPercent(dameAttack, dameAfter);
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit) {
            dameAttack *= 2;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += calPercent(dameAttack, tl);
            }
        }
        dameAttack += calPercent(dameAttack, percentXDame);
        dameAttack = Util.nextInt((int) (dameAttack - calPercent(dameAttack, 5)), (int) (dameAttack + calPercent(dameAttack, 5)));
        return (int) dameAttack;
    }

    public int getCurrPercentHP() {
        if (this.hpMax == 0) {
            return 100;
        }
        return (int) ((long) this.hp * 100 / this.hpMax);
    }

    public int getCurrPercentMP() {
        return (int) ((long) this.mp * 100 / this.mpMax);
    }

    public void setFullHpMp() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;
    }

    public void subHP(int sub) {
        this.hp -= sub;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void subMP(int sub) {
        this.mp -= sub;
        if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public long calSucManhTiemNang(long tiemNang) {
        if (power < getPowerLimit()) {
            int mapid = this.player.zone.map.mapId;
            int tlexp = 0;

            for (Integer tl : this.tlTNSM) {
                tlexp += tl;
            }
            if (this.intrinsic != null && this.intrinsic.id == 24) {
                tlexp += this.intrinsic.param1;
            }
            tiemNang += (tiemNang * tlexp / 100);
            if (this.player.cFlag != 0) {
                if (this.player.cFlag == 8) {
                    tiemNang += ((long) tiemNang * 10 / 100);
                } else {
                    tiemNang += ((long) tiemNang * 5 / 100);
                }
            }
            if (buffExpSatellite) {
                tiemNang += calPercent(tiemNang, 20);
            }
            if (player.isPet) {
                Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
                if (at != null && !at.isExpired()) {
                    tiemNang += calPercent(tiemNang, at.getValue());
                }
            }
            if (this.player.isPet) {
                int tltnsm = ((Pet) this.player).master.nPoint.tlTNSMPet;
                if (tltnsm > 0) {
                    tiemNang += calPercent(tiemNang, tltnsm);
                }
            }
            long tn = tiemNang;
            if (MapService.gI().isMapTuongLai(mapid)) {
                tn /= 5;
            }
            if (MapService.gI().isMapCold(this.player.zone.map)) {
                tn /= 7;
            }
            if (MapService.gI().isMapFide(mapid)) {
                tn /= 7;
            }
            if (MapService.gI().isMapBanDoKhoBau(mapid)) {
                tn *= 1;
            }
            if (MapService.gI().isMapNgucTu(mapid)) {
                tn /= 17;
            }
            if (MapService.gI().isMapHTTV(mapid)) {
                tn /= 12;
            }
            if (this.player.charms.tdTriTue > System.currentTimeMillis()) {
                tiemNang += 100;
            }
            if (this.player.charms.tdTriTue3 > System.currentTimeMillis()) {
                tiemNang += 200;
            }
            if (this.player.charms.tdTriTue4 > System.currentTimeMillis()) {
                tiemNang += 300;
            }
            if (this.power >= 60000000000L) {
                tiemNang -= (tiemNang * 80 / 100);
            }
            if (this.player.isPet) {
                if (((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
                    tiemNang += tn * 2;
                }
                if (((Pet) this.player).master.nPoint.suphu > 0) {
                    tiemNang += ((Pet) this.player).nPoint.suphu;
                }
            }
            if (((this.player.isPl() && this.player.itemTime.isDuoiKhi) || (this.player.isPet && ((Pet) this.player).master.itemTime.isDuoiKhi))) {
                tiemNang += tn * 4;
            }
            tiemNang *= Manager.RATE_EXP_SERVER;
            tiemNang = calSubTNSM(tiemNang);
            if (tiemNang <= 0) {
                tiemNang = 1;
            }
        } else {
            tiemNang = calSubTNSM(tiemNang);
        }
        return tiemNang;
    }

    public long calSubTNSM(long tiemNang) {
        if (power >= 700000000000L) {
            tiemNang /= 10000000;
        } else if (power >= 600000000000L) {
            tiemNang /= 5000000;
        } else if (power >= 400000000000L) {
            tiemNang /= 1000000;
        } else if (power >= 350000000000L) {
            tiemNang /= 400000;
        } else if (power >= 300000000000L) {
            tiemNang /= 300000;
        } else if (power >= 250000000000L) {
            tiemNang /= 200000;
        } else if (power >= 200000000000L) {
            tiemNang /= 100000;
        } else if (power >= 110000000000L) {
            tiemNang /= 50000;
        } else if (power >= 100000000000L) {
            tiemNang /= 20000;
        } else if (power >= 80000000000L) {
            tiemNang -= ((long) tiemNang * 98 / 100);
        } else if (power >= 90_000_000_000L) {
            tiemNang /= 50000;
        }
        return tiemNang;
    }

    public short getTileHutHp(boolean isMob) {
        if (isMob) {
            return (short) (this.tlHutHp + this.tlHutHpMob);
        } else {
            return this.tlHutHp;
        }
    }

    public short getTiLeHutMp() {
        return this.tlHutMp;
    }

    public int subDameInjureWithDeff(int dame) {
        int def = this.def;
        dame -= def;
        if (this.player.itemTime.isUseGiapXen) {
            dame /= 2;
        }
        if (this.player.itemTime.isUseGiapXen2) {
            dame -= calPercent(dame, 60);
        }
        if (dame < 0) {
            dame = 1;
        }
        return dame;
    }

    /*------------------------------------------------------------------------*/
    public boolean canOpenPower() {
        return this.power >= getPowerLimit();
    }

    public long getPowerLimit() {
        if (powerLimit != null) {
            return powerLimit.getPower();
        }
        return 0;
    }

    public long getPowerNextLimit() {
        PowerLimit powerLimit = PowerLimitManager.getInstance().get(limitPower + 1);
        if (powerLimit != null) {
            return powerLimit.getPower();
        }
        return 0;
    }

    //**************************************************************************
    //POWER - TIEM NANG
    public void powerUp(long power) {
        this.power += power;
        TaskService.gI().checkDoneTaskPower(player, this.power);
    }

    public void tiemNangUp(long tiemNang) {
        this.tiemNang += tiemNang;
    }

    public void increasePoint(byte type, short point) {
        if (powerLimit == null) {
            return;
        }
        if (point <= 0) {
            return;
        }
        long tiemNangUse = 0;
        boolean check = false;
        switch (type) {
            case 0:
                int hpOld = hpg;
                switch (point) {
                    case 1:
                        tiemNangUse = hpOld + 1000;
                        break;
                    case 10:
                        tiemNangUse = 10 * (2 * (hpOld + 1000) + 180) / 2;
                        break;
                    case 100:
                        tiemNangUse = 100 * (2 * (hpOld + 1000) + 1980) / 2;
                        break;
                    default:
                        Service.getInstance().sendThongBaoOK(player, "Giá trị nhập vào không chính xác");
                        return;
                }
                if (tiemNang < tiemNangUse) {
                    Service.getInstance().sendThongBaoOK(player, "Bạn không có đủ tiềm năng để cộng điểm");
                    return;
                }
                int hpNew = hpOld + 20 * point;
                if (hpNew > powerLimit.getHp()) {
                    Service.getInstance().sendThongBaoOK(player, "Hãy mở giới hạn để cộng điểm này");
                    return;
                }
                this.hpg = hpNew;
                check = true;
                break;
            case 1:
                int mpOld = mpg;
                switch (point) {
                    case 1:
                        tiemNangUse = mpOld + 1000;
                        break;
                    case 10:
                        tiemNangUse = 10 * (2 * (mpOld + 1000) + 180) / 2;
                        break;
                    case 100:
                        tiemNangUse = 100 * (2 * (mpOld + 1000) + 1980) / 2;
                        break;
                    default:
                        Service.getInstance().sendThongBaoOK(player, "Giá trị nhập vào không chính xác");
                        return;
                }
                if (tiemNang < tiemNangUse) {
                    Service.getInstance().sendThongBaoOK(player, "Bạn không có đủ tiềm năng để cộng điểm");
                    return;
                }
                int mpNew = mpOld + 20 * point;
                if (mpNew > powerLimit.getMp()) {
                    Service.getInstance().sendThongBaoOK(player, "Hãy mở giới hạn để cộng điểm này");
                    return;
                }
                mpg = mpNew;
                check = true;
                break;
            case 2:
                int damageOld = this.dameg;
                switch (point) {
                    case 1:
                        tiemNangUse = damageOld * 100;
                        break;
                    case 10:
                        tiemNangUse = 10 * (2 * damageOld + 9) / 2 * 100;
                        break;
                    case 100:
                        tiemNangUse = 100 * (2 * damageOld + 99) / 2 * 100;
                        break;
                    default:
                        Service.getInstance().sendThongBaoOK(player, "Giá trị nhập vào không chính xác");
                        return;
                }
                if (tiemNang < tiemNangUse) {
                    Service.getInstance().sendThongBaoOK(player, "Bạn không có đủ tiềm năng để cộng điểm");
                    return;
                }
                int damageNew = damageOld + 1 * point;
                if (damageNew > powerLimit.getDamage()) {
                    Service.getInstance().sendThongBaoOK(player, "Hãy mở giới hạn để cộng điểm này");
                    return;
                }
                dameg = damageNew;
                check = true;
                break;
            case 3:
                int defOld = this.defg;
                tiemNangUse = 2 * (defOld + 5) / 2 * 100000;
                if (tiemNang < tiemNangUse) {
                    Service.getInstance().sendThongBaoOK(player, "Bạn không có đủ tiềm năng để cộng điểm");
                    return;
                }
                if (defOld >= powerLimit.getDefense()) {
                    Service.getInstance().sendThongBaoOK(player, "Hãy mở giới hạn để cộng điểm này");
                    return;
                }
                defg += 1;
                check = true;
                break;
            case 4:
                int critOld = critg;
                tiemNangUse = 50000000;
                for (byte i = 0; i < critOld; i++) {
                    tiemNangUse *= 5;
                }
                if (tiemNang < tiemNangUse) {
                    Service.getInstance().sendThongBaoOK(player, "Bạn không có đủ tiềm năng để cộng điểm");
                    return;
                }
                if (critOld >= powerLimit.getCritical()) {
                    Service.getInstance().sendThongBaoOK(player, "Hãy mở giới hạn để cộng điểm này");
                    return;
                }
                critg += 1;
                check = true;
                break;
        }
        this.tiemNang -= tiemNangUse;
        TaskService.gI().checkDoneTaskUseTiemNang(player);
        if (check) {
            Service.getInstance().point(player);
        }
    }

    //--------------------------------------------------------------------------
    private long lastTimeHoiPhuc;
    private long lastTimeHoiStamina;

    public void update() {
        if (player != null && player.effectSkill != null) {
            if (player.effectSkill.isCharging && player.effectSkill.countCharging < 10) {
                int tiLeHoiPhuc = SkillUtil.getPercentCharge(player.playerSkill.skillSelect.point);
                if (player.effectSkill.isCharging && !player.isDie() && !player.effectSkill.isHaveEffectSkill() && (hp < hpMax || mp < mpMax)) {
                    PlayerService.gI().hoiPhuc(player, (int) calPercent(hpMax, tiLeHoiPhuc), (int) calPercent(mpMax, tiLeHoiPhuc));
                    if (player.effectSkill.countCharging % 3 == 0) {
                        Service.getInstance().chat(player, "Phục hồi năng lượng " + getCurrPercentHP() + "%");
                    }
                } else {
                    EffectSkillService.gI().stopCharge(player);
                }
                if (++player.effectSkill.countCharging >= 10) {
                    EffectSkillService.gI().stopCharge(player);
                }
            }
            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000)) {
                PlayerService.gI().hoiPhuc(this.player, hpHoi, mpHoi);
                this.lastTimeHoiPhuc = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiStamina, 60000) && this.stamina < this.maxStamina) {
                this.stamina++;
                this.lastTimeHoiStamina = System.currentTimeMillis();
                if (!this.player.isBoss && !this.player.isPet) {
                    PlayerService.gI().sendCurrentStamina(this.player);
                }
            }
        }
        //hồi phục 30s
        //hồi phục thể lực
    }

    public void setBasePoint() {
        setHpMax();
        setMpMax();
        setDame();
        setDef();
        setCrit();
        setHpHoi();
        setMpHoi();
        setNeDon();
        setCritDame();
        setSpeed();
        setAttributeOverLimit();
    }

    public void setAttributeOverLimit() {
        int max = Integer.MAX_VALUE;
        int min = -100000000;
        if (this.hpMax < 0) {
            if (this.hpMax < min) {
                this.hpMax = max;
            } else {
                this.hpMax = 1;
            }
        }
        if (this.mpMax < 0) {
            if (this.mpMax < min) {
                this.mpMax = max;
            } else {
                this.mpMax = 1;
            }
        }
        if (this.dame < 0) {
            if (this.dame < min) {
                this.dame = max;
            } else {
                this.dame = 1;
            }
        }
        if (this.def < 0) {
            if (this.def < min) {
                this.def = max;
            } else {
                this.def = 1;
            }
        }
        if (this.crit < 0) {
            if (this.crit < min) {
                this.crit = max;
            } else {
                this.crit = 1;
            }
        }
        setHp();
        setMp();
    }

    public long calPercent(long param, int percent) {
        return param * percent / 100;
    }

    public void dispose() {
        this.intrinsic = null;
        this.player = null;
        this.tlHp = null;
        this.tlMp = null;
        this.tlDef = null;
        this.tlDame = null;
        this.tlDameAttMob = null;
        this.tlSDDep = null;
        this.tlTNSM = null;
        this.tlDameCrit = null;
        this.tlSpeed = null;
    }
}
