package com.ericliu.chatbox.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @Author: liuhaoeric
 * Create time: 2019/03/03
 * Description:
 */
public class Server {

    public static void main(String[] args) {

    }

    public void start(int port) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(port));

        Selector selector = Selector.open();

        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_ACCEPT, "Prism-Server");

        while (true) {
            int i = selector.select();
            while (i > 0) {
                Iterator<SelectionKey> selectionKeySet = selector.selectedKeys().iterator();

                while (selectionKeySet.hasNext()) {
                    SelectionKey key = selectionKeySet.next();
                    processServer(selector,key);
                    selectionKeySet.remove();
                }

            }
        }
    }

    public void processServer(Selector selector, SelectionKey selectionKey) throws ClosedChannelException {
        if (selectionKey.isAcceptable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.register(selector, SelectionKey.OP_READ);
        }

        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
//            ByteBuffer byteBuffer=ByteBuffer.wrap(socketChannel);
//
//            byteBuffer.
        }
    }
}
