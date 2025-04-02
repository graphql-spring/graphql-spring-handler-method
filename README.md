# GraphQL Spring Handler Method

## Overview
`graphql-spring-handler-method` is a library that allows GraphQL to utilize HandlerMethod, similar to how MVC interceptors use HandlerMethod.

## Installation
To use this library, add the following dependency to your `pom.xml` (for Maven):

```xml
<dependency>
    <groupId>io.github.graphql-spring</groupId>
    <artifactId>graphql-spring-handler-method</artifactId>
    <version>1.0.0</version>
</dependency>
```

For Gradle:

```groovy
dependencies {
    implementation 'io.github.graphql-spring:graphql-spring-handler-method:1.0.0'
}
```

## Usage
### Configuration
Add the handler mapping bean to your Spring configuration.
```java
@Configuration
public class GraphqlInterceptorConfig {
    @Bean
    public GraphqlHandlerMapping graphqlHandlerMapping(ApplicationContext applicationContext) {
        return new GraphqlHandlerMapping(applicationContext);
    }
}
```

### Example
You can manage annotation-based logic in GraphQL interceptors like the example below.

##### TestAnnotation
```java
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestAnnotation {
}
```

##### TestController
```java
@Controller
public class TestController {
    @TestAnnotation
    @MutationMapping
    public String test(@Argument TestInput input) {
        return "test";
    }
}
```

##### TestInterceptor
```java
@Slf4j
@Configuration
@RequiredArgsConstructor
public class TestInterceptor implements WebGraphQlInterceptor {

    private final GraphqlHandlerMapping handlerMapping;

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        List<String> requestedFields = extractRequestedFields(request);

        for (String fieldName : requestedFields) {
            GraphqlHandlerMethod handlerMethod = handlerMapping.getHandlerMethod(fieldName);

            if (handlerMethod != null) {
                if (handlerMethod.hasMethodAnnotation(TestAnnotation.class)) {
                    log.warn("TestAnnotation Annotation Detected: " + fieldName);
                    //In the example, this log will be printed.
                }
                // Add Your Custom Annotation
            }
        }
        return chain.next(request);
    }
    
    private List<String> extractRequestedFields(WebGraphQlRequest request) {
        String documentString = request.getDocument();
        Document document = new Parser().parseDocument(documentString);

        return document.getDefinitions().stream()
                .filter(OperationDefinition.class::isInstance) 
                .map(OperationDefinition.class::cast)
                .filter(op -> op.getName() == null || op.getName().equals(request.getOperationName())) 
                .flatMap(op -> op.getSelectionSet().getSelections().stream()) 
                .filter(Field.class::isInstance) 
                .map(Field.class::cast)
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}
```
