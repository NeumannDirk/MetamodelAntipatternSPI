module analyzerApi {
	exports analyzerInterfaces;
	exports metamodelUtil;
	exports results;
	requires transitive org.eclipse.emf.ecore;
	requires transitive org.eclipse.emf.common;
	requires transitive org.eclipse.emf.ecore.xmi;
	requires transitive org.apache.logging.log4j;
}