package com.github.data.authority.util;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 反射获取所有字段
 *
 * @author chenzhh
 */
public final class ReflectionUtil {
    /**
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

    /**
     * @param clazz
     * @return
     */
    public final static void field(String prefix, Set<String> result, Class<?> clazz) {
        List<Field> fields = ReflectionUtil.fieldList(clazz);
        if (!org.springframework.util.CollectionUtils.isEmpty(fields)) {
            for (Field field : fields) {
                if (SimpleTypeRegistryUtil.isSimpleType(field.getType())) {
                    if (Objects.nonNull(prefix)) {
                        result.add(prefix + "." + field.getName());
                    } else {
                        result.add(field.getName());
                    }
                } else if (field.getType() == List.class || field.getType() == Map.class || field.getType() == Set.class) {
                    Type type = field.getGenericType();
                    if (type instanceof ParameterizedType) {
                        Class<?> parameterizedType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
                        if (SimpleTypeRegistryUtil.isSimpleType(parameterizedType)) {
                            if (Objects.nonNull(prefix)) {
                                result.add(prefix + "." + field.getName());
                            }else{
                                result.add(field.getName());
                            }

                        } else {
                            if (clazz == parameterizedType) {
                                List<Field> parameterfields = ReflectionUtil.fieldList(parameterizedType);
                                for (Field parameterfield : parameterfields) {
                                    if (SimpleTypeRegistryUtil.isSimpleType(parameterfield.getType())) {
                                        result.add(field.getName() + "." + parameterfield.getName());
                                    }
                                }
                            } else {
                                if (Objects.nonNull(prefix)) {
                                    field(prefix+"."+field.getName(), result, parameterizedType);
                                }else{
                                    field(field.getName(), result, parameterizedType);
                                }

                            }
                        }
                    }
                } else {
                    if (clazz == field.getType()) {
                        List<Field> parameterfields = ReflectionUtil.fieldList(field.getType());
                        for (Field parameterfield : parameterfields) {
                            if (SimpleTypeRegistryUtil.isSimpleType(parameterfield.getType())) {
                                if (Objects.nonNull(prefix)) {
                                    result.add(prefix+"."+field.getName() + "." + parameterfield.getName());
                                }else{
                                    result.add(field.getName() + "." + parameterfield.getName());
                                }

                            }
                        }
                    } else {
                        if (Objects.nonNull(prefix)) {
                            field(prefix+"."+field.getName(), result, field.getType());
                        }else{
                            field(field.getName(), result, field.getType());
                        }

                    }

                }
            }
        }
    }
}
