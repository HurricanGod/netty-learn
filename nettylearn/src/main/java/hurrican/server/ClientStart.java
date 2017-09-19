package hurrican.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by NewObject on 2017/8/25.
 */
public class ClientStart {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();

        // 创建线程池
        EventLoopGroup client = new NioEventLoopGroup();

        // 添加线程池
        bootstrap.group(client);

        // 设置socket工厂
        bootstrap.channel(NioSocketChannel.class);

        // 设置管道
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });

        // 连接服务器
        // ctrl + alt + v  =>  快速引入变量
        ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean failure = false;
        while (true) {
            try {
                String msg = reader.readLine();
                future.channel().writeAndFlush(msg);
            } catch (IOException e) {
                failure = true;
                e.printStackTrace();
            }finally {
                // 优雅地关闭客户端
                if (failure) {
                    client.shutdownGracefully();
                }
            }
        }
    }
}
