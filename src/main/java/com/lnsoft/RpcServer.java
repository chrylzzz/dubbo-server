package com.lnsoft;

import com.lnsoft.registry.IRegisterCenter;
import com.lnsoft.registry.IRegisterCenterImpl;
import com.lnsoft.registry.ZkConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于Netty：
 * 服务端被调用：监听的代码(发布服务，等待被调用，时刻监听)
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class RpcServer {


    //百度成员变量的创建
    private static final String serviceAddress = ZkConfig.CONNECTION_STR;
    private Map<String, Object> handlerMap = new HashMap<>();
    private IRegisterCenter registerCenter = new IRegisterCenterImpl();

    //不完整，百度吧
    public void publisher() {
        //1.发布服务
        for (String serviceName : handlerMap.keySet()) {
            registerCenter.doRegister(serviceName, serviceAddress);
        }
        //启动一个监听 Netty ServerSocket(ip,port)     Socket 监听端口，io交互
        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            //启动netty服务
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
//                    maxFrameLength:     帧的最大长度
//                    lengthFieldOffset length:       字段偏移的地址
//                    lengthFieldLength length;字段所占的字节长
//                    lengthAdjustment: 修改帧数据长度字段中定义的值，可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
//                    initialBytesToStrip: 解析时候跳过多少个长度
//                    failFast; 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异

                    //百度,第三个参数开始
                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, ));
                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                    pipeline.addLast("encoder", new ObjectEncoder());
                    //百度,第2个参数开始
                    pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers));
                    //Netty 不管  连接  10数据库交互 socket.IO   ----》都是用handler  类似于SpringMVC 的 Handler
                    //往pipeline里添加handler，这是netty核心
                    pipeline.addLast(new RpcServerHandler(handlerMap));


                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            //通过netty  进行监听  8080

            String[] address = serviceAddress.split(":");
            String ip = address[0];
            int port = Integer.parseInt(address[1]);

            //监听的url
            ChannelFuture future = bootstrap.bind(ip, port).sync();

            System.out.println(" Netty服务端启动成功，等待客户端的连接: ");
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
