package simpleAntipattern;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class ClassHasMoreThanOneID extends AbstractAntipattern {

	private static final String name = "ClassHasMoreThanOneID";
	private static final String shortcut = "ID++";
	private static final String description = 
			"This antipattern describes a class which has more than one attribute which is defined as ID. "
			+ "The affected attributes can be newly defined or inherited.";
			

	public ClassHasMoreThanOneID() {
		super(name, shortcut, description);
	}

	@Override
	protected long evaluateAntipattern(Resource resource) {
		List<EClass> eclasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		Predicate<EClass> moreThanOneID = eClass -> eClass.getEAllAttributes().stream().filter(EAttribute::isID)
				.count() > 1;
		return eclasses.stream().filter(moreThanOneID).count();
	}

}
