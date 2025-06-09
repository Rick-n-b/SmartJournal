package ru.nstu.logbook.Shared.events;

import java.util.ArrayList;

public class ParamEvent<T> {
    public final ArrayList<ParamEventInt> listeners = new ArrayList<>();

    public void subscribe(ParamEventInt<T> listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void invoke(Object sender, T argument){
        synchronized (listeners) {
            for (var listener : listeners)
                new Thread(() -> listener.handle(sender, argument)).start();
        }
    }

    public void clearListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }
}
