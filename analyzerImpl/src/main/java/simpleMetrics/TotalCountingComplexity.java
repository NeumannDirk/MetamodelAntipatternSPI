package simpleMetrics;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractMetric;
import metamodelUtil.MetamodelHelper;

public class TotalCountingComplexity extends AbstractMetric {

	private static final String name = "TotalCountingComplexity";
	private static final String shortcut = "TCC";
	private static final String description = "This metric counts the number of EClasses in a metamodel. "
			+ "Please note that this does not include instances of EDataType or EEnum.";

	public TotalCountingComplexity() {
		super(name, shortcut, description);
	}

	@Override
	public Double evaluate(Resource resource) {
		List<EClass> eClasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		double countingComplexity = 0D; 
		for(EClass eclass : eClasses) {
			countingComplexity += eclass.getEReferences().size();
			countingComplexity += eclass.getEAttributes().size();
			countingComplexity += eclass.getEGenericSuperTypes().size();
			countingComplexity += eclass.getEOperations().size();
		}
		return countingComplexity;
	}
}