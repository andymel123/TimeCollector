package eu.andymel.timecollector.report.html;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath.AnalyzerEachEntry;
import eu.andymel.timecollector.util.ColorGenerator;

public class HTMLFormatterStackedBars<ID_TYPE> extends AbstractHTMLFormatter<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(HTMLFormatterStackedBars.class);

	private static enum placeholder{
		_REPLACE_LABELS_,
		_REPLACE_DATA_,
		_REPLACE_DESCRIPTION_
	}

	public static <ID_TYPE> HTMLFormatterStackedBars<ID_TYPE> create(Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer) {
		if(!(analyzer instanceof AnalyzerEachPath)){
			throw new IllegalStateException("Not capable of using "+analyzer.getClass().getSimpleName());
		}
		return new HTMLFormatterStackedBars<>(analyzer);
	}

	private HTMLFormatterStackedBars(Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer) {
		super(analyzer);
	}
	
	@Override
	protected File getTemplateFile() {
		return new File("templateEach.html");
	}
	
	public String getHTMLString(TimeUnit unit) {
		
		AnalyzerEachPath<ID_TYPE> analyzer = (AnalyzerEachPath<ID_TYPE>)getAnalyzer(); // checked in constructor
		Objects.requireNonNull(analyzer, "'analyzer' is null");

		Collection<AnalyzerEachEntry<ID_TYPE>> recordedPaths = analyzer.getAll();
		Objects.requireNonNull(recordedPaths, "'recordedPaths' is null!");
		
		if(recordedPaths.size()>1){
//			TODO
//			throw new IllegalStateException("not yet implemented to display mutliple different paths!");
			
		}

		AnalyzerEachEntry<ID_TYPE> e = null;
		Iterator<AnalyzerEachEntry<ID_TYPE>> it = recordedPaths.iterator();
		int maxSize = 0;
		while(it.hasNext()){
			AnalyzerEachEntry<ID_TYPE> pathData = it.next();
			int collectedTCs = pathData.getCollectedTimes().size();
			if(collectedTCs>maxSize){
				maxSize = collectedTCs;
				e = pathData;
			}
		}

		if(e==null)return "No paths";
		
		// per path
//		for(AnalyzerEachEntry<ID_TYPE> e: recordedPaths){

		
			StringBuilder sbLables = new StringBuilder();
			StringBuilder sbData = new StringBuilder();

			List<GraphNode<ID_TYPE,NodePermissions>> recPath = e.getRecPath();
			if(recPath==null || recPath.size()==0){
				throw new IllegalStateException("There is no recorded path in this entry?! "+e);
			}
			List<long[]> collectedTimes = e.getCollectedTimes();
			if(collectedTimes==null || collectedTimes.size()==0){
				throw new IllegalStateException("There is a recpath but no recorded times?! "+Arrays.toString(recPath.toArray()));
			}
			if(!(recPath instanceof ArrayList)){
				// to ensure a fast call to get(idx) 
				recPath = new ArrayList<>(recPath);
			}
			int numberOfRecordedMilestones = recPath.size();
			
			StringBuilder[] dataRows = new StringBuilder[numberOfRecordedMilestones]; // idx0 will be empty like in times[] below
						// per milestone
			GraphNode<ID_TYPE,NodePermissions> lastNode = null;
			int idx = 0;
			for(GraphNode<ID_TYPE,NodePermissions> node: recPath){
				if(lastNode!=null){
					idx++;	/* starting with 1 to have similar idx like like in times[] below 
					(where times[0] is no timespan but the time the timeCollector was added to the analyser)*/
					String timeSpanName = getTimeSpanName(lastNode, node);
					StringBuilder sb = new StringBuilder();
					sb.append("{label:")
					.append('"').append(timeSpanName).append('"')
					.append(", backgroundColor: ").append('"').append(getHexColorString(idx)).append('"')
					.append(", borderColor: \"#FFFFFF\"").append(", data: [");
					dataRows[idx] = sb;
				}
				lastNode = node;
			}
			
			int maxView = 300;
			
			// per TimeCollector that went this path
			boolean isFirstTC = true;
			idx = -1;
			for(long[] times: collectedTimes){
				idx++;
				if(idx>maxView)break;
				
				if(numberOfRecordedMilestones!=times.length){
					throw new IllegalStateException("Different number of nodes and times! "+numberOfRecordedMilestones+" nodes, "+times.length+" times!");
				}

				if(isFirstTC){
					isFirstTC = false;
				}else{
					sbLables.append(',');
				}
				sbLables.append('"').append(times[0]).append('"');
				
				
				boolean isFirst = true;
				
				// per milestone
				for(int t=1; t<times.length; t++){
					if(!isFirstTC){
						dataRows[t].append(',');
					}
					dataRows[t].append(String.valueOf(unit.convert(times[t], TimeUnit.NANOSECONDS)));
				}

			}

			for(int i=1; i<dataRows.length; i++){
				if(i>1)sbData.append(',');
				sbData.append(dataRows[i].toString()).append("]}");
			}
			
			String dataString = sbData.toString();
			String labelString = sbLables.toString();

			String description = analyzer.getNumberOfAddedTimeCollectors()+"TimeCollectors analyzed: times written in "+unit;
			
			String template = getTemplate();
			template = template.replaceFirst(placeholder._REPLACE_DESCRIPTION_.name(), 	description);
			template = template.replaceFirst(placeholder._REPLACE_LABELS_.name(), 		labelString);
			template = template.replaceFirst(placeholder._REPLACE_DATA_.name(), 		dataString);

			// TODO don't return for multiple paths
			return template;

//		}
//		
//		return null;
		
	}

	private String getHexColorString(int idx) {
		Color c = ColorGenerator.getColor(idx);
		
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}
	
	
}
