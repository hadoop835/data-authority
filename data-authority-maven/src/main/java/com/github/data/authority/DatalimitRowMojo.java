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

import cn.hutool.core.util.IdUtil;
import com.github.data.authority.db.DBConfig;
import com.github.data.authority.model.Config;
import com.github.data.authority.row.DatalimitMetaRow;
import com.github.data.authority.row.DatalimitMetaRowField;
import com.github.data.authority.util.ClassUtil;
import com.github.data.authority.util.ReflectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Table;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.springframework.util.ReflectionUtils;

/**
 * @author chenzhh
 */
@Mojo(name = "datalimit-row",
    defaultPhase = LifecyclePhase.COMPILE,
    threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class DatalimitRowMojo extends AbstractMojo {
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject mavenProject;
    /**
     *
     */
    @Parameter(defaultValue = "${project.compileClasspathElements}", required = true, readonly = true)
    private List<String> compileClasspathElements;

    private File outputDirectory;

    @Parameter(readonly = true)
    private DBConfig dbConfig;

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
        List<DatalimitMetaRow> datalimitMetaRows = Lists.newArrayList();
        for (Class<?> clazz : classes) {
            List<String> suffixs = Lists.newArrayList(config.getSuffix());
            for (String suffix : suffixs) {
                if (clazz.getSimpleName().endsWith(suffix)) {
                    DatalimitMetaRow datalimitMetaRow = new DatalimitMetaRow();
                    datalimitMetaRow.setId(IdUtil.getSnowflakeNextId());
                    datalimitMetaRow.setName(clazz.getSimpleName());
                    datalimitMetaRow.setCreate_by("system");
                    datalimitMetaRow.setUpdate_by("system");
                    datalimitMetaRow.setModule(config.getModule());
                    datalimitMetaRow.setDr(0);
                    datalimitMetaRow.setEnable(0);
                    datalimitMetaRow.setVersion(1);
                    datalimitMetaRow.setCreate_time(LocalDateTime.now());
                    datalimitMetaRow.setUpdate_time(LocalDateTime.now());
                    Type parameterizedType = clazz.getGenericInterfaces()[0];
                    if (parameterizedType instanceof ParameterizedType) {
                        ParameterizedType type = (ParameterizedType) parameterizedType;
                        Type actualTypeArguments = type.getActualTypeArguments()[0];
                        if (actualTypeArguments instanceof Class) {
                            Class<?> clazzParameterizedType = (Class<?>) actualTypeArguments;
                            if (clazzParameterizedType.isAnnotationPresent(Table.class)) {
                                Table table = clazzParameterizedType.getAnnotation(Table.class);
                                List<Field> fields = ReflectionUtil.fieldList(clazzParameterizedType);
                                List<DatalimitMetaRowField> rowFields = Lists.newArrayList();
                                for (Field field : fields) {
                                    if (field.isAnnotationPresent(Column.class)) {
                                        Column column = field.getAnnotation(Column.class);
                                        if("extension".equalsIgnoreCase(field.getName())){
                                            continue;
                                        }
                                        DatalimitMetaRowField datalimitMetaRowField = new DatalimitMetaRowField();
                                        datalimitMetaRowField.setTableName(table.name());
                                        datalimitMetaRowField.setId(IdUtil.getSnowflakeNextId());
                                        datalimitMetaRowField.setFieldName(field.getName());
                                        datalimitMetaRowField.setName(field.getName());
                                        datalimitMetaRowField.setMetaId(datalimitMetaRow.getId() + "");
                                        datalimitMetaRowField.setTableColumn(column.name());
                                        datalimitMetaRowField.setVersion(datalimitMetaRow.getVersion());
                                        datalimitMetaRowField.setCreate_by(datalimitMetaRow.getCreate_by());
                                        datalimitMetaRowField.setUpdate_by(datalimitMetaRow.getUpdate_by());
                                        datalimitMetaRowField.setCreate_time(datalimitMetaRow.getCreate_time());
                                        datalimitMetaRowField.setUpdate_time(datalimitMetaRow.getUpdate_time());
                                        datalimitMetaRowField.setDr(datalimitMetaRow.getDr());
                                        datalimitMetaRowField.setEnable(datalimitMetaRow.getEnable());
                                        rowFields.add(datalimitMetaRowField);
                                    }
                                }
                                datalimitMetaRow.setFields(rowFields);
                            }

                        }
                    }
                    Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
                    for (Method method : methods) {
                        List<String> _methods = Lists.newArrayList(config.getMethods());
                        for(String _method : _methods){
                            if(method.getName().contains(_method)){
                                StringJoiner methodName = new StringJoiner("");
                                methodName.add(clazz.getName());
                                methodName.add(".").add(method.getName());
                                datalimitMetaRow.setMapper(methodName.toString());
                                datalimitMetaRows.add(datalimitMetaRow);
                            }
                        }
                    }
                }
            }
        }
        //保持数据库
        getLog().info(datalimitMetaRows.toString());
    }
}
