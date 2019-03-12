package com.ericliu.chatbox.model;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public enum SerializableType {
    JSON((byte) 0);

    private byte code;

    SerializableType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static SerializableType valueOfCode(byte code) {
        for (SerializableType type : SerializableType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
