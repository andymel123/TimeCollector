package eu.andymel.timecollector.server;

import java.util.Objects;

public enum TCWebsocketDataMgr {

	INSTANCE;
	private TCWebsocketDataMgr(){}

	private TCWebSocketDispatcher dispatcher;
	
	void setWebSocketDispatcher(TCWebSocketDispatcher d){
		this.dispatcher = d;
	}
	
	TCWebSocketDispatcher getWebSocketDisptcher(){
		return dispatcher;
	}
	
}
