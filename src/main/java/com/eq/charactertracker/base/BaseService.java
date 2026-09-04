package com.eq.charactertracker.base;

import java.util.List;

public interface BaseService <E, T>{
    T getById(long id);
    T create(T tObject);
    T update(T tObject, long id);
    boolean delete(long id);
    List<T> findAll();
    List<T> saveAll(List<T> tList);
}
