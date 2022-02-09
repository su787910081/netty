package com.huawei.service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class SomeServerHandler extends ByteToMessageDecoder {
    /**
     * OTHER: 预留(总数据为64 字节，不包含长度域)
     * +------------+------------+--------------------+--------------------+---------------+----------------+
     * |   VERSION  |   MSG_ID   |    REQUEST_DATE    |    RESPONSE_DATE   |   OTHER(40)   | Actual Content |
     * | 0x02FFFFFF | 0x000000FF | 0xFFFFFFFFFFFFFFFF | 0xFFFFFFFFFFFFFFFF |               | "HELLO, WORLD" |
     * +------------+------------+--------------------+--------------------+---------------+----------------+
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        int version = buf.readIntLE();
        int msgId = buf.readIntLE();
        long reqTime = buf.readLongLE();
        buf.readLongLE();   // 响应时间，这里不用，但必须读走这8 字节。
        byte[] skipBytes = new byte[36];
        buf.readBytes(skipBytes);
        int bodyLen = buf.readableBytes();
        byte[] context = new byte[bodyLen];
        buf.readBytes(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formatDate = sdf.format(reqTime);
        log.info("Server端接收到的数据，body length: {}, version: {}, msgId: {}, date: {}, context: {}",
                bodyLen, version, msgId, formatDate, new String(context));
    }
}
