package com.nro.nro_online.services.func;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.nro.nro_online.consts.ConstEvent;
import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.dialog.MenuDialog;
import com.nro.nro_online.dialog.MenuRunnable;
import com.nro.nro_online.event.Event;
import com.nro.nro_online.lib.RandomCollection;
import com.nro.nro_online.manager.NamekBallManager;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.Map;
import com.nro.nro_online.models.map.NamekBall;
import com.nro.nro_online.models.map.Satellite;
import com.nro.nro_online.models.map.SatelliteDefense;
import com.nro.nro_online.models.map.SatelliteExp;
import com.nro.nro_online.models.map.SatelliteHP;
import com.nro.nro_online.models.map.SatelliteMP;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.dungeon.zones.ZSnakeRoad;
import com.nro.nro_online.models.map.war.NamekBallWar;
import com.nro.nro_online.models.npc.specialnpc.EggLinhThu;
import com.nro.nro_online.models.player.Inventory;
import com.nro.nro_online.models.player.MiniPet;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.services.EffectSkillService;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.ItemTimeService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.PetService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;

public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;
    private static final Logger logger = Logger.getLogger(UseItem.class);

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(Session session, Message msg) {
        Player player = session.player;
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryService.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryService.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryService.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryService.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryService.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryService.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryService.gI().itemPetBodyToBag(player, index);
                    break;
            }
            player.setClothes.setup();
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.getInstance().point(player);
        } catch (Exception e) {
            Log.error(UseItem.class, e);
        }
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        try {
            byte type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            if (index >= 0 && index < player.inventory.itemsBag.size()) {
                                Item item = player.inventory.itemsBag.get(index);
                                if (item.isNotNullItem()) {
                                    if (item.template.type == 21) {
                                        MiniPet.callMiniPet(player, item.template.id);
                                        InventoryService.gI().itemBagToBody(player, index);
                                        break;
                                    }
                                    if (item.template.type == 22) {
                                        msg = new Message(-43);
                                        msg.writer().writeByte(type);
                                        msg.writer().writeByte(where);
                                        msg.writer().writeByte(index);
                                        msg.writer().writeUTF("Bạn có muốn dùng "
                                                + player.inventory.itemsBag.get(index).template.name + "?");
                                        player.sendMessage(msg);
                                        msg.close();
                                    } else if (item.template.type == 7) {
                                        msg = new Message(-43);
                                        msg.writer().writeByte(type);
                                        msg.writer().writeByte(where);
                                        msg.writer().writeByte(index);
                                        msg.writer().writeUTF("Bạn chắc chắn học "
                                                + player.inventory.itemsBag.get(index).template.name + "?");
                                        player.sendMessage(msg);
                                    } else if (player.isVersionAbove(220) && item.template.type == 23
                                            || item.template.type == 24 || item.template.type == 11) {
                                        InventoryService.gI().itemBagToBody(player, index);
                                    } else if (item.template.id == 401) {
                                        msg = new Message(-43);
                                        msg.writer().writeByte(type);
                                        msg.writer().writeByte(where);
                                        msg.writer().writeByte(index);
                                        msg.writer().writeUTF(
                                                "Sau khi đổi đệ sẽ mất toàn bộ trang bị trên người đệ tử nếu chưa tháo");
                                        player.sendMessage(msg);
                                    } else {
                                        useItem(player, item, index);
                                    }
                                }
                            }
                        } else {
                            InventoryService.gI().eatPea(player);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item = null;
                        if (where == 0) {
                            if (index >= 0 && index < player.inventory.itemsBody.size()) {
                                item = player.inventory.itemsBody.get(index);
                            }
                        } else {
                            if (index >= 0 && index < player.inventory.itemsBag.size()) {
                                item = player.inventory.itemsBag.get(index);
                            }
                        }
                        if (item != null && item.isNotNullItem()) {
                            msg = new Message(-43);
                            msg.writer().writeByte(type);
                            msg.writer().writeByte(where);
                            msg.writer().writeByte(index);
                            msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                            player.sendMessage(msg);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    Service.getInstance().point(player);
                    InventoryService.gI().throwItem(player, where, index);
                    break;
                case ACCEPT_USE_ITEM:
                    if (index >= 0 && index < player.inventory.itemsBag.size()) {
                        Item item = player.inventory.itemsBag.get(index);
                        if (item.isNotNullItem()) {
                            useItem(player, item, index);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Log.error(UseItem.class, e);
        }
    }

    public void useSatellite(Player player, Item item) {
        Satellite satellite = null;
        if (player.zone != null) {
            int count = player.zone.getSatellites().size();
            if (count < 3) {
                switch (item.template.id) {
                    case ConstItem.VE_TINH_TRI_LUC:
                        satellite = new SatelliteMP(player.zone, ConstItem.VE_TINH_TRI_LUC, player.location.x,
                                player.location.y, player);
                        break;
                    case ConstItem.VE_TINH_TRI_TUE:
                        satellite = new SatelliteExp(player.zone, ConstItem.VE_TINH_TRI_TUE, player.location.x,
                                player.location.y, player);
                        break;

                    case ConstItem.VE_TINH_PHONG_THU:
                        satellite = new SatelliteDefense(player.zone, ConstItem.VE_TINH_PHONG_THU, player.location.x,
                                player.location.y, player);
                        break;

                    case ConstItem.VE_TINH_SINH_LUC:
                        satellite = new SatelliteHP(player.zone, ConstItem.VE_TINH_SINH_LUC, player.location.x,
                                player.location.y, player);
                        break;
                }
                if (satellite != null) {
                    InventoryService.gI().subQuantityItemsBag(player, item, 1);
                    Service.getInstance().dropItemMapForMe(player, satellite);
                    Service.getInstance().dropItemMap(player.zone, satellite);
                }
            } else {
                Service.getInstance().sendThongBaoOK(player,
                        "Số lượng vệ tinh có thể đặt trong khu vực đã đạt mức tối đa.");
            }
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (Event.isEvent() && Event.getInstance().useItem(pl, item)) {
            return;
        }
        if (item.template.strRequire <= pl.nPoint.power) {
            int type = item.getType();
            switch (type) {
                case 6:
                    InventoryService.gI().eatPea(pl);
                    break;
                case 33:
                    RadaService.getInstance().useItemCard(pl, item);
                    break;
                case 22:
                    useSatellite(pl, item);
                    break;
                case 99:
                    break;
                default:
                    switch (item.template.id) {
                        case 457:
                            UseItem.gI().UseThoiVang(pl);
                            break;
                        case 737:
                            openboxsukien(pl, item, 5);
                            break;
                        case ConstItem.BON_TAM_GO:
                        case ConstItem.BON_TAM_VANG:
                            useBonTam(pl, item);
                            break;
                        case ConstItem.GOI_10_RADA_DO_NGOC:
                            findNamekBall(pl, item);
                            break;
                        case 2001:
                            capsule8thang3(pl, item);
                            break;
                        case ConstItem.CAPSULE_THOI_TRANG_30_NGAY:
                            capsuleThoiTrang(pl, item);
                            break;
                        case 570:
                            openWoodChest(pl, item);
                            break;
                        case 648:
                            openboxsukien(pl, item, 3);
                            break;
                        case 2024:
                            hopQuaTanThu(pl, item);
                            break;
                        case 992:
                            ChangeMapService.gI().goToPrimaryForest(pl);
                            break;
                        case 2023:
                            if (!pl.getSession().actived) {
                                Service.getInstance().sendThongBao(pl,
                                        "Vui lòng kích hoạt tài khoản để có thể sử dụng");
                                return;
                            }
                            Input.gI().createFormTangRuby(pl);
                            break;
                        case 2052:
                            UseItem.gI().blackGoku(pl);
                            break;
                        case 2020: // phiếu cải trang 20/10
                            openbox2010(pl, item);
                            break;
                        case 2021:
                            openboxsukien(pl, item, 2);
                            break;
                        case 2037:
                            // openboxsukien(pl, item, 6);
                            Service.getInstance().sendThongBao(pl, "Su kien da ket thuc");
                            break;
                        case 2043:
                            openboxsukien(pl, item, 7);
                            break;
                        case 736:
                            openboxsukien(pl, item, 8);
                            break;
                        case 397:
                            openboxsukien(pl, item, 9);
                            break;
                        case 398:
                            openboxsukien(pl, item, 10);
                            break;
                        case 211: // nho tím
                        case 212: // nho xanh
                            eatGrapes(pl, item);
                            break;
                        case ConstItem.CAPSULE_VANG:// 574
                            openboxsukien(pl, item, 11);
                            break;
                        case 380: // cskb
                            openCSKB(pl, item);
                            break;
                        case 381: // cuồng nộ
                        case 382: // bổ huyết
                        case 383: // bổ khí
                        case 384: // giáp xên
                        case 385: // ẩn danh
                        case 379: // máy dò
                        case 663: // bánh pudding
                        case 664: // xúc xíc
                        case 665: // kem dâu
                        case 666: // mì ly
                        case 667: // sushi
                        case 1150:
                        case 1152:
                        case 1153:
                        case 1151:
                        case 465:
                        case 466:
                        case 2044:
                        case 638:
                            useItemTime(pl, item);
                            break;
                        case 521: // tdlt
                            useTDLT(pl, item);
                            break;
                        case 454: // bông tai
                            usePorata(pl);
                            break;
                        case 921: // bông tai c2
                            UseItem.gI().usePorata2(pl);
                            break;
                        case 1995:// bong tai
                            usePorata3(pl);
                            break;
                        case 193: // gói 10 viên capsule
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        case 194: // capsule đặc biệt
                            openCapsuleUI(pl);
                            break;
                        case 401: // đổi đệ tử
                            changePet(pl, item);
                            break;
                        case 402: // sách nâng chiêu 1 đệ tử
                        case 403: // sách nâng chiêu 2 đệ tử
                        case 404: // sách nâng chiêu 3 đệ tử
                        case 759: // sách nâng chiêu 4 đệ tử
                        case 2038:
                            upSkillPet(pl, item);
                            break;
                        case 2042:
                            if (pl.egglinhthu == null) {
                                UseItem.gI().openEggLinhThu(pl, item);
                            } else {
                                Service.getInstance().sendThongBao(pl, "Vui lòng mở trứng trước!");
                            }
                            break;
                        case 1228:
                            UseItem.gI().hopQuaKichHoat(pl, item);
                            break;
                        case ConstItem.CAPSULE_TET_2022:
                            openCapsuleTet2022(pl, item);
                            break;
                        case 2004:
                            if (pl.pet == null) {
                                Service.getInstance().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                break;
                            }
                            if (pl.pet.playerSkill.skills.get(1).skillId != -1
                                    && pl.pet.playerSkill.skills.get(2).skillId != -1) {
                                pl.pet.openSkill2();
                                pl.pet.openSkill3();
                                InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                InventoryService.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Đã đổi thành công chiêu 2 3 đệ tử");
                            } else {
                                Service.getInstance().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 3 chứ!");
                            }
                            break;
                        case 2039:
                            if (pl.pet == null) {
                                Service.getInstance().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                break;
                            }
                            if (pl.pet.playerSkill.skills.get(1).skillId != -1
                                    && pl.pet.playerSkill.skills.get(2).skillId != -1
                                    && pl.pet.playerSkill.skills.get(3).skillId != -1) {
                                pl.pet.openSkill5();
                                InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                InventoryService.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Đã đổi thành công chiêu 5 đệ tử");
                            } else {
                                Service.getInstance().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 4 chứ!");
                            }
                            break;
                        case 2005:
                            if (pl.pet == null) {
                                Service.getInstance().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                break;
                            }
                            if (pl.pet.playerSkill.skills.get(1).skillId != -1
                                    && pl.pet.playerSkill.skills.get(3).skillId != -1) {
                                pl.pet.openSkill3();
                                pl.pet.openSkill4();
                                InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                InventoryService.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Đã đổi thành công chiêu 2 3 4 đệ tử");
                            } else {
                                Service.getInstance().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 4 chứ!");
                            }
                            break;
                        case 2010:
                            if (pl.pet == null) {
                                Service.getInstance().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                break;
                            }
                            if (pl.pet.playerSkill.skills.get(1).skillId != -1
                                    && pl.pet.playerSkill.skills.get(4).skillId != -1) {
                                pl.pet.openSkill4();
                                pl.pet.openSkill5();
                                InventoryService.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                InventoryService.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Đã đổi thành công chiêu 4, 5 đệ tử");
                            } else {
                                Service.getInstance().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 5 chứ!");
                            }
                            break;
                        case 2011:
                            Service.getInstance().addSMTN(pl, (byte) 2, 200_000_000, false);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            break;
                        case 2012:
                            Service.getInstance().addSMTN(pl.pet, (byte) 2, 200_000_000, false);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            break;
                        default:
                            switch (item.template.type) {
                                case 7: // sách học, nâng skill
                                    learnSkill(pl, item);
                                    break;
                                case 12: // ngọc rồng các loại
                                    // Service.getInstance().sendThongBaoOK(pl, "Bảo trì tính năng.");
                                    controllerCallRongThan(pl, item);
                                    break;
                                case 11: // item flag bag
                                    useItemChangeFlagBag(pl, item);
                                    break;
                                case 72: {
                                    InventoryService.gI().itemBagToBody(pl, indexBag);
                                    Service.getInstance().sendPetFollow(pl, (short) (item.template.iconID - 1));
                                    break;
                                }
                            }
                    }
                    break;
            }
            InventoryService.gI().sendItemBags(pl);
        } else {
            Service.getInstance().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
        }
    }

    private void UseThoiVang(Player player) {
        Item tv = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 457) {
                tv = item;
                break;
            }
        }
        if (tv != null /* && tv.isNotNullItem() */) {
            if (player.inventory.gold <= player.inventory.getGoldLimit()) {
                InventoryService.gI().subQuantityItemsBag(player, tv, 1);
                player.inventory.gold += 500_000_000;
                PlayerService.gI().sendInfoHpMpMoney(player);
                InventoryService.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Bạn đã sửa dụng quá giới hạn vàng");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void findNamekBall(Player pl, Item item) {
        List<NamekBall> balls = NamekBallManager.gI().getList();
        StringBuilder sb = new StringBuilder();
        for (NamekBall namekBall : balls) {
            if (namekBall.zone != null) {
                Map m = namekBall.zone.map;
                sb.append(namekBall.getIndex() + 1).append(" Sao: ").append(m.mapName)
                        .append(namekBall.getHolderName() == null ? "" : " - " + namekBall.getHolderName())
                        .append("\n");
            } else {
                Service.getInstance().sendThongBao(pl, "Đã xảy ra lỗi");
            }
        }
        final int star = Util.nextInt(0, 6);
        final NamekBall ball = NamekBallManager.gI().findByIndex(star);
        if (ball == null) {
            Service.getInstance().sendThongBao(pl, "Dit nhau au au");
            return;
        }
        final Inventory inventory = pl.inventory;
        MenuDialog menu = new MenuDialog(sb.toString(),
                new String[] { "Đến ngay\nViên " + (star + 1) + " Sao\n 50tr Vàng",
                        "Đến ngay\nViên " + (star + 1) + " Sao\n 5 Hồng ngọc" },
                new MenuRunnable() {
                    @Override
                    public void run() {
                        switch (getIndexSelected()) {
                            case 0:
                                if (inventory.gold < 50000000) {
                                    Service.getInstance().sendThongBao(pl, "Không đủ tiền");
                                    return;
                                }
                                inventory.subGold(50000000);
                                ChangeMapService.gI().changeMap(pl, ball.zone, ball.x, ball.y);
                                break;
                            case 1:
                                if (inventory.ruby < 5) {
                                    Service.getInstance().sendThongBao(pl, "Không đủ tiền");
                                    return;
                                }
                                inventory.subRuby(5);
                                ChangeMapService.gI().changeMap(pl, ball.zone, ball.x, ball.y);
                                break;
                        }
                        if (pl.isHoldNamecBall) {
                            NamekBallWar.gI().dropBall(pl);
                        }
                        Service.getInstance().sendMoney(pl);
                    }
                });
        menu.show(pl);
        InventoryService.gI().sendItemBags(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    }

    private void capsuleThoiTrang(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            Item it = ItemService.gI().createNewItem(
                    (short) Util.nextInt(ConstItem.CAI_TRANG_GOKU_THOI_TRANG, ConstItem.CAI_TRANG_CA_DIC_THOI_TRANG));
            it.itemOptions.add(new ItemOption(50, 30));
            it.itemOptions.add(new ItemOption(77, 30));
            it.itemOptions.add(new ItemOption(103, 30));
            it.itemOptions.add(new ItemOption(106, 0));
            InventoryService.gI().addItemBag(pl, it, 0);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
            short icon1 = item.template.iconID;
            short icon2 = it.template.iconID;
            CombineServiceNew.gI().sendEffectOpenItem(pl, icon1, icon2);
        } else {
            Service.getInstance().sendThongBao(pl, "Hãy chừa 1 ô trống để mở.");
        }

    }

    private void useBonTam(Player player, Item item) {
        if (!player.zone.map.isMapLang()) {
            Service.getInstance().sendThongBaoOK(player, "Chỉ có thể sự dụng ở các làng");
            return;
        }
        if (player.event.isUseBonTam()) {
            Service.getInstance().sendThongBaoOK(player, "Không thể sử dụng khi đang tắm");
            return;
        }
        int itemID = item.template.id;
        RandomCollection<Integer> rd = new RandomCollection<>();
        rd.add(1, ConstItem.QUAT_BA_TIEU);
        rd.add(1, ConstItem.CAY_KEM);
        rd.add(1, ConstItem.CA_HEO);
        rd.add(1, ConstItem.DIEU_RONG);

        if (itemID == ConstItem.BON_TAM_GO) {
            rd.add(1, ConstItem.CON_DIEU);
        } else {// bồn tắm vàng
            rd.add(1, ConstItem.XIEN_CA);
            rd.add(1, ConstItem.PHONG_LON);
            rd.add(1, ConstItem.CAI_TRANG_POC_BIKINI_2023);
            rd.add(1, ConstItem.CAI_TRANG_PIC_THO_LAN_2023);
            rd.add(1, ConstItem.CAI_TRANG_KING_KONG_SANH_DIEU_2023);
        }

        int rwID = rd.next();
        Item rw = ItemService.gI().createNewItem((short) rwID);
        if (rw.template.type == 11) {// đồ đeo lưng
            // option
            rw.itemOptions.add(new ItemOption(50, Util.nextInt(5, 15)));
            rw.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
            rw.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
            rw.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
        } else {// cải trang
            // option
            rw.itemOptions.add(new ItemOption(50, Util.nextInt(20, 40)));
            rw.itemOptions.add(new ItemOption(77, Util.nextInt(20, 40)));
            rw.itemOptions.add(new ItemOption(103, Util.nextInt(20, 40)));
            rw.itemOptions.add(new ItemOption(199, 0));
            rw.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
        }

        if (rwID == ConstItem.QUAT_BA_TIEU || rwID == ConstItem.VO_OC || rwID == ConstItem.CAY_KEM
                || rwID == ConstItem.CA_HEO) {
            // hsd
            rw.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));

        } else if (rwID == ConstItem.XIEN_CA || rwID == ConstItem.PHONG_LON
                || rwID == ConstItem.CAI_TRANG_POC_BIKINI_2023
                || rwID == ConstItem.CAI_TRANG_PIC_THO_LAN_2023
                || rwID == ConstItem.CAI_TRANG_KING_KONG_SANH_DIEU_2023) {
            // hsd - vinh vien
            if (Util.isTrue(1, 30)) {
                rw.itemOptions.add(new ItemOption(174, 2023));
            } else {
                rw.itemOptions.add(new ItemOption(174, 2023));
                rw.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
            }

        }

        int delay = itemID == ConstItem.BON_TAM_GO ? 3 : 1;
        ItemTimeService.gI().sendItemTime(player, 3779, 60 * delay);
        EffectSkillService.gI().startStun(player, System.currentTimeMillis(), 60000 * delay);
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBags(player);
        player.event.setUseBonTam(true);
        Util.setTimeout(() -> {
            InventoryService.gI().addItemBag(player, rw, 99);
            InventoryService.gI().sendItemBags(player);
            player.event.setUseBonTam(false);
        }, 60000 * delay);
    }

    private void openCapsuleTet2022(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
            Service.getInstance().sendThongBao(pl, "Hãy chừa 1 ô trống để mở.");
            return;
        }
        RandomCollection<Integer> rdItemID = new RandomCollection<>();
        rdItemID.add(1, ConstItem.PHAO_HOA);
        rdItemID.add(1, ConstItem.CAY_TRUC);
        rdItemID.add(1, ConstItem.NON_HO_VANG);
        switch (pl.gender) {
            case 0:
                rdItemID.add(1, ConstItem.NON_TRAU_MAY_MAN);
                rdItemID.add(1, ConstItem.NON_CHUOT_MAY_MAN);
                break;
            case 1:
                rdItemID.add(1, ConstItem.NON_TRAU_MAY_MAN_847);
                rdItemID.add(1, ConstItem.NON_CHUOT_MAY_MAN_755);
                break;
            default:
                rdItemID.add(1, ConstItem.NON_TRAU_MAY_MAN_848);
                rdItemID.add(1, ConstItem.NON_CHUOT_MAY_MAN_756);
                break;
        }
        rdItemID.add(1, ConstItem.CAI_TRANG_HO_VANG);
        rdItemID.add(1, ConstItem.HO_MAP_VANG);
        // rdItemID.add(2, ConstItem.SAO_PHA_LE);
        // rdItemID.add(2, ConstItem.SAO_PHA_LE_442);
        // rdItemID.add(2, ConstItem.SAO_PHA_LE_443);
        // rdItemID.add(2, ConstItem.SAO_PHA_LE_444);
        // rdItemID.add(2, ConstItem.SAO_PHA_LE_445);
        // rdItemID.add(2, ConstItem.SAO_PHA_LE_446);
        // rdItemID.add(2, ConstItem.SAO_PHA_LE_447);
        rdItemID.add(2, ConstItem.DA_LUC_BAO);
        rdItemID.add(2, ConstItem.DA_SAPHIA);
        rdItemID.add(2, ConstItem.DA_TITAN);
        rdItemID.add(2, ConstItem.DA_THACH_ANH_TIM);
        rdItemID.add(2, ConstItem.DA_RUBY);
        rdItemID.add(3, ConstItem.VANG_190);
        int itemID = rdItemID.next();
        Item newItem = ItemService.gI().createNewItem((short) itemID);
        switch (newItem.template.type) {
            case 9:
                newItem.quantity = Util.nextInt(10, 50) * 1000000;
                break;
            case 14:
            case 30:
                newItem.quantity = 10;
                break;
            default:
                switch (itemID) {
                    case ConstItem.CAY_TRUC: {
                        RandomCollection<ItemOption> rdOption = new RandomCollection<>();
                        rdOption.add(2, new ItemOption(77, 15));// %hp
                        rdOption.add(2, new ItemOption(103, 15));// %hp
                        rdOption.add(1, new ItemOption(50, 15));// %hp
                        newItem.itemOptions.add(rdOption.next());
                    }
                        break;

                    case ConstItem.HO_MAP_VANG: {
                        newItem.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                        newItem.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                        newItem.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                    }
                        break;

                    case ConstItem.NON_HO_VANG:
                    case ConstItem.CAI_TRANG_HO_VANG:
                    case ConstItem.NON_TRAU_MAY_MAN:
                    case ConstItem.NON_TRAU_MAY_MAN_847:
                    case ConstItem.NON_TRAU_MAY_MAN_848:
                    case ConstItem.NON_CHUOT_MAY_MAN:
                    case ConstItem.NON_CHUOT_MAY_MAN_755:
                    case ConstItem.NON_CHUOT_MAY_MAN_756:
                        newItem.itemOptions.add(new ItemOption(77, 30));
                        newItem.itemOptions.add(new ItemOption(103, 30));
                        newItem.itemOptions.add(new ItemOption(50, 30));
                        break;
                }
                RandomCollection<Integer> rdDay = new RandomCollection<>();
                rdDay.add(6, 3);
                rdDay.add(3, 7);
                rdDay.add(1, 15);
                int day = rdDay.next();
                newItem.itemOptions.add(new ItemOption(93, day));
                break;
        }
        short icon1 = item.template.iconID;
        short icon2 = newItem.template.iconID;
        if (newItem.template.type == 9) {
            Service.getInstance().sendThongBao(pl,
                    "Bạn nhận được " + Util.numberToMoney(newItem.quantity) + " " + newItem.template.name);
        } else if (newItem.quantity == 1) {
            Service.getInstance().sendThongBao(pl, "Bạn nhận được " + newItem.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn nhận được x" + newItem.quantity + " " + newItem.template.name);
        }
        CombineServiceNew.gI().sendEffectOpenItem(pl, icon1, icon2);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().addItemBag(pl, newItem, 99);
        InventoryService.gI().sendItemBags(pl);
    }

    private int randClothes(int level) {
        return ConstItem.LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][level - 1];
    }

    private void openWoodChest(Player pl, Item item) {
        int time = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.TimeUnit.DAY);
        if (time != 0) {
            Item itemReward = null;
            int param = item.itemOptions.getFirst().param;
            int gold = 0;
            int[] listItem = { 441, 442, 443, 444, 445, 446, 447, 457, 457, 457, 223, 224, 225 };
            int[] listClothesReward;
            int[] listItemReward;
            String text = "Bạn nhận được\n";
            if (param < 8) {
                gold = 100000000 * param;
                listClothesReward = new int[] { randClothes(param) };
                listItemReward = Util.pickNRandInArr(listItem, 3);
            } else if (param < 10) {
                gold = 120000000 * param;
                listClothesReward = new int[] { randClothes(param), randClothes(param) };
                listItemReward = Util.pickNRandInArr(listItem, 4);
            } else {
                gold = 150000000 * param;
                listClothesReward = new int[] { randClothes(param), randClothes(param), randClothes(param) };
                listItemReward = Util.pickNRandInArr(listItem, 5);
                int ruby = Util.nextInt(1, 5);
                pl.inventory.ruby += ruby;
                pl.textRuongGo.add(text + "|1| " + ruby + " Hồng Ngọc");
            }
            for (var i : listClothesReward) {
                itemReward = ItemService.gI().createNewItem((short) i);
                RewardService.gI().initBaseOptionClothes(itemReward.template.id, itemReward.template.type,
                        itemReward.itemOptions);
                RewardService.gI().initStarOption(itemReward,
                        new RewardService.RatioStar[] { new RewardService.RatioStar((byte) 1, 1, 2),
                                new RewardService.RatioStar((byte) 2, 1, 3),
                                new RewardService.RatioStar((byte) 3, 1, 4),
                                new RewardService.RatioStar((byte) 4, 1, 5), });
                InventoryService.gI().addItemBag(pl, itemReward, 0);
                pl.textRuongGo.add(text + itemReward.getInfoItem());
            }
            for (var i : listItemReward) {
                itemReward = ItemService.gI().createNewItem((short) i);
                RewardService.gI().initBaseOptionSaoPhaLe(itemReward);
                itemReward.quantity = Util.nextInt(1, 5);
                InventoryService.gI().addItemBag(pl, itemReward, 0);
                pl.textRuongGo.add(text + itemReward.getInfoItem());
            }
            if (param == 11) {
                if (Util.isTrue(10, 90)) {
                    int[] itemDos = new int[] { 556, 558, 560 };
                    int randomDo = new Random().nextInt(itemDos.length);
                    itemReward = ItemService.gI().createNewItem((short) itemDos[randomDo]);
                } else {
                    itemReward = ItemService.gI().createNewItem((short) 861);
                    itemReward.quantity = Util.nextInt(1000, 5000);
                }
                Item da = ItemService.gI().createNewItem((short) 2040);
                da.quantity = 2;
                InventoryService.gI().addItemBag(pl, da, 999);
                InventoryService.gI().addItemBag(pl, itemReward, 999);
                pl.textRuongGo.add(text + itemReward.getInfoItem());
            }
            NpcService.gI().createMenuConMeo(pl, ConstNpc.RUONG_GO, -1,
                    "Bạn nhận được\n|1|+" + Util.numberToMoney(gold) + " vàng",
                    "OK [" + pl.textRuongGo.size() + "]");
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            pl.inventory.addGold(gold);
            InventoryService.gI().sendItemBags(pl);
            PlayerService.gI().sendInfoHpMpMoney(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Vì bạn quên không lấy chìa nên cần đợi 24h để bẻ khóa");
        }
    }

    private void useItemChangeFlagBag(Player player, Item item) {
        switch (item.template.id) {
            case 805:// vong thien than
                break;
            case 865: // kiem Z
                if (!player.effectFlagBag.useKiemz) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useKiemz = !player.effectFlagBag.useKiemz;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 994: // vỏ ốc
                break;
            case 995: // cây kem
                break;
            case 996: // cá heo
                break;
            case 997: // con diều
                break;
            case 998: // diều rồng
                if (!player.effectFlagBag.useDieuRong) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useDieuRong = !player.effectFlagBag.useDieuRong;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 999: // mèo mun
                if (!player.effectFlagBag.useMeoMun) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useMeoMun = !player.effectFlagBag.useMeoMun;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1000: // xiên cá
                if (!player.effectFlagBag.useXienCa) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useXienCa = !player.effectFlagBag.useXienCa;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1001: // phóng heo
                if (!player.effectFlagBag.usePhongHeo) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.usePhongHeo = !player.effectFlagBag.usePhongHeo;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 954:
                if (!player.effectFlagBag.useHoaVang) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useHoaVang = !player.effectFlagBag.useHoaVang;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 955:
                if (!player.effectFlagBag.useHoaHong) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useHoaHong = !player.effectFlagBag.useHoaHong;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 852:
                if (!player.effectFlagBag.useGayTre) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useGayTre = !player.effectFlagBag.useGayTre;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
        }
        Service.getInstance().point(player);
        Service.getInstance().sendFlagBag(player);
    }

    private void changePet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
                gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }

    public void hopQuaTanThu(Player pl, Item it) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 14) {
            int gender = pl.gender;
            int[] id = { gender, 6 + gender, 21 + gender, 27 + gender, 12, 194, 441, 442, 443, 444, 445, 446, 447 };
            int[] soluong = { 1, 1, 1, 1, 1, 1, 10, 10, 10, 10, 10, 10, 10 };
            int[] option = { 0, 0, 0, 0, 0, 73, 95, 96, 97, 98, 99, 100, 101 };
            int[] param = { 0, 0, 0, 0, 0, 0, 5, 5, 5, 3, 3, 5, 5 };
            int arrLength = id.length - 1;

            for (int i = 0; i < arrLength; i++) {
                if (i < 5) {
                    Item item = ItemService.gI().createNewItem((short) id[i]);
                    RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
                    item.itemOptions.add(new ItemOption(107, 3));
                    InventoryService.gI().addItemBag(pl, item, 0);
                } else {
                    Item item = ItemService.gI().createNewItem((short) id[i]);
                    item.quantity = soluong[i];
                    item.itemOptions.add(new ItemOption(option[i], param[i]));
                    InventoryService.gI().addItemBag(pl, item, 0);
                }
            }

            int[] idpet = { 916, 917, 918, 942, 943, 944, 1046, 1039, 1040 };

            Item item = ItemService.gI().createNewItem((short) idpet[Util.nextInt(0, idpet.length - 1)]);
            item.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
            item.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
            item.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
            item.itemOptions.add(new ItemOption(93, 3));
            InventoryService.gI().addItemBag(pl, item, 0);

            InventoryService.gI().subQuantityItemsBag(pl, it, 1);
            InventoryService.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Chúc bạn chơi game vui vẻ");
        } else {
            Service.getInstance().sendThongBao(pl, "Cần tối thiểu 14 ô trống để nhận thưởng");
        }
    }

    private void openbox2010(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 17, 16, 15, 675, 676, 677, 678, 679, 680, 681, 580, 581, 582 };
            int[][] gold = { { 5000, 20000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;

            Item it = ItemService.gI().createNewItem(temp[index]);

            if (temp[index] <= 17) {
                it.itemOptions.add(new ItemOption(73, 0));

            } else if (temp[index] >= 580 && temp[index] <= 582 || temp[index] >= 675) { // cải trang

                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
                it.itemOptions.add(new ItemOption(95, Util.nextInt(5, 15)));
                it.itemOptions.add(new ItemOption(96, Util.nextInt(5, 15)));

                if (Util.isTrue(1, 200)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                }

            } else {
                it.itemOptions.add(new ItemOption(73, 0));
            }
            InventoryService.gI().addItemBag(pl, it, 0);
            icon[1] = it.template.iconID;

            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void capsule8thang3(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 17, 16, 675, 676, 677, 678, 679, 680, 681, 580, 581, 582, 1154, 1155, 1156, 860, 1041,
                    1042, 1043, 954, 955 };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            Item it = ItemService.gI().createNewItem(temp[index]);
            if (Util.isTrue(15, 100)) {
                int ruby = Util.nextInt(1, 5);
                pl.inventory.ruby += ruby;
                CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, (short) 7743);
                PlayerService.gI().sendInfoHpMpMoney(pl);
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                InventoryService.gI().sendItemBags(pl);
                Service.getInstance().sendThongBao(pl, "Bạn nhận được " + ruby + " Hồng Ngọc");
                return;
            }
            if (Util.isTrue(17, 100)) {
                Item mewmew = ItemService.gI().createNewItem((short) 457);
                int thoivang = Util.nextInt(1, 5);
                InventoryService.gI().addItemBag(pl, mewmew, thoivang);
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                InventoryService.gI().sendItemBags(pl);
                Service.getInstance().sendThongBao(pl, "Bạn nhận được thỏi vàng");
                CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, (short) 4028);
                return;
            }
            if (it.template.type == 5) { // cải trang
                it.itemOptions.add(new ItemOption(50, Util.nextInt(17, 25)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(17, 38)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(17, 38)));
                it.itemOptions.add(new ItemOption(117, Util.nextInt(6, 15)));
            } else if (it.template.id == 954 || it.template.id == 955) {
                it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 12)));
                it.itemOptions.add(new ItemOption(77, Util.nextInt(5, 12)));
                it.itemOptions.add(new ItemOption(103, Util.nextInt(5, 12)));
            }
            if (it.template.type == 5 || it.template.id == 954 || it.template.id == 955) {
                if (Util.isTrue(1, 10)) {
                    it.itemOptions.add(new ItemOption(74, 0));
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                }
            }
            InventoryService.gI().addItemBag(pl, it, 0);
            icon[1] = it.template.iconID;
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    public void openboxsukien(Player pl, Item item, int idsukien) {
        try {
            switch (idsukien) {
                case 1:
                    if (Manager.EVENT_SEVER == idsukien) {
                        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                            short[] temp = { 16, 15, 865, 999, 1000, 1001, 739, 742, 743 };
                            int[][] gold = { { 5000, 20000 } };
                            byte index = (byte) Util.nextInt(0, temp.length - 1);
                            short[] icon = new short[2];
                            icon[0] = item.template.iconID;
                            Item it = ItemService.gI().createNewItem(temp[index]);
                            if (temp[index] >= 15 && temp[index] <= 16) {
                                it.itemOptions.add(new ItemOption(73, 0));
                            } else if (temp[index] == 865) {

                                it.itemOptions.add(new ItemOption(30, 0));

                                if (Util.isTrue(1, 30)) {
                                    it.itemOptions.add(new ItemOption(93, 365));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 999) { // mèo mun
                                it.itemOptions.add(new ItemOption(77, 15));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(1, 50)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 1000) { // xiên cá
                                it.itemOptions.add(new ItemOption(103, 15));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(1, 50)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 1001) { // Phóng heo
                                it.itemOptions.add(new ItemOption(50, 15));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(1, 50)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }

                            } else if (temp[index] == 739 || temp[index] == 742) { // cải trang Billes

                                it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                                it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 45)));

                                if (Util.isTrue(1, 100)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }

                            } else if (temp[index] == 743) { // chổi bay
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(1, 50)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }

                            } else {
                                it.itemOptions.add(new ItemOption(73, 0));
                            }
                            InventoryService.gI().addItemBag(pl, it, 0);
                            icon[1] = it.template.iconID;

                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            InventoryService.gI().sendItemBags(pl);

                            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        } else {
                            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                        }
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
                    }
                case ConstEvent.SU_KIEN_20_11:
                    if (Manager.EVENT_SEVER == idsukien) {
                        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                            short[] temp = { 16, 15, 1039, 954, 955, 710, 711, 1040, 2023, 999, 1000, 1001 };
                            byte index = (byte) Util.nextInt(0, temp.length - 1);
                            short[] icon = new short[2];
                            icon[0] = item.template.iconID;
                            Item it = ItemService.gI().createNewItem(temp[index]);
                            if (temp[index] <= 16) {
                                it.itemOptions.add(new ItemOption(73, 0));
                            } else if (temp[index] == 1039 || temp[index] == 1040) {
                                it.itemOptions.add(new ItemOption(50, 10));
                                it.itemOptions.add(new ItemOption(77, 10));
                                it.itemOptions.add(new ItemOption(103, 10));
                                it.itemOptions.add(new ItemOption(30, 0));
                                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                            } else if (temp[index] == 954) {
                                it.itemOptions.add(new ItemOption(50, 15));
                                it.itemOptions.add(new ItemOption(77, 15));
                                it.itemOptions.add(new ItemOption(103, 15));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(79, 80)) {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 955) {
                                it.itemOptions.add(new ItemOption(50, 20));
                                it.itemOptions.add(new ItemOption(77, 20));
                                it.itemOptions.add(new ItemOption(103, 20));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(79, 80)) {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 710) {// cải trang quy lão kame
                                it.itemOptions.add(new ItemOption(50, 22));
                                it.itemOptions.add(new ItemOption(77, 20));
                                it.itemOptions.add(new ItemOption(103, 20));
                                it.itemOptions.add(new ItemOption(194, 0));
                                it.itemOptions.add(new ItemOption(160, 35));
                                if (Util.isTrue(99, 100)) {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 711) { // cải trang jacky chun
                                it.itemOptions.add(new ItemOption(50, 23));
                                it.itemOptions.add(new ItemOption(77, 21));
                                it.itemOptions.add(new ItemOption(103, 21));
                                it.itemOptions.add(new ItemOption(195, 0));
                                it.itemOptions.add(new ItemOption(160, 50));
                                if (Util.isTrue(99, 100)) {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 2023) {
                                it.itemOptions.add(new ItemOption(30, 0));
                            } else if (temp[index] == 999) { // mèo mun
                                it.itemOptions.add(new ItemOption(77, 15));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(1, 50)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 1000) { // xiên cá
                                it.itemOptions.add(new ItemOption(103, 15));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(1, 50)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else if (temp[index] == 1001) { // Phóng heo
                                it.itemOptions.add(new ItemOption(50, 15));
                                it.itemOptions.add(new ItemOption(30, 0));
                                if (Util.isTrue(1, 50)) {
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                            } else {
                                it.itemOptions.add(new ItemOption(73, 0));
                            }
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            icon[1] = it.template.iconID;
                            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                            InventoryService.gI().addItemBag(pl, it, 0);
                            int ruby = Util.nextInt(1, 5);
                            pl.inventory.ruby += ruby;
                            InventoryService.gI().sendItemBags(pl);
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.getInstance().sendThongBao(pl, "Bạn được tặng kèm " + ruby + " Hồng Ngọc");
                        } else {
                            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                        }
                    } else {
                        Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
                    }
                    break;
                case ConstEvent.SU_KIEN_NOEL:
                    if (Manager.EVENT_SEVER == idsukien) {
                        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                            int spl = Util.nextInt(441, 445);
                            int dnc = Util.nextInt(220, 224);
                            int nr = Util.nextInt(16, 18);
                            int nrBang = Util.nextInt(926, 931);

                            if (Util.isTrue(5, 90)) {
                                int ruby = Util.nextInt(1, 3);
                                pl.inventory.ruby += ruby;
                                CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, (short) 7743);
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                InventoryService.gI().sendItemBags(pl);
                                Service.getInstance().sendThongBao(pl, "Bạn nhận được " + ruby + " Hồng Ngọc");
                            } else {
                                int[] temp = { spl, dnc, nr, nrBang, 387, 390, 393, 821, 822, 746, 380, 999, 1000, 1001,
                                        936, 2022 };
                                byte index = (byte) Util.nextInt(0, temp.length - 1);
                                short[] icon = new short[2];
                                icon[0] = item.template.iconID;
                                Item it = ItemService.gI().createNewItem((short) temp[index]);

                                if (temp[index] >= 441 && temp[index] <= 443) {// sao pha le
                                    it.itemOptions.add(new ItemOption(temp[index] - 346, 5));
                                    it.quantity = 10;
                                } else if (temp[index] >= 444 && temp[index] <= 445) {
                                    it.itemOptions.add(new ItemOption(temp[index] - 346, 3));
                                    it.quantity = 10;
                                } else if (temp[index] >= 220 && temp[index] <= 224) { // da nang cap
                                    it.quantity = 10;
                                } else if (temp[index] >= 387 && temp[index] <= 393) { // mu noel do
                                    it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
                                    it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                                    it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                                    it.itemOptions.add(new ItemOption(80, Util.nextInt(10, 20)));
                                    it.itemOptions.add(new ItemOption(106, 0));
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                                    it.itemOptions.add(new ItemOption(199, 0));
                                } else if (temp[index] == 936) { // tuan loc
                                    it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
                                    it.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
                                    it.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
                                } else if (temp[index] == 822) { // cay thong noel
                                    it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                                    it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                                    it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
                                    it.itemOptions.add(new ItemOption(30, 0));
                                    it.itemOptions.add(new ItemOption(74, 0));
                                } else if (temp[index] == 746) { // xe truot tuyet
                                    it.itemOptions.add(new ItemOption(74, 0));
                                    it.itemOptions.add(new ItemOption(30, 0));
                                    if (Util.isTrue(99, 100)) {
                                        it.itemOptions.add(new ItemOption(93, Util.nextInt(30, 360)));
                                    }
                                } else if (temp[index] == 999) { // mèo mun
                                    it.itemOptions.add(new ItemOption(77, 15));
                                    it.itemOptions.add(new ItemOption(74, 0));
                                    it.itemOptions.add(new ItemOption(30, 0));
                                    if (Util.isTrue(99, 100)) {
                                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                    }
                                } else if (temp[index] == 1000) { // xiên cá
                                    it.itemOptions.add(new ItemOption(103, 15));
                                    it.itemOptions.add(new ItemOption(74, 0));
                                    it.itemOptions.add(new ItemOption(30, 0));
                                    if (Util.isTrue(99, 100)) {
                                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                    }
                                } else if (temp[index] == 1001) { // Phóng heo
                                    it.itemOptions.add(new ItemOption(50, 15));
                                    it.itemOptions.add(new ItemOption(74, 0));
                                    it.itemOptions.add(new ItemOption(30, 0));
                                    if (Util.isTrue(99, 100)) {
                                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                    }
                                } else if (temp[index] == 2022 || temp[index] == 821) {
                                    it.itemOptions.add(new ItemOption(30, 0));
                                } else {
                                    it.itemOptions.add(new ItemOption(73, 0));
                                }
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                icon[1] = it.template.iconID;
                                CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                                InventoryService.gI().addItemBag(pl, it, 0);
                                InventoryService.gI().sendItemBags(pl);
                            }
                        } else {
                            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                        }
                    } else {
                        Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
                    }
                    break;
                case ConstEvent.SU_KIEN_TET:
                    if (Manager.EVENT_SEVER == idsukien) {
                        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                            short[] icon = new short[2];
                            icon[0] = item.template.iconID;
                            RandomCollection<Integer> rd = Manager.HOP_QUA_TET;
                            int tempID = rd.next();
                            Item it = ItemService.gI().createNewItem((short) tempID);
                            if (it.template.type == 11) {// FLAGBAG
                                it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 20)));
                                it.itemOptions.add(new ItemOption(77, Util.nextInt(5, 20)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(5, 20)));
                            } else if (tempID >= 1159 && tempID <= 1161) {
                                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
                                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
                                it.itemOptions.add(new ItemOption(106, 0));
                            } else if (tempID == ConstItem.CAI_TRANG_SSJ_3_WHITE) {
                                it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
                                it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                                it.itemOptions.add(new ItemOption(5, Util.nextInt(10, 25)));
                                it.itemOptions.add(new ItemOption(104, Util.nextInt(5, 15)));
                            }
                            int type = it.template.type;
                            if (type == 5 || type == 11) {// cải trang & flagbag
                                if (Util.isTrue(199, 200)) {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                                }
                                it.itemOptions.add(new ItemOption(199, 0));// KHÔNG THỂ GIA HẠN
                            } else if (type == 23) {// thú cưỡi
                                if (Util.isTrue(199, 200)) {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
                                }
                            }
                            if (tempID >= ConstItem.MANH_AO && tempID <= ConstItem.MANH_GANG_TAY) {
                                it.quantity = Util.nextInt(5, 15);
                            } else {
                                it.itemOptions.add(new ItemOption(74, 0));
                            }
                            icon[1] = it.template.iconID;
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                            InventoryService.gI().addItemBag(pl, it, 0);
                            InventoryService.gI().sendItemBags(pl);
                            break;
                        } else {
                            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                        }
                    } else {
                        Service.getInstance().sendThongBao(pl, "Sự kiện đã kết thúc");
                    }
                    break;
                case 5:
                    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        int tempID;
                        if (Util.isTrue(30, 100)) {
                            byte getRandom = (byte) Util.getOne(0, 1);
                            if (getRandom == 0) {
                                tempID = 584;
                            } else {
                                tempID = 861;
                            }
                        } else if (Util.isTrue(20, 100)) {
                            byte getRandom = (byte) Util.getOne(0, 1);
                            if (getRandom == 0) {
                                tempID = Util.getOne(733, 734);
                            } else {
                                tempID = 861;
                            }
                        } else if (Util.isTrue(15, 100)) {
                            tempID = 457;
                        } else {
                            tempID = 190;
                        }
                        Item it = ItemService.gI().createNewItem((short) tempID);
                        switch (tempID) {
                            case 457:
                                it.quantity = Util.nextInt(1, 5);
                                break;
                            case 861:
                                it.quantity = Util.nextInt(100, 500);
                                break;
                            case 584:
                                it.itemOptions.add(new ItemOption(77, 45));
                                it.itemOptions.add(new ItemOption(103, 45));
                                it.itemOptions.add(new ItemOption(50, 45));
                                it.itemOptions.add(new ItemOption(116, 0));
                                if (!Util.isTrue(10, 150)) {
                                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                                }
                                it.itemOptions.add(new ItemOption(74, 0));
                                break;
                            case 733:
                            case 734:
                                it.itemOptions.add(new ItemOption(77, 5));
                                it.itemOptions.add(new ItemOption(103, 5));
                                it.itemOptions.add(new ItemOption(50, 5));
                                it.itemOptions.add(new ItemOption(74, 0));
                                break;
                        }
                        icon[1] = it.template.iconID;
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                    }
                    break;
                case 6:
                    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        int tempID;
                        if (Util.isTrue(30, 100)) {
                            byte getRandom = (byte) Util.getOne(0, 1);
                            if (getRandom == 0) {
                                tempID = 1213;
                            } else {
                                tempID = 861;
                            }
                        } else if (Util.isTrue(20, 100)) {
                            byte getRandom = (byte) Util.getOne(0, 1);
                            if (getRandom == 0) {
                                tempID = Util.getOne(1230, 1254);
                            } else {
                                tempID = 1111;
                            }
                        } else if (Util.isTrue(5, 100)) {
                            tempID = 457;
                        } else {
                            tempID = 861;
                        }
                        Item it = ItemService.gI().createNewItem((short) tempID);
                        switch (tempID) {
                            case 457:
                                it.quantity = Util.nextInt(1, 10);
                                break;
                            case 861:
                                it.quantity = Util.nextInt(1000, 5000);
                                break;
                            case 1111:
                                it.itemOptions.add(new ItemOption(50, Util.nextInt(15, 22)));
                                it.itemOptions.add(new ItemOption(77, Util.nextInt(15, 22)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(15, 22)));
                                it.itemOptions.add(new ItemOption(74, 0));
                                break;
                            case 1213:
                            case 1230:
                            case 1254:
                                it.itemOptions.add(new ItemOption(50, Util.nextInt(20, 30)));
                                it.itemOptions.add(new ItemOption(77, Util.nextInt(20, 30)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(20, 30)));
                                it.itemOptions.add(new ItemOption(106, 0));
                                it.itemOptions.add(new ItemOption(74, 0));
                                break;
                        }
                        icon[1] = it.template.iconID;
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                    }
                    break;
                case 7:
                    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        int tempID;
                        if (Util.isTrue(5, 100)) {
                            tempID = Util.getOne(2032, 2036);
                        } else if (Util.isTrue(20, 100)) {
                            tempID = 1260;
                        } else if (Util.isTrue(26, 100)) {
                            byte getRandom = (byte) Util.getOne(0, 1);
                            if (getRandom == 0) {
                                tempID = Util.getOne(702, 708);
                            } else {
                                tempID = 1143;
                            }
                        } else {
                            tempID = 861;
                        }
                        Item it = ItemService.gI().createNewItem((short) tempID);
                        switch (tempID) {
                            case 861:
                                it.quantity = Util.nextInt(100, 1000);
                                break;
                        }
                        icon[1] = it.template.iconID;
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                    }
                    break;
                case 8:
                    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        int tempID;
                        tempID = 1265;
                        Item it = ItemService.gI().createNewItem((short) tempID);
                        switch (tempID) {
                            case 1265:
                                it.itemOptions.add(new ItemOption(50, Util.nextInt(1, 5)));
                                it.itemOptions.add(new ItemOption(77, Util.nextInt(1, 30)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(1, 30)));
                                it.itemOptions.add(new ItemOption(5, Util.nextInt(50, 125)));
                                if (!Util.isTrue(1, 100)) {
                                    it.itemOptions.add(new ItemOption(93, 1));
                                }
                                it.itemOptions.add(new ItemOption(74, 0));
                                break;
                        }
                        icon[1] = it.template.iconID;
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                    }
                    break;
                case 9:// hop con mew
                    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        int tempID;
                        if (Util.isTrue(10, 100)) {
                            tempID = 1253;
                        } else if (Util.isTrue(20, 100)) {
                            tempID = 861;
                        } else if (Util.isTrue(26, 100)) {
                            tempID = 869;
                        } else if (Util.isTrue(20, 100)) {
                            tempID = 2011;
                        } else {
                            tempID = 2012;
                        }
                        Item it = ItemService.gI().createNewItem((short) tempID);
                        switch (tempID) {
                            case 1253:
                                it.itemOptions.add(new ItemOption(50, 20));
                                it.itemOptions.add(new ItemOption(77, 20));
                                it.itemOptions.add(new ItemOption(103, 20));
                                break;
                            case 861:
                                it.quantity = Util.nextInt(1, 5000);
                                break;
                            case 869:
                                it.quantity = Util.nextInt(1, 4);
                                break;
                            case 2011:
                            case 2012:
                                it.quantity = Util.nextInt(1, 2);
                                break;
                        }
                        icon[1] = it.template.iconID;
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                    }
                    break;
                case 10:
                    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        int tempID;
                        if (Util.isTrue(5, 120)) {
                            tempID = 1201;
                        } else if (Util.isTrue(20, 100)) {
                            tempID = 2040;
                        } else if (Util.isTrue(26, 100)) {
                            tempID = Util.getOne(1231, 638);
                        } else if (Util.isTrue(30, 100)) {
                            tempID = 861;
                        } else if (Util.isTrue(30, 70)) {
                            tempID = 457;
                        } else {
                            tempID = 16;
                        }
                        Item it = ItemService.gI().createNewItem((short) tempID);
                        switch (tempID) {
                            case 1201:
                                it.itemOptions.add(new ItemOption(50, 20));
                                it.itemOptions.add(new ItemOption(77, Util.nextInt(39, 69)));
                                it.itemOptions.add(new ItemOption(103, Util.nextInt(39, 69)));
                                it.itemOptions.add(new ItemOption(5, Util.nextInt(20, 69)));
                                it.itemOptions.add(new ItemOption(14, Util.nextInt(10, 30)));
                                break;
                            case 457:
                                it.quantity = Util.nextInt(1, 10);
                                break;
                            case 861:
                                it.quantity = Util.nextInt(1, 5000);
                                break;
                            case 2040:
                                it.quantity = Util.nextInt(1, 3);
                                break;
                            case 1231:
                                it.quantity = Util.nextInt(1, 2);
                                break;
                            case 638:
                                it.quantity = Util.nextInt(1, 2);
                                break;
                        }
                        icon[1] = it.template.iconID;
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                    }
                    break;
                case 11:
                    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                        short[] icon = new short[2];
                        icon[0] = item.template.iconID;
                        int tempID;
                        if (Util.isTrue(10, 100)) {
                            tempID = 2061;// cai trang
                        } else if (Util.isTrue(2, 90)) {
                            tempID = 2058;// mew
                        } else if (Util.isTrue(26, 100)) {
                            tempID = 2053;// kiem
                        } else if (Util.isTrue(30, 100)) {
                            tempID = 861;
                        } else if (Util.isTrue(3, 70)) {
                            tempID = 869;
                        } else {
                            tempID = Util.nextInt(1150, 1153);
                        }
                        Item it = ItemService.gI().createNewItem((short) tempID);
                        switch (tempID) {
                            case 2053:
                                it.itemOptions.add(new ItemOption(50, 5));
                                it.itemOptions.add(new ItemOption(21, 110));
                                break;
                            case 2061:
                                it.itemOptions.add(new ItemOption(50, 50));
                                it.itemOptions.add(new ItemOption(77, 50));
                                it.itemOptions.add(new ItemOption(103, 50));
                                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
                                it.itemOptions.add(new ItemOption(231, 0));
                                break;
                            case 2058:
                                it.itemOptions.add(new ItemOption(50, 20));
                                it.itemOptions.add(new ItemOption(77, 20));
                                it.itemOptions.add(new ItemOption(103, 20));
                                break;
                            case 869:
                                it.quantity = Util.nextInt(1, 4);
                                break;
                        }
                        icon[1] = it.template.iconID;
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                        InventoryService.gI().addItemBag(pl, it, 0);
                        InventoryService.gI().sendItemBags(pl);
                        break;
                    } else {
                        Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error("Lỗi mở hộp quà", e);
        }
    }

    private void openboxkichhoat(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 76, 188, 189, 190, 441, 442, 447, 2010, 2009, 865, 938, 939, 940, 16, 17, 18, 19, 20, 946,
                    947, 948, 382, 383, 384, 385 };
            int[][] gold = { { 5000, 20000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3 && index >= 0) {
                pl.inventory.addGold(Util.nextInt(gold[0][0], gold[0][1]));
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {

                Item it = ItemService.gI().createNewItem(temp[index]);
                if (temp[index] == 441) {
                    it.itemOptions.add(new ItemOption(95, 5));
                } else if (temp[index] == 442) {
                    it.itemOptions.add(new ItemOption(96, 5));
                } else if (temp[index] == 447) {
                    it.itemOptions.add(new ItemOption(101, 5));
                } else if (temp[index] >= 2009 && temp[index] <= 2010) {
                    it.itemOptions.add(new ItemOption(30, 0));
                } else if (temp[index] == 865) {
                    it.itemOptions.add(new ItemOption(30, 0));
                    if (Util.isTrue(1, 20)) {
                        it.itemOptions.add(new ItemOption(93, 365));
                    } else {
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                    }
                } else if (temp[index] >= 938 && temp[index] <= 940) {
                    it.itemOptions.add(new ItemOption(77, 35));
                    it.itemOptions.add(new ItemOption(103, 35));
                    it.itemOptions.add(new ItemOption(50, 35));
                    if (Util.isTrue(1, 50)) {
                        it.itemOptions.add(new ItemOption(116, 0));
                    } else {
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                    }
                } else if (temp[index] >= 946 && temp[index] <= 948) {
                    it.itemOptions.add(new ItemOption(77, 35));
                    it.itemOptions.add(new ItemOption(103, 35));
                    it.itemOptions.add(new ItemOption(50, 35));
                    if (Util.isTrue(1, 20)) {
                        it.itemOptions.add(new ItemOption(93, 365));
                    } else {
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 30)));
                    }
                } else {
                    it.itemOptions.add(new ItemOption(73, 0));
                }
                InventoryService.gI().addItemBag(pl, it, 0);
                icon[1] = it.template.iconID;

            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void openPhieuCaiTrangHaiTac(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            Item ct = ItemService.gI().createNewItem((short) Util.nextInt(618, 626));
            ct.itemOptions.add(new ItemOption(147, 3));
            ct.itemOptions.add(new ItemOption(77, 3));
            ct.itemOptions.add(new ItemOption(103, 3));
            ct.itemOptions.add(new ItemOption(149, 0));
            if (item.template.id == 2006) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
            } else if (item.template.id == 2007) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30)));
            }
            InventoryService.gI().addItemBag(pl, ct, 0);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, ct.template.iconID);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.getInstance().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.getInstance().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void openEggLinhThu(Player player, Item item) {
        EggLinhThu.createEggLinhThu(player);
        Service.getInstance()
                .sendThongBao(player,
                        "Bạn đã nhận được Trứng Linh Thú ở Sân Sau Siêu Thị\nLưu ý nếu không thấy Trứng vui lòng Thoát Game vào lại!");
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBags(player);
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = { 76, 188, 189, 190, 381, 382, 383, 384, 385 };
            int[][] gold = { { 5000, 20000 } };
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.addGold(Util.nextInt(gold[0][0], gold[0][1]));
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it, 0);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void useItemTime(Player pl, Item item) {
        boolean updatePoint = false;
        switch (item.template.id) {
            case 465:// banh trung thu 1 trung
                if (pl.itemTime.isBanhTrungThu1Trung) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBanhTrungThu1Trung = System.currentTimeMillis();
                pl.itemTime.isBanhTrungThu1Trung = true;
                updatePoint = true;
                break;
            case 466:// banh trung thu 2 trung
                if (pl.itemTime.isBanhTrungThu2Trung) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBanhTrungThu2Trung = System.currentTimeMillis();
                pl.itemTime.isBanhTrungThu2Trung = true;
                updatePoint = true;
                break;
            case 638:
                if (pl.itemTime.isDuoiKhi) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeDuoiKhi = System.currentTimeMillis();
                pl.itemTime.isDuoiKhi = true;
                updatePoint = true;
                break;
            case 382: // bổ huyết
                if (pl.itemTime.isUseBoHuyet2) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                updatePoint = true;
                break;
            case 383: // bổ khí
                if (pl.itemTime.isUseBoKhi2) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                updatePoint = true;
                break;
            case 384: // giáp xên
                if (pl.itemTime.isUseGiapXen2) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                updatePoint = true;
                break;
            case 381: // cuồng nộ
                if (pl.itemTime.isUseCuongNo2) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                updatePoint = true;
                break;
            case 385: // ẩn danh
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 1152: // bổ huyết 2
                if (pl.itemTime.isUseBoHuyet) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                updatePoint = true;
                break;
            case 1151: // bổ khí 2
                if (pl.itemTime.isUseBoKhi) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                updatePoint = true;
                break;
            case 1153: // giáp xên 2
                if (pl.itemTime.isUseGiapXen) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                updatePoint = true;
                break;
            case 1150: // cuồng nộ 2
                if (pl.itemTime.isUseCuongNo) {
                    Service.getInstance().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                updatePoint = true;
                break;
            case 379: // máy dò
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 2044:
                pl.itemTime.timeMayDo = System.currentTimeMillis();
                pl.itemTime.isMayDo = true;
                break;
            case 663: // bánh pudding
            case 664: // xúc xíc
            case 665: // kem dâu
            case 666: // mì ly
            case 667: // sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                updatePoint = true;
                break;
        }
        if (updatePoint) {
            Service.getInstance().point(pl);
        }
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                    // case SummonDragon.NGOC_RONG_2_SAO:
                    // case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openSummonMenu(pl, (byte) (tempId - 13), SummonDragon.DragonType.SHENRON);
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON, -1,
                            "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao",
                            "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        } else if (tempId == SummonDragon.NGOC_RONG_SIEU_CAP) {
            SummonDragon.gI().openSummonMenu(pl, (byte) 1015, SummonDragon.DragonType.BLACK_SHENRON);
        } else if (tempId >= SummonDragon.NGOC_RONG_BANG[0] && tempId <= SummonDragon.NGOC_RONG_BANG[6]) {
            switch (tempId) {
                case 2045:
                    SummonDragon.gI().openSummonMenu(pl, (byte) 2045, SummonDragon.DragonType.ICE_SHENRON);
                    break;
                default:
                    Service.getInstance().sendThongBao(pl, "Bạn chỉ có thể gọi rồng băng từ ngọc 1 sao");
                    break;
            }
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.getInstance().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.getInstance().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.close();
                        } else {
                            Skill skillNeed = SkillUtil
                                    .createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.getInstance().sendThongBao(pl,
                                    "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id),
                                    level);
                            // System.out.println(curSkill.template.name + " - " + curSkill.point);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.getInstance().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.close();
                        } else {
                            Service.getInstance().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp "
                                    + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryService.gI().sendItemBags(pl);
                }
            } else {
                Service.getInstance().sendThongBao(pl, "Không thể thực hiện");

            }
        } catch (Exception e) {
            Log.error(UseItem.class,
                    e);
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void usePorata2(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4 || pl.fusion.typeFusion == 6 || pl.fusion.typeFusion == 10
                || pl.fusion.typeFusion == 12) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion2(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void usePorata3(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4 || pl.fusion.typeFusion == 6 || pl.fusion.typeFusion == 12) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion3(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        if (pl.isHoldNamecBall) {
            NamekBallWar.gI().dropBall(pl);
            Service.getInstance().sendFlagBag(pl);
        }
        pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {
        int zoneId = -1;
        if (index < 0 || index >= pl.mapCapsule.size()) {
            return;
        }
        Zone zoneChose = pl.mapCapsule.get(index);
        if (index != 0 || zoneChose.map.mapId == 21 || zoneChose.map.mapId == 22 || zoneChose.map.mapId == 23) {
            if (!(pl.zone instanceof ZSnakeRoad)) {
                pl.mapBeforeCapsule = pl.zone;
            } else {
                pl.mapBeforeCapsule = null;
            }
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: // skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: // skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: // skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: // skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 2038:
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 4)) {
                        Service.getInstance().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
            }
        } catch (Exception e) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private void hopQuaKichHoat(Player player, Item item) {
        NpcService.gI().createMenuConMeo(player,
                ConstNpc.MENU_OPTION_USE_ITEM1105, -1, "Chọn hành tinh của mày đi",
                "Set trái đất",
                "Set namec",
                "Set xayda",
                "Từ chổi");
    }

    private void blackGoku(Player player) {
        NpcService.gI().createMenuConMeo(player,
                ConstNpc.MENU_USE_ITEM_BLACK_GOKU, -1, "Bạn có chắc chắn muốn sở hữu đệ tử Black Goku không?",
                "Đồng ý");
    }

}
