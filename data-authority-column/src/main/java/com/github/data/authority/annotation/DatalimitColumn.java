package com.github.data.authority.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据列级权限
 * @author chenzhh
 */
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface DatalimitColumn {
    /**
     * 返回数据类型
     * @return
     */
    Class<?> clazz ();

    /**
     * 方法说明
     * @return
     */
    String  name();
    /**
     * 方法名称
     * @return
     */
    String  method() default "";

}
