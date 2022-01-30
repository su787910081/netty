package com.abc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class SomeServerHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte b = in.readByte();
        short length = in.readShortLE();
        short version = in.readShortLE();
        long date = in.readLongLE();
        int num = in.readableBytes();
        byte[] context = new byte[num];
        in.readBytes(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formatDate = sdf.format(date);
        log.info("Server端接收到的数据，b: {}, length: {}, version: {}, date: {}, context: {}",
                0X00FF & b, length, version, formatDate, new String(context));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
