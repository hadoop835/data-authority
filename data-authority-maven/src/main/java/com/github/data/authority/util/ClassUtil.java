package com.github.data.authority.util;

import com.google.common.collect.Lists;
import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.maven.plugin.logging.Log;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/**
 * 处理calss
 *
 * @author chenzhh
 */
public final class ClassUtil {
    /**
     *
     * @param classpathElements
     * @param log
     * @return
     */
    public static List<Class<?>> getClassAll(Set<String> classpathElements,String packages, Log log){
        URLClassLoader urlClassLoader = ClassLoaderUtil.addURLClassLoader(classpathElements, log);
        Objects.requireNonNull(urlClassLoader,"类加载器失败");
        return getClassAll(classpathElements,urlClassLoader,packages,log);
    }
    /**
     * @param classpathElements
     * @param classLoader
     * @param log
     * @return
     */
    private static List<Class<?>> getClassAll(Set<String> classpathElements,
        ClassLoader classLoader,String packages, Log log) {
        List<Class<?>> classes = classpathElements.stream().filter(classpath -> {
            return new File(classpath).isDirectory();
        }).flatMap((classpath) -> {
            return getClass(new File(classpath), classLoader, log).stream();
        }).collect(Collectors.toList());
        log.info(classes.toString());
        Set<Class<?>> classSet = ScanJarClassUtil.getScanJarClass(classpathElements, classLoader,packages, log);
        if (!CollectionUtils.isEmpty(classSet)) {
            classes.addAll(classSet);
        }
        return classes;
    }

    /**
     * @param file
     * @param classLoader
     * @param log
     * @return
     */
    private static List<Class<?>> getClass(File file, ClassLoader classLoader, Log log) {
        List<Class<?>> clazzs = Lists.newArrayList();
        //递归
        if (Objects.nonNull(file) && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.nonNull(files) && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        getClass(f, classLoader, log);
                    } else if (f.isFile() && f.getName().endsWith(".class")) {
                        String absolutePath = f.getAbsolutePath();
                        absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + 8, absolutePath.length() - ".class".length());
                        absolutePath = absolutePath.replaceAll("[/|\\\\]", ".");
                        log.info(absolutePath);
                        try {
                            clazzs.add(ClassUtils.forName(absolutePath, classLoader));
                        } catch (ClassNotFoundException e) {
                            log.info("加载类异常" + f.getAbsolutePath(), e);
                            throw new RuntimeException("加载类异常", e);
                        }
                    }
                }
            }
        }
        return clazzs;
    }
}
