package com.github.data.authority.plugins;

import com.github.data.authority.ParamFiled;
import com.github.data.authority.util.PluginUtils;
import com.github.data.authority.util.ReflectionUtils;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * 数据权限
 *
 * @author chenzhh
 */
@Intercepts(
    {@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataPermissionInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        Objects.requireNonNull(statementHandler);
        //获取元数据类型
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        Objects.requireNonNull(metaObject);
        //获取mapper
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        Objects.requireNonNull(mappedStatement);
        //获取参数
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("delegate.parameterHandler");
        Object parameterObject = parameterHandler.getParameterObject();
        List<ParamFiled> paramFileds =  ReflectionUtils.getFiledsInfo(parameterObject);
        System.out.println(paramFileds);
        String mappedId = mappedStatement.getId();
        // 获取原始执行的SQL
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        //解析sql
        metaObject.setValue("delegate.boundSql.sql", sql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
