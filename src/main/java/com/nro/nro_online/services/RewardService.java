package com.nro.nro_online.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.nro.nro_online.art.ServerLog;
import com.nro.nro_online.attr.Attribute;
import com.nro.nro_online.consts.ConstAttribute;
import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.consts.ConstMob;
import com.nro.nro_online.lib.RandomCollection;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemLuckyRound;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.item.ItemOptionLuckyRound;
import com.nro.nro_online.models.item.ItemReward;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.mob.ArrietyDrop;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.mob.MobReward;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.ServerManager;
import com.nro.nro_online.server.ServerNotify;
import com.nro.nro_online.utils.Util;

public class RewardService {

    private static final int[][][] ACTIVATION_SET = {{{129, 141, 1, 1000}, {127, 139, 1, 1000}, {128, 140, 1, 1000}}, //songoku - thien xin hang - kirin
    {{131, 143, 1, 1000}, {132, 144, 1, 1000}, {130, 142, 1, 1000}}, //oc tieu - pikkoro daimao - picolo
    {{135, 138, 1, 1000}, {133, 136, 1, 1000}, {134, 137, 1, 1000}} //kakarot - cadic - nappa
};
    private static RewardService i;

    private RewardService() {
    }

    public static RewardService gI() {
        if (i == null) {
            i = new RewardService();
        }
        return i;
    }

    private MobReward getMobReward(Mob mob) {
        for (MobReward mobReward : Manager.MOB_REWARDS) {
            if (mobReward.tempId == mob.tempId) {
                return mobReward;
            }
        }
        return null;
    }

    //trả về list item quái die
    public List<ItemMap> getRewardItems(Player player, Mob mob, int x, int yEnd) {
        int mapid = player.zone.map.mapId;
        List<ItemMap> list = new ArrayList<>();
        MobReward mobReward = getMobReward(mob);
        if (mobReward != null) {
            int itemSize = mobReward.itemRewards.size();
            int goldSize = mobReward.goldRewards.size();
            int cskbSize = mobReward.capsuleKyBi.size();
            int foodSize = mobReward.foods.size();
            if (itemSize > 0) {
                ItemReward ir = mobReward.itemRewards.get(Util.nextInt(0, itemSize - 1));
                boolean inMap = false;
                if (ir.mapId[0] == -1) {
                    inMap = true;
                } else {
                    for (int i = 0; i < ir.mapId.length; i++) {
                        if (mob.zone.map.mapId == ir.mapId[i]) {
                            inMap = true;
                            break;
                        }
                    }
                }
                if (inMap && ((ir.forAllGender || ItemService.gI().getTemplate(ir.tempId).gender == player.gender || ItemService.gI().getTemplate(ir.tempId).gender > 2)) && Util.isTrue(ir.ratio, ir.typeRatio)) {
                            ItemMap itemMap = new ItemMap(mob.zone, ir.tempId, 1, x, yEnd, player.id);
                            //init option
                            switch (itemMap.itemTemplate.type) {
                            case 0, 1, 2, 3, 4 -> {
                                initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
                                initStarOption(itemMap,
                                        new RatioStar[] { new RatioStar((byte) 1, 20, 100), new RatioStar((byte) 2, 10, 100), new RatioStar((byte) 3, 5, 100),
                                                new RatioStar((byte) 4, 3, 200), new RatioStar((byte) 5, 2, 200), new RatioStar((byte) 6, 1, 200),
                                                new RatioStar((byte) 7, 1, 300), });
                            }
                            case 30 -> initBaseOptionSaoPhaLe(itemMap);
                            }
                            initNotTradeOption(itemMap);
//                            initEventOption(itemMap);

                            //end init option
                            if (itemMap.itemTemplate.id >= 555 && itemMap.itemTemplate.id <= 567) {
                                ServerNotify.gI().notify(player.name + " vừa nhặt được " + itemMap.itemTemplate.name + " tại " + mob.zone.map.mapName + " khu vực " + mob.zone.zoneId);
                            }
                            list.add(itemMap);
                        }

                if (mob.tempId == ConstMob.HIRUDEGARN) {
                    RandomCollection<Integer> rd = new RandomCollection<>();
                    rd.add(40, 568 /*, "DDijt nhau au au"*/);
                    rd.add(20, 861/*,, "DDijt nhau au au"*/);
                    rd.add(40, 17 /*, "DDijt nhau au au"*/);
                    for (int i = 0; i < 3; i++) {
                        int itemID = rd.next();
                        ItemMap itemMap = new ItemMap(mob.zone, itemID, 1, x + Util.nextInt(-50, 50), yEnd, player.id);
                        list.add(itemMap);
                    }
                    for (int i = 0; i < 10; i++) {
                        ItemReward gr = mobReward.goldRewards.get(Util.nextInt(0, goldSize - 1));
                        if (Util.isTrue(gr.ratio, gr.typeRatio)) {
                            ItemMap itemMap = new ItemMap(mob.zone, gr.tempId, 1, x + Util.nextInt(-50, 50), yEnd, player.id);
                            initQuantityGold(itemMap);
                            list.add(itemMap);
                        }
                    }
                }
                if (MapService.gI().isMapTuongLai(mapid) && player.itemTime.isUseMayDo && Util.isTrue(5, 100)) {
                            ItemMap itemMap = new ItemMap(mob.zone, 380, 1, x, yEnd, player.id);
                            list.add(itemMap);
                        }

                if (MapService.gI().isMapCold(player.zone.map) && player.itemTime.isMayDo && Util.isTrue(5, 100)) {
                            ItemMap itemMap = new ItemMap(mob.zone, 2008, 1, x, yEnd, player.id);
                            list.add(itemMap);
                        }

                if (MapService.gI().isMapCold(player.zone.map)) {// drop item cold
                    if (player.setClothes.godClothes && Util.isTrue(10, 170)) {
                            ItemMap itemMap = new ItemMap(mob.zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
                            list.add(itemMap);
                        }

                    if (Util.isTrue(1, 25222)) {
                        ItemMap itemMap = ArrietyDrop.DropItemReWardDoTL(player, 1, mob.location.x, yEnd);
                        ServerLog.logItemDrop(player.name, itemMap.itemTemplate.name);
                        list.add(itemMap);
                        ServerNotify.gI().notify(player.name + " vừa nhặt được " + itemMap.itemTemplate.name + " tại " + mob.zone.map.mapName + " khu vực " + mob.zone.zoneId);
                    }
                }
                if (player.setClothes.SetHuyDiet >= 5 && mapid == 155 && Util.isTrue(10, 100)) {
                            ItemMap itemMap = new ItemMap(mob.zone, Util.nextInt(ConstItem.MANH_AO, ConstItem.MANH_GANG_TAY), 1, x, yEnd, player.id);
                            list.add(itemMap);
                        }

                if (mapid == 208 && Util.isTrue(100, 100)) {
                        ItemMap itemMap = new ItemMap(mob.zone, 1098, 1, x, yEnd, player.id);
                        list.add(itemMap);
                    }

                if (mapid == 153) {// map bang
                    if (player.clan != null && player.zone != null) {
                        int numMenber = player.zone.getPlayersSameClan(player.clan.id).size();
                        if (numMenber >= 2 && Util.isTrue(1, 70)) {
                                player.clanMember.memberPoint++;
                                Service.getInstance().sendThongBao(player, "Bạn nhận được capsule bang hội");
                            }

                    }
                    if (Util.isTrue(1, 100)) {
                        ItemMap itemMap = new ItemMap(mob.zone, ConstItem.MANH_VO_BONG_TAI, 1, x, yEnd, player.id);
                        list.add(itemMap);
                    }
                }
            }
        }
        return list;
    }

    public static final int[] list_thuc_an = new int[]{663, 664, 665, 666, 667};

    private void initQuantityGold(ItemMap item) {
        switch (item.itemTemplate.id) {
        case 76 -> item.quantity = Util.nextInt(20000, 30000);
        case 188 -> item.quantity = Util.nextInt(20000, 30000);
        case 189 -> item.quantity = Util.nextInt(20000, 30000);
        case 190 -> item.quantity = Util.nextInt(20000, 30000);
        }
        Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.VANG);
        if (at != null && !at.isExpired()) {
            item.quantity += item.quantity * at.getValue() / 100;
        }
    }

    //chỉ số cơ bản: hp, ki, hồi phục, sđ, crit
    public void initBaseOptionClothes(int tempId, int type, List<ItemOption> list) {
        int[][] option_param = {{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}};
        switch (type) {
        case 0 -> {
            option_param[0][0] = 47; //giáp
            switch (tempId) {
            case 0 -> option_param[0][1] = 2;
            case 33 -> option_param[0][1] = 4;
            case 3 -> option_param[0][1] = 8;
            case 34 -> option_param[0][1] = 16;
            case 136 -> option_param[0][1] = 24;
            case 137 -> option_param[0][1] = 40;
            case 138 -> option_param[0][1] = 60;
            case 139 -> option_param[0][1] = 90;
            case 230 -> option_param[0][1] = 200;
            case 231 -> option_param[0][1] = 250;
            case 232 -> option_param[0][1] = 300;
            case 233 -> option_param[0][1] = 400;
            case 1 -> option_param[0][1] = 2;
            case 41 -> option_param[0][1] = 4;
            case 4 -> option_param[0][1] = 8;
            case 42 -> option_param[0][1] = 16;
            case 152 -> option_param[0][1] = 24;
            case 153 -> option_param[0][1] = 40;
            case 154 -> option_param[0][1] = 60;
            case 155 -> option_param[0][1] = 90;
            case 234 -> option_param[0][1] = 200;
            case 235 -> option_param[0][1] = 250;
            case 236 -> option_param[0][1] = 300;
            case 237 -> option_param[0][1] = 400;
            case 2 -> option_param[0][1] = 3;
            case 49 -> option_param[0][1] = 5;
            case 5 -> option_param[0][1] = 10;
            case 50 -> option_param[0][1] = 20;
            case 168 -> option_param[0][1] = 30;
            case 169 -> option_param[0][1] = 50;
            case 170 -> option_param[0][1] = 70;
            case 171 -> option_param[0][1] = 100;
            case 238 -> option_param[0][1] = 230;
            case 239 -> option_param[0][1] = 280;
            case 240 -> option_param[0][1] = 330;
            case 241 -> option_param[0][1] = 450;
            case 555 -> {
                option_param[2][0] = 21; //yêu cầu sức mạnh
                option_param[0][1] = 800;
                option_param[2][1] = 15;
            }
            case 557 -> {
                option_param[2][0] = 21; //yêu cầu sức mạnh
                option_param[0][1] = 800;
                option_param[2][1] = 15;
            }
            case 559 -> {
                option_param[2][0] = 21; //yêu cầu sức mạnh
                option_param[0][1] = 800;
                option_param[2][1] = 15;
            }
            }
        }
        case 1 -> {
            option_param[0][0] = 6; //hp
            option_param[1][0] = 27; //hp hồi/30s
            switch (tempId) {
            case 6 -> option_param[0][1] = 30;
            case 35 -> {
                option_param[0][1] = 150;
                option_param[1][1] = 12;
            }
            case 9 -> {
                option_param[0][1] = 300;
                option_param[1][1] = 40;
            }
            case 36 -> {
                option_param[0][1] = 600;
                option_param[1][1] = 120;
            }
            case 140 -> {
                option_param[0][1] = 1400;
                option_param[1][1] = 280;
            }
            case 141 -> {
                option_param[0][1] = 3000;
                option_param[1][1] = 600;
            }
            case 142 -> {
                option_param[0][1] = 6000;
                option_param[1][1] = 1200;
            }
            case 143 -> {
                option_param[0][1] = 10000;
                option_param[1][1] = 2000;
            }
            case 242 -> {
                option_param[0][1] = 14000;
                option_param[1][1] = 2500;
            }
            case 243 -> {
                option_param[0][1] = 18000;
                option_param[1][1] = 3000;
            }
            case 244 -> {
                option_param[0][1] = 22000;
                option_param[1][1] = 3500;
            }
            case 245 -> {
                option_param[0][1] = 26000;
                option_param[1][1] = 4000;
            }
            case 7 -> option_param[0][1] = 20;
            case 43 -> {
                option_param[0][1] = 25;
                option_param[1][1] = 10;
            }
            case 10 -> {
                option_param[0][1] = 120;
                option_param[1][1] = 28;
            }
            case 44 -> {
                option_param[0][1] = 250;
                option_param[1][1] = 100;
            }
            case 156 -> {
                option_param[0][1] = 600;
                option_param[1][1] = 240;
            }
            case 157 -> {
                option_param[0][1] = 1200;
                option_param[1][1] = 480;
            }
            case 158 -> {
                option_param[0][1] = 2400;
                option_param[1][1] = 960;
            }
            case 159 -> {
                option_param[0][1] = 4800;
                option_param[1][1] = 1800;
            }
            case 246 -> {
                option_param[0][1] = 13000;
                option_param[1][1] = 2200;
            }
            case 247 -> {
                option_param[0][1] = 17000;
                option_param[1][1] = 2700;
            }
            case 248 -> {
                option_param[0][1] = 21000;
                option_param[1][1] = 3200;
            }
            case 249 -> {
                option_param[0][1] = 25000;
                option_param[1][1] = 3700;
            }
            case 8 -> option_param[0][1] = 20;
            case 51 -> {
                option_param[0][1] = 20;
                option_param[1][1] = 8;
            }
            case 11 -> {
                option_param[0][1] = 100;
                option_param[1][1] = 20;
            }
            case 52 -> {
                option_param[0][1] = 200;
                option_param[1][1] = 80;
            }
            case 172 -> {
                option_param[0][1] = 500;
                option_param[1][1] = 200;
            }
            case 173 -> {
                option_param[0][1] = 1000;
                option_param[1][1] = 400;
            }
            case 174 -> {
                option_param[0][1] = 2000;
                option_param[1][1] = 800;
            }
            case 175 -> {
                option_param[0][1] = 4000;
                option_param[1][1] = 1600;
            }
            case 250 -> {
                option_param[0][1] = 12000;
                option_param[1][1] = 2100;
            }
            case 251 -> {
                option_param[0][1] = 16000;
                option_param[1][1] = 2600;
            }
            case 252 -> {
                option_param[0][1] = 20000;
                option_param[1][1] = 3100;
            }
            case 253 -> {
                option_param[0][1] = 24000;
                option_param[1][1] = 3600;
            }
            case 556 -> {
                option_param[0][0] = 22; //hp
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 52;
                option_param[1][1] = 10000;
                option_param[2][1] = 15;
            }
            case 558 -> {
                option_param[0][0] = 22; //hp
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 50;
                option_param[1][1] = 10000;
                option_param[2][1] = 15;
            }
            case 560 -> {
                option_param[0][0] = 22; //hp
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 48;
                option_param[1][1] = 10000;
                option_param[2][1] = 15;
            }
            }
        }
        case 2 -> {
            option_param[0][0] = 0; //sđ
            switch (tempId) {
            case 21:
                option_param[0][1] = 4;
                break;
            case 24:
                option_param[0][1] = 7;
                break;
            case 37:
                option_param[0][1] = 14;
                break;
            case 38:
                option_param[0][1] = 28;
                break;
            case 144:
                option_param[0][1] = 55;
                break;
            case 145:
                option_param[0][1] = 110;
                break;
            case 146:
                option_param[0][1] = 220;
                break;
            case 147:
                option_param[0][1] = 530;
                break;
            case 254:
                option_param[0][1] = 680;
                break;
            case 255:
                option_param[0][1] = 1000;
                break;
            case 256:
                option_param[0][1] = 1500;
                break;
            case 257:
                option_param[0][1] = 2200;
                break;
            case 22:
                option_param[0][1] = 3;
                break;
            case 46:
                option_param[0][1] = 6;
                break;
            case 25:
                option_param[0][1] = 12;
                break;
            case 45:
                option_param[0][1] = 24;
                break;
            case 160:
                option_param[0][1] = 50;
                break;
            case 161:
                option_param[0][1] = 100;
                break;
            case 162:
                option_param[0][1] = 200;
                break;
            case 163:
                option_param[0][1] = 500;
                break;
            case 258:
                option_param[0][1] = 630;
                break;
            case 259:
                option_param[0][1] = 950;
                break;
            case 260:
                option_param[0][1] = 1450;
                break;
            case 261:
                option_param[0][1] = 2150;
                break;
            case 23:
                option_param[0][1] = 5;
                break;
            case 53:
                option_param[0][1] = 8;
                break;
            case 26:
                option_param[0][1] = 16;
                break;
            case 54:
                option_param[0][1] = 32;
                break;
            case 176:
                option_param[0][1] = 60;
                break;
            case 177:
                option_param[0][1] = 120;
                break;
            case 178:
                option_param[0][1] = 240;
                break;
            case 179:
                option_param[0][1] = 560;
                break;
            case 262:
                option_param[0][1] = 700;
                break;
            case 263:
                option_param[0][1] = 1050;
                break;
            case 264:
                option_param[0][1] = 1550;
                break;
            case 265:
                option_param[0][1] = 2250;
                break;
            case 562: //găng thần trái đất
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 3700;
                option_param[2][1] = 17;
                break;
            case 564: //găng thần namếc
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 3500;
                option_param[2][1] = 17;
                break;
            case 566: //găng thần xayda
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 3800;
                option_param[2][1] = 17;
                break;
            }
        }
        case 3 -> {
            option_param[0][0] = 7; //ki
            option_param[1][0] = 28; //ki hồi /30s
            switch (tempId) {
            case 27 -> option_param[0][1] = 10;
            case 30 -> {
                option_param[0][1] = 25;
                option_param[1][1] = 5;
            }
            case 39 -> {
                option_param[0][1] = 120;
                option_param[1][1] = 24;
            }
            case 40 -> {
                option_param[0][1] = 250;
                option_param[1][1] = 50;
            }
            case 148 -> {
                option_param[0][1] = 500;
                option_param[1][1] = 100;
            }
            case 149 -> {
                option_param[0][1] = 1200;
                option_param[1][1] = 240;
            }
            case 150 -> {
                option_param[0][1] = 2400;
                option_param[1][1] = 480;
            }
            case 151 -> {
                option_param[0][1] = 5000;
                option_param[1][1] = 1000;
            }
            case 266 -> {
                option_param[0][1] = 9000;
                option_param[1][1] = 1500;
            }
            case 267 -> {
                option_param[0][1] = 14000;
                option_param[1][1] = 2000;
            }
            case 268 -> {
                option_param[0][1] = 19000;
                option_param[1][1] = 2500;
            }
            case 269 -> {
                option_param[0][1] = 24000;
                option_param[1][1] = 3000;
            }
            case 28 -> option_param[0][1] = 15;
            case 47 -> {
                option_param[0][1] = 30;
                option_param[1][1] = 6;
            }
            case 31 -> {
                option_param[0][1] = 150;
                option_param[1][1] = 30;
            }
            case 48 -> {
                option_param[0][1] = 300;
                option_param[1][1] = 60;
            }
            case 164 -> {
                option_param[0][1] = 600;
                option_param[1][1] = 120;
            }
            case 165 -> {
                option_param[0][1] = 1500;
                option_param[1][1] = 300;
            }
            case 166 -> {
                option_param[0][1] = 3000;
                option_param[1][1] = 600;
            }
            case 167 -> {
                option_param[0][1] = 6000;
                option_param[1][1] = 1200;
            }
            case 270 -> {
                option_param[0][1] = 10000;
                option_param[1][1] = 1700;
            }
            case 271 -> {
                option_param[0][1] = 15000;
                option_param[1][1] = 2200;
            }
            case 272 -> {
                option_param[0][1] = 20000;
                option_param[1][1] = 2700;
            }
            case 273 -> {
                option_param[0][1] = 25000;
                option_param[1][1] = 3200;
            }
            case 29 -> option_param[0][1] = 10;
            case 55 -> {
                option_param[0][1] = 20;
                option_param[1][1] = 4;
            }
            case 32 -> {
                option_param[0][1] = 100;
                option_param[1][1] = 20;
            }
            case 56 -> {
                option_param[0][1] = 200;
                option_param[1][1] = 40;
            }
            case 180 -> {
                option_param[0][1] = 400;
                option_param[1][1] = 80;
            }
            case 181 -> {
                option_param[0][1] = 1000;
                option_param[1][1] = 200;
            }
            case 182 -> {
                option_param[0][1] = 2000;
                option_param[1][1] = 400;
            }
            case 183 -> {
                option_param[0][1] = 4000;
                option_param[1][1] = 800;
            }
            case 274 -> {
                option_param[0][1] = 8000;
                option_param[1][1] = 1300;
            }
            case 275 -> {
                option_param[0][1] = 13000;
                option_param[1][1] = 1800;
            }
            case 276 -> {
                option_param[0][1] = 18000;
                option_param[1][1] = 2300;
            }
            case 277 -> {
                option_param[0][1] = 23000;
                option_param[1][1] = 2800;
            }
            case 563 -> {
                option_param[0][0] = 23;
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 48;
                option_param[1][1] = 10000;
                option_param[2][1] = 14;
            }
            case 565 -> {
                option_param[0][0] = 23;
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 48;
                option_param[1][1] = 10000;
                option_param[2][1] = 14;
            }
            case 567 -> {
                option_param[0][0] = 23;
                option_param[2][0] = 21; //yêu cầu sức mạnh

                option_param[0][1] = 46;
                option_param[1][1] = 10000;
                option_param[2][1] = 14;
            }
            }
        }
        case 4 -> {
            option_param[0][0] = 14; //crit
            switch (tempId) {
            case 12 -> option_param[0][1] = 1;
            case 57 -> option_param[0][1] = 2;
            case 58 -> option_param[0][1] = 3;
            case 59 -> option_param[0][1] = 4;
            case 184 -> option_param[0][1] = 5;
            case 185 -> option_param[0][1] = 6;
            case 186 -> option_param[0][1] = 7;
            case 187 -> option_param[0][1] = 8;
            case 278 -> option_param[0][1] = 9;
            case 279 -> option_param[0][1] = 10;
            case 280 -> option_param[0][1] = 11;
            case 281 -> option_param[0][1] = 12;
            case 561 -> {
                option_param[2][0] = 21;
                option_param[0][1] = 15;
                option_param[2][1] = 18;
            }
            }
        }
        }
        for (int[] ints : option_param) {
            if (ints[0] != -1 && ints[1] != -1) {
                list.add(new ItemOption(ints[0], (ints[1] + Util.nextInt(-(ints[1] * 10 / 100), ints[1] * 10 / 100))));
            }
        }
    }

    private void initBaseOptionSaoPhaLe(ItemMap item) {
        int optionId = -1;
        switch (item.itemTemplate.id) {
            case 441: //hút máu
                optionId = 95;
                break;
            case 442: //hút ki
                optionId = 96;
                break;
            case 443: //phản sát thương
                optionId = 97;
                break;
            case 444:
                break;
            case 445:
                break;
            case 446: //vàng
                optionId = 100;
                break;
            case 447: //tnsm
                optionId = 101;
                break;
        }
        item.options.add(new ItemOption(optionId, 5));
    }

    public void initBaseOptionSaoPhaLe(Item item) {
        int optionId = -1;
        int param = 5;
        switch (item.template.id) {
            case 441: //hút máu
                optionId = 95;
                break;
            case 442: //hút ki
                optionId = 96;
                break;
            case 443: //phản sát thương
                optionId = 97;
                break;
            case 444:
                param = 3;
                optionId = 98;
                break;
            case 445:
                param = 3;
                optionId = 99;
                break;
            case 446: //vàng
                optionId = 100;
                break;
            case 447: //tnsm
                optionId = 101;
                break;
        }
        if (optionId != -1) {
            item.itemOptions.add(new ItemOption(optionId, param));
        }
    }

    //sao pha lê
    public void initStarOption(ItemMap item, RatioStar[] ratioStars) {
        RatioStar ratioStar = ratioStars[Util.nextInt(0, ratioStars.length - 1)];
        if (Util.isTrue(ratioStar.ratio, ratioStar.typeRatio)) {
            item.options.add(new ItemOption(107, ratioStar.numStar));
        }
    }

    public void initStarOption(Item item, RatioStar[] ratioStars) {
        RatioStar ratioStar = ratioStars[Util.nextInt(0, ratioStars.length - 1)];
        if (Util.isTrue(ratioStar.ratio, ratioStar.typeRatio)) {
            item.itemOptions.add(new ItemOption(107, ratioStar.numStar));
        }
    }

    private void initNotTradeOption(ItemMap item) {
        switch (item.itemTemplate.id) {
            case 2009:
                item.options.add(new ItemOption(30, 0));
                break;

        }
    }
    //vật phẩm ký gửi

    //set kích hoạt
    public void initActivationOption(int gender, int type, List<ItemOption> list) {
        if (type <= 4) {
            int[] idOption = ACTIVATION_SET[gender][Util.nextInt(0, 2)];
            list.add(new ItemOption(idOption[0], 1)); //tên set
            list.add(new ItemOption(idOption[1], 1)); //hiệu ứng set
            list.add(new ItemOption(30, 7)); //không thể giao dịch
        }
    }

    public static Item randomCS_DHD(int itemId, int gender) {
        Item it = ItemService.gI().createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int rdtl = 561;
        if (ao.contains(itemId)) {
            it.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(150) + 700))); // áo từ 1800-2800 giáp
        }
        if (quan.contains(itemId)) {
            it.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(15) + 45))); // hp 85-100k
        }
        if (gang.contains(itemId)) {
            it.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(500) + 4500))); // 8500-10000
        }
        if (giay.contains(itemId)) {
            it.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(15) + 45))); // ki 80-90k
        }
        if (rdtl == itemId) {
            it.itemOptions.add(new ItemOption(14, new Random().nextInt(2) + 13)); //chí mạng 17-19%
        }
        if (Util.isTrue(5, 10)) {
            it.itemOptions.add(new ItemOption(86, 0));
        } else {
            it.itemOptions.add(new ItemOption(87, 0));
        }
        it.itemOptions.add(new ItemOption(21, 17));
//        it.itemOptions.add(new ItemOption(30, 1));
        return it;
    }

    //-------------------------------------------------------------------------- Item reward lucky round
    public List<Item> getListItemLuckyRound(Player player, int num) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ItemLuckyRound item = Manager.LUCKY_ROUND_REWARDS.next();
            if (item != null && (item.temp.gender == player.gender || item.temp.gender > 2)) {
                Item it = ItemService.gI().createNewItem(item.temp.id);
                for (ItemOptionLuckyRound io : item.itemOptions) {
                    int param = 0;
                    if (io.param2 != -1) {
                        param = Util.nextInt(io.param1, io.param2);
                    } else {
                        param = io.param1;
                    }
                    it.itemOptions.add(new ItemOption(io.itemOption.optionTemplate.id, param));
                }
                list.add(it);
            } else {
                Item it = ItemService.gI().createNewItem((short) 189, Util.nextInt(5, 50) * 1000);
                list.add(it);
            }
        }
        return list;
    }

    public static class RatioStar {

        public byte numStar;
        public int ratio;
        public int typeRatio;

        public RatioStar(byte numStar, int ratio, int typeRatio) {
            this.numStar = numStar;
            this.ratio = ratio;
            this.typeRatio = typeRatio;
        }
    }

    public void rewardFirstTimeLoginPerDay(Player player) {
        LocalDateTime now = LocalDateTime.now();
        if (Util.compareDay(now, player.firstTimeLogin) || player.canGetFirstTimeLogin) {
            if (player.getSession().actived) {
                Item item = ItemService.gI().createNewItem((short) Util.nextInt(2045, 2051));
                item.quantity = 1;
                item.itemOptions.add(new ItemOption(74, 0));
                item.itemOptions.add(new ItemOption(30, 0));
                InventoryService.gI().addItemBag(player, item, 1);
                Service.getInstance().sendThongBao(player, "Quà đăng nhập hàng ngày: \nBạn nhận được " + item.template.name + " số lượng : " + item.quantity);
                ServerLog.logRewardDay(player.name, item.template.name, item.quantity);
                player.firstTimeLogin = now;
            } else {
                Service.getInstance().sendThongBao(player, "Do la 1 giac mo");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Hãy cố gắng online 1 ngày sẽ nhận được phần quà nhé <3");
        }
    }
}
