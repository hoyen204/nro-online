package com.nro.nro_online.models.map.dungeon;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.nro.nro_online.consts.ConstItem;
import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.lib.RandomCollection;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.cdrd.CBoss;
import com.nro.nro_online.models.boss.cdrd.Cadich;
import com.nro.nro_online.models.boss.cdrd.Nadic;
import com.nro.nro_online.models.boss.cdrd.Saibamen;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.dungeon.zones.ZSnakeRoad;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnakeRoad extends Dungeon {

    private final List<CBoss> bosses = new CopyOnWriteArrayList<>();

    public SnakeRoad(int level) {
        super(level);
        setType(Dungeon.SNAKE_ROAD);
        setName("Con đường rắn độc");
        setTitle("Con đường rắn độc");
        setCountDown(30 * 60);
        initBoss();
    }

    @Override
    protected void init() {
        int[] maps = {
                ConstMap.CON_DUONG_RAN_DOC, ConstMap.CON_DUONG_RAN_DOC_142,
                ConstMap.CON_DUONG_RAN_DOC_143, ConstMap.THAN_DIEN,
                ConstMap.THAP_KARIN, ConstMap.RUNG_KARIN, ConstMap.HOANG_MAC
        };
        for (int mapId : maps) {
            addZone(new ZSnakeRoad(MapService.gI().getMapById(mapId), this));
        }
    }

    public void addBoss(CBoss boss) {
        bosses.add(boss);
    }

    public void removeBoss(CBoss boss) {
        bosses.remove(boss);
    }

    public CBoss getBoss(int index) {
        return (index >= 0 && index < bosses.size()) ? bosses.get(index) : null;
    }

    private void spawnBoss(int num, String name, short[] outfit, int[][] skills, int baseDame, int baseHp, int x, int y,
            Class<? extends CBoss> bossClass) {
        BossData data = BossData.builder()
                .name(name)
                .gender(ConstPlayer.XAYDA)
                .typeDame(Boss.DAME_NORMAL)
                .typeHp(Boss.HP_NORMAL)
                .dame(baseDame * level * level)
                .hp(new int[][] { { baseHp * level * level } })
                .outfit(outfit)
                .skillTemp(skills)
                .secondsRest(BossData._0_GIAY)
                .build();
        data.joinMapIdle = true;
        try {
            CBoss boss = bossClass.getConstructor(int.class, short.class, short.class, Dungeon.class, BossData.class)
                    .newInstance(num, (short) x, (short) y, this, data);
            addBoss(boss);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initBoss() {
        int num = -999;
        for (int i = 0; i < 5; i++) {
            spawnBoss(num++, "Số " + (i + 1), new short[] { 642, 643, 644 },
                    new int[][] { { Skill.DRAGON, 1, 100 }, { Skill.DRAGON, 2, 200 }, { Skill.DRAGON, 3, 300 },
                            { Skill.DRAGON, 7, 700 }, { Skill.TU_SAT, 1, 100 } },
                    1000, 20000, 400 + (24 * i), 336, Saibamen.class);
        }
        spawnBoss(num++, "Nađíc", new short[] { 648, 649, 650 },
                new int[][] { { Skill.GALICK, 3, 300 }, { Skill.GALICK, 7, 700 }, { Skill.ANTOMIC, 5, 500 } },
                100, 1000000, 520, 336, Nadic.class);
        spawnBoss(num++, "Cađích", new short[] { 645, 646, 647 },
                new int[][] { { Skill.GALICK, 7, 700 }, { Skill.ANTOMIC, 7, 1000 },
                        { Skill.TAI_TAO_NANG_LUONG, 1, 20000 }, { Skill.BIEN_KHI, 7, 60000 } },
                150, 1500000, 532, 336, Cadich.class);
    }

    @Override
    public void update() {
        bosses.forEach(CBoss::update);
        if (bosses.stream().allMatch(Boss::isDie)) {
            finish();
        }
        super.update();
    }

    @Override
    public void join(Player player) {
        player.setInteractWithKarin(false);
        ((ZSnakeRoad) find(ConstMap.CON_DUONG_RAN_DOC_143)).enter(player, 1110, 336);
    }

    @Override
    public void finish() {
        if (!finish) {
            finish = true;
            setTime(60);
            sendNotification("Trận chiến với người Xayda sẽ kết thúc sau 60 giây nữa");
            dropRewards();
        }
    }

    private void dropRewards() {
        ZSnakeRoad r = (ZSnakeRoad) find(ConstMap.HOANG_MAC);
        RandomCollection<Integer> rc = new RandomCollection<>();
        rc.add(300, ConstItem.VANG);
        rc.add(level * 2, ConstItem.HONG_NGOC);
        int quantity = Math.max(level / 10, 3);

        for (int i = 0; i < quantity; i++) {
            Service.getInstance().dropItemMap(r, new ItemMap(r, rc.next(), 1, 350 + (i * 10), 312, -1));
        }

        if (level >= 80) {
            for (int i = 0; i < Util.nextInt(2, 6); i++) {
                Service.getInstance().dropItemMap(r,
                        new ItemMap(r, ConstItem.NGOC_RONG_3_SAO, Util.nextInt(1, 5), 250 + (i * 20), 312, -1));
            }
        }

        if (level >= 110) {
            int[] specialItems = { 2040, 2012, 2040 };
            for (int i = 0; i < specialItems.length; i++) {
                Service.getInstance().dropItemMap(r, new ItemMap(r, specialItems[i], 1, 530 + i, 312, -1));
            }
        }
    }
}
