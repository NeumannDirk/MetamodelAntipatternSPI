package concurrentExecution;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AnalyzerInterfaceLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;
import metamodelUtil.MetamodelHelper;
import results.AnalysisResults;

public class MetamodelAnalysisThread implements Runnable {

	private int ecoreFileNumber;
	private String ecoreFile;
	private Map<Integer, AnalysisResults> resultMap;
	List<String> shortcutSelection;

	public MetamodelAnalysisThread(int ecoreFileNumber, String ecoreFile, Map<Integer, AnalysisResults> resultMap, List<String> shortcutSelection) {
		this.ecoreFileNumber = ecoreFileNumber;
		this.ecoreFile = ecoreFile;
		this.resultMap = resultMap;
		this.shortcutSelection = shortcutSelection;
	}

	@Override
	public void run() {
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
