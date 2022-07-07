package mainanalyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import analyzerInterfaces.AnalyzerInterfaceImplementationLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;
import baeldungExample.ExchangeRate;
import baeldungExample.ExchangeRateProvider;
import results.AnalysisResults;

public class MainAnalyzer {

	public static void main(String[] args) {
		System.out.println("\nStart");
		System.out.println("\nBaeldung");
		for (Iterator<ExchangeRateProvider> iter = ExchangeRate.providers(false); iter.hasNext();) {
			System.out.println(iter.next());
		}

		System.out.println("\nMetamodelAnalysis");

		List<String> ecoreFiles = new ArrayList<String>();
		ecoreFiles.add("D:\\Repositories\\MyEcore\\model\\myEcore.ecore");

		for (int ecoreFileNumber = 0; ecoreFileNumber < ecoreFiles.size(); ecoreFileNumber++) {
			String ecoreFile = ecoreFiles.get(ecoreFileNumber);
			
			EList<EObject> emodels = null;
			Resource myMetaModel = null;
			try {
				ResourceSet resourceSet = new ResourceSetImpl();
//				EcorePackage ecorePackage = EcorePackage.eINSTANCE;
//				resourceSet.getPackageRegistry().put(ecorePackage.getNsURI(), ecorePackage);
				resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());				
				myMetaModel = resourceSet.getResource(URI.createFileURI(ecoreFile), true);
				emodels = myMetaModel.getContents();
			} catch (Exception e) {
				System.out.println(e.getStackTrace());			
			}
			AnalysisResults analysisResult = new AnalysisResults();		

			System.out.println("\nAntipattern");
			for (Antipattern antipattern : AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer(false)) {				
				System.out.println("" + antipattern.getAntipatternID() + ": " + antipattern);
				
				antipattern.evaluateAntiPatternForMetamodel(myMetaModel, analysisResult);
//				analysisResult.addAntipattern(0, 5);
			}
			System.out.println("\nMetrics");
			for (Metric metric : AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer(false)) {
				System.out.println("" + metric.getMetricID() + ": " + metric);
				
				metric.evaluateMetricForMetamodel(myMetaModel, analysisResult);
//				analysisResult.addMetric(0, 2);
			}
			
			System.out.println("Result: \n" + analysisResult);

		}
		System.out.println("\nEnd");
	}

}
