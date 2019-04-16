package com.mqc.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements  Runnable{

    private  int port;
    private CountDownLatch countDownLatch;
    private AsynchronousServerSocketChannel serverSocketChannel;

    public AsyncTimeServerHandler(int port){
        this.port=port;
        try {
            //构造通道 绑定端口
            serverSocketChannel=AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.printf("time server is start in port:"+port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void run() {
        countDownLatch=new CountDownLatch(1);
        doAccept();
        try {
            countDownLatch.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
        //传递CompletionHandler完成异步操作
        serverSocketChannel.accept(this,new AcceptCompletionHandler());
    }
}
