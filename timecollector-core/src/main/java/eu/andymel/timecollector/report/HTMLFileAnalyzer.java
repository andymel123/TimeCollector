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

public class HTMLFileAnalyzer<ID_TYPE> extends AbstractPathAnalyzer<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(HTMLFileAnalyzer.class);

	private static final File TEMPLATE_FILE = new File("template.html");
	
	private static String htmlTemplate;

	private AllowedPathsGraph<ID_TYPE> allowedGraph;
	
	HashMap<Edge<GraphNode<ID_TYPE, NodePermissions>>, AvgMaxCalcLong> timesPerEdge;
	
	private int countTimeCollectorsAdded = 0;
	
	private static enum placeholder{
		_REPLACE_DESCRIPTION_,
		_REPLACE_NODES_,
		_REPLACE_EDGES_
	}
	
	private HTMLFileAnalyzer() {
		timesPerEdge = new LinkedHashMap<>();
	}
	public static HTMLFileAnalyzer<TestMilestones> create() {
		return new HTMLFileAnalyzer<>();
	}


	
	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		if(allowedGraph==null)allowedGraph = tc.getAllowedGraph();
		super.addCollector(tc);
		countTimeCollectorsAdded++;
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

	private static String readFile(File file) throws IOException {
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

	private synchronized String getTemplate(){
		if(htmlTemplate==null){
			try {
				htmlTemplate = readFile(TEMPLATE_FILE);
			} catch (IOException e) {
				LOG.error("Can't load template!", e);
				htmlTemplate = "";
			}
		}
		return htmlTemplate;
	}
	
	public void writeToFile(File f, TimeUnit unit, boolean appendToFileIfExisting) throws IOException{
		writeFile(f, getHTMLString(unit), appendToFileIfExisting);
	}
	
	public String getHTMLString(TimeUnit unit) {
		
		Set<GraphNode<ID_TYPE, NodePermissions>> allNodes = allowedGraph.getAllNodes();
		Set<GraphNode<ID_TYPE, NodePermissions>> nodesUsedByAtLeastOneEdge = new IdentitySet<>(allNodes.size());
		
		StringBuilder edgesString = new StringBuilder();
		String edgeFormat = "{from: '%s', to: '%s', label: '(%s | %s | %s)', arrows:'to'}";
		boolean isFirst = true;
		
		// build string of edges
		for(Entry<Edge<GraphNode<ID_TYPE, NodePermissions>>, AvgMaxCalcLong> entry:timesPerEdge.entrySet()){
			Edge<GraphNode<ID_TYPE, NodePermissions>> edge = entry.getKey();
			AvgMaxCalcLong calc = entry.getValue();
			
			GraphNode<ID_TYPE, NodePermissions> from = edge.getParentNode();
			GraphNode<ID_TYPE, NodePermissions> to = edge.getChildNode();
			
			nodesUsedByAtLeastOneEdge.add(from);
			nodesUsedByAtLeastOneEdge.add(to);
			
			if(isFirst){
				isFirst = false;
			}else{
				edgesString.append(',');
			}
			edgesString.append(
				String.format(
					edgeFormat, 
					System.identityHashCode(from), 
					System.identityHashCode(to),
					unit.convert(calc.getMin(),TimeUnit.NANOSECONDS),
					unit.convert((long)calc.getAvg(),TimeUnit.NANOSECONDS),
					unit.convert(calc.getMax(),TimeUnit.NANOSECONDS)
				)
			);	
		}
		
		// build string of nodes that were used for at least one edge
		StringBuilder nodesString = new StringBuilder();
		String oneNodeString = "{id: '%s', label: '%s'}";
		isFirst = true;
		for(GraphNode<ID_TYPE, NodePermissions> node:nodesUsedByAtLeastOneEdge){
			if(isFirst){
				isFirst = false;
			}else{
				nodesString.append(',');
			}
			int identityHashCode = System.identityHashCode(node); 
			nodesString.append(String.format(oneNodeString, String.valueOf(identityHashCode), node.getId()));	
		}
		

		
		
		
		String description = "TimeCollectors analyzed: "+countTimeCollectorsAdded;
		description += "\nEdge labels show (min | avg |max) in "+unit;
		
		String template = getTemplate();
		template = template.replaceFirst(placeholder._REPLACE_DESCRIPTION_.name(), description);
		template = template.replaceFirst(placeholder._REPLACE_NODES_.name(), nodesString.toString());
		template = template.replaceFirst(placeholder._REPLACE_EDGES_.name(), edgesString.toString());
		return template;
	}

}
