package com.ericliu.chatbox.server;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class ServerStarter {

    public static void main(String[] args) throws InterruptedException {
        ChatBoxServer chatBoxServer = new ChatBoxServer(1234);
        try {
            chatBoxServer.listerner();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            chatBoxServer.close();
        }
    }
}
