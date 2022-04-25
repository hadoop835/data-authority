package com.github.data.authority;

import com.github.data.authority.annotation.ColumnAuthority;
import com.github.data.authority.annotation.FieldAuthority;
import com.github.data.authority.annotation.OneToManyAuthority;
import com.github.data.authority.annotation.OneToOneAuthority;
import java.util.List;

/**
 *
 */
@ColumnAuthority(code = "111")
public class UserDTO {
    @FieldAuthority(field = "name",note = "名称")
    private  String name;
    @FieldAuthority(field = "sex",note = "性别")
    private  String  sex;
    @FieldAuthority(field = "age",note = "年龄")
    private  int  age;
    @FieldAuthority(field = "adminDto",note = "年龄",clazz = AdminDto.class)
    private AdminDto adminDto;
    @FieldAuthority(field = "adminDtos",note = "年龄",clazz = AdminDto.class)
    private List<AdminDto> adminDtos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public AdminDto getAdminDto() {
        return adminDto;
    }

    public void setAdminDto(AdminDto adminDto) {
        this.adminDto = adminDto;
    }

    public List<AdminDto> getAdminDtos() {
        return adminDtos;
    }

    public void setAdminDtos(List<AdminDto> adminDtos) {
        this.adminDtos = adminDtos;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
