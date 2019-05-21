## vertx-spring-boot-starter-http

Vert.x integration into WebFlux framework implements SPIs to use Vert.x server and clients to handle HTTP connections
of a WebFlux application.

### Usage

Add the starter dependency to your `pom.xml`.
```xml
<dependency>
  <groupId>dev.snowdrop</groupId>
  <artifactId>vertx-spring-boot-starter-http</artifactId>
</dependency>
```

#### HTTP Controllers

Write WebFlux controllers in a standard way as documented in [WebFlux reference](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html).
Vert.x HTTP starter will handle connections under the covers.

Example annotated controller.
```java
@RestController
public class MyController {
    @GetMapping
    public Mono<String> hello() { 
        return Mono.just("Hello, World!");
    }
}
```

Example functional endpoint.
```java
@Configuration
public class MyConfiguration {
    @Bean
    public RouterFunction<ServerResponse> helloRouter() {
        return route()
            .GET("/", request -> ok().syncBody("Hello, World!"))
            .build();
    }
}
```

#### HTTP Client

To use WebFlux WebClient, inject its builder with preconfigured Vert.x connector and build a client yourself.
 
```java
@Component
public class MyComponent {
    private final WebClient client;
    
    public MyComponent(WebClient.Builder clientBuilder) {
        this.client = clientBuilder
            .baseUrl("http://example.com")
            .build();
    }
    
    public Flux<String> getData() {
        return client
            .get()
            .retrieve()
            .bodyToFlux(String.class);
    }
}
```

Or inject a Vert.x HTTP connector and use it with a default WebClient builder.

```java
@Component
public class MyComponent {
    private final WebClient client;
    
    public MyComponent(VertxClientHttpConnector connector) {
        this.client = WebClient.builder()
            .clientConnector(connector)
            .baseUrl("http://example.com")
            .build();
    }
    
    public Flux<String> getData() {
        return client
            .get()
            .retrieve()
            .bodyToFlux(String.class);
    }
}
```

#### HTTP Test Client

To use WebFlux WebTestClient, inject a Vert.x connector and use it with your test client instance.

> Do not use injected test client, because it is dependent on Reactor Netty client.

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyTests {
    @LocalServerPort
    private String port;
    
    @Autowired
    private VertxClientHttpConnector connector;
    
    private WebTestClient client;
    
    @Before
    public void setUp() {
        client = WebTestClient.bindToServer(connector)
            .baseUrl("http://localhost:" + port)
            .build();
    }
    
    @Test
    public void myTest() {
        client
            .get()
            .exchange()
            .expectBody(String.class)
            .isEqualTo("Hello, World!");
    }
}
```

#### WebSocket Handlers

Write WebFlux WebSocket handlers in a standard way as documented in [WebFlux reference](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html).
Vert.x HTTP starter will handle connections under the covers.

> You do not need to create WebSocketHandlerAdapter bean as shown in a WebFlux reference, Vert.x HTTP starter will do that for you. 

```java
@Configuration
public class MyConfiguration {
    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/echo", session -> session.send(session.receive()));
        
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1);
        
        return mapping;
    }
}
```

#### WebSocket Client

To use WebFlux WebSocketClient, simply inject it into your component.

```java
@Component
public class MyComponent {
    private final WebSocketClient client;
    
    public MyComponent(WebSocketClient client) {
        this.client = client;
    }
    
    public Mono<Void> getData(URI uri, Consumer<WebSocketMessage> consumer) {
        return client.execute(uri, session -> session
            .receive()
            .doOnNext(consumer)
            .then()
        );
    }
}
```

### Configuration

HTTP configuration is split into server and client and maps almost all options from
`io.vertx.core.http.HttpServerOptions` and `io.vertx.core.http.HttpClientOptions` to Spring Boot properties.

#### HTTP Server Properties

There are three ways to configure HTTP and WebSocket servers (highest precedence first):
1. `HttpServerOptionsCustomizer` beans provided by your application.
2. Spring Boot properties with `server` prefix. Only a subset of them is used.
3. Spring Boot properties with `vertx.http.server` prefix.

To implement your own customizer, provide a bean in a configuration class.
```java
@Configuration
public class MyConfiguration {
    @Bean
    public HttpServerOptionsCustomizer myCustomizer() {
        return options -> options.setSsl(true);
    }
}
```

To configure a server using standard Spring Boot server properties use the following ones.
```properties
server.address
server.port
server.compression.enabled
server.ssl.enabled
server.ssl.key-store-type
server.ssl.key-store
server.ssl.key-store-password
server.ssl.trust-store-type
server.ssl.trust-store
server.ssl.trust-store-password
server.ssl.client-auth
server.ssl.enabled-protocols
server.ssl.ciphers
```

For a full list of options available under `vertx.http.server` prefix see
[HttpServerProperties.java](./src/main/java/dev/snowdrop/vertx/http/server/properties/HttpServerProperties.java)

#### HTTP Client Properties

There are two ways to configure HTTP and WebSocket clients (highest precedence first):
1. `HttpClientOptionsCustomizer` beans provided by your application.
2. Spring Boot properties with `vertx.http.client` prefix.

To implement your own customizer, provide a bean in a configuration class.
```java
@Configuration
public class MyConfiguration {
    @Bean
    public HttpClientOptionsCustomizer myCustomizer() {
        return options -> options.setSsl(true);
    }
}
```

For a full list of options available under `vertx.http.client` prefix see
[HttpClientProperties.java](./src/main/java/dev/snowdrop/vertx/http/client/properties/HttpClientProperties.java)
