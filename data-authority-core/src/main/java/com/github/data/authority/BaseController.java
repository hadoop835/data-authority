package com.github.data.authority;

import com.alibaba.fastjson2.JSON;
import com.github.data.authority.filter.DataColumnPropertyPreFilter;
import com.github.data.authority.util.ReflectionUtils;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Resource;

/**
 * @author chenzhh
 */
public class BaseController {

    /**
     * 过滤器
     */
    @Resource
    private IDataColumnService dataColumnService;
    /**
     *
     */
    public BaseController(IDataColumnService dataColumnService) {
          this.dataColumnService = dataColumnService;
    }

    /**
     * 字段权限过滤
     *
     * @param clazz    返回值class
     * @param funT     函数入参
     * @param function 函数
     * @param <T>
     * @param <R>
     * @return
     */
    <T, R> R dataColumnFilter(Class<?> clazz, T funT, Function<T, R> function) {
        R ret = function.apply(funT);
        if (Objects.isNull(clazz)) {
            return ret;
        } else {
            String code = ReflectionUtils.getColumnAuthorityCode(clazz).get();
            List<String> cloumns = dataColumnService.getDataColumnByUserId(code);
            if (Objects.nonNull(cloumns) && cloumns.size() > 0) {
                DataColumnPropertyPreFilter simplePropertyPreFilter = new DataColumnPropertyPreFilter();
                simplePropertyPreFilter.getExcludes().addAll(cloumns);
                return JSON.parseObject(JSON.toJSONString(ret, simplePropertyPreFilter), (Type) ret.getClass());
            } else {
                return ret;
            }
        }
    }
}
