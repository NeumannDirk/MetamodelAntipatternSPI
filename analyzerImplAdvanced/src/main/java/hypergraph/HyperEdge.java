package hypergraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

public class HyperEdge {
	public EObject originalModelElement; 
	public HyperGraph graph;
	public HashSet<HyperNode> nodes = new HashSet<HyperNode>();
	String name = "";
	
	void setGraph(HyperGraph graph) {
		this.graph = graph;
	}
	
	public HyperEdge(HyperGraph graph, HashSet<HyperNode> nodes, EObject originalModelElement, String name) {
		this.graph = graph;
		this.nodes = nodes;
		this.originalModelElement = originalModelElement; 
		this.name = name;
	}
	
	public static HyperEdge tryCreateHyperEdge(HyperGraph graph, HyperNode node, EReference eref) {		
		HyperNode retrievedNode = graph.tryFindNode(eref.getEReferenceType());
		if(retrievedNode != null) {
			HashSet<HyperNode> nodes = new HashSet<HyperNode>();
			nodes.add(node);
			nodes.add(retrievedNode);			
			String name = ((EClass)node.originalModelElement).getName() + "::" + eref.getName();
			return new HyperEdge(graph, nodes, eref, name);
		} else {
			return null;
		}
	}

	public static HyperEdge tryCreateHyperEdge(HyperGraph graph, HyperNode node, EList<EGenericType> superTypes) {
		HashSet<HyperNode> nodes = new HashSet<HyperNode>();
		nodes.add(node);
		
		for(EGenericType superType : superTypes) {
			HyperNode retrievedNode = graph.tryFindNode(superType.getEClassifier());
			if(retrievedNode != null) {
				nodes.add(retrievedNode);
			}
		}
		
		if(nodes.size() > 1) {			
			String name = ((EClass)node.originalModelElement).getName() + "-inheritance";
			return new HyperEdge(graph, nodes, node.originalModelElement, name);
		} else {
			return null;
		}
	}

	
	public static HyperEdge tryCreateHyperEdge(HyperGraph graph, HyperNode node, EAttribute eatt) {		
		HyperNode retrievedNode = graph.tryFindNode(eatt.getEAttributeType());
		if(retrievedNode != null) {
			HashSet<HyperNode> nodes = new HashSet<HyperNode>();
			nodes.add(node);
			nodes.add(retrievedNode);			
			String name = ((EClass)node.originalModelElement).getName() + "::" + eatt.getName();
			return new HyperEdge(graph, nodes, eatt, name);
		} else {
			return null;
		}
	}

	public static HyperEdge tryCreateHyperEdge(HyperGraph graph, HyperNode node, EOperation eop) {
		HashSet<HyperNode> nodes = new HashSet<HyperNode>();
		nodes.add(node);
		
		for(EParameter epara : eop.getEParameters()) {
			HyperNode retrievedNode = null;
			if (epara.getEGenericType() != null) {
				retrievedNode = graph.tryFindNode(epara.getEGenericType().getEClassifier()); //.getEClassifier().getName()				
			}
			if(retrievedNode != null) {
				nodes.add(retrievedNode);
			}
		}
		
		if(nodes.size() > 1) {
			String name = ((EClass)node.originalModelElement).getName() + "::" + eop.getName();
			return new HyperEdge(graph, nodes, node.originalModelElement, name);
		} else {
			return null;
		}
	}
	
	public boolean hasSameNodesAs(HyperEdge otherEdge) {
		return this.nodes.equals(otherEdge.nodes);
	}
	
	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" : Edge<");
		Iterator<HyperNode> iter = this.nodes.iterator();
		while (iter.hasNext()) {
			HyperNode next = iter.next();
			sb.append(next);
			if(iter.hasNext()) {
				sb.append(",");
			}			
		}
		sb.append(">");
		return sb.toString();		
	}
}
