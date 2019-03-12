package com.ericliu.chatbox.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ProtocalHeader implements Serializable {

    private int headData = 0X76;
    private SerializableType dataType;
    private int length;

    public ProtocalHeader(int headData, SerializableType dataType, int length) {
        this.headData = headData;
        this.dataType = dataType;
        this.length = length;
    }

    public int getHeadData() {
        return headData;
    }

    public SerializableType getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }
}
