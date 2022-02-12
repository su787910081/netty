package com.abc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class SomeClientHandler extends ChannelInboundHandlerAdapter {
    private String message = "Hello World";

    /**
     * OTHER: 预留(总数据为64 字节，不包含长度域)
     * +------------+------------+--------------------+--------------------+---------------+----------------+
     * |   VERSION  |   MSG_ID   |    REQUEST_DATE    |    RESPONSE_DATE   |   OTHER(40)   | Actual Content |
     * | 0x02FFFFFF | 0x000000FF | 0xFFFFFFFFFFFFFFFF | 0xFFFFFFFFFFFFFFFF |               | "HELLO, WORLD" |
     * +------------+------------+--------------------+--------------------+---------------+----------------+
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] bytes = message.getBytes();

        ByteBuf buffer = Unpooled.buffer(1024);
        int version = 1282;
        buffer.writeIntLE(version);
        int msgId = 2283;
        buffer.writeIntLE(msgId);
        buffer.writeLongLE(new Date().getTime());
        buffer.writeLongLE(0L);
        buffer.writeBytes(new byte[36]);
        buffer.writeBytes(bytes);
        // 将缓存中的数据写入到Channel
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
