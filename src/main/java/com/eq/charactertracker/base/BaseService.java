package com.eq.charactertracker.base;

public interface BaseService <E, T>{
    T getById(long id);
    T create(T tObject);
    T update(T tObject, long id);
    boolean delete(long id);
}
