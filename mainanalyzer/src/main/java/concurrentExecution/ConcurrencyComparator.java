package concurrentExecution;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import analyzerUtil.MetamodelLoader;
import analyzerUtil.ParameterAndLoggerHelper;
import mainanalyzer.MainAnalyzer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import results.AnalysisResults;

@Command(
		showDefaultValues = true,
		usageHelpAutoWidth = true,
		name = "ConcurrencyComparator",
		description = "%n" + "Benchmark to compare metamodel analysis with and without parallelization on file level." + "%n",
		header = "%n" + "MetamodelAntipatternSPI%nPraktikum Ingenieursmäßige Software Entwicklung - SS 2022 - KIT" + "%n",
		footer = "%n" + "AUTHOR" + "%n" + "Dirk Neumann (https://github.com/NeumannDirk), uehpw(at)student.kit.edu"
				+ "%n%n" + "REPORTING BUGS" + "%n" + "Issue on https://github.com/NeumannDirk/MetamodelAntipatternSPI"
				+ "%n%n" + "COPYRIGHT" + "%n" + "Copyright (c) 2022 Creative Commons 4.0 BY-SA-NC"
				+ "%n%n" + "LAST UPDATE" + "%n" + "07.09.2022",
		synopsisHeading = "SYNOPSIS" + "%n",
		requiredOptionMarker = '*')
/**
 * Class to compare sequential and concurrent execution of the metamodel
 * analysis.
 * 
 * @author DirkNeumann
 *
 */
public class ConcurrencyComparator {
	private static Logger logger = LogManager.getLogger(ConcurrencyComparator.class.getName());
	
	@Option(names = {"--help", "--h"}, usageHelp = true, description = "Display this help and exit")
	boolean help;

	@Option(names = {"-inputDirectory", "-in"}, required = true, description = "Directory from which all metamodels should be analysed", paramLabel = "DIR")
	private String inputDirectory = null;

	@Option(names = {"-repetitions", "-rep"}, description = "Number of repetitions of each execution version for comparisson", defaultValue = "10")
	private int repetitions = 10;

	public static void main(String[] args) {
		ConcurrencyComparator cc = new ConcurrencyComparator();
		CommandLine commandLine = new CommandLine(cc);
		commandLine.parseArgs(args);
		if (cc.help) {
			commandLine.usage(System.out);
		} else {
			cc.start();
		}
	}
	
	public void start() {		
		ParameterAndLoggerHelper.setLoggerLevel(Level.INFO);

		long[] sequentialDurations = new long[this.repetitions];
		long[] parallelDurations = new long[this.repetitions];
		
		List<String> ecoreFiles = MetamodelLoader.findAllEcoreMetamodelsInDirectory(this.inputDirectory);
		logger.info(String.format("Found %d potential ecore metamodels to analyze.", ecoreFiles.size()));

		for (int sequentialIndex = 0; sequentialIndex < this.repetitions; sequentialIndex++) {
			long start = System.currentTimeMillis();
			Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());
			new MainAnalyzer().runSequential(ecoreFiles, resultMap);
			long end = System.currentTimeMillis();
			long duration = end -start;
			//newline behind progress bar
			System.out.println();
			String status = String.format("Sequential run %2d/%2d took %s", sequentialIndex + 1, this.repetitions, convertSeconds(Math.round(0.001d * duration)));
			System.out.println(status);
			logger.info(status);
			sequentialDurations[sequentialIndex] = duration;
		}

		for (int parallelIndex = 0; parallelIndex < this.repetitions; parallelIndex++) {
			long start = System.currentTimeMillis();
			Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());
			new MainAnalyzer().runParallel(ecoreFiles, resultMap);
			long end = System.currentTimeMillis();
			long duration = end -start;
			//newline behind progress bar
			System.out.println();
			String status = String.format("Parallel run %2d/%2d took %s", parallelIndex + 1, this.repetitions, convertSeconds(Math.round(0.001d * duration)));
			System.out.println(status);
			logger.info(status);
			parallelDurations[parallelIndex] = duration;
		}

		long averageSequentialDuration = Math.round((0.001d * Arrays.stream(sequentialDurations).reduce(0, (a, b) -> a + b)) / this.repetitions);
		long averageParallelDuration = Math.round((0.001d * Arrays.stream(parallelDurations).reduce(0, (a, b) -> a + b)) / this.repetitions);

		//newline behind progress bar
		System.out.println();
		System.out.println(String.format("Sequential runs on average took %s.", convertSeconds(averageSequentialDuration) ));
		System.out.println(String.format("Parallel runs on average took %s.", convertSeconds(averageParallelDuration)));
		logger.info(System.lineSeparator());
		logger.info(String.format("Sequential runs on average took %s.", convertSeconds(averageSequentialDuration)));
		logger.info(String.format("Parallel runs on average took %s.", convertSeconds(averageParallelDuration)));
		
	}
	
	public static String convertSeconds(long seconds) {
	    long h = seconds/ 3600;
	    long m = (seconds % 3600) / 60;
	    long s = seconds % 60;
	    String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
	    String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
	    String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
	    return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
	}
}
