package com.ericliu.chatbox.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ChatHeader implements Serializable {

    private byte type;


    public static ChatHeader headerDecode(byte[] headerData, int oriHeaderLen) {
        System.out.println(oriHeaderLen);

        System.out.println("====" + (byte) ((oriHeaderLen >> 24) & 0xFF));
        return JSON.parseObject(headerData, ChatHeader.class);
    }
}
