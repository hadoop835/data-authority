package com.github.data.authority.db;

/**
 * 数据库连接信息
 *
 * @author chenzhh
 */
public class DBConfig {
    /**
     * 驱动
     */
    private String driverClass;
    /**
     * 地址
     */
    private String url;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    private String useSSL;

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(String useSSL) {
        this.useSSL = useSSL;
    }

    @Override public String toString() {
        return "DBConfig{" +
            "driverClass='" + driverClass + '\'' +
            ", url='" + url + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", useSSL='" + useSSL + '\'' +
            '}';
    }
}
