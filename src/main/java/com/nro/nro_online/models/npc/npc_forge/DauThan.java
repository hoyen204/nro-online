package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.TaskService;

public class DauThan extends Npc {

    public DauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            player.magicTree.openMenuTree();
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player))
            return;

        TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
        int menuIndex = player.iDMark.getIndexMenu();

        if (menuIndex == ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA) {
            if (select == 0)
                player.magicTree.harvestPea();
            else if (select == 1) {
                if (player.magicTree.level == 10)
                    player.magicTree.fastRespawnPea();
                else
                    player.magicTree.showConfirmUpgradeMagicTree();
            } else if (select == 2)
                player.magicTree.fastRespawnPea();
        } else if (menuIndex == ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA) {
            if (select == 0)
                player.magicTree.harvestPea();
            else if (select == 1)
                player.magicTree.showConfirmUpgradeMagicTree();
        } else if (menuIndex == ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE && select == 0) {
            player.magicTree.upgradeMagicTree();
        } else if (menuIndex == ConstNpc.MAGIC_TREE_UPGRADE) {
            if (select == 0)
                player.magicTree.fastUpgradeMagicTree();
            else if (select == 1)
                player.magicTree.showConfirmUnuppgradeMagicTree();
        } else if (menuIndex == ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE && select == 0) {
            player.magicTree.unupgradeMagicTree();
        }
    }
}
