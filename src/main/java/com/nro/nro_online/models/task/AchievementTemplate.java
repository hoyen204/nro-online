package com.nro.nro_online.models.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @build by arriety
 */
@Setter
@Getter
@AllArgsConstructor
public class AchievementTemplate {

    private int id;

    private String name;

    private String detail;

    private int money;

    private int maxCount;
}
