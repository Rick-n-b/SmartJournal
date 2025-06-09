package ru.nstu.logbook.Shared.trades;

import java.io.Serializable;

public class Trade implements Serializable {

    public enum State{
        WAITING,
        ACCEPTED,
        REJECTED,
        CANCELED
    }
    private final int id;
    public State state;

    private final int senderId;
    private final int targetId;

    private TradeInners inners;

    public Trade(int id, int senderId, int targetId, TradeInners inners){
        this.id = id;
        this.senderId = senderId;
        this.targetId = targetId;
        this.inners = inners;

        state = State.WAITING;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getId() {
        return id;
    }

    public int getTargetId() {
        return targetId;
    }

    public TradeInners getInners(){
        return inners;
    }


    public void accept() {
        if(state == State.WAITING)
            state = State.ACCEPTED;
    }
    public void reject() {
        if(state == State.WAITING)
            state = State.REJECTED;
    }
    public void cancel() {
        if(state == State.WAITING)
            state = State.CANCELED;
    }
}
