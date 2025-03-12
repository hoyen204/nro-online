package com.nro.nro_online.attr;

import com.nro.nro_online.utils.TimeUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Attribute {
    private final int id;
    private int value;
    private int time;
    private AttributeTemplate template;
    private boolean changed;

    @Builder
    public Attribute(int id, int templateID, int value, int time) {
        this.id = id;
        this.value = value;
        this.time = time;
        this.template = AttributeTemplateManager.gI().find(templateID);
    }

    public void update() {
        if (time > 0) {
            time--;
            setChanged();
        }
    }

    public void setChanged() {
        changed = true;
    }

    public boolean isExpired() {
        return time == 0;
    }

    @Override
    public String toString() {
        String text = template.getName().replace("#value", String.valueOf(this.value));
        if (time != -1) {
            String strTimeAgo = TimeUtil.getTimeAgo(time);
            text += " trong vòng " + strTimeAgo;
        }
        return text;

    }

    public void setValue(int value) {
        this.value = value;
        setChanged();
    }

    public void setTime(int time) {
        this.time = time;
        setChanged();
    }

    public void setTemplate(AttributeTemplate template) {
        this.template = template;
        setChanged();
    }
}
