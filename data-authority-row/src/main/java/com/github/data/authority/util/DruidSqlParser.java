package com.github.data.authority.util;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WJP
 * @Date: 2021/10/27 10:01
 */
public class DruidSqlParser {
    // 需要动态添加条件的表名
    static String filterTable = "student";
    // 由于有些时候表名会使用 ` 符号包裹，暂时不知道 druid sql parser 如何格式化表名
    static String filterTableSymbol = "`student`";

    // 需要动态添加条件的列名
    static String columnName = "name";
    static List<String> list = new ArrayList<>();

    static {
        list.add("wjp");
        list.add("gm");
    }

    static String customSql = "SELECT id,username,email,`password`\n" +
        "FROM `student` a\n" +
        "WHERE a.id = 2\n" +
        "UNION ALL\n" +
        "SELECT id,username,email,`password`\n" +
        "FROM `student` b\n" +
        "WHERE b.id = 5";

    static String customSql1 = "SELECT\n" +
        "\ta.*,b.*\n" +
        "FROM\n" +
        "\t`student` a\n" +
        "JOIN `user_top` b\n" +
        "on a.id = b.id";

    public static void main(String[] args) {
        String sql = customSql;
        // 新建 MySQL Parser
        SQLStatementParser parser = new MySqlStatementParser(sql);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLSelectStatement statement = (SQLSelectStatement) parser.parseStatement();
        // 从 statement 中拿出 select 信息
        SQLSelect select = statement.getSelect();
        SQLSelectQuery sqlSelectQuery = select.getQuery();
        // 分析 select 语句
        getTable(sqlSelectQuery);
        System.out.println(select.toString());
    }

    /**
     * 从 sqlQuery 中提取信息
     *
     * @param sqlSelectQuery
     */
    public static void getTable(SQLSelectQuery sqlSelectQuery) {
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            // 如果是查询语句块
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
            // 获取 from 和 where 里包含的东西
            SQLTableSource from = sqlSelectQueryBlock.getFrom();
            SQLExpr where = sqlSelectQueryBlock.getWhere();
            // 分析 where 中是否包含子查询，子查询中是否包含配置的表名
            conditionInSub(where);
            // 如果是配置的表名则返回 true，进行修改条件操作
            boolean flag = getSource(from);
            if (flag) {
                SQLExpr sqlExpr = constructCondition(where, from.getAlias(), columnName, list);
                sqlSelectQueryBlock.setWhere(sqlExpr);
            }
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            // 如果是 union 语句块
            SQLUnionQuery mySqlUnionQuery = (SQLUnionQuery) sqlSelectQuery;
            // 递归调用
            getTable(mySqlUnionQuery.getLeft());
            getTable(mySqlUnionQuery.getRight());
        }
    }

    /**
     * 从 tablesource 中提取信息
     *
     * @param sqlTableSource
     * @return
     */
    public static boolean getSource(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLJoinTableSource) {
            // 如果是 join 语句块
            SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) sqlTableSource;
            SQLTableSource left = sqlJoinTableSource.getLeft();
            SQLTableSource right = sqlJoinTableSource.getRight();
            // 如果对应的表名存在则添加 join 条件
            if (getSource(left)) {
                // 构造条件
                SQLExpr sqlExpr = constructCondition(sqlJoinTableSource.getCondition(), left.getAlias(), columnName, list);
                sqlJoinTableSource.setCondition(sqlExpr);
            }
//            getSource(right);
            if (getSource(right)) {
                // 构造条件
                SQLExpr sqlExpr = constructCondition(sqlJoinTableSource.getCondition(), right.getAlias(), columnName, list);
                sqlJoinTableSource.setCondition(sqlExpr);
            }
            return false;
        } else if (sqlTableSource instanceof SQLSubqueryTableSource) {
            // 如果是子查询
            SQLSelectQueryBlock query = (SQLSelectQueryBlock) ((SQLSubqueryTableSource) sqlTableSource).getSelect().getQuery();
            getTable(query);
            return false;
        } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
            // 如果是 union
            SQLUnionQuery union = (SQLUnionQuery) ((SQLUnionQueryTableSource) sqlTableSource).getUnion();
            getTable(union);
            return false;
        } else if (sqlTableSource instanceof SQLExprTableSource) {
            // 如果是表名（已到终点）
            SQLExpr expr = ((SQLExprTableSource) sqlTableSource).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) expr;
                String lowerName = sqlIdentifierExpr.getLowerName();
                // 如果是指定的表名
                if (lowerName.equals(filterTable) || lowerName.equals(filterTableSymbol)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public static SQLExpr constructCondition(SQLExpr expr, String alias, String columnName, List<String> list) {
        // 如果是表达式
        if (expr instanceof SQLBinaryOpExpr) {
            // 构建新的 SQLBinaryOpExpr
            SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr();
            sqlBinaryOpExpr.setOperator(SQLBinaryOperator.BooleanAnd);
            sqlBinaryOpExpr.setParent(expr.getParent());
            // 将左边设置成原来的
            sqlBinaryOpExpr.setLeft(expr);
            SQLInListExpr sqlInListExpr;
            if (alias == null) {
                // 如果没有别名，则直接列名
                SQLIdentifierExpr sqlIdentifierExpr = new SQLIdentifierExpr(columnName);
                sqlInListExpr = new SQLInListExpr(sqlIdentifierExpr);
                sqlIdentifierExpr.setParent(sqlInListExpr);
            } else {
                // 如果有别名，则构造别名表达式
                SQLIdentifierExpr sqlIdentifierExpr = new SQLIdentifierExpr(alias);
                SQLPropertyExpr sqlPropertyExpr = new SQLPropertyExpr(sqlIdentifierExpr, columnName);
                sqlIdentifierExpr.setParent(sqlPropertyExpr);
                sqlInListExpr = new SQLInListExpr(sqlPropertyExpr);
            }
            // 构造 in 内包含的东西
            List<SQLExpr> refExprList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SQLCharExpr sqlCharExpr = new SQLCharExpr(list.get(i));
                sqlCharExpr.setParent(expr.getParent());
                refExprList.add(sqlCharExpr);
            }
            sqlInListExpr.setTargetList(refExprList);
            sqlBinaryOpExpr.setRight(sqlInListExpr);
//            sqlBinaryOpExpr.setRight(new SQLBinaryOpExpr(new SQLIdentifierExpr(columnNameTemp),operator,new SQLIdentifierExpr(factor)));
            // 返回新的条件
            return sqlBinaryOpExpr;
        }
        return expr;
    }

    public static void conditionInSub(SQLExpr where) {
        if (where instanceof SQLBinaryOpExpr) {
            //如果是 SQLBinaryOpExpr 则继续遍历
            conditionInSub(((SQLBinaryOpExpr) where).getLeft());
            conditionInSub(((SQLBinaryOpExpr) where).getRight());
        } else if (where instanceof SQLInSubQueryExpr) {
            // 如果是 query 则进行 query 遍历
            SQLSelectQuery query = ((SQLInSubQueryExpr) where).getSubQuery().getQuery();
            getTable(query);
        }
    }
}
