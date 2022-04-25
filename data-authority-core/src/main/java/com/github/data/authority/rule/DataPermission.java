package com.github.data.authority.rule;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

/**
 * 权限信息
 *
 * @author chenzhh
 */
public class DataPermission {
    /**
     *设置数据权限
     */
    private Map<String, List<DataPermissionRule>> dataPermissionRule = Maps.newConcurrentMap();


    public void setDataPermissionRule(
        Map<String, List<DataPermissionRule>> dataPermissionRule) {
        this.dataPermissionRule = dataPermissionRule;
    }

    /**
     * 根据mapper查询权限
     * @param mapperId
     * @return
     */
    public List<DataPermissionRule> getDataPermissionRuleByMapperId(String mapperId){
           if(dataPermissionRule.containsKey(mapperId)){
               return dataPermissionRule.get(mapperId);
           }
           return Lists.newArrayList();
    }

    @Override public String toString() {
        return "DataPermission{" +
            "dataPermissionRule=" + dataPermissionRule +
            '}';
    }
}
