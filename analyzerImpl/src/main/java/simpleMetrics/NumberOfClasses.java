package simpleMetrics;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.Metric;
import metamodelUtil.MetamodelHelper;

public class NumberOfClasses extends Metric {
	
	public NumberOfClasses() {
		this.name = "simple NumberOfClasses";
		this.shortcut = "#Class";
	}

	@Override
	protected double evaluateMetric(Resource resource) {
		return MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource).size();
	}

}
