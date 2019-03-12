package com.ericliu.chatbox.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ChatHeader implements Serializable {

    private int headData = 0X76;
    private byte dataType = 0;
    private int length;

    public static ChatHeader headerDecode(byte[] headerData, int oriHeaderLen) {
        System.out.println(oriHeaderLen);

        System.out.println("====" + (byte) ((oriHeaderLen >> 24) & 0xFF));
        return JSON.parseObject(headerData, ChatHeader.class);
    }

    public int getHeadData() {
        return headData;
    }

    public ChatHeader setHeadData(int headData) {
        this.headData = headData;
        return this;
    }

    public byte getDataType() {
        return dataType;
    }

    public ChatHeader setDataType(byte dataType) {
        this.dataType = dataType;
        return this;
    }

    public int getLength() {
        return length;
    }

    public ChatHeader setLength(int length) {
        this.length = length;
        return this;
    }
}
