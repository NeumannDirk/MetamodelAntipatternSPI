package testSimpleMetrics;

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

import metamodelUtil.MetamodelHelper;
import simpleMetrics.NumberOfClasses;

public class TestNumberOfClasses {
	private static Logger logger = LogManager.getLogger(TestNumberOfClasses.class.getName());
	
	private static final String resourcePathGeneral = "src/test/resources";
	private static final String resourcePathSpecial = "src/test/resources/numberOfClasses";

	@ParameterizedTest
	@MethodSource("provideMetamodelsAndMetricValues")
	public void testMetric(String filename, double expected) {
		Optional<Resource> optionalMetamodel = MetamodelHelper.loadEcoreMetamodelFromFile(filename);
		assertTrue(optionalMetamodel.isPresent());
		NumberOfClasses metric = new NumberOfClasses();
		double actual = metric.evaluate(optionalMetamodel.get());
		assertEquals(expected, actual);
		logger.info(String.format("Test with parameters '%s' and '%s' succeded.", filename, expected));
	}

	private static Stream<Arguments> provideMetamodelsAndMetricValues() {
		return Stream.of(
			Arguments.of(resourcePathGeneral + "/GeneralTestModel1.ecore", 25),
			Arguments.of(resourcePathSpecial + "/NumberOfClasses_1.ecore", 1),
			Arguments.of(resourcePathSpecial + "/NumberOfClasses_3.ecore", 3)
		);
	}
}
