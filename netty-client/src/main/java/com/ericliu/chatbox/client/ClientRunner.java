package com.ericliu.chatbox.client;

import com.ericliu.chatbox.model.*;
import io.netty.channel.ChannelFuture;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class ClientRunner {

    public static void main(String[] args) {
        ChatBoxClient chatBoxClient = new ChatBoxClient(new User(args[0]));
        ChannelFuture channelFuture = chatBoxClient.start("127.0.0.1:1234");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String str = sc.next();
            String[] content = str.split("@");
            if (content.length < 2) {
                continue;
            }

            String toName = content[0];
            User to = new User(toName);

            Protocal protocal = new Protocal();
            byte b = 0x00;
            ProtocalHeader header = new ProtocalHeader(b, SerializableType.JSON, str.length(), EventType.sendmsg);
            protocal.setHeader(header);

            Message message = new Message();
            message.setFrom(chatBoxClient.getUser());
            message.setTo(to);
            message.setMessage(content[1]);
            protocal.setData(protocal.dataToJsonBytes(message));

            channelFuture.channel().writeAndFlush(protocal);
        }
    }
}
