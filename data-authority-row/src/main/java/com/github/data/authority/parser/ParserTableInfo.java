package com.github.data.authority.parser;

/**
 * 表信息
 * @author chenzhh
 */
public class ParserTableInfo {
    /**
     * 表的别名
     */
    private  String alias;
    /**
     * 表名
     */
    private String  tableName;
    /**
     * 列名
     */
    private String  columnName;

    public ParserTableInfo(String alias, String tableName) {
        this.alias = alias;
        this.tableName = tableName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
