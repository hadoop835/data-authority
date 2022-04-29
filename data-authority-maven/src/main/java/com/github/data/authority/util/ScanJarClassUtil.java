package com.github.data.authority.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.maven.plugin.logging.Log;

/**
 * 获取jar包的class
 *
 * @author chenzhh
 */
public class ScanJarClassUtil {
    /**
     * @param compileClasspaths
     * @param classLoader
     * @param log
     * @return
     */
    public static Set<Class<?>> getScanJarClass(Set<String> compileClasspaths, ClassLoader classLoader,String packages , Log log) {
        Set<Class<?>> result = Sets.newHashSet();
        List<String> list = Lists.newArrayList(packages);
        for (String path : compileClasspaths) {
            try {
                for(String pack : list){
                    if (path.contains(pack) && path.endsWith(".jar")) {
                        JarFile jarFile = new JarFile(path);
                        Enumeration<JarEntry> enumeration = jarFile.entries();
                        while (enumeration.hasMoreElements()) {
                            String name = enumeration.nextElement().getName();
                            if (name.endsWith(".class")) {
                                String clazz = name.substring(0, name.lastIndexOf(".")).replace("/", ".");
                                result.add(classLoader.loadClass(clazz));
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                log.info("读取" + path + "jar包的class异常", e);
                e.printStackTrace();
            }
        }
        return result;
    }
}
