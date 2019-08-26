package dev.snowdrop.vertx.amqp.it;

final class SslConstants {

    private SslConstants() { }

    static final String SERVER_KEYSTORE_PATH = "target/test-classes/tls/server-keystore.jks";

    static final String SERVER_KEYSTORE_PASSWORD = "wibble";

    static final String SERVER_TRUSTSTORE_PATH = "target/test-classes/tls/server-truststore.jks";

    static final String SERVER_TRUSTSTORE_PASSWORD = "wibble";

    static final String CLIENT_KEYSTORE_PATH = "target/test-classes/tls/client-keystore.jks";

    static final String CLIENT_KEYSTORE_PASSWORD = "wibble";

    static final String CLIENT_TRUSTSTORE_PATH = "target/test-classes/tls/client-truststore.jks";

    static final String CLIENT_TRUSTSTORE_PASSWORD = "wibble";

}
