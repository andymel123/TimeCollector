package eu.andymel.timecollector.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;

public abstract class AbstractHTMLFileAnalyzer<ID_TYPE> extends AbstractPathAnalyzer<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractHTMLFileAnalyzer.class);

	// TODO hash and save mutliple allowedgraphs for Dashboard views 
	private AllowedPathsGraph<ID_TYPE> allowedGraph;
	
	
	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		// TODO make async!

		if(allowedGraph==null)allowedGraph = tc.getAllowedGraph();
		super.addCollector(tc);
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
