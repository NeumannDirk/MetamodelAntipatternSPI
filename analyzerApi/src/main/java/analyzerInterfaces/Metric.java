package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

import results.AnalysisResults;

public abstract class Metric {

	public int getMetricID() {
		return metricID;
	}

	void setMetricID(int antipatternID) {
		this.metricID = antipatternID;
	}
	
	@Override
	public String toString() {
		return this.name + "(" + this.shortcut + ")" ;
	}

	private int metricID;
	
	public String description;
	public String name;
	public String shortcut;
	
	protected abstract double evaluateMetric(Resource resource);
	
	public void evaluateMetricForMetamodel(Resource resource, AnalysisResults analysisResult) {
		double val = evaluateMetric(resource);
		analysisResult.addMetric(this.getMetricID(), val);
	}
}
