package com.github.data.authority.parser;

/**
 * 列信息
 * @author chenzhh
 */
public class ParserColumnInfo {
    /**
     * 表名
     */
    private String table;
    /**
     * 规则字段
     */
    private String  column;

    /**
     * 规则字段
     */
    private String  operator;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


}
