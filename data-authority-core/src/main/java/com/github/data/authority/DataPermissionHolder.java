package com.github.data.authority;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 当前登录用户信息
 * @author chenzhh
 */
public final class DataPermissionHolder {
    /**
     * 获取当前用户信息
     */
    private static  final TransmittableThreadLocal<DataPermission> currentUser = new TransmittableThreadLocal<DataPermission>();


    public final static void  setCurrentUser(DataPermission dataPermission){
        currentUser.set(dataPermission);
    }

    public final static   DataPermission getCurrentUser(){
        return currentUser.get();
    }

}
