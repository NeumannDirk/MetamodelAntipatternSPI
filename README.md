# MetamodelAntipatternSPI

This repository contains the software artifacts for the university course "Praktikum: Ingenieursmäßige Software-Entwicklung" at the KIT (Karlsruher Institut für Technologie). Goal is to implement a modular framework to analyse metamodels with respect to the prevalence of different metamodel antipatterns and at the same time asses different metrics to categorize metamodels and make findings more expressive. Another focus is on the parellelisation and overall speed of the evaluation of the metamodels.

## Technology for Modularity 

During the initial phase of dvelopement two alternatives for implemention modularity were considered: Using the Java Service Provider Interface (Java SPI) and unsing an OSGI based solution. The decision was made to use Java SPI because of multiple reasons.
1. First, Java SPI is more lightweight and therefore easier to use and to start with.
2. Second, many advantages of OSGI such as dynamic loading, installing, updating and stopping of services ist not needed. The goal is "just" to analyse a given set of metamodels with the antipattern and metrics provided at the start up time of the programm. Adding or removing antipattern/ metrics during execution would lead to incomplete datasets in the final result and wont provide any advantage.
3. Lastly, the runtime environment for OSGI is harder to set up since the complete OSGI framework needs to be provided and shiped. Java SPI on the other hand uses the intern reflection libraries and has therefore less dependencies.

## Attempts to Improve Performance

Improving performance is split in three different levels in the project. The level of metamodels, the level of all analyzer and the level of the individual antipattern or metric.
1. On the largest scale, one of the most obvious solutions is to evaluate multiple metamodels in paralell. This solution is already very effective. In test runs is reduced the time for evaluating ~85'000 metamodels from 108sec to 29sec which is a factor of 3.7.
2. The second level is the level of the different analyzers. Here it would be possible to parallelize as well, but since on the first level already ~85'000 tasks are created, another subdivision in subtasks wont give any benefit. In addition, the evaluation of a metamodel takes only very little time and espacially for small metamodels the parallelisation of the the evaluation would create more overhead than use. The only remaining thing to consider would be the caching of often used metamodel information such as the list of all EClassifiers inside the metamodel which is needed for many antipattern and metrics.
3. The thrid level is the level of a single antipattern pr metric. Here, one can find almost non potential for optimization. Efficient programming like reusing already tested and improved code from thrid-party libraries or the built-in Java libraries is the best way to have efficient code. Apart from that, any other optimizations should be looked at with caution since here the impact of writing bad code might become bigger than the actual benefit of improved performance.

## Architecture in General

This repository consists of 4 maven projects.
* The analyzerApi-project defines all Interfaces and all reusable helpers in connection with those.
* The analyzerImpl- and analyzerImplAdvanced-project implement these interfaces and provide the actual antipattern and metrics for the analysis. These antipattern and metrics are provided via the files inside src/main/resoures/META-INF/services to let the environment know which class implements which interface. These projects are also meant to define their own unit tests for each of the antipattern/ metrics.
* The mainanalyzer-project contains the executables for the analysis of the whole metamodel dataset and currently the performance comparator for the improvements concerning the runtime

The following uML class diagram gives an overview over the architectural structure of the repository. The analyzerImplAdvanced-project is left out since it has the same purpose as the analyzerImpl-project. 
<p align="center">
<img src="https://github.com/NeumannDirk/MetamodelAntipatternSPI/blob/main/pictures/uml.svg" width="700" height="700"/>
</p>

As indicated, the `MainAnalyzer` uses the `AnalyzerInterfaceLoader` to find all implementations of the interfaces `Antipattern` and `Metric`. These are then applied to the metamodels loaded by the `MetamodelLoader`. The antipattern and metrics can use some helper methods (`MetamodelHelper`) to deal with the metamodels, retrieve the needed information and write the results into an `AnalysisResult`-object which is provided from the `MainAnalyzer`. The package `concurrentExecution` contains the needed class to parallelize the analysis and to evaluate how big the performance boost is.
