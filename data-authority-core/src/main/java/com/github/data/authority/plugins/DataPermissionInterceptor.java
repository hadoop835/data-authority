package com.github.data.authority.plugins;

import com.alibaba.druid.DruidRuntimeException;
import com.github.data.authority.rule.DataPermission;
import com.github.data.authority.rule.DataPermissionHolder;
import com.github.data.authority.rule.DataPermissionRule;
import com.github.data.authority.rule.ParamFiled;
import com.github.data.authority.parser.DruidParseStatementsFactory;
import com.github.data.authority.parser.ParserColumnInfo;
import com.github.data.authority.util.PluginUtils;
import com.github.data.authority.util.ReflectionUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 数据权限
 *
 * @author chenzhh
 */
@Intercepts(
    {@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataPermissionInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataPermissionInterceptor.class);

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
        //反射获取参数名和参数类型
        List<ParamFiled> paramFileds = ReflectionUtils.getParamFiled(parameterObject);

        String mappedId = mappedStatement.getId();
        //基本类型
        //SimpleTypeRegistry.isSimpleType()
        // 获取原始执行的SQL
        String sql = (String) metaObject.getValue("delegate.boundSql.sql");
        //解析sql
        DataPermission dataPermission = DataPermissionHolder.getCurrentUser();
        if (Objects.nonNull(dataPermission)) {
            List<DataPermissionRule> rules = dataPermission.getDataPermissionRuleByMapperId(mappedId);
            Map<String, List<ParserColumnInfo>> parserColumnInfos = parserColumnInfo(rules, paramFileds);
            String newsql = DruidParseStatementsFactory.parseStatements(sql, parserColumnInfos);
            if (Objects.nonNull(newsql)) {
                sql = newsql;
            }
        }
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

    /**
     * 当前用户解析参数
     *
     * @param rules
     * @param paramFileds
     */
    private Map<String, List<ParserColumnInfo>> parserColumnInfo(List<DataPermissionRule> rules,
        List<ParamFiled> paramFileds) {
        Map<String, ParamFiled> paramFiled = getParamFiledByName(paramFileds);
        List<ParserColumnInfo> parserColumnInfos = Lists.newArrayList();
        Map<String, List<ParserColumnInfo>> listMap = Maps.newHashMap();
        if (Objects.nonNull(rules) && rules.size() > 0) {
            for (DataPermissionRule rule : rules) {
                if ("0".equals(rule.getOperator())) {
                    ParserColumnInfo parserColumnInfo = new ParserColumnInfo();
                    parserColumnInfo.setTable(rule.getTable());
                    parserColumnInfo.setColumn(customizeWhere(rule.getColumn(), paramFileds));
                    parserColumnInfo.setOperator(rule.getOperator());
                    parserColumnInfos.add(parserColumnInfo);
                } else {
                    if (Objects.nonNull(paramFiled) && paramFiled.size() > 0) {
                        ParamFiled filed = paramFiled.get(rule.getAttribute());
                        if (Objects.nonNull(filed)) {
                            ParserColumnInfo parserColumnInfo = new ParserColumnInfo();
                            String value = customizeWhere("#{" + rule.getAttribute() + "}", paramFileds);
                            if (Objects.nonNull(value) && !"".equals(value.trim())) {
                                parserColumnInfo.setTable(rule.getTable());
                                parserColumnInfo.setColumn(rule.getColumn()+" "+rule.getOperator()+" "+value);
                                parserColumnInfo.setOperator(rule.getOperator());
                                parserColumnInfos.add(parserColumnInfo);
                            }else{
                                LOGGER.warn("配置条件没有生效，由于改属性【"+rule.getColumn()+" "+rule.getOperator()+" " +rule.getAttribute()+"】无值");
                            }
                        }
                    }
                }
            }
        }
        if (Objects.nonNull(parserColumnInfos) && parserColumnInfos.size() > 0) {
            listMap = Multimaps.asMap(Multimaps.index(parserColumnInfos, new Function<ParserColumnInfo, String>() {
                @Override
                public String apply(ParserColumnInfo info) {
                    return info.getTable();
                }
            }));
        }
        return listMap;
    }

    private Map<String, ParamFiled> getParamFiledByName(List<ParamFiled> paramFileds) {
        Map<String, ParamFiled> result = Maps.newConcurrentMap();
        if (Objects.nonNull(paramFileds) && paramFileds.size() > 0) {
            for (ParamFiled filed : paramFileds) {
                result.put(filed.getName(), filed);
            }
        }
        return result;
    }

    /**
     * 自定义where条件
     *
     * @param column
     * @param paramFileds
     * @return
     */
    private String customizeWhere(String column, List<ParamFiled> paramFileds) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = new StandardEvaluationContext();
            column = column.replaceAll("#\\{", "#\\{#");
            for (ParamFiled filed : paramFileds) {
                context.setVariable(filed.getName(), filed.getValue());
            }
            Expression expression = parser.parseExpression(column, new TemplateParserContext());
            return expression.getValue(context, String.class);
        } catch (ExpressionException expressionException) {
            LOGGER.error("缺失参数，解析自定义sql条件异常【" + column + "】,{}", expressionException);
            throw new DruidRuntimeException("自定义sql，解析异常", expressionException);
        }
    }

}
