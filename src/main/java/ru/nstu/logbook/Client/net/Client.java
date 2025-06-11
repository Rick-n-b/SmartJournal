package ru.nstu.logbook.Client.net;


import ru.nstu.logbook.Client.net.utils.ShortTrade;
import ru.nstu.logbook.Client.net.utils.TradeIn;
import ru.nstu.logbook.Client.net.utils.TradeOut;
import ru.nstu.logbook.Shared.trades.*;
import ru.nstu.logbook.Shared.messages.*;
import ru.nstu.logbook.Shared.requests.*;
import ru.nstu.logbook.Shared.responses.*;
import ru.nstu.logbook.Shared.network.*;
import ru.nstu.logbook.Shared.events.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    public enum Status {
        READY,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        CLOSING,
        CLOSED
    }

    private NetClient networkClient;
    private int id = -1;
    private String name = "Unreg";
    private final Map<Integer, NetClient> neighbours = new HashMap<>();
    private final Map<Integer, ShortTrade> trades = new HashMap<>();

    public final Event connectionClosed = new Event();
    public final Event connected = new Event();
    public final Event tradesUpdated = new Event();
    public final ParamEvent<ShortTrade> tradeAccepted = new ParamEvent<>();

    private Status status = Status.READY;
    private final static String[] SERVER_IPS = {"127.0.0.1"};
    private final static int DEFAULT_PORT = 8079;

    public final Thread acceptingThread = new Thread(this::alwaysAccepting);

    public void connect(String name) throws UnknownHostException {
        if(status != Status.READY)
            return;

        status = Status.CONNECTING;

        for(var strAddress : SERVER_IPS){
            InetAddress address = InetAddress.getByName(strAddress);
            for(int i = DEFAULT_PORT; i < 8100; i++){
                System.out.println("Trying to connect to " + strAddress + ", port:" + i);
                var socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress(address, i));
                    if(socket.isConnected()){
                        try {
                            networkClient = new NetClient(socket);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        networkClient.receivedData.subscribe(this::processMessage);
                        networkClient.closeEvent.subscribe(this::onConnectionClosed);
                        networkClient.startListen();
                        networkClient.send(new ConnectRequest(name));
                        if(acceptingThread.getState() == Thread.State.WAITING)
                            synchronized (acceptingThread){
                                acceptingThread.notify();
                            }
                        else
                            acceptingThread.start();
                        acceptingThread.setDaemon(true);
                        System.out.println("Connected to " + strAddress + " " + i);
                        return;
                    }
                } catch (IOException e) {
                    ;//ignore
                }

            }
        }
        System.err.println("Couldn't get access to a server");
        status = Status.READY;
    }

    public void disconnect() {
        if (status != Status.CONNECTED)
            return;
        status = Status.DISCONNECTING;
        neighbours.clear();
        trades.clear();
        tradesUpdated.invoke(this);
        networkClient.send(new DisconnectRequest(id));
        name = "";
        id = -1;
        networkClient.close();
        networkClient = null;
        synchronized (acceptingThread) {
            try {
                acceptingThread.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        status = Status.READY;
    }

    public Status getStatus() {
        return status;
    }

    public List<ShortTrade> getTrades(){
        return trades.values().stream().toList();
    }

    public List<NetClient> getNeighbours() {
        return neighbours.values().stream().toList();
    }

    public NetClient findNeighbour(int id) {
        return neighbours.getOrDefault(id, null);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isRegistered() {
        return id != -1;
    }

    private void onConnectionClosed(Object sender) {
        connectionClosed.invoke(this);
    }

    private void processMessage(Object sender, Serializable data) {
        if(data instanceof ConnectResponse response) {
            onConnected(response);
            return;
        }
        if(data instanceof ClientConnectMessage message) {
            onClientConnected(message);
            return;
        }
        if(data instanceof ClientDisconnectedMessage message) {
            onClientDisconnected(message);
            return;
        }
        if(data instanceof TradeOfferResponse response) {
            onTradeSent(response);
            return;
        }
        if(data instanceof OfferTradeMessage message) {
            onTradeOffered(message);
            return;
        }
        if(data instanceof AcceptTradeMessage message) {
            onTradeAccepted(message);
            return;
        }
        if(data instanceof RejectTradeMessage message){
            onTradeRejected(message);
            return;
        }
        if(data instanceof CancellTradeMessage message) {
            onTradeCanceled(message);
            return;
        }
    }

    public void offerTrade(int target, TradeInners inners) {
        if(status != Status.CONNECTED)
            return;
        var request = new OfferTradeRequest(target, inners);
        networkClient.send(request);
    }

    public void acceptTrade(TradeIn trade) {
        if(status != Status.CONNECTED)
            return;
        var request = new AcceptTradeRequest(trade.id);
        networkClient.send(request);
        synchronized (trades) {
            trades.remove(trade.id);
            tradesUpdated.invoke(this);
        }
    }

    public void rejectTrade(ShortTrade trade) {
        if(status != Status.CONNECTED)
            return;
        var request = new RejectTradeRequest(trade.id);
        networkClient.send(request);
        synchronized (trades) {
            trades.remove(trade.id);
            tradesUpdated.invoke(this);
        }
    }

    public void cancelTrade(TradeOut trade) {
        if(status != Status.CONNECTED)
            return;
        var request = new CancelTradeRequest(trade.id);
        networkClient.send(request);
        synchronized (trades) {
            trades.remove(trade.id);
            tradesUpdated.invoke(this);
        }
    }

    public void close() {
        if(status == Status.CONNECTED)
            disconnect();
        status = Status.CLOSING;
        connected.clearListeners();
        connectionClosed.clearListeners();
        tradesUpdated.clearListeners();
        tradeAccepted.clearListeners();
        status = Status.CLOSED;
    }

    private void onConnected(ConnectResponse response) {
        if(isRegistered())
            return;
        id = response.descriptor().id();
        synchronized (neighbours) {
            for (var client : response.clients())
                neighbours.put(id, client);
        }
        status = Status.CONNECTED;
        connected.invoke(this);
    }

    private void onClientConnected(ClientConnectMessage message) {
        if(message.descriptor().id() == id && message.descriptor().name().equals(name))
            return;
        var client = new NetClient(message.descriptor().id(), message.descriptor().name());
        synchronized (neighbours) {
            neighbours.put(id, client);
        }
    }

    private void onClientDisconnected(ClientDisconnectedMessage message) {
        synchronized (neighbours) {
            neighbours.remove(message.clientId());
        }
    }

    private void onTradeSent(TradeOfferResponse response) {
        var trade = new TradeOut(response.id(), response.inners(),  response.targetId());
        synchronized (trades) {
            trades.put(response.id(), trade);
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeOffered(OfferTradeMessage message) {
        var trade = new TradeIn(message.id(), message.inners(), message.senderId());
        synchronized (trades) {
            trades.put(message.id(), trade);
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeAccepted(AcceptTradeMessage message) {
        var trade = trades.get(message.id());
        tradeAccepted.invoke(this, trade);
        synchronized (trades) {
            trades.remove(message.id());
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeRejected(RejectTradeMessage message) {
        synchronized (trades) {
            trades.remove(message.id());
        }
        tradesUpdated.invoke(this);
    }

    private void onTradeCanceled(CancellTradeMessage message) {
        synchronized (trades) {
            trades.remove(message.id());
        }
        tradesUpdated.invoke(this);
    }

    public void alwaysAccepting(){
        while(status != Status.CLOSED){
            synchronized (trades){
                for(var trade  : trades.values()){
                    if(trade instanceof TradeIn)
                        acceptTrade((TradeIn)trade);
                }
            }
        }
    }
}
