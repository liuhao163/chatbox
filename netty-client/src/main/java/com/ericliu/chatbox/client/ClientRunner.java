package com.ericliu.chatbox.client;

import com.ericliu.chatbox.model.ProtocalHeader;
import com.ericliu.chatbox.model.Protocal;
import com.ericliu.chatbox.model.SerializableType;
import io.netty.channel.ChannelFuture;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class ClientRunner {

    public static void main(String[] args) {
        ChatBoxClient chatBoxClient = new ChatBoxClient();
        ChannelFuture channelFuture = chatBoxClient.start("127.0.0.1:1234");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String str = sc.next();
            Protocal protocal=new Protocal();

            byte b=0x00;
            ProtocalHeader header=new ProtocalHeader(b,SerializableType.JSON,str.length());
            protocal.setHeader(header);
            try {
                protocal.setData(str.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
            }

            channelFuture.channel().writeAndFlush(protocal);
        }
    }
}
