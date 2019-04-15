package com.lnsoft.server;

import com.lnsoft.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端自定义的handler：服务端与客户端进行交互，io交互，继承ChannelInboundHandlerAdapter
 * 覆写channelRead。
 * <p>
 * 该类是客户端往服务端传送数据的--接收类
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    //百度
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * @param ctx 发送数据
     * @param msg 接收数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //ctx：可以用来想客户端发送数据
        //msg：可以取到客户端发来的数据

        //获得客户端的传输过来的数据--->RpcRequest序列化反序列化吗？
        RpcRequest rpcRequest = (RpcRequest) msg;

        Object result = new Object();
        //Client--->使命


        //服务名字  IChrHello
        if (handlerMap.containsKey(rpcRequest.getClassName())) {
            //通过反射，用子类对象进行
            Object clazz = handlerMap.get(rpcRequest.getClassName());

            /**
             * 服务端接收到客户端调用的方法参数等，通过反射调用服务端的实现类，得到result结果
             */
            Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getTypes());
            result = method.invoke(clazz, rpcRequest.getParams());
        }

        //服务端处理的数据，写给客户端result结果，客户端在通过handler进行处理
        ctx.write(result);
        ctx.flush();
        ctx.close();

    }
}
