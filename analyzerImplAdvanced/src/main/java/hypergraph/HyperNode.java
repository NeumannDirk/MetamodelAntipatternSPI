package hypergraph;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EEnumImpl;
import org.eclipse.emf.ecore.impl.EGenericTypeImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;

public class HyperNode {
	
	public EObject originalModelElement;
	public String name;
	
	public HyperNode(EObject originalModelElement) {
		this.name = retrieveName(originalModelElement);
		this.originalModelElement = originalModelElement;
	}
	
	private static String retrieveName(EObject node) {
		String name = "";
		if(node.getClass() == EClassImpl.class) {
			name = ((EClassImpl)node).getName();
		}
		else if(node.getClass() == EEnumImpl.class) {
			name = ((EEnumImpl)node).getName();
		}
		else if(node.getClass() == EDataTypeImpl.class) {
			name = ((EDataTypeImpl)node).getName();
		}
		else if(node.getClass() == EGenericTypeImpl.class) {
			System.out.println("generic Class");
			name = ((EGenericTypeImpl)node).getEClassifier().getName();
		}
		
		EObject eContainer = node.eContainer();
		String location = "";
		if (eContainer == null) {
//			System.out.println("Kein EContainer für eine Klasse!");
		} else {
			location = retrieveNameFromEContainer(eContainer) + "/";
		}
		
		return location + name;
	}
	
	private static String retrieveName(EClass clazz) {
		EObject eContainer = clazz.eContainer();
		String location = "";
		if (eContainer == null) {
			System.out.println("Kein EContainer für eine Klasse!");
		} else {
			location = retrieveNameFromEContainer(eContainer) + "/";
		}
		
		return location + clazz.getName();
	}
	
	private static String retrieveNameFromEContainer(EObject eContainer) {
		String location = "";
		String name = "";
		if(eContainer != null) {
			EObject eContainerContainer = eContainer.eContainer();
			if (eContainerContainer == null) {
				if(eContainer instanceof EPackage) {
					name = ((EPackage)eContainer).getNsURI();	
				} else {
					name = eContainer.toString();
				}
			} else if (eContainer instanceof EPackage) {
				location = retrieveNameFromEContainer(eContainerContainer) + "/";
				name = ((EPackage)eContainer).getName();				
			} else {
//				System.out.println(eContainer);
			}
		}
		return location + name; 
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public boolean isNodeOf(EObject searchedFor) {		
		return this.originalModelElement == searchedFor;
	}

}
