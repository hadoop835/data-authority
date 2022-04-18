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
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnAuthority {
     /**
      * 功能编码,与菜单编码一致
      * @return
      */
     String  code();

     /**
      * 应用编码
      * @return
      */
     String  application() default "";
}
