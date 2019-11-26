## Vert.x AMQP TLS example

This example demonstrates a simple application with an AMQP publisher and a subscriber which use TLS to secure
communications with an AMQP broker.

## Application workflow 

Log message
```
POST request > controller > logger > AMQP broker (via TLS) > log
``` 

Receiving logged messages
```
GET request > controller > log > controller > GET response
```   

### Usage

Build the application.
```bash
mvn clean package
```

Start the application in one terminal window.
```bash
java -jar target/vertx-spring-boot-sample-amqp-tls.jar 
```

And access it in another.

First submit a couple of messages for processing.
```bash
echo "Hello, World" | http POST :8080
echo "Hello again" | http POST :8080 
```

Then check the result.
```bash
http :8080
```
You should get something like the following.
```bash
HTTP/1.1 200 OK
Content-Type: text/event-stream;charset=UTF-8
transfer-encoding: chunked

data:HELLO, WORLD

data:HELLO AGAIN
```

### TLS configuration

AMQP client TLS configuration is very straightforward and is done via `application.(yml|properties)` file. Here's the
one used in this application.

```yaml
vertx:
  amqp:
    # Tell AMQP client to use TLS when communicating with a broker
    ssl: true
    jksKeyStore:
      # Activate JKS key store (PFX is also available and is configured in the same way)
      enabled: true
      # Point the client to the key store file
      path: tls/client-keystore.jks
      # Set a key store password
      password: wibble 
    jksTrustStore:
      # Activate JKS trust store (PFX is also available and is configured in the same way)
      enabled: true
      # Point AMQP to the trust store file
      path: tls/client-truststore.jks
      # Set a trust store password
      password: wibble
``` 
