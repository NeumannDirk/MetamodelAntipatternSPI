package testSimpleAntipattern;

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
import simpleAntipattern.ClassHasMoreThanOneID;

public class TestClassHasMoreThanOneID {
	private static Logger logger = LogManager.getLogger(TestClassHasMoreThanOneID.class.getName());

	private static final String resourcePathGeneral = "src/test/resources";
	private static final String resourcePathSpecial = "src/test/resources/classHasMoreThanOneID";

	@ParameterizedTest
	@MethodSource("provideMetamodelsAndAntipatternCounts")
	public void testAntipattern(String filename, long expected) {
		Optional<Resource> optionalMetamodel = MetamodelHelper.loadEcoreMetamodelFromFile(filename);
		assertTrue(optionalMetamodel.isPresent());
		ClassHasMoreThanOneID antipattern = new ClassHasMoreThanOneID();
		long actual = antipattern.evaluate(optionalMetamodel.get());
		assertEquals(expected, actual);
		logger.info(String.format("Test with parameters '%s' and '%s' succeded.", filename, expected));
	}

	private static Stream<Arguments> provideMetamodelsAndAntipatternCounts() {
		return Stream.of(
			Arguments.of(resourcePathGeneral + "/GeneralTestModel1.ecore", 3),
			Arguments.of(resourcePathSpecial + "/ClassHasMoreThanOneID_1.ecore", 1),
			Arguments.of(resourcePathSpecial + "/ClassHasMoreThanOneID_3.ecore", 3)
		);
	}
}