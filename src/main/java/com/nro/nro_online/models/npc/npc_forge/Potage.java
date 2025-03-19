package com.nro.nro_online.models.npc.npc_forge;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossFactory;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.Service;

import java.util.List;

public class Potage extends Npc {

    private static final int COST_RUBY = 3_000;

    public Potage(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player) || this.mapId != 140)
            return;

        createOtherMenu(player, ConstNpc.BASE_MENU, "text npc", "Gọi Boss\nNhân bản", "Từ chối");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || this.mapId != 140 || !player.iDMark.isBaseMenu() || select != 0)
            return;

        if (!checkRequirements(player))
            return;

        spawnCloneBoss(player);
        deductRuby(player);
    }

    private boolean checkRequirements(Player player) {
        if (player.inventory.ruby < COST_RUBY) {
            npcChat(player, "Nhà ngươi không đủ 3k ruby");
            return false;
        }
        if (!player.getSession().actived) {
            Service.getInstance().sendThongBao(player, "Bạn chưa phải là thành viên của Ngọc rồng KIMKO");
            return false;
        }
        Boss existingBoss = BossManager.gI().getBossById(BossFactory.CLONE_NHAN_BAN);
        if (existingBoss != null && existingBoss.zone != null) {
            npcChat(player,
                    "Nhà ngươi hãy tiêu diệt Boss lúc trước ngươi vừa gọi ra ở khu vực :" + existingBoss.zone.zoneId);
            return false;
        }
        return true;
    }

    private void spawnCloneBoss(Player player) {
        BossData data = getDataBoss(player);
        Boss nhaban = BossFactory.createBossNhanBan(player, data);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                nhaban.typePk = ConstPlayer.PK_ALL;
                PlayerService.gI().sendTypePk(nhaban);
            } catch (Exception e) {
                e.printStackTrace();
            }
            nhaban.setStatus((byte) 3);
        }).start();
    }

    private void deductRuby(Player player) {
        player.inventory.ruby -= COST_RUBY;
        Service.getInstance().sendMoney(player);
    }

    public static BossData getDataBoss(Player pl) {
        List<Skill> skills = pl.playerSkill.skills.stream().filter(s -> s != null && s.point > 0).toList();
        int[][] skillTemp = new int[skills.size()][3];
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            skillTemp[i] = new int[] { skill.template.id, skill.point, skill.coolDown };
        }
        return new BossData(
                "Nhân Bản " + pl.name, pl.gender, Boss.DAME_NORMAL, Boss.HP_NORMAL, pl.nPoint.dame,
                new int[][] { { pl.nPoint.hpMax } }, new short[] { pl.getHead(), pl.getBody(), pl.getLeg() },
                new short[] { 139 }, skillTemp, 0);
    }
}