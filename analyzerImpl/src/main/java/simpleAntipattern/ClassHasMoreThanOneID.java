package simpleAntipattern;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.Antipattern;
import metamodelUtil.MetamodelHelper;

public class ClassHasMoreThanOneID extends Antipattern {

	public ClassHasMoreThanOneID() {
		this.name = "ClassHasMoreThanOneID";
		this.shortcut = "ID++";
	}

	@Override
	protected double evaluateAntipattern(Resource resource) {
		ArrayList<EClass> eclasses = MetamodelHelper.getAllEClasses(resource);

		int numberOfAntipatterns = 0;
		if (eclasses.size() > 0) {
			for (EClass ec : eclasses) {
				int numberOfIDs = 0;
				for (EAttribute att : ec.getEAllAttributes()) {
					if (att.isID()) {
						numberOfIDs++;
					}
				}
				if (numberOfIDs > 1) {
					numberOfAntipatterns++;
				}
			}
		}

		return numberOfAntipatterns;
	}

}
