package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

import results.AnalysisResults;

public abstract class AbstractMetric implements Metric {	

	@Override
	public String toString() {
		return "AbstractMetric [name=" + name + ", shortcut=" + shortcut + ", description=" + description + "]";
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getShortcut() {
		return this.shortcut;
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}

	private final String name;
	private final String shortcut;
	private final String description;
	
	public AbstractMetric(String name, String shortcut, String description) {
		this.name = name;
		this.shortcut = shortcut;
		this.description = description;
	}

	protected abstract double evaluateMetric(Resource resource);

	@Override
	public void evaluateMetricForMetamodel(Resource resource, AnalysisResults analysisResult) {
		double val = evaluateMetric(resource);
		analysisResult.addMetric(this.getShortcut(), val);
	}
}
