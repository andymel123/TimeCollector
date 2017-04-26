package eu.andymel.timecollector.report.analyzer;

import static org.junit.Assert.assertEquals;

import java.time.Clock;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.TestClockIncrementBy1;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.PermissionNode;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath.AnalyzerEachEntry;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachTest.SmallTestMilestones;

public class AnalyzerEachTest {

	
	private Clock analyzerClock;
	private AllowedPathsGraph<SmallTestMilestones> allowedGraph;
	
	enum SmallTestMilestones{
		MS1,MS2,MS3,MS4
	}
	
	@Before
	public void setup(){
		this.analyzerClock = new TestClockIncrementBy1();
		
		PermissionNode<SmallTestMilestones> n1 = PermissionNode.create(SmallTestMilestones.MS1);
		PermissionNode<SmallTestMilestones> n2 = PermissionNode.create(SmallTestMilestones.MS2);
		PermissionNode<SmallTestMilestones> n3 = PermissionNode.create(SmallTestMilestones.MS3);
		PermissionNode<SmallTestMilestones> n4 = PermissionNode.create(SmallTestMilestones.MS4);
		
		this.allowedGraph = AllowedPathsGraph
			.nodes(n1, n2, n3, n4)
			.path(n1,n2,n4)
			.path(n1,n3,n4)
			.build();
		
	}
	
	@Test
	public void test2DifferentPaths() {

		AnalyzerEachPath<SmallTestMilestones> analyzer = AnalyzerEachPath.create(analyzerClock);
		
		// path1
		TimeCollectorWithPath<SmallTestMilestones> tc = TimeCollectorWithPath.createWithPath(new TestClockIncrementBy1(), allowedGraph);
		tc.saveTime(SmallTestMilestones.MS1);
		tc.saveTime(SmallTestMilestones.MS2);
		tc.saveTime(SmallTestMilestones.MS4);
		analyzer.addCollector(tc);
		
		// path2
		tc = TimeCollectorWithPath.createWithPath(new TestClockIncrementBy1(), allowedGraph);
		tc.saveTime(SmallTestMilestones.MS1);
		tc.saveTime(SmallTestMilestones.MS3);
		tc.saveTime(SmallTestMilestones.MS4);
		analyzer.addCollector(tc);
		
		Collection<SimpleEntry<AllowedPathsGraph<SmallTestMilestones>,List<AnalyzerEachEntry<SmallTestMilestones>>>> data = analyzer.getCopyOFData();

		if(data.size()!=1){
			throw new RuntimeException("Not yet implemented!");
		}
		List<AnalyzerEachEntry<SmallTestMilestones>> all = data.iterator().next().getValue();
		assertEquals(2, all.size());
		
		long sumofInsertionTimes = 0;
		// iterate through recorded paths  
		for(AnalyzerEachEntry<SmallTestMilestones> e:all){
			List<GraphNode<SmallTestMilestones,NodePermissions>> recPath = e.getRecPath();
			List<long[]> listOfTimes = e.getCollectedTimes();
			
			boolean isFirstTCPath = recPath.get(1).getId()==SmallTestMilestones.MS2;
			
			// one tc was added (per recorded path)  
			assertEquals(1, listOfTimes.size());
			long[] times = listOfTimes.get(0);
			assertEquals(3, times.length); 	// 2 timespans (idx 1&2) and the time the tc was added (idx 0)

			// test times (they are deterministic because of my TestClock)
			if(isFirstTCPath){
				assertEquals(0, times[0]);
			}else{
				assertEquals(1, times[0]);	
			}
			sumofInsertionTimes += times[0];
			assertEquals(1000000, times[1]);// the timespan between two savetimes always has to be 1 ms = 1_000_000 nanos with my TestClock
			assertEquals(1000000, times[2]);// the timespan between two savetimes always has to be 1 ms = 1_000_000 nanos with my TestClock
			
			assertEquals(3, recPath.size());
			
		}
		assertEquals(1, sumofInsertionTimes); // one insertion time has to be 0 and one has to be 1, so sum = 1
		
	}

}
