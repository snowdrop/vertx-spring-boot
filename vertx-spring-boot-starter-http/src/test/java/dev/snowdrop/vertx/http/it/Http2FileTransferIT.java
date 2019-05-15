package dev.snowdrop.vertx.http.it;

import java.util.Properties;

public class Http2FileTransferIT extends HttpFileTransferIT {

    @Override
    protected Properties defaultProperties() {
        Properties properties = super.defaultProperties();
        properties.setProperty("vertx.http.client.protocol-version", "HTTP_2");
        // Disable text upgrade to make compression work
        properties.setProperty("vertx.http.client.http2-clear-text-upgrade", "false");

        return properties;
    }
}
