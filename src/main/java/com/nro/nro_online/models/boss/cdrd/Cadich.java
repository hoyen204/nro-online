package com.nro.nro_online.models.boss.cdrd;

import com.nro.nro_online.consts.ConstPlayer;
import com.nro.nro_online.models.boss.BossData;
import com.nro.nro_online.models.map.dungeon.SnakeRoad;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.services.SkillService;
import com.nro.nro_online.services.func.ChangeMapService;

public class Cadich extends CBoss {

    private boolean transformed;

    public Cadich(long id, short x, short y, SnakeRoad dungeon, BossData data) {
        super(id, x, y, dungeon, data);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {

    }

    @Override
    public void idle() {
        if (zone.getBosses().size() == 1) {
            changeToAttack();
            joinMapIdle = false;
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void changeToAttack() {
        chat("Vĩnh biệt chú mày nhé, Na đíc");
        super.changeToAttack();
    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{"Tuyệt chiêu hủy diệt của môn phái Xayda!"};
        this.textTalkAfter = new String[]{"Tốt lắm! Phi thuyền sẽ đến đón ta"};
    }

    @Override
    public short getHead() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        }
        return super.getHead();
    }

    @Override
    public short getBody() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        }
        return super.getBody();
    }

    @Override
    public short getLeg() {
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        }
        return super.getLeg();
    }

    @Override
    public void update() {
        if (!isDie()) {
            if (!transformed && !this.effectSkill.isMonkey && nPoint.hp <= nPoint.hpMax / 2) {
                transformed = true;
                this.playerSkill.skillSelect = this.getSkillById(Skill.BIEN_KHI);
                SkillService.gI().useSkill(this, null, null);
            }
        }
        super.update();
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().spaceShipArrive(this, (byte) 2, ChangeMapService.TENNIS_SPACE_SHIP);
        super.leaveMap();
    }

}
