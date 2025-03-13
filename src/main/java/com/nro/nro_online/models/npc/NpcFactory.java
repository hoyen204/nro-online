package com.nro.nro_online.models.npc;

import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.nro.nro_online.services.func.SummonDragon.BLACK_SHENRON_SAY;
import static com.nro.nro_online.services.func.SummonDragon.BLACK_SHENRON_WISHES;
import static com.nro.nro_online.services.func.SummonDragon.ICE_SHENRON_SAY;
import static com.nro.nro_online.services.func.SummonDragon.ICE_SHENRON_WISHES;
import static com.nro.nro_online.services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static com.nro.nro_online.services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static com.nro.nro_online.services.func.SummonDragon.SHENRON_SAY;
import static nro.server.Manager.*;
import static nro.services.func.SummonDragon.*;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.dialog.ConfirmDialog;
import com.nro.nro_online.dialog.MenuDialog;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.npc.NpcForge.GhiDanh;
import com.nro.nro_online.models.npc.NpcForge.NoiBanh;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.PetService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.func.SummonDragon;
import com.nro.nro_online.utils.Log;
import nro.attr.AttributeManager;
import nro.consts.*;
import nro.dialog.ConfirmDialog;
import nro.dialog.MenuDialog;
import nro.jdbc.daos.PlayerDAO;
import nro.models.boss.BossFactory;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.Zone;
import nro.models.map.mabu.MabuWar;
import nro.models.npc.NpcForge.*;
import nro.models.player.Player;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.services.*;
import nro.services.func.*;
import nro.utils.Log;
import nro.utils.TimeUtil;
import nro.utils.Util;

/**
 * @stole Arriety
 */
public class NpcFactory {

    // playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        Npc npc = null;
        try {
            switch (tempId) {
                case ConstNpc.TO_SU_KAIO: {
                    npc = new ToSu(mapId, status, cx, cy, tempId, avartar);
                    break;
                }

                case ConstNpc.CUA_HANG_KY_GUI: {
                    npc = new CuaHangKyGui(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.TRONG_TAI: {
                    npc = new TrongTai(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.JACO: {
                    npc = new Jaco(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.POTAGE: {
                    npc = new Potage(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.FIDE: {
                    npc = new Fide(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.CADIC: {
                    npc = new Cadic(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.TORIBOT: {
                    npc = new Toribot(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.TAPION: {
                    npc = new Tapion(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.QUY_LAO_KAME: {
                    npc = new QuyLaoKame(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.TRUONG_LAO_GURU: {
                    npc = new TruongLaoGuru(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.VUA_VEGETA: {
                    npc = new VuaVegeta(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS: {
                    npc = new Trinova(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.BUNMA: {
                    npc = new Bunma(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.DENDE: {
                    npc = new Dende(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.APPULE: {
                    npc = new Appule(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.DR_DRIEF: {
                    npc = new Dr_Drief(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.CARGO: {
                    npc = new Cargo(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.CUI: {
                    npc = new Cui(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.SANTA: {
                    npc = new Santa(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.URON: {
                    npc = new Uron(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.BA_HAT_MIT: {
                    npc = new BaHatMit(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.RUONG_DO: {
                    npc = new RuongGo(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.DAU_THAN: {
                    npc = new DauThan(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.CALICK: {
                    npc = new Calick(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.GOKU_NOEL:
                    if (mapId == 106) {
                        npc = new EventNoel(mapId, status, cx, cy, tempId, avartar);
                    }
                    break;
                case ConstNpc.THAN_MEO_KARIN: {
                    npc = new ThanMeoKarin(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.THUONG_DE: {
                    npc = new ThuongDe(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.THAN_VU_TRU: {
                    npc = new ThanVuTru(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.KIBIT: {
                    npc = new Kibit(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.OSIN: {
                    npc = new Osin(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.BABIDAY: {
                    npc = new Babiday(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.LY_TIEU_NUONG: {
                    npc = new LyTieuNuong(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.LINH_CANH: {
                    npc = new LinhCanh(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.QUA_TRUNG: {
                    npc = new Egg(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.QUOC_VUONG: {
                    npc = new QuocVuong(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.BUNMA_TL: {
                    npc = new BunmaTuongLai(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.RONG_OMEGA: {
                    npc = new RongOmega(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S: {
                    npc = new NpcBlackBall(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.BILL: {
                    npc = new Bill(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.WHIS: {
                    npc = new Whis(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.BO_MONG: {
                    npc = new BoMong(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.GOKU_SSJ: {
                    npc = new Goku(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.GOKU_SSJ_: {
                    npc = new GokuSSJ(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.GHI_DANH: {
                    npc = new GhiDanh(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                case ConstNpc.NOI_BANH: {
                    npc = new NoiBanh(mapId, status, cx, cy, tempId, avartar);
                    break;
                }
                default:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Hi", ":3");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
                                            case 1:
                                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(NpcFactory.class, e, "Lỗi load npc");
        }
        return npc;
    }

    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1
                                && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY,
                                    SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2
                                && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY,
                                    SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    case ConstNpc.BLACK_SHENRON:
                        if (player.iDMark.getIndexMenu() == ConstNpc.BLACK_SHENRON
                                && select == BLACK_SHENRON_WISHES.length) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.BLACK_SHENRON, BLACK_SHENRON_SAY,
                                    BLACK_SHENRON_WISHES);
                            break;
                        }
                    case ConstNpc.ICE_SHENRON:
                        if (player.iDMark.getIndexMenu() == ConstNpc.ICE_SHENRON
                                && select == ICE_SHENRON_WISHES.length) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.ICE_SHENRON, ICE_SHENRON_SAY,
                                    ICE_SHENRON_WISHES);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.CONFIRM_DIALOG:
                        ConfirmDialog confirmDialog = player.getConfirmDialog();
                        if (confirmDialog != null) {
                            if (confirmDialog instanceof MenuDialog menu) {
                                menu.getRunnable().setIndexSelected(select);
                                menu.run();
                                return;
                            }
                            if (select == 0) {
                                confirmDialog.run();
                            } else {
                                confirmDialog.cancel();
                            }
                            player.setConfirmDialog(null);
                        }
                        break;
                    case ConstNpc.MENU_USE_ITEM_BLACK_GOKU:
                        switch (select) {
                            case 0:
                                Item capsule = InventoryService.gI().findItemBag(player, 2052);
                                if (capsule == null) {
                                    Service.getInstance().sendThongBao(player, "Item null");
                                    break;
                                }
                                PetService.gI().createBlackPet(player, player.gender);
                                InventoryService.gI().subQuantityItemsBag(player, capsule, 1);
                                break;
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1105:
                        switch (select) {
                            case 0:
                                IntrinsicService.gI().sattd(player);
                                break;
                            case 1:
                                IntrinsicService.gI().satnm(player);
                                break;
                            case 2:
                                IntrinsicService.gI().setxd(player);
                                break;
                            default:
                                break;
                        }
                        break;
                    case ConstNpc.menutd:
                        switch (select) {
                            case 0: {// set songoku
                                try {
                                    ItemService.gI().setSongoku(player);
                                } catch (Exception ex) {
                                    Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;
                            case 1:// set kaioken
                                try {
                                ItemService.gI().setKaioKen(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:// set thenxin hang
                                   try {
                                ItemService.gI().setThenXinHang(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.menunm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setLienHoan(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setPicolo(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setPikkoroDaimao(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menuxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setKakarot(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setCadic(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setNappa(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        break;
                    case ConstNpc.CONFIRM_ACTIVE:
                        switch (select) {
                            case 1:
                                if (player.getSession().vnd >= 10_000) {
                                    if (PlayerDAO.subVND2(player, 10_000)) {
                                        Service.getInstance().sendThongBao(player, "Đã mở thành viên thành công!");
                                    } else {
                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Số dư vnd không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                }
                                break;
                        }
                        break;
                    case ConstNpc.UP_TOP_ITEM:
                        break;
                    case ConstNpc.RUONG_GO:
                        int size = player.textRuongGo.size();
                        if (size > 0) {
                            String menuselect = "OK [" + (size - 1) + "]";
                            if (size == 1) {
                                menuselect = "OK";
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1,
                                    player.textRuongGo.get(size - 1), menuselect);
                            player.textRuongGo.remove(size - 1);
                        }
                        break;
                    case ConstNpc.MENU_MABU_WAR:
                        if (select == 0) {
                            if (player.zone.finishMabuWar) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                            } else if (player.zone.map.mapId == 119) {
                                Zone zone = MabuWar.gI().getMapLastFloor(120);
                                if (zone != null) {
                                    ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                }
                            } else {
                                int idMapNextFloor = player.zone.map.mapId == 115 ? player.zone.map.mapId + 2
                                        : player.zone.map.mapId + 1;
                                ChangeMapService.gI().changeMap(player, idMapNextFloor, -1, 354, 240);
                            }
                            player.resetPowerPoint();
                            player.sendMenuGotoNextFloorMabuWar = false;
                            Service.getInstance().sendPowerInfo(player, "TL", player.getPowerPoint());
                            if (Util.isTrue(1, 30)) {
                                player.inventory.ruby += 1;
                                PlayerService.gI().sendInfoHpMpMoney(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 1 Hồng Ngọc");
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Bạn đen vô cùng luôn nên không nhận được gì cả");
                            }
                        }
                        break;
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.MAKE_MATCH_PVP:
                        PVPServcice.gI().sendInvitePVP(player, (byte) select);
                        break;
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                try {
                                    int playerIdInt = Integer.parseInt(String.valueOf(playerId));
                                    FriendAndEnemyService.gI().acceptMakeFriend(player, playerIdInt);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPServcice.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.SUMMON_BLACK_SHENRON:
                        if (select == 0) {
                            SummonDragon.gI().summonBlackShenron(player);
                        }
                        break;
                    case ConstNpc.SUMMON_ICE_SHENRON:
                        if (select == 0) {
                            SummonDragon.gI().summonIceShenron(player);
                        }
                        break;
                    case ConstNpc.INTRINSIC:
                        switch (select) {
                            case 0:
                                IntrinsicService.gI().showAllIntrinsic(player);
                                break;
                            case 1:
                                IntrinsicService.gI().showConfirmOpen(player);
                                break;
                            case 2:
                                IntrinsicService.gI().showConfirmOpenVip(player);
                                break;
                            default:
                                break;
                        }
                        break;

                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player,
                                    "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;
                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.getInstance().sendThongBao(player, "Phát đệ tử cho "
                                        + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryService.gI().addItemBag(player, item, 0);
                                }
                                InventoryService.gI().sendItemBags(player);
                                break;
                            case 1:
                                player.getSession().logCheck = !player.getSession().logCheck;
                                break;
                            case 2:
                                Maintenance.gI().start(300);
                                break;
                            case 3:
                                Input.gI().createFormFindPlayer(player);
                                break;
                            case 4:
//                                NotiManager.getInstance().load();
//                                NotiManager.getInstance().sendAlert(player);
//                                NotiManager.getInstance().sendNoti(player);
//                                Service.getInstance().chat(player, "Cập nhật thông báo thành công");
                                this.createOtherMenu(player, ConstNpc.CALL_BOSS,
                                        "Chọn Boss?", "Full Cụm\nANDROID", "BLACK", "BROLY", "Cụm\nCell",
                                        "Cụm\nDoanh trại", "DOREMON", "FIDE", "FIDE\nBlack", "Cụm\nGINYU", "Cụm\nNAPPA", "NGỤC\nTÙ");
                                break;
                        }
                        break;
                    case ConstNpc.CALL_BOSS:
                        switch (select) {
                            case 0:
                                BossFactory.createBoss(BossFactory.ANDROID_13);
                                BossFactory.createBoss(BossFactory.ANDROID_14);
                                BossFactory.createBoss(BossFactory.ANDROID_15);
                                BossFactory.createBoss(BossFactory.ANDROID_19);
                                BossFactory.createBoss(BossFactory.ANDROID_20);
                                BossFactory.createBoss(BossFactory.KINGKONG);
                                BossFactory.createBoss(BossFactory.PIC);
                                BossFactory.createBoss(BossFactory.POC);
                                break;
                            case 1:
                                BossFactory.createBoss(BossFactory.BLACKGOKU);
                                break;
                            case 2:
                                BossFactory.createBoss(BossFactory.BROLY);
                                break;
                            case 3:
                                BossFactory.createBoss(BossFactory.XEN_BO_HUNG_1);
                                break;
                            case 4:
                                Service.getInstance().sendThongBao(player, "Không có boss");
                                break;
                            case 5:
                                Service.getInstance().sendThongBao(player, "Chua duoc update");
                                break;
                            case 6:
                                BossFactory.createBoss(BossFactory.FIDE_DAI_CA_1);
                                break;
                            case 7:
                                Service.getInstance().sendThongBao(player, "Coming sooonn");
                                break;
                            case 8:
                                BossFactory.createBoss(BossFactory.SO1);
                                break;
                            case 9:
                                BossFactory.createBoss(BossFactory.KUKU);
                                BossFactory.createBoss(BossFactory.MAP_DAU_DINH);
                                BossFactory.createBoss(BossFactory.RAMBO);
                                break;
                            case 10:
                                BossFactory.createBoss(BossFactory.CUMBER);
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0 -> {
                                ClanService.gI().RemoveClanAll(player);
                                Service.getInstance().sendThongBao(player, "Đã giải tán bang hội.");
                            }
                        }
                        break;

                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x,
                                                p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x,
                                                player.location.y);
                                    }
                                    break;
                                case 2:
                                    if (p != null) {
                                        Input.gI().createFormChangeName(player, p);
                                    }
                                    break;
                                case 3:
                                    if (p != null) {
                                        String[] selects = new String[]{"Đồng ý", "Hủy"};
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                                "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        };
    }

    public static void openMenuSuKien(Player player, Npc npc, int tempId, int select) {
        switch (Manager.EVENT_SEVER) {
            case 0:
                break;
            case 1:// hlw
                switch (select) {
                    case 0:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            Item keo = InventoryService.gI().finditemnguyenlieuKeo(player);
                            Item banh = InventoryService.gI().finditemnguyenlieuBanh(player);
                            Item bingo = InventoryService.gI().finditemnguyenlieuBingo(player);

                            if (keo != null && banh != null && bingo != null) {
                                Item GioBingo = ItemService.gI().createNewItem((short) 2016, 1);

                                // - Số item sự kiện có trong rương
                                InventoryService.gI().subQuantityItemsBag(player, keo, 10);
                                InventoryService.gI().subQuantityItemsBag(player, banh, 10);
                                InventoryService.gI().subQuantityItemsBag(player, bingo, 10);

                                GioBingo.itemOptions.add(new ItemOption(74, 0));
                                InventoryService.gI().addItemBag(player, GioBingo, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Đổi quà sự kiện thành công");
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Vui lòng chuẩn bị x10 Nguyên Liệu Kẹo, Bánh Quy, Bí Ngô để đổi vật phẩm sự kiện");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                        }
                        break;
//                    case 1:
//                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                            Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
//                            Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);
//
//                            if (ve != null && giokeo != null) {
//                                Item Hopmaquy = ItemService.gI().createNewItem((short) 2017, 1);
//                                // - Số item sự kiện có trong rương
//                                InventoryService.gI().subQuantityItemsBag(player, ve, 3);
//                                InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);
//
//                                Hopmaquy.itemOptions.add(new ItemOption(74, 0));
//                                InventoryService.gI().addItemBag(player, Hopmaquy, 0);
//                                InventoryService.gI().sendItemBags(player);
//                                Service.getInstance().sendThongBao(player, "Đổi quà sự kiện thành công");
//                            } else {
//                                Service.getInstance().sendThongBao(player,
//                                        "Vui lòng chuẩn bị x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
//                            }
//                        } else {
//                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
//                        }
//                        break;
//                    case 2:
//                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                            Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
//                            Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);
//                            Item hopmaquy = InventoryService.gI().finditemnguyenlieuHopmaquy(player);
//
//                            if (ve != null && giokeo != null && hopmaquy != null) {
//                                Item HopQuaHLW = ItemService.gI().createNewItem((short) 2012, 1);
//                                // - Số item sự kiện có trong rương
//                                InventoryService.gI().subQuantityItemsBag(player, ve, 3);
//                                InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);
//                                InventoryService.gI().subQuantityItemsBag(player, hopmaquy, 3);
//
//                                HopQuaHLW.itemOptions.add(new ItemOption(74, 0));
//                                HopQuaHLW.itemOptions.add(new ItemOption(30, 0));
//                                InventoryService.gI().addItemBag(player, HopQuaHLW, 0);
//                                InventoryService.gI().sendItemBags(player);
//                                Service.getInstance().sendThongBao(player,
//                                        "Đổi quà hộp quà sự kiện Halloween thành công");
//                            } else {
//                                Service.getInstance().sendThongBao(player,
//                                        "Vui lòng chuẩn bị x3 Hộp Ma Quỷ, x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
//                            }
//                        } else {
//                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
//                        }
//                        break;
                }
                break;
            case 2:// 20/11
                switch (select) {
                    case 3:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            int evPoint = player.event.getEventPoint();
                            if (evPoint >= 999) {
                                Item HopQua = ItemService.gI().createNewItem((short) 2021, 1);
                                player.event.setEventPoint(evPoint - 999);

                                HopQua.itemOptions.add(new ItemOption(74, 0));
                                HopQua.itemOptions.add(new ItemOption(30, 0));
                                InventoryService.gI().addItemBag(player, HopQua, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được Hộp Quà Teacher Day");
                            } else {
                                Service.getInstance().sendThongBao(player, "Cần 999 điểm tích lũy để đổi");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                        }
                        break;
                    default:
                        int n = 0;
                        switch (select) {
                            case 0:
                                n = 1;
                                break;
                            case 1:
                                n = 10;
                                break;
                            case 2:
                                n = 99;
                                break;
                        }

                        if (n > 0) {
                            Item bonghoa = InventoryService.gI().finditemBongHoa(player, n);
                            if (bonghoa != null) {
                                int evPoint = player.event.getEventPoint();
                                player.event.setEventPoint(evPoint + n);
                                ;
                                InventoryService.gI().subQuantityItemsBag(player, bonghoa, n);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + n + " điểm sự kiện");
                                int pre;
                                int next;
                                String text = null;
                                AttributeManager am = ServerManager.gI().getAttributeManager();
                                switch (tempId) {
                                    case ConstNpc.THAN_MEO_KARIN:
                                        pre = EVENT_COUNT_THAN_MEO / 999;
                                        EVENT_COUNT_THAN_MEO += n;
                                        next = EVENT_COUNT_THAN_MEO / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.TNSM, 3600);
                                            text = "Toàn bộ máy chủ tăng được 20% TNSM cho đệ tử khi đánh quái trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.QUY_LAO_KAME:
                                        pre = EVENT_COUNT_QUY_LAO_KAME / 999;
                                        EVENT_COUNT_QUY_LAO_KAME += n;
                                        next = EVENT_COUNT_QUY_LAO_KAME / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.VANG, 3600);
                                            text = "Toàn bộ máy chủ được tăng 100% vàng từ quái trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.THUONG_DE:
                                        pre = EVENT_COUNT_THUONG_DE / 999;
                                        EVENT_COUNT_THUONG_DE += n;
                                        next = EVENT_COUNT_THUONG_DE / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.KI, 3600);
                                            text = "Toàn bộ máy chủ được tăng 20% KI trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.THAN_VU_TRU:
                                        pre = EVENT_COUNT_THAN_VU_TRU / 999;
                                        EVENT_COUNT_THAN_VU_TRU += n;
                                        next = EVENT_COUNT_THAN_VU_TRU / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.HP, 3600);
                                            text = "Toàn bộ máy chủ được tăng 20% HP trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.BILL:
                                        pre = EVENT_COUNT_THAN_HUY_DIET / 999;
                                        EVENT_COUNT_THAN_HUY_DIET += n;
                                        next = EVENT_COUNT_THAN_HUY_DIET / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.SUC_DANH, 3600);
                                            text = "Toàn bộ máy chủ được tăng 20% Sức đánh trong 60 phút.";
                                        }
                                        break;
                                }
                                if (text != null) {
                                    Service.getInstance().sendThongBaoAllPlayer(text);
                                }

                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Cần ít nhất " + n + " bông hoa để có thể tặng");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Cần ít nhất " + n + " bông hoa để có thể tặng");
                        }
                }
                break;
            case 3:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    Item keogiangsinh = InventoryService.gI().finditemKeoGiangSinh(player);

                    if (keogiangsinh != null && keogiangsinh.quantity >= 99) {
                        Item tatgiangsinh = ItemService.gI().createNewItem((short) 649, 1);
                        // - Số item sự kiện có trong rương
                        InventoryService.gI().subQuantityItemsBag(player, keogiangsinh, 99);

                        tatgiangsinh.itemOptions.add(new ItemOption(74, 0));
                        tatgiangsinh.itemOptions.add(new ItemOption(30, 0));
                        InventoryService.gI().addItemBag(player, tatgiangsinh, 0);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được Tất,vớ giáng sinh");
                    } else {
                        Service.getInstance().sendThongBao(player,
                                "Vui lòng chuẩn bị x99 kẹo giáng sinh để đổi vớ tất giáng sinh");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                }
                break;
            case 4:
                switch (select) {
                    case 0:
                        if (!player.event.isReceivedLuckyMoney()) {
                            Calendar cal = Calendar.getInstance();
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            if (day >= 22 && day <= 24) {
                                Item goldBar = ItemService.gI().createNewItem((short) ConstItem.THOI_VANG,
                                        Util.nextInt(1, 3));
                                player.inventory.ruby += Util.nextInt(10, 30);
                                goldBar.quantity = Util.nextInt(1, 3);
                                InventoryService.gI().addItemBag(player, goldBar, 99999);
                                InventoryService.gI().sendItemBags(player);
                                PlayerService.gI().sendInfoHpMpMoney(player);
                                player.event.setReceivedLuckyMoney(true);
                                Service.getInstance().sendThongBao(player,
                                        "Nhận lì xì thành công,chúc bạn năm mới dui dẻ");
                            } else if (day > 24) {
                                Service.getInstance().sendThongBao(player, "Hết tết rồi còn đòi lì xì");
                            } else {
                                Service.getInstance().sendThongBao(player, "Đã tết đâu mà đòi lì xì");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn đã nhận lì xì rồi");
                        }
                        break;
                    case 1:
                        break;
                }
                break;
            case ConstEvent.SU_KIEN_8_3:
                switch (select) {
                    case 3:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            int evPoint = player.event.getEventPoint();
                            if (evPoint >= 999) {
                                Item capsule = ItemService.gI().createNewItem((short) 2052, 1);
                                player.event.setEventPoint(evPoint - 999);
                                capsule.itemOptions.add(new ItemOption(74, 0));
                                capsule.itemOptions.add(new ItemOption(30, 0));
                                InventoryService.gI().addItemBag(player, capsule, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được Capsule Hồng");
                            } else {
                                Service.getInstance().sendThongBao(player, "Cần 999 điểm tích lũy để đổi");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                        }
                        break;
                    default:
                        int n = 0;
                        switch (select) {
                            case 0:
                                n = 1;
                                break;
                            case 1:
                                n = 10;
                                break;
                            case 2:
                                n = 99;
                                break;
                        }
                        if (n > 0) {
                            Item bonghoa = InventoryService.gI().finditemBongHoa(player, n);
                            if (bonghoa != null) {
                                int evPoint = player.event.getEventPoint();
                                player.event.setEventPoint(evPoint + n);
                                InventoryService.gI().subQuantityItemsBag(player, bonghoa, n);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + n + " điểm sự kiện");
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Cần ít nhất " + n + " bông hoa để có thể tặng");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Cần ít nhất " + n + " bông hoa để có thể tặng");
                        }
                }
                break;
        }
    }

    public static String getMenuSuKien(int id) {
        switch (id) {
            case ConstEvent.KHONG_CO_SU_KIEN:
                return "Chưa có\n Sự Kiện";
            case ConstEvent.SU_KIEN_HALLOWEEN:
                return "Sự Kiện\nHaloween";
            case ConstEvent.SU_KIEN_20_11:
                return "Sự Kiện\n 20/11";
            case ConstEvent.SU_KIEN_NOEL:
                return "Sự Kiện\n Giáng Sinh";
            case ConstEvent.SU_KIEN_TET:
                return "Sự Kiện\n Tết Nguyên\nĐán 2023";
            case ConstEvent.SU_KIEN_8_3:
                return "Sự Kiện\n 8/3";
        }
        return "Chưa có\n Sự Kiện";
    }

    public static String getMenuLamBanh(Player player, int type) {
        switch (type) {
            case 0:// bánh tét
                if (player.event.isCookingTetCake()) {
                    int timeCookTetCake = player.event.getTimeCookTetCake();
                    if (timeCookTetCake == 0) {
                        return "Lấy bánh";
                    } else if (timeCookTetCake > 0) {
                        return "Đang nấu\nBánh Trung Thu 1 Trứng\nCòn " + TimeUtil.secToTime(timeCookTetCake);
                    }
                } else {
                    return "Nấu\nBánh Trung Thu 1 Trứng";
                }
                break;
            case 1:// bánh chưng
                if (player.event.isCookingChungCake()) {
                    int timeCookChungCake = player.event.getTimeCookChungCake();
                    if (timeCookChungCake == 0) {
                        return "Lấy bánh";
                    } else if (timeCookChungCake > 0) {
                        return "Đang nấu\nBánh Trung Thu 2 Trứng\nCòn " + TimeUtil.secToTime(timeCookChungCake);
                    }
                } else {
                    return "Nấu\nBánh Trung Thu 2 Trứng";
                }
                break;
        }
        return "";
    }
}
