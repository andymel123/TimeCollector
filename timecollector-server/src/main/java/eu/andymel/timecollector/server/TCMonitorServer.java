package eu.andymel.timecollector.server;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.report.TimeSpanNameFormatter;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath.AnalyzerEachEntry;
import eu.andymel.timecollector.report.analyzer.AnalyzerListener;
import eu.andymel.timecollector.util.ColorGenerator;

public class TCMonitorServer implements AnalyzerListener{

	private static final Logger LOG = LoggerFactory.getLogger(TCMonitorServer.class);

	private static final String[] EMPTY_DATA = new String[]{"","",""};
    private ScheduledExecutorService es;

	private final TCMonitorServerConfig config;
	
	private Server jettyServer;

	private AnalyzerEachPath<?> monitoredAnalyzer;
	private volatile boolean analyzerUpdated;
	
	private List<Runnable> serverStoppingHooks;
	
	private final double updatesPerMinute;
	
	public TCMonitorServer(TCMonitorServerConfig config, double updatesPerMinute) {
		Objects.requireNonNull(config, "'config' is null!");
		this.config = config;
		this.updatesPerMinute = updatesPerMinute;
	}
	
	public void start() throws Exception{
		start(true);
	}
		
	public synchronized void start(boolean async) throws Exception{
		/* 
		 * from https://github.com/jetty-project/embedded-jetty-websocket-examples/blob/master/javax.websocket-example/src/main/java/org/eclipse/jetty/demo/EventServer.java
		 */
		jettyServer = new Server();
        ServerConnector connector = new ServerConnector(jettyServer);
        connector.setPort(config.getPort());
        jettyServer.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(config.getContextPath());
        jettyServer.setHandler(context);
        
        
        // static files
        ServletHolder holderHome = new ServletHolder("static-files", DefaultServlet.class);
        String staticFilesHome = config.getStaticWebContentDir();
        File f = new File(staticFilesHome);
        if(!f.exists() || !f.isDirectory() || f.listFiles().length==0){
        	throw new IllegalArgumentException("There is a problem with the given directory for searching "
        			+ "static files of the time collector monitor server! The absolute path does either "
        			+ "not exist("+f.exists()+"), is not a directory("+f.isDirectory()+" or is empty("+(f.listFiles().length==0)+"))! '"+f+"'");
        }
        
        LOG.info("static home '"+f.getAbsolutePath()+"' ("+f.listFiles().length+" files "+Arrays.toString(f.listFiles())+")");
        holderHome.setInitParameter("resourceBase", staticFilesHome);
        
        /* I'm not totally sure why I need this but I assume it's needed to be able to 
         * serve my static content from anything else but "/"
         * see http://stackoverflow.com/a/20223103 */
        holderHome.setInitParameter("pathInfoOnly","true");
        
//        holderHome.setInitParameter("dirAllowed","true");
        
        String staticSubPath = config.getSubPathStaticWebContent();
        context.addServlet(holderHome, staticSubPath);
        
        
        // WebSocket 
        // Initialize javax.websocket layer
        ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
        // Add WebSocket endpoint to javax.websocket layer
        wscontainer.addEndpoint(TCWebSocket.class);

        jettyServer.addLifeCycleListener(new LifeCycle.Listener() {
			
			@Override
			public void lifeCycleStopping(LifeCycle event) {
				LOG.info("Stopping jetty monitoring server");
			}
			
			@Override
			public void lifeCycleStopped(LifeCycle event) {
				LOG.info("Jetty monitoring server stopped");
			}
			
			@Override
			public void lifeCycleStarting(LifeCycle event) {
				LOG.info("Starting jetty monitoring server");
			}
			
			@Override
			public void lifeCycleStarted(LifeCycle event) {
				LOG.info("Jetty monitoring server started on port "+config.getPort());
			}
			
			@Override
			public void lifeCycleFailure(LifeCycle event, Throwable cause) {
				LOG.info("Jetty monitoring server failed");
			}
		});
        
        // start to listen on port
        jettyServer.start();


        if(!async){
        	// blocks the calling thread until the server has been shut down
        	jettyServer.join();
        }
        
	}

	public synchronized void stop(){

		if(jettyServer!=null){
			LOG.info("Stopping jetty monitoring server");
			try {
				jettyServer.stop();
			} catch (Exception e) {
				LOG.error("Exception while stopping monitoring jetty server", e);
			}
		}
		
		if(es!=null){
			LOG.info("Shuttiong down monitoring thread");
			try{
				es.shutdown();	
			}catch(Exception e){
				LOG.error("Exception while shutting down monitor thread", e);
			}
		}
		
		if(serverStoppingHooks!=null && serverStoppingHooks.size()>0){
			LOG.info("Calling shutdown hooks");
			serverStoppingHooks.forEach(r->CompletableFuture.runAsync(r));
		}
		
	}
	
	public synchronized void addServerStoppingHook(Runnable r){
		if(serverStoppingHooks==null){
			serverStoppingHooks = (List<Runnable>)Collections.synchronizedList(new ArrayList<Runnable>());
		}
		serverStoppingHooks.add(r);
	}
	
	public synchronized void setTimeCollectorAnalyzer(AnalyzerEachPath<?> analyzer) {
		this.monitoredAnalyzer = analyzer;
		this.monitoredAnalyzer.addListener(this);
		this.analyzerUpdated = true;
		startUpdateThread();
	}

	private void startUpdateThread() {
		if(es==null){
			ThreadFactory namedThreadFactory = new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "Monitor Updater Thread");
				}
			};
			es = Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
		}else{
			throw new IllegalStateException("Already Started?!");
		}
		
		es.scheduleAtFixedRate(
        	() -> {
        		if(analyzerUpdated){
            		try{
//            			LOG.info("update");
                		analyzerUpdated=false;
                		TCWebSocket.send(getAnalyzerStateAsJson(monitoredAnalyzer));
            		}catch(Exception e){
            			LOG.error("Exception in monitor thread", e);
            			stop();
            		}
        		}
        	},
        	0, (long)(Math.max(60000/updatesPerMinute, 1)), TimeUnit.MILLISECONDS
        );
	}

	private static JsonObject getAnalyzerStateAsJson(AnalyzerEachPath<?> monitoredAnalyzer) {

		return getFullDataJsonObject(monitoredAnalyzer, TimeUnit.MICROSECONDS);
		
	}

	private static JsonObject getFullDataJsonObject(AnalyzerEachPath analyzer, TimeUnit unit) {

		Objects.requireNonNull(analyzer, "'analyzer' is null");
		Collection<AnalyzerEachEntry> recordedPaths = analyzer.getAll();
		Objects.requireNonNull(recordedPaths, "'recordedPaths' is null!");

		if(recordedPaths.size()==0)return null;
		
		JsonObject completeJsonObject = new JsonObject();
		JsonArray graphData = new JsonArray();
		JsonArray hashes = new JsonArray();
		completeJsonObject.add("type", 		"fulldata");
		completeJsonObject.set("graphData", graphData);
		completeJsonObject.set("hashes", 	hashes);
		
		TimeSpanNameFormatter tsNameFormat = TimeSpanNameFormatter.DEFAULT_TIMESPAN_NAME_FORMATTER;
		
		for(AnalyzerEachEntry e: recordedPaths){
			
			JsonObject singleGraphJsonObject = new JsonObject();
			
			JsonArray lables = new JsonArray();
			JsonArray data = new JsonArray();
			
			
			List<GraphNode> recPath = e.getRecPath();
			if(recPath==null || recPath.size()==0){
				throw new IllegalStateException("There is no recorded path in this entry?! "+e);
			}
			List<long[]> collectedTimes = e.getCollectedTimes(); // returns a copy of the internal array, so no copying is needed here
			if(collectedTimes==null || collectedTimes.size()==0){
				throw new IllegalStateException("There is a recpath but no recorded times?! "+Arrays.toString(recPath.toArray()));
			}
			if(!(recPath instanceof ArrayList)){
				// to ensure a fast call to get(idx) 
				recPath = new ArrayList<>(recPath);
			}
			int numberOfRecordedMilestones = recPath.size();
			
			JsonArray[] dataArrays = new JsonArray[numberOfRecordedMilestones];
			// per milestone
			GraphNode lastNode = null;
			int idx = 0;
			for(GraphNode node: recPath){
				if(lastNode!=null){
					idx++;	/* starting with 1 to have similar idx like like in times[] below 
					(where times[0] is no timespan but the time the timeCollector was added to the analyser)*/
					String timeSpanName = tsNameFormat.getTimeSpanName(lastNode, node);

					JsonObject dataset = new JsonObject();
					dataset.set("label", timeSpanName);
					dataset.set("backgroundColor", getHexColorString(idx));
					dataset.set("borderColor", "#FFFFFF");

					JsonArray dataArray = new JsonArray();
					dataset.set("data", dataArray);
					dataArrays[idx] = dataArray;

					data.add(dataset);
				}
				lastNode = node;
			}
			
			int maxView = 300;
			
			// per TimeCollector that went this path
			idx = -1;
			for(long[] times: collectedTimes){
				idx++;
				if(idx>maxView)break;
				
				if(numberOfRecordedMilestones!=times.length){
					throw new IllegalStateException("Different number of nodes and times! "+numberOfRecordedMilestones+" nodes, "+times.length+" times!");
				}

				lables.add(times[0]);
				
				// per milestone
				for(int t=1; t<times.length; t++){
					dataArrays[t].add(unit.convert(times[t], TimeUnit.NANOSECONDS));
				}

			}

			String description = "Showing the last "+collectedTimes.size()+" of "+analyzer.getNumberOfAddedTimeCollectors()+" analyzed TimeCollectors. Times written in "+unit;

			int hash = e.getHashOfRecPath();
			/* I build a string that is already valid to use as id in the html page 
			 * commented out because I had too many problems with that. is doable, but 
			 * as it is not logical to do it on the server I skip it*/
//			hashes.add("#cvs"+hash); 
			hashes.add(hash); 
			
			singleGraphJsonObject.add("hash", 			hash);
			singleGraphJsonObject.add("description", 	description);
			singleGraphJsonObject.set("labels", 		lables);
			singleGraphJsonObject.set("datasets", 		data);
			graphData.add(singleGraphJsonObject);
		}
		
		return completeJsonObject;
		
	}
	
	private static String getHexColorString(int idx) {
		Color c = ColorGenerator.getColor(idx);
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}

	@Override
	public void timeCollectorAddedToAnalyzer(TimeCollector<?> tc, Analyzer<?, ?> analyzer) {
//		LOG.info("inform new tc");
		analyzerUpdated = true;
	}


	
}
