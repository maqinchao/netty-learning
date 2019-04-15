package com.mqc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements  Runnable {

    private Selector selector;

    private ServerSocketChannel socketChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer (int port){
        try {
            //打开SocketChannel 监听指定端口 设置非阻塞模式
            socketChannel=ServerSocketChannel.open();
            socketChannel.socket().bind(new InetSocketAddress(port),1024);
            socketChannel.configureBlocking(false);

            //创建多路复用器 并将socketChannel绑定到selector上
            selector=Selector.open();
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the TimeServer is start on port:"+port);

        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop=true;
    }
    public void run() {
        while (!stop){
            try {
                //设置休眠时间为1s
                selector.select(1000);
                Set<SelectionKey> selectionKeys=selector.selectedKeys();
                Iterator<SelectionKey> iterator=selectionKeys.iterator();
                SelectionKey key=null;
                while (iterator.hasNext()){
                    key=iterator.next();
                    iterator.remove();
                    try{
                        handleInput(key);
                    }catch (Exception e){
                        if(key!=null){
                            key.cancel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }


                }
            }catch (Throwable t){
                t.printStackTrace();
            }
        }
        //设置stop为true后 可以关闭监听
        if (selector!=null){
            try{
                selector.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()){
            //如果收到的是accept类型，说明客户端发起了连接请求，需要将对应的channel注册到到selector上
            //完成这个阶段后 相当于完成tcp的三次握手
            if(key.isAcceptable()){
                ServerSocketChannel ssc= (ServerSocketChannel) key.channel();
                SocketChannel sc=ssc.accept();
                //设置channel为非阻塞模式

                sc.configureBlocking(false);
                sc.register(selector,SelectionKey.OP_READ);
            }
            //可读 用buffer缓存sc中的数据
            if(key.isReadable()){
                SocketChannel sc= (SocketChannel) key.channel();
                ByteBuffer readBuffer=ByteBuffer.allocate(1024);
                //因为是非阻塞模式 read会立即给出结果 >0：说明有数据可用 =0：正常场景 没有读取到数据 <0：说明链路已经被关闭
                int readBytes=sc.read(readBuffer);
                if (readBytes>0){
                    //切换读写模式
					
                    readBuffer.flip();
                    byte[] bytes=new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body=new String(bytes,"UTF-8");
                    System.out.println("Time Server received order :"+body);
                    String currentTime="Query Time Order".equalsIgnoreCase(body.trim())?new Date(System.currentTimeMillis()).toString():"Bad Order";
                    doWrite(sc,currentTime);
                }else if(readBytes<0){
                    key.cancel();
                    sc.close();
                }else {
                    //读到0字节
                }

            }
        }


    }

    public void doWrite(SocketChannel channel,String response) throws IOException {
        if(response!=null&&response.trim().length()>0){
            byte[] bytes=response.getBytes("UTF-8");
            ByteBuffer writeBuffer=ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            //实际上可能会出现写半包的现象
            channel.write(writeBuffer);

        }
    }
}
