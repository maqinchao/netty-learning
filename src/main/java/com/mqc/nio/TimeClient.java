package com.mqc.nio;

/**
 * @author mqc
 * @create 2019-04-14 22:50
 */
public class TimeClient {
    public static void main(String[] args) {
        TimeClientHandle timeClientHandle=new TimeClientHandle("127.0.0.1",8080);
        new Thread(timeClientHandle).start();
    }
}
