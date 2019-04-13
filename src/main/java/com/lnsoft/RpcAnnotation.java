package com.lnsoft;

import java.lang.annotation.*;

/**
 * Created By Chr on 2019/4/11/0011.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcAnnotation {

    //百度
    Class<?> value() default void.class;

    Class<?> interfaceName() default void.class;


}
