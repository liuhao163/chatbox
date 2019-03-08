package com.ericliu.chatbox.nio.client;

import com.ericliu.chatbox.nio.common.ChannelUtils;
import com.ericliu.chatbox.service.EventDispatcher;
import com.ericliu.chatbox.service.dto.Message;
import com.ericliu.chatbox.service.dto.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
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

        Thread.sleep(1000L);

        System.out.printf("now---run");
        while (true) {
            Scanner sc = new Scanner(System.in);
            if (sc.hasNext()) {
                String str = sc.next();
                String[] content = str.split("@");
                if (content.length < 2) {
                    continue;
                }
                String s = "sender=" + client.getName() + "||type=" + MessageType.sender.ordinal() + "||recv=" + content[0] + "||body=" + content[1];
                client.write(s);
            }
        }
    }

    public String getName() {
        return name;
    }

    public SocketChannel connect() throws Exception {
        boosLoop.execute(() -> {
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
                                write("sender=" + key.attachment() + "||type=" + MessageType.register.ordinal());
                            }
                            if (key.isReadable()) {
                                read(key);
                            }

                            if (key.isWritable()) {
//                                SocketChannel channel = (SocketChannel) key.channel();
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
        });

        return channel;
    }


    private void write(String msg) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes("utf-8"));
        ChannelUtils.write(this.selector, this.channel, buffer);
        channel.write(buffer);
    }

    public void read(SelectionKey key) throws IOException {
        String msg = ChannelUtils.read(key);
        Message message = EventDispatcher.get().processEvent(msg);

        String yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message.getDate());

        System.out.println("\n来自：" + message.getSender().getName() + "的消息：\t" + message.getBody() + " 时间是\t：" + yyyyMMdd);
    }


//    public String read(SelectionKey key) throws IOException {
//        SocketChannel sc = (SocketChannel) key.channel();
//        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
//        int readBytes = sc.read(readBuffer);
//        if (readBytes < 0) {
//            //对端链路关闭
//            try {
//                key.cancel();
//                sc.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return "";
//        }
//        byte[] data = readBuffer.array();
//        String msg = new String(data).trim();
//        System.out.println(msg);
//        return msg;
//    }

}
