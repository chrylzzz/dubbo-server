package com.lnsoft.server;

import com.lnsoft.registry.IRegisterCenter;
import com.lnsoft.registry.IRegisterCenterImpl;
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
import io.netty.handler.timeout.IdleStateHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于Netty：与客户端交互
 * 服务端被调用：监听的代码(发布服务，等待被调用，时刻监听)
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class RpcServer {

    private String serviceAddress;
    //百度成员变量的创建
    //private static final String serviceAddress = ZkConfig.CONNECTION_STR;
    private Map<String, Object> handlerMap = new HashMap<>();
    private IRegisterCenter registerCenter = new IRegisterCenterImpl();

    public RpcServer(IRegisterCenter registerCenter, String serviceAddress) {
        this.serviceAddress = serviceAddress;
        this.registerCenter = registerCenter;
    }

    //zk端口yu发布的方法绑定
    public void bind(Object object) {

    }

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
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {//连接处理
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();//任务处理
//                    maxFrameLength:     帧的最大长度
//                    lengthFieldOffset length:       字段偏移的地址
//                    lengthFieldLength length;字段所占的字节长
//                    lengthAdjustment: 修改帧数据长度字段中定义的值，可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
//                    initialBytesToStrip: 解析时候跳过多少个长度
//                    failFast; 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异


//                            pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,));
//                            pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
//                            pipeline.addLast("encoder",new ObjectEncoder());
//                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.));


                    //百度,第三个参数开始：百度的4,0,4
                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                    pipeline.addLast("encoder", new ObjectEncoder());
                    //百度,第2个参数开始：百度的.cacheDisabled(null)
                    pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                    //Netty 不管  连接  10数据交互 socket.IO   ----》都是用handler  类似于SpringMVC 的 Handler
                    /**
                     * 往pipeline里添加handler，这是netty核心，netty让io交互
                     */
                    pipeline.addLast(new RpcServerHandler(handlerMap));
                    //心跳检测，读超时，写超时，读写超时
                    pipeline.addLast(
                            new IdleStateHandler(5,//
                                    0,//
                                    0,//
                                    TimeUnit.SECONDS));

                }
            }).option(ChannelOption.SO_BACKLOG, 128)//保持连接数
                    .childOption(ChannelOption.SO_KEEPALIVE, true);//有数据立即发送

            //通过netty  进行监听  8080

            String[] address = serviceAddress.split(":");
            String ip = address[0];
            int port = Integer.parseInt(address[1]);

            /**
             * 服务端监听的url
             */
            ChannelFuture future = bootstrap.bind(ip, port).sync();

            System.out.println(" Netty服务端启动成功，等待客户端的连接: ");
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
