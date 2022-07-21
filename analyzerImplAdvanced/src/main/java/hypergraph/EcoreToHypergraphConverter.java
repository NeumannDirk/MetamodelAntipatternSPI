package hypergraph;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.EEnumImpl;
import org.eclipse.emf.ecore.impl.EGenericTypeImpl;
import org.eclipse.emf.ecore.resource.Resource;

import metamodelUtil.MetamodelHelper;

public class EcoreToHypergraphConverter {

	public static HyperGraph createHypergraph(Resource metaModel) {
		HyperGraph hypergraph = new HyperGraph();  
		EList<EObject> emodels = metaModel.getContents();

		if (emodels.size() > 0) {
			List<EClass> eclasses = MetamodelHelper.getAllModelElementsOfGivenType(EClass.class, metaModel);
			for(EObject potentialHyperNode : eclasses) {
				HyperNode hyperNode = new HyperNode(potentialHyperNode);
				hypergraph.nodes.add(hyperNode);
			}
		}
		
		if (hypergraph.nodes.size() > 0) {
			for(HyperNode hyperNode : hypergraph.nodes) {
				Class hyperNodeClass = hyperNode.originalModelElement.getClass();
				if(hyperNodeClass == EEnumImpl.class) {
					continue;
				} else if (hyperNodeClass == EDataTypeImpl.class) {
					continue;
				} else if (hyperNodeClass == EGenericTypeImpl.class) {
					System.out.println("Mal schauen was hier noch kommt!");
				} else {					
					EClass nodeAsClass = (EClass) hyperNode.originalModelElement;

					//check for supertypes
					EList<EGenericType> superTypes = nodeAsClass.getEGenericSuperTypes();
					if (superTypes.size() > 0) {
						HyperEdge hyperEdge = HyperEdge.tryCreateHyperEdge(hypergraph, hyperNode, superTypes);
						if(hyperEdge != null) {							
							hypergraph.edges.add(hyperEdge);
						}
					}
					
					//check for attributes
					EList<EAttribute> attributes = nodeAsClass.getEAttributes();
					for(EAttribute eatt : attributes) {
						HyperEdge hyperEdge = HyperEdge.tryCreateHyperEdge(hypergraph, hyperNode, eatt);
						if(hyperEdge != null) {							
							hypergraph.edges.add(hyperEdge);
						}
					}
					
					//check for references
					EList<EReference> references = nodeAsClass.getEReferences();
					for(EReference eref : references) {
						HyperEdge hyperEdge = HyperEdge.tryCreateHyperEdge(hypergraph, hyperNode, eref);
						if(hyperEdge != null) {							
							hypergraph.edges.add(hyperEdge);
						}
					}
					
					//check for operations
					EList<EOperation> operations = nodeAsClass.getEOperations();
					for(EOperation eop : operations) {
						HyperEdge hyperEdge = HyperEdge.tryCreateHyperEdge(hypergraph, hyperNode, eop);
						if(hyperEdge != null) {				
							hypergraph.edges.add(hyperEdge);
						}
					}
				}				
			}
		}
		return hypergraph;
	}
}
