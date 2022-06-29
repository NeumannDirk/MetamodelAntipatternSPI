package mainanalyzer;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;

import analyzerInterfaces.AnalyzerInterfaceImplementationLoader;
import analyzerInterfaces.AntiPattern;
import analyzerInterfaces.Metric;
import baeldungExample.ExchangeRate;
import baeldungExample.ExchangeRateProvider;

public class MainAnalyzer {

	public static void main(String[] args) {
		System.out.println("\nStart");
		System.out.println("\nBaeldung");
		for(Iterator<ExchangeRateProvider> iter = ExchangeRate.providers(false); iter.hasNext(); ) {
			System.out.println(iter.next());
		}
		System.out.println("\nAntiPatterns");
		for(Iterator<AntiPattern> iter = AnalyzerInterfaceImplementationLoader.getAntipatternsAnalyzer(false); iter.hasNext(); ) {
			System.out.println(iter.next());
		}
		System.out.println("\nMetrics");
		for(Iterator<Metric> iter = AnalyzerInterfaceImplementationLoader.getMetricsAnalyzer(false); iter.hasNext(); ) {
			System.out.println(iter.next());
		}
		System.out.println("\nEnd");
	}

}
