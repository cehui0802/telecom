package com.telecom.ecloudframework.base.core.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

public class PackageTools {
    private static final Log log = LogFactory.getLog(PackageTools.class);
    protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public PackageTools() {
    }

    public static Set<Method> findClassAnnotationMethods(String scanPackages, Class<? extends Annotation> annotation) {
        Set<String> clazzSet = findPackageClass(scanPackages);
        Set<Method> methods = new HashSet();
        Iterator var4 = clazzSet.iterator();

        while (var4.hasNext()) {
            String clazz = (String) var4.next();

            try {
                Set<Method> ms = findAnnotationMethods(clazz, annotation);
                if (ms != null) {
                    methods.addAll(ms);
                }
            } catch (ClassNotFoundException var7) {
                ;
            }
        }

        return methods;
    }

    public static Set<String> findPackageClass(String scanPackages) {
        if (StringUtils.isBlank(scanPackages)) {
            return Collections.EMPTY_SET;
        } else {
            Set<String> packages = checkPackage(scanPackages);
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            Set<String> clazzSet = new HashSet();
            Iterator var5 = packages.iterator();

            while (true) {
                String basePackage;
                do {
                    if (!var5.hasNext()) {
                        return clazzSet;
                    }

                    basePackage = (String) var5.next();
                } while (StringUtils.isBlank(basePackage));

                String packageSearchPath = "classpath*:" + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/" + "**/*.class";

                try {
                    Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                    Resource[] var9 = resources;
                    int var10 = resources.length;

                    for (int var11 = 0; var11 < var10; ++var11) {
                        Resource resource = var9[var11];
                        String clazz = loadClassName(metadataReaderFactory, resource);
                        clazzSet.add(clazz);
                    }
                } catch (Exception var14) {
                    log.error("获取包下面的类信息失败,package:" + basePackage, var14);
                }
            }
        }
    }

    private static Set<String> checkPackage(String scanPackages) {
        if (StringUtils.isBlank(scanPackages)) {
            return Collections.EMPTY_SET;
        } else {
            Set<String> packages = new HashSet();
            Collections.addAll(packages, scanPackages.split(","));
            String[] var2 = (String[]) packages.toArray(new String[packages.size()]);
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String pInArr = var2[var4];
                if (!StringUtils.isBlank(pInArr) && !pInArr.equals(".") && !pInArr.startsWith(".")) {
                    if (pInArr.endsWith(".")) {
                        pInArr = pInArr.substring(0, pInArr.length() - 1);
                    }

                    Iterator<String> packageIte = packages.iterator();
                    boolean needAdd = true;

                    while (packageIte.hasNext()) {
                        String pack = (String) packageIte.next();
                        if (pInArr.startsWith(pack + ".")) {
                            needAdd = false;
                        } else if (pack.startsWith(pInArr + ".")) {
                            packageIte.remove();
                        }
                    }

                    if (needAdd) {
                        packages.add(pInArr);
                    }
                }
            }

            return packages;
        }
    }

    private static String loadClassName(MetadataReaderFactory metadataReaderFactory, Resource resource) throws IOException {
        try {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (metadataReader != null) {
                    return metadataReader.getClassMetadata().getClassName();
                }
            }
        } catch (Exception var3) {
            log.error("根据resource获取类名称失败", var3);
        }

        return null;
    }

    public static Set<Method> findAnnotationMethods(String fullClassName, Class<? extends Annotation> anno) throws ClassNotFoundException {
        Set<Method> methodSet = new HashSet();
        Class<?> clz = Class.forName(fullClassName);
        Method[] methods = clz.getDeclaredMethods();
        Method[] var5 = methods;
        int var6 = methods.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            Method method = var5[var7];
            if (method.getModifiers() == 1) {
                Annotation annotation = method.getAnnotation(anno);
                if (annotation != null) {
                    methodSet.add(method);
                }
            }
        }

        return methodSet;
    }
}
