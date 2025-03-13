package com.nro.nro_online.services.func;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.mob.ArrietyDrop;
import com.nro.nro_online.models.npc.Npc;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.InventoryService;
import com.nro.nro_online.services.Service;

/**
 * @Build Arriety
 */
public class CombineServiceNew {

    private static final int COST_DOI_VE_DOI_DO_HUY_DIET = 500000000;
    private static final int COST_DAP_DO_KICH_HOAT = 20_000;
    private static final int COST_DAP_DO_KICH_HOAT_LINH_THU = 10_000;
    private static final int COST_LEVER = 5000;
    private static final int COST_DOI_MANH_KICH_HOAT = 500000000;
    private static final int COST_GIA_HAN_CAI_TRANG = 500000000;

    private static final int COST_NANG_DO_TS = 1_000_000_000;
    private static final int COST_RUBY_NANG_DO_TS = 15_000;

    private static final int COST_RUBY_TRADE_TL = 5_000;

    private static final int TIME_COMBINE = 500;

    private static final byte MAX_STAR_ITEM = 9;

    private static final byte MAX_SAO_FLAG_BAG = 5;

    private static final byte MAX_LEVEL_ITEM = 7;

    private static final byte OPEN_TAB_COMBINE = 0;
    private static final byte REOPEN_TAB_COMBINE = 1;
    private static final byte COMBINE_SUCCESS = 2;
    private static final byte COMBINE_FAIL = 3;
    private static final byte COMBINE_DRAGON_BALL = 5;
    public static final byte OPEN_ITEM = 6;

    public static final int EP_SAO_TRANG_BI = 500;
    public static final int PHA_LE_HOA_TRANG_BI = 501;
    public static final int CHUYEN_HOA_TRANG_BI = 502;
    public static final int DOI_VE_HUY_DIET = 503;
    public static final int DAP_SET_KICH_HOAT = 504;
    public static final int DOI_MANH_KICH_HOAT = 505;
    public static final int NANG_CAP_VAT_PHAM = 506;
    public static final int NANG_CAP_BONG_TAI = 507;
    public static final int LAM_PHEP_NHAP_DA = 508;
    public static final int NHAP_NGOC_RONG = 509;
    public static final int CHE_TAO_DO_THIEN_SU = 510;
    public static final int DAP_SET_KICH_HOAT_CAO_CAP = 511;
    public static final int GIA_HAN_CAI_TRANG = 512;
    public static final int NANG_CAP_DO_THIEN_SU = 513;
    public static final int PHA_LE_HOA_TRANG_BI_X10 = 514;
    public static final int NANG_CAP_DO_TS = 515;
    public static final int TRADE_DO_THAN_LINH = 516;
    public static final int NANG_CAP_DO_KICH_HOAT = 517;
    public static final int UPGRADE_CAITRANG = 518;
    public static final int MO_CHI_SO_BONG_TAI = 519;
    public static final int UPGRADE_LINHTHU = 520;
    public static final int EP_NGOC_RONG_BANG = 521;
    public static final int UPGRADE_THAN_LINH = 522;
    public static final int UPGRADE_PET = 523;
    public static final int TRADE_PET = 524;
    public static final int PHA_LE_HOA_DISGUISE = 525;
    public static final int NANG_CAP_BONG_TAI_CAP3 = 526;
    public static final int DAP_BONG_TAI_CAP_3 = 527;
    public static final int PHA_LE_HOA_CAI_TRANG = 528;

    private static final int GOLD_MOCS_BONG_TAI = 500_000_000;

    private static final int RUBY_MOCS_BONG_TAI = 10_000;

    private static final int GOLD_BONG_TAI2 = 500_000_000;

    private static final int RUBY_BONG_TAI2 = 20_000;

    private static final int RATIO_NANG_CAP = 44;

    private final Npc baHatMit;
    private final Npc bulmatl;
    private final Npc whis;
    private final Npc tosu;

    private static CombineServiceNew i;

    public CombineServiceNew() {
        this.baHatMit = NpcManager.getNpc(ConstNpc.BA_HAT_MIT);
        this.whis = NpcManager.getNpc(ConstNpc.WHIS);
        this.bulmatl = NpcManager.getNpc(ConstNpc.BUNMA_TL);
        this.tosu = NpcManager.getNpc(ConstNpc.TO_SU_KAIO);
    }

    public static CombineServiceNew gI() {
        if (i == null) {
            i = new CombineServiceNew();
        }
        return i;
    }

    /**
     * Mở tab đập đồ
     *
     * @param player
     * @param type kiểu đập đồ
     */
    public void openTabCombine(Player player, int type) {
        player.combineNew.setTypeCombine(type);
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_TAB_COMBINE);
            msg.writer().writeUTF(getTextInfoTabCombine(type));
            msg.writer().writeUTF(getTextTopTabCombine(type));
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getRationangbt(int lvbt) { // tile dap do chi hat mit
        switch (lvbt) {
            case 1:
                return 40f;

        }
        return 0;
    }

    public void showInfoCombine(Player player, int[] index) {
        player.combineNew.clearItemCombine();
        if (index.length > 0) {
            for (int i = 0; i < index.length; i++) {
                player.combineNew.itemsCombine.add(player.inventory.itemsBag.get(index[i]));
            }
        }
        switch (player.combineNew.typeCombine) {
            case TRADE_PET:
                if (player.combineNew.itemsCombine.isEmpty()) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta x10 Gậy thượng đế và 5k hồng ngọc ta sẽ giúp đệ tử ngươi trở thành đệ tử Whis", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() >= 1) {
                    Item gayTD = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.id == 1231) {
                                gayTD = item;
                            }
                        }
                    }
                    if (player.pet.typePet != ConstPet.SUPER) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ngươi không có đệ tử Black gâu ku", "Đóng");
                        return;
                    }
                    if (gayTD == null) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "thiếu Gậy thượng đế", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Con có muốn nâng cấp đệ tử của con?"
                            + "\n|8|Tỉ lệ mặc định là 10%";
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Nâng cấp", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp!", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case UPGRADE_PET:
                if (player.combineNew.itemsCombine.isEmpty()) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta x10 Đá nguyền ấn và 5k hồng ngọc ta sẽ giúp đệ tử ngươi lên 1 Lever", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() >= 1) {
                    Item da = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.id == 2040) {
                                da = item;
                            }
                        }
                    }
                    if (da == null) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "thiếu Đá nguyền ấn", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Con có muốn nâng cấp đệ tử của con?"
                            + "\n|8|Tỉ lệ mặc định là 50%"
                            + "\n|1|Cần " + Util.numberToMoney(COST_LEVER) + " hồng ngọc";
                    if (player.inventory.ruby < COST_LEVER) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "hết xiền rồi", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST_DAP_DO_KICH_HOAT_LINH_THU) + " hồng ngọc", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp!", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case UPGRADE_THAN_LINH:
                if (player.combineNew.itemsCombine.isEmpty()) {
                    this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 1  món đồ kích hoạt bất kỳ và 1 món đồ Thần Linh", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() >= 1) {
                    Item manhTL = null;
                    Item dokh = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.id >= 555 && item.template.id <= 567) {
                                manhTL = item;
                            } else if (item.isSKHVip()) {
                                dokh = item;
                            }
                        }
                    }
                    if (dokh == null) {
                        this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ Kích Hoạt Thường", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Con có muốn Nâng cấp Đồ KH này thành Đồ KH vip 100% chỉ số set kích hoạt thần linh bất kì không?"
                            + "\n|8|Tỉ lệ mặc định là 10%\n|8|Nếu + thêm x1 món Thần Linh tỉ lệ sẽ là 30%"
                            + "\n|1|Cần " + Util.numberToMoney(COST_DAP_DO_KICH_HOAT_LINH_THU) + " hồng ngọc";
                    if (player.inventory.ruby < COST_DAP_DO_KICH_HOAT_LINH_THU) {
                        this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.tosu.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST_DAP_DO_KICH_HOAT_LINH_THU) + " hồng ngọc", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp!", "Đóng");
                        return;
                    }
                    this.tosu.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case UPGRADE_LINHTHU:
                if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                    this.bulmatl.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn phải có ít nhất 1 ô trống hành trang", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 4) {
                    Item itemLinhThu = null;
                    Item stone = null;
                    Item HLT = null;// hon linh thu
                    Item TTT = null;// thang tinh thach
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            switch (item.template.id) {
                                case 2029:
                                    HLT = item;
                                    break;
                                case 2031:
                                    TTT = item;
                                    break;
                            }
                            if (isLinhThu(item)) {
                                itemLinhThu = item;
                            } else if (isStone(item)) {
                                stone = item;
                            }
                        }
                    }
                    if (player.inventory.ruby < 10_000) {
                        this.bulmatl.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần 10k Ruby", "Đóng");
                        return;
                    }
                    if (itemLinhThu == null) {
                        this.bulmatl.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần linh thú", "Đóng");
                        return;
                    }
                    if (HLT == null || HLT.quantity < 99) {
                        this.bulmatl.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần x99 Hồn Linh Thú", "Đóng");
                        return;
                    }
                    if (TTT == null) {
                        this.bulmatl.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần Thăng Tinh Thạch", "Đóng");
                        return;
                    }
                    if (stone == null) {
                        this.bulmatl.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cần Stone linh thú", "Đóng");
                        return;
                    }
                    String npcSay = "|1|Cậu đang có\n|8|" + itemLinhThu.template.name + "\n"
                            + "|5|" + HLT.quantity + " Hồn linh thú\n"
                            + "|5|" + TTT.quantity + " Thăng tinh thạch\n"
                            + "|5|" + stone.quantity + " " + stone.template.name + "\n"
                            + "|1|Tôi sẽ giúp cậu nâng cấp linh thú này\n"
                            + "|8|Bạn đưa tôi 10 thăng tinh thạch tôi sẽ giúp bạn\n"
                            + "|8|Tôi sẽ giúp bạn có tỉ lệ thành công cao hơn\n"
                            + "|1|Cậu có đồng ý không!";
                    this.bulmatl.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Đồng Ý", "Từ chối");
                } else {
                    this.bulmatl.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn không đủ nguyên liệu", "Đóng");
                }
                break;
            case UPGRADE_CAITRANG:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 1  món đồ kích hoạt thường bất kỳ và x5 mảnh Thần Linh", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item ct = null;
                    Item stole = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.id == 711) {
                                ct = item;
                            } else if (item.template.id == 1260) {
                                stole = item;
                            }
                        }
                    }
                    if (stole == null) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thieu da", "Đóng");
                        return;
                    }
                    if (stole == null && stole.quantity < 99) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thieu da", "Đóng");
                        return;
                    }
                    if (ct == null) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thieu ct", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Con có muốn nâng cấp cải trang  " + ct.template.name
                            + "|1|Cần 10k ruby";
                    if (player.inventory.ruby < 10_000) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Nâng cấp\n10k ruby", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 2) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp!", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NANG_CAP_DO_KICH_HOAT:
                if (player.combineNew.itemsCombine.size() == 0) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy đưa ta 1  món đồ kích hoạt thường bất kỳ và x5 mảnh Thần Linh", "Đóng");
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item manhTL = null;
                    Item dokh = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.id >= 2032 && item.template.id <= 2036) {
                                manhTL = item;
                            } else if (item.isSKHThuong()) {
                                dokh = item;
                            }
                        }
                    }
                    if (manhTL == null || manhTL.quantity < 2) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu mảnh Thần Linh", "Đóng");
                        return;
                    }
                    if (dokh == null) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ Kích Hoạt Thường", "Đóng");
                        return;
                    }
                    String npcSay = "|2|Con có muốn Nâng cấp Đồ KH thường này thành Đồ KH vip 100% chỉ số set bất kì không?\n|7|"
                            + "|1|Cần " + Util.numberToMoney(COST_DAP_DO_KICH_HOAT) + " hồng ngọc";
                    if (player.inventory.ruby < COST_DAP_DO_KICH_HOAT) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Nâng cấp\n" + Util.numberToMoney(COST_DAP_DO_KICH_HOAT) + " hồng ngọc", "Từ chối");
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Nguyên liệu không phù hợp!", "Đóng");
                        return;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;

            case TRADE_DO_THAN_LINH:
                if (player.combineNew.itemsCombine.size() != 1) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu Nguyên liệu", "Đóng");
                    break;
                }
                if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn phải có ít nhất 1 ô trống hành trang", "Đóng");
                    break;
                }
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item doTL = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.id >= 555 && item.template.id <= 567) {
                                doTL = item;
                            }
                        }
                    }
                    if (doTL == null || doTL.quantity < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ rồi ku!", "Đóng");
                        break;
                    }
                    Item itemMTL = ItemService.gI().createNewItem((short) Util.nextInt(2032, 2036));
                    String npcSay = "Chế tạo " + itemMTL.template.name + "\n"
                            + "Trang bị Thần Linh sẽ được chuyển hóa\n"
                            + "Sang mảnh thần linh\n"
                            + "Mảnh Thần Linh dùng để nâng cấp SKH 100%\n"
                            + "Tỉ lệ thành công: 100%\n"
                            + "Phí nâng cấp: " + Util.numberToMoney(COST_RUBY_TRADE_TL) + " ruby";
                    if (player.inventory.ruby < COST_RUBY_TRADE_TL) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nNạp tiền vào đay Donate cho taoo", "Đóng");
                        break;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                            npcSay, "Nâng cấp", "Từ chối");
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case EP_SAO_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item trangBi = null;
                    Item daPhaLe = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (isTrangBiEpSaoPhale(item)) {
                            trangBi = item;
                        } else if (isDaPhaLe(item)) {
                            daPhaLe = item;
                        }
                    }
                    int star = 0; // sao pha lê đã ép
                    int starEmpty = 0; // lỗ sao pha lê
                    if (trangBi != null && daPhaLe != null) {
                        for (ItemOption io : trangBi.itemOptions) {
                            if (io.optionTemplate.id == 102) {
                                star = io.param;
                            } else if (io.optionTemplate.id == 107) {
                                starEmpty = io.param;
                            }
                        }
                        if (star < starEmpty) {
                            player.combineNew.gemCombine = getGemEpSao(star);
                            String npcSay = trangBi.template.name + "\n|2|";
                            for (ItemOption io : trangBi.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            if (daPhaLe.template.type == 30) {
                                for (ItemOption io : daPhaLe.itemOptions) {
                                    npcSay += "|7|" + io.getOptionString() + "\n";
                                }
                            } else {
                                npcSay += "|7|" + ItemService.gI().getItemOptionTemplate(getOptionDaPhaLe(daPhaLe)).name
                                        .replaceAll("#", getParamDaPhaLe(daPhaLe) + "") + "\n";
                            }
                            npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.gemCombine) + " ngọc";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc");

                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Cần 1 trang bị có lỗ sao pha lê và 1 loại đá pha lê để ép vào", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 trang bị có lỗ sao pha lê và 1 loại đá pha lê để ép vào", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 trang bị có lỗ sao pha lê và 1 loại đá pha lê để ép vào", "Đóng");
                }
                break;
            case PHA_LE_HOA_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (isTrangBiPhaLeHoa(item)) {
                        int star = 0;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 107) {
                                star = io.param;
                                break;
                            }
                        }
                        if (star < MAX_STAR_ITEM) {
                            player.combineNew.goldCombine = getGoldPhaLeHoa(star);
                            player.combineNew.gemCombine = getGemPhaLeHoa(star);
                            player.combineNew.ratioCombine = getRatioPhaLeHoa(star);

                            String npcSay = item.template.name + "\n|2|";
                            for (ItemOption io : item.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            if (star == 8) {
                                npcSay += "|7|Tỉ lệ thành công: 1%\nCần 200 ruby";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần 200 ruby");
                            } else if (player.combineNew.goldCombine <= player.inventory.gold) {
                                npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc");
                            } else {
                                npcSay += "Còn thiếu "
                                        + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                        + " vàng";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Vật phẩm đã đạt tối đa sao pha lê", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể đục lỗ",
                                "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy hãy chọn 1 vật phẩm để pha lê hóa",
                            "Đóng");
                }
                break;
            case PHA_LE_HOA_CAI_TRANG:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (isItemCaiTrang(item)) {
                        int star = 0;
                        Item hoa = null;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 107) {
                                star = io.param;
                                break;
                            }
                        }
                        for (Item it : player.combineNew.itemsCombine) {
                            if (it.isNotNullItem()) {
                                switch (it.template.id) {
                                    case 2063:
                                        hoa = it;
                                        break;
                                }
                            }
                        }
                        if (star < MAX_SAO_FLAG_BAG) {
                            int ruby = 10_000;
                            String npcSay = item.template.name + "\n|2|";
                            for (ItemOption io : item.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            if (hoa == null || hoa.quantity < 999) {
                                npcSay += "|1|Cần x99 Hoa cúc vàng";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay,
                                        "Đóng");
                            } else if (player.inventory.ruby < ruby) {
                                npcSay += "thiếu ruby";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            } else {
                                npcSay += "|1|Tỉ lệ thành công 20%\nCần " + Util.numberToMoney(ruby) + " ruby";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần 10k ruby");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Vật phẩm đã đạt tối đa sao pha lê", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể đục lỗ",
                                "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy hãy chọn 1 vật phẩm để pha lê hóa",
                            "Đóng");
                }
                break;
            case PHA_LE_HOA_DISGUISE:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (isItemPhaLeHoa(item)) {
                        int star = 0;
                        Item capsule = null;
                        for (ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 107) {
                                star = io.param;
                                break;
                            }
                        }
                        for (Item it : player.combineNew.itemsCombine) {
                            if (it.isNotNullItem()) {
                                switch (it.template.id) {
                                    case 869:
                                        capsule = it;
                                        break;
                                }
                            }
                        }
                        if (star < MAX_SAO_FLAG_BAG) {
                            int ruby = 10_000;
                            String npcSay = item.template.name + "\n|2|";
                            for (ItemOption io : item.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            if (capsule == null || capsule.quantity < 9) {
                                npcSay += "|1|Cần x9 capsule 1 Sao";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay,
                                        "Đóng");
                            } else if (player.inventory.ruby < ruby) {
                                npcSay += "thiếu ruby";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            } else {
                                npcSay += "|1|Cần " + Util.numberToMoney(ruby) + " ruby";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần 10k ruby");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Vật phẩm đã đạt tối đa sao pha lê", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể đục lỗ",
                                "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy hãy chọn 1 vật phẩm để pha lê hóa",
                            "Đóng");
                }
                break;
            case NANG_CAP_DO_TS:
                if (player.combineNew.itemsCombine.size() != 4) {
                    this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu Nguyên liệu", "Đóng");
                    break;
                }
                if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
                    break;
                }
                if (player.combineNew.itemsCombine.size() == 4) {
                    Item manhts = null;
                    Item danc = null;
                    Item damayman = null;
                    Item congthuc = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.isManhTS()) {
                                manhts = item;
                            } else if (item.isdanangcapDoTs()) {
                                danc = item;
                            } else if (item.isDamayman()) {
                                damayman = item;
                            } else if (item.isCongthucNomal() || item.isCongthucVip()) {
                                congthuc = item;
                            }
                        }
                    }
                    if (manhts == null || manhts.quantity < 999) {
                        Service.getInstance().sendThongBao(player, "Cần x999 mảnh thiên sứ!!");
                        break;
                    }
                    if (danc == null) {
                        Service.getInstance().sendThongBao(player, "Thiếu đá nâng cấp!!");
                        break;
                    }
                    if (damayman == null) {
                        Service.getInstance().sendThongBao(player, "Thiếu đá may mắn!!");
                        break;
                    }
                    if (congthuc == null) {
                        Service.getInstance().sendThongBao(player, "Thiếu công thức!!");
                        break;
                    }
                    short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061}, {1050, 1053, 1056, 1059, 1062}};
                    Item itemTS = ItemService.gI().createNewItem(itemIds[congthuc.template.gender][manhts.typeIdManh()]);
                    String npcSay = "Chế tạo " + itemTS.template.name + "\n"
                            + "Mạnh hơn trang bị Hủy Diệt từ 20% đến 35%\n"
                            + "Mảnh ghép " + manhts.quantity + "/999 (Thất bại -99 mảnh ghép)\n"
                            + danc.template.name + " (thêm " + (danc.template.id - 1073) * 10 + "% tỉ lệ thành công)\n"
                            + damayman.template.name + " (thêm " + (damayman.template.id - 1078) * 10 + "% tỉ lệ tối đa các chỉ số)\n"
                            + "Tỉ lệ thành công: " + get_Tile_nang_Do_TS(danc, congthuc) + "%\n"
                            + "Phí nâng cấp: " + Util.numberToMoney(COST_NANG_DO_TS) + " vàng"
                            + "Phí nâng cấp: " + Util.numberToMoney(COST_RUBY_NANG_DO_TS) + " hồng ngọc";
                    if (player.inventory.gold < COST_NANG_DO_TS) {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        break;
                    }
                    if (player.inventory.ruby < COST_RUBY_NANG_DO_TS) {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
                        break;
                    }
                    this.whis.createOtherMenu(player, ConstNpc.MENU_NANG_CAP_DO_TS,
                            npcSay, "Nâng cấp", "Từ chối");
                } else {
                    this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Còn thiếu nguyên liệu để nâng cấp hãy quay lại sau", "Đóng");
                }
                break;
            case NHAP_NGOC_RONG:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 1) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        if (item != null && item.isNotNullItem()) {
                            if ((item.template.id > 14 && item.template.id <= 20) && item.quantity >= 7) {
                                String npcSay = "|2|Con có muốn biến 7 " + item.template.name + " thành\n" + "1 viên "
                                        + ItemService.gI().getTemplate((short) (item.template.id - 1)).name + "\n"
                                        + "|7|Cần 7 " + item.template.name;
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép",
                                        "Từ chối");
                            } else if ((item.template.id == 14 && item.quantity >= 7)) {
                                String npcSay = "|2|Con có muốn biến 7 " + item.template.name + " thành\n" + "1 viên "
                                        + ItemService.gI().getTemplate((short) (925)).name + "\n" + "\n|7|Cần 7 "
                                        + item.template.name + "\n|7|Cần 500tr Vàng";
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép",
                                        "Từ chối");
                            } else if (item.template.id == 926 && item.quantity >= 7) {
                                String npcSay = "|2|Con có muốn biến 7 " + item.template.name + " thành\n" + "1 viên "
                                        + ItemService.gI().getTemplate((short) (925)).name + "\n" + "\n|7|Cần 7 "
                                        + item.template.name + "\n|7|Cần 500tr Vàng";
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép",
                                        "Từ chối");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Cần 7 viên ngọc rồng 2 sao trở lên", "Đóng");
                            }
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 7 viên ngọc rồng 2 sao trở lên", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống",
                            "Đóng");
                }
                break;
            case EP_NGOC_RONG_BANG:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 1) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        if (item != null && item.isNotNullItem()) {
                            if ((item.template.id > 925 && item.template.id <= 931) && item.quantity >= 7) {
                                String npcSay = "|2|Con có muốn biến 7 " + item.template.name + " thành\n" + "1 viên "
                                        + ItemService.gI().getTemplate((short) (item.template.id - 1)).name + "\n"
                                        + "|7|Cần 7 " + item.template.name;
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép",
                                        "Từ chối");
                            } else if ((item.template.id == 2009 && item.quantity >= 7)) {
                                String npcSay = "|2|Con có muốn biến 7 " + item.template.name + " thành\n" + "1 viên "
                                        + ItemService.gI().getTemplate((short) (931)).name + "\n" + "\n|7|Cần 7 "
                                        + item.template.name + "\n|7|Cần 1k ruby";
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép",
                                        "Từ chối");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Cần 7 viên Băng tâm thần thủy trở lên", "Đóng");
                            }
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Thiếu vật phẩm", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hành trang cần ít nhất 1 chỗ trống",
                            "Đóng");
                }
                break;

            case NANG_CAP_BONG_TAI_CAP3:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item bongtai = null;
                    Item binh = null;
                    Item hoa = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        switch (item.template.id) {
                            case 921:
                                bongtai = item;
                                break;
                            case 2062:
                                binh = item;
                                break;
                            case 1098:
                                hoa = item;
                                break;
                        }
                    }

                    if (bongtai != null && hoa != null && binh != null) {
                        int level = 0;
                        for (ItemOption io : bongtai.itemOptions) {
                            if (io.optionTemplate.id == 72) {
                                level = io.param;
                                break;
                            }
                        }
                        if (level < 3) {
                            int lvbt = lvbt(bongtai);
                            int countmvbt = getcountmvbtnangbt(lvbt);

                            String npcSay = "Bông tai Porata Cấp: " + lvbt + " \n|2|";
                            for (ItemOption io : bongtai.itemOptions) {
                                npcSay += io.getOptionString() + "\n";
                            }
                            npcSay += "|7|Tỉ lệ thành công: 10%\n";
                            if (hoa.quantity >= countmvbt && binh.quantity >= countmvbt) {
                                if (player.inventory.ruby < 10_000) {
                                    npcSay += "Còn thiếu " + Util.numberToMoney(10_000 - player.inventory.ruby) + " ngọc hồng";
                                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                } else {
                                    npcSay += "|1|Cần 10k  ruby";
                                    baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                            "Nâng cấp\ncần 10k ngọc hồng");
                                }
                            } else {
                                npcSay += "Còn thiếu " + Util.numberToMoney(countmvbt - hoa.quantity)
                                        + " Hoa hoặc Bình độc dược";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Đã đạt cấp tối đa", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Bông tai Porata cấp 2 và x99 Bình độc dược và X99 hoa hồng xanh", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Bông tai Porata cấp 2 và x99 Bình độc dược và X99 hoa hồng xanh", "Đóng");
                }
                break;
            case DAP_BONG_TAI_CAP_3:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item bongtai = null;
                    Item hoa = null;
                    Item binh = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template == null) {
                            Service.getInstance().sendThongBao(player, "ERROR");
                            break;
                        }
                        switch (item.template.id) {
                            case 1995:
                                bongtai = item;
                                break;
                            case 2062:
                                binh = item;
                                break;
                            case 1098:
                                hoa = item;
                                break;
                        }
                    }
                    if (bongtai != null && binh != null && hoa != null && binh.quantity >= 99 && hoa.quantity >= 99) {
                        int ruby = 10_000;
                        String npcSay = "Bông tai Porata cấp 3\n|2|";
                        for (ItemOption io : bongtai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: 10%\n";
                        if (player.inventory.ruby < ruby) {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(10_000 - player.inventory.ruby)
                                    + " ngọc hồng";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        } else {
                            npcSay += "|1|Cần 10k ruby và x99 Hoa xanh, Bình dược";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nâng cấp\ncần 10k ngọc hồng");

                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Bông tai Porata cấp 3, X99 X99 hoa hồng xanh và x99 Bình độc dược ", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Bông tai Porata cấp 3,  X99 hoa hồng xanh và x99 Bình độc dược ", "Đóng");
                }
                break;
            case NANG_CAP_BONG_TAI:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item bongtai = null;
                    Item manhvobt = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (checkbongtai(item)) {
                            bongtai = item;
                        } else if (item.template.id == 933) {
                            manhvobt = item;
                        }
                    }

                    if (bongtai != null && manhvobt != null) {
                        int level = 0;
                        for (ItemOption io : bongtai.itemOptions) {
                            if (io.optionTemplate.id == 72) {
                                level = io.param;
                                break;
                            }
                        }
                        if (level < 2) {
                            int lvbt = lvbt(bongtai);
                            int countmvbt = getcountmvbtnangbt(lvbt);
                            player.combineNew.goldCombine = getGoldnangbt(lvbt);
                            player.combineNew.rubyCombine = getRubyUpgradeBT(lvbt);
                            player.combineNew.ratioCombine = getRationangbt(lvbt);

                            String npcSay = "Bông tai Porata Cấp: " + lvbt + " \n|2|";
                            for (ItemOption io : bongtai.itemOptions) {
                                npcSay += io.getOptionString() + "\n";
                            }
                            npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                            if (manhvobt.quantity >= countmvbt) {
                                if (player.combineNew.goldCombine <= player.inventory.gold) {
                                    if (player.combineNew.rubyCombine <= player.inventory.ruby) {
                                        npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine)
                                                + " vàng";
                                        baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                                "Nâng cấp\ncần " + player.combineNew.rubyCombine + " ngọc hồng");
                                    } else {
                                        npcSay += "Còn thiếu " + Util.numberToMoney(
                                                player.combineNew.rubyCombine - player.inventory.ruby) + " ngọc hồng";
                                        baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                    }
                                } else {
                                    npcSay += "Còn thiếu "
                                            + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                            + " vàng";
                                    baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                                }
                            } else {
                                npcSay += "Còn thiếu " + Util.numberToMoney(countmvbt - manhvobt.quantity)
                                        + " Mảnh vỡ bông tai";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Đã đạt cấp tối đa! Nâng con cặc =))))", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Bông tai Porata cấp 1 hoặc 2 và Mảnh vỡ bông tai", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Bông tai Porata cấp 1 hoặc 2 và Mảnh vỡ bông tai", "Đóng");
                }
                break;

            case MO_CHI_SO_BONG_TAI:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item bongTai = null;
                    Item manhHon = null;
                    Item daXanhLam = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.template == null) {
                            Service.getInstance().sendThongBao(player, "ERROR");
                            break;
                        }
                        switch (item.template.id) {
                            case 921:
                                bongTai = item;
                                break;
                            case 934:
                                manhHon = item;
                                break;
                            case 935:
                                daXanhLam = item;
                                break;
                            default:
                                break;
                        }
                    }
                    if (bongTai != null && manhHon != null && daXanhLam != null && manhHon.quantity >= 99) {
                        player.combineNew.goldCombine = GOLD_MOCS_BONG_TAI;
                        player.combineNew.rubyCombine = RUBY_MOCS_BONG_TAI;
                        player.combineNew.ratioCombine = RATIO_NANG_CAP;
                        String npcSay = "Bông tai Porata cấp "
                                + (bongTai.template.id == 921 ? bongTai.template.id == 1128 ? "2" : "3" : "1")
                                + " \n|2|";
                        for (ItemOption io : bongTai.itemOptions) {
                            npcSay += io.getOptionString() + "\n";
                        }
                        npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                        if (player.combineNew.goldCombine <= player.inventory.gold) {
                            if (player.combineNew.rubyCombine <= player.inventory.ruby) {
                                npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nâng cấp\ncần " + player.combineNew.rubyCombine + " ngọc hồng");
                            } else {
                                npcSay += "Còn thiếu "
                                        + Util.numberToMoney(player.combineNew.rubyCombine - player.inventory.ruby)
                                        + " ngọc hồng";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                            }
                        } else {
                            npcSay += "Còn thiếu "
                                    + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold)
                                    + " vàng";
                            baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cần 1 Bông tai Porata cấp 2 hoặc 3, X99 Mảnh hồn bông tai và 1 Đá xanh lam", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cần 1 Bông tai Porata cấp 2 hoặc 3, X99 Mảnh hồn bông tai và 1 Đá xanh lam", "Đóng");
                }
                break;
            case NANG_CAP_VAT_PHAM:
                if (player.combineNew.itemsCombine.size() >= 2 && player.combineNew.itemsCombine.size() < 4) {
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type < 5).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ nâng cấp", "Đóng");
                        break;
                    }
                    if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đá nâng cấp", "Đóng");
                        break;
                    }
                    if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1143).count() < 1) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu đồ nâng cấp", "Đóng");
                        break;
                    }
                    Item itemDo = null;
                    Item itemDNC = null;
                    Item itemDBV = null;
                    for (int j = 0; j < player.combineNew.itemsCombine.size(); j++) {
                        if (player.combineNew.itemsCombine.get(j).isNotNullItem()) {
                            if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.get(j).template.id == 1143) {
                                itemDBV = player.combineNew.itemsCombine.get(j);
                                continue;
                            }
                            if (player.combineNew.itemsCombine.get(j).template.type < 5) {
                                itemDo = player.combineNew.itemsCombine.get(j);
                            } else {
                                itemDNC = player.combineNew.itemsCombine.get(j);
                            }
                        }
                    }
                    if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {
                        int level = 0;
                        for (ItemOption io : itemDo.itemOptions) {
                            if (io.optionTemplate.id == 72) {
                                level = io.param;
                                break;
                            }
                        }
                        if (level < MAX_LEVEL_ITEM) {
                            player.combineNew.goldCombine = getGoldNangCapDo(level);
                            player.combineNew.ratioCombine = (float) getTileNangCapDo(level);
                            player.combineNew.countDaNangCap = getCountDaNangCapDo(level);
                            player.combineNew.countDaBaoVe = (short) getCountDaBaoVe(level);
                            String npcSay = "|2|Hiện tại " + itemDo.template.name + " (+" + level + ")\n|0|";
                            for (ItemOption io : itemDo.itemOptions) {
                                if (io.optionTemplate.id != 72) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            String option = null;
                            int param = 0;
                            for (ItemOption io : itemDo.itemOptions) {
                                if (io.optionTemplate.id == 47
                                        || io.optionTemplate.id == 6
                                        || io.optionTemplate.id == 0
                                        || io.optionTemplate.id == 7
                                        || io.optionTemplate.id == 14
                                        || io.optionTemplate.id == 22
                                        || io.optionTemplate.id == 23) {
                                    option = io.optionTemplate.name;
                                    param = io.param + (io.param * 10 / 100);
                                    break;
                                }
                            }
                            npcSay += "|2|Sau khi nâng cấp (+" + (level + 1) + ")\n|7|"
                                    + option.replaceAll("#", String.valueOf(param))
                                    + "\n|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%\n"
                                    + (player.combineNew.countDaNangCap > itemDNC.quantity ? "|7|" : "|1|")
                                    + "Cần " + player.combineNew.countDaNangCap + " " + itemDNC.template.name
                                    + "\n" + (player.combineNew.goldCombine > player.inventory.gold ? "|7|" : "|1|")
                                    + "Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";

                            String daNPC = player.combineNew.itemsCombine.size() == 3 && itemDBV != null ? String.format("\nCần tốn %s đá bảo vệ", player.combineNew.countDaBaoVe) : "";
                            if ((level == 2 || level == 4 || level == 6) && !(player.combineNew.itemsCombine.size() == 3 && itemDBV != null)) {
                                npcSay += "\nNếu thất bại sẽ rớt xuống (+" + (level - 1) + ")";
                            }
                            if (player.combineNew.countDaNangCap > itemDNC.quantity) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn thiếu\n" + (player.combineNew.countDaNangCap - itemDNC.quantity) + " " + itemDNC.template.name);
                            } else if (player.combineNew.goldCombine > player.inventory.gold) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn thiếu\n" + Util.numberToMoney((player.combineNew.goldCombine - player.inventory.gold)) + " vàng");
                            } else if (player.combineNew.itemsCombine.size() == 3 && Objects.nonNull(itemDBV) && itemDBV.quantity < player.combineNew.countDaBaoVe) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Còn thiếu\n" + (player.combineNew.countDaBaoVe - itemDBV.quantity) + " đá bảo vệ");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                        npcSay, "Nâng cấp\n" + Util.numberToMoney(player.combineNew.goldCombine) + " vàng" + daNPC, "Từ chối");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Trang bị của ngươi đã đạt cấp tối đa", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị và 1 loại đá nâng cấp", "Đóng");
                    }
                } else {
                    if (player.combineNew.itemsCombine.size() > 3) {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cất đi con ta không thèm", "Đóng");
                        break;
                    }
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị và 1 loại đá nâng cấp", "Đóng");
                }
                break;
            case DOI_VE_HUY_DIET:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (item.isNotNullItem() && item.template.id >= 555 && item.template.id <= 567) {
                        String ticketName = "Vé đổi " + (item.template.type == 0 ? "áo"
                                : item.template.type == 1 ? "quần"
                                        : item.template.type == 2 ? "găng" : item.template.type == 3 ? "giày" : "nhẫn")
                                + " hủy diệt";
                        String npcSay = "|6|Ngươi có chắc chắn muốn đổi\n|7|" + item.template.name + "\n";
                        for (ItemOption io : item.itemOptions) {
                            npcSay += "|2|" + io.getOptionString() + "\n";
                        }
                        npcSay += "|6|Lấy\n|7|" + ticketName + "\n|6|Với giá "
                                + Util.numberToMoney(COST_DOI_VE_DOI_DO_HUY_DIET) + " vàng không?";
                        if (player.inventory.gold >= COST_DOI_VE_DOI_DO_HUY_DIET) {
                            this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Đổi",
                                    "Từ chối");
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Còn thiếu\n"
                                    + Util.numberToMoney(COST_DOI_VE_DOI_DO_HUY_DIET - player.inventory.gold) + " vàng",
                                    "Đóng");
                        }

                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Hãy chọn 1 trang bị thần linh ngươi muốn trao đổi", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Hãy chọn 1 trang bị thần linh ngươi muốn trao đổi", "Đóng");
                }
                break;
            case DAP_SET_KICH_HOAT:
                if (player.combineNew.itemsCombine.size() == 1 || player.combineNew.itemsCombine.size() == 2) {
                    Item dhd = null, dtl = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.id >= 650 && item.template.id <= 662) {
                                dhd = item;
                            } else if (item.template.id >= 555 && item.template.id <= 567) {
                                dtl = item;
                            }
                        }
                    }
                    if (dhd != null) {
                        String npcSay = "|6|" + dhd.template.name + "\n";
                        for (ItemOption io : dhd.itemOptions) {
                            npcSay += "|2|" + io.getOptionString() + "\n";
                        }
                        if (dtl != null) {
                            npcSay += "|6|" + dtl.template.name + "\n";
                            for (ItemOption io : dtl.itemOptions) {
                                npcSay += "|2|" + io.getOptionString() + "\n";
                            }
                        }
                        npcSay += "Ngươi có muốn chuyển hóa thành\n";
                        npcSay += "|1|" + getNameItemC0(dhd.template.gender, dhd.template.type)
                                + " (ngẫu nhiên kích hoạt)\n|7|Tỉ lệ thành công " + (dtl != null ? "100%" : "40%")
                                + "\n|2|Cần " + Util.numberToMoney(COST_DAP_DO_KICH_HOAT) + " vàng";
                        if (player.inventory.gold >= COST_DAP_DO_KICH_HOAT) {
                            this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Cần " + Util.numberToMoney(COST_DAP_DO_KICH_HOAT) + " vàng");
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Còn thiếu\n"
                                    + Util.numberToMoney(player.inventory.gold - COST_DAP_DO_KICH_HOAT) + " vàng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ta cần 1 món đồ hủy diệt của ngươi để có thể chuyển hóa 1", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Ta cần 1 món đồ hủy diệt của ngươi để có thể chuyển hóa 2", "Đóng");
                }
                break;
            case DOI_MANH_KICH_HOAT:
                if (player.combineNew.itemsCombine.size() == 2 || player.combineNew.itemsCombine.size() == 3) {
                    Item nr1s = null, doThan = null, buaBaoVe = null;
                    for (Item it : player.combineNew.itemsCombine) {
                        if (it.template.id == 14) {
                            nr1s = it;
                        } else if (it.template.id == 2010) {
                            buaBaoVe = it;
                        } else if (it.template.id >= 555 && it.template.id <= 567) {
                            doThan = it;
                        }
                    }

                    if (nr1s != null && doThan != null) {
                        int tile = 50;
                        String npcSay = "|6|Ngươi có muốn trao đổi\n|7|" + nr1s.template.name
                                + "\n|7|" + doThan.template.name
                                + "\n";
                        for (ItemOption io : doThan.itemOptions) {
                            npcSay += "|2|" + io.getOptionString() + "\n";
                        }
                        if (buaBaoVe != null) {
                            tile = 100;
                            npcSay += buaBaoVe.template.name
                                    + "\n";
                            for (ItemOption io : buaBaoVe.itemOptions) {
                                npcSay += "|2|" + io.getOptionString() + "\n";
                            }
                        }

                        npcSay += "|6|Lấy\n|7|Mảnh kích hoạt\n"
                                + "|1|Tỉ lệ " + tile + "%\n"
                                + "|6|Với giá " + Util.numberToMoney(COST_DOI_MANH_KICH_HOAT) + " vàng không?";
                        if (player.inventory.gold >= COST_DOI_MANH_KICH_HOAT) {
                            this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                    npcSay, "Đổi", "Từ chối");
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    npcSay, "Còn thiếu\n"
                                    + Util.numberToMoney(COST_DOI_MANH_KICH_HOAT - player.inventory.gold) + " vàng", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị thần linh và 1 viên ngọc rồng 1 sao", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy chọn 1 trang bị thần linh và 1 viên ngọc rồng 1 sao", "Đóng");
                }
                break;
            case DAP_SET_KICH_HOAT_CAO_CAP:
                if (player.combineNew.itemsCombine.size() == 3) {
                    Item it = player.combineNew.itemsCombine.get(0), it1 = player.combineNew.itemsCombine.get(1),
                            it2 = player.combineNew.itemsCombine.get(2);
                    if (!isDestroyClothes(it.template.id) || !isDestroyClothes(it1.template.id)
                            || !isDestroyClothes(it2.template.id)) {
                        it = null;
                    }
                    if (it != null) {
                        String npcSay = "|1|" + it.template.name + "\n" + it1.template.name + "\n" + it2.template.name
                                + "\n";
                        npcSay += "Ngươi có muốn chuyển hóa thành\n";
                        npcSay += "|7|" + getTypeTrangBi(it.template.type)
                                + " cấp bậc ngẫu nhiên (set kích hoạt ngẫu nhiên)\n|2|Cần "
                                + Util.numberToMoney(10_000) + " vàng";
                        if (player.inventory.ruby >= 10_000) {
                            this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Cần " + Util.numberToMoney(COST_DAP_DO_KICH_HOAT) + " vàng");
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Còn thiếu\n"
                                    + Util.numberToMoney(player.inventory.gold - COST_DAP_DO_KICH_HOAT) + " hồng ngọc");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ta cần 3 món đồ hủy diệt của ngươi để có thể chuyển hóa", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Nhót", "Đóng");
                }
                break;
            case GIA_HAN_CAI_TRANG:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item caitrang = null, vegiahan = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (item.isNotNullItem()) {
                            if (item.template.type == 5) {
                                caitrang = item;
                            } else if (item.template.id == 2022) {
                                vegiahan = item;
                            }
                        }
                    }
                    int expiredDate = 0;
                    boolean canBeExtend = true;
                    if (caitrang != null && vegiahan != null) {
                        for (ItemOption io : caitrang.itemOptions) {
                            if (io.optionTemplate.id == 93) {
                                expiredDate = io.param;
                            }
                            if (io.optionTemplate.id == 199) {
                                canBeExtend = false;
                            }
                        }
                        if (canBeExtend) {
                            if (expiredDate > 0) {
                                String npcSay = "|2|" + caitrang.template.name + "\n"
                                        + "Sau khi gia hạn +1 ngày \n Tỷ lệ thành công: 101% \n" + "|7|Cần 500tr vàng";
                                if (player.inventory.gold >= COST_GIA_HAN_CAI_TRANG) {
                                    this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                            "Gia hạn");
                                } else {
                                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay,
                                            "Còn thiếu\n"
                                            + Util.numberToMoney(player.inventory.gold - COST_GIA_HAN_CAI_TRANG)
                                            + " vàng");
                                }
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Cần cải trang có hạn sử dụng và thẻ gia hạn", "Đóng");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Vật phẩm này không thể gia hạn", "Đóng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ta Cần cải trang có hạn sử dụng và thẻ gia hạn", "Đóng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Ta Cần cải trang có hạn sử dụng và thẻ gia hạn", "Đóng");
                }
                break;
            case NANG_CAP_DO_THIEN_SU:
                if (player.combineNew.itemsCombine.size() > 1) {
                    int ratioLuckyStone = 0, ratioRecipe = 0, ratioUpgradeStone = 0, countLuckyStone = 0,
                            countUpgradeStone = 0;
                    Item angelClothes = null;
                    Item craftingRecipe = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        int id = item.template.id;
                        if (item.isNotNullItem()) {
                            if (isAngelClothes(id)) {
                                angelClothes = item;
                            } else if (isLuckyStone(id)) {
                                ratioLuckyStone += getRatioLuckyStone(id);
                                countLuckyStone++;
                            } else if (isUpgradeStone(id)) {
                                ratioUpgradeStone += getRatioUpgradeStone(id);
                                countUpgradeStone++;
                            } else if (isCraftingRecipe(id)) {
                                ratioRecipe += getRatioCraftingRecipe(id);
                                craftingRecipe = item;
                            }
                        }
                    }
                    if (angelClothes == null) {
                        return;
                    }
                    boolean canUpgrade = true;
                    for (ItemOption io : angelClothes.itemOptions) {
                        int optId = io.optionTemplate.id;
                        if (optId == 41) {
                            canUpgrade = false;
                        }
                    }
                    if (angelClothes.template.gender != craftingRecipe.template.gender) {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vui lòng chọn đúng công thức chế tạo",
                                "Đóng");
                        return;
                    }
                    if (canUpgrade) {
                        if (craftingRecipe != null) {
                            if (countLuckyStone < 2 && countUpgradeStone < 2) {
                                int ratioTotal = (20 + ratioUpgradeStone + ratioRecipe);
                                int ratio = ratioTotal > 75 ? ratio = 75 : ratioTotal;
                                String npcSay = "|1| Nâng cấp " + angelClothes.template.name + "\n|7|";
                                npcSay += ratioRecipe > 0 ? " Công thức VIP (+" + ratioRecipe + "% tỉ lệ thành công)\n"
                                        : "";
                                npcSay += ratioUpgradeStone > 0
                                        ? "Đá nâng cấp cấp " + ratioUpgradeStone / 10 + " (+" + ratioUpgradeStone
                                        + "% tỉ lệ thành công)\n"
                                        : "";
                                npcSay += ratioLuckyStone > 0
                                        ? "Đá nâng may mắn cấp " + ratioLuckyStone / 10 + " (+" + ratioLuckyStone
                                        + "% tỉ lệ tối đa các chỉ số)\n"
                                        : "";
                                npcSay += "Tỉ lệ thành công: " + ratio + "%\n";
                                npcSay += "Phí nâng cấp: " + Util.numberToMoney(COST_DAP_DO_KICH_HOAT) + " vàng";
                                if (player.inventory.gold >= COST_DAP_DO_KICH_HOAT) {
                                    this.whis.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Nâng cấp");
                                } else {
                                    this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay,
                                            "Còn thiếu\n"
                                            + Util.numberToMoney(player.inventory.gold - COST_DAP_DO_KICH_HOAT)
                                            + " vàng");
                                }
                            } else {
                                this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Chỉ có thể sự dụng tối đa 1 loại nâng cấp và đá may mắn", "Đóng");
                            }
                        } else {
                            this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Người cần ít nhất 1 trang bị thiên sứ và 1 công thức để có thể nâng cấp", "Đóng");
                        }
                    } else {
                        this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Mỗi vật phẩm chỉ có thể nâng cấp 1 lần", "Đóng");
                    }
                } else {
                    this.whis.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Người cần ít nhất 1 trang bị thiên sứ và 1 công thức để có thể nâng cấp", "Đóng");
                }
                break;
        }
    }

    /**
     * Bắt đầu đập đồ - điều hướng từng loại đập đồ
     *
     * @param player
     */
    public void startCombine(Player player) {
        if (Util.canDoWithTime(player.combineNew.lastTimeCombine, TIME_COMBINE)) {
            if (false) {
                Service.getInstance().sendThongBao(player, "Tính năng đang tạm khóa");
                return;
            }
            switch (player.combineNew.typeCombine) {
                case NANG_CAP_BONG_TAI_CAP3:
                    nangCapBongTaiCap3(player);
                    break;
                case PHA_LE_HOA_CAI_TRANG:
                    phaLeHoaCaiTrang(player);
                    break;
                case PHA_LE_HOA_DISGUISE:
                    phalehoaDisguise(player);
                    break;
                case TRADE_PET:
                    tradePet(player);
                    break;
                case UPGRADE_PET:
                    upgradePet(player);
                    break;
                case UPGRADE_THAN_LINH:
                    dapDoThanLinh(player);
                    break;
                case UPGRADE_LINHTHU:
                    upgradeLinhThu(player);
                    break;
                case UPGRADE_CAITRANG:
                    upgradeDisguise(player);
                    break;
                case NANG_CAP_DO_KICH_HOAT:
                    dapDoKichHoat(player);
                    break;
                case TRADE_DO_THAN_LINH:
                    tradeDoThanLinh(player);
                case EP_SAO_TRANG_BI:
                    epSaoTrangBi(player);
                    break;
                case PHA_LE_HOA_TRANG_BI:
                    phaLeHoaTrangBi(player);
                    break;
                case NANG_CAP_DO_TS:
                    openDTS(player);
                    break;
                case NHAP_NGOC_RONG:
                    nhapNgocRong(player);
                    break;
                case EP_NGOC_RONG_BANG:
                    epNgocRongBang(player);
                    break;
                case NANG_CAP_VAT_PHAM:
                    nangCapVatPham(player);
                    break;
                case DOI_VE_HUY_DIET:
                    doiVeHuyDiet(player);
                    break;
                case GIA_HAN_CAI_TRANG:
                    giaHanCaiTrang(player);
                    break;
                case NANG_CAP_DO_THIEN_SU:
                    nangCapDoThienSu(player);
                    break;
                case PHA_LE_HOA_TRANG_BI_X10:
                    phaLeHoaTrangBiX10(player);
                    break;
                case NANG_CAP_BONG_TAI:
                    nangCapBongTai(player);
                    break;
                case MO_CHI_SO_BONG_TAI:
                    moChiSoBongTai(player);
                    break;
                case DAP_BONG_TAI_CAP_3:
                    dapBongTaiCap3(player);
                    break;
            }
            player.iDMark.setIndexMenu(ConstNpc.IGNORE_MENU);
            player.combineNew.clearParamCombine();
            player.combineNew.lastTimeCombine = System.currentTimeMillis();
        } else {
            Service.getInstance().sendThongBao(player, "@@");
        }
    }

    private void phaLeHoaTrangBiX10(Player player) {
        for (int i = 0; i < 5; i++) { // số lần pha lê hóa
            if (phaLeHoaTrangBi(player)) {
                Service.getInstance().sendThongBao(player,
                        "Chúc mừng bạn đã pha lê hóa thành công,tổng số lần nâng cấp là: " + i);
                break;
            }
        }
    }

    private void nangCapBongTaiCap3(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            int ruby = 10_000;
            if (player.inventory.ruby < ruby) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item bongtai = null;
            Item hoa = null;
            Item binh = null;
            for (Item item : player.combineNew.itemsCombine) {
                switch (item.template.id) {
                    case 921:
                        bongtai = item;
                        break;
                    case 2062:
                        binh = item;
                        break;
                    case 1098:
                        hoa = item;
                }
            }
            if (bongtai != null && hoa != null && binh != null) {
                int level = 0;
                for (ItemOption io : bongtai.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        break;
                    }
                }
                if (level < 3) {
                    int lvbt = lvbt(bongtai);
                    int countmvbt = getcountmvbtnangbt(lvbt);
                    if (countmvbt > hoa.quantity) {
                        Service.getInstance().sendThongBao(player, "Không đủ Hoa Xanh");
                        return;
                    }
                    if (countmvbt > binh.quantity) {
                        Service.getInstance().sendThongBao(player, "Không đủ Bình độc dược");
                        return;
                    }
                    player.inventory.ruby -= ruby;
                    InventoryService.gI().subQuantityItemsBag(player, hoa, 99);
                    InventoryService.gI().subQuantityItemsBag(player, binh, 99);
                    if (Util.isTrue(10, 100)) {
                        bongtai.template = ItemService.gI().getTemplate(getidbtsaukhilencap(lvbt));
//                        bongtai.itemOptions.clear();
                        bongtai.itemOptions.add(new ItemOption(72, lvbt + 1));
                        sendEffectSuccessCombine(player);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void nangCapBongTai(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item bongtai = null;
            Item manhvobt = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (checkbongtai(item)) {
                    bongtai = item;
                } else if (item.template.id == 933) {
                    manhvobt = item;
                }
            }
            if (bongtai != null && manhvobt != null) {
                int level = 0;
                for (ItemOption io : bongtai.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        break;
                    }
                }
                if (level < 2) {
                    int lvbt = lvbt(bongtai);
                    int countmvbt = getcountmvbtnangbt(lvbt);
                    if (countmvbt > manhvobt.quantity) {
                        Service.getInstance().sendThongBao(player, "Không đủ Mảnh vỡ bông tai");
                        return;
                    }
                    player.inventory.gold -= gold;
                    player.inventory.ruby -= ruby;
                    InventoryService.gI().subQuantityItemsBag(player, manhvobt, countmvbt);
                    if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                        bongtai.template = ItemService.gI().getTemplate(getidbtsaukhilencap(lvbt));
                        bongtai.itemOptions.clear();
                        bongtai.itemOptions.add(new ItemOption(72, lvbt + 1));
                        sendEffectSuccessCombine(player);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private short getidbtsaukhilencap(int lvbtcu) {
        switch (lvbtcu) {
            case 1:
                return 921;
            case 2:
                return 1995;
        }
        return 0;
    }

    private void moChiSoBongTai(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            int gold = player.combineNew.goldCombine;
            if (player.inventory.gold < gold) {
                Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            }
            int ruby = player.combineNew.rubyCombine;
            if (player.inventory.ruby < ruby) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item bongTai = null;
            Item manhHon = null;
            Item daXanhLam = null;
            for (Item item : player.combineNew.itemsCombine) {
                switch (item.template.id) {
                    case 921:
                    case 1128:
                        bongTai = item;
                        break;
                    case 934:
                        manhHon = item;
                        break;
                    case 935:
                        daXanhLam = item;
                        break;
                    default:
                        break;
                }
            }
            if (bongTai != null && daXanhLam != null && manhHon.quantity >= 99) {
                player.inventory.gold -= gold;
                player.inventory.ruby -= ruby;
                InventoryService.gI().subQuantityItemsBag(player, manhHon, 99);
                InventoryService.gI().subQuantityItemsBag(player, daXanhLam, 1);
                if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                    bongTai.itemOptions.clear();
                    if (bongTai.template.id == 921) {
                        bongTai.itemOptions.add(new ItemOption(72, 2));
                    } else if (bongTai.template.id == 1128) {
                        bongTai.itemOptions.add(new ItemOption(72, 3));
                    }
                    int rdUp = Util.nextInt(0, 7);
                    switch (rdUp) {
                        case 0:
                            bongTai.itemOptions.add(new ItemOption(50, Util.nextInt(5, 25)));
                            break;
                        case 1:
                            bongTai.itemOptions.add(new ItemOption(77, Util.nextInt(5, 25)));
                            break;
                        case 2:
                            bongTai.itemOptions.add(new ItemOption(103, Util.nextInt(5, 25)));
                            break;
                        case 3:
                            bongTai.itemOptions.add(new ItemOption(108, Util.nextInt(5, 25)));
                            break;
                        case 4:
                            bongTai.itemOptions.add(new ItemOption(94, Util.nextInt(5, 15)));
                            break;
                        case 5:
                            bongTai.itemOptions.add(new ItemOption(14, Util.nextInt(5, 15)));
                            break;
                        case 6:
                            bongTai.itemOptions.add(new ItemOption(80, Util.nextInt(5, 25)));
                            break;
                        case 7:
                            bongTai.itemOptions.add(new ItemOption(81, Util.nextInt(5, 25)));
                            break;
                    }
                    sendEffectSuccessCombine(player);
                } else {
                    sendEffectFailCombine(player);
                }
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void dapBongTaiCap3(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            int ruby = 10_000;
            if (player.inventory.ruby < ruby) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item bongtai = null;
            Item hoa = null;
            Item binh = null;
            for (Item item : player.combineNew.itemsCombine) {
                switch (item.template.id) {
                    case 1995:
                        bongtai = item;
                        break;
                    case 2062:
                        binh = item;
                        break;
                    case 1098:
                        hoa = item;
                        break;
                }
            }
            if (bongtai != null && binh != null && hoa.quantity >= 99 && binh.quantity >= 99) {
                player.inventory.ruby -= ruby;
                InventoryService.gI().subQuantityItemsBag(player, hoa, 99);
                InventoryService.gI().subQuantityItemsBag(player, binh, 99);
                bongtai.itemOptions.clear();
                if (bongtai.template.id == 1995) {
                    bongtai.itemOptions.add(new ItemOption(72, 3));
                }
                int rdUp = Util.nextInt(0, 7);
                switch (rdUp) {
                    case 0:
                        bongtai.itemOptions.add(new ItemOption(50, Util.nextInt(5, 35)));
                        break;
                    case 1:
                        bongtai.itemOptions.add(new ItemOption(77, Util.nextInt(5, 35)));
                        break;
                    case 2:
                        bongtai.itemOptions.add(new ItemOption(103, Util.nextInt(5, 35)));
                        break;
                    case 3:
                        bongtai.itemOptions.add(new ItemOption(108, Util.nextInt(5, 35)));
                        break;
                    case 4:
                        bongtai.itemOptions.add(new ItemOption(94, Util.nextInt(5, 35)));
                        break;
                    case 5:
                        bongtai.itemOptions.add(new ItemOption(14, Util.nextInt(5, 35)));
                        break;
                    case 6:
                        bongtai.itemOptions.add(new ItemOption(80, Util.nextInt(5, 35)));
                        break;
                    case 7:
                        bongtai.itemOptions.add(new ItemOption(81, Util.nextInt(5, 35)));
                        break;
                }
                sendEffectSuccessCombine(player);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nangCapDoThienSu(Player player) {
        if (player.combineNew.itemsCombine.size() > 1) {
            int ratioLuckyStone = 0, ratioRecipe = 0, ratioUpgradeStone = 0;
            List<Item> list = new ArrayList<>();
            Item angelClothes = null;
            Item craftingRecipe = null;
            for (Item item : player.combineNew.itemsCombine) {
                int id = item.template.id;
                if (item.isNotNullItem()) {
                    if (isAngelClothes(id)) {
                        angelClothes = item;
                    } else if (isLuckyStone(id)) {
                        ratioLuckyStone += getRatioLuckyStone(id);
                        list.add(item);
                    } else if (isUpgradeStone(id)) {
                        ratioUpgradeStone += getRatioUpgradeStone(id);
                        list.add(item);
                    } else if (isCraftingRecipe(id)) {
                        ratioRecipe += getRatioCraftingRecipe(id);
                        craftingRecipe = item;
                        list.add(item);
                    }
                }
            }
            boolean canUpgrade = true;
            for (ItemOption io : angelClothes.itemOptions) {
                int optId = io.optionTemplate.id;
                if (optId == 41) {
                    canUpgrade = false;
                }
            }
            if (canUpgrade) {
                if (angelClothes != null && craftingRecipe != null) {
                    int ratioTotal = (20 + ratioUpgradeStone + ratioRecipe);
                    int ratio = ratioTotal > 75 ? ratio = 75 : ratioTotal;
                    if (player.inventory.gold >= COST_DAP_DO_KICH_HOAT) {
                        if (Util.isTrue(ratio, 150)) {
                            int num = 0;
                            if (Util.isTrue(ratioLuckyStone, 150)) {
                                num = 15;
                            } else if (Util.isTrue(5, 100)) {
                                num = Util.nextInt(10, 15);
                            } else if (Util.isTrue(20, 100)) {
                                num = Util.nextInt(1, 10);
                            }
                            RandomCollection<Integer> rd = new RandomCollection<>();
                            rd.add(50, 1 /*, "DDijt nhau au au"*/);
                            rd.add(25, 2 /*, "DDijt nhau au au"*/);
                            rd.add(10, 3 /*, "DDijt nhau au au"*/);
                            rd.add(5, 4 /*, "DDijt nhau au au"*/);
                            int color = rd.next();
                            for (ItemOption io : angelClothes.itemOptions) {
                                int optId = io.optionTemplate.id;
                                switch (optId) {
                                    case 47: // giáp
                                    case 6: // hp
                                    case 26: // hp/30s
                                    case 22: // hp k
                                    case 0: // sức đánh
                                    case 7: // ki
                                    case 28: // ki/30s
                                    case 23: // ki k
                                    case 14: // crit
                                        io.param += ((long) io.param * num / 100);
                                        break;
                                }
                            }
                            angelClothes.itemOptions.add(new ItemOption(41, color));
                            for (int i = 0; i < color; i++) {
                                angelClothes.itemOptions
                                        .add(new ItemOption(Util.nextInt(201, 212), Util.nextInt(1, 10)));
                            }
                            sendEffectSuccessCombine(player);
                            Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã nâng cấp thành công");
                        } else {
                            sendEffectFailCombine(player);
                            Service.getInstance().sendThongBao(player, "Chúc bạn đen nốt lần sau");
                        }
                        for (Item it : list) {
                            InventoryService.gI().subQuantityItemsBag(player, it, 1);
                        }
                        player.inventory.subGold(COST_DAP_DO_KICH_HOAT);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendMoney(player);
                        reOpenItemCombine(player);
                    }
                }
            }
        }
    }

    private void giaHanCaiTrang(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            Item caitrang = null, vegiahan = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.type == 5) {
                        caitrang = item;
                    } else if (item.template.id == 2022) {
                        vegiahan = item;
                    }
                }
            }
            if (caitrang != null && vegiahan != null) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_GIA_HAN_CAI_TRANG) {
                    ItemOption expiredDate = null;
                    boolean canBeExtend = true;
                    for (ItemOption io : caitrang.itemOptions) {
                        if (io.optionTemplate.id == 93) {
                            expiredDate = io;
                        }
                        if (io.optionTemplate.id == 199) {
                            canBeExtend = false;
                        }
                    }
                    if (canBeExtend) {
                        if (expiredDate.param > 0) {
                            player.inventory.subGold(COST_GIA_HAN_CAI_TRANG);
                            sendEffectSuccessCombine(player);
                            expiredDate.param++;
                            InventoryService.gI().subQuantityItemsBag(player, vegiahan, 1);
                            InventoryService.gI().sendItemBags(player);
                            Service.getInstance().sendMoney(player);
                            reOpenItemCombine(player);
                        }
                    }
                }
            }
        }
    }

    public void openDTS(Player player) {
        if (player.combineNew.itemsCombine.size() != 4) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (player.inventory.gold < COST_NANG_DO_TS) {
            Service.getInstance().sendThongBao(player, "Ảo ít thôi con...");
            return;
        }
        if (player.inventory.ruby < COST_RUBY_NANG_DO_TS) {
            Service.getInstance().sendThongBao(player, "Ảo ít thôi con...");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            return;
        }
        if (player.combineNew.itemsCombine.size() == 4) {
            Item manhts = null;
            Item danc = null;
            Item damayman = null;
            Item congthuc = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.isManhTS()) {
                        manhts = item;
                    } else if (item.isdanangcapDoTs()) {
                        danc = item;
                    } else if (item.isDamayman()) {
                        damayman = item;
                    } else if (item.isCongthucNomal() || item.isCongthucVip()) {
                        congthuc = item;
                    }
                }
            }
            if (manhts == null || danc == null || damayman == null || congthuc == null) {
                Service.getInstance().sendThongBao(player, "Thiếu đồ!!");
                return;
            }
            if (manhts.quantity < 999) {
                Service.getInstance().sendThongBao(player, "Cần x999 mảnh thiên sứ!");
                return;
            }
            if (manhts != null && danc != null && damayman != null && congthuc != null && manhts.quantity >= 999) {
                int tile = get_Tile_nang_Do_TS(danc, congthuc);
                int perLucky = 20;// chi
                perLucky += perLucky * (damayman.template.id - 1078) * 10 / 100;
                int perSuccesslucky = Util.nextInt(0, 100);
                if (Util.isTrue(tile, 100)) {
                    short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061}, {1050, 1053, 1056, 1059, 1062}};
                    Item itemTS = ItemService.gI().DoThienSu(itemIds[congthuc.template.gender][manhts.typeIdManh()], congthuc.template.gender, perSuccesslucky, perLucky);
                    sendEffectSuccessCombineDoTS(player, itemTS.template.iconID);
                    InventoryService.gI().addItemBag(player, itemTS, 0);
                    InventoryService.gI().subQuantityItemsBag(player, manhts, 999);
                } else {
                    sendEffectFailCombineDoTS(player);
                    InventoryService.gI().subQuantityItemsBag(player, manhts, 99);
                }
                player.inventory.gold -= COST_NANG_DO_TS;
                player.inventory.ruby -= COST_RUBY_NANG_DO_TS;
                InventoryService.gI().subQuantityItemsBag(player, danc, 1);
                InventoryService.gI().subQuantityItemsBag(player, damayman, 1);
                InventoryService.gI().subQuantityItemsBag(player, congthuc, 1);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                player.combineNew.itemsCombine.clear();
                reOpenItemCombine(player);
            }
        }
    }

    private void dapDoKichHoat(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            Item manhTL = null;
            Item dokh = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.id >= 2032 && item.template.id <= 2036) {
                        manhTL = item;
                    } else if (item.isSKHThuong()) {
                        dokh = item;
                    }
                }
            }
            if (dokh != null && manhTL != null && manhTL.quantity >= 2) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0 && player.inventory.ruby >= 20_000) {
                    player.inventory.ruby -= 20_000;
                    sendEffectSuccessCombine(player);
                    Item item = ArrietyDrop.randomCS_DKH(dokh.template.id, (byte) 1, dokh.template.gender == 3 ? player.gender : dokh.template.gender);
                    InventoryService.gI().addItemBag(player, item, 0);
                    InventoryService.gI().subQuantityItemsBag(player, dokh, 1);
                    InventoryService.gI().subQuantityItemsBag(player, manhTL, 2);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Thieu tien roi cu em");
                }
            }
        }
    }

    private int getTine(Item tl) {// tile thanh cong
        int tile = 10;
        if (tl != null) {
            if (tl.template.id >= 555 && tl.template.id <= 567) {
                tile = 30;
            }
        }
        return tile;
    }

    private void tradePet(Player player) {
        if (player.combineNew.itemsCombine.size() >= 1) {
            Item GAYTD = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.id == 1231) {
                        GAYTD = item;
                    }
                }
            }
            if (GAYTD.quantity < 1) {
                Service.getInstance().sendThongBao(player, "cần x10 Đá nguyền cc");
                return;
            }
            if (player.pet.typePet != ConstPet.SUPER) {
                Service.getInstance().sendThongBao(player, "Ngươi cần có đệ tử Black gâu ku");
                return;
            }
            if (GAYTD != null && GAYTD.quantity > 10) {
                InventoryService.gI().subQuantityItemsBag(player, GAYTD, 10);
                InventoryService.gI().sendItemBags(player);
                if (Util.isTrue(10, 100)) {
                    PetService.gI().createWhisPet(player, player.gender);
                    Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã nâng cấp Thành Công");
                } else {
                    Service.getInstance().sendThongBao(player, "Xịt =)))))");
                }
            }
        }
    }

    private void upgradePet(Player player) {
        if (player.combineNew.itemsCombine.size() >= 1) {
            Item da = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.id == 2040) {
                        da = item;
                    }
                }
            }
            if (da.quantity < 1) {
                Service.getInstance().sendThongBao(player, "cần x10 Đá nguyền cc");
                return;
            }
            if (player.pet.getLever() >= 7) {
                Service.getInstance().sendThongBao(player, "Max Lever rồi thằng nhót");
                return;
            }
            if (da != null && da.quantity > 10) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0 && player.inventory.ruby >= COST_LEVER) {
                    player.inventory.ruby -= 5_000;
                    Service.getInstance().sendMoney(player);
                    InventoryService.gI().subQuantityItemsBag(player, da, 10);
                    if (Util.isTrue(50, 100)) {
                        player.pet.setLever(player.pet.getLever() + 1);
                        Service.getInstance().sendThongBao(player, "Chúc mừng bạn đã nâng cấp Thành Công");
                    } else {
                        Service.getInstance().sendThongBao(player, "Xịt =)))))");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Thieu tien roi cu em");
                }
            }
        }
    }

    private void dapDoThanLinh(Player player) {
        if (player.combineNew.itemsCombine.size() >= 1) {
            Item manhTL = null;
            Item dokh = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.id >= 555 && item.template.id <= 567) {
                        manhTL = item;
                    } else if (item.isSKHVip()) {
                        dokh = item;
                    }
                }
            }
            if (dokh != null) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0 && player.inventory.ruby >= 10_000) {
                    int tile = getTine(manhTL);
                    // lew lew Chính béo <33
                    if (Util.isTrue(tile, 100)) {
                        int typeOption = 0;
                        int check = dokh.checkSet(dokh);
                        for (ItemOption io : dokh.itemOptions) {
                            switch (io.optionTemplate.id) {
                                case 129:
                                    typeOption = 2;//Set Songoku
                                    break;
                                case 128:
                                    typeOption = 1;  //Set Krillin
                                    break;
                                case 127:
                                    typeOption = 0;  //Set Tenshinhan
                                    break;
                                case 130:
                                    typeOption = 3; //Set Piccolo
                                    break;
                                case 131:
                                    typeOption = 4; //Set Dende
                                    break;
                                case 132:
                                    typeOption = 5; ///Set Pikkoro Daimao
                                    break;
                                case 133:
                                    typeOption = 6;//Set Kakarot  
                                    break;
                                case 134:
                                    typeOption = 7; //Set Vegeta
                                    break;
                                case 135:
                                    typeOption = 8; //Set Nappa
                                    break;
                            }
                        }
                        sendEffectSuccessCombine(player);
                        Item item = ArrietyDrop.randomCS_DKHTL(check, typeOption, dokh.template.gender == 3 ? player.gender : dokh.template.gender);
                        InventoryService.gI().addItemBag(player, item, 0);
                        InventoryService.gI().subQuantityItemsBag(player, dokh, 1);
                    }
                    Service.getInstance().sendThongBao(player, "Xịt nhót");
                    player.inventory.ruby -= 10_000;
                    if (manhTL != null) {
                        InventoryService.gI().subQuantityItemsBag(player, manhTL, 1);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Thieu tien roi cu em");
                }
            }
        }
    }

    private void upgradeDisguise(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            Item dis = null;
            Item task = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.id == 711) {
                        dis = item;
                    } else if (item.template.id == 1260) {
                        task = item;
                    }
                }
            }
            if (player.inventory.ruby < 10_000) {
                Service.getInstance().sendThongBao(player, "khong du tien");
                reOpenItemCombine(player);
                return;
            }
            if (dis != null && task != null && task.quantity >= 99) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    player.inventory.ruby -= 10_000;
                    int tile = 10;
                    if (Util.isTrue(tile, 100)) {
                        sendEffectSuccessCombine(player);
                        Item item = ItemService.gI().Disguise();
                        InventoryService.gI().addItemBag(player, item, 0);
                        InventoryService.gI().subQuantityItemsBag(player, dis, 1);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryService.gI().subQuantityItemsBag(player, task, 99);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
                }
            }
        } else {
            Service.getInstance().sendThongBao(player, "default");
        }
    }

    private void tradeDoThanLinh(Player player) {
        if (player.combineNew.itemsCombine.size() != 1) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (player.inventory.ruby < COST_RUBY_TRADE_TL) {
            Service.getInstance().sendThongBao(player, "Nạp tiền vào donate cho taooo!");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
            return;
        }
        if (player.combineNew.itemsCombine.size() == 1) {
            Item doTL = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.id >= 555 && item.template.id <= 567) {
                        doTL = item;
                    }
                }
            }
            if (doTL == null) {
                Service.getInstance().sendThongBao(player, "Thiếu đồ!!");
                return;
            }
            if (doTL.quantity < 1) {
                Service.getInstance().sendThongBao(player, "Cần x1 món đồ Thần Linh bất kì!");
                return;
            }
            if (doTL != null && doTL.quantity >= 1) {
                int tile = 50;
                Item itemMTL = ItemService.gI().createNewItem((short) Util.nextInt(2032, 2036));
                sendEffectSuccessCombineDoTS(player, itemMTL.template.iconID);
                InventoryService.gI().addItemBag(player, itemMTL, 0);
                InventoryService.gI().subQuantityItemsBag(player, doTL, 1);
                player.inventory.ruby -= COST_RUBY_TRADE_TL;
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                player.combineNew.itemsCombine.clear();
                reOpenItemCombine(player);
            }
        }

    }

    private void doiManhKichHoat(Player player) {
        if (player.combineNew.itemsCombine.size() == 2 || player.combineNew.itemsCombine.size() == 3) {
            Item nr1s = null, doThan = null, buaBaoVe = null;
            for (Item it : player.combineNew.itemsCombine) {
                if (it.template.id == 14) {
                    nr1s = it;
                } else if (it.template.id == 2010) {
                    buaBaoVe = it;
                } else if (it.template.id >= 555 && it.template.id <= 567) {
                    doThan = it;
                }
            }
            if (nr1s != null && doThan != null) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0 && player.inventory.gold >= COST_DOI_MANH_KICH_HOAT) {
                    player.inventory.gold -= COST_DOI_MANH_KICH_HOAT;
                    int tiLe = buaBaoVe != null ? 100 : 50;
                    if (Util.isTrue(tiLe, 100)) {
                        sendEffectSuccessCombine(player);
                        Item item = ItemService.gI().createNewItem((short) 2009);
                        item.itemOptions.add(new ItemOption(30, 0));
                        InventoryService.gI().addItemBag(player, item, 0);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryService.gI().subQuantityItemsBag(player, nr1s, 1);
                    InventoryService.gI().subQuantityItemsBag(player, doThan, 1);
                    if (buaBaoVe != null) {
                        InventoryService.gI().subQuantityItemsBag(player, buaBaoVe, 1);
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            } else {
                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Hãy chọn 1 trang bị thần linh và 1 viên ngọc rồng 1 sao", "Đóng");
            }
        }
    }

    private void doiVeHuyDiet(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item item = player.combineNew.itemsCombine.get(0);
            if (item.isNotNullItem() && item.template.id >= 555 && item.template.id <= 567) {
                if (InventoryService.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_DOI_VE_DOI_DO_HUY_DIET) {
                    player.inventory.gold -= COST_DOI_VE_DOI_DO_HUY_DIET;
                    Item ticket = ItemService.gI().createNewItem((short) (2001 + item.template.type));
                    ticket.itemOptions.add(new ItemOption(30, 0));
                    InventoryService.gI().subQuantityItemsBag(player, item, 1);
                    InventoryService.gI().addItemBag(player, ticket, 99);
                    sendEffectOpenItem(player, item.template.iconID, ticket.template.iconID);

                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void epSaoTrangBi(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gem < gem) {
                Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            Item trangBi = null;
            Item daPhaLe = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (isTrangBiEpSaoPhale(item)) {
                    trangBi = item;
                } else if (isDaPhaLe(item)) {
                    daPhaLe = item;
                }
            }
            int star = 0; // sao pha lê đã ép
            int starEmpty = 0; // lỗ sao pha lê
            if (trangBi != null && daPhaLe != null) {
                ItemOption optionStar = null;
                for (ItemOption io : trangBi.itemOptions) {
                    if (io.optionTemplate.id == 102) {
                        star = io.param;
                        optionStar = io;
                    } else if (io.optionTemplate.id == 107) {
                        starEmpty = io.param;
                    }
                }
                if (star < starEmpty) {
                    player.inventory.subGem(gem);
                    int optionId = getOptionDaPhaLe(daPhaLe);
                    int param = getParamDaPhaLe(daPhaLe);
                    ItemOption option = null;
                    for (ItemOption io : trangBi.itemOptions) {
                        if (io.optionTemplate.id == optionId) {
                            option = io;
                            break;
                        }
                    }
                    if (option != null) {
                        option.param += param;
                    } else {
                        trangBi.itemOptions.add(new ItemOption(optionId, param));
                    }
                    if (optionStar != null) {
                        optionStar.param++;
                    } else {
                        trangBi.itemOptions.add(new ItemOption(102, 1));
                    }

                    InventoryService.gI().subQuantityItemsBag(player, daPhaLe, 1);
                    sendEffectSuccessCombine(player);
                }
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private boolean phaLeHoaTrangBi(Player player) {
        boolean flag = false;
        if (!player.combineNew.itemsCombine.isEmpty()) {
            Item item = player.combineNew.itemsCombine.get(0);
            int gold = player.combineNew.goldCombine;
            int gem = player.combineNew.gemCombine;
            int ruby = 200;
            if (isTrangBiPhaLeHoa(item)) {
                int star = 0;
                ItemOption optionStar = null;
                ItemOption khoagd = null;
                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 107) {
                        star = io.param;
                        optionStar = io;
                        break;
                    } else if (io.optionTemplate.id == 30) {
                        khoagd = io;
                    }
                }
                if (star == 8 && player.inventory.ruby < ruby) {
                    Service.getInstance().sendThongBao(player, "Không đủ ruby để thực hiện");
                    return false;
                }
                if (player.inventory.gold < gold) {
                    Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                    return false;
                }
                if (player.inventory.gem < gem) {
                    Service.getInstance().sendThongBao(player, "Không đủ ngọc để thực hiện");
                    return false;
                }
                if (star < MAX_STAR_ITEM) {
                    if (star == 8) {
                        player.inventory.ruby -= ruby;
                        if (Util.isTrue(1, 100)) {
                            if (optionStar == null) {
                                item.itemOptions.add(new ItemOption(107, 1));
                            } else {
                                if (khoagd == null) {
                                    item.itemOptions.add(new ItemOption(30, 0));
                                }
                                optionStar.param++;
                            }
                            flag = true;
                        } else {
                            sendEffectFailCombine(player);
                        }
                    } else {
                        player.inventory.gold -= gold;
                        player.inventory.subGem(gem);

                        if (optionStar == null) {
                            item.itemOptions.add(new ItemOption(107, 1));
                        } else {
                            optionStar.param++;
                        }
                        sendEffectSuccessCombine(player);
                        if (optionStar != null && optionStar.param >= 7) {
                            ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa " + "thành công "
                                    + item.template.name + " lên " + optionStar.param + " sao pha lê");
                            ServerLog.logCombine(player.name, item.template.name, optionStar.param);
                        }
                        flag = true;
                    }
                }
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
        return flag;
    }

    public boolean isItemPhaLeHoa(Item it) {
        return it.template.id == 2053;
    }

    public boolean isItemCaiTrang(Item it) {
        return it.template.type == 5;
    }

    private boolean phaLeHoaCaiTrang(Player player) {
        boolean flag = false;
        if (!player.combineNew.itemsCombine.isEmpty()) {
            Item item = player.combineNew.itemsCombine.get(0);
            int ruby = 10_000;
            if (isItemCaiTrang(item)) {
                int star = 0;
                ItemOption optionStar = null;
                Item hoa = null;
                for (Item it : player.combineNew.itemsCombine) {
                    if (it.isNotNullItem()) {
                        switch (it.template.id) {
                            case 2063:
                                hoa = it;
                                break;
                        }
                    }
                }
                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 107) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }
                if (hoa != null || hoa.quantity < 99) {
                    if (player.inventory.ruby < ruby) {
                        Service.getInstance().sendThongBao(player, "Không đủ ruby để thực hiện");
                        return false;
                    }
                    if (star < MAX_SAO_FLAG_BAG) {
                        player.inventory.ruby -= ruby;
                        InventoryService.gI().subQuantityItemsBag(player, hoa, 99);
                        if (Util.isTrue(20, 100)) {
                            if (optionStar == null) {
                                item.itemOptions.add(new ItemOption(107, 1));
                            } else {
                                optionStar.param++;
                            }
                            flag = true;
                        } else {
                            sendEffectFailCombine(player);
                        }
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
        return flag;
    }

    private boolean phalehoaDisguise(Player player) {
        boolean flag = false;
        if (!player.combineNew.itemsCombine.isEmpty()) {
            Item item = player.combineNew.itemsCombine.get(0);
            int ruby = 10_000;
            if (isItemPhaLeHoa(item)) {
                int star = 0;
                ItemOption optionStar = null;
                Item capsule = null;
                for (Item it : player.combineNew.itemsCombine) {
                    if (it.isNotNullItem()) {
                        switch (it.template.id) {
                            case 869:
                                capsule = it;
                                break;
                        }
                    }
                }
                for (ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 107) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }
                if (capsule != null || capsule.quantity < 9) {
                    if (player.inventory.ruby < ruby) {
                        Service.getInstance().sendThongBao(player, "Không đủ ruby để thực hiện");
                        return false;
                    }
                    if (star < MAX_SAO_FLAG_BAG) {
                        player.inventory.ruby -= ruby;
                        InventoryService.gI().subQuantityItemsBag(player, capsule, 9);
                        if (Util.isTrue(89, 100)) {
                            if (optionStar == null) {
                                item.itemOptions.add(new ItemOption(107, 1));
                            } else {
                                optionStar.param++;
                            }
                            flag = true;
                        } else {
                            sendEffectFailCombine(player);
                        }
                    }
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
        return flag;
    }

    private void nhapNgocRong(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                if (item != null && item.isNotNullItem()) {
                    if ((item.template.id > 14 && item.template.id <= 20) && item.quantity >= 7) {
                        Item nr = ItemService.gI().createNewItem((short) (item.template.id - 1));
                        InventoryService.gI().addItemBag(player, nr, 0);
                        InventoryService.gI().subQuantityItemsBag(player, item, 7);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);
                        sendEffectCombineDB(player, item.template.iconID);
                        return;
                    }
                    if (player.inventory.gold >= 500000000) {
                        if (item.template.id == 14 && item.quantity >= 7) {
                            Item nr = ItemService.gI().createNewItem((short) (1015));
                            InventoryService.gI().addItemBag(player, nr, 0);
                            sendEffectCombineDB(player, (short) 9650);
                        } else if (item.template.id == 926 && item.quantity >= 7) {
                            Item nr = ItemService.gI().createNewItem((short) (925));
                            nr.itemOptions.add(new ItemOption(93, 70));
                            InventoryService.gI().addItemBag(player, nr, 0);
                            sendEffectCombineDB(player, item.template.iconID);
                        }
                        InventoryService.gI().subQuantityItemsBag(player, item, 7);
                        player.inventory.gold -= 500000000;
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu " + Util.numberToMoney(500000000 - player.inventory.gold) + " vàng");
                    }
                }
            }
        }
    }

    private void epNgocRongBang(Player player) {
//        System.out.println("loggggggggggg");
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                if (item != null && item.isNotNullItem()) {
                    if ((item.template.id > 925 && item.template.id <= 931) && item.quantity >= 7) {
                        Item nr = ItemService.gI().createNewItem((short) (item.template.id - 1));
                        InventoryService.gI().addItemBag(player, nr, 0);
                        InventoryService.gI().subQuantityItemsBag(player, item, 7);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);
                        sendEffectCombineDB(player, item.template.iconID);
                        return;
                    }
                    if (player.inventory.ruby >= 1000) {
                        if (item.template.id == 2009 && item.quantity >= 7) {
                            Item nr = ItemService.gI().createNewItem((short) (931));
                            InventoryService.gI().addItemBag(player, nr, 0);
                            sendEffectCombineDB(player, (short) 8585);
                        }
                        InventoryService.gI().subQuantityItemsBag(player, item, 7);
                        player.inventory.ruby -= 1000;
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        reOpenItemCombine(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ ruby, còn thiếu " + Util.numberToMoney(1000 - player.inventory.ruby) + " ruby");
                    }
                }
            }
        }
    }

    private boolean checkStone(Item linhthu, Item stone) {
        if (linhthu != null && stone != null) {
            if (linhthu.template.id == 2014 && stone.template.id == 1977) {//Aether 
                return true;
            } else if (linhthu.template.id == 2015 && stone.template.id == 1978) {//Aurora 
                return true;
            } else if (linhthu.template.id == 2016 && stone.template.id == 1979) {//Power 
                return true;
            } else if (linhthu.template.id == 2017 && stone.template.id == 1980) {//Mind 
                return true;
            } else if (linhthu.template.id == 2018 && stone.template.id == 1981) {//Space 
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isStone(Item item) {// da
        return item.template.id >= 1977 && item.template.id <= 1981;
    }

    private boolean isLinhThu(Item item) {// xac dinh item linh thu
        return item.template.id >= 2014 && item.template.id <= 2018;
    }

    private int getTile(Item ttt) {// tile thanh cong
        int tile = 0;
        if (ttt.template.id == 2031) {
            tile = 20;
        } else if (ttt.quantity >= 10) {
            tile = Util.nextInt(20, 40);
        }
        return tile;
    }

    private void upgradeLinhThu(Player player) {
        if (player.combineNew.itemsCombine.size() == 4) {
            Item itemLinhThu = null;
            Item stone = null;
            Item HLT = null;// hon linh thu
            Item TTT = null;// thang tinh thach
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    switch (item.template.id) {
                        case 2029:
                            HLT = item;
                            break;
                        case 2031:
                            TTT = item;
                            break;
                    }
                    if (isLinhThu(item)) {
                        itemLinhThu = item;
                    } else if (isStone(item)) {
                        stone = item;
                    }
                }
            }
            if (checkStone(itemLinhThu, stone) && HLT != null) {
                if (player.inventory.ruby < 10_000) {
                    Service.getInstance().sendThongBao(player, "Không đủ ruby để thực hiện");
                    reOpenItemCombine(player);
                    return;
                }
                player.inventory.ruby -= 10_000;
                int tile = getTile(TTT);
                if (Util.isTrue(tile, 100)) {
                    itemLinhThu.itemOptions.clear();
                    switch (itemLinhThu.template.id) {
                        case 2014:// hoa
                            itemLinhThu.itemOptions.add(new ItemOption(50, Util.nextInt(7, 45)));
                            itemLinhThu.itemOptions.add(new ItemOption(168, 0));
                            break;
                        case 2015:
                            itemLinhThu.itemOptions.add(new ItemOption(94, Util.nextInt(7, 45)));
                            itemLinhThu.itemOptions.add(new ItemOption(192, 0));
                            break;
                        case 2016:
                            itemLinhThu.itemOptions.add(new ItemOption(77, Util.nextInt(7, 45)));
                            itemLinhThu.itemOptions.add(new ItemOption(80, Util.nextInt(30, 70)));
                            break;
                        case 2017:
                            itemLinhThu.itemOptions.add(new ItemOption(108, Util.nextInt(7, 45)));
                            itemLinhThu.itemOptions.add(new ItemOption(111, 0));
                            break;
                        case 2018:// 13 23
                            itemLinhThu.itemOptions.add(new ItemOption(103, Util.nextInt(7, 45)));
                            itemLinhThu.itemOptions.add(new ItemOption(173, Util.nextInt(30, 70)));
                            break;
                    }
                    sendEffectSuccessCombine(player);
                } else {
                    Service.getInstance().sendThongBao(player, "Xịt nhót");
                    sendEffectFailCombine(player);
                }
                InventoryService.gI().subQuantityItemsBag(player, HLT, 99);
                InventoryService.gI().subQuantityItemsBag(player, stone, 1);
                if (TTT.quantity < 10) {
                    InventoryService.gI().subQuantityItemsBag(player, TTT, 1);
                } else {
                    InventoryService.gI().subQuantityItemsBag(player, TTT, 10);
                }
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            } else {
                Service.getInstance().sendThongBao(player, "Không đúng Hệ với Linh Thú hoặc thiếu Hồn Linh Thú");
            }
        }
    }

    private void nangCapVatPham(Player player) {
        if (player.combineNew.itemsCombine.size() >= 2 && player.combineNew.itemsCombine.size() < 4) {
            if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type < 5).count() != 1) {
                return;
            }
            if (player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.type == 14).count() != 1) {
                return;
            }
            if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.stream().filter(item -> item.isNotNullItem() && item.template.id == 1143).count() != 1) {
                return;
            }
            Item itemDo = null;
            Item itemDNC = null;
            Item itemDBV = null;
            for (int j = 0; j < player.combineNew.itemsCombine.size(); j++) {
                if (player.combineNew.itemsCombine.get(j).isNotNullItem()) {
                    if (player.combineNew.itemsCombine.size() == 3 && player.combineNew.itemsCombine.get(j).template.id == 1143) {
                        itemDBV = player.combineNew.itemsCombine.get(j);
                        continue;
                    }
                    if (player.combineNew.itemsCombine.get(j).template.type < 5) {
                        itemDo = player.combineNew.itemsCombine.get(j);
                    } else {
                        itemDNC = player.combineNew.itemsCombine.get(j);
                    }
                }
            }
            if (isCoupleItemNangCapCheck(itemDo, itemDNC)) {
                int countDaNangCap = player.combineNew.countDaNangCap;
                int gold = player.combineNew.goldCombine;
                short countDaBaoVe = player.combineNew.countDaBaoVe;
                if (player.inventory.gold < gold) {
                    Service.getInstance().sendThongBao(player, "Không đủ vàng để thực hiện");
                    return;
                }
                if (itemDNC.quantity < countDaNangCap) {
                    return;
                }
                if (player.combineNew.itemsCombine.size() == 3) {
                    if (Objects.isNull(itemDBV)) {
                        return;
                    }
                    if (itemDBV.quantity < countDaBaoVe) {
                        return;
                    }
                }
                int level = 0;
                ItemOption optionLevel = null;
                for (ItemOption io : itemDo.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (level < MAX_LEVEL_ITEM) {
                    player.inventory.gold -= gold;
                    ItemOption option = null;
                    ItemOption option2 = null;
                    for (ItemOption io : itemDo.itemOptions) {
                        if (io.optionTemplate.id == 47
                                || io.optionTemplate.id == 6
                                || io.optionTemplate.id == 0
                                || io.optionTemplate.id == 7
                                || io.optionTemplate.id == 14
                                || io.optionTemplate.id == 22
                                || io.optionTemplate.id == 23) {
                            option = io;
                        } else if (io.optionTemplate.id == 27 || io.optionTemplate.id == 28) {
                            option2 = io;
                        }
                    }
                    if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                        if (option.optionTemplate.id == 14) {
                            option.param += 1;
                        } else {
                            option.param += (option.param * 10 / 100);
                        }
                        if (option2 != null) {
                            option2.param += (option2.param * 10 / 100);
                        }
                        if (optionLevel == null) {
                            itemDo.itemOptions.add(new ItemOption(72, 1));
                        } else {
                            optionLevel.param++;
                        }
                        if (optionLevel != null && optionLevel.param >= 5) {
                            ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa nâng cấp "
                                    + "thành công " + itemDo.template.name + " lên +" + optionLevel.param);
                        }
                        sendEffectSuccessCombine(player);
                    } else {
                        if ((level == 2 || level == 4 || level == 6) && (player.combineNew.itemsCombine.size() != 3)) {
                            if (option.optionTemplate.id == 14) {
                                option.param -= 1;
                            } else {
                                option.param -= (option.param * 15 / 100);
                            }
                            if (option2 != null) {
                                option2.param -= (option2.param * 15 / 100);
                            }
                            optionLevel.param--;
                        }
                        sendEffectFailCombine(player);
                    }
                    if (player.combineNew.itemsCombine.size() == 3) {
                        InventoryService.gI().subQuantityItemsBag(player, itemDBV, countDaBaoVe);
                    }
                    InventoryService.gI().subQuantityItemsBag(player, itemDNC, player.combineNew.countDaNangCap);
                    InventoryService.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private boolean isCoupleItemNangCapCheck(Item trangBi, Item daNangCap) {
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 220) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // --------------------------------------------------------------------------
    /**
     * Hiệu ứng mở item
     *
     * @param player
     */
    public void sendEffectOpenItem(Player player, short icon1, short icon2) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_ITEM);
            msg.writer().writeShort(icon1);
            msg.writer().writeShort(icon2);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiệu ứng đập đồ thành công
     *
     * @param player
     */
    private void sendEffectSuccessCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_SUCCESS);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void sendEffectCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(8);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hiệu ứng đập đồ thất bại
     *
     * @param player
     */
    private void sendEffectFailCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_FAIL);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private void sendEffectFailCombineDoTS(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(8);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi lại danh sách đồ trong tab combine
     *
     * @param player
     */
    private void reOpenItemCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(REOPEN_TAB_COMBINE);
            msg.writer().writeByte(player.combineNew.itemsCombine.size());
            for (Item it : player.combineNew.itemsCombine) {
                for (int j = 0; j < player.inventory.itemsBag.size(); j++) {
                    if (it == player.inventory.itemsBag.get(j)) {
                        msg.writer().writeByte(j);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiệu ứng ghép ngọc rồng
     *
     * @param player
     * @param icon
     */
    private void sendEffectCombineDB(Player player, short icon) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_DRAGON_BALL);
            msg.writer().writeShort(icon);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    private int getCountDaBaoVe(int level) {
        return level + 1;
    }

    // --------------------------------------------------------------------------Ratio,
    // cost combine
    private int getRatioDaMayMan(int id) {
        switch (id) {
            case 1079:
                return 10;
            case 1080:
                return 20;
            case 1081:
                return 30;
            case 1082:
                return 40;
            case 1083:
                return 50;
        }
        return 0;
    }

    private int getRatioDaNangCap(int id) {
        switch (id) {
            case 1074:
                return 10;
            case 1075:
                return 20;
            case 1076:
                return 30;
            case 1077:
                return 40;
            case 1078:
                return 50;
        }
        return 0;
    }

    private int getGoldPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 5000000;
            case 1:
                return 10000000;
            case 2:
                return 20000000;
            case 3:
                return 40000000;
            case 4:
                return 60000000;
            case 5:
                return 90000000;
            case 6:
                return 120000000;
            case 7:
                return 200000000;
        }
        return 0;
    }

    private float getRatioPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 80f;
            case 1:
                return 50f;
            case 2:
                return 40f;
            case 3:
                return 30f;
            case 4:
                return 15f;
            case 5:
                return 5f;
            case 6:
                return 1f;
            case 7:
                return 1f;
            case 8:
                return 1f;
        }
        return 0;
    }

    private int getGemPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 30;
            case 3:
                return 40;
            case 4:
                return 50;
            case 5:
                return 60;
            case 6:
                return 70;
            case 7:
                return 80;
            case 8:
                return 90;
        }
        return 0;
    }

    private int getGemEpSao(int star) {
        switch (star) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 5;
            case 3:
                return 10;
            case 4:
                return 25;
            case 5:
                return 50;
            case 6:
                return 100;
        }
        return 0;
    }

    private int getTileNangCapDo(int level) {
        switch (level) {
            case 0:
                return 80;
            case 1:
                return 50;
            case 2:
                return 20;
            case 3:
                return 10;
            case 4:
                return 7;
            case 5:
                return 5;
            case 6:
                return 1;
        }
        return 0;
    }

    private int getCountDaNangCapDo(int level) {
        switch (level) {
            case 0:
                return 3;
            case 1:
                return 7;
            case 2:
                return 11;
            case 3:
                return 17;
            case 4:
                return 23;
            case 5:
                return 35;
            case 6:
                return 50;
        }
        return 0;
    }

    private int lvbt(Item bongtai) {
        switch (bongtai.template.id) {
            case 454:
                return 1;
            case 921:
                return 2;
            case 1995:
                return 3;
        }
        return 0;

    }

    private int getGoldNangCapDo(int level) {
        switch (level) {
            case 0:
                return 10000;
            case 1:
                return 70000;
            case 2:
                return 300000;
            case 3:
                return 1500000;
            case 4:
                return 7000000;
            case 5:
                return 23000000;
            case 6:
                return 100000000;
        }
        return 0;
    }

    // --------------------------------------------------------------------------check
    public boolean isAngelClothes(int id) {
        if (id >= 1048 && id <= 1062) {
            return true;
        }
        return false;
    }

    public boolean isDestroyClothes(int id) {
        if (id >= 650 && id <= 662) {
            return true;
        }
        return false;
    }

    private String getTypeTrangBi(int type) {
        switch (type) {
            case 0:
                return "Áo";
            case 1:
                return "Quần";
            case 2:
                return "Găng";
            case 3:
                return "Giày";
            case 4:
                return "Nhẫn";
        }
        return "";
    }

    public boolean isManhTrangBi(Item it) {
        switch (it.template.id) {
            case 1066:
            case 1067:
            case 1068:
            case 1069:
            case 1070:
                return true;
        }
        return false;
    }

    public boolean isCraftingRecipe(int id) {
        switch (id) {
            case 1071:
            case 1072:
            case 1073:
            case 1084:
            case 1085:
            case 1086:
                return true;
        }
        return false;
    }

    public int getRatioCraftingRecipe(int id) {
        switch (id) {
            case 1071:
                return 0;
            case 1072:
                return 0;
            case 1073:
                return 0;
            case 1084:
                return 10;
            case 1085:
                return 10;
            case 1086:
                return 10;
        }
        return 0;
    }

    public boolean isUpgradeStone(int id) {
        switch (id) {
            case 1074:
            case 1075:
            case 1076:
            case 1077:
            case 1078:
                return true;
        }
        return false;
    }

    public int getRatioUpgradeStone(int id) {
        switch (id) {
            case 1074:
                return 10;
            case 1075:
                return 20;
            case 1076:
                return 30;
            case 1077:
                return 40;
            case 1078:
                return 50;
        }
        return 0;
    }

    public boolean isLuckyStone(int id) {
        switch (id) {
            case 1079:
            case 1080:
            case 1081:
            case 1082:
            case 1083:
                return true;
        }
        return false;
    }

    private int getGoldnangbt(int lvbt) {
        return GOLD_BONG_TAI2;
    }

    private int getRubyUpgradeBT(int lvbt) {
        return RUBY_BONG_TAI2;
    }

    private int getcountmvbtnangbt(int lvbt) {
        return 99;
    }

    private boolean checkbongtai(Item item) {
        if (item.template.id == 454 || item.template.id == 921) {
            return true;
        }
        return false;
    }

    public int getRatioLuckyStone(int id) {
        switch (id) {
            case 1079:
                return 10;
            case 1080:
                return 20;
            case 1081:
                return 30;
            case 1082:
                return 40;
            case 1083:
                return 50;
        }
        return 0;
    }

    private boolean isCoupleItemNangCap(Item item1, Item item2) {
        Item trangBi = null;
        Item daNangCap = null;
        if (item1 != null && item1.isNotNullItem()) {
            if (item1.template.type < 5) {
                trangBi = item1;
            } else if (item1.template.type == 14) {
                daNangCap = item1;
            }
        }
        if (item2 != null && item2.isNotNullItem()) {
            if (item2.template.type < 5) {
                trangBi = item2;
            } else if (item2.template.type == 14) {
                daNangCap = item2;
            }
        }
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 220) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDaPhaLe(Item item) {
        if (item != null && item.isNotNullItem()) {
            return item.template.type == 30 || (item.template.id >= 14 && item.template.id <= 20);
        }
        return false;
    }

    private boolean isTrangBiPhaLeHoa(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.type < 5 || item.template.type == 32) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isTrangBiEpSaoPhale(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.type < 5 || item.template.type == 32 || item.template.id == 2053) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int getParamDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).param;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 5; // +5%hp
            case 19:
                return 5; // +5%ki
            case 18:
                return 5; // +5%hp/30s
            case 17:
                return 5; // +5%ki/30s
            case 16:
                return 3; // +3%sđ
            case 15:
                return 2; // +2%giáp
            case 14:
                return 2; // +2%né đòn
            default:
                return -1;
        }
    }

    private int getOptionDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).optionTemplate.id;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 77;
            case 19:
                return 103;
            case 18:
                return 80;
            case 17:
                return 81;
            case 16:
                return 50;
            case 15:
                return 94;
            case 14:
                return 108;
            default:
                return -1;
        }
    }

    /**
     * Trả về id item c0
     *
     * @param gender
     * @param type
     * @return
     */
    private int getTempIdItemC0(int gender, int type) {
        if (type == 4) {
            return 12;
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return 0;
                    case 1:
                        return 6;
                    case 2:
                        return 21;
                    case 3:
                        return 27;
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return 1;
                    case 1:
                        return 7;
                    case 2:
                        return 22;
                    case 3:
                        return 28;
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return 2;
                    case 1:
                        return 8;
                    case 2:
                        return 23;
                    case 3:
                        return 29;
                }
                break;
        }
        return -1;
    }

    // Trả về tên đồ c0
    private String getNameItemC0(int gender, int type) {
        if (type == 4) {
            return "Rada cấp 1";
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return "Áo vải 3 lỗ";
                    case 1:
                        return "Quần vải đen";
                    case 2:
                        return "Găng thun đen";
                    case 3:
                        return "Giầy nhựa";
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return "Áo sợi len";
                    case 1:
                        return "Quần sợi len";
                    case 2:
                        return "Găng sợi len";
                    case 3:
                        return "Giầy sợi len";
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return "Áo vải thô";
                    case 1:
                        return "Quần vải thô";
                    case 2:
                        return "Găng vải thô";
                    case 3:
                        return "Giầy vải thô";
                }
                break;
        }
        return "";
    }

    public int get_Tile_nang_Do_TS(Item danc, Item congthuc) {
        int tile = 0;
        if (congthuc.isCongthucVip()) {
            tile = 35;
        } else if (congthuc.isCongthucNomal()) {
            tile = 20;
        }
        if (danc != null && danc.isdanangcapDoTs()) {
            tile += (danc.template.id - 1073) * 10;
        }
        return tile;
    }

    // --------------------------------------------------------------------------Text
    // tab combine
    private String getTextTopTabCombine(int type) {
        switch (type) {
            case PHA_LE_HOA_CAI_TRANG:
                return "Ta sẽ giúp ngươi làm điều đó =)))";
            case PHA_LE_HOA_DISGUISE:
                return "Ta sẽ giúp ngươi làm điều đó =)))";
            case TRADE_PET:
                return "Ta sẽ giúp ngươi làm điều đó =)))";
            case UPGRADE_PET:
                return "Ta sẽ giúp ngươi làm điều đó =)))";
            case UPGRADE_THAN_LINH:
                return "Ta sẽ giúp ngươi\n làm điều đó";
            case UPGRADE_LINHTHU:
                return "Ta sẽ giúp ngươi\n làm điều đó";
            case UPGRADE_CAITRANG:
                return "Ta sẽ giúp ngươi\n làm điều đó";
            case NANG_CAP_DO_KICH_HOAT:
                return "Ta sẽ giúp ngươi\n làm điều đó";
            case NANG_CAP_DO_TS:
                return "Chế tạo trang bị thiên sứ!";
            case TRADE_DO_THAN_LINH:
                return "Ta sẽ giúp ngươi trao đổi đồ Thần Linh này\nsang những mảnh Thần Linh";
            case EP_SAO_TRANG_BI:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở lên mạnh mẽ";
            case PHA_LE_HOA_TRANG_BI:
            case PHA_LE_HOA_TRANG_BI_X10:
                return "Ta sẽ phù phép\ncho trang bị của ngươi\ntrở thành trang bị pha lê";
            case NHAP_NGOC_RONG:
                return "Ta sẽ phù phép\ncho 7 viên Ngọc Rồng\nthành 1 viên Ngọc Rồng cấp cao";
            case EP_NGOC_RONG_BANG:
                return "Ta sẽ phù phép\ncho 7 viên Ngọc Rồng Băng\nthành 1 viên Ngọc Rồng cấp cao";
            case NANG_CAP_VAT_PHAM:
                return "Ta sẽ phù phép cho trang bị của ngươi trở lên mạnh mẽ";
            case DOI_VE_HUY_DIET:
                return "Ta sẽ đưa ngươi 1 vé đổi đồ\nhủy diệt, đổi lại ngươi phải đưa ta\n 1 món đồ thần linh tương ứng";
            case DAP_SET_KICH_HOAT:
                return "Ta sẽ giúp ngươi chuyển hóa\n1 món đồ hủy diệt\nthành 1 món đồ kích hoạt";
            // case DOI_MANH_KICH_HOAT:
            // return "Ta sẽ giúp ngươi biến hóa\nviên ngọc 1 sao và 1 món đồ\nthần linh
            // thành mảnh kích hoạt";
            case DAP_SET_KICH_HOAT_CAO_CAP:
                return "Ta sẽ giúp ngươi chuyển hóa\n3 món đồ hủy diệt\nthành 1 món đồ kích hoạt cao cấp";
            case GIA_HAN_CAI_TRANG:
                return "Ta sẽ phù phép\n cho trang bị của mi\n thêm hạn sử dụng";
            case NANG_CAP_DO_THIEN_SU:
                return "Nâng cấp\n trang bị thiên sứ";
            case NANG_CAP_BONG_TAI:
                return "Ta sẽ phù phép\ncho bông tai Porata của ngươi\nthành cấp 2";
            case NANG_CAP_BONG_TAI_CAP3:
                return "Ta sẽ phù phép\ncho bông tai Porata của ngươi\nthành cấp 3";
            case MO_CHI_SO_BONG_TAI:
                return "Ta sẽ phù phép\ncho bông tai Porata cấp 2 của ngươi\ncó 1 chỉ số ngẫu nhiên";
            default:
                return "";
        }
    }

    private String getTextInfoTabCombine(int type) {
        switch (type) {
            case PHA_LE_HOA_CAI_TRANG:
                return "Con hãy đưa cho ta 1 Cải trang bất kì\nVaf x99 Hoa cúc vàng";
            case PHA_LE_HOA_DISGUISE:
                return "Con hãy đưa ta x1 Kiếm Hasakiiii mà muốn pha lê hóa\nVà x9 capsule 1 Sao";
            case TRADE_PET:
                return "Con hãy đưa cho ta x10 Gậy thượng đế\nTa sẽ giúp con chuyển đệ Black gâu ku\nThanh đệ tử Whis";
            case UPGRADE_PET:
                return "Vào hành trang chọn x10 Đá Nguyền Ấn\nvà 5k ruby";
            case UPGRADE_THAN_LINH:
                return "Vào hành trang\nChọn 1 món Thần Linh bất kì\n "
                        + " và 1 món SKH bất kỳ\n"
                        + "Chỉ cần chọn 'Nâng Cấp'";
            case UPGRADE_LINHTHU:
                return "Vào hàng trang\nChọn x1 Linh thú bất kì, x1 Stone cùng hệ Linh Thú\nx1 Thăng Tinh Thạch\nx10 Thăng Tinh Thạch để tỉ lệ Thành Công cao hơn\nx99 Hồn Linh Thú\nXong rồi ấn Nâng Cấp";
            case UPGRADE_CAITRANG:
                return "Vào hành trang\nChọn x1 cải trang jaki chun\nvà  Lọ nước hồi sinh";
            case NANG_CAP_DO_KICH_HOAT:
                return "Vào hành trang\nChọn 5 mảnh Thần Linh bất kì\n "
                        + " và 1 món SKH thường bất kỳ\n"
                        + "Chỉ cần chọn 'Nâng Cấp'";
            case EP_SAO_TRANG_BI:
                return "Chọn trang bị\n(Áo, quần, găng, giày hoặc rađa) có ô đặt sao pha lê\nChọn loại sao pha lê\n"
                        + "Sau đó chọn 'Nâng cấp'";
            case TRADE_DO_THAN_LINH:
                return "Chọn trang bị\n(Áo, quần, găng, giày hoặc nhẫn)\n"
                        + "Sau đó chọn 'Nâng cấp'";
            case PHA_LE_HOA_TRANG_BI:
                return "Chọn trang bị\n(Áo, quần, găng, giày hoặc rađa)\nSau đó chọn 'Nâng cấp'";
            case PHA_LE_HOA_TRANG_BI_X10:
                return "Chọn trang bị\n(Áo, quần, găng, giày hoặc rađa)\nSau đó chọn 'Nâng cấp'\n Khi nâng cấp thành công hoặc đủ 5 lần thì sẽ dừng lại";
            case NHAP_NGOC_RONG:
                return "Vào hành trang\nChọn 7 viên ngọc cùng sao\nSau đó chọn 'Làm phép'";
            case EP_NGOC_RONG_BANG:
                return "Vào hành trang\nChọn 7 viên ngọc cùng sao\nSau đó chọn 'Làm phép'";
            case NANG_CAP_VAT_PHAM:
                return "vào hành trang\nChọn trang bị\n(Áo, quần, găng, giày hoặc rađa)\nChọn loại đá để nâng cấp\n"
                        + "Sau đó chọn 'Nâng cấp'";
            case DOI_VE_HUY_DIET:
                return "Vào hành trang\nChọn món đồ thần linh tương ứng\n(Áo, quần, găng, giày hoặc nhẫn)\nSau đó chọn 'Đổi'";
            case DAP_SET_KICH_HOAT:
                return "Vào hành trang\nChọn món đồ hủy diệt tương ứng\n(Áo, quần, găng, giày hoặc nhẫn)\n(Có thể thêm 1 món đồ thần linh bất kỳ để tăng tỉ lệ)\nSau đó chọn 'Đập'";
            // case DOI_MANH_KICH_HOAT:
            // return "Vào hành trang\nChọn món đồ thần linh tương ứng\n(Áo, quần, găng,
            // giày hoặc nhẫn)\nSau đó chọn 'Đổi'";
            case DAP_SET_KICH_HOAT_CAO_CAP:
                return "Vào hành trang\nChọn 3 món đồ hủy diệt khác nhau\n(Áo, quần, găng, giày hoặc nhẫn)\nSau đó chọn 'Đập'";
            case GIA_HAN_CAI_TRANG:
                return "Vào hành trang \n Chọn cải trang có hạn sử dụng \n Chọn thẻ gia hạn \n Sau đó chọn gia hạn";
            case NANG_CAP_DO_THIEN_SU:
                return "Cần 1 công thức\nTrang bị thiên sứ\nĐá nâng cấp (tùy chọn)\nĐá may mắn (tùy chọn)";
            case NANG_CAP_BONG_TAI:
                return "Vào hành trang\nChọn bông tai Porata\nChọn mảnh bông tai để nâng cấp, số lượng\n99 cái\nSau đó chọn 'Nâng cấp'";
            case NANG_CAP_BONG_TAI_CAP3:
                return "Vào hành trang"
                        + "\nChọn bông tai Porata cấp 2"
                        + "\nChọn X99 hoa hồng xanh và x99 Bình độc dược "
                        + "\nSau đó chọn 'Nâng cấp'";
            case NANG_CAP_DO_TS:
                return "Cần 1 công thức\n "
                        + "Mảnh trang bị tương ứng"
                        + "1 đá nâng cấp (tùy chọn)\n"
                        + "1 đá may mắn (tùy chọn)\n";
            case MO_CHI_SO_BONG_TAI:
                return "Vào hành trang\nChọn bông tai Porata\nChọn mảnh hồn bông tai số lượng 99 cái\nvà đá xanh lam để nâng cấp\nSau đó chọn 'Nâng cấp'";
            default:
                return "";
        }
    }

    private void sendEffectSuccessCombineDoTS(Player player, short icon) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(7);
            msg.writer().writeShort(icon);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
