package analyzerInterfaces;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnalyzerInterfaceImplementationLoader {
	static ServiceLoader<Antipattern> antipatternLoader = ServiceLoader.load(Antipattern.class);
	static ServiceLoader<Metric> metricLoader = ServiceLoader.load(Metric.class);

	private static List<Antipattern> antipatternList = null;
	private static List<Metric> metricList = null;

	public static void refresh() {
		antipatternLoader.reload();
		metricLoader.reload();

		antipatternList = null;
		metricList = null;
	}

	public static List<Antipattern> getAntipatternsAnalyzer() {
		if (antipatternList == null) {
			antipatternList = StreamSupport.stream(antipatternLoader.spliterator(), false).collect(Collectors.toList());

			for (int id = 0; id < antipatternList.size(); id++) {
				antipatternList.get(id).setAntipatternID(id);
			}
		}
		return antipatternList;
	}

	public static List<Metric> getMetricsAnalyzer() {
		if (metricList == null) {
			metricList = StreamSupport.stream(metricLoader.spliterator(), false).collect(Collectors.toList());

			for (int id = 0; id < metricList.size(); id++) {
				metricList.get(id).setMetricID(id);
			}
		}
		return metricList;
	}

}
