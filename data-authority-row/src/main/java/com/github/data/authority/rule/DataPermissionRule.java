package com.github.data.authority.rule;

/**
 * 数据权限规则
 *
 * @author chenzhh
 */
public class DataPermissionRule {
    /**
     * 表名
     */
    private String table;
    /**
     * 规则字段
     */
    private String column;

    /**
     * 规则字段
     */
    private String operator;
    /**
     * 规则值
     */
    private String attribute;

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

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override public String toString() {
        return "DataPermissionRule{" +
            "table='" + table + '\'' +
            ", column='" + column + '\'' +
            ", operator='" + operator + '\'' +
            ", attribute='" + attribute + '\'' +
            '}';
    }
}
