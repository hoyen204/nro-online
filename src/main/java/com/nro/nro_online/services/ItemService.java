package com.nro.nro_online.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.item.ItemOptionTemplate;
import com.nro.nro_online.models.item.ItemShop;
import com.nro.nro_online.models.item.ItemTemplate;
import com.nro.nro_online.models.kygui.ConsignmentItem;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.utils.TimeUtil;
import com.nro.nro_online.utils.Util;


public class ItemService {
    private ItemService() {}

    private static final ItemService instance = new ItemService();

    public static ItemService gI() {
        return instance;
    }

    public Item createNullItem() {
        return new Item();
    }

    public Item createItemFromItemShop(ItemShop itemShop) {
        Item item = new Item();
        item.template = itemShop.temp;
        item.quantity = 1;
        item.content = item.getContent();
        item.info = item.getInfo();
        for (ItemOption io : itemShop.options) {
            item.itemOptions.add(new ItemOption(io));
        }
        return item;
    }

    public Item copyItem(Item item) {
        return item.getClone();
    }

    public Item otpKH(short tempId) {
        return otpKH(tempId, 1);
    }

    public Item Disguise() {
        Item it = ItemService.gI().createNewItem((short) 742);
        it.itemOptions.add(new ItemOption(50, 80));
        it.itemOptions.add(new ItemOption(77, 80));
        it.itemOptions.add(new ItemOption(103, 80));
        it.itemOptions.add(new ItemOption(228, 0));
        it.itemOptions.add(new ItemOption(230, 0));
        return it;
    }

    public Item otpKH(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        switch (item.template.type) {
            case 0:
                item.itemOptions.add(new ItemOption(47, Util.nextInt(1, 4)));
                break;
            case 1:
                item.itemOptions.add(new ItemOption(6, Util.nextInt(150, 200)));
                break;
            case 2:
                item.itemOptions.add(new ItemOption(0, Util.nextInt(7, 29)));
                break;
            case 3:
                item.itemOptions.add(new ItemOption(7, Util.nextInt(2, 12)));
                break;
            case 4:
                item.itemOptions.add(new ItemOption(14, 1));
                break;
            default:
                break;
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public void setSongoku(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 0);
        Item quan = ItemService.gI().otpKH((short) 6);
        Item gang = ItemService.gI().otpKH((short) 21);
        Item giay = ItemService.gI().otpKH((short) 27);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(129, 0));//129
        quan.itemOptions.add(new ItemOption(129, 0));
        gang.itemOptions.add(new ItemOption(129, 0));
        giay.itemOptions.add(new ItemOption(129, 0));
        rd.itemOptions.add(new ItemOption(129, 0));
        ao.itemOptions.add(new ItemOption(141, 0));
        quan.itemOptions.add(new ItemOption(141, 0));
        gang.itemOptions.add(new ItemOption(141, 0));
        giay.itemOptions.add(new ItemOption(141, 0));
        rd.itemOptions.add(new ItemOption(141, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Kame");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setThenXinHang(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 0);
        Item quan = ItemService.gI().otpKH((short) 6);
        Item gang = ItemService.gI().otpKH((short) 21);
        Item giay = ItemService.gI().otpKH((short) 27);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(128, 0));
        quan.itemOptions.add(new ItemOption(128, 0));
        gang.itemOptions.add(new ItemOption(128, 0));
        giay.itemOptions.add(new ItemOption(128, 0));
        rd.itemOptions.add(new ItemOption(128, 0));
        ao.itemOptions.add(new ItemOption(139, 0));
        quan.itemOptions.add(new ItemOption(139, 0));
        gang.itemOptions.add(new ItemOption(139, 0));
        giay.itemOptions.add(new ItemOption(139, 0));
        rd.itemOptions.add(new ItemOption(139, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Thên Xin Hăng");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setKaioKen(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 0);
        Item quan = ItemService.gI().otpKH((short) 6);
        Item gang = ItemService.gI().otpKH((short) 21);
        Item giay = ItemService.gI().otpKH((short) 27);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(128, 0));
        quan.itemOptions.add(new ItemOption(128, 0));
        gang.itemOptions.add(new ItemOption(128, 0));
        giay.itemOptions.add(new ItemOption(128, 0));
        rd.itemOptions.add(new ItemOption(128, 0));
        ao.itemOptions.add(new ItemOption(140, 0));
        quan.itemOptions.add(new ItemOption(140, 0));
        gang.itemOptions.add(new ItemOption(140, 0));
        giay.itemOptions.add(new ItemOption(140, 0));
        rd.itemOptions.add(new ItemOption(140, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Kaioken");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setLienHoan(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 1);
        Item quan = ItemService.gI().otpKH((short) 7);
        Item gang = ItemService.gI().otpKH((short) 22);
        Item giay = ItemService.gI().otpKH((short) 28);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(131, 0));
        quan.itemOptions.add(new ItemOption(131, 0));
        gang.itemOptions.add(new ItemOption(131, 0));
        giay.itemOptions.add(new ItemOption(131, 0));
        rd.itemOptions.add(new ItemOption(131, 0));

        ao.itemOptions.add(new ItemOption(143, 0));
        quan.itemOptions.add(new ItemOption(143, 0));
        gang.itemOptions.add(new ItemOption(143, 0));
        giay.itemOptions.add(new ItemOption(143, 0));
        rd.itemOptions.add(new ItemOption(143, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Liên hoàn");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setPicolo(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 1);
        Item quan = ItemService.gI().otpKH((short) 7);
        Item gang = ItemService.gI().otpKH((short) 22);
        Item giay = ItemService.gI().otpKH((short) 28);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(130, 0));
        quan.itemOptions.add(new ItemOption(130, 0));
        gang.itemOptions.add(new ItemOption(130, 0));
        giay.itemOptions.add(new ItemOption(130, 0));
        rd.itemOptions.add(new ItemOption(130, 0));

        ao.itemOptions.add(new ItemOption(142, 0));
        quan.itemOptions.add(new ItemOption(142, 0));
        gang.itemOptions.add(new ItemOption(142, 0));
        giay.itemOptions.add(new ItemOption(142, 0));
        rd.itemOptions.add(new ItemOption(142, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Picolo");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setPikkoroDaimao(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 1);
        Item quan = ItemService.gI().otpKH((short) 7);
        Item gang = ItemService.gI().otpKH((short) 22);
        Item giay = ItemService.gI().otpKH((short) 28);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(132, 0));
        quan.itemOptions.add(new ItemOption(132, 0));
        gang.itemOptions.add(new ItemOption(132, 0));
        giay.itemOptions.add(new ItemOption(132, 0));
        rd.itemOptions.add(new ItemOption(132, 0));

        ao.itemOptions.add(new ItemOption(144, 0));
        quan.itemOptions.add(new ItemOption(144, 0));
        gang.itemOptions.add(new ItemOption(144, 0));
        giay.itemOptions.add(new ItemOption(144, 0));
        rd.itemOptions.add(new ItemOption(144, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Picolo");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setKakarot(Player player) throws Exception {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 2);
        Item quan = ItemService.gI().otpKH((short) 8);
        Item gang = ItemService.gI().otpKH((short) 23);
        Item giay = ItemService.gI().otpKH((short) 29);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(133, 0));
        quan.itemOptions.add(new ItemOption(133, 0));
        gang.itemOptions.add(new ItemOption(133, 0));
        giay.itemOptions.add(new ItemOption(133, 0));
        rd.itemOptions.add(new ItemOption(133, 0));

        ao.itemOptions.add(new ItemOption(136, 0));
        quan.itemOptions.add(new ItemOption(136, 0));
        gang.itemOptions.add(new ItemOption(136, 0));
        giay.itemOptions.add(new ItemOption(136, 0));
        rd.itemOptions.add(new ItemOption(136, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Kakarot");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setCadic(Player player) {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 2);
        Item quan = ItemService.gI().otpKH((short) 8);
        Item gang = ItemService.gI().otpKH((short) 23);
        Item giay = ItemService.gI().otpKH((short) 29);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(134, 0));
        quan.itemOptions.add(new ItemOption(134, 0));
        gang.itemOptions.add(new ItemOption(134, 0));
        giay.itemOptions.add(new ItemOption(134, 0));
        rd.itemOptions.add(new ItemOption(134, 0));

        ao.itemOptions.add(new ItemOption(137, 0));
        quan.itemOptions.add(new ItemOption(137, 0));
        gang.itemOptions.add(new ItemOption(137, 0));
        giay.itemOptions.add(new ItemOption(137, 0));
        rd.itemOptions.add(new ItemOption(137, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Ca đíc");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setNappa(Player player) {
        Item hq = InventoryService.gI().findItem(player.inventory.itemsBag, 1228);
        Item ao = ItemService.gI().otpKH((short) 2);
        Item quan = ItemService.gI().otpKH((short) 8);
        Item gang = ItemService.gI().otpKH((short) 23);
        Item giay = ItemService.gI().otpKH((short) 29);
        Item rd = ItemService.gI().otpKH((short) 12);
        ao.itemOptions.add(new ItemOption(135, 0));
        quan.itemOptions.add(new ItemOption(135, 0));
        gang.itemOptions.add(new ItemOption(135, 0));
        giay.itemOptions.add(new ItemOption(135, 0));
        rd.itemOptions.add(new ItemOption(135, 0));

        ao.itemOptions.add(new ItemOption(138, 0));
        quan.itemOptions.add(new ItemOption(138, 0));
        gang.itemOptions.add(new ItemOption(138, 0));
        giay.itemOptions.add(new ItemOption(138, 0));
        rd.itemOptions.add(new ItemOption(138, 0));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        rd.itemOptions.add(new ItemOption(30, 0));
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            InventoryService.gI().addItemBag(player, ao, 0);
            InventoryService.gI().addItemBag(player, quan, 0);
            InventoryService.gI().addItemBag(player, gang, 0);
            InventoryService.gI().addItemBag(player, giay, 0);
            InventoryService.gI().addItemBag(player, rd, 0);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set Kích hoạt Nappa");
            InventoryService.gI().subQuantityItemsBag(player, hq, 1);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public Item angelClothes(int itemId, int gender, int perSuccess, int perLucky) {
        Item dots = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(1048, 1049, 1050);
        List<Integer> quan = Arrays.asList(1051, 1052, 1053);
        List<Integer> gang = Arrays.asList(1054, 1055, 1056);
        List<Integer> giay = Arrays.asList(1057, 1058, 1059);
        List<Integer> nhan = Arrays.asList(1060, 1061, 1062);
        //áo
        if (ao.contains(itemId)) {
            switch (gender) {
                case 0:
                    dots.itemOptions.add(new ItemOption(47, Util.nextInt(120, 135) * 1_600 / 100)); // áo TD giáp
                    break;
                case 1:
                    dots.itemOptions.add(new ItemOption(47, Util.nextInt(120, 135) * 1_700 / 100)); // áo NM giáp
                    break;
                case 2:
                    dots.itemOptions.add(new ItemOption(47, Util.nextInt(120, 135) * 1_800 / 100)); // áo XD giáp
                    break;
            }
        }
        //quần
        if (Util.isTrue(80, 100)) {
            if (quan.contains(itemId)) {
                switch (gender) {
                    case 0:
                        dots.itemOptions.add(new ItemOption(22, Util.nextInt(120, 130) * 104 / 100)); // quần TD hp
                        dots.itemOptions.add(new ItemOption(27, Util.nextInt(120, 130) * 16_000 / 100)); // quần td hồi hp
                        break;
                    case 1:
                        dots.itemOptions.add(new ItemOption(22, Util.nextInt(120, 130) * 100 / 100)); // quần NM hp
                        dots.itemOptions.add(new ItemOption(27, Util.nextInt(120, 130) * 14_000 / 100)); // quần NM hồi hp
                        break;
                    case 2:
                        dots.itemOptions.add(new ItemOption(22, Util.nextInt(120, 130) * 96 / 100)); // quần XD hp
                        dots.itemOptions.add(new ItemOption(27, Util.nextInt(120, 130) * 12_000 / 100)); // quần XD hồi hp
                        break;
                }
            }
        } else {
            if (quan.contains(itemId)) {
                switch (gender) {
                    case 0:
                        dots.itemOptions.add(new ItemOption(22, Util.nextInt(120, 135) * 104 / 100)); // quần TD hp
                        dots.itemOptions.add(new ItemOption(27, Util.nextInt(120, 135) * 16_000 / 100)); // quần td hồi hp
                        break;
                    case 1:
                        dots.itemOptions.add(new ItemOption(22, Util.nextInt(120, 135) * 100 / 100)); // quần NM hp
                        dots.itemOptions.add(new ItemOption(27, Util.nextInt(120, 135) * 14_000 / 100)); // quần NM hồi hp
                        break;
                    case 2:
                        dots.itemOptions.add(new ItemOption(22, Util.nextInt(120, 135) * 96 / 100)); // quần XD hp
                        dots.itemOptions.add(new ItemOption(27, Util.nextInt(120, 135) * 12_000 / 100)); // quần XD hồi hp
                        break;
                }
            }
        }
        //găng
        if (Util.isTrue(80, 100)) {
            if (gang.contains(itemId)) {
                switch (gender) {
                    case 0:
                        dots.itemOptions.add(new ItemOption(0, Util.nextInt(120, 130) * 10_800 / 100)); // gang TD sd
                        break;
                    case 1:
                        dots.itemOptions.add(new ItemOption(0, Util.nextInt(120, 130) * 10_800 / 100)); // gang NM sd
                        break;
                    case 2:
                        dots.itemOptions.add(new ItemOption(0, Util.nextInt(120, 130) * 10_800 / 100)); // gang XD sd
                        break;
                }
            }
        } else {
            if (gang.contains(itemId)) {
                switch (gender) {
                    case 0:
                        dots.itemOptions.add(new ItemOption(0, Util.nextInt(120, 135) * 10_800 / 100)); // gang TD sd
                        break;
                    case 1:
                        dots.itemOptions.add(new ItemOption(0, Util.nextInt(120, 135) * 10_800 / 100)); // gang NM sd
                        break;
                    case 2:
                        dots.itemOptions.add(new ItemOption(0, Util.nextInt(120, 135) * 10_800 / 100)); // gang XD sd
                        break;
                }
            }
        }
        //giày
        if (Util.isTrue(80, 100)) {
            if (giay.contains(itemId)) {
                switch (gender) {
                    case 0:
                        dots.itemOptions.add(new ItemOption(23, Util.nextInt(120, 130) * 96 / 100)); // quần TD hp
                        dots.itemOptions.add(new ItemOption(28, Util.nextInt(120, 130) * 12_000 / 100)); // quần td hồi hp
                        break;
                    case 1:
                        dots.itemOptions.add(new ItemOption(23, Util.nextInt(120, 130) * 100 / 100)); // quần NM hp
                        dots.itemOptions.add(new ItemOption(28, Util.nextInt(120, 130) * 12_800 / 100)); // quần NM hồi hp
                        break;
                    case 2:
                        dots.itemOptions.add(new ItemOption(23, Util.nextInt(120, 130) * 92 / 100)); // quần XD hp
                        dots.itemOptions.add(new ItemOption(28, Util.nextInt(120, 130) * 11_200 / 100)); // quần XD hồi hp
                        break;
                }
            }
        } else {
            if (giay.contains(itemId)) {
                switch (gender) {
                    case 0:
                        dots.itemOptions.add(new ItemOption(23, Util.nextInt(120, 135) * 96 / 100)); // quần TD hp
                        dots.itemOptions.add(new ItemOption(28, Util.nextInt(120, 135) * 12_000 / 100)); // quần td hồi hp
                        break;
                    case 1:
                        dots.itemOptions.add(new ItemOption(23, Util.nextInt(120, 135) * 100 / 100)); // quần NM hp
                        dots.itemOptions.add(new ItemOption(28, Util.nextInt(120, 135) * 12_800 / 100)); // quần NM hồi hp
                        break;
                    case 2:
                        dots.itemOptions.add(new ItemOption(28, Util.nextInt(120, 135) * 11_200 / 100)); // quần XD hồi hp
                        break;
                }
            }
        }
        if (nhan.contains(itemId)) {
            dots.itemOptions.add(new ItemOption(14, Util.nextInt(120, 135) * 16 / 100)); // nhẫn 18-20%
        }
        dots.itemOptions.add(new ItemOption(21, 90));
        dots.itemOptions.add(new ItemOption(30, 0));
        if (perSuccess <= perLucky) {
            if (perSuccess >= (perLucky - 3)) {
                perLucky = 3;
            } else if (perSuccess <= (perLucky - 4) && perSuccess >= (perLucky - 10)) {
                perLucky = 2;
            } else {
                perLucky = 1;
            }
            dots.itemOptions.add(new ItemOption(41, perLucky));
            ArrayList<Integer> listOptionBonus = new ArrayList<>();
            listOptionBonus.add(42);
            listOptionBonus.add(43);
            listOptionBonus.add(44);
            listOptionBonus.add(45);
            listOptionBonus.add(46);
            listOptionBonus.add(197);
            listOptionBonus.add(198);
            listOptionBonus.add(199);
            listOptionBonus.add(200);
            listOptionBonus.add(201);
            listOptionBonus.add(202);
            listOptionBonus.add(203);
            listOptionBonus.add(204);
            for (int i = 0; i < perLucky; i++) {
                perSuccess = Util.nextInt(0, listOptionBonus.size() - 1);
                dots.itemOptions.add(new ItemOption(listOptionBonus.get(perSuccess), Util.nextInt(1, 6)));
                listOptionBonus.remove(perSuccess);
            }
        }
        return dots;
    }

    public ConsignmentItem convertToConsignmentItem(Item item) {
        ConsignmentItem it = new ConsignmentItem();
        it.itemOptions = new ArrayList<>();
        it.template = item.template;
        it.info = item.info;
        it.content = item.content;
        it.quantity = item.quantity;
        it.createTime = item.createTime;
        for (ItemOption io : item.itemOptions) {
            it.itemOptions.add(new ItemOption(io));
        }
        it.setPriceGold(-1);
        it.setPriceGem(-1);
        return it;
    }

    public Item createItemSetKichHoat(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createNullItem().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createNewItem(short tempId) {
        return createNewItem(tempId, 1);
    }

    public Item createNewItem(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();

        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public ConsignmentItem createNewConsignmentItem(short tempId, int quantity) {
        ConsignmentItem item = new ConsignmentItem();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemFromItemMap(ItemMap itemMap) {
        Item item = createNewItem(itemMap.itemTemplate.id, itemMap.quantity);
        item.itemOptions = itemMap.options;
        return item;
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        return Manager.ITEM_OPTION_TEMPLATES.get(id);
    }

    public ItemTemplate getTemplate(int id) {
        return Manager.ITEM_TEMPLATES.get(id);
    }

    public boolean isItemActivation(Item item) {
        return false;
    }

    public int getPercentTrainArmor(Item item) {
        if (item != null) {
            return switch (item.template.id) {
                case 529, 534 -> 10;
                case 530, 535 -> 20;
                case 531, 536 -> 30;
                default -> 0;
            };
        } else {
            return 0;
        }
    }

    public boolean isTrainArmor(Item item) {
        if (item != null) {
            return switch (item.template.id) {
                case 529, 534, 530, 535, 531, 536 -> true;
                default -> false;
            };
        } else {
            return false;
        }
    }

    public boolean isOutOfDateTime(Item item) {
        long now = System.currentTimeMillis();
        if (item != null) {
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 93) {
                    int dayPass = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.TimeUnit.DAY);
                    if (dayPass != 0) {
                        io.param -= dayPass;
                        if (io.param <= 0) {
                            return true;
                        } else {
                            item.createTime = System.currentTimeMillis();
                        }
                    }
                } else if (io.optionTemplate.id == 196) {
                    long e = io.param * 1000L;
                    if (e <= now) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isUnLimitQuantity(int id) {// item k giới hạn số lượng
        // mảnh trang bị thiên sứ
        return id >= 1066 && id <= 1070;
    }
}
