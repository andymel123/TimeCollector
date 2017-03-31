package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.ne;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.Edge;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.util.AvgMaxCalcLong;
import eu.andymel.timecollector.util.IdentitySet;

public abstract class AbstractHTMLFileAnalyzer<ID_TYPE> extends AbstractPathAnalyzer<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractHTMLFileAnalyzer.class);

	// TODO hash and save mutliple allowedgraphs for Dashboard views 
	private AllowedPathsGraph<ID_TYPE> allowedGraph;
	
	HashMap<Edge<GraphNode<ID_TYPE, NodePermissions>>, AvgMaxCalcLong> timesPerEdge;
	
	protected AbstractHTMLFileAnalyzer() {
		timesPerEdge = new LinkedHashMap<>();
	}


	
	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		// TODO make async!

		if(allowedGraph==null)allowedGraph = tc.getAllowedGraph();
		super.addCollector(tc);
	}

	@Override
	protected void addTimes(
		GraphNode<ID_TYPE, NodePermissions> node1, 
		GraphNode<ID_TYPE, NodePermissions> node2,
		Instant instant1, Instant instant2) {
		
		List<Edge<GraphNode<ID_TYPE, NodePermissions>>> edgesToChildren = node1.getEdgesToChildren();
		ne(edgesToChildren, "'edgesToChildren' may not be empty!");
		
		Edge<GraphNode<ID_TYPE, NodePermissions>> edgeOfTime = null;
		if(edgesToChildren.size()==1){
			edgeOfTime = edgesToChildren.get(0);
		}else{
			for(Edge<GraphNode<ID_TYPE, NodePermissions>> edge: edgesToChildren){
				if(edge.getChildNode()==node2){
					edgeOfTime = edge;
				}
			}
		}
		
		if(edgeOfTime==null){
			throw new IllegalStateException("There is no edge to '"+node2+"' in "+node1);
		}
		
		addEdgeWithTime(edgeOfTime, Duration.between(instant1, instant2).toNanos());
		
	}
	

	private void addEdgeWithTime(Edge<GraphNode<ID_TYPE, NodePermissions>> edge, long time) {
		AvgMaxCalcLong calc = timesPerEdge.get(edge);
		if(calc == null){
			calc = AvgMaxCalcLong.create();
			timesPerEdge.put(edge, calc);
		}
		calc.add(time);
	}

	protected static String readFile(File file) throws IOException {
		byte[] encoded = getBytesFromFile(file);
		return new String(encoded);
	}
	private static byte[] getBytesFromFile(File file) throws IOException{
		return Files.readAllBytes(file.toPath());
	}
	public static void writeFile(File f, String s, boolean appendToFileIfExisting) throws IOException{
		FileWriter fw = new FileWriter(f, appendToFileIfExisting);
		fw.write(s);
		fw.flush();
		fw.close();
	}

	public void writeToFile(File f, TimeUnit unit, boolean appendToFileIfExisting) throws IOException{
		writeFile(f, getHTMLString(unit), appendToFileIfExisting);
	}

	protected abstract String getHTMLString(TimeUnit unit);
	
	protected AllowedPathsGraph<ID_TYPE> getAllowedGraph() {
		return allowedGraph;
	}
	
}
