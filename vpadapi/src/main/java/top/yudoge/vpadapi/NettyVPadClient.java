//package top.yudoge.vpadapi;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//
//public class NettyVPadClient implements VPadClient {
//    protected Bootstrap bootstrap;
//    protected NioEventLoopGroup workerGroup;
//    protected ChannelFuture channelFuture;
//
//    @Override
//    public VPadConnection connect(VPadServer vPadServer) {
//        bootstrap = new Bootstrap()
//                    .group(workerGroup)
//                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//
//                        }
//                    });
//        channelFuture = bootstrap.connect(vPadServer.getIp(), Constants.PORT);
//        return new NettyVPadConnectionHandler(this);
//    }
//
//
//
//}
