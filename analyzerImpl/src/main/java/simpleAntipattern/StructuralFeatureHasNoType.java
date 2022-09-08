package simpleAntipattern;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractAntipattern;
import metamodelUtil.MetamodelHelper;

public class StructuralFeatureHasNoType extends AbstractAntipattern {

	private static final String name = "StructuralFeatureHasNoType";
	private static final String shortcut = "UTSF"; //UnTypedStructuralFeature
	private static final String description = "This antipattern describes an EStructuralFeature which has no type.";

	public StructuralFeatureHasNoType() {
		super(name, shortcut, description);
	}

	@Override
	public Long evaluate(Resource resource) {
		List<EReference> eReferences = MetamodelHelper.getAllModelElementsOfGivenType(EReference.class, resource);
		List<EAttribute> eAttributes = MetamodelHelper.getAllModelElementsOfGivenType(EAttribute.class, resource);
		long untypedEReferences = eReferences.stream().filter(eRef -> eRef.getEReferenceType() == null).count();
		long untypedEAttributes = eAttributes.stream().filter(eAtt -> eAtt.getEAttributeType() == null).count();
		return untypedEReferences + untypedEAttributes;
	}
}