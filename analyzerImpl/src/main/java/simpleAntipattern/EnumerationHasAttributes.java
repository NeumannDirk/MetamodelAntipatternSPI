package simpleAntipattern;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class EnumerationHasAttributes extends AbstractAntipattern {

	private static final String name = "EnumerationHasAttributes";
	private static final String shortcut = "EnumAtts";
	private static final String description = "This antipattern describes an EEnumeration which contains any EStructuralFeatures which are not supposed to be contained.";

	public EnumerationHasAttributes() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		List<EEnum> eenums = MetamodelHelper.getAllModelElementsOfGivenType(EEnum.class, resource);
		long numberOfAntipattern = 0L;
		for (EEnum eenum : eenums) {
			Stream<EObject> eContents = StreamSupport
					.stream(Spliterators.spliteratorUnknownSize(eenum.eAllContents(), Spliterator.ORDERED), false);
			if (eContents.anyMatch(eObject -> EStructuralFeature.class.isInstance(eObject))) {
				numberOfAntipattern++;
			}
		}

		return numberOfAntipattern;
	}

}