package com.ericliu.chatbox.model;

import java.io.Serializable;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ProtocalHeader implements Serializable {

    private int headData = 0X76;
    private SerializableType serializalbeType = SerializableType.JSON;
    private int length;
    private EventType eventType;

    public ProtocalHeader(int length, EventType eventType) {
        this.length = length;
        this.eventType = eventType;
    }

    public ProtocalHeader(int headData, SerializableType serializalbeType, int length, EventType eventType) {
        this.headData = headData;
        this.serializalbeType = serializalbeType;
        this.length = length;
        this.eventType = eventType;
    }

    public int getHeadData() {
        return headData;
    }

    public SerializableType getSerializalbeType() {
        return serializalbeType;
    }

    public int getLength() {
        return length;
    }

    public EventType getEventType() {
        return eventType;
    }
}
