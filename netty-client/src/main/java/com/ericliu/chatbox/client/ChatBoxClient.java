package com.ericliu.chatbox.client;

import com.ericliu.chatbox.common.RemotingHelper;
import com.ericliu.chatbox.model.EventType;
import com.ericliu.chatbox.model.Protocal;
import com.ericliu.chatbox.model.ProtocalHeader;
import com.ericliu.chatbox.model.User;
import com.ericliu.chatbox.nio.ChatProtocalDecoder;
import com.ericliu.chatbox.nio.ChatProtocalEncoder;
import com.ericliu.chatbox.service.ChatEventDispatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/12
 */
public class ChatBoxClient {

    private Logger logger = LoggerFactory.getLogger(ChatBoxClient.class);

    private Bootstrap bootstrap = new Bootstrap();

    private EventLoopGroup eventLoopGroupWorker = new NioEventLoopGroup(1, new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, String.format("NettyClientSelector_%d", this.threadIndex.incrementAndGet()));
        }
    });

    private User user;


    public ChatBoxClient(User user) {
        this.user = user;
        init();
    }

    public User getUser() {
        return user;
    }

    private DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(
            20,
            new ThreadFactory() {

                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "NettyClientWorkerThread_" + this.threadIndex.incrementAndGet());
                }
            });

    private void init() {

        bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(
                                defaultEventExecutorGroup,
                                new ChatProtocalEncoder(),
                                new ChatProtocalDecoder(),
                                new IdleStateHandler(0, 0, 100),
                                new NettyConnectManageHandler(),
                                new NettyClientHandler());
                    }
                });
    }

    public ChannelFuture start(String addr) {
        ChannelFuture channelFuture = this.bootstrap.connect(RemotingHelper.string2SocketAddress(addr));

        return channelFuture;
    }


    class NettyClientHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            Protocal protocal = (Protocal) msg;
            ChatEventDispatcher.get().dispatch(protocal, ctx.channel());
        }
    }

    class NettyConnectManageHandler extends ChannelDuplexHandler {
        private AtomicInteger lossConnectCount = new AtomicInteger(0);

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
                            ChannelPromise promise) throws Exception {
            logger.info("====>user {} connect now connect.", user);

            Protocal protocal = new Protocal();

            byte[] data = protocal.dataToJsonBytes(user);
            protocal.setHeader(new ProtocalHeader(data.length, EventType.register));
            protocal.setData(data);

            super.connect(ctx, remoteAddress, localAddress, promise);
            ctx.channel().writeAndFlush(protocal);
            logger.info("====>user {} connect now connect.finish", user);
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            System.out.println("====>disconnect");
            super.disconnect(ctx, promise);
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            System.out.println("====>close");
            super.close(ctx, promise);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("已经5秒未收到服务端的消息了！");
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.READER_IDLE) {
                    int val = lossConnectCount.addAndGet(1);
                    if (val > 2) {
                        System.out.println("关闭这个不活跃通道！");
                        ctx.channel().close();
                    }
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }
    }

}