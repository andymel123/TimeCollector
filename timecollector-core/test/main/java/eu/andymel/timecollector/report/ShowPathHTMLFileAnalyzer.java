package eu.andymel.timecollector.report;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.Edge;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.util.AvgMaxCalcLong;
import eu.andymel.timecollector.util.IdentitySet;

public class ShowPathHTMLFileAnalyzer<ID_TYPE> extends AbstractHTMLFileAnalyzer<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(ShowPathHTMLFileAnalyzer.class);

	private static final File TEMPLATE_FILE = new File("template.html");

	private static String htmlTemplate;

	private static enum placeholder{
		_REPLACE_DESCRIPTION_,
		_REPLACE_NODES_,
		_REPLACE_EDGES_
	}
	

	public static <ID_TYPE> ShowPathHTMLFileAnalyzer<ID_TYPE> create() {
		return new ShowPathHTMLFileAnalyzer<>();
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

	public String getHTMLString(TimeUnit unit) {
		
		AllowedPathsGraph<ID_TYPE> allowedGraph = getAllowedGraph();
		Objects.requireNonNull(allowedGraph, "'allowedGraph' is null!");
		
		HashMap<Edge<GraphNode<ID_TYPE, NodePermissions>>, AvgMaxCalcLong> timesPerEdge = getTimesPerEdge();
		Objects.requireNonNull(timesPerEdge, "'timesPerEdge' is null!");
		
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
		

		
		
		
		String description = "TimeCollectors analyzed: "+getNumberOfAddedTimeCollectors();
		description += "\nEdge labels show (min | avg |max) in "+unit;
		
		String template = getTemplate();
		template = template.replaceFirst(placeholder._REPLACE_DESCRIPTION_.name(), description);
		template = template.replaceFirst(placeholder._REPLACE_NODES_.name(), nodesString.toString());
		template = template.replaceFirst(placeholder._REPLACE_EDGES_.name(), edgesString.toString());
		return template;
	}
	
	
	
	
}
