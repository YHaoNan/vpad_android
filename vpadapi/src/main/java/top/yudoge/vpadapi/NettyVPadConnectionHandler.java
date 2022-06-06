//package top.yudoge.vpadapi;
//
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.nio.AbstractNioByteChannel;
//import top.yudoge.vpadapi.structure.Message;
//
//public final class NettyVPadConnectionHandler extends SimpleChannelInboundHandler<Message> implements VPadConnection {
//
//    private NettyVPadClient client;
//    private ChannelHandlerContext ctx;
//    private OnMessageArrivedListener onMessageArrivedListener;
//
//    protected NettyVPadConnectionHandler(NettyVPadClient client) {
//        this.client = client;
//    }
//
//    public void setOnMessageArrivedListener(OnMessageArrivedListener onMessageArrivedListener) {
//        this.onMessageArrivedListener = onMessageArrivedListener;
//    }
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        this.ctx = ctx;
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        disconnect();
//    }
//
//    @Override
//    public void disconnect() {
//        ctx.close();
//    }
//
//    @Override
//    public void sendMessage(Message msg) {
//        ctx.writeAndFlush(msg);
//    }
//
//    /**
//     * 之前API设计的缺陷，之前的读取操作是阻塞在`read`上，等待来一个`Message.op`符号对应的消息
//     * netty框架不支持这种读取方式（不知道是否支持，但是貌似没人会这么用）。
//     * 所以，做出了如下迫不得已的决定，废弃该Connection实现类的`readMessage(Message)`方法
//     */
//    @Override
//    public void readMessage(Message msg) {
//        throw new RuntimeException("无法使用NettyVPadConnectionHandler的read(Message)方法，请使用onMessageArrived替代");
//    }
//
//    @Override
//    public boolean isClosed() {
//        return !client.channelFuture.channel().isOpen();
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
//        if (onMessageArrivedListener!=null) onMessageArrivedListener.onMessageArrived(msg);
//    }
//}
