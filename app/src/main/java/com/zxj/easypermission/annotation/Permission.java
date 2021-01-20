package com.zxj.easypermission.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * 请求的权限
     * @return
     */
    String[] value();

    /**
     * 请求码
     * @return
     */
    int requestCode();
}
