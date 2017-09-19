package hurrican.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by NewObject on 2017/8/25.
 */
public class ServerStart {
    public static void main(String[] args) {
        // 创建服务类
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 创建boss线程池用于管理连接建立
        EventLoopGroup boss = new NioEventLoopGroup();
        // 创建worker线程用于处理会话通信
        EventLoopGroup worker = new NioEventLoopGroup();

        bootstrap.group(boss, worker);

        // 设置socket工厂
        bootstrap.channel(NioServerSocketChannel.class);

        // 设置管道工厂
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(new StringEncoder());
                channel.pipeline().addLast(new ServerHandler());
            }
        });

        //serverSocketChannel的设置，链接缓冲池的大小
        bootstrap.option(ChannelOption.SO_BACKLOG, 2048);
        //serverSocketChannel的设置,维持链接的活跃，清除死链接
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        //serverSocketChannel的设置,关闭延迟发送
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

        ChannelFuture channelFuture = bootstrap.bind(8080);
        System.out.println("start");
        try {
            // 等待服务端关闭
            channelFuture.channel().closeFuture().sync();
            System.out.println("服务器已关闭");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 释放资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
