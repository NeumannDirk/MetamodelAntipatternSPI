package simpleAntipattern;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class MultiplePossibleElementContainers extends AbstractAntipattern {

	private static final String name = "MultiplePossibleElementContainers";
	private static final String shortcut = "MPEC";
	private static final String description = "This antipattern describes an EClass which has more than one possible container class.";

	public MultiplePossibleElementContainers() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		long numberOfAntipatterns = 0;
		List<EClass> eClasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		List<EReference> eReferences = MetamodelHelper.getAllModelElementsOfGivenType(EReference.class, resource);
		List<EReference> containments = eReferences.stream().filter(eRef -> eRef.isContainment()
				&& eRef.getEReferenceType() != null && eClasses.contains(eRef.getEReferenceType()))
				.collect(Collectors.toList());
		for (EReference containment1 : containments) {
			for (EReference containment2 : containments) {
				if (containment1 != containment2
						&& containment1.getEReferenceType() == containment2.getEReferenceType()) {
					numberOfAntipatterns++;
				}
			}
		}
		return numberOfAntipatterns;
	}
}