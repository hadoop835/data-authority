package com.github.data.authority.util;

import com.github.data.authority.rule.ParamFiled;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 反射工具类
 *
 * @author chenzhh
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * 获取参数值
     *
     * @param o
     * @return
     */
    public static List<ParamFiled> getParamFiled(Object o) {
        List<ParamFiled> list = Lists.newArrayList();
        if (o instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>) o;
            for (String param : paramMap.keySet()) {
                if (!param.startsWith("param")) {
                    ParamFiled filed = new ParamFiled();
                    filed.setName(param);
                    filed.setValue(paramMap.get(param));
                    list.add(filed);
                }
            }
        } else {
            Field[] fields = o.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Object value = getFieldValueByName(fields[i].getName(), o);
                if (Objects.nonNull(value) && !"".equals(value) && !"null".equals(value)) {
                    ParamFiled filed = new ParamFiled();
                    filed.setName(fields[i].getName());
                    filed.setValue(value);
                    list.add(filed);
                }

            }
        }
        return list;
    }

    /**
     * 根据属性名获取属性值
     */
    private final static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }

    }

//    private final static void paramFiled(Class<?> clazz, Object value, ParamFiled filed) {
//        if (clazz == List.class) {
//            List list = (List) value;
//            filed.setLen(list.size());
//        } else if (clazz == Set.class) {
//            Set set = (Set) value;
//            filed.setLen(set.size());
//        } else if (clazz == Map.class) {
//            Map map = (Map) value;
//            filed.setLen(map.size());
//        } else {
//            filed.setLen(0);
//        }
//    }
}
