package com.nro.nro_online.models.map.challenge;

import com.nro.nro_online.consts.*;
import com.nro.nro_online.event.Event;
import com.nro.nro_online.event.SummerEvent;
import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.dhvt.*;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.*;
import com.nro.nro_online.services.func.ChangeMapService;
import com.nro.nro_online.utils.Util;
import lombok.Getter;
import lombok.Setter;

public class MartialCongress {

    @Setter @Getter private Player player;
    @Setter private Boss boss;
    @Setter private Player npc;

    @Setter private int time = 185;
    private int round = 0;
    @Setter private int timeWait;

    public void update() {
        if (timeWait > 0) {
            handleCountdown();
            timeWait--;
            return;
        }

        if (time > 0) {
            time--;
            if (player == null || player.zone == null || player.isDie()) {
                endChallenge();
            } else if (boss.isDie()) {
                round++;
                boss.leaveMap();
                toTheNextRound();
            } else if (player.location.y > 264 && time > 10) {
                leave();
            }
        } else {
            timeOut();
        }
    }

    private void handleCountdown() {
        switch (timeWait) {
        case 10 -> Service.getInstance().chat(npc, "Trận " + player.name + " vs " + boss.name + " sắp bắt đầu, hồi hộp ghê! 😱");
        case 8 -> Service.getInstance().chat(npc, "Khán giả đâu, vỗ tay cái nào cho nóng! 👏");
        case 4 -> Service.getInstance().chat(npc, "Ngồi yên nào, 3 giây nữa là đấm nhau! 👊");
        case 2 -> Service.getInstance().chat(npc, "Bắt đầu! Choảng nhau đi! 💥");
        case 1 -> {
            Service.getInstance().chat(player, "Sẵn sàng đập boss đây! 💪");
            Service.getInstance().chat(boss, "Tao không sợ mày đâu! 😤");
        }
        default -> {}
        }
    }

    public void ready() {
        EffectSkillService.gI().startStun(boss, System.currentTimeMillis(), 10000);
        EffectSkillService.gI().startStun(player, System.currentTimeMillis(), 10000);
        ItemTimeService.gI().sendItemTime(player, 3779, 10);
        Util.setTimeout(() -> {
            MartialCongressService.gI().sendTypePK(player, boss);
            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_PVP);
            boss.setStatus((byte) 3);
        }, 10000);
    }

    public void toTheNextRound() {
        PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
        Boss nextBoss = createBossForRound(round);
        if (nextBoss == null) {
            champion();
            return;
        }

        PlayerService.gI().setPos(player, 335, 264, 0);
        setBoss(nextBoss);
        setTime(185);
        setTimeWait(11);
        resetSkill();
    }

    private Boss createBossForRound(int round) {
        return switch (round) {
            case 0 -> new SoiHecQuyn(player);
            case 1 -> new ODo(player);
            case 2 -> new Xinbato(player);
            case 3 -> new ChaPa(player);
            case 4 -> new PonPut(player);
            case 5 -> new ChanXu(player);
            case 6 -> new TauPayPay(player);
            case 7 -> new Yamcha(player);
            case 8 -> new JackyChun(player);
            case 9 -> new ThienXinHang(player);
            case 10 -> new LiuLiu(player);
            default -> null;
        };
    }

    private void resetSkill() {
        player.playerSkill.skills.forEach(skill -> skill.lastTimeUseThisSkill = 0);
        Service.getInstance().sendTimeSkill(player);
    }

    private void timeOut() {
        Service.getInstance().sendThongBao(player, "Hết giờ rồi, thua ké nhé! 😂");
        endChallenge();
    }

    private void champion() {
        Service.getInstance().sendThongBao(player, "Chúc mừng " + player.name + " vô địch, pro vãi! 🏆");
        Service.getInstance().sendThongBaoAllPlayer(player.name + " vừa đè bẹp Đại hội võ thuật 23, quá đỉnh! 🎉");
        endChallenge();
    }

    public void leave() {
        setTime(0);
        PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
        EffectSkillService.gI().removeStun(player);
        Service.getInstance().sendThongBao(player, "Nhảy ra khỏi đài, chịu thua hả? 😛");
        endChallenge();
    }

    private void reward() {
        if (player.levelWoodChest < round) {
            player.levelWoodChest = round;
        }
        if (round > 5 && Event.isEvent() && Event.getInstance() instanceof SummerEvent) {
            byte[] rwLimit = player.getRewardLimit();
            if (rwLimit[ConstRewardLimit.QUE_DOT] < 10) {
                rwLimit[ConstRewardLimit.QUE_DOT]++;
                Item queDot = ItemService.gI().createNewItem((short) ConstItem.QUE_DOT, 1);
                InventoryService.gI().addItemBag(player, queDot, 1);
                Service.getInstance().sendThongBao(player, "Nhặt được Que Đốt, cháy hết mình nào! 🔥");
            }
        }
    }

    public void endChallenge() {
        reward();
        PlayerService.gI().hoiSinh(player);
        PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.NON_PK);
        if (player != null && player.zone != null && player.zone.map.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
            Util.setTimeout(() -> ChangeMapService.gI().changeMapNonSpaceship(player, ConstMap.DAI_HOI_VO_THUAT_129, player.location.x, 360), 500);
        }
        if (boss != null) {
            boss.leaveMap();
        }
        MartialCongressManager.gI().remove(this);
    }
}