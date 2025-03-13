package com.nro.nro_online.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.consts.Cmd;
import com.nro.nro_online.resources.entity.EffectData;
import com.nro.nro_online.resources.entity.ImageByName;
import com.nro.nro_online.resources.entity.MobData;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.server.io.Session;
import com.nro.nro_online.utils.FileIO;
import com.nro.nro_online.utils.FileUtils;
import com.nro.nro_online.utils.Log;

public class Resources {

    private static final Resources instance = new Resources();
    private static final int VS_RES = 19061997;

    public static Resources getInstance() {
        return instance;
    }

    private final List<AbsResources> resourceses = new ArrayList<>();

    public Resources() {
        resourceses.add(new RNormal());
        // resourceses.add(new RSpecial());
    }

    public void init() {
        resourceses.forEach(AbsResources::init);
    }

    public byte[] readAllBytes(byte type, String path) {
        return find(type).readAllBytes(path);
    }

    public List<String> readAllLines(byte type, String path) {
        return find(type).readAllLines(path);
    }

    public AbsResources find(int type) {
        return (type == 5 && resourceses.size() >= 2) ? resourceses.get(1) : resourceses.get(0);
    }

    public void downloadResources(Session session, Message ms) {
        try {
            if (ms.reader().readByte() == 1) {
                AbsResources res = find(session.typeClient);
                if (res != null) {
                    File root = new File(res.getFolder(), "data/" + session.zoomLevel);
                    List<File> datas = new ArrayList<>();
                    FileUtils.addPath(datas, root);
                    sendNumberOfFiles(session, (short) datas.size());
                    datas.forEach(file -> fileTransfer(session, root, file));
                    fileTransferCompleted(session);
                }
            }
        } catch (IOException e) {
            Log.error(Resources.class, e);
        }
    }

    private void sendNumberOfFiles(Session session, short size) {
        try (Message msg = new Message(Cmd.GET_IMAGE_SOURCE)) {
            msg.writer().writeByte(1);
            msg.writer().writeShort(size);
            session.sendMessage(msg);
        } catch (IOException e) {
            Log.error(Resources.class, e);
        }
    }

    private void fileTransferCompleted(Session session) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            try (Message msg = new Message(Cmd.GET_IMAGE_SOURCE)) {
                msg.writer().writeByte(3);
                msg.writer().writeInt(res.getDataVersion()[session.zoomLevel - 1]);
                session.sendMessage(msg);
            } catch (IOException e) {
                Log.error(Resources.class, e);
            }
        }
    }

    public void sendResVersion(Session session) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            try (Message msg = new Message(Cmd.GET_IMAGE_SOURCE)) {
                msg.writer().writeByte(0);
                msg.writer().writeInt(res.getDataVersion()[session.zoomLevel - 1]);
                session.sendMessage(msg);
            } catch (IOException e) {
                Log.error(Resources.class, e);
            }
        }
    }

    private void fileTransfer(Session session, File root, File file) {
        try (Message msg = new Message(Cmd.GET_IMAGE_SOURCE)) {
            String path = file.getPath().replace(root.getPath(), "").replace("\\", "/");
            path = FileUtils.cutPng(path);
            msg.writer().writeByte(2);
            msg.writer().writeUTF(path);
            byte[] data = Files.readAllBytes(file.toPath());
            msg.writer().writeInt(data.length);
            msg.writer().write(data);
            session.sendMessage(msg);
        } catch (IOException e) {
            Log.error(Resources.class, e);
        }
    }

    public void downloadIconData(Session session, int id) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            try (Message msg = new Message(Cmd.REQUEST_ICON)) {
                byte[] data = res.getRawIconData(session.zoomLevel, id);
                msg.writer().writeInt(id);
                msg.writer().writeInt(data.length);
                msg.writer().write(data);
                session.sendMessage(msg);
            } catch (IOException e) {
                Log.error(Resources.class, e);
            }
        }
    }

    public void downloadBGTemplate(Session session, int id) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            try (Message msg = new Message(Cmd.BACKGROUND_TEMPLATE)) {
                byte[] data = res.getRawBGData(session.zoomLevel, id);
                msg.writer().writeShort(id);
                msg.writer().writeInt(data.length);
                msg.writer().write(data);
                session.sendMessage(msg);
            } catch (IOException e) {
                Log.error(Resources.class, e);
            }
        }
    }

    public void sendSmallVersion(Session session) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            try (Message msg = new Message(Cmd.SMALLIMAGE_VERSION)) {
                byte[] data = res.getSmallVersion()[session.zoomLevel - 1];
                msg.writer().writeShort(data.length);
                msg.writer().write(data);
                session.sendMessage(msg);
            } catch (IOException e) {
                Log.error(Resources.class, e);
            }
        }
    }

    public void sendBGVersion(Session session) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            try (Message msg = new Message(Cmd.BGITEM_VERSION)) {
                byte[] data = res.getBackgroundVersion()[session.zoomLevel - 1];
                msg.writer().writeShort(data.length);
                msg.writer().write(data);
                session.sendMessage(msg);
            } catch (IOException e) {
                Log.error(Resources.class, e);
            }
        }
    }

    public void downloadIBN(Session session, String filename) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            ImageByName ibn = res.getIBN(filename);
            if (ibn != null) {
                try (Message msg = new Message(Cmd.GET_IMG_BY_NAME)) {
                    byte[] data = res.getRawIBNData(session.zoomLevel, filename);
                    msg.writer().writeUTF(ibn.getFilename());
                    msg.writer().writeByte(ibn.getNFrame()); // Fix typo: nFame -> nFrame
                    msg.writer().writeInt(data.length);
                    msg.writer().write(data);
                    session.sendMessage(msg);
                } catch (IOException e) {
                    Log.error(Resources.class, e);
                }
            }
        }
    }

    public void loadMobData(Session session, int id) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            MobData mob = res.getMobData(id);
            if (mob != null) {
                try (Message msg = new Message(Cmd.REQUEST_NPCTEMPLATE)) {
                    byte[] data = mob.getDataMob();
                    byte[] imgData = res.getRawMobData(session.zoomLevel, id);
                    msg.writer().writeByte(mob.getId());
                    msg.writer().writeByte(mob.getType());
                    msg.writer().writeInt(data.length);
                    msg.writer().write(data);
                    msg.writer().writeInt(imgData.length);
                    msg.writer().write(imgData);
                    msg.writer().writeByte(mob.getTypeData());
                    if (mob.getTypeData() == 1 || mob.getTypeData() == 2) {
                        byte[][] frameBoss = mob.getFrameBoss();
                        msg.writer().writeByte(frameBoss.length);
                        for (byte[] frame : frameBoss) {
                            msg.writer().writeByte(frame.length);
                            msg.writer().write(frame);
                        }
                    }
                    session.sendMessage(msg);
                } catch (IOException e) {
                    Log.error(Resources.class, e);
                }
            }
        }
    }

    public static void sendEffectTemplate(Session session, int id) {
        try (Message msg = new Message(-66)) {
            byte[] data = FileIO.readFile("Eff/effdata/x" + session.zoomLevel + "/" + id);
            msg.writer().write(data);
            session.sendMessage(msg);
        } catch (IOException e) {
            Log.error(Resources.class, e);
        }
    }

    public static void effData(Session session, int id, int... idTemp) {
        int effId = idTemp.length > 0 && idTemp[0] != 0 ? idTemp[0] : id;
        try (Message msg = new Message(-66)) {
            byte[] effData = FileIO.readFile("Eff/effect/x" + session.zoomLevel + "/data/DataEffect_" + effId);
            byte[] effImg = FileIO.readFile("Eff/effect/x" + session.zoomLevel + "/img/ImgEffect_" + effId + ".png");
            msg.writer().writeShort(id);
            msg.writer().writeInt(effData.length);
            msg.writer().write(effData);
            msg.writer().writeByte(0);
            msg.writer().writeInt(effImg.length);
            msg.writer().write(effImg);
            session.sendMessage(msg);
        } catch (IOException e) {
            Log.error(Resources.class, e);
        }
    }

    public static void requestMobTemplate(Session session, int id) {
        try (Message msg = new Message(11)) {
            byte[] mob = FileIO.readFile("mob/x" + session.zoomLevel + "/" + id);
            if (mob != null) {
                if (id < 82) msg.writer().writeByte(id);
                else if (id == 82) msg.writer().writeByte(0);
                msg.writer().write(mob);
                session.sendMessage(msg);
            }
        } catch (IOException e) {
            Log.error(Resources.class, e, "Mob lỗi: " + id);
        }
    }

    public void loadEffectData(Session session, int id) {
        AbsResources res = find(session.typeClient);
        if (res != null) {
            try {
                int effId = adjustEffectId(session, id);
                EffectData eff = res.getEffectData(effId);
                if (eff != null) {
                    try (Message msg = new Message(Cmd.GET_EFFDATA)) {
                        byte[] data = eff.getData(session.version);
                        byte[] imgData = res.getRawEffectData(session.zoomLevel, effId);
                        msg.writer().writeShort(id);
                        msg.writer().writeInt(data.length);
                        msg.writer().write(data);
                        if (session.isVersionAbove(220)) msg.writer().writeByte(eff.getType());
                        msg.writer().writeInt(imgData.length);
                        msg.writer().write(imgData);
                        session.sendMessage(msg);
                    }
                }
            } catch (IOException e) {
                Log.error(Resources.class, e);
            }
        }
    }

    private int adjustEffectId(Session session, int id) {
        if (id == 25 && session.player != null && session.player.zone != null) {
            byte effDragon = session.player.zone.effDragon;
            if (effDragon != -1) {
                int adjustedId = effDragon;
                if (adjustedId == 60 && !session.isVersionAbove(220)) adjustedId = 61;
                return adjustedId;
            }
        }
        return id;
    }
}