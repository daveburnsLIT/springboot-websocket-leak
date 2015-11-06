package org.sample.websocket;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

public class TestStompSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStompSessionHandler.class);
            
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        
        LOGGER.debug("Session [{}]  connected", session.getSessionId());
        
        session.subscribe("/user/exchange/amq.direct/notifications", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // ...
            }

        });
    }

}
