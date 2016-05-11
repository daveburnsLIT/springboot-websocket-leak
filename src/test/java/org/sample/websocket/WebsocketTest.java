package org.sample.websocket;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class WebsocketTest {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketTest.class);
    private static String APPLICATION_ENDPOINT_URL = "http://<your-server>:8080/hello";
    private static String WEBSOCKET_ENDPOINT_URL = "ws://<your-server>:8080/websocket";
    private static int MAX_CONNECTIONS = 2000;
    
    private RestTemplate restTemplate =  new RestTemplate();
	
    /**
     * Ramp up the connections in blocks of 500
     * 
     * @throws InterruptedException
     */
    @Test
    public void TestConnectionsSingleThread() throws InterruptedException {
        
        LOGGER.info("Starting single thread test for {} connections", MAX_CONNECTIONS);
        
        List<WebSocketStompClient> connections = new ArrayList<WebSocketStompClient>();
        
        for (int count=1; count < MAX_CONNECTIONS; count++) {
            
            try {
                
                List<Transport> transports = new ArrayList<>(2);
                transports.add(new WebSocketTransport(new StandardWebSocketClient()));
                transports.add(new RestTemplateXhrTransport());
                
                WebSocketClient transport = new SockJsClient(transports);
                WebSocketStompClient stompClient = new WebSocketStompClient(transport);
                stompClient.setTaskScheduler(new DefaultManagedTaskScheduler());
                
                // Below add explicit call to get new auth or hijack one at start of method
                WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
                headers.add("Cookie", getCookies());
                
                long [] heartBeat = {10000,10000};
                stompClient.setDefaultHeartbeat(heartBeat);
                
                StompSessionHandler handler = new TestStompSessionHandler() ;
                stompClient.connect(WEBSOCKET_ENDPOINT_URL, headers, handler);
                
                connections.add(stompClient);
                
                if (count % 500 == 0) {
                    Thread.sleep(60000);
                }
            
            } catch (Exception ex) {
                LOGGER.error("Failed to connect {} ", ex);
                ex.printStackTrace();
            }
            
       }
        
        LOGGER.info("Completed single thread test for {} connections", MAX_CONNECTIONS);
       
        Thread.sleep(6000000);
        
        
    }
    
    @Test
    public void TestConnectionsMultiThreaded() throws InterruptedException {
        
        LOGGER.info("Starting multi thread test for {} threads", MAX_CONNECTIONS);
        
        for (int count=1; count < MAX_CONNECTIONS; count++) {
        	
        	Thread thread = new Thread() {
            
        		public void run() {           
	                
	                List<Transport> transports = new ArrayList<>(2);
	                transports.add(new WebSocketTransport(new StandardWebSocketClient()));
	                transports.add(new RestTemplateXhrTransport());
	                
	                WebSocketClient transport = new SockJsClient(transports);
	                WebSocketStompClient stompClient = new WebSocketStompClient(transport);
	                stompClient.setTaskScheduler(new DefaultManagedTaskScheduler());
	                
	                // Below add explicit call to get new auth or hijack one at start of method
	                WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
	                try {
	                	headers.add("Cookie", getCookies());
	                } catch (Exception e) {
                        LOGGER.info("ERROR ***");
                        //e.printStackTrace();
                    }
	                
	                long [] heartBeat = {10000,10000};
	                stompClient.setDefaultHeartbeat(heartBeat);
	                
	                StompSessionHandler handler = new TestStompSessionHandler() ;
	                stompClient.connect(WEBSOCKET_ENDPOINT_URL, headers, handler);
            
		         } 
        	};
            
             
	        LOGGER.info("About to start thread  {}", count);
	       
	        thread.start();        
        
        } // Thread loop
        
        LOGGER.info("Sleep in thread 30 secs");
        Thread.sleep(300000);
        
    }
    
    /**
     * Get the associated jsessionid for the stomp client connection 
     */
    private String getCookies() throws Exception {
       
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> response = restTemplate.exchange(APPLICATION_ENDPOINT_URL, HttpMethod.GET, request, String.class);
        
        String allCookies =  "";
        
        HttpHeaders httpHeaders = response.getHeaders();
        
        List <String> cookies = httpHeaders.get("Set-Cookie");
        
        for (String s : cookies)
        {
            allCookies += s;
        }
        
        return allCookies;
    }
}
