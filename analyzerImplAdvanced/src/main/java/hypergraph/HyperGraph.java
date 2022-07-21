package hypergraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

public class HyperGraph {
	public HashSet<HyperNode> nodes = new HashSet<HyperNode>();
	public ArrayList<HyperEdge> edges = new ArrayList<HyperEdge>();
	public HashSet<HyperNode> additionalNodes = new HashSet<HyperNode>();
	
	public String summary() {
		StringBuilder sb = new StringBuilder()
				.append(this.nodes.size()).append(" Nodes\n")
				.append(this.edges.size()).append(" Edges\n")
				.append("H = ").append(this.calculateEntropy()).append("\n");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Nodes:\n");
		for(HyperNode node : this.nodes) {
			sb.append(node.toString());
			sb.append("\n");
		}
		sb.append("\nEdges:\n");
		for(HyperEdge edge : this.edges) {
			sb.append(edge.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public HyperNode tryFindNode(EObject searchedFor) {
		for(HyperNode node : this.nodes) {
			if(node.originalModelElement == searchedFor) {				
				return node;
			}
		}
		for(HyperNode node : this.additionalNodes) {
			if(node.isNodeOf(searchedFor)) {
				return node;
			}
		}
		return null;
		
	}
	
	public double calculateEntropy() {		
		Map<Object, Long> counts = this.edges.stream().collect(Collectors.groupingBy(e -> e.nodes, Collectors.counting()));
		ArrayList<HyperNode> xx = new ArrayList<HyperNode>(this.nodes);
		
		Map<HyperNode, ArrayList<HyperEdge>> table = new HashMap<HyperNode, ArrayList<HyperEdge>>();
		for(HyperNode node : this.nodes) {
			table.put(node, new ArrayList<HyperEdge>());
			for(HyperEdge edge : this.edges) {
				if(edge.nodes.contains(node)) {
					table.get(node).add(edge);
				}
			}
		}
		
		Map<Object, Long> weights = table.values().stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		
		ArrayList<Double> rows = new ArrayList<Double>();
		weights.forEach((k,v) -> {
			double weight = v*1.0 / this.nodes.size();
			rows.add(weight * (-Math.log(weight)));
		});
		double entropy = rows.stream().reduce(0.0d, (a,b) -> a + b);
				
		return entropy;
	}
}
