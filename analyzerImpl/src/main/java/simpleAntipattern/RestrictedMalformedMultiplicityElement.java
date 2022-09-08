package simpleAntipattern;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class RestrictedMalformedMultiplicityElement extends AbstractAntipattern {

	private static final String name = "RestrictedMalformedMultiplicityElement";
	private static final String shortcut = "MME_RES";
	private static final String description = "This antipattern describes an EStructuralFeature which has a malformed multiplicity. "
			+ "This includes only the case in which both bounds are set but the upper bound is lower than the lower bound.";

	public RestrictedMalformedMultiplicityElement() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		List<EStructuralFeature> eStructuralFeatures = MetamodelHelper
				.getAllModelElementsOfGivenType(EStructuralFeature.class, resource);
		long numberOfAntipatterns = 0;

		// [*,*] ==> ok
		// [x,*] ==> ok
		// [*,y] ==> ok
		// [x,y] ==> nicht ok wenn x > y
		for (EStructuralFeature esf : eStructuralFeatures) {
			int lo = esf.getLowerBound();
			int up = esf.getUpperBound();
			// [x,y] ==> nicht ok wenn x > y
			if ((lo != -1) && (up != -1) && (lo > up)) {
				numberOfAntipatterns++;
			}
		}

		return numberOfAntipatterns;
	}
}