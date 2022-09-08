package simpleAntipattern;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class ClassifierNotInPackageContained extends AbstractAntipattern {

	private static final String name = "ClassifierNotInPackageContained";
	private static final String shortcut = "CNIPC";
	private static final String description = "This antipattern describes an EClassifier which is not contained within any EPackage.";

	public ClassifierNotInPackageContained() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		List<EClassifier> eclassifiers = MetamodelHelper.getAllModelElementsOfGivenType(EClassifier.class, resource);
		Predicate<EClassifier> notContained = eclassifier -> EcorePackage.class.isInstance(eclassifier.eContainer());
		return eclassifiers.stream().filter(notContained).count();
	}

}