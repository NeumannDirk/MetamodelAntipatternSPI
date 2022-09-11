package simpleAntipattern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
		List<EClass> eClasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		List<EReference> eReferences = MetamodelHelper.getAllModelElementsOfGivenType(EReference.class, resource);
		List<EReference> containments = eReferences.stream().filter(eRef -> eRef.isContainment()
				&& eRef.getEReferenceType() != null && eClasses.contains(eRef.getEReferenceType()))
				.collect(Collectors.toList());
		
		Set<EClass> multipleContainers = new HashSet<EClass>();		
		for (int i = 0; i < containments.size() - 1; i++) {
			EReference containment1 = containments.get(i);
			for (int j = i + 1; j < containments.size(); j++) {
				EReference containment2 = containments.get(j);
				if(containment1.getEReferenceType() == containment2.getEReferenceType()) {
					multipleContainers.add(containment1.getEReferenceType());
				}
			}
		}
		
		return (long) multipleContainers.size();
	}
}