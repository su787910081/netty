package com.abc.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyServerApplication {
    public static void main(String[] args) throws InterruptedException {

        // 用于处理客户端连接请求，将请求发送给childGroup中的eventLoop
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        // 用于处理客户端请求
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            // 用户启动ServerChannel
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)  // 指定eventLoopGroup
                    .channel(NioServerSocketChannel.class)  // 指定使用NIO进行通信
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        /**
                         * 当Channel初始创建完毕后就会触发该方法的执行，用于初始化Channel
                         * 长度域的值包含长度域的长度(4 字节)
                         * OTHER: 预留(总数据为64 字节，不包含长度域)
                         * +--------+------------+------------+--------------------+--------------------+---------------+----------------+
                         * | Length |   VERSION  |   MSG_ID   |    REQUEST_DATE    |    RESPONSE_DATE   |   OTHER(40)   | Actual Content |
                         * | 0x004C | 0x02FFFFFF | 0x000000FF | 0xFFFFFFFFFFFFFFFF | 0xFFFFFFFFFFFFFFFF |               | "HELLO, WORLD" |
                         * +--------+------------+------------+--------------------+--------------------+---------------+----------------+
                         */
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 从Channel中获取pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //
                            // 0: 长度域偏移量
                            // 4: 长度域是int 类型
                            // -4: 除长度域之外，其他数据都要自行解析。将长度域校正扣减掉。
                            // 4: 将长度域剔除出去不需要(长度域是int 类型)
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, -4, 4));
                            pipeline.addLast(new com.abc.service.SomeServerHandler());
                        }
                    });

            // 指定当前服务器所监听的端口号
            // bind()方法的执行是异步的
            // sync()方法会使bind()操作与后续的代码的执行由异步变为了同步
            ChannelFuture future = bootstrap.bind(8888).sync();
            System.out.println("服务器启动成功。监听的端口号为：8888");
            // 关闭Channel
            // closeFuture()的执行是异步的。
            // 当Channel调用了close()方法并关闭成功后才会触发closeFuture()方法的执行
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
