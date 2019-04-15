package com.lnsoft.registry;

/**
 * Created By Chr on 2019/4/11/0011.
 */
public interface IRegisterCenter {

    //serviceName:com.lnsoft.rpcMethod.IChrHello

    //serviceAddress:127.0.0.1:8080

    //将serviceName与serviceAddress绑定在一起注册在zk上

    void doRegister(String serviceName, String serviceAddress);
}
