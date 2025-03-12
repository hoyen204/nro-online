package com.nro.nro_online.attr;

import java.util.HashMap;
import java.util.Map;

import com.nro.nro_online.utils.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttributeManager {

    @Getter
    private final Map<Integer, Attribute> attributeMap;
    private long lastUpdate;

    public AttributeManager() {
        this.attributeMap = new HashMap<>();
    }

    public void add(Attribute at) {
        if(attributeMap.containsKey(at.getId()))
            return;

        attributeMap.put(at.getId(), at);
    }

    public void remove(Attribute at) {
        attributeMap.remove(at.getId());
    }

    public Attribute find(int templateID) {
        return attributeMap.get(templateID);
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            for (Attribute at : attributeMap.values()) {
                try {
                    if (!at.isExpired()) {
                        at.update();
                    }
                } catch (Exception e) {
                    log.error("Update attribute lỗi to, huhu: {}", e.getMessage());
                }
            }
        }
    }

    public boolean setTime(int templateID, int time) {
        Attribute attr = find(templateID);
        if (attr != null) {
            attr.setTime(time);
            return true;
        }
        return false;
    }
}