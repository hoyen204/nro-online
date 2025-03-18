package com.nro.nro_online.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.models.PartManager;
import com.nro.nro_online.models.item.HeadAvatar;
import com.nro.nro_online.models.map.MapTemplate;
import com.nro.nro_online.models.mob.MobTemplate;
import com.nro.nro_online.models.npc.NpcTemplate;
import com.nro.nro_online.models.skill.NClass;
import com.nro.nro_online.models.skill.Skill;
import com.nro.nro_online.models.skill.SkillTemplate;
import com.nro.nro_online.power.Caption;
import com.nro.nro_online.power.CaptionManager;
import com.nro.nro_online.resources.Resources;
import com.nro.nro_online.server.Manager;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.FileIO;
import com.nro.nro_online.utils.Log;

public class DataGame {
    public static byte vsData = 12;
    public static byte vsMap = 6;
    public static byte vsSkill = 6;
    public static byte vsItem = 40;
    public static String LINK_IP_PORT = "NRO:localhost:14445:0";

    private static final String MOUNT_NUM = "733:1,734:2,735:3,743:4,744:5,746:6,795:7,849:8,897:9,920:10,1092:11,1135:12,1148:13,1278:19,1176:14";
    public static final Map<String, Short> MAP_MOUNT_NUM = new HashMap<>();

    private static final byte[] dart = FileIO.readFile("resources/data/nro/update_data/dart");
    private static final byte[] arrow = FileIO.readFile("resources/data/nro/update_data/arrow");
    private static final byte[] effect = FileIO.readFile("resources/data/nro/update_data/effect");
    private static final byte[] image = FileIO.readFile("resources/data/nro/update_data/image");
    private static final byte[] skill = FileIO.readFile("resources/data/nro/update_data/skill");

    static {
        for (String str : MOUNT_NUM.split(",")) {
            String[] data = str.split(":");
            MAP_MOUNT_NUM.put(data[0], (short) (Short.parseShort(data[1]) + 30000));
        }
        Resources.getInstance().init();
    }

    public static void sendVersionGame(Session session) {
        try (Message msg = Service.getInstance().messageNotMap((byte) 4)) {
            msg.writer().writeByte(vsData);
            msg.writer().writeByte(vsMap);
            msg.writer().writeByte(vsSkill);
            msg.writer().writeByte(vsItem);
            msg.writer().writeByte(0);
            NavigableMap<Long, Caption> captions = CaptionManager.getInstance().getCaptionsByPower();
            msg.writer().writeByte(captions.size());
            for (Caption caption : captions.values()) {
                msg.writer().writeLong(caption.getPower());
            }
            session.sendMessage(msg);
        } catch (IOException e) {
            Log.error(DataGame.class, e);
        }
    }

    public static void updateData(Session session) {
        try (Message msg = new Message(-87)) {
            msg.writer().writeByte(vsData);
            writeData(msg, dart, arrow, effect, image, skill);
            byte[] dataPart = PartManager.getInstance().getData();
            msg.writer().writeInt(dataPart.length);
            msg.writer().write(dataPart);
            session.doSendMessage(msg);
        } catch (Exception e) {
            Log.error(DataGame.class, e);
        }
    }

    private static void writeData(Message msg, byte[]... datas) throws IOException {
        for (byte[] data : datas) {
            msg.writer().writeInt(data.length);
            msg.writer().write(data);
        }
    }

    public static void createMap(Session session) {
        try (Message msg = Service.getInstance().messageNotMap((byte) 6)) {
            msg.writer().writeByte(vsMap);
            msg.writer().writeByte(Manager.MAP_TEMPLATES.length);
            for (MapTemplate temp : Manager.MAP_TEMPLATES)
                msg.writer().writeUTF(temp.name);
            msg.writer().writeByte(Manager.NPC_TEMPLATES.size());
            for (NpcTemplate temp : Manager.NPC_TEMPLATES) {
                msg.writer().writeUTF(temp.name);
                msg.writer().writeShort(temp.head);
                msg.writer().writeShort(temp.body);
                msg.writer().writeShort(temp.leg);
                msg.writer().writeByte(0);
            }
            msg.writer().writeByte(Manager.MOB_TEMPLATES.size());
            for (MobTemplate temp : Manager.MOB_TEMPLATES) {
                msg.writer().writeByte(temp.type);
                msg.writer().writeUTF(temp.name);
                msg.writer().writeInt(temp.hp);
                msg.writer().writeByte(temp.rangeMove);
                msg.writer().writeByte(temp.speed);
                msg.writer().writeByte(temp.dartType);
            }
            session.sendMessage(msg);
        } catch (Exception e) {
            Log.error(DataGame.class, e);
        }
    }

    public static void updateSkill(Session session) {
        try (Message msg = new Message(-28)) {
            msg.writer().writeByte(7);
            msg.writer().writeByte(vsSkill);
            msg.writer().writeByte(0);
            msg.writer().writeByte(Manager.NCLASS.size());
            for (NClass nClass : Manager.NCLASS) {
                msg.writer().writeUTF(nClass.name);
                msg.writer().writeByte(nClass.skillTemplatess.size());
                for (SkillTemplate skillTemp : nClass.skillTemplatess) {
                    writeSkillTemplate(msg, skillTemp);
                }
            }
            session.doSendMessage(msg);
        } catch (Exception e) {
            Log.error(DataGame.class, e);
        }
    }

    private static void writeSkillTemplate(Message msg, SkillTemplate skillTemp) throws IOException {
        msg.writer().writeByte(skillTemp.id);
        msg.writer().writeUTF(skillTemp.name);
        msg.writer().writeByte(skillTemp.maxPoint);
        msg.writer().writeByte(skillTemp.manaUseType);
        msg.writer().writeByte(skillTemp.type);
        msg.writer().writeShort(skillTemp.iconId);
        msg.writer().writeUTF(skillTemp.damInfo);
        msg.writer().writeUTF(skillTemp.description);
        int skillCount = skillTemp.id == 0 ? skillTemp.skillss.size() + 2 : skillTemp.skillss.size();
        msg.writer().writeByte(skillCount);
        for (Skill skill : skillTemp.skillss)
            writeSkill(msg, skill);
        if (skillTemp.id == 0) {
            for (int i = 105; i <= 106; i++)
                writeEmptySkill(msg, i);
        }
    }

    private static void writeSkill(Message msg, Skill skill) throws IOException {
        msg.writer().writeShort(skill.skillId);
        msg.writer().writeByte(skill.point);
        msg.writer().writeLong(skill.powRequire);
        msg.writer().writeShort(skill.manaUse);
        msg.writer().writeInt(skill.skillId == 1 ? 5000 : skill.coolDown);
        msg.writer().writeShort(skill.dx);
        msg.writer().writeShort(skill.dy);
        msg.writer().writeByte(skill.maxFight);
        msg.writer().writeShort(skill.damage);
        msg.writer().writeShort(skill.price);
        msg.writer().writeUTF(skill.moreInfo);
    }

    private static void writeEmptySkill(Message msg, int skillId) throws IOException {
        msg.writer().writeShort(skillId);
        msg.writer().writeByte(0);
        msg.writer().writeLong(0);
        msg.writer().writeShort(0);
        msg.writer().writeInt(0);
        msg.writer().writeShort(0);
        msg.writer().writeShort(0);
        msg.writer().writeByte(0);
        msg.writer().writeShort(0);
        msg.writer().writeShort(0);
        msg.writer().writeUTF("");
    }

    public static void sendDataImageVersion(Session session) {
        try (Message msg = new Message(-111)) {
            msg.writer().write(
                    FileIO.readFile("resources/data/nro/data_img_version/x" + session.zoomLevel + "/img_version"));
            session.doSendMessage(msg);
        } catch (Exception e) {
            Log.error(DataGame.class, e);
        }
    }

    public static void sendDataItemBG(Session session) {
        try (Message msg = new Message(Cmd.ITEM_BACKGROUND)) {
            msg.writer().write(FileIO.readFile("resources/bg_data"));
            session.sendMessage(msg);
        } catch (Exception e) {
            Log.error(DataGame.class, e);
        }
    }

    public static void sendTileSetInfo(Session session) {
        try (Message msg = new Message(-82)) {
            msg.writer().write(FileIO.readFile("resources/data/nro/map/tile_set_info"));
            session.sendMessage(msg);
        } catch (Exception e) {
            Log.error(DataGame.class, e);
        }
    }

    public static void sendMapTemp(Session session, int id) {
        try (Message msg = Service.getInstance().messageNotMap(Cmd.REQUEST_MAPTEMPLATE)) {
            msg.writer().write(FileIO.readFile("resources/map/" + id));
            session.sendMessage(msg);
        } catch (Exception e) {
            Log.error(DataGame.class, e);
        }
    }

    public static void sendHeadAvatar(Message msg) {
        try {
            msg.writer().writeShort(Manager.HEAD_AVATARS.size());
            for (HeadAvatar ha : Manager.HEAD_AVATARS) {
                msg.writer().writeShort(ha.headId);
                msg.writer().writeShort(ha.avatarId);
            }
        } catch (IOException e) {
            Log.error(DataGame.class, e);
        }
    }

    public static void sendLinkIP(Session session) {
        try (Message msg = new Message(-29)) {
            msg.writer().writeByte(2);
            msg.writer().writeUTF(LINK_IP_PORT + ",0,0");
            msg.writer().writeByte(1);
            session.sendMessage(msg);
        } catch (IOException e) {
            Log.error(DataGame.class, e);
        }
    }
}