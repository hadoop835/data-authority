package com.github.data.authority.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 查询数据解析
 *
 * @author chenzhh
 */
public class SelectParserVisitor extends MySqlASTVisitorAdapter {
    /**
     *
     */
    private Map<String, List<ParserColumnInfo>> parserColumnInfos;

    public SelectParserVisitor(Map<String, List<ParserColumnInfo>> parserColumnInfos) {
        this.parserColumnInfos = parserColumnInfos;
    }

    @Override
    public boolean visit(MySqlSelectQueryBlock mySqlSelectQueryBlock) {
        SQLTableSource tableSource = mySqlSelectQueryBlock.getFrom();
        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) tableSource;
            List<ParserTableInfo> tableInfos = getAllTableAlias(Lists.newArrayList(), sqlJoinTableSource);
            SQLExpr expr = mySqlSelectQueryBlock.getWhere();
            if (Objects.nonNull(expr)) {
                if (Objects.nonNull(tableInfos) && tableInfos.size() > 0) {
                    filterWhereColumnTable(tableInfos, expr);
                    SQLExpr sqlExpr = parserWhere(tableInfos, expr);
                    mySqlSelectQueryBlock.setWhere(sqlExpr);
                }
            } else {
                SQLExpr sqlExpr = parserWhere(tableInfos);
                mySqlSelectQueryBlock.setWhere(sqlExpr);
            }
        }else if (tableSource instanceof SQLSubqueryTableSource) {
            return isSQLJoinTableSource(tableSource);
        }else{
            return true;
        }
        return false;
    }

    private boolean isSQLJoinTableSource(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) sqlTableSource;
            SQLSelectQuery sqlSelectQuery = subqueryTableSource.getSelect().getQuery();
            if (sqlSelectQuery instanceof MySqlSelectQueryBlock) {
                MySqlSelectQueryBlock block = (MySqlSelectQueryBlock) sqlSelectQuery;
                SQLTableSource tableSource = block.getFrom();
                if (tableSource instanceof SQLSubqueryTableSource) {
                    isSQLJoinTableSource(tableSource);
                }else if (tableSource instanceof SQLJoinTableSource) {
                    SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) tableSource;
                    List<ParserTableInfo> tableInfos = getAllTableAlias(Lists.newArrayList(), sqlJoinTableSource);
                    SQLExpr expr = block.getWhere();
                    if (Objects.nonNull(expr)) {
                        if (Objects.nonNull(tableInfos) && tableInfos.size() > 0) {
                            filterWhereColumnTable(tableInfos, expr);
                            SQLExpr sqlExpr = parserWhere(tableInfos, expr);
                            block.setWhere(sqlExpr);
                        }
                    } else {
                        SQLExpr sqlExpr = parserWhere(tableInfos);
                        block.setWhere(sqlExpr);
                    }
                }else{
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 解析
     *
     * @param expr
     * @return
     */
    @Override
    public boolean visit(SQLBinaryOpExpr expr) {
        SQLObject parent = expr.getParent();
        //join的on条件
        if (parent instanceof SQLJoinTableSource) {
            SQLJoinTableSource sqlJoinTableSource = (SQLJoinTableSource) parent;
            List<ParserTableInfo> tableInfos = getAllTableAlias(Lists.newArrayList(), sqlJoinTableSource);
            if (Objects.nonNull(tableInfos) && tableInfos.size() > 0) {
                filterWhereColumnTable(tableInfos, expr);
                SQLExpr sqlExpr = parserWhere(tableInfos, expr);
                sqlJoinTableSource.setCondition(sqlExpr);
            }
            return false;
        }
        //where 条件
        else if (parent instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock mySqlSelectQueryBlock = (MySqlSelectQueryBlock) parent;
            List<ParserTableInfo> tableInfos = getTableAlias(Lists.newArrayList(), mySqlSelectQueryBlock.getFrom(), false);
            if (Objects.nonNull(tableInfos) && tableInfos.size() > 0) {
                filterWhereColumnTable(tableInfos, expr);
                SQLExpr sqlExpr = parserWhere(tableInfos, expr);
                mySqlSelectQueryBlock.setWhere(sqlExpr);
            }
            return false;
        }
        return false;
    }

    /**
     * 解析当前层级下所有sql中表的别名
     *
     * @param tableInfos  存储表信息，包括表名和别名
     * @param tableSource 解析表的，from
     */
    public List<ParserTableInfo> getAllTableAlias(List<ParserTableInfo> tableInfos, SQLTableSource tableSource) {
        return getTableAlias(tableInfos, tableSource, true);
    }

    /**
     * 解析当前层级下sql中表的别名
     *
     * @param tableInfos  存储表信息，包括表名和别名
     * @param tableSource 解析表的，from
     * @param isRight     右边是否加入where条件
     */
    private List<ParserTableInfo> getTableAlias(List<ParserTableInfo> tableInfos, SQLTableSource tableSource,
        boolean isRight) {
        if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) tableSource;
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) subqueryTableSource.getSelect().getQuery();
            getTableAlias(tableInfos, sqlSelectQueryBlock.getFrom(), isRight);
        }
        //内连接
        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinSource = (SQLJoinTableSource) tableSource;
            getTableAlias(tableInfos, joinSource.getLeft(), isRight);
            //join语句在where条件中是不需要加入右表的
            if (isRight) {
                getTableAlias(tableInfos, joinSource.getRight(), true);
            }
        }
        if (tableSource instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) tableSource;

            tableInfos.add(new ParserTableInfo(sqlExprTableSource.getAlias(), sqlExprTableSource.getTableName()));
        }
        return tableInfos;
    }

    /**
     * 过滤where 条件
     *
     * @param tableInfos
     * @param where
     */
    private void filterWhereColumnTable(List<ParserTableInfo> tableInfos, SQLExpr where) {
        if (Objects.isNull(where)) {
            return;
        }
        // 遍历左端
        SQLExpr sqlExpr = where;
        if (sqlExpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            sqlExpr = sqlBinaryOpExpr.getLeft();
            if (Objects.nonNull(sqlExpr)) {
                filterWhereColumnTable(tableInfos, sqlExpr);
            }
        }
        sqlExpr = where;
        //遍历右边
        if (sqlExpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            sqlExpr = sqlBinaryOpExpr.getRight();
            if (Objects.nonNull(sqlExpr)) {
                filterWhereColumnTable(tableInfos, sqlExpr);
            }
        }
        if (sqlExpr instanceof SQLInSubQueryExpr) {
            SQLInSubQueryExpr sqlInSubQueryExpr = (SQLInSubQueryExpr) where;
            filterWhereColumnTable(tableInfos, sqlInSubQueryExpr.getExpr());
        }
        //处理没有别名存在的字段
        if (where instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr sqlIdentifierExpr = (SQLIdentifierExpr) where;
            tableInfos.removeIf(item -> Objects.isNull(item.getAlias()) && Objects.equals(sqlIdentifierExpr.getName(), item.getColumnName()));
        }
        //处理包含别名的存在字段
        if (where instanceof SQLPropertyExpr) {
            SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) where;
            tableInfos.removeIf(item -> StringUtils.equalsIgnoreCase(sqlPropertyExpr.getOwner().toString(), returnAlias(item.getAlias(), item.getTableName()))
                && StringUtils.equalsIgnoreCase(sqlPropertyExpr.getName(), item.getColumnName()));
        }

    }

    /**
     * @param expr
     * @return
     */
    private SQLExpr parserWhere(List<ParserTableInfo> tableInfos, SQLExpr expr) {
        SQLExpr allOpExpr = expr;
        if (Objects.nonNull(tableInfos) && tableInfos.size() > 0) {
            for (ParserTableInfo tableInfo : tableInfos) {
                List<ParserColumnInfo> parserColumnInfoList = this.parserColumnInfos.get(tableInfo.getTableName());
                if (Objects.nonNull(parserColumnInfoList) && parserColumnInfoList.size() > 0) {
                    for (ParserColumnInfo parserColumnInfo : parserColumnInfoList) {
                        if ("0".equals(parserColumnInfo.getOperator())) {
                            SQLExpr sqlExpr = SQLUtils.toSQLExpr(parserColumnInfo.getColumn(), DbType.mysql);
                            StringJoiner sql = new StringJoiner("");
                            if (sqlExpr instanceof SQLBinaryOpExpr) {
                                SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
                                parserSQLExpr(tableInfo, sql, sqlBinaryOpExpr.getLeft(), sqlBinaryOpExpr.getOperator(), sqlBinaryOpExpr.getRight());
                                allOpExpr = SQLBinaryOpExpr.and(allOpExpr, SQLUtils.toSQLExpr(sql.toString(), DbType.mysql));
                            }
                            if (sqlExpr instanceof SQLInListExpr) {
                                sql.add(returnAlias(tableInfo.getAlias(), tableInfo.getTableName())).add(".").add(sqlExpr.toString());
                                allOpExpr = SQLBinaryOpExpr.and(allOpExpr, SQLUtils.toSQLExpr(sql.toString()));
                            }
                        } else {
                            StringJoiner sql = new StringJoiner("");
                            sql.add(returnAlias(tableInfo.getAlias(), tableInfo.getTableName()));
                            sql.add(".").add(parserColumnInfo.getColumn());
                            allOpExpr = SQLBinaryOpExpr.and(allOpExpr, SQLUtils.toSQLExpr(sql.toString(), DbType.mysql));
                        }
                    }
                }
            }
        }
        return allOpExpr;
    }

    /**
     * 返回别名
     *
     * @param alias
     * @param tableName
     * @return
     */
    private String returnAlias(String alias, String tableName) {
        if (Objects.nonNull(alias) && !"".equals(alias)) {
            return alias;
        }
        return tableName;
    }

    /**
     * 解析自定sql
     *
     * @param parserTableInfo
     * @param sql
     * @param left
     * @param operator
     * @param right
     */
    private void parserSQLExpr(ParserTableInfo parserTableInfo, StringJoiner sql, SQLExpr left,
        SQLBinaryOperator operator, SQLExpr right) {
        //
        if (left instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) left;
            parserSQLExpr(parserTableInfo, sql, sqlBinaryOpExpr.getLeft(), sqlBinaryOpExpr.getOperator(), sqlBinaryOpExpr.getRight());
        }
        if (right instanceof SQLBinaryOpExpr) {
            sql.add(" ").add(operator.getName()).add(" ");
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) right;
            parserSQLExpr(parserTableInfo, sql, sqlBinaryOpExpr.getLeft(), sqlBinaryOpExpr.getOperator(), sqlBinaryOpExpr.getRight());
        }
        if (left instanceof SQLIdentifierExpr) {
            sql.add(returnAlias(parserTableInfo.getAlias(), parserTableInfo.getTableName()));
            sql.add(".").add(left.toString()).add(" ").add(operator.getName()).add(" ").add(right.toString());
        }
        if (left instanceof SQLInListExpr) {
            sql.add(returnAlias(parserTableInfo.getAlias(), parserTableInfo.getTableName()));
            sql.add(".").add(left.toString());
        }
        if (right instanceof SQLIdentifierExpr) {
            sql.add(" ").add(operator.getName()).add(" ");
            sql.add(returnAlias(parserTableInfo.getAlias(), parserTableInfo.getTableName()));
            sql.add(".").add(left.toString()).add(" ").add(operator.getName()).add(" ").add(right.toString());
        }
        if (right instanceof SQLInListExpr) {
            sql.add(" ").add(operator.getName()).add(" ");
            sql.add(returnAlias(parserTableInfo.getAlias(), parserTableInfo.getTableName()));
            sql.add(".").add(right.toString());
        }
    }

    /**
     * join 查询，将查询条件追加到左表后
     *
     * @param tableInfos
     * @return
     */
    private SQLExpr parserWhere(List<ParserTableInfo> tableInfos) {
        SQLExpr allOpExpr = null;
        if (Objects.nonNull(tableInfos) && tableInfos.size() > 0) {
            for (ParserTableInfo tableInfo : tableInfos) {
                List<ParserColumnInfo> parserColumnInfoList = this.parserColumnInfos.get(tableInfo.getTableName());
                if (Objects.nonNull(parserColumnInfoList) && parserColumnInfoList.size() > 0) {
                    for (ParserColumnInfo parserColumnInfo : parserColumnInfoList) {
                        if ("0".equals(parserColumnInfo.getOperator())) {
                            SQLExpr sqlExpr = SQLUtils.toSQLExpr(parserColumnInfo.getColumn(), DbType.mysql);
                            StringJoiner sql = new StringJoiner("");
                            if (sqlExpr instanceof SQLBinaryOpExpr) {
                                SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
                                parserSQLExpr(tableInfo, sql, sqlBinaryOpExpr.getLeft(), sqlBinaryOpExpr.getOperator(), sqlBinaryOpExpr.getRight());
                                if (Objects.nonNull(allOpExpr)) {
                                    allOpExpr = SQLBinaryOpExpr.and(allOpExpr, SQLUtils.toSQLExpr(sql.toString(), DbType.mysql));
                                } else {
                                    allOpExpr = SQLUtils.toSQLExpr(sql.toString(), DbType.mysql);
                                }

                            }
                            if (sqlExpr instanceof SQLInListExpr) {
                                sql.add(returnAlias(tableInfo.getAlias(), tableInfo.getTableName())).add(".").add(sqlExpr.toString());
                                if (Objects.nonNull(allOpExpr)) {
                                    allOpExpr = SQLBinaryOpExpr.and(allOpExpr, SQLUtils.toSQLExpr(sql.toString()));
                                } else {
                                    allOpExpr = SQLUtils.toSQLExpr(sql.toString());
                                }
                            }
                        } else {
                            StringJoiner sql = new StringJoiner("");
                            sql.add(returnAlias(tableInfo.getAlias(), tableInfo.getTableName()));
                            sql.add(".").add(parserColumnInfo.getColumn());
                            if (Objects.nonNull(allOpExpr)) {
                                allOpExpr = SQLBinaryOpExpr.and(allOpExpr, SQLUtils.toSQLExpr(sql.toString(), DbType.mysql));
                            } else {
                                allOpExpr = SQLUtils.toSQLExpr(sql.toString(), DbType.mysql);
                            }
                        }
                    }
                }
            }
        }
        return allOpExpr;
    }
}