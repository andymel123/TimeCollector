package eu.andymel.timecollector.report.html;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Random;
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

public class HTMLFormatterRangeBars<ID_TYPE> extends AbstractHTMLFormatter<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(HTMLFormatterRangeBars.class);

	private static String htmlTemplate;

	private static Random random = new Random();
	
	private static enum placeholder{
		_REPLACE_GRAPH_TITLE_,
		_REPLACE_LABELS_,
		_REPLACE_DATA_,
		_REPLACE_UNIT_,
		_REPLACE_TIMETABLE_
	}
	

	public static <ID_TYPE> HTMLFormatterRangeBars<ID_TYPE> create(Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer) {
		if(!(analyzer instanceof AnalyzerAvgPath)){
			throw new IllegalStateException(HTMLFormatterBars.class.getSimpleName()+" is not capable of using "+analyzer.getClass().getSimpleName());
		}
		return new HTMLFormatterRangeBars<>(analyzer);
	}

	private HTMLFormatterRangeBars(Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer) {
		super(analyzer);
	}
	
	@Override
	protected File getTemplateFile() {
		return new File("templateRangeBar.html");
	}
	
	@Override
	public String getHTMLString(TimeUnit unit) {
		
		AnalyzerAvgPath<ID_TYPE> analyzer = (AnalyzerAvgPath<ID_TYPE>)getAnalyzer(); // checked in constructor
		Objects.requireNonNull(analyzer, "'analyzer' is null");

		AllowedPathsGraph<ID_TYPE> allowedGraph = analyzer.getAllowedGraph();
		Objects.requireNonNull(allowedGraph, "'allowedGraph' is null!");
		
		IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> timesPerSpan = analyzer.getTimesPerSpan();
		Objects.requireNonNull(timesPerSpan, "'timesPerSpan' is null!");
		
		Set<GraphNode<ID_TYPE, NodePermissions>> allNodes = allowedGraph.getAllNodes();
		Set<GraphNode<ID_TYPE, NodePermissions>> nodesUsedByAtLeastOneEdge = new IdentitySet<>(allNodes.size());
		
		
		double totalAvg = analyzer.getAvgSummedUp();
		
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
		String labelString = "";
		String dataString = "";
		
		if(arr!=null && arr.length>1){

			// first line is the header
			StringBuilder sbTable = new StringBuilder();
			StringBuilder sbLables = new StringBuilder();
			StringBuilder sbData = new StringBuilder();
			
			String formatTableHeader= "<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr>";
			String formatTableRow 	= "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
			//{x: new Date(2014,03,1), y: [ 17,33]},  
			String formatDataRow 	= "{label: '%s', y: [ %s,%s]}";

			
			sbTable.append("<table>")
			.append(String.format(formatTableHeader, arr[0][0],arr[0][1],arr[0][2],arr[0][3]));
			
			boolean isFirst = true;
			
			// rows
			for(int r=1; r<arr.length; r++){
				String name = arr[r][0];
				String min = arr[r][1];
				String avg = arr[r][2];
				String max = arr[r][3];
				
//				min = ""+random.nextInt(100);
				
				sbTable.append(String.format(formatTableRow, name, min, avg, max));

				if(isFirst){
					isFirst=false;
				}else{
					sbLables.append(',');
					sbData.append(',');
				}
				sbLables.append('"').append(name).append('"');
				sbData.append(String.format(formatDataRow, name, min, avg));
			}
			
			sbTable.append(String.format(formatTableRow, "Sum of averages","",(long)totalAvg,""));
			sbTable.append("</table>");
			
			tableString = sbTable.toString();
			dataString = sbData.toString();
			labelString = sbLables.toString();
		}
		
		
		
		String description = analyzer.getNumberOfAddedTimeCollectors()+"TimeCollectors analyzed: average time in "+unit;
		
		String template = getTemplate();
		template = template.replaceFirst(placeholder._REPLACE_GRAPH_TITLE_.name(), 	description);
		template = template.replaceFirst(placeholder._REPLACE_DATA_.name(), 		dataString);
		template = template.replaceFirst(placeholder._REPLACE_TIMETABLE_.name(), 	tableString);
		return template;
	}
	
	
	
}
