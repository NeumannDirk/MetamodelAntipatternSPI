package results;

import java.util.HashMap;
import java.util.List;
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
	
	static SortedMap<String, Metric> metricMap = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer();
	static SortedMap<String, Antipattern> antipatternMap = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer();
	
	static List<String> shortcutOrder;
	
	public static void setShortcutOrder(List<String> shortcutOrder) {
		AnalysisResults.shortcutOrder = checkShortCuts(shortcutOrder);
	}

	Map<String, Double> antipattern = new HashMap<String, Double>();
	Map<String, Double> metrics = new HashMap<String, Double>();
	
	public static String getHeaderCSV() {
		return (shortcutOrder == null) ? getUnorderedHeaderCSV() : getOrderedHeaderCSV();
	}

	private static List<String> checkShortCuts(List<String> shortcutOrder) {
		return shortcutOrder.stream().filter(shortcut -> metricMap.keySet().contains(shortcut) || antipatternMap.keySet().contains(shortcut)).toList();
	}
	
	private static String getOrderedHeaderCSV() {
		
		List<String> unorderedMetrics = metricMap.keySet().stream().filter(metricShortcut -> !shortcutOrder.contains(metricShortcut)).toList();
		List<String> unorderedAntipattern = antipatternMap.keySet().stream().filter(antipatternShortcut -> !shortcutOrder.contains(antipatternShortcut)).toList();				

		StringBuilder stringBuilder = new StringBuilder("Nr.");
		//Add the shortcuts in the order the user wanted
		for (String orderedShortcut : shortcutOrder) {
			stringBuilder.append(",").append(orderedShortcut);
		}		
		//Add the remaining metric-shortcuts
		for (String metricShortcut : unorderedMetrics) {
			stringBuilder.append(",").append(metricShortcut);
		}
		//Add the remaining antipattern-shortcuts
		for (String antipatternShortcut : unorderedAntipattern) {
			stringBuilder.append(",").append(antipatternShortcut);
		}
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}

	private static String getUnorderedHeaderCSV() {

		StringBuilder stringBuilder = new StringBuilder("Nr.");
		for (String metricShortcut : metricMap.keySet()) {
			stringBuilder.append(",").append(metricShortcut);
		}
		for (String antipatternShortcut : antipatternMap.keySet()) {
			stringBuilder.append(",").append(antipatternShortcut);
		}
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}
	
	public String getContentCSV() {
		return (shortcutOrder == null) ? getUnorderedContentCSV() : getOrderedContentCSV();		
	}
	
	private String getOrderedContentCSV() {		
		List<String> orderedMetrics = metricMap.keySet().stream().filter(metricShortcut -> shortcutOrder.contains(metricShortcut)).toList();
		List<String> orderedAntipattern = antipatternMap.keySet().stream().filter(antipatternShortcut -> shortcutOrder.contains(antipatternShortcut)).toList();
		List<String> unorderedMetrics = metricMap.keySet().stream().filter(metricShortcut -> !shortcutOrder.contains(metricShortcut)).toList();
		List<String> unorderedAntipattern = antipatternMap.keySet().stream().filter(antipatternShortcut -> !shortcutOrder.contains(antipatternShortcut)).toList();
		
		StringBuilder stringBuilder = new StringBuilder().append(this.metamodelIndex);

		//Adding the ordered metrics and antipattern		
		for (String metricShortcut : orderedMetrics) {
			stringBuilder.append(",").append(this.metrics.get(metricShortcut));
		}
		for (String antipatternShortcut : orderedAntipattern) {
			stringBuilder.append(",").append(this.antipattern.get(antipatternShortcut));
		}
		
		//Adding the unordered metrics and antipattern
		for (String metricShortcut : unorderedMetrics) {
			stringBuilder.append(",").append(this.metrics.get(metricShortcut));
		}
		for (String antipatternShortcut : unorderedAntipattern) {
			stringBuilder.append(",").append(this.antipattern.get(antipatternShortcut));
		}
		
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}

	private String getUnorderedContentCSV() {
		StringBuilder stringBuilder = new StringBuilder().append(this.metamodelIndex);
		for (String metricShortcut : metricMap.keySet()) {
			stringBuilder.append(",").append(this.metrics.get(metricShortcut));
		}
		for (String antipatternShortcut : antipatternMap.keySet()) {
			stringBuilder.append(",").append(this.antipattern.get(antipatternShortcut));
		}
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}


	public void addMetric(String id, double value) {
		metrics.put(id, value);
	}

	public void addAntipattern(String id, double value) {
		antipattern.put(id, value);
	}

	@Override
	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("\nMetrics:\n");
		for (String metricShortcut : metricMap.keySet()) {
			stringbuilder.append("\t").append(metricShortcut).append(": ")
					.append(this.metrics.get(metricShortcut)).append("\n");
		}
		stringbuilder.append("Antipattern:\n");
		for (String antipatternShortcut : antipatternMap.keySet()) {
			stringbuilder.append("\t").append(antipatternShortcut).append(": ")
					.append(this.antipattern.get(antipatternShortcut)).append("\n");
		}

		return stringbuilder.toString();
	}
}
