package simpleAntipattern;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class UnnamedPackage extends AbstractAntipattern {

	private static final String name = "UnnamedPackage";
	private static final String shortcut = "UNP"; // UnNamed-Named
	private static final String description = "This antipattern describes an EPackage which has either null or the empty string as name.";

	public UnnamedPackage() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		List<EPackage> ePackages = MetamodelHelper.getAllModelElementsOfGivenType(EPackage.class, resource);
		return ePackages.stream().filter(ePack -> ePack.getName() == null || "".equals(ePack.getName())).count();
	}
}