package com.github.data.authority;

import com.github.data.authority.annotation.FieldAuthority;

public class AdminDto {
    @FieldAuthority(field = "name",note = "名称")
    private String name;
    @FieldAuthority(field = "sex",note = "性别")
    private int  sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
