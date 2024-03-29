package concurrentExecution;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AnalyzerInterfaceLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;
import metamodelUtil.MetamodelHelper;
import results.AnalysisResults;

public class MetamodelAnalysisThread implements Callable<AnalysisResults> {

	private int ecoreFileNumber;
	private String ecoreFile;
	List<String> shortcutSelection;
	private AtomicInteger lock;
	private int max;
	private boolean noProgressBar;

	public MetamodelAnalysisThread(int ecoreFileNumber, String ecoreFile, List<String> shortcutSelection, AtomicInteger lock, int max, boolean noProgressBar) {
		this.ecoreFileNumber = ecoreFileNumber;
		this.ecoreFile = ecoreFile;
		this.shortcutSelection = shortcutSelection;
		this.lock = lock;
		this.max = max;
		this.noProgressBar = noProgressBar;
	}

	@Override
	public AnalysisResults call() throws Exception { 
		AnalysisResults analysisResult = new AnalysisResults(ecoreFileNumber);

		Optional<Resource> optionalMetamodel = MetamodelHelper.loadEcoreMetamodelFromFile(ecoreFile);
		optionalMetamodel.ifPresent(metamodel -> {			
			List<Antipattern> antipatternToEvaluate = AnalyzerInterfaceLoader.getAllAntipatterns().values().stream()
					.filter(ap -> shortcutSelection == null || shortcutSelection.contains(ap.getShortcut()))
					.collect(Collectors.toList());
			List<Metric> metricsToEvaluate = AnalyzerInterfaceLoader.getAllMetrics().values().stream()
					.filter(m -> shortcutSelection == null || shortcutSelection.contains(m.getShortcut()))
					.collect(Collectors.toList());

			for (Antipattern antipattern : antipatternToEvaluate) {
				long evaluationResult = antipattern.evaluate(metamodel);
				analysisResult.addAntipattern(antipattern.getShortcut(), evaluationResult);
			}
			for (Metric metric : metricsToEvaluate) {
				double evaluationResult = metric.evaluate(metamodel);
				analysisResult.addMetric(metric.getShortcut(), evaluationResult);
			}
			MetamodelHelper.dropCache(metamodel);
		});
		analysisResult.prepareContentString();
		if(!this.noProgressBar) {
			printProgressBar();
		}
		return analysisResult;
	}

	private void printProgressBar() {
		synchronized (this.lock) {
			double percent100 = (this.lock.addAndGet(1) * 100.0) / this.max;
			int percent50 = (int) Math.round(percent100 / 2);
			StringBuilder stringBuilder = new StringBuilder(String.format("\rProgress %3.0f%% [", percent100));
			stringBuilder.append("=".repeat(percent50 == 0 ? 0 : percent50 - 1) + ">"
					+ " ".repeat(percent50 == 0 ? 49 : 50 - percent50));
			String template = "] %" + String.valueOf(this.max).length() + "d/%d";
			stringBuilder.append(String.format(template, this.lock.get(), this.max));
			System.out.print(stringBuilder.toString());
		}
	}
}
