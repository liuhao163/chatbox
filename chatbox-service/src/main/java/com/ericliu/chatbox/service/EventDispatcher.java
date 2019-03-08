package com.ericliu.chatbox.service;

import com.ericliu.chatbox.service.dto.Message;
import com.ericliu.chatbox.service.dto.MessageType;
import com.ericliu.chatbox.service.dto.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/7
 */
public class EventDispatcher {

    private Map<String, SocketChannel> userConnMap = new HashMap<String, SocketChannel>();

    private EventDispatcher() {
    }

    private static EventDispatcher instence;

    public static EventDispatcher get() {
        if (instence == null) {
            synchronized (EventDispatcher.class) {
                if (instence == null) {
                    instence = new EventDispatcher();
                }
            }
        }
        return instence;
    }


    public Message processEvent(String message) {
        Message res = new Message();
        String[] entrys = message.split("\\|\\|");
        for (String entry : entrys) {
            String[] kv = entry.split("=");
            String k = kv[0];
            String v = kv[1];

            if (k.contains("type")) {
                MessageType type = MessageType.values()[Integer.valueOf(v)];
                res.setMessageType(type);
            }
            if (k.contains("sender")) {
                User sender = new User(0, v);
                res.setSender(sender);
            }
            if (k.contains("recv")) {
                User recv = new User(0, v);
                res.setReceiver(recv);
            }
            if (k.contains("body")) {
                res.setBody(v);
            }
            if (k.contains("time")) {
                res.setDate(new Date(Long.parseLong(v)));
            }
        }

        return res;
    }

    public void dispatch(Selector selector, SocketChannel sc, String msg) {
        Message message = processEvent(msg);

        if (message == null) {
            System.out.println("todo ");
        }

        switch (message.getMessageType()) {
            case sender:
                System.out.println("发送信息："+msg);
                processSend(selector, message);
                break;
            case receive:
                break;
            case register:
                System.out.println("注册信息："+msg);
                processRegister(message, sc);
                break;
            case unregister:
                System.out.println("离开信息："+msg);
                processUnRegister(message);
                break;
            default:
                break;
        }

    }


    public void processRegister(Message registerMessage, SocketChannel socketChannel) {
        userConnMap.put(registerMessage.getSender().getName(), socketChannel);
    }

    public void processUnRegister(Message registerMessage) {
        userConnMap.remove(registerMessage.getSender().getName());
    }

    public void processSend(Selector selector, Message registerMessage) {
        SocketChannel channel = userConnMap.get(registerMessage.getReceiver().getName());
        if (channel != null) {
            String s = "sender=" + registerMessage.getSender() + "||recv=" + registerMessage.getReceiver().getName() + "||body=" + registerMessage.getBody() + "||time=" + System.currentTimeMillis() + "||type=" + MessageType.sender.ordinal();

            try {
                channel.write(ByteBuffer.wrap(s.getBytes("utf-8")));
                channel.register(selector, SelectionKey.OP_WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
