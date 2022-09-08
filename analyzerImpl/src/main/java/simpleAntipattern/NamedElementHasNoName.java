package simpleAntipattern;

import java.util.List;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class NamedElementHasNoName extends AbstractAntipattern {

	private static final String name = "NamedElementHasNoName";
	private static final String shortcut = "UNN"; // UnNamed-Named
	private static final String description = "This antipattern describes an ENamedElement which has either null or the empty string as name.";

	public NamedElementHasNoName() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		List<ENamedElement> eNamedElements = MetamodelHelper.getAllModelElementsOfGivenType(ENamedElement.class,
				resource);
		return eNamedElements.stream().filter(eNamed -> eNamed.getName() == null || "".equals(eNamed.getName()))
				.count();
	}
}