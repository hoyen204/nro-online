/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nro.nro_online.models.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Administrator
 */
public class KeoBuaBaoManager implements Runnable {

    private static KeoBuaBaoManager instance;

    private final List<KeoBuaBao> listKeoBuaBao = new ArrayList<>();

    public static KeoBuaBaoManager gI() {
        if (instance == null) {
            instance = new KeoBuaBaoManager();
        }
        return instance;
    }

    public void add(KeoBuaBao a) {
        synchronized (listKeoBuaBao) {
            listKeoBuaBao.add(a);
        }
    }

    public void remove(KeoBuaBao a) {
        synchronized (listKeoBuaBao) {
            listKeoBuaBao.remove(a);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (int i = 0; i < listKeoBuaBao.size(); i++) {
                    KeoBuaBao k = listKeoBuaBao.get(i);
                    if (k != null) {
                        k.update();
                    }
                }
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
        }
    }

}
