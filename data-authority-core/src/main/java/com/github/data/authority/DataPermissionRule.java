package com.github.data.authority;

/**
 * 数据权限规则
 * @author chenzhh
 */
public class DataPermissionRule {
    /**
     * 规则字段
     */
    private String  column;

    /**
     * 规则字段
     */
    private String  operator;
    /**
     * 规则值
     */
    private String  attribute;

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
}
