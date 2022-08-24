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
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AnalyzerInterfaceLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;
import analyzerUtil.MetamodelLoader;
import concurrentExecution.MetamodelAnalysisThread;
import metamodelUtil.MetamodelHelper;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import results.AnalysisResults;

public class MainAnalyzer {

	private final static String helpParameter = "--help";
	private final static String helpDescription = "show all commandline parameters";
	@Option(names = helpParameter, usageHelp = true, description = helpDescription)
	boolean help;

	private final static String headerParameter = "-h";
	private final static String headerDescription = "print header into result csv";
	@Option(names = headerParameter, description = headerDescription)
	boolean header;

	private final static String sequentialParameter = "-s";
	private final static String sequentialDescription = "execute sequential";
	@Option(names = sequentialParameter, description = sequentialDescription)
	boolean sequential;

	private final static String selectionParameter = "-selection";
	private final static String selectionDescription = "selection and order of antipattern and metrics to analyze given by ID";
	@Option(names = selectionParameter, arity = "0..*", split = ",", description = selectionDescription)
	List<String> shortcutSelection = new ArrayList<String>();

	private final static String inputDirectoryParameter = "-inputDirectory";
	private final static String inputDirectoryDescription = "directory from which all metamodels should be analysed";
	@Option(names = inputDirectoryParameter, description = inputDirectoryDescription)
	private String inputDirectory = null;

	private final static String outputDirectoryParameter = "-outputDirectory";
	private final static String outputDirectoryDescription = "directory in which the result csv file schould be saved";
	@Option(names = outputDirectoryParameter, description = outputDirectoryDescription)
	private String outputDirectory = null;

	private static void printHelp() {

		final int widthCol1 = 25;
		final int widthCol2 = 80;
		final String headingSeparator = "=".repeat(widthCol1 + widthCol2 + 3) + System.lineSeparator();
		final String rowSeparator = "-".repeat(widthCol1 + widthCol2 + 3) + System.lineSeparator();
		final String template = "|%-" + widthCol1 + "s|%-" + widthCol2 + "s|" + System.lineSeparator();

		StringBuilder sb = new StringBuilder();
		sb.append(headingSeparator);
		sb.append(String.format(template, "Parameter", "Description"));
		sb.append(headingSeparator);
		sb.append(String.format(template, helpParameter, helpDescription));
		sb.append(rowSeparator);
		sb.append(String.format(template, headerParameter, headerDescription));
		sb.append(rowSeparator);
		sb.append(String.format(template, sequentialParameter, sequentialDescription));
		sb.append(rowSeparator);
		sb.append(String.format(template, selectionParameter, selectionDescription));
		sb.append(rowSeparator);
		sb.append(String.format(template, inputDirectoryParameter, inputDirectoryDescription));
		sb.append(rowSeparator);
		sb.append(String.format(template, outputDirectoryParameter, outputDirectoryDescription));
		sb.append(rowSeparator);
		System.out.println(sb.toString());
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
		if (!checkCommandLineParameters()) {
			return;
		}

		List<String> ecoreFiles = MetamodelLoader.findAllEcoreMetamodelsInDirectory(inputDirectory);
		System.out.println("ecoreFiles.size() = " + ecoreFiles.size());
		Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());

		if (this.sequential) {
			runSequential(ecoreFiles, resultMap);
		} else {
			runParallel(ecoreFiles, resultMap);
		}

		AnalysisResults.setShortcutSelection(shortcutSelection);
		printResultsCSV(header, resultMap);
	}

	private boolean checkCommandLineParameters() {
		boolean returnValue = true;
		if (this.inputDirectory == null) {
			System.out.println("The parameter \"-inputDirectory\" must be set.");
			returnValue = false;
		}
		if (this.outputDirectory == null) {
			System.out.println("It is recommended to set the parameter \"-outputDirectory\".");
		}
		return returnValue;
	}

	public void runParallel(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap) {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
		for (int ecoreFileNumber = 0; ecoreFileNumber < 10000; ecoreFileNumber++) {
			executorService
					.execute(new MetamodelAnalysisThread(ecoreFileNumber, ecoreFiles.get(ecoreFileNumber), resultMap, shortcutSelection));
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(2, TimeUnit.MINUTES);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void runSequential(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap) {
		for (int ecoreFileNumber = 0; ecoreFileNumber < 10000; ecoreFileNumber++) {

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
		}
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
