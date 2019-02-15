## Chunked response example

This example demonstrates a scenario where chunked response is returned by a server. The normal workflow is the following:

1. User requests X number of entries via UI
2. Reactive service forwards request to httpbin
3. httpbin returns a stream of entries and backend service forwards them to the UI one by one
4. In addition backend service also batches the entities the the groups of 10 and sends these batches by email
5. UI is updated every time a chunk of entires arrives

This application requires a running SMTP server. You can provide the server credentials in an application.properties file or just pass them on a start up as follows:

```
> java -jar target/chunked-response-sample-0.0.1-SNAPSHOT.jar --vertx.mail.host=${SMTP_HOST} --vertx.mail.username=${SMTP_USERNAME} --vertx.mail.password=${SMTP_PASSWORD}
```

Once the application is running, you can access it with your browser at http://localhost:8080/index.html.
