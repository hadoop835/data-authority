package com.github.data.authority.util;

import com.google.common.collect.Sets;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Set;
import org.apache.maven.plugin.logging.Log;
import org.springframework.util.ClassUtils;

/**
 * @author chenzhh
 */
public class ClassLoaderUtil {
    /**
     * @param compileClasspaths
     * @param log
     * @return
     */
    public static URLClassLoader addURLClassLoader(Set<String> compileClasspaths, Log log) {
        Set<URL> urls = Sets.newHashSet();
        try {
            for (String classpath : compileClasspaths) {
                urls.add(new File(classpath).toURI().toURL());
            }
        } catch (MalformedURLException e) {
            log.warn("加载url异常", e);
            e.printStackTrace();
        }
        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[] {}), ClassUtils.getDefaultClassLoader());
        Objects.requireNonNull(classLoader, "获取类加载器失败");
        return classLoader;
    }
}
