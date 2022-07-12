package simpleAntipattern;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.Antipattern;
import metamodelUtil.MetamodelHelper;

public class ClassHasMoreThanOneID extends Antipattern {

	public ClassHasMoreThanOneID() {
		this.name = "ClassHasMoreThanOneID";
		this.shortcut = "ID++";
	}

	@Override
	protected double evaluateAntipattern(Resource resource) {
		List<EClass> eclasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		Predicate<EClass> moreThanOneID = eClass -> eClass.getEAllAttributes().stream().filter(EAttribute::isID).count() > 1;		
		return eclasses.stream().filter(moreThanOneID).count();
	}

}
