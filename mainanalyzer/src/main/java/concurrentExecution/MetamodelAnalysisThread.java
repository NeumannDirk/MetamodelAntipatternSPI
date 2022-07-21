package concurrentExecution;

import java.util.Map;
import java.util.Optional;

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

	public MetamodelAnalysisThread(int ecoreFileNumber, String ecoreFile, Map<Integer, AnalysisResults> resultMap) {
		this.ecoreFileNumber = ecoreFileNumber;
		this.ecoreFile = ecoreFile;
		this.resultMap = resultMap;
	}

	@Override
	public void run() {
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
