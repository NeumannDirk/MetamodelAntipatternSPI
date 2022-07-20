package concurrentExecution;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import analyzerUtil.MetamodelLoader;
import mainanalyzer.MainAnalyzer;
import results.AnalysisResults;

/**
 * Class to compare sequential and concurrent execution of the metamodel
 * analysis.
 * 
 * @author DirkNeumann
 *
 */
public class ConcurrencyComparator {

	public static void main(String[] args) throws IOException {
		int repetitions = 10;

		long[] sequentialDurations = new long[repetitions];
		long[] parallelDurations = new long[repetitions];
		
		List<String> ecoreFiles = MetamodelLoader.findAllEcoreMetamodelsInDirectory("D:\\data\\ap_mm");

		for (int sequentialIndex = 0; sequentialIndex < repetitions; sequentialIndex++) {
			long start = System.currentTimeMillis();
			Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());
			MainAnalyzer.runSequential(ecoreFiles, resultMap);
			long end = System.currentTimeMillis();
			long duration = end -start;
			System.out.println("Sequential run " + sequentialIndex + ": " + duration);
			sequentialDurations[sequentialIndex] = duration;
		}

		for (int parallelIndex = 0; parallelIndex < repetitions; parallelIndex++) {
			long start = System.currentTimeMillis();
			Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());
			MainAnalyzer.runParallel(ecoreFiles, resultMap);
			long end = System.currentTimeMillis();
			long duration = end -start;
			System.out.println("Parallel run " + parallelIndex + ": " + duration);
			parallelDurations[parallelIndex] = duration;
		}

		double averageSequentialDuration = (0.001d * Arrays.stream(sequentialDurations).reduce(0, (a, b) -> a + b)) / repetitions;
		double averageParallelDuration = (0.001d * Arrays.stream(parallelDurations).reduce(0, (a, b) -> a + b)) / repetitions;

		System.out.println("Sequential runs on average took  " + String.format("{0:0.##}", averageSequentialDuration) + " sec.");
		System.out.println("Parallel runs on average took  " + String.format("{0:0.##}", averageParallelDuration) + " sec.");
	}
}
