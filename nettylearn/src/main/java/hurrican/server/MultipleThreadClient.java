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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by NewObject on 2017/8/25.
 */
public class MultipleThreadClient {
    private String host;

    private int port;

    private Bootstrap bootstrap = new Bootstrap();

    private List<Channel> channels = new ArrayList<>();

    private final AtomicInteger index = new AtomicInteger();

    public MultipleThreadClient(){
        this.host = "127.0.0.1";
        this.port = 8080;
    }

    public void init(int count){
        // 创建线程池
        EventLoopGroup worker = new NioEventLoopGroup();

        // 添加线程池
        bootstrap.group(worker);

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

        // 连接并获取多个通道
        for (int i = 0; i < count; i++) {
            ChannelFuture connect = bootstrap.connect(this.host, this.port);
            this.channels.add(connect.channel());
        }
    }

    public Channel nextChannel(){
        return getFirstActiveChannel(0);
    }

    private Channel getFirstActiveChannel(int count){
        Channel channel = this.channels.get(Math.abs(index.getAndIncrement()) % this.channels.size());
        if (!channel.isActive()) {
            reconnect(channel);
            if (count > channels.size()) {
                throw new RuntimeException("out of range, don't have too much channel");
            }
            return getFirstActiveChannel(count+1);
        }
        return channel;
    }

    private void reconnect(Channel channel) {
        synchronized (channel){
            if (this.channels.indexOf(channel) == -1) {
                return;
            }
            Channel newChannel = bootstrap.connect(this.host, this.port).channel();
            channels.set(channels.indexOf(channel), newChannel);
        }
    }
}
