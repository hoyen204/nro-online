package com.nro.nro_online.models.task;

import java.util.ArrayList;
import java.util.List;

import com.nro.nro_online.models.player.Player;

public class TaskPlayer {

    public TaskMain taskMain;

    public SideTask sideTask;
    public List<Achievement> achievements;

    private Player player;

    public TaskPlayer(Player player) {
        this.player = player;
        this.sideTask = new SideTask();
        this.achievements = new ArrayList<>();
    }

    public void dispose() {
        this.taskMain = null;
        this.sideTask = null;
        this.player = null;
    }

}
