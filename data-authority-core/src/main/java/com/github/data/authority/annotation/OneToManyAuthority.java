package com.github.data.authority.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列权限注解
 * @author chenzhh
 */
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToManyAuthority {
     /**
      * 字段名称
      * @return
      */
     String  field();

    /**
     * 字段说名
      * @return
     */
     String  note();


}
