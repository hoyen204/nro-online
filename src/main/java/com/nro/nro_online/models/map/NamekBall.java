package com.nro.nro_online.models.map;

import lombok.Getter;
import lombok.Setter;
import com.nro.nro_online.services.ItemMapService;
import com.nro.nro_online.services.Service;

/**
 * @build by arriety
 */

@Getter
@Setter
public class NamekBall extends ItemMap {

    private boolean isHolding;
    private boolean isCleaning;
    private boolean isStone;
    private long cleaningTime;
    private int index;
    private String holderName;
    public NamekBall(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        super(zone, tempId, quantity, x, y, playerId);
        setHolderName("");
    }

    @Override
    public void update() {
        if (isCleaning && cleaningTime > 0) {
            cleaningTime--;
        }
    }

    public void setZone(Zone newZone) {
        this.zone.removeItemMap(this);
        this.zone = newZone;
        this.zone.addItem(this);
    }

    @Override
    public void reAppearItem() {
        if (isHolding) {
            ItemMapService.gI().sendItemMapDisappear(this);
        } else {
            Service.getInstance().dropItemMap(this.zone, this);
        }
    }

}
