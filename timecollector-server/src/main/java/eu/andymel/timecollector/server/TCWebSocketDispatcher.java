package eu.andymel.timecollector.server;

import java.util.function.Consumer;

public interface TCWebSocketDispatcher {

	void dispatch(String command, Consumer<String> answerConsumer);
	
}
