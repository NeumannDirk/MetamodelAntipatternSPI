package advancedAntipattern;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class DiamondInheritance extends AbstractAntipattern {

	private static final String name = "DiamondInheritance";
	private static final String shortcut = "DiaIn";
	private static final String description = "This antipattern describes a class which has two Supertypes, which both have a common supertype. "
			+ "Transitive hulls are taken into account. Simple Example: D -> B -> A; D -> C -> A.";

	public DiamondInheritance() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		List<EClass> eclasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		long numberOfAntipattern = 0;
		for (EClass eclass : eclasses) {
			// Filter out classes which cannot contain this antipattern to begin with
			if (eclass.getESuperTypes().size() > 1 && eclass.getEAllSuperTypes().size() > 2
					&& hasDiamondInheritance(eclass)) {
				numberOfAntipattern++;
			}
		}
		return numberOfAntipattern;
	}

	private boolean hasDiamondInheritance(EClass eclass) {
		EList<EClass> directSuperTypes = eclass.getESuperTypes();

		if (directSuperTypes.size() < 2) {
			return false;
		}

		EList<EClass> allSuperTypes = eclass.getEAllSuperTypes();
		List<EClass> allIndirectSuperTypes = allSuperTypes.stream().filter(x -> !directSuperTypes.contains(x))
				.collect(Collectors.toList());

		if (allIndirectSuperTypes.size() == 0) {
			return false;
		}

		for (int i = 0; i < directSuperTypes.size() - 1; i++) {
			EClass directSuperType1 = directSuperTypes.get(i);
			for (int j = i + 1; j < directSuperTypes.size(); j++) {
				EClass directSuperType2 = directSuperTypes.get(j);
				if (haveCommonSuperType(directSuperType1, directSuperType2)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean haveCommonSuperType(EClass eClass1, EClass eClass2) {
		EList<EClass> indirectSuperTypes1 = eClass1.getEAllSuperTypes();
		EList<EClass> indirectSuperTypes2 = eClass2.getEAllSuperTypes();
		for (EClass indirectSuperType1 : indirectSuperTypes1) {
			for (EClass indirectSuperType2 : indirectSuperTypes2) {
				if (indirectSuperType1.equals(indirectSuperType2)) {
					return true;
				}
			}
		}
		return false;
	}
}