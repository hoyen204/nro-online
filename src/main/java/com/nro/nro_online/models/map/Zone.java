package com.nro.nro_online.models.map;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.nro.nro_online.consts.ConstMap;
import com.nro.nro_online.consts.ConstMob;
import com.nro.nro_online.consts.ConstPet;
import com.nro.nro_online.consts.ConstTask;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.list_boss.WhisTop;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.war.NamekBallWar;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.npc.NpcManager;
import com.nro.nro_online.models.player.Pet;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.power.CaptionManager;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.EffectSkillService;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemMapService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.FileIO;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

import lombok.Getter;
import lombok.Setter;

public class Zone {
    public static final byte PLAYERS_TIEU_CHUAN_TRONG_MAP = 7;
    public final List<Mob> mobs = new ArrayList<>();
    public final List<Player> humanoids = new ArrayList<>();
    public final List<Player> notBosses = new ArrayList<>();
    public final List<Player> players = new ArrayList<>();
    public final List<Player> bosses = new ArrayList<>();
    public final List<Player> pets = new ArrayList<>();
    public final List<Player> minipets = new ArrayList<>();
    public final List<ItemMap> items = new ArrayList<>();
    public int countItemAppeaerd;
    public Map map;
    public int zoneId;
    public int maxPlayer;
    public long lastTimeDropBlackBall;
    public boolean finishBlackBallWar;
    public byte percentMabuEgg;
    public boolean initBossMabu;
    public boolean finishMabuWar;
    public List<TrapMap> trapMaps = new ArrayList<>();
    public byte effDragon = -1;
    @Setter
    @Getter
    private Player referee;

    public Zone(Map map, int zoneId, int maxPlayer) {
        this.map = map;
        this.zoneId = zoneId;
        this.maxPlayer = maxPlayer;
    }

    public short[] getXYMabuMap() {
        return Manager.POINT_MABU_MAP[players.stream()
                .noneMatch(
                        pl -> pl != null && pl.effectSkill.isHoldMabu && pl.location.x == Manager.POINT_MABU_MAP[0][0]
                                && pl.location.y == Manager.POINT_MABU_MAP[0][1])
                                        ? 0
                                        : 1];
    }

    public boolean isFullPlayer() {
        return players.size() >= maxPlayer;
    }

    public void addMob(Mob mob) {
        mob.id = mobs.size();
        mobs.add(mob);
    }

    private void updateMob() {
        mobs.forEach(Mob::update);
    }

    public long getTotalHP() {
        return mobs.stream().filter(m -> !m.isDie()).mapToLong(m -> m.point.hp).sum()
                + players.stream().filter(p -> p.nPoint != null && !p.isDie()).mapToLong(p -> p.nPoint.hp).sum()
                + pets.stream().filter(p -> p.nPoint != null && !p.isDie()).mapToLong(p -> p.nPoint.hp).sum();
    }

    private void updatePlayer() {
        notBosses.stream().filter(p -> !p.isPet && !p.isMiniPet).forEach(Player::update);
    }

    public boolean findPlayerHaveBallTranhDoat(int id) {
        return players.stream()
                .anyMatch(pl -> pl != null && pl.isHoldNamecBallTranhDoat && pl.tempIdNamecBallHoldTranhDoat == id);
    }

    private void updateReferee() {
        referee.update();
    }

    private void updateItem() {
        items.forEach(ItemMap::update);
    }

    public void update() {
        updateMob();
        updatePlayer();
        updateItem();
        if (map.mapId == ConstMap.DAI_HOI_VO_THUAT)
            updateReferee();
    }

    public int getNumOfPlayers() {
        return players.size();
    }

    public int getNumOfBosses() {
        return bosses.size();
    }

    public boolean isBossCanJoin(Boss boss) {
        return bosses.stream().noneMatch(b -> b.id == boss.id);
    }

    public List<Player> getNotBosses() {
        return notBosses;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getHumanoids() {
        return humanoids;
    }

    public List<Player> getBosses() {
        return bosses;
    }

    public void addPlayer(Player player) {
        if (player == null)
            return;
        if (!humanoids.contains(player))
            humanoids.add(player);
        if (!player.isBoss) {
            if (!notBosses.contains(player))
                notBosses.add(player);
            if (player.isPet)
                pets.add(player);
            else if (player.isMiniPet)
                minipets.add(player);
            else if (!players.contains(player))
                players.add(player);
        } else {
            bosses.add(player);
        }
    }

    public void removePlayer(Player player) {
        if (player == null)
            return;
        humanoids.remove(player);
        if (!player.isBoss) {
            notBosses.remove(player);
            if (player.isPet)
                pets.remove(player);
            else if (player.isMiniPet)
                minipets.remove(player);
            else
                players.remove(player);
        } else {
            bosses.remove(player);
        }
    }

    public ItemMap getItemMapByItemMapId(int itemId) {
        return items.stream().filter(item -> item.itemMapId == itemId).findFirst().orElse(null);
    }

    public ItemMap getItemMapByTempId(int tempId) {
        return items.stream().filter(item -> item.itemTemplate.id == tempId).findFirst().orElse(null);
    }

    public List<ItemMap> getItemMapsForPlayer(Player player) {
        return items.stream()
                .filter(item -> !(item instanceof NamekBall && ((NamekBall) item).isHolding()))
                .filter(item -> item.itemTemplate != null)
                .filter(item -> item.itemTemplate.id != 78 || TaskService.gI().getIdTask(player) == ConstTask.TASK_3_1)
                .filter(item -> item.itemTemplate.id != 74 || TaskService.gI().getIdTask(player) >= ConstTask.TASK_3_0)
                .collect(Collectors.toList());
    }

    public List<ItemMap> getSatellites() {
        return items.stream().filter(i -> i instanceof Satellite).collect(Collectors.toList());
    }

    public Player getPlayerInMap(int idPlayer) {
        return humanoids.stream().filter(pl -> pl != null && pl.id == idPlayer).findFirst().orElse(null);
    }

    public List<Player> getPlayersSameClan(int clanID) {
        return players.stream().filter(pl -> pl.clan != null && pl.clan.id == clanID).collect(Collectors.toList());
    }

    public void pickItem(Player player, int itemMapId) {
        ItemMap itemMap = getItemMapByItemMapId(itemMapId);
        if (itemMap instanceof Satellite || itemMap == null || itemMap.isPickedUp) {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            return;
        }

        if (itemMap.playerId != player.id && itemMap.playerId != -1) {
            Service.getInstance().sendThongBao(player, "Không thể nhặt vật phẩm của người khác");
            return;
        }

        if (itemMap.itemTemplate.id == 648) {
            Item item = InventoryService.gI().findItemBagByTemp(player, 649);
            if (item == null) {
                Service.getInstance().sendThongBao(player, "Bạn không có Tất,vớ giáng sinh để đựng quà.");
                return;
            }
            itemMap.options.add(new ItemOption(74, 0));
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            InventoryService.gI().sendItemBags(player);
        }

        if (itemMap instanceof NamekBall) {
            NamekBallWar.gI().pickBall(player, (NamekBall) itemMap);
            return;
        }

        Item item = ItemService.gI().createItemFromItemMap(itemMap);
        int maxQuantity = ItemService.gI().isUnLimitQuantity(item.template.id) ? 99999 : 0;
        boolean picked = InventoryService.gI().addItemBag(player, item, maxQuantity);

        if (!picked) {
            if (!ItemMapService.gI().isBlackBall(item.template.id)) {
                Service.getInstance().sendThongBao(player, "Hành trang không còn chỗ trống");
            }
            return;
        }

        if (itemMap.itemTemplate.id != 74)
            itemMap.isPickedUp = true;
        int itemType = item.template.type;
        try (Message msg = new Message(-20)) {
            msg.writer().writeShort(itemMapId);
            switch (itemType) {
                case 9:
                case 10:
                case 34:
                    msg.writer().writeUTF("");
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    break;
                default:
                    switch (item.template.id) {
                        case 73:
                            msg.writer().writeUTF("");
                            msg.writer().writeShort(item.quantity);
                            player.sendMessage(msg);
                            break;
                        case 74:
                            msg.writer().writeUTF("Bạn vừa ăn " + item.template.name);
                            break;
                        case 78:
                            msg.writer().writeUTF("Wow, một cậu bé dễ thương!");
                            msg.writer().writeShort(item.quantity);
                            player.sendMessage(msg);
                            break;
                        case 516:
                            player.nPoint.setFullHpMp();
                            PlayerService.gI().sendInfoHpMp(player);
                            Service.getInstance().sendThongBao(player, "Bạn vừa ăn " + itemMap.itemTemplate.name);
                            break;
                        default:
                            msg.writer().writeUTF("Bạn nhặt được " + item.template.name);
                            InventoryService.gI().sendItemBags(player);
                            break;
                    }
            }
            msg.writer().writeShort(item.quantity);
            player.sendMessage(msg);
            Service.getInstance().sendToAntherMePickItem(player, itemMapId);
            int mapID = this.map.mapId;
            if (!(mapID >= 21 && mapID <= 23 && itemMap.itemTemplate.id == 74
                    || mapID >= 42 && mapID <= 44 && itemMap.itemTemplate.id == 78)) {
                items.remove(itemMap);
            }
        } catch (Exception e) {
            Log.error(Zone.class, e);
        }

        TaskService.gI().checkDoneTaskPickItem(player, itemMap);
        TaskService.gI().checkDoneSideTaskPickItem(player, itemMap);
    }

    public void addItem(ItemMap itemMap) {
        items.add(itemMap);
    }

    public void removeItemMap(ItemMap itemMap) {
        items.remove(itemMap);
    }

    public Player getRandomPlayerInMap() {
        return notBosses.isEmpty() ? notBosses.get(Util.nextInt(0, notBosses.size() - 1)) : null;
    }

    public Player getRandomPlayerInMap(List<Player> players) {
        List<Player> playerList = players.stream()
                .filter(p -> p != null && p.zone != null && p.zone.map.mapId == this.map.mapId
                        && p.zone.zoneId == this.zoneId)
                .distinct()
                .collect(Collectors.toList());
        return playerList.isEmpty() ? null : playerList.get(Util.nextInt(0, playerList.size() - 1));
    }

    public Player getPlayerInMap(long id) {
        return notBosses.stream().filter(pl -> pl != null && Math.abs(pl.id) == id).findFirst().orElse(null);
    }

    public void loadMeToAnother(Player player) {
        try {
            if (player.zone == null)
                return;
            if (map.isMapOffline) {
                if (player.isPet && this.equals(((Pet) player).master.zone)) {
                    infoPlayer(((Pet) player).master, player);
                }
            } else {
                players.stream().filter(pl -> !player.equals(pl)).forEach(pl -> infoPlayer(pl, player));
            }
        } catch (Exception e) {
            Log.error(MapService.class, e);
        }
    }

    public void loadAnotherToMe(Player player) {
        try {
            if (map.isMapOffline) {
                ReentrantLock lock = new ReentrantLock();
                lock.lock();
                try {
                    humanoids.stream().filter(pl -> pl != null && CanSeeInMapOffline(player, pl))
                            .forEach(pl -> infoPlayer(player, pl));
                } finally {
                    lock.unlock();
                }
            } else {
                humanoids.stream().filter(pl -> pl != null && player != pl).forEach(pl -> {
                    infoPlayer(player, pl);
                    PlayerService.gI().sendPetFollow(player, pl);
                });
            }
        } catch (Exception e) {
            Log.error(MapService.class, e);
        }
    }

    public boolean CanSeeInMapOffline(Player player, Player pl) {
        try {
            if (pl instanceof WhisTop) {
                return player.id == (long) Util.getPropertyByName(pl, "player_id");
            }
            return pl.id == -player.id;
        } catch (Exception e) {
            return false;
        }
    }

    public void infoPlayer(Player plReceive, Player plInfo) {
        try (Message msg = new Message(-5)) {
            msg.writer().writeInt((int) plInfo.id);
            String name = plInfo.clan != null ? "[" + plInfo.clan.name + "]" + plInfo.name
                    : plInfo.isPet && ((Pet) plInfo).typePet == ConstPet.WHIS
                            ? plInfo.name + "[Level " + ((Pet) plInfo).getLever() + "]"
                            : plInfo.isPet && ((Pet) plInfo).typePet == ConstPet.SUPER
                                    ? (plInfo.nPoint.power < 10_000_000_000L
                                            ? plInfo.name + "[Level " + ((Pet) plInfo).getLever() + "]"
                                            : "$Super Black Goku [Level " + ((Pet) plInfo).getLever() + "]")
                                    : plInfo.name;
            msg.writer().writeInt(plInfo.clan != null ? plInfo.clan.id : -1);
            msg.writer().writeByte(CaptionManager.getInstance().getLevel(plInfo));
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(plInfo.typePk);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeShort(plInfo.getHead());
            msg.writer().writeUTF(name);
            msg.writer().writeInt(plInfo.nPoint.hp);
            msg.writer().writeInt(plInfo.nPoint.hpMax);
            msg.writer().writeShort(plInfo.getBody());
            msg.writer().writeShort(plInfo.getLeg());
            msg.writer().writeByte(plInfo.getFlagBag());
            msg.writer().writeByte(-1);
            msg.writer().writeShort(plInfo.location.x);
            msg.writer().writeShort(plInfo.location.y);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(0);
            msg.writer().writeByte(plInfo.getUseSpaceShip());
            msg.writer().writeByte(plInfo.effectSkill.isMonkey ? 1 : 0);
            msg.writer().writeShort(plInfo.getMount());
            msg.writer().writeByte(plInfo.cFlag);
            msg.writer().writeByte(0);
            msg.writer().writeShort(plInfo.getAura());
            msg.writer().writeByte(plInfo.getEffFront());
            plReceive.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Service.getInstance().sendFlagPlayerToMe(plReceive, plInfo);
        if (plInfo.isDie()) {
            try (Message msg = new Message(-8)) {
                msg.writer().writeInt((int) plInfo.id);
                msg.writer().writeByte(0);
                msg.writer().writeShort(plInfo.location.x);
                msg.writer().writeShort(plInfo.location.y);
                plReceive.sendMessage(msg);
            } catch (Exception ignored) {
            }
        }
    }

    public void mapInfo(Player pl) {
        try (Message msg = new Message(-24)) {
            msg.writer().writeByte(map.mapId);
            msg.writer().writeByte(map.planetId);
            msg.writer().writeByte(map.tileId);
            msg.writer().writeByte(map.bgId);
            msg.writer().writeByte(map.type);
            msg.writer().writeUTF(map.mapName);
            msg.writer().writeByte(zoneId);
            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);
            List<WayPoint> wayPoints = map.wayPoints;
            msg.writer().writeByte(wayPoints.size());
            for (WayPoint wp : wayPoints) {
                msg.writer().writeShort(wp.minX);
                msg.writer().writeShort(wp.minY);
                msg.writer().writeShort(wp.maxX);
                msg.writer().writeShort(wp.maxY);
                msg.writer().writeBoolean(wp.isEnter);
                msg.writer().writeBoolean(wp.isOffline);
                msg.writer().writeUTF(wp.name);
            }
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(mob.tempId);
                msg.writer().writeByte(mob.getSys());
                msg.writer().writeInt(mob.point.getHP());
                msg.writer().writeByte(mob.level);
                msg.writer().writeInt(mob.point.getHpFull());
                msg.writer().writeShort(mob.location.x);
                msg.writer().writeShort(mob.location.y);
                msg.writer().writeByte(mob.isDie() ? ConstMob.MA_INHELL : ConstMob.MA_WALK);
                msg.writer().writeByte(0);
                msg.writer().writeBoolean(false);
            }
            msg.writer().writeByte(0);
            List<Npc> npcs = NpcManager.getNpcsByMapPlayer(pl);
            msg.writer().writeByte(npcs.size());
            for (Npc npc : npcs) {
                msg.writer().writeByte(npc.status);
                msg.writer().writeShort(npc.cx);
                msg.writer().writeShort(npc.cy);
                msg.writer().writeByte(npc.tempId);
                msg.writer().writeShort(npc.avartar);
            }
            List<ItemMap> itemsMap = getItemMapsForPlayer(pl);
            msg.writer().writeByte(itemsMap.size());
            for (ItemMap it : itemsMap) {
                msg.writer().writeShort(it.itemMapId);
                msg.writer().writeShort(it.itemTemplate.id);
                msg.writer().writeShort(it.x);
                msg.writer().writeShort(it.y);
                msg.writer().writeInt((int) it.playerId);
                if (it.playerId == -2)
                    msg.writer().writeShort(it.range);
            }
            try {
                msg.writer().write(FileIO.readFile("resources/data/nro/map/item_bg_map_data/" + map.mapId));
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }
            List<EffectMap> em = map.effMap;
            msg.writer().writeShort(em.size());
            for (EffectMap e : em) {
                msg.writer().writeUTF(e.getKey());
                msg.writer().writeUTF(e.getValue());
            }
            msg.writer().writeByte(map.bgType);
            msg.writer().writeByte(pl.getUseSpaceShip());
            msg.writer().writeByte(0);
            pl.sendMessage(msg);
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public void mapInfo(Player pl, int npcId) {
        try (Message msg = new Message(-24)) {
            msg.writer().writeByte(map.mapId);
            msg.writer().writeByte(map.planetId);
            msg.writer().writeByte(map.tileId);
            msg.writer().writeByte(map.bgId);
            msg.writer().writeByte(map.type);
            msg.writer().writeUTF(map.mapName);
            msg.writer().writeByte(zoneId);
            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);
            List<WayPoint> wayPoints = map.wayPoints;
            msg.writer().writeByte(wayPoints.size());
            for (WayPoint wp : wayPoints) {
                msg.writer().writeShort(wp.minX);
                msg.writer().writeShort(wp.minY);
                msg.writer().writeShort(wp.maxX);
                msg.writer().writeShort(wp.maxY);
                msg.writer().writeBoolean(wp.isEnter);
                msg.writer().writeBoolean(wp.isOffline);
                msg.writer().writeUTF(wp.name);
            }
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(mob.tempId);
                msg.writer().writeByte(mob.getSys());
                msg.writer().writeInt(mob.point.getHP());
                msg.writer().writeByte(mob.level);
                msg.writer().writeInt(mob.point.getHpFull());
                msg.writer().writeShort(mob.location.x);
                msg.writer().writeShort(mob.location.y);
                msg.writer().writeByte(mob.isDie() ? ConstMob.MA_INHELL : ConstMob.MA_WALK);
                msg.writer().writeByte(0);
                msg.writer().writeBoolean(false);
            }
            msg.writer().writeByte(0);
            List<Npc> npcs = NpcManager.getNpcsByMapPlayer(pl);
            msg.writer().writeByte(npcs.size() - 1);
            for (Npc npc : npcs.stream().filter(npc -> npc.tempId != npcId).collect(Collectors.toList())) {
                msg.writer().writeByte(npc.status);
                msg.writer().writeShort(npc.cx);
                msg.writer().writeShort(npc.cy);
                msg.writer().writeByte(npc.tempId);
                msg.writer().writeShort(npc.avartar);
            }
            List<ItemMap> itemsMap = getItemMapsForPlayer(pl);
            msg.writer().writeByte(itemsMap.size());
            for (ItemMap it : itemsMap) {
                msg.writer().writeShort(it.itemMapId);
                msg.writer().writeShort(it.itemTemplate.id);
                msg.writer().writeShort(it.x);
                msg.writer().writeShort(it.y);
                msg.writer().writeInt((int) it.playerId);
                if (it.playerId == -2)
                    msg.writer().writeShort(it.range);
            }
            try {
                msg.writer().write(FileIO.readFile("resources/data/nro/map/item_bg_map_data/" + map.mapId));
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }
            List<EffectMap> em = map.effMap;
            msg.writer().writeShort(em.size());
            for (EffectMap e : em) {
                msg.writer().writeUTF(e.getKey());
                msg.writer().writeUTF(e.getValue());
            }
            msg.writer().writeByte(map.bgType);
            msg.writer().writeByte(pl.getUseSpaceShip());
            msg.writer().writeByte(0);
            pl.sendMessage(msg);
        } catch (Exception e) {
            Log.error(Service.class, e);
        }
    }

    public TrapMap isInTrap(Player player) {
        return trapMaps.stream()
                .filter(trap -> player.location.x >= trap.x && player.location.x <= trap.x + trap.w
                        && player.location.y >= trap.y && player.location.y <= trap.y + trap.h)
                .findFirst().orElse(null);
    }

    public void changeMapWaypoint(Player player) {
        Zone zoneJoin = null;
        int xGo = player.location.x;
        int yGo = player.location.y;
        if (map.mapId == 45 || map.mapId == 46) {
            int x = player.location.x;
            int y = player.location.y;
            if (x >= 35 && x <= 685 && y >= 550 && y <= 560) {
                xGo = map.mapId == 45 ? 420 : 636;
                yGo = 150;
                zoneJoin = MapService.gI().getMapCanJoin(player, map.mapId + 1);
            }
        }
        if (zoneJoin == null) {
            WayPoint wp = MapService.gI().getWaypointPlayerIn(player);
            if (wp != null) {
                zoneJoin = MapService.gI().getMapCanJoin(player, wp.goMap);
                if (zoneJoin != null) {
                    xGo = wp.goX;
                    yGo = wp.goY;
                }
            }
        }
        if (zoneJoin != null) {
            ChangeMapService.gI().changeMap(player, zoneJoin, -1, -1, xGo, yGo, ChangeMapService.NON_SPACE_SHIP);
        } else {
            int x = Math.min(Math.max(player.location.x, 60), map.mapWidth - 60);
            Service.getInstance().resetPoint(player, x, player.location.y);
            Service.getInstance().sendThongBaoOK(player, "Không thể đến khu vực này");
        }
    }

    public void playerMove(Player player, int x, int y) {
        if (player.isDie())
            return;
        if (player.effectSkill.isCharging)
            EffectSkillService.gI().stopCharge(player);
        if (player.effectSkill.useTroi)
            EffectSkillService.gI().removeUseTroi(player);
        player.location.x = x;
        player.location.y = y;
        if (map.mapId >= 85 && map.mapId <= 91) {
            if (x < 24 || x > map.mapWidth - 24 || y < 0 || y > map.mapHeight - 24
                    || (!player.isBoss && !player.isPet && map.yPhysicInTop(x, y) >= map.mapHeight - 24)) {
                if (MapService.gI().getWaypointPlayerIn(player) == null) {
                    ChangeMapService.gI().changeMap(player, 21 + player.gender, 0, 200, 336);
                    return;
                }
            }
        }
        if (player.pet != null)
            player.pet.followMaster();
        if (player.minipet != null)
            player.minipet.followMaster();
        MapService.gI().sendPlayerMove(player);
        TaskService.gI().checkDoneTaskGoToMap(player, this);
    }

    public Mob findMobByID(int id) {
        return mobs.stream().filter(m -> m.id == id).findFirst().orElse(null);
    }

    public Player findPlayerByID(long id) {
        return players.stream().filter(p -> p.id == id).findFirst().orElse(null);
    }

    public void sendMessage(Message m) {
        players.forEach(p -> p.sendMessage(m));
    }
}