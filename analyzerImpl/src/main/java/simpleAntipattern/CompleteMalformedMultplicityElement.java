package simpleAntipattern;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.Antipattern;
import metamodelUtil.MetamodelHelper;

public class CompleteMalformedMultplicityElement extends Antipattern {

	public CompleteMalformedMultplicityElement() {
		this.name = "CompleteMalformedMultplicityElement";
		this.shortcut = "MME_COM";
	}

	@Override
	protected double evaluateAntipattern(Resource resource) {
		List<EClass> eclasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, resource);
		if (eclasses.size() == 0) {
			return 0;
		}

		long numberOfAntipatterns = 0;
		
		//[*,*] ==> ok
		//[x,*] ==> ok
		//[*,y] ==> nicht ok
		//[x,y] ==> nicht ok wenn x > y
		for (EClass ec : eclasses) {
			for (EStructuralFeature esf : ec.getEStructuralFeatures()) {
				int lo = esf.getLowerBound();
				int up = esf.getUpperBound();
				//[x,y] ==> nicht ok wenn x > y
				if((lo != -1)&&(up != -1)&&(lo > up)) {
					numberOfAntipatterns++;
				}
				//[*,y] ==> nicht ok
				if((lo == -1)&&(up != -1)){
					numberOfAntipatterns++;
				}
			}
		}
		
		return numberOfAntipatterns;
	}

}