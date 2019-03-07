package com.ericliu.chatbox.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @Author: liuhaoeric
 * Create time: 2019/03/03
 * Description:
 */
public class Client {

    public static void main(String[] args) throws Exception {
        new Client().connect(8000);
    }

    public void connect(int serverPort) throws Exception {
        SocketChannel channel = SocketChannel.open();

        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(serverPort));
        Selector selector = Selector.open();
        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_CONNECT, "chatbox-client-id-" + 1);

        while (true) {
            selector.select();

            Iterator<SelectionKey> selectionKeySet = selector.selectedKeys().iterator();

            while (selectionKeySet.hasNext()) {
                SelectionKey key = selectionKeySet.next();
                selectionKeySet.remove();
                processServer(selector, key);
            }


        }
    }

    private void processServer(Selector selector, SelectionKey key) throws Exception {
        if (key.isConnectable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()) {
                channel.finishConnect();
            }

            String clientInfo= (String) key.attachment();
            // 设置成非阻塞
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);

            //在这里可以给服务端发送信息哦
//            channel.write(ByteBuffer.wrap(clientInfo.getBytes("utf-8")));

            write(selector,channel,clientInfo);
        }
        if (key.isReadable()) {
            read(key);
            write(selector,(SocketChannel) key.channel(),"from client:"+System.currentTimeMillis());
        }
        if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.write((ByteBuffer) key.attachment());
            channel.register(selector, SelectionKey.OP_READ);
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
        System.out.println(msg);

    }





}
