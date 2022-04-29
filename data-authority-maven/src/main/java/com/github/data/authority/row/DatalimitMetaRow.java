package com.github.data.authority.row;

import com.github.data.authority.col.model.DatalimitMetaColField;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author chenzhh
 */
public class DatalimitMetaRow {
    private Long id;
    private String module;
    private String mapper;
    private String name;
    private LocalDateTime create_time;
    private String create_by;
    private LocalDateTime update_time;
    private String update_by;
    private Integer version;
    private Integer dr;
    private Integer enable;
    private List<DatalimitMetaRowField> fields;

    @Override public String toString() {
        return "DatalimitMetaRow{" +
            "id=" + id +
            ", module='" + module + '\'' +
            ", mapper='" + mapper + '\'' +
            ", name='" + name + '\'' +
            ", create_time=" + create_time +
            ", create_by='" + create_by + '\'' +
            ", update_time=" + update_time +
            ", update_by='" + update_by + '\'' +
            ", version=" + version +
            ", dr=" + dr +
            ", enable=" + enable +
            ", fields=" + fields +
            '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getMapper() {
        return mapper;
    }

    public void setMapper(String mapper) {
        this.mapper = mapper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<DatalimitMetaRowField> getFields() {
        return fields;
    }

    public void setFields(List<DatalimitMetaRowField> fields) {
        this.fields = fields;
    }
}
