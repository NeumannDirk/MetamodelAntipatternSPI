package results;

import java.util.HashMap;
import java.util.List;

import analyzerInterfaces.AnalyzerInterfaceImplementationLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;

public class AnalysisResults {
	
	private int metamodelIndex;
	
	public AnalysisResults(int metamodelIndex) {
		this.metamodelIndex = metamodelIndex;
	}

	public static String getHeaderCSV() {
		List<Antipattern> antipatternList = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer();
		List<Metric> metricList = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer();
		
		StringBuilder stringBuilder = new StringBuilder("Nr.");
		for (Metric metric : metricList) {
			stringBuilder.append(",").append(metric.shortcut);
		}		
		for (Antipattern antipattern : antipatternList) {
			stringBuilder.append(",").append(antipattern.shortcut);
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}
	
	public String getContentCSV() {
		List<Antipattern> antipatternList = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer();
		List<Metric> metricList = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer();
		
		StringBuilder stringBuilder = new StringBuilder().append(this.metamodelIndex);
		for (int mIndex = 0; mIndex < this.metrics.size(); mIndex++) {
			Metric metric = metricList.get(mIndex);
			double result = this.metrics.get(metric.getMetricID());
			stringBuilder.append(",").append(result);
		}
		
		for (int apIndex = 0; apIndex < this.antipattern.size(); apIndex++) {
			Antipattern antipattern = antipatternList.get(apIndex);
			double result = this.antipattern.get(antipattern.getAntipatternID());
			stringBuilder.append(",").append(result);
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

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
		
		List<Antipattern> antipatternList = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer();
		List<Metric> metricList = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer();
		
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("\nMetrics:\n");
		
		for (int mIndex = 0; mIndex < this.metrics.size(); mIndex++) {
			Metric metric = metricList.get(mIndex);
			double result = this.metrics.get(metric.getMetricID());
			stringbuilder.append("\t").append(metric.shortcut).append(": ").append(result).append("\n");
		}
		
		stringbuilder.append("Antipattern:\n");

		for (int apIndex = 0; apIndex < this.antipattern.size(); apIndex++) {
			Antipattern antipattern = antipatternList.get(apIndex);
			double result = this.antipattern.get(antipattern.getAntipatternID());
			stringbuilder.append("\t").append(antipattern.shortcut).append(": ").append(result).append("\n");
		}

		return stringbuilder.toString();
	}
}
