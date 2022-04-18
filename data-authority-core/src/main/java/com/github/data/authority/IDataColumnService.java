package com.github.data.authority;

import java.util.List;

/**
 * 查询数据列级权限接口
 * @author chenzhh
 */
public interface IDataColumnService {
    /**
     * 根据用户查询列权限
     * @param code 模块编码
     * @return
     */
    List<String>  getDataColumnByUserId(String code);
}
