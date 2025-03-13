package com.nro.nro_online.event;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

import com.nro.nro_online.models.item.Item;
import com.nro.nro_online.models.map.ItemMap;
import com.nro.nro_online.models.mob.Mob;
import com.nro.nro_online.models.player.Player;
import com.nro.nro_online.utils.Log;
import lombok.Getter;

public abstract class Event {
    @Getter
    private static volatile Event instance;

    public static void initEvent(String eventClassName) {
        if (isInvalidEventName(eventClassName)) {
            return;
        }

        try {
            instance = createEventInstance(eventClassName);
            Log.success("Khởi tạo event thành công: " + eventClassName);
        } catch (Exception e) {
            handleEventInitError(eventClassName, e);
        }
    }

    private static boolean isInvalidEventName(String eventClassName) {
        return eventClassName == null || eventClassName.trim().isEmpty() || "null".equalsIgnoreCase(eventClassName);
    }

    private static Event createEventInstance(String eventClassName)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> eventClass = Class.forName(eventClassName);
        return (Event) eventClass.getDeclaredConstructor().newInstance();
    }

    private static void handleEventInitError(String eventClassName, Exception e) {
        Log.error(instance.getClass(), e, "Lỗi khi khởi tạo event: " + eventClassName);
    }

    public static boolean isEvent() {
        return Objects.nonNull(instance);
    }

    public abstract void init();

    public abstract void initNpc();

    public abstract void initMap();

    public abstract void dropItem(Player player, Mob mob, List<ItemMap> drops, int x, int yEnd);

    public abstract boolean useItem(Player player, Item item);
}