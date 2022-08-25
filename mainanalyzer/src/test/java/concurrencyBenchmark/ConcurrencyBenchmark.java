package concurrencyBenchmark;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import analyzerUtil.MetamodelLoader;
import analyzerUtil.ParameterAndLoggerHelper;
import mainanalyzer.MainAnalyzer;
import results.AnalysisResults;

/**
 * Class to compare sequential and concurrent execution of the metamodel
 * analysis.
 * 
 * @author DirkNeumann
 *
 */
public class ConcurrencyBenchmark {
	private static Logger logger = LogManager.getLogger(ConcurrencyBenchmark.class.getName());

	@Test
	public void testBenchmark() {
		String ecoreFileDirectory = "D:/data/ap_mm";
		int repetitions = 10;
		
		ParameterAndLoggerHelper.setLoggerLevel(Level.INFO);

		long[] sequentialDurations = new long[repetitions];
		long[] parallelDurations = new long[repetitions];
		
		List<String> ecoreFiles = MetamodelLoader.findAllEcoreMetamodelsInDirectory(ecoreFileDirectory);

		for (int sequentialIndex = 0; sequentialIndex < repetitions; sequentialIndex++) {
			long start = System.currentTimeMillis();
			Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());
			new MainAnalyzer().runSequential(ecoreFiles, resultMap);
			long end = System.currentTimeMillis();
			long duration = end -start;
			logger.info(String.format("Sequential run %d: %d", sequentialIndex, duration));
			sequentialDurations[sequentialIndex] = duration;
		}

		for (int parallelIndex = 0; parallelIndex < repetitions; parallelIndex++) {
			long start = System.currentTimeMillis();
			Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());
			new MainAnalyzer().runParallel(ecoreFiles, resultMap);
			long end = System.currentTimeMillis();
			long duration = end -start;
			logger.info(String.format("Parallel run %d: %d", parallelIndex, duration));
			parallelDurations[parallelIndex] = duration;
		}

		double averageSequentialDuration = (0.001d * Arrays.stream(sequentialDurations).reduce(0, (a, b) -> a + b)) / repetitions;
		double averageParallelDuration = (0.001d * Arrays.stream(parallelDurations).reduce(0, (a, b) -> a + b)) / repetitions;
		
		logger.info(String.format("Sequential runs on average took %.2f sec.", averageSequentialDuration));
		logger.info(String.format("Parallel runs on average took %.2f sec.", averageParallelDuration));
	}
}
