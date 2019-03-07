package com.ericliu.chatbox.service;

import com.ericliu.chatbox.service.dto.Message;
import com.ericliu.chatbox.service.dto.MessageType;
import com.ericliu.chatbox.service.dto.User;

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
        Message res = null;
        String[] entrys = message.split("\\|\\|");
        for (String entry : entrys) {
            String[] kv = entry.split("=");
            String k = kv[0];
            String v = kv[1];

            res = new Message();

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

    public void processRegister(Message registerMessage, SocketChannel socketChannel) {
        userConnMap.put(registerMessage.getSender().getName(), socketChannel);
    }
}
