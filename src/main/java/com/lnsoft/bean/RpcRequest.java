package com.lnsoft.bean;

import java.io.Serializable;

/**
 * 发送和接口的数据包装
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class RpcRequest implements Serializable {


    private static final long serialVersionUID = -4126222781012055227L;
    private String className;
    private String methodName;
    private Class<?>[] types;
    private Object[] params;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
