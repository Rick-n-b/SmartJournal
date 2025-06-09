package ru.nstu.logbook.Shared.network;

import ru.nstu.logbook.Shared.events.*;

import java.io.*;
import java.net.Socket;

public class NetClient extends Thread implements Serializable{

    private transient final Socket socket;
    private transient ObjectInputStream inputStream;
    private final transient ObjectOutputStream outputStream;
    public String name;
    public Integer id;
    public final static Integer NOT_REGISTERED = -1;
    //public final String;

    public transient Event closeEvent = new Event();
    public transient Event invalidData = new Event();
    public transient ParamEvent<Serializable> receivedData = new ParamEvent<>();

    public NetClient(Socket socket, Integer id) throws IOException {
        this.socket = socket;
        this.setDaemon(true);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.id = id;
        name = "client" + id;
    }

    public NetClient(Socket socket) throws IOException {
        this(socket, NOT_REGISTERED);
    }

    public NetClient(Integer id, String name){
        this.name = name;
        this.id = id;
        socket = null;
        outputStream = null;
    }

    public void startListen(){
        this.start();
    }

    @Override
    public void run(){
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true){
            try {
                receivedData.invoke(this, (Serializable) inputStream.readObject());
            } catch (IOException e) {
                close();
            } catch (ClassNotFoundException e) {
                invalidData.invoke(this);
            }
        }
    }

    public void send(Serializable data){
        try {
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (IOException e) {
            close();
        }
    }

    public void close(){
        this.interrupt();
        receivedData.clearListeners();
        invalidData.clearListeners();
        try {
            if (socket.isClosed()) {
                inputStream.close();
                outputStream.close();
                socket.close();
            }
            closeEvent.invoke(this);
            closeEvent.clearListeners();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
