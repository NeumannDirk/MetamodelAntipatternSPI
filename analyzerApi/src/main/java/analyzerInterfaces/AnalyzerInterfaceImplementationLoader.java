package analyzerInterfaces;

import java.util.List;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnalyzerInterfaceImplementationLoader {
	static ServiceLoader<Antipattern> antipatternLoader = ServiceLoader.load(Antipattern.class);
	static ServiceLoader<Metric> metricLoader = ServiceLoader.load(Metric.class);

	private static SortedMap<String, Antipattern> antipatternHashMap = null;
	private static SortedMap<String, Metric> metricHashMap = null;

	public static void refresh() {
		antipatternLoader.reload();
		metricLoader.reload();

		antipatternHashMap = null;
		metricHashMap = null;
	}

	public static SortedMap<String, Antipattern> getAntipatternsAnalyzer() {
		if (antipatternHashMap == null) {
			antipatternHashMap = new TreeMap<String, Antipattern>();
			
			List<Antipattern> antipatternList = StreamSupport.stream(antipatternLoader.spliterator(), false).collect(Collectors.toList());
			
			for (Antipattern antipattern : antipatternList) {				
				final String antipatternShortcut = antipattern.getShortcut();
				if(antipatternHashMap.containsKey(antipatternShortcut)) {
					System.out.println("Ignoring the antipattern with the shortcut " + antipatternShortcut 
					+ ", since another antipattern with this shortcut is already registered.");
				} else if(metricHashMap != null && metricHashMap.containsKey(antipatternShortcut)) {
					System.out.println("Ignoring the antipattern with the shortcut " + antipatternShortcut 
					+ ", since another metric with this shortcut is already registered.");
				} else {
					antipatternHashMap.put(antipatternShortcut, antipattern);
				}
			}
		}
		return antipatternHashMap;
	}

	public static SortedMap<String, Metric> getMetricsAnalyzer() {
		if (metricHashMap == null) {
			metricHashMap = new TreeMap<String, Metric>();
			
			List<Metric> metricList = StreamSupport.stream(metricLoader.spliterator(), false).collect(Collectors.toList());
			
			for (Metric metric : metricList) {
				final String metricShortcut = metric.getShortcut();
				if(metricHashMap.containsKey(metricShortcut)) {
					System.out.println("Ignoring the antipattern with the shortcut " + metricShortcut 
					+ ", since another antipattern with this shortcut is already registered.");
				} else if(antipatternHashMap != null && antipatternHashMap.containsKey(metricShortcut)) {
					System.out.println("Ignoring the metric with the shortcut " + metricShortcut 
					+ ", since another antipattern with this shortcut is already registered.");
				} else {
					metricHashMap.put(metricShortcut, metric);
				}
			}
		}
		return metricHashMap;
	}

}
