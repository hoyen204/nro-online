package com.nro.nro_online.manager;

import com.nro.nro_online.jdbc.DBService;
import com.nro.nro_online.models.task.AchievementTemplate;
import com.nro.nro_online.utils.Log;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AchieveManager implements IManager<AchievementTemplate> {

    private static final AchieveManager INSTANCE = new AchieveManager();
    private static final String SELECT_QUERY = "SELECT id, name, detail, money, max_count FROM achivements";

    public static AchieveManager getInstance() {
        return INSTANCE;
    }

    @Getter
    private final List<AchievementTemplate> list = new ArrayList<>();

    public void load() {
        try (Connection conn = DBService.gI().getConnectionForGame();
                PreparedStatement ps = conn.prepareStatement(SELECT_QUERY);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String detail = rs.getString("detail");
                int money = rs.getInt("money");
                int maxCount = rs.getInt("max_count");
                list.add(new AchievementTemplate(id, name, detail, money, maxCount));
            }
            Log.log("Load " + list.size() + " thành tựu xong, ngon lành nha! 😎");
        } catch (SQLException ex) {
            Log.error(AchieveManager.class, ex, "Load thành tựu lỗi, khổ ghê! 😭");
        }
    }

    @Override
    public AchievementTemplate findById(int id) {
        return list.stream()
                .filter(template -> template.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void add(AchievementTemplate achievementTemplate) {
        if (achievementTemplate == null) {
            return;
        }
        if (findById(achievementTemplate.getId()) == null) {
            list.add(achievementTemplate);
            Log.log("Thêm thành tựu " + achievementTemplate.getName() + " thành công, đỉnh quá! 🌟");
        }
    }

    @Override
    public void remove(AchievementTemplate achievementTemplate) {
        if (achievementTemplate == null || !list.remove(achievementTemplate)) {
            Log.warning("Xóa thành tựu fail, có gì đâu mà xóa hả? 😛");
        } else {
            Log.log("Xóa thành tựu " + achievementTemplate.getName() + " xong, sạch sẽ! 🧹");
        }
    }
}