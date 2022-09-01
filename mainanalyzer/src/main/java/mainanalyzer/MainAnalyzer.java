package mainanalyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import picocli.CommandLine.Option;
import results.AnalysisResults;

public class MainAnalyzer {
	private static Logger logger = LogManager.getLogger(MainAnalyzer.class.getName());

	private final static String helpParameter = "--help";
	private final static String helpDescription = "Show all commandline parameters";
	@Option(names = helpParameter, description = helpDescription)
	boolean help;

	private final static String loggerLevelParameter = "-log";
	private final static String loggerLevelDescription = "Set logger level: trace(0), debug(1), info(2), warn(3), error(4), fatal(5). Default is warn(3).";
	@Option(names = loggerLevelParameter, description = loggerLevelDescription, defaultValue = "3")
	int loggerLevel = 3;
	
	private final static String headerParameter = "-header";
	private final static String headerDescription = "Print header into result csv";
	@Option(names = headerParameter, description = headerDescription)
	boolean header;

	private final static String sequentialParameter = "-sequential";
	private final static String sequentialDescription = "Execute sequential";
	@Option(names = sequentialParameter, description = sequentialDescription)
	boolean sequential;

	private final static String selectionParameter = "-selection";
	private final static String selectionDescription = "Selection and order of antipattern and metrics to analyze given by ID";
	@Option(names = selectionParameter, arity = "0..*", split = ",", description = selectionDescription)
	List<String> shortcutSelection = new ArrayList<String>();

	private final static String inputDirectoryParameter = "-inputDirectory";
	private final static String inputDirectoryDescription = "Directory from which all metamodels should be analysed";
	@Option(names = inputDirectoryParameter, description = inputDirectoryDescription)
	private String inputDirectory = null;

	private final static String outputDirectoryParameter = "-outputDirectory";
	private final static String outputDirectoryDescription = "Directory in which the result csv file schould be saved";
	@Option(names = outputDirectoryParameter, description = outputDirectoryDescription)
	private String outputDirectory = null;

	private boolean checkCommandLineParameters() {
		boolean returnValue = true;
		if (this.loggerLevel < 0 || this.loggerLevel > 5) {
			logger.warn("Invalid logger level. Setting warn-level.");
		}
		ParameterAndLoggerHelper.setLoggerLevel(this.loggerLevel);
		if (this.inputDirectory == null) {
			System.out.println("The parameter \"-inputDirectory\" must be set.");
			returnValue = false;
		}
		if (this.outputDirectory == null) {
			System.out.println("It is recommended to set the parameter \"-outputDirectory\".");
		}
		return returnValue;
	}

	private static void printHelp() {

		final int widthColumn1 = 25;
		final int widthColumn2 = 100;
		final String headingSeparator = "=".repeat(widthColumn1 + widthColumn2 + 3) + System.lineSeparator();
		final String rowSeparator = "-".repeat(widthColumn1 + widthColumn2 + 3) + System.lineSeparator();
		final String template = "|%-" + widthColumn1 + "s|%-" + widthColumn2 + "s|" + System.lineSeparator();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(headingSeparator);
		stringBuilder.append(String.format(template, "Parameter", "Description"));
		stringBuilder.append(headingSeparator);
		stringBuilder.append(String.format(template, helpParameter, helpDescription));
		stringBuilder.append(rowSeparator);
		stringBuilder.append(String.format(template, loggerLevelParameter, loggerLevelDescription));
		stringBuilder.append(rowSeparator);
		stringBuilder.append(String.format(template, headerParameter, headerDescription));
		stringBuilder.append(rowSeparator);
		stringBuilder.append(String.format(template, sequentialParameter, sequentialDescription));
		stringBuilder.append(rowSeparator);
		stringBuilder.append(String.format(template, selectionParameter, selectionDescription));
		stringBuilder.append(rowSeparator);
		stringBuilder.append(String.format(template, inputDirectoryParameter, inputDirectoryDescription));
		stringBuilder.append(rowSeparator);
		stringBuilder.append(String.format(template, outputDirectoryParameter, outputDirectoryDescription));
		stringBuilder.append(rowSeparator);
		System.out.println(stringBuilder.toString());
	}

	public static void main(String[] args) throws IOException {		
		MainAnalyzer ma = new MainAnalyzer();
		new CommandLine(ma).parseArgs(args);
		if (ma.help) {
			CommandLine.usage(new MainAnalyzer(), System.out);
			printHelp();
		} else {
			ma.start();
		}
	}

	public void start() throws IOException {
		logger.info("Starting new analysis task.");
		if (!checkCommandLineParameters()) {
			return;
		}

		List<String> ecoreFiles = MetamodelLoader.findAllEcoreMetamodelsInDirectory(this.inputDirectory);
		logger.trace(String.format("Found %d potential ecore metamodels to analyze.", ecoreFiles.size()));
		
		Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());

		if (this.sequential) {
			runSequential(ecoreFiles, resultMap);
		} else {
			runParallel(ecoreFiles, resultMap);
		}
		logger.trace("Analysis completed. Printing results...");

		AnalysisResults.setShortcutSelection(shortcutSelection);
		printResultsCSV(header, resultMap);
		logger.trace("Results stored. Done.");
	}

	public void runParallel(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap) {
		
		AtomicInteger lock = new AtomicInteger(0);
		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		logger.trace(String.format("Available Processors: %d", Runtime.getRuntime().availableProcessors()));
		for (int ecoreFileNumber = 0; ecoreFileNumber < ecoreFiles.size(); ecoreFileNumber++) {
			executorService.execute(new MetamodelAnalysisThread(ecoreFileNumber, ecoreFiles.get(ecoreFileNumber), resultMap, shortcutSelection, lock,ecoreFiles.size()));
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(2, TimeUnit.MINUTES);
		} catch (Exception e) {
			logger.error(String.format("Error occurrec during parallel analysis: %s", e.getMessage()));
		}
	}

	public void runSequential(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap) {
		for (int ecoreFileNumber = 0; ecoreFileNumber < ecoreFiles.size(); ecoreFileNumber++) {

			String ecoreFile = ecoreFiles.get(ecoreFileNumber);
			AnalysisResults analysisResult = new AnalysisResults(ecoreFileNumber);
			resultMap.put(ecoreFileNumber, analysisResult);

			Optional<Resource> optionalMetamodel = MetamodelHelper.loadEcoreMetamodelFromFile(ecoreFile);

			optionalMetamodel.ifPresent(metamodel -> {
				for (Antipattern antipattern : AnalyzerInterfaceLoader.getAllAntipatterns().values().stream()
						.filter(ap -> shortcutSelection.contains(ap.getShortcut())).collect(Collectors.toList())) {
					long evaluationResult = antipattern.evaluate(metamodel);
					analysisResult.addAntipattern(antipattern.getShortcut(), evaluationResult);
				}
				for (Metric metric : AnalyzerInterfaceLoader.getAllMetrics().values().stream()
						.filter(m -> shortcutSelection.contains(m.getShortcut())).collect(Collectors.toList())) {
					double evaluationResult = metric.evaluate(metamodel);
					analysisResult.addMetric(metric.getShortcut(), evaluationResult);
				}
			});
			if(this.loggerLevel > 1) {
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
		String execution = this.sequential ? "seq" : "par";
		String fileName = "metamodel_analysis_results.csv";

		FileWriter fileWriter;
		try {
			String completeFileName = date + "_" + execution + "_" + fileName;
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
