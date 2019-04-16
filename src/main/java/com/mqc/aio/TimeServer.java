package com.mqc.aio;

import java.nio.channels.AsynchronousServerSocketChannel;

public class TimeServer {
    public static void main(String[] args) {
        new Thread(new AsyncTimeServerHandler(8080)).start();
    }
}
