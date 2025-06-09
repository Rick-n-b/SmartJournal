package ru.nstu.logbook.Client.net.Trades;

import ru.nstu.logbook.Shared.trades.*;

import java.io.Serializable;

public class TradeIn extends ShortTrade implements Serializable {
    public final int senderId;

    public TradeIn(int id, TradeInners inners, int senderId){
        super(id, inners);
        this.senderId = senderId;
    }
}
