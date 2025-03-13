package com.nro.nro_online.models.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @build by arriety
 */
@Setter
@Getter
public class Hit {
    private List<Integer> hits;
    private Map<Integer, Integer> targets;

    public Hit() {
        hits = new ArrayList<>();
        targets = new HashMap<>();
    }

    public void addTarget(int targetID, int type) {
        targets.put(targetID, type);
    }

    public void addHit(int damage) {
        hits.add(damage);
    }
}
