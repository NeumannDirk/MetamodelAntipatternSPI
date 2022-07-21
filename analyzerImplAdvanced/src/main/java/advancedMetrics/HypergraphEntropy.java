package advancedMetrics;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;

import analyzerInterfaces.AbstractMetric;
import hypergraph.EcoreToHypergraphConverter;
import metamodelUtil.MetamodelHelper;

public class HypergraphEntropy extends AbstractMetric {

	private static final String name = "HypergraphEntropy";
	private static final String shortcut = "HyEnt";
	private static final String description = "This metric calculates the entropy of the metamodel "
			+ "based on its hypergraph. The calculation is based on information theory (put link to paper here).";

	public HypergraphEntropy() {
		super(name, shortcut, description);
	}

	@Override
	public double evaluateMetric(Resource resource) {		
		return EcoreToHypergraphConverter.createHypergraph(resource).calculateEntropy();
	}

}
