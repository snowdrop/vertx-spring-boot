## vertx-spring-boot-starter

Core Vert.x Spring Boot starter integrates Vert.x instance into Spring Boot infrastructure by mapping Spring Boot
properties to Vert.x specific options and auto-configuring Vert.x instance for dependency injection.

### Usage

Add the starter dependency to your `pom.xml`.
```xml
<dependency>
  <groupId>dev.snowdrop</groupId>
  <artifactId>vertx-spring-boot-starter</artifactId>
</dependency>
```

Use standard Spring Boot injection techniques whenever you need to use a Vert.x instance.
```java
@Component
public class MyComponent {
    private final Vertx vertx;
    
    public MyComponent(Vertx vertx) {
        this.vertx = vertx; 
    }
    
    public void executeLater(Runnable command) {
        vertx.setTimer(1000, id -> command.run());
    }
}
```

### Configuration

All options from `io.vertx.core.VertxOptions` are mapped to Spring properties under `vertx` prefix.
For example, to set a quorum size add the following property to your `application.properties` file.
```properties
vertx.quorum-size=2
``` 

For the full list of available properties see [VertxProperties.java](./src/main/java/dev/snowdrop/vertx/VertxProperties.java).
