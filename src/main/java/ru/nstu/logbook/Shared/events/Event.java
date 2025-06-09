package ru.nstu.logbook.Shared.events;

import java.util.ArrayList;

public class Event {
    public final ArrayList<EventInt> listeners = new ArrayList<>();

    public void subscribe(EventInt listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void invoke(Object sender){
        synchronized (listeners) {
            for (var listener : listeners)
                new Thread(() -> listener.handle(sender)).start();
        }
    }

    public void clearListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }
}
