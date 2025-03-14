package com.nro.nro_online.services.func;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.item.ItemOption;
import com.nro.nro_online.models.map.Zone;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.npc.NpcManager;
import com.nro.nro_online.models.player.Inventory;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.server.Client;
import com.nro.nro_online.server.io.Message;
import com.nro.nro_online.services.GiftService;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.ItemService;
import com.nro.nro_online.services.NpcService;
import com.nro.nro_online.services.PlayerService;
import com.nro.nro_online.services.RewardService;
import com.nro.nro_online.services.Service;
import com.nro.nro_online.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class Input {

    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<>();

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
    public static final int CHUYEN_KHOAN = 569;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;

    private static Input instance;

    public static Input gI() {
        if (instance == null) {
            instance = new Input();
        }
        return instance;
    }

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
            case CHANGE_PASSWORD:
                Service.getInstance().changePassword(player, text[0], text[1], text[2]);
                break;
            case GIFT_CODE:
                GiftService.gI().use(player, text[0]);
                break;
            case FIND_PLAYER:
                Player pl = Client.gI().getPlayer(text[0]);
                if (pl != null) {
                    NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1,
                            "Ngài muốn làm gì với " + pl.name + "?",
                            new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban"}, pl);
                } else {
                    Service.getInstance().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline 😕");
                }
                break;
            case CHANGE_NAME:
                Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                if (plChanged != null) {
                    if (PlayerDAO.isExistName(text[0])) {
                        Service.getInstance().sendThongBao(player, "Tên này đã có người dùng rồi bro! 😅");
                    } else {
                        plChanged.name = text[0];
                        PlayerDAO.saveName(plChanged);
                        Service.getInstance().player(plChanged);
                        Service.getInstance().Send_Caitrang(plChanged);
                        Service.getInstance().sendFlagBag(plChanged);
                        Zone zone = plChanged.zone;
                        ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                        Service.getInstance().sendThongBao(plChanged, "Tên mới xịn hơn tên cũ nha! 😎");
                        Service.getInstance().sendThongBao(player, "Đổi tên thành công, đẹp lắm! 🌟");
                    }
                }
                break;
            case SEND_ITEM_OP:
                if (player.isAdmin()) {
                    Player pBuffItem = Client.gI().getPlayer(text[0]);
                    if (pBuffItem != null) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionBuff = Integer.parseInt(text[2]);
                        int slOptionBuff = Integer.parseInt(text[3]);
                        int slItemBuff = Integer.parseInt(text[4]);
                        String txtBuff = "Buff cho " + pBuffItem.name + ": ";
                        switch (idItemBuff) {
                        case -1:
                            pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, Inventory.LIMIT_GOLD);
                            txtBuff += slItemBuff + " vàng";
                            break;
                        case -2:
                            pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2_000_000_000);
                            txtBuff += slItemBuff + " ngọc";
                            break;
                        case -3:
                            pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2_000_000_000);
                            txtBuff += slItemBuff + " hồng ngọc";
                            break;
                        default:
                            Item itemBuff = ItemService.gI().createNewItem((short) idItemBuff);
                            itemBuff.itemOptions.add(new ItemOption(idOptionBuff, slOptionBuff));
                            itemBuff.quantity = slItemBuff;
                            InventoryService.gI().addItemBag(pBuffItem, itemBuff, slItemBuff);
                            txtBuff += "x" + slItemBuff + " " + itemBuff.template.name;
                            break;
                        }
                        InventoryService.gI().sendItemBags(pBuffItem);
                        Service.getInstance().sendMoney(pBuffItem);
                        NpcService.gI().createTutorial(player, 24, txtBuff + " 😏");
                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không online đâu! 😛");
                    }
                }
                break;
            case CHUYEN_KHOAN:
                Service.getInstance().sendThongBao(player, "Có lỗi rồi, liên hệ ADMIN Béo toán học nhé! 😭");
                break;
            case TRADE_RUBY:
                int quantity = Integer.parseInt(text[0]);
                if (!player.getSession().actived) {
                    Service.getInstance().sendThongBao(player, "Kích hoạt tài khoản đi đã nha! 😤");
                } else if (quantity < 1000 || quantity > 500_000) {
                    Service.getInstance().sendThongBao(player, "Chỉ được đổi từ 1000 đến 500000 thôi bro! 😅");
                } else if (player.getSession().vnd < quantity) {
                    Service.getInstance().sendThongBao(player, "Không đủ tiền, nạp thêm đi nào! 🌐");
                } else {
                    PlayerDAO.subVND2(player, quantity);
                    player.inventory.ruby += quantity;
                    Service.getInstance().sendMoney(player);
                    Service.getInstance().sendThongBao(player, "Đổi " + quantity + " hồng ngọc thành công! 💎");
                }
                break;
            case CHOOSE_LEVEL_BDKB:
                int levelBDKB = Integer.parseInt(text[0]);
                if (levelBDKB >= 1 && levelBDKB <= 110) {
                    Npc npcBDKB = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                    if (npcBDKB != null) {
                        npcBDKB.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                "Chắc chắn đi kho báu cấp " + levelBDKB + " không con?",
                                new String[]{"Đồng ý", "Từ chối"}, levelBDKB);
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Cấp độ phải từ 1-110 thôi nha! 😕");
                }
                break;
            case CHOOSE_LEVEL_CDRD:
                int levelCDRD = Integer.parseInt(text[0]);
                if (levelCDRD >= 1 && levelCDRD <= 110) {
                    Npc npcCDRD = NpcManager.getByIdAndMap(ConstNpc.THAN_VU_TRU, player.zone.map.mapId);
                    if (npcCDRD != null) {
                        npcCDRD.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_CDRD,
                                "Chắc chắn đi con đường rắn độc cấp " + levelCDRD + " không con?",
                                new String[]{"Đồng ý", "Từ chối"}, levelCDRD);
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Cấp độ phải từ 1-110 thôi nha! 😕");
                }
                break;
            case TANG_NGOC_HONG:
                Player plGift = Client.gI().getPlayer(text[0]);
                int rubyAmount = Integer.parseInt(text[1]);
                if (plGift != null) {
                    if (rubyAmount > 0 && player.inventory.ruby >= rubyAmount) {
                        Item veTangNgoc = InventoryService.gI().findVeTangNgoc(player);
                        if (veTangNgoc != null) {
                            player.inventory.subRuby(rubyAmount);
                            plGift.inventory.ruby += rubyAmount;
                            InventoryService.gI().subQuantityItemsBag(player, veTangNgoc, 1);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendMoney(player);
                            Service.getInstance().sendMoney(plGift);
                            Service.getInstance().sendThongBao(player, "Tặng " + rubyAmount + " hồng ngọc thành công! 🎁");
                            Service.getInstance().sendThongBao(plGift, "Bạn được " + player.name + " tặng " + rubyAmount + " hồng ngọc! 💎");
                        } else {
                            Service.getInstance().sendThongBao(player, "Cần vé tặng ngọc để tặng nha! 😛");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ hồng ngọc để tặng đâu! 😭");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Người này offline rồi bro! 😕");
                }
                break;
            case ADD_ITEM:
                short id = Short.parseShort(text[0]);
                int qty = Integer.parseInt(text[1]);
                Item item = ItemService.gI().createNewItem(id);
                if (item.template.type < 7) {
                    for (int i = 0; i < qty; i++) {
                        item = ItemService.gI().createNewItem(id);
                        RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
                        InventoryService.gI().addItemBag(player, item, 0);
                    }
                } else {
                    item.quantity = qty;
                    InventoryService.gI().addItemBag(player, item, 0);
                }
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Nhận x" + qty + " " + item.template.name + " ngon lành! 😎");
                break;
            }
        } catch (Exception e) {
            // Không log lỗi để tránh yapping
        }
    }

    public void createFormChuyenKhoan(Player pl) {
        createForm(pl, CHUYEN_KHOAN, "Nhập số tiền muốn chuyển", new SubInput("Số tiền", NUMERIC));
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        try (Message msg = new Message(-125)) {
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
            // Không log để gọn
        }
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi mật khẩu", new SubInput("Mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD), new SubInput("Nhập lại", PASSWORD));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Nhập giftcode", new SubInput("Mã giftcode", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void createFormSenditem1(Player pl) {
        createForm(pl, SEND_ITEM_OP, "Buff vật phẩm",
                new SubInput("Tên người chơi", ANY), new SubInput("ID vật phẩm", NUMERIC),
                new SubInput("ID Option", NUMERIC), new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormTradeRuby(Player pl) {
        createForm(pl, TRADE_RUBY, "Đổi hồng ngọc (1 VNĐ = 1 HN)\nSố dư: " + pl.getSession().vnd,
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ kho báu", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormChooseLevelCDRD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_CDRD, "Chọn cấp độ con đường rắn độc", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormTangRuby(Player pl) {
        createForm(pl, TANG_NGOC_HONG, "Tặng hồng ngọc", new SubInput("Tên nhân vật", ANY),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormAddItem(Player pl) {
        createForm(pl, ADD_ITEM, "Thêm vật phẩm", new SubInput("ID vật phẩm", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public static class SubInput {
        private final String name;
        private final byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }
}