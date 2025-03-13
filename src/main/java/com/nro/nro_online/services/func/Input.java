package com.nro.nro_online.services.func;

import java.util.HashMap;
import java.util.Map;

import nro.art.ServerLog;
import nro.consts.ConstNpc;
import nro.jdbc.daos.PlayerDAO;
import nro.manager.ChuyenKhoanManager;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.Zone;
import nro.models.npc.Npc;
import nro.models.npc.NpcManager;
import nro.models.player.Inventory;
import nro.models.player.Player;
import nro.server.Client;
import nro.server.io.Message;
import nro.services.*;
import nro.services.card.NapThe;
import nro.utils.Util;

/**
 * Build Arriety
 */
public class Input {

    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 5066;
    public static final int CHOOSE_LEVEL_CDRD = 7700;
    public static final int TANG_NGOC_HONG = 505;
    public static final int ADD_ITEM = 506;
    public static final int SEND_ITEM_OP = 507;
    public static final int TRADE_RUBY = 508;
    public static final int NAP_THE = 509;

    public static final int CHUYEN_KHOAN = 569;

    public static String LOAI_THE;
    public static String MENH_GIA;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;

    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            Player pl = null;
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case NAP_THE:
                    NapThe.SendCard(player, LOAI_THE, MENH_GIA, text[0], text[1]);
                    break;
                case CHANGE_PASSWORD:
                    Service.getInstance().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    GiftService.gI().use(player, text[0]);
                    break;
                case FIND_PLAYER:
                    pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban"},
                                pl);
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME:
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (PlayerDAO.isExistName(text[0])) {
                            Service.getInstance().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            PlayerDAO.saveName(plChanged);
                            Service.getInstance().player(plChanged);
                            Service.getInstance().Send_Caitrang(plChanged);
                            Service.getInstance().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.getInstance().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.getInstance().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                    break;
                case SEND_ITEM_OP:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionBuff = Integer.parseInt(text[2]);
                        int slOptionBuff = Integer.parseInt(text[3]);
                        int slItemBuff = Integer.parseInt(text[4]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";

                            switch (idItemBuff) {
                                case -1:
                                    pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, Inventory.LIMIT_GOLD);
                                    txtBuff += slItemBuff + " vàng\b";
                                    Service.getInstance().sendMoney(player);
                                    ServerLog.logAdmin(pBuffItem.name, slItemBuff);
                                    break;
                                case -2:
                                    pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                    txtBuff += slItemBuff + " ngọc\b";
                                    Service.getInstance().sendMoney(player);
                                    ServerLog.logAdmin(pBuffItem.name, slItemBuff);
                                    break;
                                case -3:
                                    pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                    txtBuff += slItemBuff + " ngọc khóa\b";
                                    Service.getInstance().sendMoney(player);
                                    ServerLog.logAdmin(pBuffItem.name, slItemBuff);
                                    break;
                                default:
                                    Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                    itemBuffTemplate.itemOptions.add(new ItemOption(idOptionBuff, slOptionBuff));
                                    itemBuffTemplate.quantity = slItemBuff;
                                    txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                    InventoryService.gI().addItemBag(pBuffItem, itemBuffTemplate, slItemBuff);
                                    ServerLog.logAdmin(pBuffItem.name, slItemBuff);
                                    InventoryService.gI().sendItemBags(pBuffItem);
                                    break;
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case CHUYEN_KHOAN:
                    try {
                    long money = Long.parseLong(text[0]);
                    String description = Util.generateRandomString();

                    ChuyenKhoanManager.InsertTransaction(player.id, money, description);
                    if (money < 1000 || money > 1_000_000) {
                        Service.getInstance().sendThongBao(player, "Tối thiểu 1000 và tối đa 1000000");
                        break;
                    }
                    Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                    if (npc != null) {
                        npc.createOtherMenu(player, ConstNpc.CONTENT_CHUYEN_KHOAN,
                                "Con đã tạo thành công giao dịch có nội dung "
                                + description
                                + " với số tiền "
                                + Util.numberToMoney(money)
                                + "!\nVui lòng chuyển khoản đến ngân hàng MBBank có số tài khoản 02147019062000 với số tiền và nội dung như trên!", "Đóng", "Quét QR");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Service.getInstance().sendThongBao(player, "Đã có lỗi xảy ra liên hệ với ADMIN Béo toán học để được hỗ trợ");
                }
                break;
                case TRADE_RUBY:
                    int cuantity = Integer.valueOf(text[0]);
                    if (!player.getSession().actived) {
                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản!");
                        break;
                    }
                    if (cuantity < 1000 || cuantity > 500_000) {
                        Service.getInstance().sendThongBao(player, "Tối thiểu 1000 và tối đa 500000");
                        break;
                    }
                    if (player.getSession().vnd < cuantity) {
                        Service.getInstance().sendThongBao(player, "Số dư không đủ vui lòng nạp thêm!\n Web: NROKIMKAN.ONLINE");
                    } else {
                        PlayerDAO.subVND2(player, cuantity);
                        player.inventory.ruby += cuantity;
                        Service.getInstance().sendMoney(player);
                        ServerLog.logTradeRuby(player.name, cuantity);
                        Service.getInstance().sendThongBao(player, "Đã đổi thành công");
                    }
                    break;
                case CHOOSE_LEVEL_BDKB: {
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc chắn muốn tới bản đồ kho báu cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                }
                break;
                case CHOOSE_LEVEL_CDRD: {
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.THAN_VU_TRU, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_CDRD,
                                    "Con có chắc chắn muốn đến con đường rắn độc cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                    }
                }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                break;
                case TANG_NGOC_HONG:
                    pl = Client.gI().getPlayer(text[0]);
                    int numruby = Integer.parseInt((text[1]));
                    if (pl != null) {
                        if (numruby > 0 && player.inventory.ruby >= numruby) {
                            Item item = InventoryService.gI().findVeTangNgoc(player);
                            player.inventory.subRuby(numruby);
                            PlayerService.gI().sendInfoHpMpMoney(player);
                            pl.inventory.ruby += numruby;
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.getInstance().sendThongBao(player, "Tặng Hồng ngọc thành công");
                            Service.getInstance().sendThongBao(pl, "Bạn được " + player.name + " tặng " + numruby + " Hồng ngọc");
                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                            InventoryService.gI().sendItemBags(player);
                        } else {
                            Service.getInstance().sendThongBao(player, "Không đủ Hồng ngọc để tặng");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case ADD_ITEM:
                    short id = Short.parseShort((text[0]));
                    int quantity = Integer.parseInt(text[1]);
                    Item item = ItemService.gI().createNewItem(id);
                    if (item.template.type < 7) {
                        for (int i = 0; i < quantity; i++) {
                            item = ItemService.gI().createNewItem(id);
                            RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
                            InventoryService.gI().addItemBag(player, item, 0);
                        }
                    } else {
                        item.quantity = quantity;
                        InventoryService.gI().addItemBag(player, item, 0);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Bạn nhận được " + item.template.name + " Số lượng: " + quantity);
            }
        } catch (Exception e) {
        }
    }

    public void createFormChuyenKhoan(Player pl) {
        createForm(pl, CHUYEN_KHOAN, "Nhập số tiền muốn nạp", new SubInput("Số tiền", NUMERIC));
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi mật khẩu", new SubInput("Mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "GIFTCODE", new SubInput("Nhập mã giftcode", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void createFormSenditem1(Player pl) {
        createForm(pl, SEND_ITEM_OP, "SEND Vật Phẩm Option",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormTradeRuby(Player pl) {
        createForm(pl, TRADE_RUBY, "Tỉ lệ quy đổi: 1 vnđ = 1 HN \n Số dư hiện tại: " + pl.getSession().vnd, new SubInput("Số lượng", NUMERIC));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormChooseLevelCDRD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_CDRD, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormTangRuby(Player pl) {
        createForm(pl, TANG_NGOC_HONG, "Tặng ngọc", new SubInput("Tên nhân vật", ANY),
                new SubInput("Số Hồng Ngọc Muốn Tặng", NUMERIC));
    }

    public void createFormAddItem(Player pl) {
        createForm(pl, ADD_ITEM, "Add Item", new SubInput("ID VẬT PHẨM", NUMERIC),
                new SubInput("SỐ LƯỢNG", NUMERIC));
    }

    public void createFormNapThe(Player pl, String loaiThe, String menhGia) {
        LOAI_THE = loaiThe;
        MENH_GIA = menhGia;
        createForm(pl, NAP_THE, "Nạp thẻ\nLoại thẻ: " + loaiThe + "\nMệnh giá: " + menhGia, new SubInput("Số Seri", ANY), new SubInput("Mã thẻ", ANY));
    }

    public class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }
}
