package com.mqc.nio;

public class TimeServer {
    public static void main(String[] args) {
        int port=8080;
        MultiplexerTimeServer server=new MultiplexerTimeServer(8080);
        new Thread(server).start();

    }
}
