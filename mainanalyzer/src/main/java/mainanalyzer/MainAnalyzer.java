package mainanalyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AnalyzerInterfaceLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;
import analyzerUtil.MetamodelLoader;
import analyzerUtil.ParameterAndLoggerHelper;
import concurrentExecution.MetamodelAnalysisThread;
import metamodelUtil.MetamodelHelper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import results.AnalysisResults;

@Command(
		showDefaultValues = true,
		usageHelpAutoWidth = true,
		name = "MainAnalyzer",
		description = "%n" + "Project to improve the evaluation of ecore metamodel with respect to antipattern and metrics." + "%n",
		version="MetamodelAntipatternSPI v1.0",
		header = "%n" + "MetamodelAntipatternSPI%nPraktikum Ingenieursmäßige Software Entwicklung - SS 2022 - KIT" + "%n",
		footer = "%n" + "AUTHOR" + "%n" + "Dirk Neumann (https://github.com/NeumannDirk), uehpw(at)student.kit.edu"
				+ "%n%n" + "REPORTING BUGS" + "%n" + "Issue on https://github.com/NeumannDirk/MetamodelAntipatternSPI"
				+ "%n%n" + "COPYRIGHT" + "%n" + "Copyright (c) 2022 Creative Commons 4.0 BY-SA-NC"
				+ "%n%n" + "LAST UPDATE" + "%n" + "14.09.2022 - MetamodelAntipatternSPI v1.0",
		synopsisHeading = "SYNOPSIS" + "%n",
		requiredOptionMarker = '*')
/**
 * Main entry point for the analysis of metamodel with respect to the found antipattern and metrics.
 * 
 * @author DirkNeumann
 *
 */
public class MainAnalyzer {
	private static Logger logger = LogManager.getLogger(MainAnalyzer.class.getName());
	
	@Option(names = { "--v", "--version" }, versionHelp = true, description = "Print version information and exit")
	boolean versionRequested;

	@Option(names = {"--help", "--h"}, usageHelp = true, description = "Display this help and exit")
	boolean help;

	@Option(names = {"-log_level", "-log"}, description = "Set logger level: trace(0), debug(1), info(2), warn(3), error(4), fatal(5).", defaultValue = "3", paramLabel = "[0-5]")
	int loggerLevel = 3;

	@Option(names = "-header", description = "Print header into result csv", defaultValue = "false")
	boolean header;

	@Option(names = {"-sequential","-seq"}, description = "Execute sequentially")
	boolean sequential;

	@Option(names = {"-selection", "-sel"}, arity = "0..*", split = ",", description = "Selection and order of antipattern and metrics to analyze given by ID", paramLabel = "STR")
	List<String> selection = null;
	
	@Option(names = "-no_caching", description = "Deactivates caching during the metamodel analysis to improve performance")
	boolean noCaching;
	
	@Option(names = {"-no_progresss_bar", "-no_progress"}, description = "Deactivates printing the progress bar during execution.")
	boolean noProgressBar;

	@Option(names = {"-csv_separator", "-csv_sep"}, description = "Separator for the csv output", paramLabel = "STR", defaultValue = ",")
	private String csvSeparator = ",";
	
	@Option(names = {"-inputDirectory", "-in"}, required = true, description = "Directory from which all metamodels should be analysed", paramLabel = "DIR")
	private String inputDirectory = null;

	@Option(names = {"-outputDirectory", "-out"}, description = "Directory in which the result csv file schould be saved", paramLabel = "DIR")
	private String outputDirectory = null;

	private boolean checkCommandLineParameters() {
		boolean returnValue = true;
		if (this.loggerLevel < 0 || this.loggerLevel > 5) {
			logger.warn("Invalid logger level. Setting warn-level.");
		}
		ParameterAndLoggerHelper.setLoggerLevel(this.loggerLevel);
		if (this.inputDirectory == null) {
			logger.fatal("The parameter \"-inputDirectory\" must be set.");
			returnValue = false;
		}
		if (this.outputDirectory == null) {
			logger.info("It is recommended to set the parameter \"-outputDirectory\".");
		}
		return returnValue;
	}

	public static void main(String[] args) throws IOException {
		MainAnalyzer ma = new MainAnalyzer();
		CommandLine commandLine = new CommandLine(ma);
		commandLine.parseArgs(args);
		if (ma.help) {
			commandLine.usage(System.out);
		} else if(ma.versionRequested) {
			commandLine.printVersionHelp(System.out);
		} else {
			ma.start();
		}
	}

	public void start() throws IOException {
		logger.info("Starting new analysis task.");
		if (!checkCommandLineParameters()) {
			return;
		}
		AnalysisResults.setSeparator(this.csvSeparator);
		MetamodelHelper.useCaching = !this.noCaching;

		List<String> ecoreFiles = MetamodelLoader.findAllEcoreMetamodelsInDirectory(this.inputDirectory);
		logger.trace(String.format("Found %d potential ecore metamodels to analyze.", ecoreFiles.size()));

		Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());

		if (this.sequential) {
			runSequential(ecoreFiles, resultMap, this.noProgressBar);
		} else {
			runParallel(ecoreFiles, resultMap, this.noProgressBar);
		}
		logger.trace("Analysis completed. Printing results...");
		
		AnalysisResults.setShortcutSelection(selection);
		printResultsCSV(header, resultMap);
		logger.trace("Results stored. Done.");
	}
	
	public void runParallel(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap, boolean noProgressBar) {
		AtomicInteger lock = new AtomicInteger(0);
		
		int numberOfThreads = Runtime.getRuntime().availableProcessors();
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		logger.trace(String.format("Available Processors: %d", numberOfThreads));
		
		List<Callable<AnalysisResults>> tasks = new ArrayList<Callable<AnalysisResults>>(ecoreFiles.size());
		for (int ecoreFileNumber = 0; ecoreFileNumber < ecoreFiles.size(); ecoreFileNumber++) {
			tasks.add(new MetamodelAnalysisThread(ecoreFileNumber, ecoreFiles.get(ecoreFileNumber), selection,
					lock, ecoreFiles.size(), noProgressBar));
		}
		
		List<Future<AnalysisResults>> futures = null;
		
		try {
			futures = executorService.invokeAll(tasks);
			if(!noProgressBar) {
				//Sometimes the last tasks do npt print to the console anymore. If here all tasks are done,
				//just print an 100% prograss bar to also visualize that all tasks are done.
				System.out.print("\rProgress 100% [" + "=".repeat(49) + ">] " + ecoreFiles.size() + "/" + ecoreFiles.size());
			}
			executorService.shutdown();
		} catch (InterruptedException executionException) {
			logger.error(String.format("Error occurred during parallel analysis: %s", executionException.getMessage()));
		}
		
		for(Future<AnalysisResults> future : futures) {
			try {
				resultMap.put(future.get().metamodelIndex, future.get());
			} catch (InterruptedException | ExecutionException analysisException) {
				logger.error(String.format("Error occurred during result analysis: %s", analysisException.getMessage()));
			}
		}
	}

	public void runSequential(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap, boolean noProgressBar) {
		for (int ecoreFileNumber = 0; ecoreFileNumber < ecoreFiles.size(); ecoreFileNumber++) {

			String ecoreFile = ecoreFiles.get(ecoreFileNumber);
			AnalysisResults analysisResult = new AnalysisResults(ecoreFileNumber);
			resultMap.put(ecoreFileNumber, analysisResult);

			Optional<Resource> optionalMetamodel = MetamodelHelper.loadEcoreMetamodelFromFile(ecoreFile);

			optionalMetamodel.ifPresent(metamodel -> {
				List<Antipattern> antipatternToEvaluate = AnalyzerInterfaceLoader.getAllAntipatterns().values().stream()
						.filter(ap -> selection == null || selection.contains(ap.getShortcut()))
						.collect(Collectors.toList());
				List<Metric> metricsToEvaluate = AnalyzerInterfaceLoader.getAllMetrics().values().stream()
						.filter(m -> selection == null || selection.contains(m.getShortcut()))
						.collect(Collectors.toList());

				for (Antipattern antipattern : antipatternToEvaluate) {
					long evaluationResult = antipattern.evaluate(metamodel);
					analysisResult.addAntipattern(antipattern.getShortcut(), evaluationResult);
				}
				for (Metric metric : metricsToEvaluate) {
					double evaluationResult = metric.evaluate(metamodel);
					analysisResult.addMetric(metric.getShortcut(), evaluationResult);
				}
			});
			if (!noProgressBar && this.loggerLevel > 1) {
				this.printSequentialProgressBar(ecoreFileNumber, ecoreFiles.size());
			}
		}
	}

	private void printSequentialProgressBar(int current, int max) {
		double percent100 = (current * 100.0) / max;
		int percent50 = (int) Math.round(percent100 / 2);
		StringBuilder stringBuilder = new StringBuilder(String.format("\rProgress %3.0f%% [", percent100));
		stringBuilder.append("=".repeat(percent50 == 0 ? 0 : percent50 - 1) + ">"
				+ " ".repeat(percent50 == 0 ? 49 : 50 - percent50));
		String template = "] %" + String.valueOf(max).length() + "d/%d";
		stringBuilder.append(String.format(template, current, max));
		System.out.print(stringBuilder.toString());
	}

	private void printResultsCSV(boolean printHeader, Map<Integer, AnalysisResults> resultMap) {

		String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String fileName = "metamodel_analysis_results.csv";

		FileWriter fileWriter;
		try {
			String completeFileName = date + "_" + fileName;
			if (outputDirectory != null) {
				completeFileName = outputDirectory + completeFileName;
			}
			fileWriter = new FileWriter(completeFileName);
			if (printHeader) {
				fileWriter.write(AnalysisResults.getHeaderCSV());
			}
			for (int resultIndex = 0; resultIndex < resultMap.size(); resultIndex++) {
				fileWriter.write(resultMap.get(resultIndex).getContentCSV());
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
		}
	}
}