package dev.snowdrop.vertx.http.server;

import dev.snowdrop.vertx.http.common.VertxWebSocketSession;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.server.ServerWebExchange;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class VertxRequestUpgradeStrategyTest {

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

    @Test
    public void shouldUpgradeToWebSocket() {
        given(mockServerWebExchange.getRequest()).willReturn(mockVertxServerHttpRequest);
        given(mockVertxServerHttpRequest.getNativeRequest()).willReturn(mockHttpServerRequest);
        given(mockHttpServerRequest.upgrade()).willReturn(mockServerWebSocket);

        VertxRequestUpgradeStrategy strategy = new VertxRequestUpgradeStrategy(1, 1);
        strategy.upgrade(mockServerWebExchange, mockWebSocketHandler, null, () -> mockHandshakeInfo);

        verify(mockHttpServerRequest).upgrade();
        verify(mockWebSocketHandler).handle(any(VertxWebSocketSession.class));
    }
}
