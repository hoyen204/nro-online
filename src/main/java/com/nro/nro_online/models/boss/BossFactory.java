package com.nro.nro_online.models.boss;

import org.apache.log4j.Logger;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.boss.event.noel.*;
import com.nro.nro_online.models.boss.list_boss.*;
import com.nro.nro_online.models.boss.mabu_war.*;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.mabu.MabuWar;
import com.nro.nro_online.models.map.mabu.MabuWar14h;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.MapService;

public class BossFactory {

    public static final byte BASIL = -45;
    public static final byte BERGAMO = -46;
    public static final byte LAVENDER = -47;

    public static final byte SO4NM = -48;
    public static final byte SO3NM = -49;
    public static final byte SO2NM = -50;
    public static final byte SO1NM = -51;
    public static final byte TIEU_DOI_TRUONGNM = -52;
    public static final byte ROBIN = -53;

    public static final byte SO4 = -28;
    public static final byte SO3 = -29;
    public static final byte SO2 = -30;
    public static final byte SO1 = -31;
    public static final byte TIEU_DOI_TRUONG = -32;

    //id boss
    public static final byte BROLY = -1;
    public static final byte SUPER_BROLY = -2;
    public static final byte TRUNG_UY_TRANG = -3;
    public static final byte TRUNG_UY_XANH_LO = -4;
    public static final byte TRUNG_UD_THEP = -5;
    public static final byte NINJA_AO_TIM = -6;
    public static final byte NINJA_AO_TIM_FAKE_1 = -7;
    public static final byte NINJA_AO_TIM_FAKE_2 = -8;
    public static final byte NINJA_AO_TIM_FAKE_3 = -9;
    public static final byte NINJA_AO_TIM_FAKE_4 = -10;
    public static final byte NINJA_AO_TIM_FAKE_5 = -11;
    public static final byte NINJA_AO_TIM_FAKE_6 = -12;
    public static final byte ROBOT_VE_SI_1 = -13;
    public static final byte ROBOT_VE_SI_2 = -14;
    public static final byte ROBOT_VE_SI_3 = -15;
    public static final byte ROBOT_VE_SI_4 = -16;
    public static final byte XEN_BO_HUNG_1 = -17;
    public static final byte XEN_BO_HUNG_2 = -18;
    public static final byte XEN_BO_HUNG_HOAN_THIEN = -19;
    public static final byte XEN_BO_HUNG = -20;
    public static final byte SIEU_BO_HUNG = -22;
    public static final byte KUKU = -23;
    public static final byte MAP_DAU_DINH = -24;
    public static final byte RAMBO = -25;
    public static final byte COOLER = -26;
    public static final byte COOLER2 = -27;
    public static final byte FIDE_DAI_CA_1 = -33;
    public static final byte FIDE_DAI_CA_2 = -34;
    public static final byte FIDE_DAI_CA_3 = -35;
    public static final byte ANDROID_19 = -36;
    public static final byte ANDROID_20 = -37;
    public static final byte ANDROID_13 = -38;
    public static final byte ANDROID_14 = -39;
    public static final byte ANDROID_15 = -40;
    public static final byte PIC = -41;
    public static final byte POC = -42;
    public static final byte KINGKONG = -43;
    public static final byte SUPER_BROLY_RED = -44;
    public static final byte WHIS = -54;
    public static final byte BILL = -55;
    public static final byte CHILL = -56;
    public static final byte CHILL2 = -57;
    public static final byte BULMA = -58;
    public static final byte POCTHO = -59;
    public static final byte CHICHITHO = -60;
    public static final byte BLACKGOKU = -61;
    public static final byte SUPERBLACKGOKU = -62;
    public static final byte SANTA_CLAUS = -63;
    public static final byte MABU_MAP = -64;
    public static final byte SUPER_BU = -65;
    public static final byte BU_TENK = -66;
    public static final byte DRABULA_TANG1 = -67;
    public static final byte BUIBUI_TANG2 = -68;
    public static final byte BUIBUI_TANG3 = -69;
    public static final byte YACON_TANG4 = -70;
    public static final byte DRABULA_TANG5 = -71;
    public static final byte GOKU_TANG5 = -72;
    public static final byte CADIC_TANG5 = -73;
    public static final byte DRABULA_TANG6 = -74;
    public static final byte XEN_MAX = -75;
    public static final byte HOA_HONG = -76;
    public static final byte SOI_HEC_QUYN = -77;
    public static final byte O_DO = -78;
    public static final byte XINBATO = -79;
    public static final byte CHA_PA = -80;
    public static final byte PON_PUT = -81;
    public static final byte CHAN_XU = -82;
    public static final byte TAU_PAY_PAY = -83;
    public static final byte YAMCHA = -84;
    public static final byte JACKY_CHUN = -85;
    public static final byte THIEN_XIN_HANG = -86;
    public static final byte LIU_LIU = -87;
    public static final byte THIEN_XIN_HANG_CLONE = -88;
    public static final byte THIEN_XIN_HANG_CLONE1 = -89;
    public static final byte THIEN_XIN_HANG_CLONE2 = -90;
    public static final byte THIEN_XIN_HANG_CLONE3 = -91;
    public static final byte QILIN = -92;
    public static final byte NGO_KHONG = -93;
    public static final byte BAT_GIOI = -94;
    public static final byte FIDEGOLD = -95;
    public static final byte CUMBER = -96;
    public static final byte CUMBER2 = -97;

    public static final byte TAP_SU_1 = -104;
    public static final byte TAP_SU_2 = -105;
    public static final byte TAP_SU_3 = -106;
    public static final byte TAP_SU_4 = -107;
    public static final byte TAP_SU_5 = -108;

    public static final byte TAN_BINH_1 = -109;
    public static final byte TAN_BINH_2 = -110;
    public static final byte TAN_BINH_3 = -111;
    public static final byte TAN_BINH_4 = -112;
    public static final byte TAN_BINH_5 = -113;
    public static final byte TAN_BINH_6 = -114;

    public static final byte DOI_TRUONG_1 = -115;

    public static final byte CHIEN_BINH_1 = -98;
    public static final byte CHIEN_BINH_2 = -99;
    public static final byte CHIEN_BINH_3 = -100;
    public static final byte CHIEN_BINH_4 = -101;
    public static final byte CHIEN_BINH_5 = -102;
    public static final byte CHIEN_BINH_6 = -103;

    public static final byte THO_DAI_KA = -110;

    public static final byte KID_BU = -111;
    public static final byte BU_HAN = -112;
    public static final byte XEN_CON = -122;

    public static final byte RAITY = -123;

    public static final byte MA_TROI = -124;
    public static final byte BO_XUONG = -125;
    public static final byte DOI_NHI = -126;
    public static final byte WHIS_TOP = -127;

    public static final byte CLONE_PLAYER = -128;

    public static final int NOEL_BOSS_ONE = -129;
    public static final int NOEL_BOSS_TWO = -130;
    public static final int NOEL_BOSS_BALL = -131;
    public static final int NOEL_BOSS_BALL_2 = -132;
    public static final int NOEL_BOSS_BALL_3 = -133;
    public static final int NOEL_BOSS_BALL_4 = -134;
    public static final int NOEL_BOSS_BALL_5 = -135;

    public static final int CLONE_NHAN_BAN = -136;
    public static final int OMEGA_PLUS = -137;

    public static final int OMEGA_PLUS_DOUBLE = -138;
    public static final int SATAN_KING = -139;
    public static final int DRACULA_HUT_MAU = -140;

    private static final Logger logger = Logger.getLogger(BossFactory.class);
    public static final int[] MAP_APPEARED_QILIN = { ConstMap.VACH_NUI_ARU_42, ConstMap.VACH_NUI_MOORI_43, ConstMap.VACH_NUI_KAKAROT,
        ConstMap.LANG_ARU, ConstMap.LANG_MORI, ConstMap.LANG_KAKAROT, ConstMap.DOI_HOA_CUC, ConstMap.DOI_NAM_TIM, ConstMap.DOI_HOANG,
        ConstMap.TRAM_TAU_VU_TRU, ConstMap.TRAM_TAU_VU_TRU_25, ConstMap.TRAM_TAU_VU_TRU_26, ConstMap.LANG_PLANT, ConstMap.RUNG_NGUYEN_SINH,
        ConstMap.RUNG_CO, ConstMap.RUNG_THONG_XAYDA, ConstMap.RUNG_DA, ConstMap.THUNG_LUNG_DEN, ConstMap.BO_VUC_DEN, ConstMap.THANH_PHO_VEGETA,
        ConstMap.THUNG_LUNG_TRE, ConstMap.RUNG_NAM, ConstMap.RUNG_BAMBOO, ConstMap.RUNG_XUONG, ConstMap.RUNG_DUONG_XI, ConstMap.NAM_KAME,
        ConstMap.DAO_BULONG, ConstMap.DONG_KARIN, ConstMap.THI_TRAN_MOORI, ConstMap.THUNG_LUNG_MAIMA, ConstMap.NUI_HOA_TIM, ConstMap.NUI_HOA_VANG,
        ConstMap.NAM_GURU, ConstMap.DONG_NAM_GURU, ConstMap.THUNG_LUNG_NAMEC
    };

    private BossFactory() {

    }

    public static boolean isYar(byte id) {
        return (id == TAP_SU_1 || id == TAP_SU_2 || id == TAP_SU_3 || id == TAP_SU_4 || id == TAP_SU_5 || id == TAN_BINH_1 || id == TAN_BINH_2
                || id == TAN_BINH_3 || id == TAN_BINH_4 || id == TAN_BINH_5 || id == TAN_BINH_6 || id == DOI_TRUONG_1 || id == CHIEN_BINH_1 || id == CHIEN_BINH_2
                || id == CHIEN_BINH_3 || id == CHIEN_BINH_4 || id == CHIEN_BINH_5 || id == CHIEN_BINH_6);
    }

    public static void initBoss() {
        new Thread(() -> {
            try {
                createBoss(BASIL);
                createBoss(CUMBER);
                createBoss(BLACKGOKU);
                createBoss(CHILL);
                createBoss(WHIS);
                createBoss(COOLER);
                createBoss(XEN_BO_HUNG);
                createBoss(KUKU);
                createBoss(MAP_DAU_DINH);
                createBoss(RAMBO);
                createBoss(TIEU_DOI_TRUONG);
                createBoss(FIDE_DAI_CA_1);
                createBoss(ANDROID_20);
                createBoss(KINGKONG);
                createBoss(XEN_BO_HUNG_1);
                createBoss(OMEGA_PLUS);
                createBoss(DRACULA_HUT_MAU);
                createBoss(SATAN_KING);
                createBoss(SANTA_CLAUS);
                for (int i = 0; i < 20; i++) {
                    createBoss(BROLY);
                }
            } catch (Exception e) {
                logger.error("Err initboss", e);
            }
        }).start();
    }

    public static void initBossMabuWar14H() {
        new Thread(() -> {
            Map map = MapService.gI().getMapById(114);
            for (Zone zone : map.zones) {
                Boss boss = new Mabu_14H(114, zone.zoneId);
                MabuWar14h.gI().bosses.add(boss);
            }
            map = MapService.gI().getMapById(128);
            for (Zone zone : map.zones) {
                Boss boss = new SuperBu_14H(128, zone.zoneId);
                MabuWar14h.gI().bosses.add(boss);
            }
        }).start();
    }

    public static void initBossMabuWar() {
        new Thread(() -> {
            for (short mapid : BossData.DRABULA_TANG1.mapJoin) {
                Map map = MapService.gI().getMapById(mapid);
                for (Zone zone : map.zones) {
                    Boss boss = new Drabula_Tang1(mapid, zone.zoneId);
                    MabuWar.gI().bosses.add(boss);
                }
            }
            for (short mapid : BossData.DRABULA_TANG6.mapJoin) {
                Map map = MapService.gI().getMapById(mapid);
                for (Zone zone : map.zones) {
                    Boss boss = new Drabula_Tang6(mapid, zone.zoneId);
                    MabuWar.gI().bosses.add(boss);
                }
            }
            for (short mapid : BossData.GOKU_TANG5.mapJoin) {
                Map map = MapService.gI().getMapById(mapid);
                for (Zone zone : map.zones) {
                    Boss boss = new Goku_Tang5(mapid, zone.zoneId);
                    MabuWar.gI().bosses.add(boss);
                }
            }
            for (short mapid : BossData.CALICH_TANG5.mapJoin) {
                Map map = MapService.gI().getMapById(mapid);
                for (Zone zone : map.zones) {
                    Boss boss = new Calich_Tang5(mapid, zone.zoneId);
                    MabuWar.gI().bosses.add(boss);
                }
            }
            for (short mapid : BossData.BUIBUI_TANG2.mapJoin) {
                Map map = MapService.gI().getMapById(mapid);
                for (Zone zone : map.zones) {
                    Boss boss = new BuiBui_Tang2(mapid, zone.zoneId);
                    MabuWar.gI().bosses.add(boss);
                }
            }
            for (short mapid : BossData.BUIBUI_TANG3.mapJoin) {
                Map map = MapService.gI().getMapById(mapid);
                for (Zone zone : map.zones) {
                    Boss boss = new BuiBui_Tang3(mapid, zone.zoneId);
                    MabuWar.gI().bosses.add(boss);
                }
            }
            for (short mapid : BossData.YACON_TANG4.mapJoin) {
                Map map = MapService.gI().getMapById(mapid);
                for (Zone zone : map.zones) {
                    Boss boss = new Yacon_Tang4(mapid, zone.zoneId);
                    MabuWar.gI().bosses.add(boss);
                }
            }
        }).start();
    }

    public static Boss createBoss(int bossId) {
        Boss boss = null;
        switch (bossId) {
            case BROLY:
                boss = new Broly();
                break;
            case LAVENDER:
                boss = new Lavender();
                break;
            case BERGAMO:
                boss = new Bergamo();
                break;
            case BASIL:
                boss = new Basil();
                break;
            case CUMBER:
                boss = new Cumber();
                break;
            case CUMBER2:
                boss = new SuperCumber();
                break;
            case XEN_BO_HUNG_1:
                boss = new XenBoHung1();
                break;
            case XEN_BO_HUNG_2:
                boss = new XenBoHung2();
                break;
            case XEN_BO_HUNG_HOAN_THIEN:
                boss = new XenBoHungHoanThien();
                break;
            case XEN_CON:
                boss = new XenCon();
                break;
            case XEN_BO_HUNG:
                boss = new XenBoHung();
                break;
            case SIEU_BO_HUNG:
                boss = new SieuBoHung();
                break;
            case KUKU:
                boss = new Kuku();
                break;
            case MAP_DAU_DINH:
                boss = new MapDauDinh();
                break;
            case RAMBO:
                boss = new Rambo();
                break;
            case COOLER:
                boss = new Cooler();
                break;
            case COOLER2:
                boss = new Cooler2();
                break;
            case SO4:
                boss = new So4();
                break;
            case SO3:
                boss = new So3();
                break;
            case DRACULA_HUT_MAU:
                boss = new DraculaHutMau();
                break;
            case SATAN_KING:
                boss = new SatanKing();
                break;
            case OMEGA_PLUS:
                boss = new OmegaPlus();
                break;
            case SO2:
                boss = new So2();
                break;
            case SO1:
                boss = new So1();
                break;
            case TIEU_DOI_TRUONG:
                boss = new TieuDoiTruong();
                break;
            case FIDE_DAI_CA_1:
                boss = new FideDaiCa1();
                break;
            case FIDE_DAI_CA_2:
                boss = new FideDaiCa2();
                break;
            case FIDE_DAI_CA_3:
                boss = new FideDaiCa3();
                break;
            case ANDROID_19:
                boss = new Android19();
                break;
            case ANDROID_20:
                boss = new Android20();
                break;
            case POC:
                boss = new Poc();
                break;
            case PIC:
                boss = new Pic();
                break;
            case KINGKONG:
                boss = new KingKong();
                break;
            case WHIS:
                boss = new Whis();
                break;
            case BILL:
                boss = new Bill();
                break;
            case CHILL:
                boss = new Chill();
                break;
            case CHILL2:
                boss = new Chill2();
                break;
            case BLACKGOKU:
                boss = new Blackgoku();
                break;
            case SUPERBLACKGOKU:
                boss = new Superblackgoku();
                break;
            case MABU_MAP:
                boss = new Mabu_Tang6();
                break;
            case FIDEGOLD:
                boss = new FideGold();
                break;
        }
        return boss;
    }

    public static Boss createWhisBoss(long bossId, int level, long player_id) {
        return new WhisTop(bossId, level, player_id);
    }

    public static Boss createBossNhanBan(Player player, BossData data) {
        return new NhanBan(player, data);
    }

    public static NoelBoss createNoelBoss(long bossId, Player player) {
        NoelBoss boss = null;
        switch ((int) bossId) {
            case NOEL_BOSS_ONE -> {
                boss = new NoelBossOne();
            }
            case NOEL_BOSS_TWO -> {
                boss = new NoelBossTwo();
            }
        }
        if (boss != null) {
            boss.setStatus(Boss.ATTACK);
            boss.zone = player.zone;
            boss.typePk = ConstPlayer.PK_ALL;
            boss.location.x = player.location.x;
            boss.location.y = player.location.y;
            boss.joinMap();
            boss.AddPlayerCanAttack(player);
        }
        return boss;
    }

    public static NoelBossBall createNoelBossBall(long bossId, Player player, Boss baseBoss) {
        NoelBossBall boss = null;

        boss = new NoelBossBall(bossId, player);
        if (boss != null) {
            boss.setStatus(Boss.ATTACK);
            boss.zone = player.zone;
            boss.typePk = ConstPlayer.NON_PK;
            boss.location.x = baseBoss.location.x;
            boss.location.y = baseBoss.location.y;
            boss.joinMap();
        }

        return boss;
    }

}
