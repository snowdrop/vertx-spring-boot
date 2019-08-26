## Vert.x AMQP example

This example demonstrates a publish-subscribe integration pattern implemented using Vert.x Spring Boot AMQP client.
This monolithic application is divided into two logical services which communicate via two queues created in an embedded Artemis broker.

## Application workflow 

Submitting message for processing
```
POST request > controller > processor client > AMQP broker > uppercase processor > AMQP broker > processor client
``` 

Receiving processed messages
```
GET request > controller > processor client > controller > GET response
```   

### Usage

Start the application in one terminal window.
```bash
java -jar target/vertx-spring-boot-sample-amqp.jar 
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

