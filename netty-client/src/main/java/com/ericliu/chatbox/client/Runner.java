package com.ericliu.chatbox.client;

import com.ericliu.chatbox.model.ChatMessage;
import io.netty.channel.ChannelFuture;

import java.util.Scanner;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class Runner {

    public static void main(String[] args) {
        ChatBoxClient chatBoxClient=new ChatBoxClient();
       ChannelFuture channelFuture= chatBoxClient.start("127.0.0.1:1234");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String str = sc.next();
//            String[] content = str.split("@");
//            if (content.length < 2) {
//                continue;
//            }

            channelFuture.channel().writeAndFlush(str);
        }
    }
}
