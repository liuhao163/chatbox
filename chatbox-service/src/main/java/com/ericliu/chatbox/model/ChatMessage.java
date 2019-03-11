package com.ericliu.chatbox.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ChatMessage implements Serializable {
    private ChatHeader header;
    private String body;

    public static ChatMessage decode(ByteBuffer byteBuffer) {
        int length = byteBuffer.limit();

        int oriHeaderLen = byteBuffer.getInt();
        int headerLength = getHeaderLength(oriHeaderLen);

        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        ChatHeader header = ChatHeader.headerDecode(headerData, oriHeaderLen);

        //因为length-包长度-头
        int bodyLength = length - 4 - headerLength;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }

        ChatMessage message=new ChatMessage();
        message.setHeader(header);
        try {
            message.setBody(new String(bodyData,"utf-8"));
        } catch (UnsupportedEncodingException e) {
        }

        return message;
    }

    public static int getHeaderLength(int length) {
        return length & 0xFFFFFF;
    }

    public ChatHeader getHeader() {
        return header;
    }

    public ChatMessage setHeader(ChatHeader header) {
        this.header = header;
        return this;
    }

    public String getBody() {
        return body;
    }

    public ChatMessage setBody(String body) {
        this.body = body;
        return this;
    }

    public static void main(String[] args) {
//        int length = 1000;
//        System.out.println(0xFFFFFF);
//        System.out.println(length & 0xFFFFFF);

//        int source = 255;
//        System.out.println(0xFF);
//        System.out.println(source >> 24);
//        System.out.println((byte) ((source >> 24) & 0xFF));

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("dsfdsfs".getBytes());
        int headLen=byteBuffer.getInt();

        byteBuffer.flip();
        System.out.println(headLen);
        System.out.println(byteBuffer.limit());
        System.out.println((byte) ((headLen >> 24) & 0xFF));

        byte[] result = new byte[4];

        result[0] = 0;
        result[1] = (byte) ((5 >> 16) & 0xFF);
        result[2] = (byte) ((5 >> 8) & 0xFF);
        result[3] = (byte) (5 & 0xFF);

        System.out.println(new String(result));

    }


}


