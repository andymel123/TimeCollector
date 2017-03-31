package eu.andymel.timecollector.report;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
		_REPLACE_EDGES_,
		_REPLACE_TIMETABLE_
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
		
		IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> timesPerSpan = getTimesPerSpan();
		Objects.requireNonNull(timesPerSpan, "'timesPerSpan' is null!");
		
		Set<GraphNode<ID_TYPE, NodePermissions>> allNodes = allowedGraph.getAllNodes();
		Set<GraphNode<ID_TYPE, NodePermissions>> nodesUsedByAtLeastOneEdge = new IdentitySet<>(allNodes.size());
		
		StringBuilder edgesString = new StringBuilder();
		String edgeFormat = "{from: '%s', to: '%s', label: '%s | %s | %s (%s)', arrows:'to'}";
		boolean isFirst = true;
		
		// build string of edges
		for(Entry<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> outer: getTimesPerSpan().entrySet()){
			GraphNode<ID_TYPE, NodePermissions> node1 = outer.getKey();
			IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong> inner = outer.getValue();
			for(Entry<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong> e: inner.entrySet()){
				GraphNode<ID_TYPE, NodePermissions> node2 = e.getKey();
				AvgMaxCalcLong calc = e.getValue();
				
				nodesUsedByAtLeastOneEdge.add(node1);
				nodesUsedByAtLeastOneEdge.add(node2);
				
				if(isFirst){
					isFirst = false;
				}else{
					edgesString.append(',');
				}
				edgesString.append(
					String.format(
						edgeFormat, 
						System.identityHashCode(node1), 
						System.identityHashCode(node2),
						unit.convert(calc.getMin(),TimeUnit.NANOSECONDS),
						unit.convert((long)calc.getAvg(),TimeUnit.NANOSECONDS),
						unit.convert(calc.getMax(),TimeUnit.NANOSECONDS),
						calc.getCount()
					)
				);
			}
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
		
		String[][] arr = getAsStringTable(unit).asArray();

		String tableString = "No Data!";
		
		if(arr!=null && arr.length>1){

			// first line is the header
			StringBuilder sbTable = new StringBuilder();
			sbTable
			.append("<table>")
			.append("<tr><th>")
			.append(arr[0][0]).append("</th><th>")
			.append(arr[0][1]).append("</th><th>")
			.append(arr[0][2]).append("</th><th>")
			.append(arr[0][3])
			.append("</th></tr>");

			for(int r=1; r<arr.length; r++){
				sbTable
				.append("<tr><td>")
				.append(arr[r][0]).append("</td><td>")
				.append(arr[r][1]).append("</td><td>")
				.append(arr[r][2]).append("</td><td>")
				.append(arr[r][3])
				.append("</td></tr>");
			}
			
			sbTable.append("</table>");
			
			tableString = sbTable.toString();
		}
		
		
		
		String description = "TimeCollectors analyzed: "+getNumberOfAddedTimeCollectors();
		description += "\nEdge labels show 'min | avg | max (count)' in "+unit;
		
		String template = getTemplate();
		template = template.replaceFirst(placeholder._REPLACE_DESCRIPTION_.name(), 	description);
		template = template.replaceFirst(placeholder._REPLACE_NODES_.name(), 		nodesString.toString());
		template = template.replaceFirst(placeholder._REPLACE_EDGES_.name(), 		edgesString.toString());
		template = template.replaceFirst(placeholder._REPLACE_TIMETABLE_.name(), 	tableString);
		return template;
	}
	
	
	
	
}
