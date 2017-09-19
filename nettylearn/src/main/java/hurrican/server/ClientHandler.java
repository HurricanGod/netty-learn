package hurrican.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by NewObject on 2017/8/25.
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
        // 往服务器发送信息
        // ctx.writeAndFlush("ok");
    }
}
