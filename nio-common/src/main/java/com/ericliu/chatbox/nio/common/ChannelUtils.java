package com.ericliu.chatbox.nio.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @Author: liuhaoeric
 * Create time: 2019/03/03
 * Description:
 */
public class ChannelUtils {

    public static void write(Selector selector, SocketChannel channel, ByteBuffer buffer) throws Exception {
        channel.register(selector, SelectionKey.OP_WRITE, buffer);
    }

    public static String read(SelectionKey key, ByteBuffer readBuffer) throws IOException {
        readBuffer.flip();
        SocketChannel sc = (SocketChannel) key.channel();
        int readBytes = sc.read(readBuffer);
        if (readBytes <= 0) {
            key.cancel();
            return "";
        }
        byte[] data = readBuffer.array();
        String msg = new String(data).trim();
        System.out.printf(msg);

        return msg;

    }


    public static String read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int readBytes = sc.read(readBuffer);
        if (readBytes < 0) {
            //对端链路关闭
            key.cancel();
            return "";
        }
        byte[] data = readBuffer.array();
        String msg = new String(data).trim();
//        System.out.println(msg);
        return msg;
    }

}
