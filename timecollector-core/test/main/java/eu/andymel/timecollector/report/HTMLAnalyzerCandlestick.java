package eu.andymel.timecollector.report;

import java.io.File;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.util.AvgMaxCalcLong;
import eu.andymel.timecollector.util.IdentitySet;

public class HTMLAnalyzerCandlestick<ID_TYPE> extends AbstractHTMLFileAnalyzer<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(HTMLAnalyzerCandlestick.class);

	private static enum placeholder{
		_REPLACE_DATA_,
		_REPLACE_DESCRIPTION_,
		_REPLACE_TIMETABLE_,
		_REPLACE_MAX_VALUE_
	}
	
	@Override
	protected File getTemplateFile() {
		return new File("template_candlestick.html");
	}

	public static <ID_TYPE> HTMLAnalyzerCandlestick<ID_TYPE> create() {
		return new HTMLAnalyzerCandlestick<>();
	}

	public String getHTMLString(TimeUnit unit) {
		
		AllowedPathsGraph<ID_TYPE> allowedGraph = getAllowedGraph();
		Objects.requireNonNull(allowedGraph, "'allowedGraph' is null!");
		
		IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> timesPerSpan = getTimesPerSpan();
		Objects.requireNonNull(timesPerSpan, "'timesPerSpan' is null!");
		
		Set<GraphNode<ID_TYPE, NodePermissions>> allNodes = allowedGraph.getAllNodes();
		Set<GraphNode<ID_TYPE, NodePermissions>> nodesUsedByAtLeastOneEdge = new IdentitySet<>(allNodes.size());
		
		double totalAvg = getAvgSummedUp();
		
		StringTable table = getAsStringTable(unit);
		table.sort((String[] row1, String[] row2)->{
			try{
				return Integer.compare(Integer.parseInt(row2[2]), Integer.parseInt(row1[2]));	
			}catch(Exception e){
				return Integer.MAX_VALUE;	// header(not a number) to the top
			}
		});
		String[][] arr = table.asArray();

		String tableString = "No Data!";
		String dataString = "";
		String maxY = "";
		
		if(arr!=null && arr.length>1){

			// first line is the header
			StringBuilder sbTable = new StringBuilder();
			StringBuilder sbCandlestick = new StringBuilder();
			
			String formatTableHeader= "<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr>";
			String formatTableRow 	= "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
			String formatCandleStickDataRow = "['%s', %s, %s, %s, %s]";
			
			sbTable.append("<table>")
			.append(String.format(formatTableHeader, arr[0][0],arr[0][1],arr[0][2],arr[0][3]));
			
			boolean isFirst = true;
			// rows
			for(int r=1; r<arr.length; r++){
				String name = arr[r][0];
				String min = arr[r][1];
				String avg = arr[r][2];
				String max = arr[r][3];
				
				sbTable.append(String.format(formatTableRow, name, min, avg, max));
				
				
				if(isFirst){
					isFirst=false;
					// the first avg is the highest avg
					// set the max y of the chart 30% above this heighest avg
					maxY = String.valueOf((Double.parseDouble(avg)*1.3));
				}else{
					sbCandlestick.append(',');
				}
				sbCandlestick.append(String.format(formatCandleStickDataRow, name, min, avg, avg, max));
			}

			sbTable.append(String.format(formatTableRow, "Sum of averages","",(long)totalAvg,""));
			sbTable.append("</table>");
			
			tableString = sbTable.toString();
			dataString = sbCandlestick.toString();
		}
		
		String description = "TimeCollectors analyzed: "+getNumberOfAddedTimeCollectors();
		description += "\nEdge labels show 'min | avg | max (count)' in "+unit;
		
		String template = getTemplate();
		template = template.replaceFirst(placeholder._REPLACE_DESCRIPTION_.name(), 	description);
		template = template.replaceFirst(placeholder._REPLACE_DATA_.name(), 		dataString);
		template = template.replaceFirst(placeholder._REPLACE_TIMETABLE_.name(), 	tableString);
		template = template.replaceFirst(placeholder._REPLACE_MAX_VALUE_.name(), 	maxY);
		return template;
	}
	
	
	@Override
	protected String getTimeSpanName(GraphNode<ID_TYPE, NodePermissions> from, GraphNode<ID_TYPE, NodePermissions> to) {
		return from.getId()+" => "+to.getId();
	}
	
}
