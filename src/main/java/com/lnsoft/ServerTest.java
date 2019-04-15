package com.lnsoft;

import com.lnsoft.registry.IRegisterCenter;
import com.lnsoft.registry.IRegisterCenterImpl;
import com.lnsoft.rpcMethod.IChrHello;
import com.lnsoft.rpcMethod.IChrHelloImpl;
import com.lnsoft.server.RpcServer;

/**
 * 服务端：注册接口，对netty端口的监听
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class ServerTest {

    public static void main(String args[]) {

        //根据服务的名称，实例化对应的

        IChrHello iChrHello = new IChrHelloImpl();
        IRegisterCenter registerCenter = new IRegisterCenterImpl();

        //发布服务，监听端口  --》类中

        RpcServer rpcServer = new RpcServer(registerCenter, "127.0.0.1:8888");

        //服务端需要考虑的事情是，服务名字  绑定  对应多少个实例对象  2  3  4
        //服务名称和实例对象的关系

        rpcServer.bind(iChrHello);
        rpcServer.publisher();
    }
}
