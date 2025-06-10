package ru.nstu.logbook.Client.net.utils;

import ru.nstu.logbook.Shared.trades.*;

public class TradeOut extends ShortTrade {
    public final int targetId;

    public TradeOut(int id, TradeInners inners, int targetId){
        super(id, inners);
        this.targetId = targetId;
    }
}
