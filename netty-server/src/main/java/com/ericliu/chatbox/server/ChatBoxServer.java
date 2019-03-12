package com.ericliu.chatbox.server;

import com.ericliu.chatbox.common.RemotingUtil;
import com.ericliu.chatbox.model.Protocal;
import com.ericliu.chatbox.nio.ChatProtocalDecoder;
import com.ericliu.chatbox.nio.ChatProtocalEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ChatBoxServer {

    private static boolean useEpollNativeSelector = false;

    private ChannelFuture f;

    private ServerBootstrap serverBootstrap;

    private int port;

    private NioEventLoopGroup defaultEventExecutorGroup = new NioEventLoopGroup(100);
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup(100);

    public ChatBoxServer(int port) {
        init(port);
    }

    private void init(int port) {
        this.port = port;
        serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))//port

                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true) //SO_REUSEADDR 允许重复使用本地地址和端口
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, 65535)
                .childOption(ChannelOption.SO_RCVBUF, 65535)

                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(defaultEventExecutorGroup,
                                        new ChatProtocalEncoder(),
                                        new ChatProtocalDecoder(),
                                        new IdleStateHandler(0, 0, 100),
                                        new NettyConnectManageHandler(),
                                        new NettyServerHandler()
                                );
                    }
                });
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

    }

    public void listerner() throws InterruptedException {
        f = serverBootstrap.bind().sync();
    }

    public void close() throws InterruptedException {
        try {
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            defaultEventExecutorGroup.shutdownGracefully();
        }
    }

    static class NettyServerHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            Protocal protocal = (Protocal) msg;
            System.out.println(protocal.serializeString());
        }
    }

    static class NettyConnectManageHandler extends ChannelDuplexHandler {

        private AtomicInteger lossConnectCount = new AtomicInteger(0);

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("====>channelRegistered");
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("====>channelUnregistered");
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("====>channelActive");
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("====>channelInactive");
            super.channelInactive(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("已经5秒未收到客户端的消息了！");
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


            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            System.out.println("====>connect");
            super.connect(ctx, remoteAddress, localAddress, promise);
        }
    }

    private static boolean useEpoll() {
        return RemotingUtil.isLinuxPlatform()
                && useEpollNativeSelector
                && Epoll.isAvailable();
    }
}

