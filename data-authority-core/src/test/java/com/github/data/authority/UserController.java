package com.github.data.authority;

import java.util.List;

/**
 *
 */
public class UserController extends BaseController{

    private UserService userService=new UserService();
    static  IDataColumnService dataColumnService = new DataColumnServiceImpl();
    /**
     * @param dataColumnService
     */
    public UserController(IDataColumnService dataColumnService) {
        super(dataColumnService);

    }

    public List<UserDTO>   userList(UserDTO userDTO){

        List<UserDTO> list =  dataColumnFilter(UserDTO.class,userService,userService->{
          List<UserDTO> result =userService.getList(userDTO.getName());
          return result;
       });
      System.out.println(list.toString());
     return list;
    }

    public static void main(String[] args) {
        new UserController(dataColumnService).userList(new UserDTO());
    }

}
