package results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import analyzerInterfaces.AnalyzerInterfaceLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;

public class AnalysisResults {
	
	static SortedMap<String, Metric> metricMap = AnalyzerInterfaceLoader.getAllMetrics();
	static SortedMap<String, Antipattern> antipatternMap = AnalyzerInterfaceLoader.getAllAntipatterns();
	
	private static String separator = ",";
	
	public static void setSeparator(String separator) {
		AnalysisResults.separator = separator;
	}
	
	private static List<String> shortcutSelection;
	
	public static void setShortcutSelection(List<String> shortcutSelection) {
		if(shortcutSelection != null && shortcutSelection.size() > 0) {
			AnalysisResults.shortcutSelection = checkShortCuts(shortcutSelection);
		}
	}
	
	public static String getHeaderCSV() {
		return (shortcutSelection == null) ? getAllHeaderCSV() : getSelectionHeaderCSV();
	}

	private static List<String> checkShortCuts(List<String> shortcutOrder) {
		return shortcutOrder.stream().filter(shortcut -> metricMap.keySet().contains(shortcut) || antipatternMap.keySet().contains(shortcut)).toList();
	}
	
	private static String getSelectionHeaderCSV() {
		StringBuilder stringBuilder = new StringBuilder("Nr.");
		//Add the selected shortcuts in the order the user wanted
		for (String shortcut : shortcutSelection) {
			stringBuilder.append(AnalysisResults.separator).append(shortcut);
		}
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}

	private static String getAllHeaderCSV() {

		StringBuilder stringBuilder = new StringBuilder("Nr.");
		for (String metricShortcut : metricMap.keySet()) {
			stringBuilder.append(AnalysisResults.separator).append(metricShortcut);
		}
		for (String antipatternShortcut : antipatternMap.keySet()) {
			stringBuilder.append(AnalysisResults.separator).append(antipatternShortcut);
		}
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}
	
	public final int metamodelIndex;

	public AnalysisResults(int metamodelIndex) {
		this.metamodelIndex = metamodelIndex;
	}

	Map<String, Long> antipattern = new HashMap<String, Long>();
	Map<String, Double> metrics = new HashMap<String, Double>();
	
	public String getContentCSV() {
		return (shortcutSelection == null) ? getAllContentCSV() : getSelectionContentCSV();		
	}
	
	private String getSelectionContentCSV() {		
		List<String> selectedMetrics = metricMap.keySet().stream().filter(metricShortcut -> shortcutSelection.contains(metricShortcut)).toList();
		List<String> selectedAntipattern = antipatternMap.keySet().stream().filter(antipatternShortcut -> shortcutSelection.contains(antipatternShortcut)).toList();
		
		StringBuilder stringBuilder = new StringBuilder().append(this.metamodelIndex);

		//Adding the selected metrics and antipattern		
		for (String metricShortcut : selectedMetrics) {
			stringBuilder.append(AnalysisResults.separator).append(this.metrics.get(metricShortcut));
		}
		for (String antipatternShortcut : selectedAntipattern) {
			stringBuilder.append(AnalysisResults.separator).append(this.antipattern.get(antipatternShortcut));
		}
		
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}
	
	private String contentString = null;
	
	/**
	 * Prepares the content String beforehand to improve the printing of results. 
	 */
	public void prepareContentString() {
		StringBuilder stringBuilder = new StringBuilder().append(this.metamodelIndex);
		for (String metricShortcut : metricMap.keySet()) {
			stringBuilder.append(AnalysisResults.separator).append(this.metrics.get(metricShortcut));
		}
		for (String antipatternShortcut : antipatternMap.keySet()) {
			stringBuilder.append(AnalysisResults.separator).append(this.antipattern.get(antipatternShortcut));
		}
		stringBuilder.append(System.lineSeparator());
		this.contentString = stringBuilder.toString();
	}

	private String getAllContentCSV() {
		if(contentString == null) {
			this.prepareContentString();
		}
		return this.contentString;
	}


	public void addMetric(String id, double value) {
		metrics.put(id, value);
	}

	public void addAntipattern(String id, long value) {
		antipattern.put(id, value);
	}
}
