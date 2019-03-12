package com.ericliu.chatbox.nio;

import com.ericliu.chatbox.model.Protocal;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 继承自LengthFieldBasedFrameDecoder解决粘包和半粘包问题
 *
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ChatProtocalEncoder extends MessageToByteEncoder<Protocal> {

    private static final Logger log = LoggerFactory.getLogger(ChatProtocalEncoder.class);

    private static final int FRAME_MAX_LENGTH = 1024;

    protected void encode(ChannelHandlerContext ctx, Protocal protocal, ByteBuf out) {
        out.writeInt(protocal.getHeader().getHeadData());
        out.writeByte(protocal.getHeader().getDataType());
        out.writeInt(protocal.getData().length);
        out.writeBytes(protocal.getData());
    }
}
