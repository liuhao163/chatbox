package com.ericliu.chatbox.service;

import com.ericliu.chatbox.model.*;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/7
 */
public class ChatEventDispatcher {

    private static Logger logger = LoggerFactory.getLogger(ChatEventDispatcher.class);


    private ChatEventDispatcher() {
    }

    private static ChatEventDispatcher instence;

    public static ChatEventDispatcher get() {
        if (instence == null) {
            synchronized (ChatEventDispatcher.class) {
                if (instence == null) {
                    instence = new ChatEventDispatcher();
                }
            }
        }
        return instence;
    }

    public void dispatch(Protocal protocal, Channel channel) {
        switch (protocal.getHeader().getEventType()) {
            case register:
                User user = protocal.serializData(User.class);
                logger.info("注册用户：{}", user);
                Container.getInstance().setUser(user, channel);
                break;
            case leave:
                break;
            case sendmsg:
                Message message = protocal.serializData(Message.class);
                Channel toChan = Container.getInstance().getUser(message.getTo());

                Protocal sendProt = new Protocal();
                sendProt.setHeader(new ProtocalHeader(protocal.getData().length, EventType.receiveUser)).setData(protocal.getData());
                toChan.writeAndFlush(sendProt);
                logger.info("server tranrs from:{}-->to:{}--->message:{}", message.getFrom(), message.getTo(), message.getMessage());
                break;
            case receiveUser:
                Message receiveMsg = protocal.serializData(Message.class);
                logger.info("server tranrs from:{}-->to:{}--->message:{}", receiveMsg.getFrom(), receiveMsg.getTo(), receiveMsg.getMessage());
                break;
            default:
                break;
        }

    }


}
