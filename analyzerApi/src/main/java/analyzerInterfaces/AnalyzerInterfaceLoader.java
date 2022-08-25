package analyzerInterfaces;

import java.util.List;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnalyzerInterfaceLoader {
	private static Logger logger = LogManager.getLogger(AnalyzerInterfaceLoader.class.getName());
	
	static ServiceLoader<Antipattern> antipatternLoader = ServiceLoader.load(Antipattern.class);
	static ServiceLoader<Metric> metricLoader = ServiceLoader.load(Metric.class);

	private static SortedMap<String, Antipattern> antipatternHashMap = null;
	private static SortedMap<String, Metric> metricHashMap = null;

	public static SortedMap<String, Antipattern> getAllAntipatterns() {
		if (antipatternHashMap == null) {
			antipatternHashMap = new TreeMap<String, Antipattern>();
			
			logger.trace(String.format("Start loading all implementations of %s.", Antipattern.class.getName()));
			List<Antipattern> antipatternList = StreamSupport.stream(antipatternLoader.spliterator(), false).collect(Collectors.toList());
			logger.trace(String.format("Loading %d implementations of %s.", antipatternList.size(), Antipattern.class.getName()));
			
			for (Antipattern antipattern : antipatternList) {				
				final String antipatternShortcut = antipattern.getShortcut();
				if(antipatternHashMap.containsKey(antipatternShortcut)) {
					logger.warn("Ignoring the antipattern with the shortcut " + antipatternShortcut 
					+ ", since another antipattern with this shortcut is already registered.");
				} else if(metricHashMap != null && metricHashMap.containsKey(antipatternShortcut)) {
					logger.warn("Ignoring the antipattern with the shortcut " + antipatternShortcut 
					+ ", since another metric with this shortcut is already registered.");
				} else {
					antipatternHashMap.put(antipatternShortcut, antipattern);
				}
			}
			logger.info(String.format("Kept %d out of %d implementations of %s.", antipatternHashMap.keySet().size(), antipatternList.size(), Antipattern.class.getName()));
			logger.trace(String.format("Finished loading all implementations of %s.", Antipattern.class.getName()));
		}
		return antipatternHashMap;
	}

	public static SortedMap<String, Metric> getAllMetrics() {
		if (metricHashMap == null) {
			metricHashMap = new TreeMap<String, Metric>();
			
			logger.trace(String.format("Start loading all implementations of %s.", Metric.class.getName()));
			List<Metric> metricList = StreamSupport.stream(metricLoader.spliterator(), false).collect(Collectors.toList());
			logger.trace(String.format("Loading %d implementations of %s.", metricList.size(), Metric.class.getName()));
			
			for (Metric metric : metricList) {
				final String metricShortcut = metric.getShortcut();
				if(metricHashMap.containsKey(metricShortcut)) {
					logger.warn("Ignoring the antipattern with the shortcut " + metricShortcut 
					+ ", since another antipattern with this shortcut is already registered.");
				} else if(antipatternHashMap != null && antipatternHashMap.containsKey(metricShortcut)) {
					logger.warn("Ignoring the metric with the shortcut " + metricShortcut 
					+ ", since another antipattern with this shortcut is already registered.");
				} else {
					metricHashMap.put(metricShortcut, metric);
				}
			}
			logger.info(String.format("Kept %d out of %d implementations of %s.", metricHashMap.keySet().size(), metricList.size(), Metric.class.getName()));
			logger.trace(String.format("Finished loading all implementations of %s.", Metric.class.getName()));
		}
		return metricHashMap;
	}

}
