package ru.nstu.logbook.Shared.messages;

import ru.nstu.logbook.Shared.trades.*;

import java.io.Serializable;

public record OfferTradeMessage(int id, int senderId, TradeInners inners) implements Serializable {
}
