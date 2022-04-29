package com.github.data.authority;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.data.authority.annotation.DatalimitColumn;
import com.github.data.authority.db.DBConfig;
import com.github.data.authority.model.Config;
import com.github.data.authority.util.ClassUtil;
import com.github.data.authority.util.ReflectionUtil;
import com.github.data.authority.util.SimpleTypeRegistryUtil;
import com.google.common.collect.Sets;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 行级权限
 *
 * @author chenzhh
 */
@Mojo(name = "datalimit-column",
    defaultPhase = LifecyclePhase.COMPILE,
    threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class DatalimitColumnMojo extends AbstractMojo {
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject mavenProject;
    /**
     *
     */
    @Parameter(defaultValue = "${project.compileClasspathElements}", required = true, readonly = true)
    private List<String> compileClasspathElements;
    /**
     *
     */
    private File outputDirectory;
    /**
     *
     */
    @Parameter(readonly = true)
    private DBConfig dbConfig;
    /**
     *
     */
    @Parameter(readonly = true)
    private Config config;
    /**
     * 依赖包地址
     */
    private Set<String> classpathElements = Sets.newHashSet();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<Artifact> artifacts = this.mavenProject.getArtifacts();
        for (Artifact artifact : artifacts) {
            classpathElements.add(artifact.getFile().getAbsoluteFile().toString());
        }
        Set<String> compileClasspath = compileClasspathElements.stream().filter(path -> {
            return new File(path).isDirectory();
        }).collect(Collectors.toSet());
        classpathElements.addAll(compileClasspath);

        List<Class<?>> classes = ClassUtil.getClassAll(this.classpathElements, config.getPackages(), getLog());
        //注解判断
        if (!CollectionUtils.isEmpty(classes)) {
            for (Class<?> clazz : classes) {
                Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
                if (Objects.nonNull(methods) && methods.length > 0) {
                    for (Method method : methods) {
                        //是否数据列级权限注解
                        if (method.isAnnotationPresent(DatalimitColumn.class)) {
                            StringJoiner methodName = new StringJoiner("");
                            methodName.add(clazz.getName()).add(".").add(method.getName());
                            DatalimitColumn datalimitColumn = method.getAnnotation(DatalimitColumn.class);
                            if (Objects.nonNull(datalimitColumn)) {
                                //方法名称
                                String annotation_method = datalimitColumn.method();
                                if (Objects.nonNull(annotation_method) && !"".contains(annotation_method.trim())) {
                                    methodName.add(clazz.getName()).add(".").add(method.getName()).add(".").add(annotation_method);
                                }
                                //获取对象字段
                                Set<String> result = Sets.newHashSet();
                                ReflectionUtil.field(null, result, datalimitColumn.clazz());
                                getLog().info(methodName.toString());
                                //保存数据库

                            }
                        }
                    }
                }
            }

        }

    }
}
