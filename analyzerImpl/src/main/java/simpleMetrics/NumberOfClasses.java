package simpleMetrics;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractMetric;
import metamodelUtil.MetamodelHelper;

public class NumberOfClasses extends AbstractMetric {

	private static final String name = "NumberOfClasses";
	private static final String shortcut = "#Class";
	private static final String description = 
			"This metric counts the number of EClasses in a metamodel. "
			+ "Please note that this does not include instances of EDataType or EEnum.";

	public NumberOfClasses() {
		super(name, shortcut, description);
	}

	@Override
	protected double evaluateMetric(Resource resource) {
		return MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource).size();
	}

}
