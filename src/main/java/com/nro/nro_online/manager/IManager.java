package com.nro.nro_online.manager;

public interface IManager<E> {

    void add(E e);

    void remove(E e);

    E findById(int id);
}
