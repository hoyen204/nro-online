package com.nro.nro_online.models.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item {

    private static final ItemOption OPTION_NULL = new ItemOption(73, 0);
    private static final Map<Integer, int[]> GOD_CLOTHES = new HashMap<>() {{
        put(0, new int[]{555, 556, 562, 563}); // Trái Đất
        put(1, new int[]{557, 558, 564, 565}); // Namek
        put(2, new int[]{559, 560, 566, 567}); // Xayda
    }};

    public ItemTemplate template;
    public String info;
    public String content;
    public int quantity;
    public List<ItemOption> itemOptions;
    public long createTime;

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public Item getClone(){
        Item it = new Item();
        it.template = this.template;
        it.info = this.info;
        it.content = this.content;
        it.quantity = this.quantity;
        it.createTime = this.createTime;
        for (ItemOption io : this.itemOptions) {
            it.itemOptions.add(new ItemOption(io));
        }
        return it;
    }

    public boolean isNotNullItem() {
        return template != null;
    }

    public String getInfo() {
        StringBuilder strInfo = new StringBuilder();
        for (ItemOption opt : itemOptions) {
            strInfo.append(opt.getOptionString());
        }
        return strInfo.toString();
    }

    public String getInfoItem() {
        StringBuilder strInfo = new StringBuilder("|1|").append(template.name).append("\n|0|");
        for (ItemOption opt : itemOptions) {
            strInfo.append(opt.getOptionString()).append("\n");
        }
        return strInfo.append("|2|").append(template.description).toString();
    }

    public boolean hasOptionRange(int minId, int maxId) {
        for (ItemOption opt : itemOptions) {
            int id = opt.optionTemplate.id;
            if (id >= minId && id <= maxId) return true;
        }
        return false;
    }

    public boolean isSKHThuong() {
        return hasOptionRange(210, 218);
    }

    public boolean isSKHVip() {
        return hasOptionRange(127, 144);
    }

    public boolean isItemKyGui() {
        return hasOptionRange(86, 87);
    }

    public int checkGodClothes(Item it) {
        if (it.template.type == 4) return 561; // Găng tay fix cứng
        int gender = it.template.gender;
        int type = it.template.type;
        int[] items = GOD_CLOTHES.get(gender);
        return (items != null && type >= 0 && type < items.length) ? items[type] : -1;
    }

    public List<ItemOption> getDisplayOptions() {
        if (itemOptions.isEmpty()) return Collections.singletonList(OPTION_NULL);
        List<ItemOption> list = new ArrayList<>(itemOptions.size());
        for (ItemOption opt : itemOptions) {
            list.add(opt.format());
        }
        return list;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + template.strRequire + " trở lên";
    }

    public boolean canConsign() {
        return isItemKyGui(); // Tái sử dụng logic
    }

    public boolean isItemFlagBagPet() {
        return template.id == 1197 || template.id == 2053;
    }

    public boolean isDestroy() {
        return template.id >= 650 && template.id <= 662;
    }

    public void dispose() {
        template = null;
        info = null;
        content = null;
        if (itemOptions != null) {
            itemOptions.forEach(ItemOption::dispose);
            itemOptions.clear();
            itemOptions = null;
        }
    }

    public short getId() {
        return template.id;
    }

    public byte getType() {
        return template.type;
    }

    public String getName() {
        return template.name;
    }

    public boolean isInRange(int minId, int maxId) {
        return template.id >= minId && template.id <= maxId;
    }

    public boolean isAngelFrag() {
        return isInRange(1066, 1070);
    }

    public boolean isAngelUpgradeStone() {
        return isInRange(1074, 1078);
    }

    public boolean isLuckyStone() {
        return isInRange(1079, 1083);
    }

    public boolean isVipFormula() {
        return isInRange(1084, 1086);
    }

    public boolean isFormula() {
        return isInRange(1071, 1073);
    }

    public byte typeIdManh() {
        if (!isAngelFrag()) return -1;
        return switch (template.id) {
            case 1066 -> 0;
            case 1067 -> 1;
            case 1070 -> 2;
            case 1068 -> 3;
            case 1069 -> 4;
            default -> -1;
        };
    }
}