package com.ericliu.chatbox.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class Protocal implements Serializable {
    private ProtocalHeader header;
    private byte[] data;

    public <T> T serializData(Class<T> classOfT) {
        try {
            String dataStr = new String(data, "utf-8");
            switch (header.getSerializalbeType()) {
                case JSON:
                    return JSON.parseObject(dataStr, classOfT);
                default:
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> byte[] dataToJsonBytes(T object) {
        return JSON.toJSONBytes(object);
    }

    public String serializeString() {
        try {
            return new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Protocal decode(ByteBuffer byteBuffer) {
        int headerData = byteBuffer.getInt();
        byte dataType = byteBuffer.get();
        int contentLength = byteBuffer.getInt();
        int eventType = byteBuffer.getInt();

        byte[] bodyData = null;
        if (contentLength > 0) {
            bodyData = new byte[contentLength];
            byteBuffer.get(bodyData);
        }

        Protocal ret = new Protocal();
        ProtocalHeader chatHeader = new ProtocalHeader(headerData, SerializableType.valueOfCode(dataType), contentLength, EventType.values()[eventType]);
        ret.setHeader(chatHeader);
        ret.setData(bodyData);

        return ret;
    }

    public ProtocalHeader getHeader() {
        return header;
    }

    public Protocal setHeader(ProtocalHeader header) {
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
