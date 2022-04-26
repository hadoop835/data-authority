/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.data.authority.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author chenzhh
 */
public final class MethodUtil {

    private static final Map<Method, String> methodNameMap = new ConcurrentHashMap<Method, String>();

    private static final Object LOCK = new Object();

    /**
     * Parse and resolve the method name, then cache to the map.
     *
     * @param method method instance
     * @return resolved method name
     */
    public static String resolveMethodName(Method method) {
        if (Objects.isNull(method)) {
            throw new IllegalArgumentException("方法名不能为空");
        }
        String methodName = methodNameMap.get(method);
        if (Objects.isNull(methodName)) {
            synchronized (LOCK) {
                methodName = methodNameMap.get(method);
                if (Objects.isNull(methodName)) {
                    StringBuilder sb = new StringBuilder();

                    String className = method.getDeclaringClass().getName();
                    String name = method.getName();
                    Class<?>[] params = method.getParameterTypes();
                    sb.append(className).append(":").append(name);
                    sb.append("(");
                    int paramPos = 0;
                    for (Class<?> clazz : params) {
                        sb.append(clazz.getCanonicalName());
                        if (++paramPos < params.length) {
                            sb.append(",");
                        }
                    }
                    sb.append(")");
                    methodName = sb.toString();
                    methodNameMap.put(method, methodName);
                }
            }
        }
        return methodName;
    }

    /**
     * For test.
     */
    static void clearMethodMap() {
        methodNameMap.clear();
    }
}
