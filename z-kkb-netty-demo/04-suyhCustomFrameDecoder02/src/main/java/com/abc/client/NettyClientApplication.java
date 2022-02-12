package com.abc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyClientApplication {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        /**
                         * 长度域的值包含长度域的长度(4 字节)：new {@link LengthFieldPrepender}(4, true)
                         * OTHER: 预留(总数据为64 字节，不包含长度域)
                         * +--------+------------+------------+--------------------+--------------------+---------------+----------------+
                         * | Length |   VERSION  |   MSG_ID   |    REQUEST_DATE    |    RESPONSE_DATE   |   OTHER(40)   | Actual Content |
                         * | 0x004C | 0x02FFFFFF | 0x000000FF | 0xFFFFFFFFFFFFFFFF | 0xFFFFFFFFFFFFFFFF |               | "HELLO, WORLD" |
                         * +--------+------------+------------+--------------------+--------------------+---------------+----------------+
                         */
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldPrepender(4, true));
                            pipeline.addLast(new SomeClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8888).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
