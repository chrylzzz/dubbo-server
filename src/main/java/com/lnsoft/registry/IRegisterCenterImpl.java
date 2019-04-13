package com.lnsoft.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * （1）注册功能
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class IRegisterCenterImpl implements IRegisterCenter {


    private CuratorFramework curatorFramework = null;

    {

        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkConfig.CONNECTION_STR).sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();

        curatorFramework.start();
    }


    //serviceName:com.lnsoft.IChrHello
    //serviceAddress:127.0.0.1:8080
    //将serviceName与serviceAddress绑定在一起注册在zk上
    @Override
    public void doRegister(String serviceName, String serviceAddress) {

        //registrys/com.lnsoft.IChrHello
        String servicePath = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;
        try {

            //判断 /registrys/IChrHello是否存在，不存在则创建
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                //不存在的话，创建   /registrys/IChrHello
                //服务名称创建，持久的：为什么？---因为该类服务可能以后也会用的到
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)//持久的
                        .forPath(servicePath, "0".getBytes());
            }
            //代码执行在这里，肯定存在 /registrys/IChrHello
            //服务发布的地址是：127.0.0.1:8080   address   registrys/com.lnsoft.IChrHello  127.0.0.1:8080,8081,8082 临时节点
            String addressPath = servicePath + "/" + serviceAddress;

            //服务地址url，临时的：为什么？---因为比如活动，有上下线，下线了就是临时的，下线之后就不存在zk
            String rsNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL)//临时的
                    .forPath(addressPath, "0".getBytes());

            System.out.println("服务注册成功：" + rsNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
