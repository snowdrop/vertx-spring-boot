## vertx-spring-boot-starter-mail

Vert.x Spring Boot mail starter adapts Vert.x mail client to be used with Reactor API.

### Usage

Add the starter dependency to your `pom.xml`.
```xml
<dependency>
  <groupId>dev.snowdrop</groupId>
  <artifactId>vertx-spring-boot-starter-mail</artifactId>
</dependency>
```

Use standard Spring Boot injection techniques whenever you need to send an email.
```java
@Component
public class MyComponent {
    private final EmailService emailService;
    
    public MyComponent(EmailService emailService) {
        this.emailService = emailService; 
    }
    
    public Mono<MailResult> sendMail(String from, String to, String subject, String text) {
        MailMessage message = new MailMessage(from, to, subject, text);
        return emailService.send(message);
    }
}
```

### Configuration

All options from `io.vertx.ext.mail.MailConfig` are mapped to Spring properties under `vertx.mail` prefix.
For example, to set a mail server hostname add the following property to your `application.properties` file.
```properties
vertx.mail.hostname=localhost
``` 

For the full list of available properties see [MailProperties.java](./src/main/java/dev/snowdrop/vertx/mail/MailProperties.java).
