package advancedAntipattern;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class DiamondInheritance extends AbstractAntipattern {
	
	private static final String name = "DiamondInheritance";
	private static final String shortcut = "DiaIn";
	private static final String description = 
			"This antipattern describes a class which has two Supertypes, which both have a common supertype. "
			+ "Transitive hulls are taken into account. Simple Example: D -> B -> A; D -> C -> A.";

	public DiamondInheritance() {
		super(name, shortcut, description);
	}

	@Override
	public long evaluateAntipattern(Resource resource) {
		List<EClass> eclasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		long numberOfAntipattern = 0;
		for (EClass eclass : eclasses) {
			//Filter out classes which cannot contain this antipattern to begin with 
			if (eclass.getESuperTypes().size() > 1 && eclass.getEAllSuperTypes().size() > 2 && hasDiamondInheritance(eclass)) {
				numberOfAntipattern++;
			}
		}
		return numberOfAntipattern;
	}
	
	private boolean hasDiamondInheritance(EClass eclass) {
		EList<EGenericType> directSuperTypes = eclass.getEGenericSuperTypes();
		EList<EGenericType> allSuperTypes = eclass.getEAllGenericSuperTypes();
		List<EGenericType> allIndirectSuperTypes = allSuperTypes.stream().filter(x -> !directSuperTypes.contains(x)).collect(Collectors.toList());
		
		if(allIndirectSuperTypes.size() > 0) {		
			for (EGenericType directSuperType1 : directSuperTypes) {
				for (EGenericType directSuperType2 : directSuperTypes) {
					if(directSuperType1 != directSuperType2) {
						if(directSuperType1.getEClassifier() instanceof EClass && directSuperType2.getEClassifier() instanceof EClass) {
							EList<EGenericType> indirectSuperTypes1 = ((EClass) directSuperType1.getEClassifier()).getEAllGenericSuperTypes();
							EList<EGenericType> indirectSuperTypes2 = ((EClass) directSuperType2.getEClassifier()).getEAllGenericSuperTypes();
							for (EGenericType indirectSuperType1 : indirectSuperTypes1) {
								for (EGenericType indirectSuperType2 : indirectSuperTypes2) {									
									if(indirectSuperType1.getEClassifier().equals(indirectSuperType2.getEClassifier())) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
