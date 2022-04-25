package com.github.data.authority.rule;

import java.util.Objects;

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
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }



    public void setValue(Object value) {
        this.value = value;
    }
    @Override public String toString() {
        return "ParamFiled{" +
            "name='" + name + '\'' +
            ", value=" + value +
            '}';
    }

}
