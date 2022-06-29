package analyzerInterfaces;

import java.util.Iterator;
import java.util.ServiceLoader;

public class AnalyzerInterfaceImplementationLoader {
	static ServiceLoader<AntiPattern> antipatternLoader = ServiceLoader.load(AntiPattern.class);

	public static Iterator<AntiPattern> getAntipatternsAnalyzer(boolean refresh) {
		if (refresh) {
			antipatternLoader.reload();
		}
		return antipatternLoader.iterator();
	}
	
	static ServiceLoader<Metric> metricLoader = ServiceLoader.load(Metric.class);

	public static Iterator<Metric> getMetricsAnalyzer(boolean refresh) {
		if (refresh) {
			metricLoader.reload();
		}
		return metricLoader.iterator();
	}
}
