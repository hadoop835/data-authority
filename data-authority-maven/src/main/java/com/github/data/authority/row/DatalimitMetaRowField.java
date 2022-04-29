package com.github.data.authority.row;

import java.time.LocalDateTime;

/**
 * @author chenzhh
 */
public class DatalimitMetaRowField {
    private Long id;
    private String tableColumn;
    private String tableName;
    private String fieldName;
    private String name;
    private String metaId;
    private LocalDateTime create_time;
    private String create_by;
    private LocalDateTime update_time;
    private String update_by;
    private Integer version;
    private Integer dr;
    private Integer enable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableColumn() {
        return tableColumn;
    }

    public void setTableColumn(String tableColumn) {
        this.tableColumn = tableColumn;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public LocalDateTime getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(LocalDateTime update_time) {
        this.update_time = update_time;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    @Override public String toString() {
        return "DatalimitMetaRowField{" +
            "id=" + id +
            ", tableColumn='" + tableColumn + '\'' +
            ", tableName='" + tableName + '\'' +
            ", fieldName='" + fieldName + '\'' +
            ", name='" + name + '\'' +
            ", metaId='" + metaId + '\'' +
            ", create_time=" + create_time +
            ", create_by='" + create_by + '\'' +
            ", update_time=" + update_time +
            ", update_by='" + update_by + '\'' +
            ", version=" + version +
            ", dr=" + dr +
            ", enable=" + enable +
            '}';
    }
}
