package mainanalyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import analyzerInterfaces.AnalyzerInterfaceImplementationLoader;
import analyzerInterfaces.Antipattern;
import analyzerInterfaces.Metric;
import results.AnalysisResults;

public class MainAnalyzer {

	public static void main(String[] args) {
		System.out.println("\nStart");
		System.out.println("\nMetamodelAnalysis");

		List<String> ecoreFiles = new ArrayList<String>();
		ecoreFiles.add("D:\\Repositories\\MyEcore\\model\\myEcore.ecore");
		ecoreFiles.add("D:\\Repositories\\MyEcore\\model\\myEcore.ecore");
		ecoreFiles.add("D:\\Repositories\\MyEcore\\model\\myEcore.ecore");
		
		HashMap<Integer, AnalysisResults> resultMap = new HashMap<Integer, AnalysisResults>();

		for (int ecoreFileNumber = 0; ecoreFileNumber < ecoreFiles.size(); ecoreFileNumber++) {
			String ecoreFile = ecoreFiles.get(ecoreFileNumber);
			AnalysisResults analysisResult = new AnalysisResults(ecoreFileNumber);
			resultMap.put(ecoreFileNumber, analysisResult);
						
			Resource myMetaModel = null;
			try {
				ResourceSet resourceSet = new ResourceSetImpl();
				resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());				
				myMetaModel = resourceSet.getResource(URI.createFileURI(ecoreFile), true);
			} catch (Exception e) {
				System.out.println(e.getStackTrace());			
			}
					

			System.out.println("\nAntipattern");
			for (Antipattern abstractAntipattern : AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer().values()) {				
				System.out.println("" + abstractAntipattern.getShortcut() + ": " + abstractAntipattern);				
				abstractAntipattern.evaluateAntiPatternForMetamodel(myMetaModel, analysisResult);
			}
			System.out.println("\nMetrics");
			for (Metric abstractMetric : AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer().values()) {
				System.out.println("" + abstractMetric.getShortcut() + ": " + abstractMetric);				
				abstractMetric.evaluateMetricForMetamodel(myMetaModel, analysisResult);
			}
			System.out.println("Result: \n" + analysisResult);
		}		
		System.out.println("\nPrinting results to csv...");
		
		boolean printHeader = Arrays.asList(args).contains("--header");
		MainAnalyzer.printResultsCSV(printHeader, resultMap);
		System.out.println("\nEnd");
	}

	private static void printResultsCSV(boolean printHeader, HashMap<Integer,AnalysisResults> resultMap) {
		String pathToSave = "D:\\metamodel_analysis_results.csv";		
		
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(pathToSave);
			if (printHeader) {
				System.out.println("print header");
				fileWriter.write(AnalysisResults.getHeaderCSV());
			}
			for(int resultIndex = 0; resultIndex < resultMap.size(); resultIndex++) {				
				fileWriter.write(resultMap.get(resultIndex).getContentCSV());				
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
