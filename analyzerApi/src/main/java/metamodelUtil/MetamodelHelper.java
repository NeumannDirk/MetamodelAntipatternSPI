package metamodelUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;

public class MetamodelHelper {
	
	//SHORTCUTS FOR SPECIFIC TYPES
		
	public static ArrayList<EClass> getAllEClasses(Resource myMetaModel) {
		ArrayList<Class> clazzes = new ArrayList<Class>();
		clazzes.add(EClassImpl.class);
		return (ArrayList<EClass>)(ArrayList<?>)getAllModelElementsOfGivenTypes(myMetaModel, clazzes);
	}
	
	//WRAPPER FOR ANY TYPE
	
	public static ArrayList<EObject> getAllModelElementsOfGivenType(Resource myMetaModel, Class clazz) {
		ArrayList<Class> clazzes = new ArrayList<Class>();
		clazzes.add(clazz);
		return getAllModelElementsOfGivenTypes(myMetaModel, clazzes);
	}
	
	public static ArrayList<EObject> getAllModelElementsOfGivenTypes(Resource myMetaModel, List<Class> clazzes) {
		ArrayList<EObject> returnList = new ArrayList<EObject>();
		EList<EObject> emodels = myMetaModel.getContents();
		getAllModelElementsOfGivenType(emodels, returnList, clazzes, null);				
		return returnList;
	}
	
	//ACTUAL COMPUTATION
	
	private static void getAllModelElementsOfGivenType(EList<EObject> contents, ArrayList<EObject> returnList, List<Class> clazzes, Integer packageHash) {
		for (EObject content : contents) {			 
			Class<? extends EObject> clazz = content.getClass();
			if (clazz == EPackageImpl.class) {
				int ph = content.hashCode();
				if(packageHash == null || ph != packageHash) {
					getAllModelElementsOfGivenType(((EPackageImpl)content).eContents(), returnList, clazzes, ph);					
				}						
			}
			if (clazzes.contains(clazz)) {
				returnList.add(content);
			}
		}
	}
}
