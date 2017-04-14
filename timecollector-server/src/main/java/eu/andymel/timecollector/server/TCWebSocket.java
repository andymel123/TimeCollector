package eu.andymel.timecollector.server;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;

/**
 * Inspired by https://github.com/jetty-project/embedded-jetty-websocket-examples/blob/master/javax.websocket-example/src/main/java/org/eclipse/jetty/demo/EventSocket.java
 * 
 * @author andymatic
 *
 */

@ServerEndpoint(value="/ws/v1")
public class TCWebSocket {

	private static final Logger LOG = LoggerFactory.getLogger(TCWebSocket.class);

	private static final Collection<Session> openSessions = ConcurrentHashMap.newKeySet(); 
	
//	public TCWebSocket(String s) {
//		LOG.info("TCWebSocket init!!! "+s);
//	}

	public TCWebSocket() {
		LOG.info("TCWebSocket init!!!");
	}
	
	@OnOpen
	public void onWebSocketConnect(Session sess) {
		LOG.info("Socket Connected: " + sess);
		openSessions.add(sess);
		LOG.info("sessions: "+openSessions.size()+" / "+sess.getOpenSessions().size());
	}

	@OnMessage
	public void onWebSocketText(Session sess, String message) {
		LOG.info("Received TEXT message: " + message);
		StringBuilder sb = (StringBuilder)sess.getUserProperties().get("txt");
		if(sb==null){
			sb = new StringBuilder();
			sess.getUserProperties().put("txt", sb); 
		}else{
			sb.append(',');
		}
		sb.append(message);
	}

	@OnClose
	public void onWebSocketClose(Session sess, CloseReason reason) {
		LOG.info("Socket Closed: " + reason);
		openSessions.remove(sess);
		StringBuilder sb = (StringBuilder)sess.getUserProperties().get("txt");
		if(sb!=null)LOG.info("txt:"+ sb.toString());
		LOG.info("sessions: "+openSessions.size()+" / "+sess.getOpenSessions().size());
	}

	@OnError
	public void onWebSocketError(Session sess, Throwable cause) {
		LOG.error("WebSocket Error", cause);
	}
	
	public static void send(JsonObject data){
		if(data==null)return;
		if(openSessions==null || openSessions.size()==0)return;
		AtomicInteger count = new AtomicInteger(0);
		String txt = data.toString();
		openSessions.forEach(s->{
			s.getAsyncRemote().sendText(txt);
			count.incrementAndGet();
		});
//		LOG.info("Sent "+txt.length()+" chars to "+count.get()+" clients");
//		LOG.info(txt);
	}
}
