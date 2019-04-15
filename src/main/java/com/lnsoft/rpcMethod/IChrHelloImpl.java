package com.lnsoft.rpcMethod;

/**
 * Created By Chr on 2019/4/11/0011.
 */
@RpcAnnotation(IChrHello.class)
public class IChrHelloImpl implements IChrHello {
    @Override
    public String sayHello(String msg) {
        return "From ,  message I'm " + msg;
    }
}
