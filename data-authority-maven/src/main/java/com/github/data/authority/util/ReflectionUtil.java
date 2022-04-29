package com.github.data.authority.util;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

/**
 * 反射获取所有字段
 *
 * @author chenzhh
 */
public final class ReflectionUtil {
    /**
     *
     * @param clazz
     * @return
     */
    public final static List<Field> fieldList(Class<?> clazz) {
        List<Field> list = Lists.newArrayList();
        while (Objects.nonNull(clazz)) {
            list.addAll(Lists.newArrayList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        list.removeIf(field -> Modifier.isStatic(field.getModifiers()));
        return list;
    }
}
