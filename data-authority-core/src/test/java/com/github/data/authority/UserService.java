package com.github.data.authority;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public List<UserDTO>  getList(String userId){
        List<UserDTO> list = new ArrayList<UserDTO>();
        UserDTO dto1 = new UserDTO();
        dto1.setName("111");
        dto1.setSex("男");
        AdminDto adminDto = new AdminDto();
        adminDto.setName("测试");
        dto1.setAdminDto(adminDto);
        List<AdminDto> adminDtos = Lists.newArrayList();
        AdminDto adminDto1 = new AdminDto();
        adminDto1.setName("测试");

        adminDtos.add(adminDto1);
        dto1.setAdminDtos(adminDtos);
        list.add(dto1);

        return list;
    }
}
