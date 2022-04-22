package com.github.data.authority;

/**
 * 参数字段
 *
 * @author chenzhh
 */
public class ParamFiled {
    /**
     * 字段名称
     */
    private String name;
    /**
     * 参数长度大于0 标识集合
     */
    private int len;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    @Override public String toString() {
        return "ParamFiled{" +
            "name='" + name + '\'' +
            ", len=" + len +
            '}';
    }
}
