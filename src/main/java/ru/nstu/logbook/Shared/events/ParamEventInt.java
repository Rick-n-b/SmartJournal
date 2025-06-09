package ru.nstu.logbook.Shared.events;

public interface ParamEventInt<T> {
    void handle(Object sender, T argument);
}
