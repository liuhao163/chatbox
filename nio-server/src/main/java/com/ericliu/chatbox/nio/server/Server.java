package com.ericliu.chatbox.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: liuhaoeric
 * Create time: 2019/03/03
 * Description:
 */
public class Server {
    public static void main(String[] args) throws Exception {
        new Server().start(8000);
    }

    public void start(int port) throws Exception {
        Selector selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port), 1024);

        channel.register(selector, SelectionKey.OP_ACCEPT, "chatbox-Server-id-" + 1);
        System.out.println("The server is start in port: " + port);

        try {
            while (true) {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();

                    try {
                        processServer(selector, key);
                    } catch (IOException e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (selector != null) {
            selector.close();
        }
    }

    public void processServer(Selector selector, SelectionKey key) throws Exception {
        if (!key.isValid()) {
            System.out.println(key);
        }

        if (key.isAcceptable()) {
            //获取服务器通道
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            //执行阻塞方法(等待客户端资源)
            SocketChannel sc = ssc.accept();
            //设置为非阻塞模式
            sc.configureBlocking(false);
            //注册到多路复用器上，并设置为可读状态
            sc.register(selector, SelectionKey.OP_READ);
        }

        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            read(key);

            String currentTime = new Date(System.currentTimeMillis()).toString();
            write(selector, socketChannel, currentTime);
        }

        if (key.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            socketChannel.write((ByteBuffer) key.attachment());
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    private void write(Selector selector, SocketChannel channel, String msg) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes("utf-8"));
        channel.register(selector, SelectionKey.OP_WRITE, buffer);
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int readBytes = sc.read(readBuffer);
        if (readBytes < 0) {
            //对端链路关闭
            key.cancel();
            sc.close();
            return;
        }
        byte[] data = readBuffer.array();
        String msg = new String(data).trim();
        System.out.println("The time server receive order : " + msg);

    }

}
