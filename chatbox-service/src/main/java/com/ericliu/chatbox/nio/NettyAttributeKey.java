package com.ericliu.chatbox.nio;

import com.ericliu.chatbox.model.User;
import io.netty.util.AttributeKey;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class NettyAttributeKey {
    public static final AttributeKey<User> ATTR_KEY_CHAT_USER = AttributeKey.valueOf("chatUer");
}
