package eu.andymel.timecollector.report.html;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerAvgPath;
import eu.andymel.timecollector.util.AvgMaxCalcLong;
import eu.andymel.timecollector.util.IdentitySet;
import eu.andymel.timecollector.util.StringTable;

public class HTMLFormatterPath<ID_TYPE> extends AbstractHTMLFormatter<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(HTMLFormatterPath.class);

	private static String htmlTemplate;

	private static enum placeholder{
		_REPLACE_DESCRIPTION_,
		_REPLACE_NODES_,
		_REPLACE_EDGES_,
		_REPLACE_TIMETABLE_
	}
	

	public static <ID_TYPE> HTMLFormatterPath<ID_TYPE> create(Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer) {
		if(!(analyzer instanceof AnalyzerAvgPath)){
			throw new IllegalStateException(HTMLFormatterBars.class.getSimpleName()+" is not capable of using "+analyzer.getClass().getSimpleName());
		}
		return new HTMLFormatterPath<>(analyzer);
	}
	
	private HTMLFormatterPath(Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer) {
		super(analyzer);
	}
	
	
	
	@Override
	protected File getTemplateFile() {
		return new File("templatePath.html");
	}
	
	public String getHTMLString(TimeUnit unit) {
		
		AnalyzerAvgPath<ID_TYPE> analyzer = (AnalyzerAvgPath<ID_TYPE>)getAnalyzer(); // checked in constructor
		Objects.requireNonNull(analyzer, "'analyzer' is null");

		AllowedPathsGraph<ID_TYPE> allowedGraph = analyzer.getAllowedGraph();
		Objects.requireNonNull(allowedGraph, "'allowedGraph' is null!");
		
		IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> timesPerSpan = analyzer.getTimesPerSpan();
		Objects.requireNonNull(timesPerSpan, "'timesPerSpan' is null!");
		
		Set<GraphNode<ID_TYPE, NodePermissions>> allNodes = allowedGraph.getAllNodes();
		Set<GraphNode<ID_TYPE, NodePermissions>> nodesUsedByAtLeastOneEdge = new IdentitySet<>(allNodes.size());
		
		StringBuilder edgesString = new StringBuilder();
		String edgeFormat = "{from: '%s', to: '%s', label: '%s | %s | %s (%.2f%%)', arrows:'to', value: %s}";
		boolean isFirst = true;
		
		double totalAvg = analyzer.getAvgSummedUp();
		
		// build string of edges
		for(Entry<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> outer: analyzer.getTimesPerSpan().entrySet()){
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
				
				double avg = calc.getAvg();
				double weightFactor = 100*avg/totalAvg;
				edgesString.append(
					String.format(
						edgeFormat, 
						System.identityHashCode(node1), 
						System.identityHashCode(node2),
						unit.convert(calc.getMin(),TimeUnit.NANOSECONDS),
						unit.convert((long)avg,TimeUnit.NANOSECONDS),
						unit.convert(calc.getMax(),TimeUnit.NANOSECONDS),
						weightFactor,
						weightFactor
					)
				);
			}
		}
		
		// build string of nodes that were used for at least one edge
		StringBuilder nodesString = new StringBuilder();
		String oneNodeString = "{id: '%s', label: '%s', shape: 'box'}";
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
		
		StringTable table = analyzer.getAsStringTable(unit, this);
		table.sort((String[] row1, String[] row2)->{
			try{
				return Integer.compare(Integer.parseInt(row2[2]), Integer.parseInt(row1[2]));	
			}catch(Exception e){
				return Integer.MAX_VALUE;	// header(not a number) to the top
			}
		});
		String[][] arr = table.asArray();

		String tableString = "No Data!";
		
		if(arr!=null && arr.length>1){

			// first line is the header
			StringBuilder sbTable = new StringBuilder();
			
			String formatTableHeader= "<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr>";
			String formatTableRow 	= "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";

			
			sbTable.append("<table>")
			.append(String.format(formatTableHeader, arr[0][0],arr[0][1],arr[0][2],arr[0][3]));
			
			// rows
			for(int r=1; r<arr.length; r++){
				String name = arr[r][0];
				String min = arr[r][1];
				String avg = arr[r][2];
				String max = arr[r][3];
				
				sbTable.append(String.format(formatTableRow, name, min, avg, max));

			}
			
			sbTable.append(String.format(formatTableRow, "Sum of averages","",(long)totalAvg,""));
			sbTable.append("</table>");
			
			tableString = sbTable.toString();
		}
		
		
		
		String description = "TimeCollectors analyzed: " + analyzer.getNumberOfAddedTimeCollectors();
		description += "\nEdge labels show 'min | avg | max (count)' in "+unit;
		
		String template = getTemplate();
		template = template.replaceFirst(placeholder._REPLACE_DESCRIPTION_.name(), 	description);
		template = template.replaceFirst(placeholder._REPLACE_NODES_.name(), 		nodesString.toString());
		template = template.replaceFirst(placeholder._REPLACE_EDGES_.name(), 		edgesString.toString());
		template = template.replaceFirst(placeholder._REPLACE_TIMETABLE_.name(), 	tableString);
		return template;
	}
	
	@Override
	public String getTimeSpanName(GraphNode<ID_TYPE, NodePermissions> from, GraphNode<ID_TYPE, NodePermissions> to) {
		return String.format("%s <b>&rArr;</b> %s", from.getId(), to.getId());
	}
}
