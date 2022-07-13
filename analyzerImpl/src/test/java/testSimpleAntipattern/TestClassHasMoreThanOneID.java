package testSimpleAntipattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simpleAntipattern.ClassHasMoreThanOneID;

public class TestClassHasMoreThanOneID {
	
	@Test
	public void testCorrectName() {
		ClassHasMoreThanOneID antipattern = new ClassHasMoreThanOneID();		
		assertEquals("ClassHasMoreThanOneID", antipattern.getName());
	}
}