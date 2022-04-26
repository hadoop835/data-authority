package com.github.data.authority.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author chenzhh
 */
public final class DruidParseStatementsFactory {
    /**
     * 动态添加条件
     *
     * @param sql sql语句
     * @return
     */
    public static String parseStatements(String sql, Map<String, List<ParserColumnInfo>> parserColumnInfos) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
        if (Objects.isNull(sqlStatements) || sqlStatements.size() == 0) {
            return null;
        }
        for (SQLStatement sqlStatement : sqlStatements) {

            SelectParserVisitor parserVisitor = new SelectParserVisitor(parserColumnInfos);
            sqlStatement.accept(parserVisitor);
            return sqlStatement.toString();
        }
         return null;
    }
}
