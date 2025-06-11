package ru.nstu.logbook.Server;


import ru.nstu.logbook.Server.utils.*;
import ru.nstu.logbook.Shared.network.NetClient;
import ru.nstu.logbook.Shared.trades.Trade;
import ru.nstu.logbook.Shared.messages.*;
import ru.nstu.logbook.Shared.requests.*;
import ru.nstu.logbook.Shared.responses.*;
import ru.nstu.logbook.Shared.dto.*;

import java.sql.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private static final int DEFAULT_PORT = 8080;
    private int port;
    private static final int MAX_CAPACITY = 3;
    private int capacity;

    int localTradeId = 1;

    private final Thread waitThread;
    private enum State {
        STARTING,
        WAITING,
        FULL,
        STOPPING,
        STOPPED
    }

    State state;

    private final ServerSocket serverSocket;
    private final HashMap<Integer, NetClient> clients;
    private final ArrayList<NetClient> notRegistered;
    private final ArrayList<Trade> trades;

    public Server(int port, int capacity) throws IOException {
        this.port = port;
        this.capacity = capacity;

        serverSocket = new ServerSocket(port);
        clients = new HashMap<>();
        notRegistered = new ArrayList<>();
        trades = new ArrayList<>();

        waitThread = new Thread(this::waiting);
        waitThread.setDaemon(true);
        waitThread.start();
        state = State.STOPPED;
        while (waitThread.getState() != Thread.State.WAITING);
    }

    public Server(int port) throws IOException {
        this(port, MAX_CAPACITY);
    }

    public void start(){
        if(state != State.STOPPED)
            return;

        state = State.STARTING;
        beginWaiting();
    }

    public void waitingToStop() {
        if (state == State.STOPPED)
            return;
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public void stop() {//необходимо нежно прерывать все связи
        state = State.STOPPING;
        waitThread.interrupt();
        synchronized (clients) {
            for (var client : clients.values()) {
                disconnect(client);
            }
        }
        synchronized (notRegistered){
            notRegistered.clear();
        }
        synchronized (trades){
            trades.clear();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        synchronized (this) {
            this.notify();
        }
        state = State.STOPPED;

    }

    private void beginWaiting() {
        if (state == State.WAITING || state == State.STOPPED || state == State.STOPPING)
            return;
        synchronized (waitThread) {
            waitThread.notify();
        }
    }

    private void waiting() {
        Integer id = 0;
        while (true) {
            synchronized (waitThread) {
                try {
                    waitThread.wait();
                } catch (InterruptedException e) {
                    return;
                }
                System.out.println("1");
                state = State.WAITING;
                System.out.println("2");
                while (clients.size() <= MAX_CAPACITY) {
                    try {
                        System.out.println("wait");
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Connected, not reg");
                        synchronized (notRegistered) {
                            var client = new NetClient(clientSocket);
                            notRegistered.add(client);

                            client.receivedData.subscribe(this::process);
                            client.closeEvent.subscribe(this::disconnect);
                            //client.invalidData.subscribe();
                            client.startListen();
                            System.out.println("Connected, reg" + client.name);

                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                state = State.FULL;
            }
        }
    }

    private void process(Object sender, Object data){
        NetClient client = (NetClient) sender;

        if(data instanceof ConnectRequest request){
            connect(client, request);
            return;
        }

        if(notRegistered.contains(client)){
            return;
        }

        if(data instanceof DisconnectRequest request){
            disconnect(client);
            return;
        }
        if(data instanceof OfferTradeRequest request) {
            offerTrade(client, request);
            return;
        }
        if(data instanceof AcceptTradeRequest request) {
            acceptTrade(client, request);
            return;
        }
        if(data instanceof RejectTradeRequest request) {
            rejectTrade(client, request);
            return;
        }
        if(data instanceof CancelTradeRequest request) {
            cancelTrade(client, request);
            return;
        }

    }

    private void connect(Object sender, ConnectRequest request){
        var client = (NetClient) sender;
        if(!notRegistered.contains(client))
            return;

        synchronized (clients){
            client.id = generateId();
            clients.put(client.id, client);
        }
        synchronized (notRegistered){
            notRegistered.remove(client);
        }
        var descriptor = new ClientDescriptor(client.id, client.name);
        var response =
                new ConnectResponse(descriptor, clients.values().stream().filter(t -> t.id != client.id).toList());

        var message = new ClientConnectMessage(descriptor);
        client.send(response);
        broadcast(message);
        System.out.println("Connected client " + client.id + "(" + client.name + ")");
    }

    private void disconnect(Object sender) {
        var client = (NetClient) sender;

        for(Trade cancel: trades.stream().filter(trade -> trade.getSenderId() == client.id).toList()){
            cancelTrade(client, new CancelTradeRequest(cancel.getId()));
        }
        for(Trade reject: trades.stream().filter(trade -> trade.getTargetId() == client.id).toList()){
            rejectTrade(client, new RejectTradeRequest(reject.getId()));
        }

        synchronized (clients) {
            clients.remove(client.id);
        }
        var message = new ClientDisconnectedMessage(client.id);
        broadcast(message);
        beginWaiting();
        System.out.println("Disconnected client " + client.id + "(" + client.name + ")");
    }

    private void cancelTrade(NetClient client, CancelTradeRequest request) {
        var trade = getTradeById(request.id());

        if(trade.state != Trade.State.WAITING) {
            return;
        }
        synchronized (trade) {
            trade.cancel();
        }
        var cancelTrade = new CancellTradeMessage(trade.getId());
        var target = clients.get(trade.getSenderId());

        target.send(cancelTrade);
        System.out.println("Trade " + trade.getId() + " was cancelled");
    }

    private void rejectTrade(NetClient client, RejectTradeRequest request) {
        var trade = getTradeById(request.id());

        if(trade.state != Trade.State.WAITING) {
            return;
        }
        synchronized (trade) {
            trade.reject();
        }
        var rejectTrade = new RejectTradeMessage(trade.getId());
        var sender = clients.get(trade.getSenderId());


        sender.send(rejectTrade);
        System.out.println("Trade " + trade.getId() + " was rejected");
    }

    private void offerTrade(NetClient client, OfferTradeRequest request){
        synchronized (trades){
            var trade = new Trade(++localTradeId, client.id, request.targetId(), request.inners());
            trades.add(trade);
        }
        var target = clients.get(request.targetId());

        var response = new TradeOfferResponse(localTradeId, target.id, request.inners());

        var message = new OfferTradeMessage(localTradeId, client.id, request.inners());

        client.send(response);
        target.send(message);
        System.out.println("Trade " + localTradeId + " offered to client " + target + " from " + message.senderId()
                + ". Sent: " + message.inners().offer().size() + " " + message.inners().offer().type());
    }

    private void acceptTrade(NetClient client, AcceptTradeRequest request){
        var trade = getTradeById(request.id());
        if(trade != null)
            return;
        if(trade.state != Trade.State.WAITING) {
            return;
        }
        synchronized (trade) {
            trade.accept();
        }
        var message = new AcceptTradeMessage(trade.getId());
        var issuer = clients.get(trade.getSenderId());
        if(issuer == null) {
            //client.send(new ClientNotFoundError(trade.getIssuerId()));
            return;
        }
        issuer.send(message);
        System.out.println("Trade " + trade.getId() + " accepted "
                + ". Sent: " + trade.getInners() + " " + trade.getInners());
    }

    private Trade getTradeById(int tradeId) {
        for (Trade trade : trades) {
            if (trade.getId() == tradeId)
                return trade;
        }

        return null;
    }

    public void broadcast(Serializable message){
        synchronized (clients){
            for(var client : clients.values()){
                client.send(message);
            }
        }

    }

    private Integer generateId(){
        Random random = new Random();
        int id = random.nextInt(100, 10000);
        while (!clients.containsKey(id)){
            id = random.nextInt(100, 10000);
        }
        return id;
    }



    public static void main(String[] args) throws IOException {
        var port = DEFAULT_PORT;
        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if(!PortCheck.portValid(port))
                    port = DEFAULT_PORT;
            } catch (NumberFormatException ignored) {

            }
        }
        while (!PortCheck.portAvailable(port) && PortCheck.portValid(port)) {
            port++;
        }
        if(port == PortCheck.MAX_PORT){
            System.out.println("Available port not found");
            return;
        }
        var server = new Server(port);
        server.start();
        System.out.println("Ready on port " + port);
        server.waitingToStop();

    }
}