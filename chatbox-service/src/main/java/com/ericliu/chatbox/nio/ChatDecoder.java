package com.ericliu.chatbox.nio;

import com.ericliu.chatbox.model.ChatMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 继承自LengthFieldBasedFrameDecoder解决粘包和半粘包问题
 *
 * @author <a href=mailto:ericliu@fivewh.com>ericliu</a>,Date:2019/3/11
 */
public class ChatDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger log = LoggerFactory.getLogger(ChatDecoder.class);

    private static final int FRAME_MAX_LENGTH = 16777216;

    public ChatDecoder() {
        //FRAME_MAX_LENGTH 单个包最大长度
        //lengthFieldOffset 表示数据长度字段开始的偏移量
        //lengthFieldLength 数据长度字段的所占的字节数
        //lengthAdjustment 满足lengthAdjustment=bytes.length-lengthFieldOffset-lengthFieldLength-body.bytes
        //initialBytesToStrip 表示从整个包第一个字节开始，向后忽略的字节数
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (null == frame) {
            return null;
        }

        ByteBuffer byteBuffer = frame.nioBuffer();

        return ChatMessage.decode(byteBuffer);
    }
}
