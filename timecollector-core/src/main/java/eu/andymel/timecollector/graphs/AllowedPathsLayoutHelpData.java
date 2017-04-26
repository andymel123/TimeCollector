package eu.andymel.timecollector.graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * I save data about how the nodes and path methods were used in the builder to reuse
 * that info when I have to visualize the allowed graph for example in my monitoring gui
 * 
 * @author andymatic
 */
public class AllowedPathsLayoutHelpData {

	private final List<String> nodes;
	private final List<List<String>> paths;

	private final List<String> viewOnNodes;
	private final List<List<String>> viewOnPaths;
	
	public AllowedPathsLayoutHelpData(List<String> nodes) {
		this.nodes = new ArrayList<>(nodes);
		this.paths = new ArrayList<>();
		this.viewOnNodes = Collections.unmodifiableList(nodes);
		this.viewOnPaths = Collections.unmodifiableList(paths);
	}
	
	void addPath(List<String> path){
		this.paths.add(new ArrayList<>(path));
	}
	
	public List<String> getNodes() {
		return viewOnNodes;
	}
	
	public List<List<String>> getPaths() {
		return viewOnPaths;
	}
}
