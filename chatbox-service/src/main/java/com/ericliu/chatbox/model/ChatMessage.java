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

}


