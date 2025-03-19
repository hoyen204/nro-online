package com.nro.nro_online.models.npc.specialnpc;

import com.nro.nro_online.consts.ConstAchive;
import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Log;
import com.nro.nro_online.utils.Util;

public class MagicTree {
    // Constants
    public static final byte MAX_LEVEL = 10;
    public static final byte MIN_LEVEL = 1;

    // Item templates and parameters
    public static final short[] PEA_TEMP = {13, 60, 61, 62, 63, 64, 65, 352, 523, 595};
    public static final int[] PEA_PARAM = {100, 500, 2, 4, 8, 16, 32, 64, 128, 256};

    // Position data
    private static final int[][][] POS_PEAS = {
        {{19, 22}, {-1, 16}, {3, 10}, {19, 8}, {9, 0}},
        {{-1, 27}, {22, 35}, {15, 24}, {0, 17}, {-1, 7}, {26, 5}, {5, 0}},
        {{25, 41}, {-1, 40}, {25, 34}, {3, 32}, {25, 23}, {10, 19}, {2, 12}, {17, 10}, {4, 5}},
        {{3, 44}, {21, 49}, {25, 39}, {4, 30}, {29, 25}, {0, 18}, {21, 15}, {14, 39}, {18, 25}, {4, 7}, {15, 0}},
        {{21, 58}, {0, 56}, {18, 48}, {10, 0}, {25, 38}, {0, 26}, {14, 28}, {25, 16}, {1, 14}, {22, 7}, {10, 14}, {28, 23}, {15, 16}},
        {{25, 63}, {0, 66}, {21, 52}, {3, 55}, {14, 60}, {3, 45}, {22, 43}, {10, 35}, {22, 28}, {3, 28}, {18, 17}, {3, 14}, {17, 6}, {11, 22}, {6, 1}},
        {{32, 86}, {5, 77}, {25, 77}, {8, 89}, {29, 68}, {4, 63}, {18, 61}, {33, 53}, {8, 48}, {26, 39}, {11, 36}, {33, 23}, {18, 25}, {4, 20}, {26, 12}, {12, 7}, {19, 0}},
        {{32, 86}, {5, 77}, {25, 77}, {8, 89}, {29, 68}, {4, 63}, {18, 61}, {33, 53}, {8, 48}, {26, 39}, {11, 36}, {33, 23}, {18, 25}, {4, 20}, {26, 12}, {12, 7}, {19, 0}, {19, 0}, {19, 0}},
        {{32, 86}, {5, 77}, {25, 77}, {8, 89}, {29, 68}, {4, 63}, {18, 61}, {33, 53}, {8, 48}, {26, 39}, {11, 36}, {33, 23}, {18, 25}, {4, 20}, {26, 12}, {12, 7}, {19, 0}, {19, 0}, {19, 0}, {19, 0}, {19, 0}},
        {{32, 86}, {5, 77}, {25, 77}, {8, 89}, {29, 68}, {4, 63}, {18, 61}, {33, 53}, {8, 48}, {26, 39}, {11, 36}, {33, 23}, {18, 25}, {4, 20}, {26, 12}, {12, 7}, {19, 0}, {19, 0}, {19, 0}, {19, 0}, {19, 0}, {19, 0}, {19, 0}}
    };

    // Upgrade data [days, hours, mins, gold]
    private static final short[][] PEA_UPGRADE = {
        {0, 0, 10, 5}, {0, 1, 40, 10}, {0, 16, 40, 100}, {6, 22, 0, 1},
        {13, 21, 0, 10}, {27, 18, 0, 20}, {55, 13, 0, 50}, {69, 10, 0, 100},
        {104, 4, 0, 300}, {0, 0, 0, 0}
    };

    // Magic tree icons [gender][level]
    private static final short[][] ID_MAGIC_TREE = {
        {84, 85, 86, 87, 88, 89, 90, 90, 90, 90},
        {371, 372, 373, 374, 375, 376, 377, 377, 377, 377},
        {378, 379, 380, 381, 382, 383, 384, 384, 384, 384}
    };
    private static final short[][] POS_MAGIC_TREE = {{348, 336}, {372, 336}, {348, 336}};

    // Instance variables
    public final Player player;
    public boolean loadedMagicTreeToPlayer;
    public boolean isUpgrade;
    public byte level;
    public int currPeas;
    public long lastTimeHarvest;
    public long lastTimeUpgrade;

    public MagicTree(Player player, byte level, byte currPeas, long lastTimeHarvest, boolean isUpgrade, long lastTimeUpgrade) {
        this.player = player;
        this.level = level;
        this.currPeas = Math.min(currPeas, getMaxPea());
        this.isUpgrade = isUpgrade;
        this.lastTimeHarvest = lastTimeHarvest;
        this.lastTimeUpgrade = lastTimeUpgrade;
    }

    public void update() {
        if (!isUpgrade) {
            updatePeas();
        } else {
            updateUpgrade();
        }
    }

    private void updatePeas() {
        if (this.currPeas < getMaxPea()) {
            int timeElapsed = (int) ((System.currentTimeMillis() - lastTimeHarvest) / 1000);
            int numPeaRelease = timeElapsed / getSecondPerPea();
            if (numPeaRelease > 0) {
                this.currPeas = Math.min(this.currPeas + numPeaRelease, getMaxPea());
                if (this.currPeas >= getMaxPea()) {
                    this.lastTimeHarvest = System.currentTimeMillis();
                } else {
                    this.lastTimeHarvest += (numPeaRelease * getSecondPerPea()) * 1000L;
                }
            }
        }
    }

    private void updateUpgrade() {
        if (Util.canDoWithTime(lastTimeUpgrade, getTimeUpgrade())) {
            if (this.level < MAX_LEVEL) {
                this.level++;
                if (player.playerTask != null && player.playerTask.achievements != null) {
                    player.playerTask.achievements.get(ConstAchive.NONG_DAN_CHAM_CHI).count++;
                }
            }
            this.isUpgrade = false;
        }
    }

    public void loadMagicTree() {
        try (Message msg = new Message(-34)) {
            msg.writer().writeByte(0);
            msg.writer().writeShort(ID_MAGIC_TREE[player.gender][level - 1]);
            msg.writer().writeUTF("Đậu thần cấp " + level);
            msg.writer().writeShort(POS_MAGIC_TREE[player.gender][0]);
            msg.writer().writeShort(POS_MAGIC_TREE[player.gender][1]);
            msg.writer().writeByte(level);
            msg.writer().writeShort(this.currPeas);
            msg.writer().writeShort(getMaxPea());
            msg.writer().writeUTF("Đang kết hạt\nCây lớn sinh nhiều hạt hơn");
            msg.writer().writeInt(this.isUpgrade ? getSecondUpgrade() : getSecondPea());
            msg.writer().writeByte(POS_PEAS[this.level - 1].length);
            for (int[] pos : POS_PEAS[this.level - 1]) {
                msg.writer().writeByte(pos[0]);
                msg.writer().writeByte(pos[1]);
            }
            msg.writer().writeBoolean(this.isUpgrade);
            player.sendMessage(msg);
            if (!loadedMagicTreeToPlayer) {
                loadedMagicTreeToPlayer = true;
            }
        } catch (Exception e) {
            Log.error(MagicTree.class, e);
        }
    }

    public void openMenuTree() {
        try (Message msg = new Message(-34)) {
            msg.writer().writeByte(1);
            if (!isUpgrade) {
                msg.writer().writeUTF("Thu\nhoạch");
                if (this.level < MAX_LEVEL) {
                    msg.writer().writeUTF(getTextMenuUpgrade());
                }
                if (this.currPeas < getMaxPea()) {
                    msg.writer().writeUTF("Kết hạt\nnhanh\n0 ngọc");
                    this.player.iDMark.setIndexMenu(ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA);
                } else {
                    this.player.iDMark.setIndexMenu(ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA);
                }
            } else {
                msg.writer().writeUTF("Nâng cấp\nnhanh\n0\nngọc");
                msg.writer().writeUTF("Hủy\nnâng cấp\nhồi " + (PEA_UPGRADE[this.level - 1][3] / 2 + (this.level <= 3 ? " k" : " Tr")) + "\nvàng");
                this.player.iDMark.setIndexMenu(ConstNpc.MAGIC_TREE_UPGRADE);
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(MagicTree.class, e);
        }
    }

    public void harvestPea() {
        if (this.currPeas > 0) {
            byte currPeasTemp = (byte) this.currPeas;
            this.currPeas = (byte) InventoryService.gI().addPeaHarvest(player, this.level, this.currPeas);
            if (this.currPeas == currPeasTemp) {
                return;
            }
            this.lastTimeHarvest = System.currentTimeMillis();
            InventoryService.gI().sendItemBags(player);
            InventoryService.gI().sendItemBox(player);
            try (Message msg = new Message(-34)) {
                msg.writer().writeByte(2);
                msg.writer().writeShort(this.currPeas);
                msg.writer().writeInt(getSecondPea());
                player.sendMessage(msg);
            } catch (Exception e) {
                Log.error(MagicTree.class, e);
            }
        }
    }

    public void showConfirmUpgradeMagicTree() {
        NpcService.gI().createOtherMenu(player, ConstNpc.DAU_THAN, 
            ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE, 
            "Bạn có chắc chắn nâng cấp cây đậu?", 
            "OK", "Từ chối");
    }

    public void showConfirmUnuppgradeMagicTree() {
        NpcService.gI().createOtherMenu(player, ConstNpc.DAU_THAN,
            ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE,
            "Bạn có chắc chắn hủy nâng cấp cây đậu?",
            "OK", "Từ chối");
    }

    public void upgradeMagicTree() {
        short gold = PEA_UPGRADE[this.level - 1][3];
        int goldRequire = gold * (this.level <= 3 ? 1000 : 1000000);
        if (this.player.inventory.gold < goldRequire) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ vàng để nâng cấp, còn thiếu "
                    + (goldRequire - this.player.inventory.gold) + " vàng nữa");
        } else {
            this.player.inventory.gold -= goldRequire;
            PlayerService.gI().sendInfoHpMpMoney(this.player);
            this.isUpgrade = true;
            this.lastTimeUpgrade = System.currentTimeMillis();
            this.loadMagicTree();
        }
    }

    public void unupgradeMagicTree() {
        short gold = PEA_UPGRADE[this.level - 1][3];
        int goldReturn = (gold * (this.level <= 3 ? 1000 : 1000000)) / 2;
        this.player.inventory.gold += goldReturn;
        PlayerService.gI().sendInfoHpMpMoney(this.player);
        this.isUpgrade = false;
        this.loadMagicTree();
    }

    public void fastRespawnPea() {
        this.currPeas = getMaxPea();
        this.loadMagicTree();
    }

    public void fastUpgradeMagicTree() {
        if (this.level < MAX_LEVEL) {
            this.level++;
        }
        this.isUpgrade = false;
        this.loadMagicTree();
    }

    private byte getMaxPea() {
        return (byte) ((this.level - 1) * 2 + 5);
    }

    private short getSecondPerPea() {
        return (short) (this.level * 60);
    }

    private int getSecondPea() {
        short secondPerPea = getSecondPerPea();
        long timePeaRelease = lastTimeHarvest + secondPerPea * 1000L;
        int secondLeft = (int) ((timePeaRelease - System.currentTimeMillis()) / 1000);
        return Math.max(0, secondLeft);
    }

    private int getSecondUpgrade() {
        return (int) ((lastTimeUpgrade + getTimeUpgrade() - System.currentTimeMillis()) / 1000);
    }

    private String getTextMenuUpgrade() {
        StringBuilder text = new StringBuilder("Nâng cấp\n");
        short d = PEA_UPGRADE[this.level - 1][0];
        short h = PEA_UPGRADE[this.level - 1][1];
        short m = PEA_UPGRADE[this.level - 1][2];
        short gold = PEA_UPGRADE[this.level - 1][3];
        
        if (d != 0) {
            text.append(d).append("d");
        }
        if (h != 0) {
            text.append(h).append("h");
        }
        if (m != 0) {
            text.append(m).append("'");
        }
        text.append("\n").append(gold).append(this.level <= 3 ? " k" : " Tr").append("\nvàng");
        return text.toString();
    }

    private long getTimeUpgrade() {
        short d = PEA_UPGRADE[this.level - 1][0];
        short h = PEA_UPGRADE[this.level - 1][1];
        short m = PEA_UPGRADE[this.level - 1][2];
        return d * 24 * 60 * 60 * 1000L + h * 60 * 60 * 1000L + m * 60 * 1000L;
    }

    public void dispose() {
        // Cleanup resources if needed
    }
}
