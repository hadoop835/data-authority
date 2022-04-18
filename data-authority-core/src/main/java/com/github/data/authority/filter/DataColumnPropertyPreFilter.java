package com.github.data.authority.filter;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.PropertyPreFilter;
import com.google.common.collect.Sets;
import java.util.Set;

/**
 * 改造fastjson源码
 * @author chenzhh
 */
public class DataColumnPropertyPreFilter implements PropertyPreFilter {
    private final Class<?> clazz;
    private final Set<String> includes = Sets.newHashSet();
    private final Set<String> excludes = Sets.newHashSet();
    private int maxLevel = 0;

    public DataColumnPropertyPreFilter(String... properties) {
        this(null, properties);
    }

    public DataColumnPropertyPreFilter(Class<?> clazz, String... properties) {
        super();
        this.clazz = clazz;
        for (String item : properties) {
            if (item != null) {
                this.includes.add(item);
            }
        }
    }

    /**
     * @since 1.2.9
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * @since 1.2.9
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }


    @Override public boolean process(JSONWriter writer,  Object source, String name) {
        if (source == null) {
            return true;
        }

        if (clazz != null && !clazz.isInstance(source)) {
            return true;
        }

        if (this.excludes.contains(name)) {
            return false;
        }
        JSONWriter.Context context =  writer.getContext();
        String  prefix = context.toString();
        prefix = prefix + "." + name;
        //去除开头的$.
        prefix = prefix.replaceAll("\\[\\d+\\]", "");
        prefix = prefix.replace("$.", "");
        if (this.excludes.contains(prefix)) {
            return false;
        }
        return includes.size() == 0 || includes.contains(name);

    }
}
