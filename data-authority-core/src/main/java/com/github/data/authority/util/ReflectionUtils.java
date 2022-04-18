package com.github.data.authority.util;

import com.github.data.authority.annotation.ColumnAuthority;
import java.util.Objects;
import java.util.Optional;

/**
 * 反射工具类
 *
 * @author chenzhh
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * @param clazz
     * @return
     */
    public static Optional<String> getColumnAuthorityCode(Class<?> clazz) {
        ColumnAuthority columnAuthority = clazz.getAnnotation(ColumnAuthority.class);
        if (Objects.nonNull(columnAuthority)) {
            return Optional.ofNullable(columnAuthority.code());
        }
        return Optional.empty();
    }

}
