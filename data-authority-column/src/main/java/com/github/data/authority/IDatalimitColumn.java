package com.github.data.authority;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 查询用户的列级权限
 *
 * @author chenzhh
 */
public interface IDatalimitColumn {
    /**
     * 根据方法名称查询列级权限
     *
     * @param method
     * @return
     */
    default List<String> getDatalimitColumnByMethod(String method) {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Objects.requireNonNull(httpServletRequest, "httpServletRequest不能为空");
        String applicationId = httpServletRequest.getHeader("yes.req.applicationId");
        Objects.requireNonNull(applicationId, "应用Id不能为空");
        String tenantId = httpServletRequest.getHeader("yes.req.tenantId");
        Objects.requireNonNull(tenantId, "租户Id不能为空");
        String userId = httpServletRequest.getHeader("yes.req.userId");
        Objects.requireNonNull(userId, "用户Id不能为空");
        return getDatalimitColumnByUserId(tenantId,applicationId,userId,method);
    }
    /**
     * 查询用户字段级权限
     *
     * @param tenantId      租户
     * @param applicationId 应用
     * @param userId        用户
     * @param method        方法
     * @return
     */
    List<String> getDatalimitColumnByUserId(String tenantId, String applicationId, String userId, String method);

}
