package com.cs309.websocket3.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * configuration class for setting up websocket server endpoints
 * @author Soma Germano
 */
@Configuration
public class ChatSocketConfig {
<<<<<<< HEAD:Experiments/weyderts/websockets3/websocket3/src/main/java/com/cs309/websocket3/chat/ChatSocketConfig.java
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
=======
    /**
     *scans application for endpoints and registers them
     * with the underlying websocket runtime
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
>>>>>>> demo3_soma:Backend/src/main/java/backend/Chat/websocket/ChatSocketConfig.java

}
