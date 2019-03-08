package com.ericliu.chatbox.nio.client;

import com.ericliu.chatbox.nio.common.ChannelUtils;
import com.ericliu.chatbox.service.dto.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @Author: liuhaoeric
 * Create time: 2019/03/03
 * Description:
 */
public class Client {

    private int port;
    private String name;


    //boss
    private ExecutorService boosLoop = Executors.newSingleThreadExecutor();

    //
    private ExecutorService childLoop = Executors.newFixedThreadPool(10);


    private Selector selector;
    private SocketChannel channel;

    public Client(int port, String name) throws IOException {
        this.port = port;
        this.name = name;
        this.selector = Selector.open();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(1234, args[0]);
        client.connect();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String str = sc.next();
            client.write(str);
        }
    }


    public SocketChannel connect() throws Exception {
            try {
                channel = SocketChannel.open();
                channel.configureBlocking(false);
                channel.connect(new InetSocketAddress(port));
                channel.register(selector, SelectionKey.OP_CONNECT, name);
                while (true) {
                    try {
                        selector.select();
                        Iterator<SelectionKey> selectionKeySet = selector.selectedKeys().iterator();
                        while (selectionKeySet.hasNext()) {
                            SelectionKey key = selectionKeySet.next();
                            selectionKeySet.remove();
                            if (key.isConnectable()) {
                                channel = (SocketChannel) key.channel();
                                if (channel.isConnectionPending()) {
                                    channel.finishConnect();
                                }

                                // 设置成非阻塞
                                channel.configureBlocking(false);
//                                channel.register(selector, SelectionKey.OP_READ);
                                write("sender=" + key.attachment() + "||type=" + MessageType.register.ordinal());
                            }
                            if (key.isReadable()) {
                                read(key);
                            }

                            if (key.isWritable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                channel.write((ByteBuffer) key.attachment());
                                channel.register(selector, SelectionKey.OP_READ);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return channel;
    }


    private void write(String msg) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes("utf-8"));
        ChannelUtils.write(this.selector, this.channel, buffer);
    }

    public void read(SelectionKey key) throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        System.out.println(ChannelUtils.read(key, readBuffer));
    }


}
