package eu.andymel.timecollector.server;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCMonitorServer{

	private static final Logger LOG = LoggerFactory.getLogger(TCMonitorServer.class);
	
	private final TCMonitorServerConfig config;
	
	private Server jettyServer;
	
	public TCMonitorServer(TCMonitorServerConfig config) {
		Objects.requireNonNull(config, "'config' is null!");
		this.config = config;
	}
	
	public void start() throws Exception{
		start(true);
	}
		
	public void start(boolean async) throws Exception{
		/* 
		 * from https://github.com/jetty-project/embedded-jetty-websocket-examples/blob/master/javax.websocket-example/src/main/java/org/eclipse/jetty/demo/EventServer.java
		 */
		Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.getPort());
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(config.getContextPath());
        server.setHandler(context);
        
        
        
     // Initialize javax.websocket layer
        ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);

        // Add WebSocket endpoint to javax.websocket layer
        wscontainer.addEndpoint(TCWebSocket.class);

        server.start();

        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleAtFixedRate(
        	() -> TCWebSocket.send(Instant.now().toString()), 
        	1, 1, TimeUnit.SECONDS
        );
        
        if(!async){
        	// blocks the calling thread until the server has been shut down
        	server.join();
        }
        
	}
	
	
	
}
