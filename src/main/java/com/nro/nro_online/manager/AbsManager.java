package com.nro.nro_online.manager;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public abstract class AbsManager<E> implements IManager<E> {

    protected List<E> list = new ArrayList<>();

    @Override
    public void add(E e) {
        list.add(e);
    }

    @Override
    public void remove(E e) {
        list.remove(e);
    }

}
