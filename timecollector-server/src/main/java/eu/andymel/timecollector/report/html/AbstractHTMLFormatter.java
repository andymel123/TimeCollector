package eu.andymel.timecollector.report.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.report.TimeSpanNameFormatter;
import eu.andymel.timecollector.report.analyzer.Analyzer;

public abstract class AbstractHTMLFormatter<ID_TYPE> implements TimeSpanNameFormatter<ID_TYPE>{

	private static final Logger LOG = LoggerFactory.getLogger(AbstractHTMLFormatter.class);

	private final Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer;
	
	public AbstractHTMLFormatter(Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> analyzer) {
		Objects.requireNonNull(analyzer, "'analyzer' is null");
		this.analyzer = analyzer;
	}
	
	private static String htmlTemplate;

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

	protected synchronized String getTemplate(){
		if(htmlTemplate==null){
			File f = getTemplateFile();
			try {
				htmlTemplate = readFile(f);
			} catch (IOException e) {
				LOG.error("Can't load template '"+f.getAbsolutePath()+"'!", e);
				htmlTemplate = "";
			}
		}
		return htmlTemplate;
	}
	
	protected abstract File getTemplateFile();

	public void writeToFile(File f, TimeUnit unit, boolean appendToFileIfExisting) throws IOException{
		writeFile(f, getHTMLString(unit), appendToFileIfExisting);
	}
	public void writeToFile(File f, TimeUnit unit) throws IOException{
		writeFile(f, getHTMLString(unit), false);
	}

	protected abstract String getHTMLString(TimeUnit unit);
	
	protected Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> getAnalyzer() {
		return analyzer;
	}

	
	@Override
	public String getTimeSpanName(GraphNode<ID_TYPE, NodePermissions> from, GraphNode<ID_TYPE, NodePermissions> to) {
		return String.format("%s => %s", from.getId(), to.getId());
	}

}
