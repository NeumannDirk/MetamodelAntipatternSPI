package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

import results.AnalysisResults;

public abstract class AbstractAntipattern implements Antipattern {

	@Override
	public String toString() {
		return "AbstractAntipattern [name=" + name + ", shortcut=" + shortcut + ", description=" + description + "]";
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
	
	public AbstractAntipattern(String name, String shortcut, String description) {
		this.name = name;
		this.shortcut = shortcut;
		this.description = description;
	}	

	public abstract long evaluateAntipattern(Resource resource);

	@Override
	public void evaluateAntiPatternForMetamodel(Resource resource, AnalysisResults analysisResult) {
		long val = evaluateAntipattern(resource);
		analysisResult.addAntipattern(this.getShortcut(), val);
	}
}
