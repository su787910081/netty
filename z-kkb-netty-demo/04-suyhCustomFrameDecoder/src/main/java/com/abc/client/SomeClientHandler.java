package com.abc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class SomeClientHandler extends ChannelInboundHandlerAdapter {

    private String message = "Hello World";

    /**
     * +------+--------+---------+--------------------+----------------+
     * | HDR1 | Length | VERSION |    CURRENT_DATE    | Actual Content |
     * | 0xCA | 0x0010 | 0x02    | 0xFFFFFFFFFFFFFFFF | "HELLO, WORLD" |
     * +------+--------+---------+--------------------+----------------+
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte[] bytes = message.getBytes();

        ByteBuf buffer = Unpooled.buffer(1024);
        buffer.writeByte(0XCA);
        buffer.writeShortLE(bytes.length);
        buffer.writeShortLE(2);
        buffer.writeLongLE(new Date().getTime());
        buffer.writeBytes(bytes);
        // 将缓存中的数据写入到Channel
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
