package com.github.data.authority.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 基本类型
 * @author chenzhh
 */
public class SimpleTypeRegistryUtil {
    private static final Set<Class<?>> SIMPLE_TYPE_SET = new HashSet();

    private SimpleTypeRegistryUtil() {
    }

    public static boolean isSimpleType(Class<?> clazz) {
        return SIMPLE_TYPE_SET.contains(clazz);
    }

    static {

        SIMPLE_TYPE_SET.add(String.class);
        SIMPLE_TYPE_SET.add(String[].class);
        SIMPLE_TYPE_SET.add(Byte.class);
        SIMPLE_TYPE_SET.add(byte.class);
        SIMPLE_TYPE_SET.add(byte[].class);
        SIMPLE_TYPE_SET.add(Short.class);
        SIMPLE_TYPE_SET.add(short.class);
        SIMPLE_TYPE_SET.add(short[].class);
        SIMPLE_TYPE_SET.add(Character.class);
        SIMPLE_TYPE_SET.add(char.class);
        SIMPLE_TYPE_SET.add(char[].class);
        SIMPLE_TYPE_SET.add(Integer.class);
        SIMPLE_TYPE_SET.add(Integer[].class);
        SIMPLE_TYPE_SET.add(int.class);
        SIMPLE_TYPE_SET.add(int[].class);
        SIMPLE_TYPE_SET.add(Long.class);
        SIMPLE_TYPE_SET.add(Long[].class);
        SIMPLE_TYPE_SET.add(long.class);
        SIMPLE_TYPE_SET.add(long[].class);
        SIMPLE_TYPE_SET.add(Float.class);
        SIMPLE_TYPE_SET.add(Float[].class);
        SIMPLE_TYPE_SET.add(float.class);
        SIMPLE_TYPE_SET.add(float[].class);
        SIMPLE_TYPE_SET.add(Double.class);
        SIMPLE_TYPE_SET.add(Double[].class);
        SIMPLE_TYPE_SET.add(double.class);
        SIMPLE_TYPE_SET.add(double[].class);
        SIMPLE_TYPE_SET.add(Boolean.class);
        SIMPLE_TYPE_SET.add(Boolean[].class);
        SIMPLE_TYPE_SET.add(boolean.class);
        SIMPLE_TYPE_SET.add(boolean[].class);
        SIMPLE_TYPE_SET.add(Date.class);
        SIMPLE_TYPE_SET.add(LocalDate.class);
        SIMPLE_TYPE_SET.add(LocalDateTime.class);
        SIMPLE_TYPE_SET.add(java.sql.Date.class);
        SIMPLE_TYPE_SET.add(Class.class);
        SIMPLE_TYPE_SET.add(BigInteger.class);
        SIMPLE_TYPE_SET.add(BigDecimal.class);
    }
}

