## Vert.x HTTP OAuth 2 example

This example demonstrates how to use GitHub authentication service in Vert.x WebFlux application.

> Because of Spring Security implementation details, Reactor Netty client is required aside to Vert.x integration.

### Usage

Setup OAuth application at https://github.com/settings/developers with the following parameters.
```
Home URL: http://localhost:8080
Authorization callback URL: http://localhost:8080/login/oauth2/code/github
```

Then export client id and secret as environment variables.
```bash
export GITHUB_CLIENT_ID={your client id}
export GITHUB_CLIENT_SECRET={your client secret}
```

And start the application.
```bash
java -jar target/vertx-spring-boot-sample-http-oauth-0.0.1-SNAPSHOT.jar 
```

Open http://localhost:8080 and you should be redirect to GitHub for authorization.
