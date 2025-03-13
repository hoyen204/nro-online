package com.nro.nro_online.dialog;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class MenuRunnable implements Runnable {

    private int indexSelected;
}
