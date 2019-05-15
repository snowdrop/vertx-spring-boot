## vertx-spring-boot-starter-actuator

This is an extension of the Vert.x HTTP Spring Boot starter to handle actuator in a child context.
While you can use `vertx-spring-boot-starter-http` with `spring-boot-starter-actuator`, it will only function
correctly in the same server (i.e. actuator has to use the same address and port as the application). 
If you need to separate them, you should add this starter to your `pom.xml`
```xml
<dependency>
  <groupId>dev.snowdrop</groupId>
  <artifactId>vertx-spring-boot-starter-actuator</artifactId>
</dependency>
```

Once the dependency is added you can safely expose actuator under a different port.
```properties
management.server.port=8081
```
