package com.nro.nro_online.models.player;

public class Charms {
    public Charms(Player player) {
    }

    public long tdTriTue;
    public long tdManhMe;
    public long tdDaTrau;
    public long tdOaiHung;
    public long tdBatTu;
    public long tdDeoDai;
    public long tdThuHut;
    public long tdDeTu;
    public long tdDeTuMabu;
    public long tdTriTue3;
    public long tdTriTue4;

    public long lastTimeSubMinTriTueX4;

    public void addTimeCharms(int itemId, int min) {
        long currentTime = System.currentTimeMillis();
        long duration = min * 60 * 1000L;
        switch (itemId) {
            case 213:
                tdTriTue = tdTriTue < currentTime ? currentTime + duration : tdTriTue + duration;
                break;
            case 214:
                tdManhMe = tdManhMe < currentTime ? currentTime + duration : tdManhMe + duration;
                break;
            case 215:
                tdDaTrau = tdDaTrau < currentTime ? currentTime + duration : tdDaTrau + duration;
                break;
            case 216:
                tdOaiHung = tdOaiHung < currentTime ? currentTime + duration : tdOaiHung + duration;
                break;
            case 217:
                tdBatTu = tdBatTu < currentTime ? currentTime + duration : tdBatTu + duration;
                break;
            case 218:
                tdDeoDai = tdDeoDai < currentTime ? currentTime + duration : tdDeoDai + duration;
                break;
            case 219:
                tdThuHut = tdThuHut < currentTime ? currentTime + duration : tdThuHut + duration;
                break;
            case 522:
                tdDeTu = tdDeTu < currentTime ? currentTime + duration : tdDeTu + duration;
                break;
            case 671:
                tdTriTue3 = tdTriTue3 < currentTime ? currentTime + duration : tdTriTue3 + duration;
                break;
            case 672:
                tdTriTue4 = tdTriTue4 < currentTime ? currentTime + duration : tdTriTue4 + duration;
                break;
            case 2025:
                tdDeTuMabu = tdDeTuMabu < currentTime ? currentTime + duration : tdDeTuMabu + duration;
                break;
        }
    }

    public void dispose() {
    }
}