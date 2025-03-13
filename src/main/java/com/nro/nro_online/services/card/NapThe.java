package com.nro.nro_online.services.card;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.nro.nro_online.consts.ConstNpc;
import com.nro.nro_online.jdbc.daos.PlayerDAO;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.services.Service;
import nro.consts.ConstNpc;
import nro.jdbc.daos.PlayerDAO;
import nro.models.player.Player;
import nro.services.NpcService;
import nro.services.Service;
import nro.utils.Util;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NapThe {

    private static NapThe I;

    public static NapThe gI() {
        if (NapThe.I == null) {
            NapThe.I = new NapThe();
        }
        return NapThe.I;
    }

    public static void SendCard(Player p, String loaiThe, String menhGia, String soSeri, String maPin) {
        String partnerId = "72461046463";
        String partnerKey = "16502d49bf5e949c3f27238c2a762115";
        String api = MD5Hash(partnerKey + maPin + soSeri);
        int requestID = Util.nextInt(100000000, 999999999);
        String t = String.valueOf(requestID);
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("telco", loaiThe)
                    .addFormDataPart("code", maPin)
                    .addFormDataPart("serial", soSeri)
                    .addFormDataPart("amount", menhGia)
                    .addFormDataPart("request_id", t)
                    .addFormDataPart("partner_id", partnerId)
                    .addFormDataPart("sign", api)
                    .addFormDataPart("command", "charging")
                    .build();
            

            Request request = new Request.Builder().url("https://thesieure.com/chargingws/v2").post(body).addHeader("Content-Type", "application/json").build();
            okhttp3.Response response = client.newCall(request).execute();
            String jsonString = response.body().string();
            Object obj = JSONValue.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            long name = (long) jsonObject.get("status");
            if (name == 99) {
                PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, "Thẻ chờ xử lý", t, "0", loaiThe);
                NpcService.gI().createOtherMenu(p, (p.gender), ConstNpc.IGNORE_MENU, "|4|Gửi thẻ thành công\b"
                        + "|5|Hãy chờ một lúc và thoát game vào lại để cập nhật số tiền.\b"
                        + "|5|Hoặc lên trang chủ và theo dõi lịch sử giao dịch\b"
                        + "|3|Thông tin thẻ\b"
                        + "|1|Seri: " + soSeri + "\b"
                        + "|1|Mã thẻ: " + maPin + "\b"
                        + "|1|Mệnh giá: " + menhGia + "VNĐ\b"
                        + "|1|Thời gian : " + java.time.LocalDate.now() + " " + java.time.LocalTime.now() + "\b"
                        + "|8|Số tiền hiện tại trong tài khoản: " + p.getSession().vnd + "VNĐ", "Đồng ý");
            }
            if (name == 1) {
                PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, "Thẻ thành công đúng mệnh giá", t, "0", loaiThe);
                NpcService.gI().createOtherMenu(p, (p.gender), ConstNpc.IGNORE_MENU, "|4|Gửi thẻ thành công\b"
                        + "|5|Hãy chờ một lúc và thoát game vào lại để cập nhật số tiền.\b"
                        + "|5|Hoặc lên trang chủ và theo dõi lịch sử giao dịch\b"
                        + "|3|Thông tin thẻ\b"
                        + "|1|Seri: " + soSeri + "\b"
                        + "|1|Mã thẻ: " + maPin + "\b"
                        + "|1|Mệnh giá: " + menhGia + "VNĐ\b"
                        + "|1|Thời gian : " + java.time.LocalDate.now() + " " + java.time.LocalTime.now() + "\b"
                        + "|8|Số tiền hiện tại trong tài khoản: " + p.getSession().vnd + "VNĐ", "Đồng ý");
            } else if (name == 2) {
                Service.getInstance().sendThongBao(p, "nạp thành công nhưng sai mệnh giá.con sẽ ko dc cộng tiền \n lần sau ông khóa mẹ acc con cho chừa nhé");
            } else if (name == 3) {
                Service.getInstance().sendThongBao(p, "Bạn đã nhập sai giá trị, hãy nhập đúng nhóe :3");
            } else if (name == 4) {
                Service.getInstance().sendThongBao(p, "Hệ thống nạp bảo trì rồi con");
            } else if (name == 100) {
                Service.getInstance().sendThongBao(p, "Sai seri và mã ping ồi con ơi");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Service.getInstance().sendThongBao(p, "Có lỗi xảy ra vui lòng thử lại, hoặc báo cho Admin");
        }
    }

    private static String MD5Hash(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
