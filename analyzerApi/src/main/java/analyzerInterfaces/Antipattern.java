package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

import results.AnalysisResults;

public abstract class Antipattern {	
	
	public int getAntipatternID() {
		return antipatternID;
	}

	void setAntipatternID(int antipatternID) {
		this.antipatternID = antipatternID;
	}
	
	@Override
	public String toString() {
		return this.name + "(" + this.shortcut + ")" ;
	}

	private int antipatternID;
	
	public String description;
	public String name;
	public String shortcut;
	
	protected abstract double evaluateAntipattern(Resource resource);
	
	public void evaluateAntiPatternForMetamodel(Resource resource, AnalysisResults analysisResult) {
		double val = evaluateAntipattern(resource);
		analysisResult.addAntipattern(this.getAntipatternID(), val);
	}
}
