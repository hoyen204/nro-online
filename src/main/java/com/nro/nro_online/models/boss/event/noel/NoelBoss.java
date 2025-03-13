package com.nro.nro_online.models.boss.event.noel;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.boss.Boss;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.boss.BossManager;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.MapService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.services.MapService;
import nro.services.Service;
import nro.utils.Util;

public class NoelBoss extends Boss {

    protected List<Player> players = new ArrayList<>();

    protected static final int _15_PHUT = 900;

    public NoelBoss(long id, BossData data) {
        super(id, data);
    }

    @Override
    public void rewards(Player pl) {
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        if (countChangePlayerAttack < targetCountChangePlayerAttack && plAttack != null && plAttack.zone != null && plAttack.zone.equals(this.zone)) {
            if (!plAttack.isDie() && !plAttack.effectSkin.isVoHinh && !plAttack.isMiniPet) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            try {
                if (plAttack != null && !plAttack.isDie() && plAttack.effectSkin.isVoHinh) {
                    plAttack = null;
                }
                this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
                this.countChangePlayerAttack = 0;
                if (this.zone != null) {
                    plAttack = this.zone.getRandomPlayerInMap(this.players);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Loi Bosss:" + this.name);
            }
        }
        return plAttack;
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    public Boolean AddPlayerCanAttack(Player player) {
        if (players == null) {
            players = new ArrayList<>();
        }

        if (players.contains(player)) {
            return false;
        } else {
            players.add(player);
            return true;
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!players.contains(plAtt)) {
            Service.getInstance().sendThongBao(plAtt, "Tấn công vô hiệu do bạn chưa rửa tay");
            return 0;
        }

        int dame = 0;
        if (this.isDie()) {
            return dame;
        } else {
            dame = super.injured(plAtt, damage, piercing, isMobAttack);
            if (this.isDie()) {
                rewards(plAtt);
                notifyPlayeKill(plAtt);
                die();
            }
            return dame;
        }
    }

    @Override
    public void initTalk() {
    }

    @Override
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.nPoint.hp <= 0) {
            Service.getInstance().sendThongBao(pl, "Hãy quay lại khi mạnh hơn");
            Player player = this.zone.getRandomPlayerInMap();
            if (player != null) {
                this.players.clear();
                AddPlayerCanAttack(player);
            } else {
                leaveMap();
            }
        }
    }

    @Override
    public void joinMap() {
        try {
            if (this.zone != null) {
                MapService.gI().goToMap(this, this.zone);
            } else {
                BossManager.gI().removeBoss(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
