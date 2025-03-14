package com.nro.nro_online.manager;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.item.MiniPetTemplate;
import com.nro.nro_online.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MiniPetManager implements IManager<MiniPetTemplate> {

    private static final MiniPetManager INSTANCE = new MiniPetManager();
    private static final String SELECT_QUERY = "SELECT id_temp, head, body, leg FROM mini_pet";

    private final List<MiniPetTemplate> list = new ArrayList<>();

    public static MiniPetManager gI() {
        return INSTANCE;
    }

    public void load() {
        try (Connection conn = DBService.gI().getConnectionForGame();
                PreparedStatement ps = conn.prepareStatement(SELECT_QUERY);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_temp");
                short head = rs.getShort("head");
                short body = rs.getShort("body");
                short leg = rs.getShort("leg");
                add(new MiniPetTemplate(id, head, body, leg));
            }
            Log.log("Load " + list.size() + " mini pet xong, cute phô mai luôn! 🐾");
        } catch (SQLException e) {
            Log.error(MiniPetManager.class, e, "Load mini pet lỗi, buồn ghê! 😿");
            throw new RuntimeException("Không load được mini pet, chết tui rồi!", e);
        }
    }

    @Override
    public void add(MiniPetTemplate miniPetTemplate) {
        if (miniPetTemplate == null) {
            Log.warning("Thêm mini pet null là sao nổi hả bro? 🤔");
            return;
        }
        if (findById(miniPetTemplate.getId()) == null) {
            list.add(miniPetTemplate);
            Log.log("Thêm mini pet ID " + miniPetTemplate.getId() + " thành công, xịn xò! 🌟");
        } else {
            Log.warning("Mini pet ID " + miniPetTemplate.getId() + " đã có rồi, thêm chi nữa? 😛");
        }
    }

    @Override
    public void remove(MiniPetTemplate miniPetTemplate) {
        if (miniPetTemplate == null || !list.remove(miniPetTemplate)) {
            Log.warning("Xóa mini pet fail, có đâu mà xóa hả? 😕");
        } else {
            Log.log("Xóa mini pet ID " + miniPetTemplate.getId() + " xong, gọn gàng! 🧹");
        }
    }

    @Override
    public MiniPetTemplate findById(int id) {
        return list.stream()
                .filter(temp -> temp.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<MiniPetTemplate> getList() {
        return new ArrayList<>(list); // Trả về bản sao để tránh chỉnh sửa trực tiếp
    }
}