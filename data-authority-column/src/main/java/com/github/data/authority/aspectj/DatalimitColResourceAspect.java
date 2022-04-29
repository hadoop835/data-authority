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
package com.github.data.authority.aspectj;

import com.alibaba.fastjson2.JSON;
import com.github.data.authority.IDatalimitColumn;
import com.github.data.authority.annotation.DatalimitColumn;
import com.github.data.authority.exception.DatalimitColumnException;
import com.github.data.authority.filter.DataColumnPropertyPreFilter;
import com.github.data.authority.util.MethodUtil;
import com.github.data.authority.util.SimpleTypeRegistryUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 数据列级权限
 *
 * @author chenzhh
 */
@Aspect
public class DatalimitColResourceAspect extends AbstractDatalimitColAspectSupport {
    /**
     *
     */
    private IDatalimitColumn datalimitColumn;

    public DatalimitColResourceAspect(IDatalimitColumn datalimitColumn) {
        this.datalimitColumn = datalimitColumn;
    }

    @Pointcut("@annotation(com.github.data.authority.annotation.DatalimitColumn)")
    public void datalimitColumnAnnotationPointcut() {
    }

    @Around("datalimitColumnAnnotationPointcut()")
    public Object invokeResourceWithDatalimitColumn(ProceedingJoinPoint pjp) throws Throwable {
        Method originMethod = resolveMethod(pjp);
        DatalimitColumn annotation = originMethod.getAnnotation(DatalimitColumn.class);
        if (Objects.isNull(annotation)) {
            throw new IllegalStateException("权限注解【DatalimitColumn】不能为空");
        }
        try {
            Object result = pjp.proceed();
            String method = MethodUtil.resolveMethodName(originMethod);
            String value = annotation.method();
            if(Objects.nonNull(value) && !"".equals(value.trim())){
                method = method+"."+value;
            }
            List<String> columns = this.datalimitColumn.getDatalimitColumnByMethod(method);
            Class<?> ret = result.getClass();
            if (Objects.nonNull(ret) && !SimpleTypeRegistryUtil.isSimpleType(ret)) {
                DataColumnPropertyPreFilter simplePropertyPreFilter = new DataColumnPropertyPreFilter();
                if (Objects.nonNull(columns) && columns.size() > 0) {
                    simplePropertyPreFilter.getExcludes().addAll(columns);
                } else {
                    LOGGER.error("数据列级权限查询为空,方法为:{}", method);
                }
                return JSON.parseObject(JSON.toJSONString(result, simplePropertyPreFilter), ret);
            }
            return result;
        } catch (Exception ex) {
             throw  new DatalimitColumnException("数据列级权限拦截失败",ex);
        }
    }
}
