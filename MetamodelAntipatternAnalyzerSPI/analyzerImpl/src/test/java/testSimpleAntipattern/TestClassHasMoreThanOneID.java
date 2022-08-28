package testSimpleAntipattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import metamodelUtil.MetamodelHelper;
import simpleAntipattern.ClassHasMoreThanOneID;

public class TestClassHasMoreThanOneID {

	private static final String resourcePath = "src/test/resources";

	@Test
	public void testCorrectName() {
		ClassHasMoreThanOneID antipattern = new ClassHasMoreThanOneID();
		assertEquals("ClassHasMoreThanOneID", antipattern.getName());
	}

	@ParameterizedTest
	@MethodSource("provideMetamodelsAndAntipatternCounts")
	public void testMetamodel1(String filename, int expected) {
		Optional<Resource> optionalMetamodel = MetamodelHelper
				.loadEcoreMetamodelFromFile(resourcePath + "/" + filename);
		assertTrue(optionalMetamodel.isPresent());
		ClassHasMoreThanOneID antipattern = new ClassHasMoreThanOneID();
		long actual = antipattern.evaluate(optionalMetamodel.get());
		assertEquals(expected, actual);
	}

	private static Stream<Arguments> provideMetamodelsAndAntipatternCounts() {
		return Stream.of(Arguments.of("myEcore1.ecore", 3));
	}
}