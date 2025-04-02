package com.graphql.handler;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphqlHandlerMethod {
    private final Object bean;
    private final Method method;

    public GraphqlHandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public boolean hasMethodAnnotation(Class<? extends Annotation> annotationClass) {
        return AnnotationUtils.findAnnotation(method, annotationClass) != null;
    }

    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        return AnnotationUtils.findAnnotation(method, annotationClass);
    }

    public Set<Class<? extends Annotation>> getMethodAnnotations() {
        return Arrays.stream(method.getAnnotations())
                .map(Annotation::annotationType)
                .collect(Collectors.toSet());
    }

    public String getOperationName() {
        if (method.isAnnotationPresent(QueryMapping.class)) {
            return method.getAnnotation(QueryMapping.class).value();
        } else if (method.isAnnotationPresent(MutationMapping.class)) {
            return method.getAnnotation(MutationMapping.class).value();
        }
        return method.getName();
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }
}
