package com.nro.nro_online.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.nro.nro_online.models.DragonNamecWar.TranhNgoc;
import com.nro.nro_online.models.player.Player;
import nro.models.phuban.DragonNamecWar.TranhNgoc;
import nro.models.player.Player;

/**
 *
 * @Build by Arriety
 */
public class TranhNgocManager extends ReentrantReadWriteLock implements Runnable {

    private final ExecutorService threadPool;
    private final List<TranhNgoc> list;
    private boolean running;
    private int increasement;

    public TranhNgocManager() {
        this.threadPool = Executors.newFixedThreadPool(25);
        this.list = new ArrayList<TranhNgoc>();
        this.start();
    }

    public int generateID() {
        return this.increasement++;
    }

    public void start() {
        this.running = true;
    }

    @Override
    public void run() {
        while (this.running) {
            final long now = System.currentTimeMillis();
            this.update();
            final long now2 = System.currentTimeMillis();
            if (now2 - now < 1000L) {
                try {
                    Thread.sleep(1000L - (now2 - now));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public TranhNgoc findByPLayerId(final long playerId) {
        this.readLock().lock();
        try {
            for (final TranhNgoc tn : this.list) {
                for (final Player player : tn.getPlayersCadic()) {
                    if (player.id == playerId) {
                        return tn;
                    }
                }
                for (final Player player : tn.getPlayersFide()) {
                    if (player.id == playerId) {
                        return tn;
                    }
                }
            }
        } finally {
            this.readLock().unlock();
        }
        return null;
    }

    public TranhNgoc getAvableTranhNgoc() {
        this.readLock().lock();
        try {
            for (final TranhNgoc tn : this.list) {
                if (tn.numOfPlayers < 10) {
                    return tn;
                }
            }
        } finally {
            this.readLock().unlock();
        }
        return null;
    }

    public boolean register(final Player player, boolean is_cadic) {
        boolean registerStatus = false;

        this.readLock().lock();
        try {
            for (final TranhNgoc tn : this.list) {
                if (is_cadic) {
                    registerStatus = tn.addPlayersCadic(player);
                    if (registerStatus) {
                        break;
                    }
                } else {
                    registerStatus = tn.addPlayersFide(player);
                    if (registerStatus) {
                        break;
                    }
                }
            }
        } finally {
            this.readLock().unlock();
        }
        return registerStatus;
    }

    public void update() {
        this.readLock().lock();
        try {
            final List<TranhNgoc> remove = new ArrayList<TranhNgoc>();
            for (final TranhNgoc tn : this.list) {
                try {
                    tn.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tn.isClosed()) {
                    remove.add(tn);
                }
            }
            this.list.removeAll(remove);
        } finally {
            this.readLock().unlock();
        }
    }

    public void add(final TranhNgoc tn) {
        this.writeLock().lock();
        try {
            tn.setId(this.generateID());
            this.list.add(tn);
        } finally {
            this.writeLock().unlock();
        }
    }

    public ExecutorService getThreadPool() {
        return this.threadPool;
    }

    public int numOfTranhNgoc() {
        return this.list.size();
    }
}
