package results;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import analyzerInterfaces.AnalyzerInterfaceImplementationLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;

public class AnalysisResults {

	private int metamodelIndex;

	public AnalysisResults(int metamodelIndex) {
		this.metamodelIndex = metamodelIndex;
	}

	public static String getHeaderCSV() {
		SortedMap<String, Metric> metricMap = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer();
		SortedMap<String, Antipattern> antipatternMap = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer();

		StringBuilder stringBuilder = new StringBuilder("Nr.");
		for (String abstractMetricShortcut : metricMap.keySet()) {
			stringBuilder.append(",").append(abstractMetricShortcut);
		}
		for (String abstractAntipatternShortcut : antipatternMap.keySet()) {
			stringBuilder.append(",").append(abstractAntipatternShortcut);
		}
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}

	public String getContentCSV() {
		SortedMap<String, Metric> metricMap = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer();
		SortedMap<String, Antipattern> antipatternMap = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer();

		StringBuilder stringBuilder = new StringBuilder().append(this.metamodelIndex);
		for (String abstractMetricShortcut : metricMap.keySet()) {
			stringBuilder.append(",").append(this.metrics.get(abstractMetricShortcut));
		}
		for (String abstractAntipatternShortcut : antipatternMap.keySet()) {
			stringBuilder.append(",").append(this.antipattern.get(abstractAntipatternShortcut));
		}
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}

	Map<String, Double> antipattern = new HashMap<String, Double>();
	Map<String, Double> metrics = new HashMap<String, Double>();

	public void addMetric(String id, double value) {
		metrics.put(id, value);
	}

	public void addAntipattern(String id, double value) {
		antipattern.put(id, value);
	}

	@Override
	public String toString() {

		SortedMap<String, Metric> metricMap = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer();
		SortedMap<String, Antipattern> antipatternMap = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer();

		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("\nMetrics:\n");
		for (String abstractMetricShortcut : metricMap.keySet()) {
			stringbuilder.append("\t").append(abstractMetricShortcut).append(": ")
					.append(this.metrics.get(abstractMetricShortcut)).append("\n");
		}
		stringbuilder.append("AbstractAntipattern:\n");
		for (String abstractAntipatternShortcut : antipatternMap.keySet()) {
			stringbuilder.append("\t").append(abstractAntipatternShortcut).append(": ")
					.append(this.antipattern.get(abstractAntipatternShortcut)).append("\n");
		}

		return stringbuilder.toString();
	}
}
