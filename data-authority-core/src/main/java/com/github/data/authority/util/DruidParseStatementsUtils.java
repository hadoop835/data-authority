package com.github.data.authority.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author chenzhh
 */
public class DruidParseStatementsUtils {
    // 需要动态添加条件的列名
    static String columnName = "name";
    static List<String> list = new ArrayList<>();

    static {
        list.add("wjp");
        list.add("gm");
    }

    /**
     * 动态添加条件
     *
     * @param sql sql语句
     * @return
     */
    public static String parseStatements(String sql) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, DbType.mysql);
        if (Objects.isNull(sqlStatements) || sqlStatements.size() == 0) {
            return null;
        }
        for (SQLStatement sqlStatement : sqlStatements) {
            //只解析select 查询语句
            if (sqlStatement instanceof SQLSelectStatement) {
                MySqlSchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
                sqlStatement.accept(schemaStatVisitor);
                //获取from后得表
                SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) sqlStatement;
                SQLSelect select = sqlSelectStatement.getSelect();
                Objects.requireNonNull(select, "解析查询语句失败");
                SQLSelectQuery selectQuery = select.getQuery();
                selectQuery(selectQuery);
                return select.toString();
            }
        }
        return null;
    }

    private static void selectQuery(SQLSelectQuery selectQuery) {
        if (selectQuery instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) selectQuery;
            SQLTableSource from = sqlSelectQueryBlock.getFrom();
            SQLExpr where = sqlSelectQueryBlock.getWhere();
            conditionSub(where);
            if (from instanceof SQLJoinTableSource) {
                SQLExpr sqlExpr = null;
                if (Objects.nonNull(where)) {
                    sqlExpr = condition(where, from.getAlias(), columnName, list);
                } else {
                    sqlExpr = condition(SQLUtils.toSQLExpr("a.id=1"), from.getAlias(), columnName, list);
                }
                sqlSelectQueryBlock.setWhere(sqlExpr);
            }
            boolean flag = getSQLTableSource(from);
            if (flag) {
                SQLExpr sqlExpr = condition(where, from.getAlias(), columnName, list);
                sqlSelectQueryBlock.setWhere(sqlExpr);
            }

        } else if (selectQuery instanceof SQLUnionQuery) {
            SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) selectQuery;
            selectQuery(sqlUnionQuery.getLeft());
            selectQuery(sqlUnionQuery.getRight());
        }
    }

    /**
     * 分析表
     *
     * @param sqlTableSource
     */
    private static boolean getSQLTableSource(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) sqlTableSource;
//            SQLTableSource left = sqlJoinTableSource.getLeft();
//            if (getSQLTableSource(left)) {
//                SQLExpr sqlExpr = condition(sqlJoinTableSource.getCondition(), left.getAlias(), columnName,list );
//                sqlJoinTableSource.setCondition(sqlExpr);
//            }
//            SQLTableSource right = sqlJoinTableSource.getRight();
//            if (getSQLTableSource(right)) {
//                SQLExpr sqlExpr = condition(sqlJoinTableSource.getCondition(), right.getAlias(), columnName, list);
//                sqlJoinTableSource.setCondition(sqlExpr);
//            }
            return false;
        } else if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) sqlTableSource;
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) subqueryTableSource.getSelect().getQuery();
            selectQuery(sqlSelectQueryBlock);
            return false;

        } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource sqlUnionQueryTableSource = (SQLUnionQueryTableSource) sqlTableSource;
            SQLUnionQuery sqlUnionQuery = sqlUnionQueryTableSource.getUnion();
            selectQuery(sqlUnionQuery);
            return false;
        } else if (sqlTableSource instanceof SQLExprTableSource) {
            SQLExprTableSource exprTableSource = (SQLExprTableSource) sqlTableSource;
            SQLExpr sqlExpr = exprTableSource.getExpr();
            if (sqlExpr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) sqlExpr;
                String lowerName = sqlIdentifierExpr.getLowerName();
                String filterTable = "student";
                if (lowerName.equals(filterTable) || lowerName.equals("`" + filterTable + "`")) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 处理查询条件
     *
     * @param sqlExpr
     * @param alias
     * @param column
     * @param list
     * @return
     */
    private static SQLExpr condition(SQLExpr sqlExpr, String alias, String column, List<String> list) {
        if (sqlExpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr(DbType.mysql);
            sqlBinaryOpExpr.setOperator(SQLBinaryOperator.BooleanAnd);
            sqlBinaryOpExpr.setParent(sqlExpr.getParent());
            sqlBinaryOpExpr.setLeft(sqlExpr);
            SQLInListExpr sqlInListExpr;
            SQLBinaryOpExpr operator;
            if (Objects.isNull(alias)) {
                //没有别名，直接用列名
                SQLIdentifierExpr sqlIdentifierExpr = new SQLIdentifierExpr(column);
                operator = new SQLBinaryOpExpr();
                operator.setLeft(sqlIdentifierExpr);
                operator.setOperator(SQLBinaryOperator.NotILike);
                operator.setRight(SQLUtils.toSQLExpr("?"));
                // sqlInListExpr = new SQLInListExpr(sqlIdentifierExpr);
                //sqlIdentifierExpr.setParent(sqlInListExpr);
            } else {
                SQLIdentifierExpr sqlIdentifierExpr = new SQLIdentifierExpr(alias);
                SQLPropertyExpr sqlPropertyExpr = new SQLPropertyExpr(sqlIdentifierExpr, column);
                sqlIdentifierExpr.setParent(sqlPropertyExpr);
                operator = new SQLBinaryOpExpr();
                operator.setLeft(sqlPropertyExpr);
                operator.setOperator(SQLBinaryOperator.NotILike);
                operator.setRight(SQLUtils.toSQLExpr("?"));
                //sqlInListExpr = new SQLInListExpr(sqlPropertyExpr);
            }
            List<SQLExpr> sqlExprs = Lists.newArrayList();
            if (Objects.nonNull(list) && list.size() > 0) {
                for (String expr : list) {
                    SQLCharExpr sqlCharExpr = new SQLCharExpr(expr);
                    sqlCharExpr.setParent(sqlExpr.getParent());
                    sqlExprs.add(sqlCharExpr);
                }
            }
            //sqlInListExpr.setTargetList(sqlExprs);
            sqlBinaryOpExpr.setRight(operator);
            return sqlBinaryOpExpr;
        }
        return sqlExpr;
    }

    /**
     * 子查询条件
     *
     * @param where
     */
    private static void conditionSub(SQLExpr where) {
        if (where instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) where;
            conditionSub(sqlBinaryOpExpr.getLeft());
            conditionSub(sqlBinaryOpExpr.getRight());
        } else if (where instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr sqlInSubQueryExpr = (SQLInSubQueryExpr) where;
            SQLSelectQuery sqlSelectQuery = sqlInSubQueryExpr.getSubQuery().getQuery();
            selectQuery(sqlSelectQuery);
        }
    }
}
