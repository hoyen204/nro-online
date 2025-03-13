package com.nro.nro_online.models.player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.card.Card;
import com.nro.nro_online.card.CollectionBook;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.consts.ConstTask;
import com.nro.nro_online.data.DataGame;
import com.nro.nro_online.dialog.ConfirmDialog;
import com.nro.nro_online.models.DragonNamecWar.TranhNgocService;
import com.nro.nro_online.models.boss.event.EscortedBoss;
import com.nro.nro_online.models.boss.event.noel.NoelBossBall;
import com.nro.nro_online.models.clan.Buff;
import com.nro.nro_online.models.clan.Clan;
import com.nro.nro_online.models.clan.ClanMember;
import com.nro.nro_online.models.intrinsic.IntrinsicPlayer;
import com.nro.nro_online.models.item.Costume;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.item.ItemTime;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.map.TrapMap;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.map.mabu.MabuWar;
import com.nro.nro_online.models.map.mabu.MabuWar14h;
import com.nro.nro_online.models.map.war.BlackBallWar;
import com.nro.nro_online.models.map.war.NamekBallWar;
import com.nro.nro_online.models.mob.MobMe;
import com.nro.nro_online.models.npc.specialnpc.BillEgg;
import com.nro.nro_online.models.npc.specialnpc.EggLinhThu;
import com.nro.nro_online.models.npc.specialnpc.MabuEgg;
import com.nro.nro_online.models.npc.specialnpc.MagicTree;
import com.nro.nro_online.models.pvp.PVP;
import com.nro.nro_online.models.skill.PlayerSkill;
import com.nro.nro_online.models.task.TaskPlayer;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.services.TaskService;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.services.func.CombineNew;
import com.nro.nro_online.services.func.PVPServcice;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;
import lombok.Getter;
import lombok.Setter;

/**
 * Arriety
 */
public class Player {

    public BillEgg billEgg;
    public EggLinhThu egglinhthu;

    public long lastTimeCTG;
    public long lastTimeChat;

    public List<Integer> idEffChar = new ArrayList<>();

    public int server;
    public byte[] buyLimit;
    public boolean isLinhThuFollow;
    public long dameAttack = 1000;
    public long lastTimeUpdateBallWar;
    public long lastTimeChatTheGioi;
    public PlayerEvent event;
    public List<String> textRuongGo = new ArrayList<>();
    public boolean receivedWoodChest;
    public int goldChallenge;
    public int levelWoodChest;
    public boolean isInvisible;
    public boolean sendMenuGotoNextFloorMabuWar;
    public long lastTimeBabiday;
    public long lastTimeChangeZone;
    public long lastTimeChatGlobal;
    public long lastTimeChatPrivate;
    public long lastTimeChangeMap;

    public LocalDateTime firstTimeLogin;
    public boolean canGetFirstTimeLogin;

    @Getter
    private Session session;
    public byte countSaveFail;
    public boolean beforeDispose;

    public long timeFixInventory;
    public boolean isPet;
    public boolean isBoss;
    public boolean isMiniPet;

    public int playerTradeId = -1;
    public Player playerTrade;

    public int mapIdBeforeLogout;
    public List<Zone> mapBlackBall;
    public Zone zone;
    public Zone mapBeforeCapsule;
    public List<Zone> mapCapsule;
    public Pet pet;
    public MiniPet minipet;

    public long lastTimeNotifyTimeHoldBlackBall;
    public long lastTimeHoldBlackBall;
    public int tempIdBlackBallHold = -1;
    public boolean isHoldBlackBall;
    public boolean isHoldNamecBall;
    public boolean isHoldNamecBallTranhDoat;
    public int tempIdNamecBallHoldTranhDoat = -1;

    public MobMe mobMe;
    public Location location;
    public SetClothes setClothes;
    public EffectSkill effectSkill;
    public MabuEgg mabuEgg;
    public TaskPlayer playerTask;
    public ItemTime itemTime;
    public Fusion fusion;
    public MagicTree magicTree;
    public IntrinsicPlayer playerIntrinsic;
    public Inventory inventory;
    public PlayerSkill playerSkill;
    public CombineNew combineNew;
    public IDMark iDMark;
    public Charms charms;
    public EffectSkin effectSkin;
    public Gift gift;
    public NPoint nPoint;
    public RewardBlackBall rewardBlackBall;
    public EffectFlagBag effectFlagBag;

    public Clan clan;
    public ClanMember clanMember;

    public ListFriendEnemy<Friend> friends;
    public ListFriendEnemy<Enemy> enemies;

    protected boolean actived = false;
    public boolean loaded;

    public long id;
    public String name;
    public byte gender;
    public boolean isNewMember = true;
    public short head;

    public byte typePk;

    public byte cFlag;
    public long lastTimeChangeFlag;
    public long lastTimeTrade;

    public boolean haveTennisSpaceShip;
    private byte useSpaceShip;

    public boolean isGoHome;

    public boolean justRevived;
    public long lastTimeRevived;
    public boolean immortal;

    public long lastTimeBan;
    public long lastTimeUpdate;
    public boolean isBan;

    public boolean isGotoFuture;
    public long lastTimeGoToFuture;
    public boolean isgotoPrimaryForest;
    public long lastTimePrimaryForest;

    public boolean isGoToBDKB;
    public long lastTimeGoToBDKB;
    public long lastTimeAnXienTrapBDKB;
    private short powerPoint;
    private short percentPowerPont;

    public long lastTimePickItem;
    public int tongnap;
    @Setter
    @Getter
    private CollectionBook collectionBook;
    @Getter
    @Setter
    private boolean isSaving, isDisposed;
    @Getter
    @Setter
    private boolean interactWithKarin;
    @Getter
    @Setter
    private EscortedBoss escortedBoss;
    @Setter
    @Getter
    private ConfirmDialog confirmDialog;
    @Getter
    @Setter
    public byte[] rewardLimit;
    @Setter
    @Getter
    private PetFollow petFollow;
    @Setter
    @Getter
    private Buff buff;
    @Setter
    @Getter
    private LocalDateTime timeCache;

    public Player() {
        location = new Location();
        nPoint = new NPoint(this);
        inventory = new Inventory(this);
        playerSkill = new PlayerSkill(this);
        setClothes = new SetClothes(this);
        effectSkill = new EffectSkill(this);
        fusion = new Fusion(this);
        playerIntrinsic = new IntrinsicPlayer(this);
        rewardBlackBall = new RewardBlackBall(this);
        effectFlagBag = new EffectFlagBag(this);
        //----------------------------------------------------------------------
        iDMark = new IDMark();
        combineNew = new CombineNew();
        playerTask = new TaskPlayer(this);
        friends = new ListFriendEnemy<>(this);
        enemies = new ListFriendEnemy<>(this);
        itemTime = new ItemTime(this);
        charms = new Charms(this);
        gift = new Gift(this);
        effectSkin = new EffectSkin(this);
        event = new PlayerEvent(this);
        buyLimit = new byte[13];
        buff = Buff.NONE;
    }

    //--------------------------------------------------------------------------
    public short getPowerPoint() {
        return powerPoint;
    }

    public void addPowerPoint(int value) {
        powerPoint += value;
    }

    public short getPercentPowerPont() {
        return percentPowerPont;
    }

    public void addPercentPowerPoint(int value) {
        percentPowerPont += value;
    }

    public void resetPowerPoint() {
        percentPowerPont = 0;
        powerPoint = 0;
    }

    public void setUseSpaceShip(byte useSpaceShip) {
        // 0 - không dùng
        // 1 - tàu vũ trụ theo hành tinh
        // 2 - dịch chuyển tức thời
        // 3 - tàu tenis
        this.useSpaceShip = useSpaceShip;
    }

    public byte getUseSpaceShip() {
        return this.useSpaceShip;
    }

    public boolean isDie() {
        if (this.nPoint != null) {
            return this.nPoint.hp <= 0;
        } else {
            return true;
        }
    }

    //--------------------------------------------------------------------------
    public void setSession(Session session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session == null)
            return;

        session.sendMessage(msg);
    }

    public boolean isPl() {
        return !isPet && !isBoss && !isMiniPet;
    }

    public int version() {
        return session.version;
    }

    public boolean isVersionAbove(int version) {
        return version() >= version;
    }

    public void update() {
        if (!this.beforeDispose) {
            try {
                if (!isBan) {
                    if (nPoint != null) {
                        nPoint.update();
                    }
                    if (fusion != null) {
                        fusion.update();
                    }
                    if (effectSkin != null) {
                        effectSkill.update();
                    }
                    if (mobMe != null) {
                        mobMe.update();
                    }
                    if (effectSkin != null) {
                        effectSkin.update();
                    }
                    if (pet != null) {
                        pet.update();
                    }
                    if (minipet != null) {
                        minipet.update();
                    }
                    if (magicTree != null) {
                        magicTree.update();
                    }
                    if (itemTime != null) {
                        itemTime.update();
                    }
                    if (event != null) {
                        event.update();
                    }
                    UpdateEffChar.getInstance().updateEff(this);
                    BlackBallWar.gI().update(this);
                    if (this.isPl()) {
                        MabuWar.gI().update(this);
                        MabuWar14h.gI().update(this);
                        checkPlayerInMap();
                        //                        if (Util.canDoWithTime(lastTimeUpdate, 60000)) {
                        //                            this.playerTask.achivements.get(ConstAchive.HOAT_DONG_CHAM_CHI).count++;
                        //                        }
                    }
                    if (isGotoFuture && Util.canDoWithTime(lastTimeGoToFuture, 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                        this.isGotoFuture = false;
                    }
                    if (isGoToBDKB && Util.canDoWithTime(lastTimeGoToBDKB, 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 135, -1, 35);
                        this.isGoToBDKB = false;
                    }
                    if (isgotoPrimaryForest && Util.canDoWithTime(lastTimePrimaryForest, 6000)) {
                        ChangeMapService.gI().changeMap(this, 161, -1, 169, 312);
                        this.isgotoPrimaryForest = false;
                    }
                    if (this.zone != null) {
                        TrapMap trap = this.zone.isInTrap(this);
                        if (trap != null) {
                            trap.doPlayer(this);
                        }
                    }
                } else {
                    if (Util.canDoWithTime(lastTimeBan, 5000)) {
                        Client.gI().kickSession(session);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.error(Player.class, e, "Lỗi tại player: " + this.name);
            }
        }
    }

    public boolean checkHutMau() {
        return this.nPoint.tlHutHp > 0 || this.nPoint.tlHutMp > 0 || this.nPoint.tlHutHpMob > 0;
    }

    private void checkPlayerInMap() {
        if (this.zone != null) {
            int mapid = this.zone.map.mapId;
            if (MapService.gI().isMapNgucTu(mapid) && this.nPoint.power < 80_000_000_000L) {
                ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                Service.getInstance().sendBigMessage(this, 1139, "|1|Do phát hiện có hành vi bất thường nên\n "
                        + "chúng tôi đã đưa bạn về nhà và xử phạt 50 ruby\n"
                        + "|7|nếu còn tiếp tục tái phạm 5 lần bạn sẽ bị khóa account vĩnh viễn");
            }

            if (MapService.gI().isMapHTTV(mapid) && this.playerTask.taskMain.id < 24) {
                ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
                Service.getInstance().sendBigMessage(this, 1139, "|1|Do phát hiện có hành vi bất thường nên\n "
                        + "chúng tôi đã đưa bạn về nhà và xử phạt 50 ruby\n"
                        + "|7|nếu còn tiếp tục tái phạm 5 lần bạn sẽ bị khóa account vĩnh viễn");
            }
        }
    }

    private void checkLocation() {
        if (this.location.x > this.zone.map.mapWidth || this.location.x < 0
                || this.location.y > this.zone.map.mapHeight || this.location.y < 0) {
            if (this.inventory.gold >= 500000000) {
                this.inventory.subGold(500000000);
            } else {
                this.inventory.gold = 0;
            }
            PlayerService.gI().sendInfoHpMpMoney(this);
            ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, 400, 336);
            Service.getInstance().sendBigMessage(this, 1139, "|1|Do phát hiện có hành vi bất thường nên\n "
                    + "chúng tôi đã đưa bạn về nhà và xử phạt 500Tr vàng\n"
                    + "|7|nếu còn tiếp tục tái phạm sẽ khóa vĩnh viễn");
        }
    }

    //--------------------------------------------------------------------------
    /*
     * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất
     * {383, 384, 385}: ht porata xayda trái đất
     * {391, 392, 393}: ht namếc
     * {870, 871, 872}: ht c2 trái đất
     * {873, 874, 875}: ht c2 namếc
     * {867, 878, 869}: ht c2 xayda
     */
    private static final short[][] idOutfitFusion = {
            { 380, 381, 382 },//ht lưỡng long nhất thể xayda trái đất
            { 383, 384, 385 },//ht porata xayda trái đất
            { 391, 392, 393 },//ht namếc

            { 870, 871, 872 },//ht c2 trái đất
            { 873, 874, 875 },//ht c2 namếc
            { 867, 868, 869 },//ht c2 xayda

            { 1332, 1333, 1334 },//ht c3 trái đất
            { 1329, 1330, 1331 },//ht c3 namếc
            { 1335, 1336, 1337 }//ht c3 xayda
    };

    public byte getEffFront() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item costume = this.inventory.itemsBody.get(5);
        if (!costume.isNotNullItem()) {
            return -1;
        }
        if (costume.template.id == 2061) {
            return 8;
        }
        int[] levels = new int[5];
        ItemOption[] options = new ItemOption[5];
        Item[] items = new Item[] {
                this.inventory.itemsBody.get(0),
                this.inventory.itemsBody.get(1),
                this.inventory.itemsBody.get(2),
                this.inventory.itemsBody.get(3),
                this.inventory.itemsBody.get(4)
        };
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 72) {
                    levels[i] = io.param;
                    options[i] = io;
                    break;
                }
            }
        }
        int minLevel = Integer.MAX_VALUE;
        int count = 0;
        for (int level : levels) {
            if (level >= 4 && level <= 8) {
                minLevel = Math.min(minLevel, level);
                count++;
            }
        }
        if (count == 5) {
            return (byte) minLevel;
        } else {
            return -1;
        }

    }

    public byte getAura() {
        CollectionBook book = this.collectionBook;
        if (book != null) {
            Card card = book.getCards().stream()
                    .filter(t -> t.isUse() && t.getCardTemplate().getAura() != -1)
                    .findFirst()
                    .orElse(null);
            if (card != null) {
                return (byte) card.getCardTemplate().getAura();
            }
        }
        return -1;
    }

    public boolean checkSkinFusion() {
        if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            short costumeId = inventory.itemsBody.get(5).template.id;
            return costumeId >= 601 && costumeId <= 603 || costumeId >= 639 && costumeId <= 641;
        }
        return false;
    }

    public short getHead() {
        if (this.id == 1000000) {
            return 412;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else if (this.itemTime.isMaTroi) {
            switch (this.itemTime.iconMaTroi) {
            case 6091 -> {
                return 651;
            }
            case 5101 -> {
                return 545;
            }
            case 6094 -> {
                return 654;
            }
            }
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else if (effectSkin != null && effectSkin.isHoaDa) {
            return 454;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (checkSkinFusion()) {
                Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
                return (short) (ct.getId()[0] != -1 ? ct.getId()[0] : inventory.itemsBody.get(5).template.part);
            } else if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][0];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (checkSkinFusion()) {
                return this.head;
            }
            if (ct != null) {
                return (short) (ct.getId()[0] != -1 ? ct.getId()[0] : inventory.itemsBody.get(5).template.part);
            }
        }
        return this.head;
    }

    public short getBody() {
        if (this.id == 1000000) {
            return 413;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        } else if (this.itemTime.isMaTroi) {
            switch (this.itemTime.iconMaTroi) {
            case 6091 -> {
                return 652;
            }
            case 5101 -> {
                return 548;
            }
            case 6094 -> {
                return 655;
            }
            }
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else if (effectSkin != null && effectSkin.isHoaDa) {
            return 455;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (checkSkinFusion()) {
                Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
                return (short) ct.getId()[1];
            }
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][1];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (checkSkinFusion()) {
                if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
                    if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
                        return inventory.itemsBody.get(0).template.part;
                    }
                }
            }
            if (ct != null && ct.getId()[1] != -1) {
                return (short) ct.getId()[1];
            }
        }
        if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.getFirst().template.part;
        }
        return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
    }

    public short getLeg() {
        if (this.id == 1000000) {
            return 414;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        } else if (this.itemTime.isMaTroi) {
            switch (this.itemTime.iconMaTroi) {
            case 6091:
                return 653;
            case 5101:
                return 549;
            case 6094:
                return 656;
            }
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else if (effectSkin != null && effectSkin.isHoaDa) {
            return 456;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (checkSkinFusion()) {
                Costume ct = Manager.gI().getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
                return (short) ct.getId()[2];
            }
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][2];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            if (checkSkinFusion()) {
                if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
                    return inventory.itemsBody.get(1).template.part;
                }
            }
            Costume ct = Manager.getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getId()[2] != -1) {
                return (short) ct.getId()[2];
            }
        }
        if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }
        return (short) (gender == 1 ? 60 : 58);
    }

    public short getFlagBag() {
        if (this.isHoldBlackBall) {
            return 31;
        } else if (this.isHoldNamecBall) {
            return 30;
        }
        if (this.inventory.itemsBody.size() >= 8 && this.inventory.itemsBody.get(7).isNotNullItem()) {
                return this.inventory.itemsBody.get(7).template.part;
            }

        if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
            return 28;
        }
        if (this.clan != null) {
            return (short) this.clan.imgId;
        }
        return -1;
    }

    public short getMount() {
        if (this.isVersionAbove(220)) {
            for (Item item : inventory.itemsBody) {
                if (item.isNotNullItem()) {
                    if (item.template.type == 24) {
                        if (item.template.gender == 3 || item.template.gender == this.gender) {
                            return item.template.id;
                        } else {
                            return -1;
                        }
                    }
                    if (item.template.type == 23) {
                        if (item.template.id < 500) {
                            return item.template.id;
                        } else {
                            Object mount = DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
                            if (mount == null) {
                                return -1;
                            }
                            return (short) mount;
                        }
                    }
                }
            }
        } else {
            for (Item item : inventory.itemsBag) {
                if (item.isNotNullItem()) {
                    if (item.template.type == 24) {
                        if (item.template.gender == 3 || item.template.gender == this.gender) {
                            return item.template.id;
                        } else {
                            return -1;
                        }
                    }
                    if (item.template.type == 23) {
                        if (item.template.id < 500) {
                            return item.template.id;
                        } else {
                            Object mount = DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
                            if (mount == null) {
                                return -1;
                            }
                            return (short) mount;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private Integer GetDameByNoelBall(Player plAtt, int damage) {
        if (plAtt instanceof NoelBossBall) {
            if (isDie()) {
                if (plAtt != null && plAtt.zone != null) {
                    if (MapService.gI().isMapMabuWar(plAtt.zone.map.mapId) && MabuWar.gI().isTimeMabuWar()) {
                        plAtt.addPowerPoint(5);
                        Service.getInstance().sendPowerInfo(plAtt, "TL", plAtt.getPowerPoint());
                    }
                }
                setDie(plAtt);
            }
            return damage;
        }
        return null;
    }

    //--------------------------------------------------------------------------
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.getSession() != null && this.isAdmin()) {
            return 0;
        }
        if (this.isDie()) {
            return 0;
        }
        if (this.isMiniPet) {
            return 0;
        }
        if (plAtt != null) {
            if (!this.isBoss && plAtt.nPoint.xDameChuong && SkillUtil.isUseSkillChuong(plAtt)) {
                damage = plAtt.nPoint.tlDameChuong * damage;
                plAtt.nPoint.xDameChuong = false;
            }
            if (this.nPoint.mstChuong > 0 && SkillUtil.isUseSkillChuong(plAtt)) {
                PlayerService.gI().hoiPhuc(this, 0, damage * this.nPoint.mstChuong / 100);
                damage = 0;
            }
        }
        if (!SkillUtil.isUseSkillBoom(plAtt)) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                return 0;
            }
        }
        if (isMobAttack && (this.charms.tdBatTu > System.currentTimeMillis() || this.itemTime.isMaTroi) && damage >= this.nPoint.hp) {
            damage = this.nPoint.hp - 1;
        }
        damage = this.nPoint.subDameInjureWithDeff(damage);
        if (!piercing && effectSkill.isShielding) {
            if (damage > nPoint.hpMax) {
                EffectSkillService.gI().breakShield(this);
            }
            damage = 1;
        }
        if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
            damage = this.nPoint.hp - 1;
        }
        if (this.nPoint.tlGiamst > 0) {
            damage -= nPoint.calPercent(damage, this.nPoint.tlGiamst);
        }
        if (this.effectSkill.isHoldMabu) {
            damage = 1;
        }
        this.nPoint.subHP(damage);
        if (this.effectSkill.isHoldMabu && Util.isTrue(30, 150)) {
            Service.getInstance().removeMabuEat(this);
        }
        if (this.isDie()) {
            if (plAtt != null && plAtt.zone != null) {
                if (MapService.gI().isMapMabuWar(plAtt.zone.map.mapId) && MabuWar.gI().isTimeMabuWar()) {
                    plAtt.addPowerPoint(5);
                    Service.getInstance().sendPowerInfo(plAtt, "TL", plAtt.getPowerPoint());
                }
            }
            this.setDie(plAtt);
        }
        return damage;
    }

    public void setDie(Player plAtt) {
        if (this.nPoint.power >= 1_000_000) {
            long powersub = this.nPoint.power / 10000;
            nPoint.powerSub(powersub);
        }
        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.getInstance().point(this);
        }
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        this.effectSkill.removeSkillEffectWhenDie();
        nPoint.setHp(0);
        nPoint.setMp(0);
        if (this.mobMe != null) {
            this.mobMe.mobMeDie();
        }
        Service.getInstance().charDie(this);
        if (!this.isPet && !this.isBoss && plAtt != null && !plAtt.isPet && !plAtt.isBoss) {
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
            }
        }
        if (this.effectSkin.isSocola) {
            reward(plAtt);
        }
        if (this.zone != null) {
            if (MapService.gI().isMapMabuWar(this.zone.map.mapId)) {
                if (this.powerPoint < 20) {
                    this.powerPoint = 0;
                }
                if (this.percentPowerPont < 100) {
                    this.percentPowerPont = 0;
                }
            }
        }
        PVPServcice.gI().finishPVP(this, PVP.TYPE_DIE);
        BlackBallWar.gI().dropBlackBall(this);
        if (isHoldNamecBall) {
            NamekBallWar.gI().dropBall(this);
        }
        if (isHoldNamecBallTranhDoat) {
            TranhNgocService.getInstance().dropBall(this, (byte) -1);
            TranhNgocService.getInstance().sendUpdateLift(this);
        }
    }

    public void rewardDuoiKhi(Player pl) {
        if (pl != null) {
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            ItemMap itemMap = new ItemMap(this.zone, 579, 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            Service.getInstance().dropItemMap(zone, itemMap);
        }
    }

    public void reward(Player pl) {
        if (pl != null) {
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            ItemMap itemMap = new ItemMap(this.zone, 516, 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            Service.getInstance().dropItemMap(zone, itemMap);
        }
    }

    //--------------------------------------------------------------------------
    public void setClanMember() {
        if (this.clanMember != null) {
            this.clanMember.powerPoint = this.nPoint.power;
            this.clanMember.head = this.getHead();
            this.clanMember.body = this.getBody();
            this.clanMember.leg = this.getLeg();
        }
    }

    public boolean isAdmin() {
        return this.session.isAdmin;
    }

    public void setJustRevivaled() {
        this.justRevived = true;
        this.lastTimeRevived = System.currentTimeMillis();
        this.immortal = true;
    }

    public void dispose() {
        if (escortedBoss != null) {
            escortedBoss.stopEscorting();
        }
        isDisposed = true;
    }
}
