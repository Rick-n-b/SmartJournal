package ru.nstu.logbook.Client.net.utils;

import ru.nstu.logbook.Shared.trades.*;


public class ShortTrade {
    public final int id;
    private TradeInners inners;

    public ShortTrade(int id, TradeInners inners){
        this.id = id;
        this.inners = inners;
    }

    public TradeInners getInners() {
        return inners;
    }

}
