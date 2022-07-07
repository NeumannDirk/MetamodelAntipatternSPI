package analyzerInterfaces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnalyzerInterfaceImplementationLoader {
	static ServiceLoader<Antipattern> antipatternLoader = ServiceLoader.load(Antipattern.class);

	public static List<Antipattern> getAntipatternsAnalyzer(boolean refresh) {
		if (refresh) {
			antipatternLoader.reload();
		}
		
		List<Antipattern> antipatternList = StreamSupport
				  .stream(antipatternLoader.spliterator(), false)
				  .collect(Collectors.toList());
		
		for(int id = 0; id < antipatternList.size(); id++) {
			antipatternList.get(id).setAntipatternID(id);
		}
		return antipatternList;
	}
	
	static ServiceLoader<Metric> metricLoader = ServiceLoader.load(Metric.class);

	public static List<Metric> getMetricsAnalyzer(boolean refresh) {
		if (refresh) {
			metricLoader.reload();
		}

		List<Metric> metricList = StreamSupport
				  .stream(metricLoader.spliterator(), false)
				  .collect(Collectors.toList());
		
		for(int id = 0; id < metricList.size(); id++) {
			metricList.get(id).setMetricID(id);
		}
		return metricList;
	}
	
}
