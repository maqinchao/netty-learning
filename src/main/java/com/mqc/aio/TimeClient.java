package com.mqc.aio;

public class TimeClient {
    private int port;

    public TimeClient(int port){
        this.port=port;
        new Thread(new AsyncTimeClientHandler("127.0.0.1",port)).start();
    }
    public static void main(String[] args) {
        TimeClient timeClient=new TimeClient(8080);
    }
}
