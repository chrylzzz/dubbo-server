package com.lnsoft;

import com.lnsoft.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端往客户端写数据
 * io交互，继承ChannelInboundHandlerAdapter
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    //百度
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

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
            //用子类对象进行
            Object clazz = handlerMap.get(rpcRequest.getClassName());

            Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getTypes());
            result = method.invoke(clazz, rpcRequest.getParams());
        }

        //写给客户端result结果
        ctx.write(result);
        ctx.flush();
        ctx.close();

    }
}
