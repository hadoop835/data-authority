package com.github.data.authority.db;

import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 数据库连接
 *
 * @author chenzhh
 */
public final class MyDataSource {
    /**
     * @param config
     * @return
     */
    public static Connection getConnection(DBConfig config) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(config.getDriverClass()+"&"+config.getUseSSL());
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUsername());
        dataSource.setPassword(config.getPassword());
        try {
            return dataSource.getConnection().getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException("数据库连接异常", ex);
        }

    }

    public static  void close(ResultSet resultSet, PreparedStatement statement, Connection conn) {
        try {
            if (Objects.nonNull(resultSet)) {
                resultSet.close();
            }
            if (Objects.nonNull(statement)) {
                statement.execute();
            }
            if (Objects.nonNull(conn)) {
                conn.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
