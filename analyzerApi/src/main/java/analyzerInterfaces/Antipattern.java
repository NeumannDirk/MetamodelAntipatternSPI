package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

import results.AnalysisResults;

public interface Antipattern extends UniquelyDescribed {

	void evaluateAntiPatternForMetamodel(Resource resource, AnalysisResults analysisResult);
}
