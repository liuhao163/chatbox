package com.ericliu.chatbox.model;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class Protocal {
    private ChatHeader header;
    private byte[] data;

    public static Protocal decode(ByteBuf byteBuffer) {
//        int length = byteBuffer.limit();
        int headerData = byteBuffer.readInt();
        byte dataType = byteBuffer.readByte();
        int contentLength = byteBuffer.readInt();
        byte[] bodyData = new byte[contentLength];
        byteBuffer.readBytes(bodyData);
//        if (bodyLength > 0) {
//            bodyData = new byte[bodyLength];
//            byteBuffer.get(bodyData);
//        }

        Protocal ret = new Protocal();
        ChatHeader chatHeader = new ChatHeader();
        chatHeader.setHeadData(headerData);
        chatHeader.setDataType(dataType);
        chatHeader.setLength(contentLength);
        ret.setHeader(chatHeader);
        ret.setData(bodyData);

        try {
            System.out.println(new String(bodyData, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static Protocal decode(ByteBuffer byteBuffer) {
        int headerData = byteBuffer.getInt();
        byte dataType = byteBuffer.get();
        int contentLength = byteBuffer.getInt();
        byte[] bodyData = null;
        if (contentLength > 0) {
            bodyData = new byte[contentLength];
            byteBuffer.get(bodyData);
        }

        Protocal ret = new Protocal();
        ChatHeader chatHeader = new ChatHeader();
        chatHeader.setHeadData(headerData);
        chatHeader.setDataType(dataType);
        chatHeader.setLength(contentLength);
        ret.setHeader(chatHeader);
        ret.setData(bodyData);

        try {
            System.out.println(new String(bodyData, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public ChatHeader getHeader() {
        return header;
    }

    public Protocal setHeader(ChatHeader header) {
        this.header = header;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public Protocal setData(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Protocal{" +
                "header=" + header +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
