package com.lnsoft;


import com.lnsoft.registry.IRegisterCenter;
import com.lnsoft.registry.IRegisterCenterImpl;

import java.io.IOException;

/**
 * 第一步：（1）注册功能的测试
 * <p>
 * 测试zk的持久和临时
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class Test {

    public static void main(String args[]) throws IOException {
        IRegisterCenter iRegisterCenter
                = new IRegisterCenterImpl();
        iRegisterCenter.doRegister("com.lnsoft.IChr",//
                "127.0.0.1:9090");

        //先注释，在删除， 测试：
        // 不注释：持久
        // 注释：临时的
        System.in.read();
    }
}
