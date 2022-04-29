package com.github.data.authority.col.dao;

import cn.hutool.core.util.IdUtil;
import com.github.data.authority.col.model.DatalimitMetaCol;
import com.github.data.authority.col.model.DatalimitMetaColField;
import com.github.data.authority.db.MyDataSource;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chenzhh
 */
public class DatalimitColDao {

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

    /**
     * @param connection
     */
    public DatalimitColDao(Connection connection) {
        this.connection = connection;
    }

    public void saveOrUpdate(DatalimitMetaCol metaCol) {
         DatalimitMetaCol datalimitMetaCol = get(metaCol.getService());
        //修改
        if (Objects.nonNull(datalimitMetaCol) && datalimitMetaCol.getId() > 0) {
            //查询数据库
            List<DatalimitMetaColField> fields = get(datalimitMetaCol.getId());
            //删除
            Map<String,DatalimitMetaColField> result =  fields.stream().collect(Collectors.toMap(DatalimitMetaColField::getFieldName,field -> field));
            List<DatalimitMetaColField> exists = datalimitMetaCol.getFields();
            for(DatalimitMetaColField field : exists){
                DatalimitMetaColField datalimitMetaColField =  result.get(field.getName());
                if(Objects.nonNull(datalimitMetaColField)){
                    field.setId(datalimitMetaColField.getId());
                }else{
                    field.setId(IdUtil.getSnowflakeNextId());
                }
            }

        } else {
             save(metaCol);
        }

    }

    /**
     * @param metaId
     * @return
     */
    public List<DatalimitMetaColField> get(Long metaId) {
        List<DatalimitMetaColField> fields = Lists.newArrayList();
        try {
            StringBuffer sql = new StringBuffer("select * from datalimit_meta_col_field where meta_id=").append("?");
            statement = this.connection.prepareStatement(sql.toString());
            statement.setLong(1, metaId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                DatalimitMetaColField field = new DatalimitMetaColField();
                field.setId(resultSet.getLong("id"));
                field.setFieldName(resultSet.getString("field_name"));
                field.setName(resultSet.getString("name"));
                field.setMetaId(resultSet.getString("meta_id"));
                field.setCreate_by(resultSet.getString("create_by"));
                //field.setCreate_time(resultSet.getString("create_time"));
                // field.setUpdate_time(resultSet.getString("update_time"));
                field.setUpdate_by(resultSet.getString("update_by"));
                field.setDr(resultSet.getInt("dr"));
                field.setEnable(resultSet.getInt("enable"));
                field.setVersion(resultSet.getInt("version"));
                fields.add(field);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            MyDataSource.close(resultSet, statement, connection);
        }
        return fields;
    }

    /**
     * @param service
     * @return
     */
    public DatalimitMetaCol get(String service) {
        DatalimitMetaCol datalimitMetaCol = new DatalimitMetaCol();
        try {
            StringBuffer sql = new StringBuffer("select * from datalimit_meta_col where service=").append("?");
            statement = this.connection.prepareStatement(sql.toString());
            statement.setString(1, service);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                datalimitMetaCol.setId(resultSet.getLong("id"));
                datalimitMetaCol.setService(resultSet.getString("service"));
                datalimitMetaCol.setName(resultSet.getString("name"));
                datalimitMetaCol.setCreate_by(resultSet.getString("create_by"));
                //field.setCreate_time(resultSet.getString("create_time"));
                // field.setUpdate_time(resultSet.getString("update_time"));
                datalimitMetaCol.setUpdate_by(resultSet.getString("update_by"));
                datalimitMetaCol.setDr(resultSet.getInt("dr"));
                datalimitMetaCol.setEnable(resultSet.getInt("enable"));
                datalimitMetaCol.setVersion(resultSet.getInt("version"));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            MyDataSource.close(resultSet, statement, connection);
        }
        return datalimitMetaCol;
    }

    public void save(DatalimitMetaCol datalimitMetaCol) {
        try {

            StringBuffer sql = new StringBuffer("insert into datalimit_meta_col (id,service,name,create_time,create_by,update_time,update_by,version,dr,enable)");
            sql.append("values(?,?,?,?,?,?,?,?,?)");
            statement = this.connection.prepareStatement(sql.toString());
            statement.setLong(1, datalimitMetaCol.getId());
            statement.setString(2, datalimitMetaCol.getService());
            statement.setString(3, datalimitMetaCol.getName());
            //statement.setString(4, LocalDateTime.now());
            statement.setString(5,"");
            statement.setString(6,"");
            statement.setString(7, "");
            statement.setInt(8, 0);
            statement.setInt(9, 0);
            statement.setInt(10, 0);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            MyDataSource.close(resultSet, statement, connection);
        }

    }

}


