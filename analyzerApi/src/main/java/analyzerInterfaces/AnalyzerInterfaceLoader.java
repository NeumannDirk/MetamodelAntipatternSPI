package analyzerInterfaces;

import java.util.List;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnalyzerInterfaceLoader {
	
	static ServiceLoader<Antipattern> antipatternLoader = ServiceLoader.load(Antipattern.class);
	static ServiceLoader<Metric> metricLoader = ServiceLoader.load(Metric.class);

	private static SortedMap<String, Antipattern> antipatternHashMap = null;
	private static SortedMap<String, Metric> metricHashMap = null;

	public static SortedMap<String, Antipattern> getAllAntipatterns() {
		if (antipatternHashMap == null) {
			antipatternHashMap = new TreeMap<String, Antipattern>();
			
			List<Antipattern> antipatternList = StreamSupport.stream(antipatternLoader.spliterator(), false).collect(Collectors.toList());
			
			for (Antipattern antipattern : antipatternList) {				
				final String antipatternShortcut = antipattern.getShortcut();
				if(antipatternHashMap.containsKey(antipatternShortcut)) {
				} else if(metricHashMap != null && metricHashMap.containsKey(antipatternShortcut)) {
				} else {
					antipatternHashMap.put(antipatternShortcut, antipattern);
				}
			}
		}
		return antipatternHashMap;
	}

	public static SortedMap<String, Metric> getAllMetrics() {
		if (metricHashMap == null) {
			metricHashMap = new TreeMap<String, Metric>();
			
			List<Metric> metricList = StreamSupport.stream(metricLoader.spliterator(), false).collect(Collectors.toList());
			
			for (Metric metric : metricList) {
				final String metricShortcut = metric.getShortcut();
				if(metricHashMap.containsKey(metricShortcut)) {
				} else if(antipatternHashMap != null && antipatternHashMap.containsKey(metricShortcut)) {
				} else {
					metricHashMap.put(metricShortcut, metric);
				}
			}
		}
		return metricHashMap;
	}

}
