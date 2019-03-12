package com.ericliu.chatbox.service;

import com.ericliu.chatbox.model.User;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class Container {
    private Map<String, Channel> channelMap = new HashMap<>();

    private static Container container=new Container();

    public static Container getInstance() {
        return container;
    }

    private Container() {
    }



    public void setUser(User user, Channel channelFuture) {
        channelMap.put(user.getName(), channelFuture);
    }

    public Channel getUser(User user) {
        return channelMap.get(user.getName());
    }
}
