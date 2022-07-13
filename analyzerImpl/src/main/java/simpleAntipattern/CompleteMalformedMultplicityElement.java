package simpleAntipattern;

import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class CompleteMalformedMultplicityElement extends AbstractAntipattern {

	private static final String name = "CompleteMalformedMultplicityElement";
	private static final String shortcut = "#MME_COM";
	private static final String description = 
			"This antipattern describes an EStructuralFeature which has a malformed multiplicity. "
			+ "This includes the case in which no lower bound is set but an upper bound is set "
			+ "as well as the case in which both bounds are set but the upper bound is lower than the lower bound.";

	public CompleteMalformedMultplicityElement() {
		super(name, shortcut, description);
	}

	@Override
	protected long evaluateAntipattern(Resource resource) {
		List<EStructuralFeature> eStructuralFeatures = MetamodelHelper
				.getAllModelElementsOfGivenType(EStructuralFeature.class, resource);
		if (eStructuralFeatures.size() == 0) {
			return 0;
		}

		long numberOfAntipatterns = 0;

		// [*,*] ==> ok
		// [x,*] ==> ok
		// [*,y] ==> nicht ok
		// [x,y] ==> nicht ok wenn x > y
		for (EStructuralFeature esf : eStructuralFeatures) {
			int lo = esf.getLowerBound();
			int up = esf.getUpperBound();
			// [x,y] ==> nicht ok wenn x > y
			if ((lo != -1) && (up != -1) && (lo > up)) {
				numberOfAntipatterns++;
			}
			// [*,y] ==> nicht ok
			if ((lo == -1) && (up != -1)) {
				numberOfAntipatterns++;
			}
		}

		return numberOfAntipatterns;
	}

}