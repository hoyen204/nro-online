package com.nro.nro_online.services.func;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.item.Costume;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.item.ItemShop;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.shop.Shop;
import com.nro.nro_online.models.shop.TabShop;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.SkillUtil;
import nro.consts.ConstAchive;
import nro.consts.ConstItem;
import nro.consts.ConstNpc;
import nro.jdbc.daos.PlayerDAO;
import nro.models.item.CaiTrang;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.shop.ItemShop;
import nro.models.shop.Shop;
import nro.models.shop.TabShop;
import nro.models.skill.Skill;
import nro.server.Manager;
import nro.server.io.Message;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 * @Bulid Arriety
 */
public class ShopService {

    private static final int COST_GOLD_BAR = 500000000;
    private static final int COST_LOCK_GOLD_BAR = 300000000;

    private static final byte NORMAL_SHOP = 0;
    private static final byte LEARN_SKILL = 1;
    private static final byte SPEC_SHOP = 3;

    private static ShopService i;

    public static ShopService gI() {
        if (i == null) {
            i = new ShopService();
        }
        return i;
    }

    //Lấy ra itemshop khi mua
    private ItemShop getItemShop(int shopId, int tempId) {
        ItemShop itemShop = null;
        Shop shop = null;
        switch (shopId) {
            case ConstNpc.SHOP_BILL_HUY_DIET_0:
                shop = getShop(ConstNpc.BILL, 0, -1);
                break;
            case ConstNpc.SHOP_BUNMA_QK_0:
                shop = getShop(ConstNpc.BUNMA, 0, -1);
                break;
            case ConstNpc.SHOP_DENDE_0:
                shop = getShop(ConstNpc.DENDE, 0, -1);
                break;
            case ConstNpc.SHOP_APPULE_0:
                shop = getShop(ConstNpc.APPULE, 0, -1);
                break;
            case ConstNpc.SHOP_URON_0:
                shop = getShop(ConstNpc.URON, 0, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_0:
                shop = getShop(ConstNpc.BA_HAT_MIT, 0, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_1:
                shop = getShop(ConstNpc.BA_HAT_MIT, 1, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_2:
                shop = getShop(ConstNpc.BA_HAT_MIT, 2, -1);
                break;
            case ConstNpc.SHOP_BA_HAT_MIT_3:
                shop = getShop(ConstNpc.BA_HAT_MIT, 3, -1);
                break;
            case ConstNpc.SHOP_BUNMA_TL_0:
                shop = getShop(ConstNpc.BUNMA_TL, 0, -1);
                break;
            case ConstNpc.SHOP_WHIS_THIEN_SU:
                shop = getShop(ConstNpc.WHIS, 0, -1);
                break;
            case ConstNpc.SHOP_LY_TIEU_NUONG:
                shop = getShop(ConstNpc.LY_TIEU_NUONG, 0, -1);
                break;
            case ConstNpc.SHOP_TORIBOT:
                shop = getShop(ConstNpc.TORIBOT, 0, -1);
                break;
            case ConstNpc.SHOP_LEARN_SKILL:
                shop = getShop(ConstNpc.QUY_LAO_KAME, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_0:
                shop = getShop(ConstNpc.SANTA, 0, -1);
                break;
            case ConstNpc.SHOP_SANTA_1:
                shop = getShop(ConstNpc.SANTA, 1, -1);
                break;
            case ConstNpc.SHOP_GIAM_GIA:
                shop = getShop(ConstNpc.SANTA, 2, -1);
                break;
            case ConstNpc.SHOP_SANTA_SPEC:
                shop = getShop(ConstNpc.SANTA, 3, -1);
                break;
            case ConstNpc.SHOP_SANTA_SPEC_50:
                shop = getShop(ConstNpc.SANTA, 4, -1);
                break;
            case ConstNpc.SHOP_CHIEN_LUC:
                shop = getShop(ConstNpc.TRUONG_LAO_GURU, 0, -1);
                break;
            case ConstNpc.SHOP_BUNMA_LINH_THU:
                shop = getShop(ConstNpc.BUNMA_TL, 1, -1);
                break;
        }
        if (shop != null) {
            for (TabShop tab : shop.tabShops) {
                for (ItemShop is : tab.itemShops) {
                    if (is.temp.id == tempId) {
                        itemShop = is;
                        break;
                    }
                }
                if (itemShop != null) {
                    break;
                }
            }
        }
        return itemShop;
    }

    private Shop getShop(int npcId, int order, int gender) {
        for (Shop shop : Manager.SHOPS) {
            if (shop.npcId == npcId && shop.shopOrder == order) {
                if (gender != -1) {
                    return new Shop(shop, gender);
                } else {
                    return shop;
                }
            }
        }
        return null;
    }

    private Shop getShopHuyDiet(Player player, Shop s) {
        Shop shop = new Shop(s);
        for (TabShop tabShop : shop.tabShops) {
            for (ItemShop item : tabShop.itemShops) {
                item.iconSpec = 15012 + item.temp.type;
                item.costSpec = 1;
            }
        }
        return shop;
    }

    private Shop getShopBua(Player player, Shop s) {
        Shop shop = new Shop(s);
        for (TabShop tabShop : shop.tabShops) {
            for (ItemShop item : tabShop.itemShops) {

                long min = 0;
                switch (item.temp.id) {
                    case 213:
                        long timeTriTue = player.charms.tdTriTue;
                        long current = System.currentTimeMillis();
                        min = (timeTriTue - current) / 60000;
                        break;
                    case 214:
                        min = (player.charms.tdManhMe - System.currentTimeMillis()) / 60000;
                        break;
                    case 215:
                        min = (player.charms.tdDaTrau - System.currentTimeMillis()) / 60000;
                        break;
                    case 216:
                        min = (player.charms.tdOaiHung - System.currentTimeMillis()) / 60000;
                        break;
                    case 217:
                        min = (player.charms.tdBatTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 218:
                        min = (player.charms.tdDeoDai - System.currentTimeMillis()) / 60000;
                        break;
                    case 219:
                        min = (player.charms.tdThuHut - System.currentTimeMillis()) / 60000;
                        break;
                    case 522:
                        min = (player.charms.tdDeTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 671:
                        min = (player.charms.tdTriTue3 - System.currentTimeMillis()) / 60000;
                        break;
                    case 672:
                        min = (player.charms.tdTriTue4 - System.currentTimeMillis()) / 60000;
                        break;
                }
                if (min > 0) {
                    item.options.clear();
                    if (min >= 1440) {
                        item.options.add(new ItemOption(63, (int) min / 1440));
                    } else if (min >= 60) {
                        item.options.add(new ItemOption(64, (int) min / 60));
                    } else {
                        item.options.add(new ItemOption(65, (int) min));
                    }
                }
            }
        }
        return shop;
    }

    //shop đồ hủy diệt
    public void openShopBillHuyDiet(Player player, int shopId, int order) {
        Shop shop = getShopHuyDiet(player, getShop(ConstNpc.BILL, order, -1));
        openShopType3(player, shop, shopId);
    }

    public void openShopWhisThienSu(Player player, int shopId, int order) {
        Shop shop = getShop(ConstNpc.WHIS, order, -1);
        openShopType3(player, shop, shopId);
    }

    //shop bùa
    public void openShopBua(Player player, int shopId, int order) {
//        player.iDMark.setShopId(shopId);
        Shop shop = getShopBua(player, getShop(ConstNpc.BA_HAT_MIT, order, -1));
        openShopType0(player, shop, shopId);
    }

    public void openShopLearnSkill(Player player, int idNpc, int shopId, int order, int gender) {
        Shop shop = getShop(idNpc, order, gender);
        openShopType1(player, shop, shopId);
    }

    public void openShopLearnSkill(Player player, Npc npc, int shopId, int order, int gender) {
        Shop shop = getShop(npc.tempId, order, gender);
        openShopType1(player, shop, shopId);
    }

    //shop normal
    public void openShopNormal(Player player, Npc npc, int shopId, int order, int gender) {
        Shop shop = getShop(npc.tempId, order, gender);
        openShopType0(player, shop, shopId);
    }

    public void openShopSpecial(Player player, Npc npc, int shopId, int order, int gender) {
        Shop shop = getShop(npc.tempId, order, gender);
        openShopType3(player, shop, shopId);
    }

    private void openShopType1(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(LEARN_SKILL);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    List<ItemShop> listNew = new ArrayList<>();
                    for (int i = 0; i < tab.itemShops.size(); i++) {
                        ItemShop itemShop = tab.itemShops.get(i);
                        if (itemShop != null) {
                            Skill curSkill = SkillUtil.getSkillByItemID(player, itemShop.temp.id);
                            if (curSkill == null || curSkill.point < itemShop.getLevelSkill()) {
                                listNew.add(itemShop);
                            }
                        }
                    }
                    msg.writer().writeByte(listNew.size());
                    for (ItemShop itemShop : listNew) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeLong(itemShop.getPowerRequire());
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        msg.writer().writeByte(0);
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(ShopService.class, e);
            }
        }
    }

    private void openShopType0(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(NORMAL_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeInt(itemShop.gold);
                        msg.writer().writeInt(itemShop.gem);
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        Costume caiTrang = Manager.getCaiTrangByItemId(itemShop.temp.id);
                        msg.writer().writeByte(caiTrang != null ? 1 : 0);
                        if (caiTrang != null) {
                            msg.writer().writeShort(caiTrang.getId()[0]);
                            msg.writer().writeShort(caiTrang.getId()[1]);
                            msg.writer().writeShort(caiTrang.getId()[2]);
                            msg.writer().writeShort(caiTrang.getId()[3]);
                        }
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(ShopService.class, e);
            }
        }
    }

    private void openShopType3(Player player, Shop shop, int shopId) {
        player.iDMark.setShopId(shopId);
        if (shop != null) {
            Message msg;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(SPEC_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    //System.out.println(tab.name);
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeShort(itemShop.iconSpec);
                        msg.writer().writeInt(itemShop.costSpec);
                        msg.writer().writeByte(itemShop.options.size());
                        for (ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        Costume caiTrang = Manager.getCaiTrangByItemId(itemShop.temp.id);
                        msg.writer().writeByte(caiTrang != null ? 1 : 0);
                        if (caiTrang != null) {
                            msg.writer().writeShort(caiTrang.getId()[0]);
                            msg.writer().writeShort(caiTrang.getId()[1]);
                            msg.writer().writeShort(caiTrang.getId()[2]);
                            msg.writer().writeShort(caiTrang.getId()[3]);
                        }
                    }
                }
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Log.error(ShopService.class, e);
            }
        }
    }

    private void learnSkill(Player player, ItemShop it) {
        Message msg;
        try {
            if (it != null && (it.temp.gender == player.gender || it.temp.gender == 3)) {
                long power = it.getPowerRequire();
                if (player.nPoint.tiemNang < power) {
                    Service.getInstance().sendThongBao(player, "Không đủ " + Util.powerToString(power) + " tiềm năng");
                    return;
                }
                byte level = it.getLevelSkill();
                Skill curSkill = SkillUtil.getSkillByItemID(player, it.temp.id);
                if (curSkill == null) {
                    return;
                }
                if (curSkill.point >= level || curSkill.point == 7) {
                    return;
                }
                if (curSkill.point == 0) {
                    if (level == 1) {
                        player.nPoint.tiemNang -= power;
                        Service.getInstance().point(player);
                        curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(it.temp.id), level);
                        SkillUtil.setSkill(player, curSkill);
                        msg = Service.getInstance().messageSubCommand((byte) 23);
                        msg.writer().writeShort(curSkill.skillId);
                        player.sendMessage(msg);
                        msg.cleanup();
                    } else {
                        Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(it.temp.id), level);
                        Service.getInstance().sendThongBao(player, "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                    }
                } else {
                    if (curSkill.point + 1 == level) {
                        player.nPoint.tiemNang -= power;
                        Service.getInstance().point(player);
                        curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(it.temp.id), level);
                        SkillUtil.setSkill(player, curSkill);
                        msg = Service.getInstance().messageSubCommand((byte) 62);
                        msg.writer().writeShort(curSkill.skillId);
                        player.sendMessage(msg);
                        msg.cleanup();
                    } else {
                        Service.getInstance().sendThongBao(player, "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                    }
                }
                openShopLearnSkill(player, 13, ConstNpc.SHOP_LEARN_SKILL, 0, player.gender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buyItemShopNormal(Player player, ItemShop is) {
        if (is != null) {
            if (player.iDMark.getShopId() == ConstNpc.SHOP_BILL_HUY_DIET_0) {
                Item eat = InventoryService.gI().findMealChangeDestroyClothes(player);
                if (eat == null) {
                    Service.getInstance().sendThongBao(player, "Yêu cầu có 99 thức ăn");
                    return;
                }
            }
            int itemShopID = is.temp.id;
            if (is.temp.id == 517 && player.inventory.itemsBag.size() >= 60) {
                Service.getInstance().sendThongBao(player, "Hành trang đã đạt tới số lượng tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (is.temp.id == 518 && player.inventory.itemsBox.size() >= 40) {
                Service.getInstance().sendThongBao(player, "rương đồ đã đạt tới số lượng tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (is.temp.id == 988 && player.inventory.getGoldLimit() >= 1000_000_000_000L) {
                Service.getInstance().sendThongBao(player, "Giới hạn vàng của bạn đã đạt tối đa");
                Service.getInstance().sendMoney(player);
                return;
            }
            if (is.temp.id == 711) {//chun
                boolean hasRequiredItem = false;
                Item requiredItem = InventoryService.gI().findItemBag(player, 710);// quy lao
                if (requiredItem != null) {
                    hasRequiredItem = true;
                }
                if (!hasRequiredItem) {
                    Service.getInstance().sendThongBao(player, "Hãy mua cải trang Quy lão Kamê trước");
                    return;
                }
            }
            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                int gold = is.gold;
                int gem = is.gem;
                int itemExchange = is.itemExchange;
                if (gold != 0) {
                    if (player.inventory.gold >= gold) {
                        player.inventory.gold -= gold;
                    } else {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ vàng, còn thiếu "
                                + (Util.numberToMoney(gold - player.inventory.gold) + " vàng"));
                        Service.getInstance().sendMoney(player);
                        return;
                    }
                }
                if (gem != 0) {
                    if (player.inventory.getGem() >= gem) {
                        player.inventory.subGem(gem);
                    } else {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu "
                                + (gem - player.inventory.getGem()) + " ngọc");
                        Service.getInstance().sendMoney(player);
                        return;
                    }
                }
                if (itemExchange >= 0) {
                    Item itm = InventoryService.gI().findItemBagByTemp(player, itemExchange);
                    if (isLimitItem(itemShopID)) {
                        if (player.buyLimit[itemShopID - 1074] < getBuyLimit(itemShopID)) {
                            player.buyLimit[itemShopID - 1074]++;
                        } else {
                            Service.getInstance().sendThongBao(player, "Số lượt mua trong ngày đã đạt giới hạn");
                            return;
                        }
                    }
                    if (itemExchange == 861 && player.inventory.getRuby() >= is.costSpec) {
                        player.inventory.subRuby(is.costSpec);
                    } else if (itm != null && itm.isNotNullItem() && itm.quantity >= is.costSpec) {
                        InventoryService.gI().subQuantityItemsBag(player, itm, is.costSpec);
                    } else {
                        Service.getInstance().sendThongBao(player, "Bạn không đủ vật phẩm để trao đổi.");
                        return;
                    }
                }
                switch (player.iDMark.getShopId()) {
                    case ConstNpc.SHOP_BILL_HUY_DIET_0:
                        if (player.setClothes.godClothes || true) {
                            Item meal = InventoryService.gI().findMealChangeDestroyClothes(player);
                            if (meal != null) {
                                Item item = ItemService.gI().createItemFromItemShop(is);
                                int param = 0;
                                if (Util.isTrue(2, 10)) {
                                    param = Util.nextInt(10, 15);
                                } else if (Util.isTrue(3, 10)) {
                                    param = Util.nextInt(0, 10);
                                }
                                for (ItemOption io : item.itemOptions) {
                                    int optId = io.optionTemplate.id;
                                    switch (optId) {
                                        case 47: //giáp
                                        case 6: //hp
                                        case 26: //hp/30s
                                        case 22: //hp k
                                        case 0: //sức đánh
                                        case 7: //ki
                                        case 28: //ki/30s
                                        case 23: //ki k
                                        case 14: //crit
                                            io.param += ((long) io.param * param / 100);
                                            break;
                                    }
                                }
                                InventoryService.gI().subQuantityItemsBag(player, meal, 99);
                                InventoryService.gI().addItemBag(player, item, 99);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Đổi thành công " + is.temp.name);
                            } else {
                                Service.getInstance().sendThongBao(player, "Yêu cầu có 99 thức ăn");
                                return;
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Yêu cầu có đủ trang bị thần linh");
                            return;
                        }
                        break;
                    case ConstNpc.SHOP_SANTA_1:
                        player.head = is.temp.part;
                        Service.getInstance().Send_Caitrang(player);
                        Service.getInstance().sendThongBao(player, "Đổi kiểu tóc thành công");
                        break;
                    case ConstNpc.SHOP_BA_HAT_MIT_0:
                        player.charms.addTimeCharms(is.temp.id, 60);
                        openShopBua(player, player.iDMark.getShopId(), 0);
                        break;
                    case ConstNpc.SHOP_BA_HAT_MIT_1:
                        player.charms.addTimeCharms(is.temp.id, 60 * 8);
                        openShopBua(player, player.iDMark.getShopId(), 1);
                        break;
                    case ConstNpc.SHOP_BA_HAT_MIT_2:
                        player.charms.addTimeCharms(is.temp.id, 60 * 24 * 30);
                        openShopBua(player, player.iDMark.getShopId(), 2);
                        break;
                    case ConstNpc.SHOP_BA_HAT_MIT_3:
                        player.charms.addTimeCharms(is.temp.id, 60);
                        openShopBua(player, player.iDMark.getShopId(), 3);
                        break;
                    case ConstNpc.SHOP_GIAM_GIA:
                        Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
                        if (pGG != null) {
                            Item item = ItemService.gI().createItemFromItemShop(is);
                            InventoryService.gI().subQuantityItemsBag(player, pGG, 1);
                            InventoryService.gI().addItemBag(player, item, 1);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendThongBao(player, "Đổi thành công " + is.temp.name);
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn không có phiếu giảm giá!");
                        }
                        break;

                    case ConstNpc.SHOP_WHIS_THIEN_SU:
                        int param = 0;
                        Item item = ItemService.gI().createItemFromItemShop(is);
                        InventoryService.gI().addItemBag(player, item, 99);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Đổi thành công " + is.temp.name);
                        break;
                    default:
                        InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is), 99);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Mua thành công " + is.temp.name);
                        break;
                }
            } else {
                Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
            }
            Service.getInstance().sendMoney(player);
        }
    }

    private boolean isLimitItem(int id) {
        return id >= ConstItem.DA_NANG_CAP_CAP_1 && id <= ConstItem.CONG_THUC_VIP_1086;
    }

    private int getBuyLimit(int id) {
        switch (id) {
            case ConstItem.DA_NANG_CAP_CAP_1:
            case ConstItem.DA_NANG_CAP_CAP_2:
            case ConstItem.DA_MAY_MAN_CAP_1:
            case ConstItem.DA_MAY_MAN_CAP_2:
            case ConstItem.CONG_THUC_VIP:
            case ConstItem.CONG_THUC_VIP_1085:
            case ConstItem.CONG_THUC_VIP_1086:
                return 10;
            case ConstItem.DA_NANG_CAP_CAP_3:
            case ConstItem.DA_MAY_MAN_CAP_3:
                return 5;
            case ConstItem.DA_NANG_CAP_CAP_4:
            case ConstItem.DA_MAY_MAN_CAP_4:
                return 2;
            case ConstItem.DA_NANG_CAP_CAP_5:
            case ConstItem.DA_MAY_MAN_CAP_5:
                return 1;
        }
        return -1;
    }

    //item reward lucky round---------------------------------------------------
    public void openBoxItemLuckyRound(Player player) {
        player.iDMark.setShopId(ConstNpc.SIDE_BOX_LUCKY_ROUND);
        InventoryService.gI().arrangeItems(player.inventory.itemsBoxCrackBall);
        Message msg;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Rương đồ");
            int n = player.inventory.itemsBoxCrackBall.size()
                    - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall);
            msg.writer().writeByte(n);
            for (int i = 0; i < n; i++) {
                Item item = player.inventory.itemsBoxCrackBall.get(i);
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("\n|7|DRAGON LUCKY");
                List<ItemOption> itemOptions = item.getDisplayOptions();
                msg.writer().writeByte(itemOptions.size());
                for (ItemOption io : itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                msg.writer().writeByte(1);
                CaiTrang ct = Manager.getCaiTrangByItemId(item.template.id);
                msg.writer().writeByte(ct != null ? 1 : 0);
                if (ct != null) {
                    msg.writer().writeShort(ct.getID()[0]);
                    msg.writer().writeShort(ct.getID()[1]);
                    msg.writer().writeShort(ct.getID()[2]);
                    msg.writer().writeShort(ct.getID()[3]);
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void getItemSideBoxLuckyRound(Player player, byte type, int index) {
        if (index < 0 || index >= player.inventory.itemsBoxCrackBall.size()) {
            return;
        }
        Item item = player.inventory.itemsBoxCrackBall.get(index);
        switch (type) {
            case 0: //nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item, 0);
                        Service.getInstance().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        InventoryService.gI().sendItemBags(player);
                        InventoryService.gI().removeItem(player.inventory.itemsBoxCrackBall, index);
                        openBoxItemLuckyRound(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: //xóa
                InventoryService.gI().subQuantityItem(player.inventory.itemsBoxCrackBall, item, item.quantity);
                openBoxItemLuckyRound(player);
                Service.getInstance().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: //nhận hết
                for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                    item = player.inventory.itemsBoxCrackBall.get(i);
                    if (item.isNotNullItem()) {
                        if (InventoryService.gI().addItemBag(player, item, 0)) {
                            player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            Service.getInstance().sendThongBao(player,
                                    "Bạn nhận được " + (item.template.id == 189
                                            ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        }
                    } else {
                        break;
                    }
                }
                InventoryService.gI().sendItemBags(player);
                openBoxItemLuckyRound(player);
                break;
        }
    }
    //item reward---------------------------------------------------------------

    public void openBoxItemReward(Player player) {
        if (player.getSession().itemsReward == null) {
            player.getSession().initItemsReward();
        }
        player.iDMark.setShopId(ConstNpc.SIDE_BOX_ITEM_REWARD);
        Message msg;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Phần\nthưởng");
            msg.writer().writeByte(player.getSession().itemsReward.size());
            for (Item item : player.getSession().itemsReward) {
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("\n|7|DRAGON LUCKY");
                List<ItemOption> itemOptions = item.getDisplayOptions();
                msg.writer().writeByte(itemOptions.size() + 1);
                for (ItemOption io : itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                //số lượng
                msg.writer().writeByte(31);
                msg.writer().writeShort(item.quantity);
                //
                msg.writer().writeByte(1);
                CaiTrang ct = Manager.getCaiTrangByItemId(item.template.id);
                msg.writer().writeByte(ct != null ? 1 : 0);
                if (ct != null) {
                    msg.writer().writeShort(ct.getID()[0]);
                    msg.writer().writeShort(ct.getID()[1]);
                    msg.writer().writeShort(ct.getID()[2]);
                    msg.writer().writeShort(ct.getID()[3]);
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getItemSideBoxReward(Player player, byte type, int index) {
        if (index < 0 || index >= player.getSession().itemsReward.size()) {
            return;
        }
        Item item = player.getSession().itemsReward.get(index);
        switch (type) {
            case 0: //nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item, 0);
                        Service.getInstance().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        InventoryService.gI().sendItemBags(player);
                        player.getSession().itemsReward.remove(index);
                        openBoxItemReward(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: //xóa
                player.getSession().itemsReward.remove(index);
                openBoxItemReward(player);
                Service.getInstance().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: //nhận hết
                for (int i = player.getSession().itemsReward.size() - 1; i >= 0; i--) {
                    item = player.getSession().itemsReward.get(i);
                    if (item.isNotNullItem()) {
                        if (InventoryService.gI().addItemBag(player, item, 0)) {
                            player.getSession().itemsReward.remove(i);
                            Service.getInstance().sendThongBao(player,
                                    "Bạn nhận được " + (item.template.id == 189
                                            ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        }
                    } else {
                        break;
                    }
                }
                InventoryService.gI().sendItemBags(player);
                openBoxItemReward(player);
                break;
        }
        PlayerDAO.updateItemReward(player);

    }

    //--------------------------------------------------------------------------
    //điều hướng mua
    public void buyItem(Player player, byte type, int tempId) {
        switch (player.iDMark.getShopId()) {
            case ConstNpc.SIDE_BOX_LUCKY_ROUND:
                getItemSideBoxLuckyRound(player, type, tempId);
                break;
            case ConstNpc.SIDE_BOX_ITEM_REWARD:
                getItemSideBoxReward(player, type, tempId);
                break;
            case ConstNpc.SHOP_LEARN_SKILL:
                learnSkill(player, getItemShop(player.iDMark.getShopId(), tempId));
                break;
            default:
                buyItemShopNormal(player, getItemShop(player.iDMark.getShopId(), tempId));
                break;
        }
    }

    public void showConfirmSellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            if (index < 0 || index >= pl.inventory.itemsBody.size()) {
                return;
            }
            item = pl.inventory.itemsBody.get(index);
        } else {
            if (index < 0 || index >= pl.inventory.itemsBag.size()) {
                return;
            }
            item = pl.inventory.itemsBag.get(index);
        }
        if (item.isNotNullItem()) {
            int goldReceive = 0;
            switch (item.template.id) {
                case 457:
                    goldReceive = COST_GOLD_BAR;
                    break;
                case 2011:
                    goldReceive = COST_LOCK_GOLD_BAR;
                    break;
                default:
                    goldReceive = item.quantity;
                    break;
            }
            Message msg = new Message(7);
            try {
                msg.writer().writeByte(where);
                msg.writer().writeShort(index);
                msg.writer().writeUTF("Bạn có muốn bán\n x" + (item.template.id == 457 || item.template.id == 2011 ? 1 : item.quantity) + " " + item.template.name
                        + "\nvới giá là " + Util.numberToMoney(goldReceive) + " vàng?");
                pl.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            if (index < 0 || index >= pl.inventory.itemsBody.size()) {
                return;
            }
            item = pl.inventory.itemsBody.get(index);
        } else {
            if (index < 0 || index >= pl.inventory.itemsBag.size()) {
                return;
            }
            item = pl.inventory.itemsBag.get(index);
        }
        if (item != null && item.isNotNullItem()) {
            int goldReceive = 0;
            switch (item.template.id) {
                case 457:
                    goldReceive = COST_GOLD_BAR;
                    break;
                case 2011:
                    goldReceive = COST_LOCK_GOLD_BAR;
                    break;
                default:
                    goldReceive = item.quantity;
                    break;
            }
            if (pl.inventory.gold + goldReceive <= pl.inventory.getGoldLimit()) {
                if (where == 0) {
                    InventoryService.gI().subQuantityItemsBody(pl, item, item.quantity);
                    InventoryService.gI().sendItemBody(pl);
                    Service.getInstance().Send_Caitrang(pl);
                } else {
                    if (item.template.id == 457 || item.template.id == 2011) {
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        InventoryService.gI().subQuantityItemsBag(pl, item, item.quantity);
                    }
                    InventoryService.gI().sendItemBags(pl);
                }
                pl.inventory.gold += goldReceive;
                pl.playerTask.achievements.get(ConstAchive.TRUM_NHAT_VE_CHAI).count++;
                PlayerService.gI().sendInfoHpMpMoney(pl);
                Service.getInstance().sendThongBao(pl, "Đã bán " + item.template.name
                        + " thu được " + Util.numberToMoney(goldReceive) + " vàng");
            } else {
                Service.getInstance().sendThongBao(pl, "Vàng sau khi bán vượt quá giới hạn");
            }
        } else {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        }
    }
}
