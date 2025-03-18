package com.nro.nro_online.models.skill;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.Service;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class PlayerSkill {
    private final Player player;
    public final List<Skill> skills;
    public Skill skillSelect;
    private final Timer timer;
    public byte[] skillShortCut = new byte[10];
    public boolean prepareQCKK;
    public boolean prepareTuSat;
    public boolean prepareLaze;
    public long lastTimeUseQCKK;
    public long lastTimePrepareTuSat;

    public PlayerSkill(Player player) {
        this.player = player;
        this.skills = Collections.synchronizedList(new ArrayList<>());
        this.timer = new Timer();
    }

    public Skill getSkillbyId(int id) {
        return skills.stream()
                .filter(skill -> skill.template.id == id)
                .findFirst()
                .orElse(null);
    }

    public void sendSkillShortCut() {
        Service service = Service.getInstance();
        try (Message msg = service.messageSubCommand((byte) 61)) {
            msg.writer().writeUTF("KSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
        } catch (IOException ignored) {}

        try (Message msg = service.messageSubCommand((byte) 61)) {
            msg.writer().writeUTF("OSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
        } catch (IOException ignored) {}
    }

    public void sendSkillShortCutNew() {
        try (Message msg = Service.getInstance().messageSubCommand(Cmd.CHANGE_ONSKILL)) {
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
        } catch (IOException ignored) {}
    }

    public byte getIndexSkillSelect() {
        if (skillSelect == null) return 3;
        int skillId = skillSelect.template.id;

        if (skillId == Skill.DRAGON || skillId == Skill.DEMON || skillId == Skill.GALICK ||
                skillId == Skill.KAIOKEN || skillId == Skill.LIEN_HOAN) {
            return (byte) 1;
        }
        if (skillId == Skill.KAMEJOKO || skillId == Skill.ANTOMIC || skillId == Skill.MASENKO) {
            return (byte) 2;
        }
        return (byte) 3;
    }

    public byte getSizeSkill() {
        return (byte) skills.stream()
                .filter(skill -> skill.skillId != -1)
                .count();
    }

    public void dispose() {
        timer.cancel();
        skills.clear();
        skillSelect = null;
    }
}