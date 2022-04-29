package com.github.data.authority.model;

/**
 * 配置信息
 * @author chenzhh
 */
public class Config {
    /**
     * 模块名称
     */
    private String module;
    /**
     * 解析包名，多个逗号分隔
     */
    private String packages;
    /**
     * 后缀名称，多个逗号分隔
     */
    private String suffix;
    /**
     * 包含方法名，多个逗号分隔
     */
    private String  methods;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }
}
