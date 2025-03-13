/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nro.nro_online.models.mob;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

/**
 *
 * @author Béo
 */
public class ArrietyDrop {

    public static final int[][] list_do_than_linh = {
        {555, 556, 562, 563, 561},//td
        {557, 558, 564, 565, 561},//nm
        {559, 560, 566, 567, 561}//xd
    };

    public static final int[] list_thuc_an = new int[]{663, 664, 665, 666, 667};
    public static final int[] list_manh_thien_xu = new int[]{1066, 1067, 1068, 1069, 1070};
    public static final int[] list_manh_than_linh = new int[]{1141, 1142, 1143, 1144, 1145};
    public static final short[] list_dnc = {220, 221, 222, 223, 224};

    public static final int[][] list_do_huy_diet = {
        {650, 651, 657, 658, 656},//td
        {652, 653, 659, 660, 656},//nm
        {654, 655, 661, 662, 656}//xd
    };

    public static final int[][] list_item_SKH = {
        {0, 6, 21, 27, 12},//td
        {1, 7, 22, 28, 12},//nm
        {2, 8, 23, 29, 12}//xd
    };

    public static void DropItemReWard(Player player, int idItem, int soluong, int x, int y) {
        ItemMap item = new ItemMap(player.zone, idItem, soluong, Util.nextInt((x - 50), (x + 50)), y, player.id);
        item.options.add(new ItemOption(30, 0));
        Service.getInstance().dropItemMap(player.zone, item);
    }

    public static boolean IsItemKhongChoGiaoDich(int id) {
        return (id >= 663 && id <= 667);
    }

    public static Item randomCS_DKH(int itemId, byte type, byte gender) {
        Item it = ItemService.gI().createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(0, 1, 2);
        List<Integer> quan = Arrays.asList(6, 7, 8);
        List<Integer> gang = Arrays.asList(21, 22, 23);
        List<Integer> giay = Arrays.asList(27, 28, 29);
        int rada = 12;

        if (ao.contains(itemId)) {
            it.itemOptions.add(new ItemOption(47, new Random().nextInt(3) + 2)); // áo từ 2-5 giáp
        }
        if (quan.contains(itemId)) {
            it.itemOptions.add(new ItemOption(6, new Random().nextInt(80) + 20)); // hp 20-100
        }
        if (gang.contains(itemId)) {
            it.itemOptions.add(new ItemOption(0, new Random().nextInt(6) + 4)); // sd 4-6
        }
        if (giay.contains(itemId)) {
            it.itemOptions.add(new ItemOption(7, new Random().nextInt(80) + 20)); // ki 20-100
        }
        if (rada == itemId) {
            it.itemOptions.add(new ItemOption(14, 1)); //chí mạng 1%
        }
        if (type == 0) {
            if (Util.isTrue(20, 100)) {
                int IDSet = Util.nextInt(0, 6);
                switch (gender) {
                    case 0:
                        if (IDSet <= 2) {
                            it.itemOptions.add(new ItemOption(210, 0));
                            it.itemOptions.add(new ItemOption(222, 0));
                        } else if (IDSet <= 4) {
                            it.itemOptions.add(new ItemOption(211, 0));
                            it.itemOptions.add(new ItemOption(223, 0));
                        } else {
                            it.itemOptions.add(new ItemOption(212, 0));
                            it.itemOptions.add(new ItemOption(224, 0));
                        }
                        break;
                    case 1:
                        if (IDSet <= 2) {
                            it.itemOptions.add(new ItemOption(213, 0));
                            it.itemOptions.add(new ItemOption(225, 0));
                        } else if (IDSet <= 4) {
                            it.itemOptions.add(new ItemOption(214, 0));
                            it.itemOptions.add(new ItemOption(226, 0));
                        } else {
                            it.itemOptions.add(new ItemOption(215, 0));
                            it.itemOptions.add(new ItemOption(227, 0));
                        }
                        break;
                    case 2:
                        if (IDSet <= 2) {
                            it.itemOptions.add(new ItemOption(216, 0));
                            it.itemOptions.add(new ItemOption(219, 0));
                        } else if (IDSet <= 4) {
                            it.itemOptions.add(new ItemOption(217, 0));
                            it.itemOptions.add(new ItemOption(220, 0));
                        } else {
                            it.itemOptions.add(new ItemOption(218, 0));
                            it.itemOptions.add(new ItemOption(221, 0));
                        }
                        break;
                }
                it.itemOptions.add(new ItemOption(30, 0));// ko the gd
            }
            if (Util.isTrue(10, 100)) {
                byte numstar = 1;
                int tile = Util.nextInt(100);
                if (tile > 90) {
                    numstar = 3;
                } else if (tile > 60) {
                    numstar = 2;
                } else {
                    numstar = 1;
                }
                it.itemOptions.add(new ItemOption(107, numstar));
            }
        } else if (type == 1) { // Đồ nâng cấp 100% chỉ số
            int IDSet = Util.nextInt(0, 6);
            switch (gender) {
                case 0:
                    if (IDSet <= 2) {
                        it.itemOptions.add(new ItemOption(127, 0));
                        it.itemOptions.add(new ItemOption(139, 0));
                    } else if (IDSet <= 4) {
                        it.itemOptions.add(new ItemOption(128, 0));
                        it.itemOptions.add(new ItemOption(140, 0));
                    } else {
                        it.itemOptions.add(new ItemOption(129, 0));
                        it.itemOptions.add(new ItemOption(141, 0));
                    }
                    break;
                case 1:
                    if (IDSet <= 2) {
                        it.itemOptions.add(new ItemOption(130, 0));
                        it.itemOptions.add(new ItemOption(142, 0));
                    } else if (IDSet <= 4) {
                        it.itemOptions.add(new ItemOption(131, 0));
                        it.itemOptions.add(new ItemOption(143, 0));
                    } else {
                        it.itemOptions.add(new ItemOption(132, 0));
                        it.itemOptions.add(new ItemOption(144, 0));
                    }
                    break;
                case 2:
                    if (IDSet <= 2) {
                        it.itemOptions.add(new ItemOption(133, 0));
                        it.itemOptions.add(new ItemOption(136, 0));
                    } else if (IDSet <= 4) {
                        it.itemOptions.add(new ItemOption(134, 0));
                        it.itemOptions.add(new ItemOption(137, 0));
                    } else {
                        it.itemOptions.add(new ItemOption(135, 0));
                        it.itemOptions.add(new ItemOption(138, 0));
                    }
                    break;
            }
            it.itemOptions.add(new ItemOption(30, 0));// ko the gd
        }
        return it;
    }

    public static Item randomCS_DKHTL(int itemId, int typeOption, byte gender) {
        Item it = ItemService.gI().createItemSetKichHoat(itemId, 1);
        int[] doTD = new int[]{555, 556, 562, 563};

        int[] doNM = new int[]{557, 558, 564, 565};

        int[] doXD = new int[]{559, 560, 566, 567};
        int rada = 561;
        switch (gender) {
            case 0:
                if (doTD[0] == itemId) {
                    it.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(150) + 700))); // áo từ 1800-2800 giáp
                }
                if (doTD[1] == itemId) {
                    it.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(15) + 45))); // hp 85-100k
                }
                if (doTD[2] == itemId) {
                    it.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(500) + 4500))); // 8500-10000
                }
                if (doTD[3] == itemId) {
                    it.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(15) + 45))); // ki 80-90k
                }
                break;
            case 1:
                if (doNM[0] == itemId) {
                    it.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(150) + 700))); // áo từ 1800-2800 giáp
                }
                if (doNM[1] == itemId) {
                    it.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(15) + 45))); // hp 85-100k
                }
                if (doNM[2] == itemId) {
                    it.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(500) + 4500))); // 8500-10000
                }
                if (doNM[3] == itemId) {
                    it.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(15) + 45))); // ki 80-90k
                }
                break;
            case 2:
                if (doXD[0] == itemId) {
                    it.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(150) + 700))); // áo từ 1800-2800 giáp
                }
                if (doXD[1] == itemId) {
                    it.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(15) + 45))); // hp 85-100k
                }
                if (doXD[2] == itemId) {
                    it.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(500) + 4500))); // 8500-10000
                }
                if (doXD[3] == itemId) {
                    it.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(15) + 45))); // ki 80-90k
                }
                break;
        }
        if (rada == itemId) {
            it.itemOptions.add(new ItemOption(14, new Random().nextInt(2) + 13)); //chí mạng 17-19%
            switch (typeOption) {
                case 6://Set Kakarot  
                    it.itemOptions.add(new ItemOption(133, 0));
                    it.itemOptions.add(new ItemOption(136, 0));
                    break;
                case 7://Set Vegeta
                    it.itemOptions.add(new ItemOption(134, 0));
                    it.itemOptions.add(new ItemOption(137, 0));
                    break;
                case 8://Set Nappa
                    it.itemOptions.add(new ItemOption(135, 0));
                    it.itemOptions.add(new ItemOption(138, 0));
                    break;
            }
        }
        switch (gender) {
            case 0:
                switch (typeOption) {
                    case 0://Set Tenshinhan
                        it.itemOptions.add(new ItemOption(127, 0));
                        it.itemOptions.add(new ItemOption(139, 0));
                        break;
                    case 1://Set Krillin
                        it.itemOptions.add(new ItemOption(128, 0));
                        it.itemOptions.add(new ItemOption(140, 0));
                        break;
                    case 2://Set Songoku
                        it.itemOptions.add(new ItemOption(129, 0));
                        it.itemOptions.add(new ItemOption(141, 0));
                        break;
                }
                break;
            case 1:
                switch (typeOption) {
                    case 3://Set Piccolo
                        it.itemOptions.add(new ItemOption(130, 0));
                        it.itemOptions.add(new ItemOption(142, 0));
                        break;
                    case 4://Set Dende
                        it.itemOptions.add(new ItemOption(131, 0));
                        it.itemOptions.add(new ItemOption(143, 0));
                        break;
                    case 5://Set Pikkoro Daimao
                        it.itemOptions.add(new ItemOption(132, 0));
                        it.itemOptions.add(new ItemOption(144, 0));
                        break;
                }
                break;
            case 2:
                switch (typeOption) {
                    case 6://Set Kakarot  
                        it.itemOptions.add(new ItemOption(133, 0));
                        it.itemOptions.add(new ItemOption(136, 0));
                        break;
                    case 7://Set Vegeta
                        it.itemOptions.add(new ItemOption(134, 0));
                        it.itemOptions.add(new ItemOption(137, 0));
                        break;
                    case 8://Set Nappa
                        it.itemOptions.add(new ItemOption(135, 0));
                        it.itemOptions.add(new ItemOption(138, 0));
                        break;
                }
                break;
        }
        it.itemOptions.add(new ItemOption(30, 0));// ko the gd
        it.itemOptions.add(new ItemOption(21, 100));// ko the gd

        return it;
    }

    public static ItemMap DropItemSetKichHoat(Player player, int soluong, int x, int y) {
        Item itemKichHoat = ArrietyDrop.randomCS_DKH(ArrietyDrop.list_item_SKH[player.gender][Util.nextInt(0, 4)], (byte) 0, player.gender);
        ItemMap item = new ItemMap(player.zone, itemKichHoat.template.id, soluong, Util.nextInt((x - 50), (x + 50)), y, player.id);
        item.options = itemKichHoat.itemOptions;
        return item;
    }

    public static ItemMap DropItemReWardDoTL(Player player, int soluong, int x, int y) {
        Item itemHuyDiet = RewardService.randomCS_DHD(ArrietyDrop.list_do_than_linh[player.gender][Util.nextInt(0, 4)], player.gender);
        ItemMap item = new ItemMap(player.zone, itemHuyDiet.template.id, soluong, Util.nextInt((x - 50), (x + 50)), y, player.id);
        item.options = itemHuyDiet.itemOptions;
        return item;
    }
}
