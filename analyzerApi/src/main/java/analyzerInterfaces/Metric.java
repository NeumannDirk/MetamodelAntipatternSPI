package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

import results.AnalysisResults;

public interface Metric extends UniquelyDescribed {

	void evaluateMetricForMetamodel(Resource resource, AnalysisResults analysisResult);
}
