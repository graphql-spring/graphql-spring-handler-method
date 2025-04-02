package com.graphql.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GraphqlHandlerMapping {
    private final Map<String, GraphqlHandlerMethod> handlerMethods = new HashMap<>();

    public GraphqlHandlerMapping(ApplicationContext applicationContext) {
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);

        for (Object controller : controllers.values()) {
            for (Method method : controller.getClass().getDeclaredMethods()) {
                GraphqlHandlerMethod handlerMethod = new GraphqlHandlerMethod(controller, method);
                handlerMethods.put(handlerMethod.getOperationName(), handlerMethod);
            }
        }
    }

    public GraphqlHandlerMethod getHandlerMethod(String operationName) {
        return handlerMethods.get(operationName);
    }
}
