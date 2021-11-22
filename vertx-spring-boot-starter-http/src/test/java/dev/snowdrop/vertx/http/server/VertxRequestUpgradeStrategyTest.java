package dev.snowdrop.vertx.http.server;

import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.server.ServerWebExchange;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VertxRequestUpgradeStrategyTest {

    private final Logger logger = LoggerFactory.getLogger(VertxRequestUpgradeStrategyTest.class);

    @Mock
    private ServerWebExchange mockServerWebExchange;

    @Mock
    private WebSocketHandler mockWebSocketHandler;

    @Mock
    private VertxServerHttpRequest mockVertxServerHttpRequest;

    @Mock
    private HttpServerRequest mockHttpServerRequest;

    @Mock
    private ServerWebSocket mockServerWebSocket;

    @Mock
    private HandshakeInfo mockHandshakeInfo;

//    @Test
    public void shouldUpgradeToWebSocket() {
        given(mockServerWebExchange.getRequest()).willReturn(mockVertxServerHttpRequest);
        given(mockVertxServerHttpRequest.getNativeRequest()).willReturn(mockHttpServerRequest);
        logger.error("mockHttpServerRequest.toWebSocket(): {}", mockHttpServerRequest.toWebSocket());
//        given(mockHttpServerRequest.toWebSocket().result()).willReturn(mockServerWebSocket);
//        verify(mockWebSocketHandler).handle(any(VertxWebSocketSession.class));
    }
}
