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

	@Option(names = "-h", description = "print csv header")
	boolean header;

	@Option(names = "-s", description = "execute analysis tasks sequentially")
	boolean sequential;

	@Option(names = "-order", arity = "0..*", split = ",")
	List<String> shortcutOrder = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		MainAnalyzer ma = new MainAnalyzer();
		new CommandLine(ma).parseArgs(args);
		ma.start();
	}

	public void start() throws IOException {
		String dir = "D:\\data\\ap_mm";
		List<String> ecoreFiles = MetamodelLoader.findAllEcoreMetamodelsInDirectory(dir);
		System.out.println("ecoreFiles.size() = " + ecoreFiles.size());
		Map<Integer, AnalysisResults> resultMap = new ConcurrentHashMap<Integer, AnalysisResults>(ecoreFiles.size());

		if (this.sequential) {
			MainAnalyzer.runSequential(ecoreFiles, resultMap);
		} else {
			MainAnalyzer.runParallel(ecoreFiles, resultMap);
		}

		AnalysisResults.setShortcutOrder(shortcutOrder);
		printResultsCSV(header, resultMap);
	}

	public static void runParallel(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap) {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		System.out.println("Runtime.getRuntime().availableProcessors() = " + Runtime.getRuntime().availableProcessors());
		for (int ecoreFileNumber = 0; ecoreFileNumber < 10000; ecoreFileNumber++) {
			executorService
					.execute(new MetamodelAnalysisThread(ecoreFileNumber, ecoreFiles.get(ecoreFileNumber), resultMap));
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(2, TimeUnit.MINUTES);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void runSequential(List<String> ecoreFiles, Map<Integer, AnalysisResults> resultMap) {
		for (int ecoreFileNumber = 0; ecoreFileNumber < 10000; ecoreFileNumber++) {

			String ecoreFile = ecoreFiles.get(ecoreFileNumber);
			AnalysisResults analysisResult = new AnalysisResults(ecoreFileNumber);
			resultMap.put(ecoreFileNumber, analysisResult);

			Optional<Resource> optionalMetamodel = MetamodelHelper.loadEcoreMetamodelFromFile(ecoreFile);

			optionalMetamodel.ifPresent(metamodel -> {
				for (Antipattern antipattern : AnalyzerInterfaceLoader.getAllAntipatterns().values()) {
					antipattern.evaluateAntiPatternForMetamodel(metamodel, analysisResult);
				}
				for (Metric metric : AnalyzerInterfaceLoader.getAllMetrics().values()) {
					metric.evaluateMetricForMetamodel(metamodel, analysisResult);
				}
			});
		}
	}

	private void printResultsCSV(boolean printHeader, Map<Integer, AnalysisResults> resultMap) {		
		String goalDirectory = "D:\\";
		String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String execution = this.sequential ? "seq" : "par";
		String fileName = "metamodel_analysis_results.csv";		

		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(goalDirectory + date + "_" + execution + "_" + fileName);
			if (printHeader) {
				fileWriter.write(AnalysisResults.getHeaderCSV());
			}
			for (int resultIndex = 0; resultIndex < resultMap.size(); resultIndex++) {
				fileWriter.write(resultMap.get(resultIndex).getContentCSV());
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) { }
	}
}
