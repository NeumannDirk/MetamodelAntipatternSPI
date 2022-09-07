package testAdvancedMetrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import advancedMetrics.HypergraphEntropy;
import metamodelUtil.MetamodelHelper;

public class TestHypergraphEntropy {
	private static Logger logger = LogManager.getLogger(TestHypergraphEntropy.class.getName());
	
	private static final String resourcePathGeneral = "src/test/resources";
	private static final String resourcePathSpecial = "src/test/resources/hypergraphEntropy";

	@ParameterizedTest
	@MethodSource("provideMetamodelsAndMetricValues")
	public void testMetric(String filename, double expected) {
		double delta = 0.00001d;
		Optional<Resource> optionalMetamodel = MetamodelHelper.loadEcoreMetamodelFromFile(filename);
		assertTrue(optionalMetamodel.isPresent());
		HypergraphEntropy metric = new HypergraphEntropy();
		double actual = metric.evaluate(optionalMetamodel.get());
		assertEquals(expected, actual, delta);;
		logger.info(String.format("Test with parameters '%s' and '%s' succeded.", filename, expected));
	}

	private static Stream<Arguments> provideMetamodelsAndMetricValues() {
		return Stream.of(
			Arguments.of(resourcePathSpecial + "/HypergraphEntropy_empty.ecore", 0.0d),
			Arguments.of(resourcePathSpecial + "/HypergraphEntropy_oneClass.ecore", 0.0d),
			Arguments.of(resourcePathSpecial + "/HypergraphEntropy_oneClass2.ecore", 0.0d),
			Arguments.of(resourcePathSpecial + "/HypergraphEntropy_twoLonely.ecore", 0.0d),
			Arguments.of(resourcePathSpecial + "/HypergraphEntropy_twoDifferent.ecore", 0.69315d),
			Arguments.of(resourcePathSpecial + "/HypergraphEntropy_twoCross.ecore", 0.0d)
		);
	}
}
