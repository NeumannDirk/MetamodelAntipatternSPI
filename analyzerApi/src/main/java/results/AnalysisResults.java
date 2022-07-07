package results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import analyzerInterfaces.AnalyzerInterfaceImplementationLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;

public class AnalysisResults {
	
	HashMap<Integer, Double> antipattern = new HashMap<Integer, Double>();
	HashMap<Integer, Double> metrics = new HashMap<Integer, Double>();
	
	public void addMetric(int id, double value) {
		metrics.put(id, value);
	}
	
	public void addAntipattern(int id, double value) {
		antipattern.put(id, value);
	}
	
	@Override 
	public String toString() {
		StringBuilder stringbuilder = new StringBuilder("Antipattern:\n");
		
		List<Antipattern> antipatternList = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer(false);
		List<Metric> metricList = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer(false);
		
		for(int apIndex = 0; apIndex < this.antipattern.size(); apIndex++) {
			double result = this.antipattern.get(apIndex);
			final int compareValue = apIndex;
			Antipattern antipattern = antipatternList.stream().filter(ap -> ap.getAntipatternID() == compareValue).findFirst().get();
			
			stringbuilder.append("\t").append(antipattern.shortcut).append(": ").append(result).append("\n");
		}
		
		stringbuilder.append("\nMetrics:\n");
		
		for(int mIndex = 0; mIndex < this.metrics.size(); mIndex++) {
			double result = this.metrics.get(mIndex);
			final int compareValue = mIndex;
			Metric metric = metricList.stream().filter(m -> m.getMetricID() == compareValue).findFirst().get();
			
			stringbuilder.append("\t").append(metric.shortcut).append(": ").append(result).append("\n");
		}
		return stringbuilder.toString();
	}
}
