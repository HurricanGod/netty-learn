package hurrican.other;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by NewObject on 2017/8/25.
 */
public class HeartInspectServer {

    public static void main(String[] args) {
        /**
        *  创建服务类
        */
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        /**
         *  创建boss线程池
         */
        EventLoopGroup boss = new NioEventLoopGroup();

        /**
         *  创建worker线程池
         */
        EventLoopGroup worker = new NioEventLoopGroup();

        serverBootstrap.group(boss, worker);

        /**
         * 注册ServerSocket工厂
         */
        serverBootstrap.channel(NioServerSocketChannel.class);

        /**
         *  设置管道
         */
        serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new IdleStateHandler(5, 5, 10));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new SessionHandler());
            }
        });

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);

        ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8080);
        System.out.println("start service");

        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
