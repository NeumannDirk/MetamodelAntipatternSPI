# MetamodelAntipatternSPI

This repository contains the software artifacts for the university course "Praktikum: Ingenieursmäßige Software-Entwicklung" at the KIT (Karlsruher Institut für Technologie). Goal is to implement a modular framework to analyse the quality of metamodels with respect to the prevalence of different metamodel antipatterns and different metrics. There is a special focus on modularity and on overall performance which is explained later.

## Synopsis
```
//Main Analysis
java -cp [classpath elements] mainanalyzer.MainAnalyzer -inputDirectory "<input directory>" [more options]
//Evaluate Parallelization
java -cp [classpath elements] concurrentExecution.ConcurrencyComparator -inputDirectory "<input directory>" [more options]
//Evaluate Caching
java -cp [classpath elements] concurrentExecution.CachingComparator -inputDirectory "<input directory>" [more options]
```

## Technology for Modularity 

During the initial phase of dvelopement two alternatives for implemention modularity were considered: Using the Java Service Provider Interface (Java SPI) and unsing an OSGI based solution. The decision was made to use Java SPI because of multiple reasons.
1. First, Java SPI is more lightweight and therefore easier to use and to start with.
2. Second, many advantages of OSGI such as dynamic loading, installing, updating and stopping of services ist not needed. The goal is "just" to analyse a given set of metamodels with the antipattern and metrics provided at the start up time of the programm. Adding or removing antipattern/ metrics during execution would lead to incomplete datasets in the final result and wont provide any advantage.
3. Lastly, the runtime environment for OSGI is harder to set up since the complete OSGI framework needs to be provided and shiped. Java SPI on the other hand uses the intern reflection libraries and has therefore less dependencies.

## Attempts to Improve Performance

Improving performance is split in three different levels in the project. The level of metamodels, the level of all analyzer and the level of the individual antipattern or metric, although the on second level two approaches can be discussed.
1. On the largest scale, one of the most obvious solutions is to evaluate multiple metamodels in paralell. Parallelizing on this level has the advantage that no synchronization or copying of ressources is needed since every metamodel is saved in its own file. This solution is already very effective. In test runs is reduced the time for evaluating ~85'000 metamodels from 6min25sec to 1min44sec which is a factor of 3.7.
2.
    * The second level is the level of the different analyzers. Here it would be possible to parallelize as well, but since on the first level already ~85'000 tasks are created, another subdivision in subtasks wont give any benefit. In addition, the evaluation of a metamodel takes only very little time and espacially for small metamodels the parallelisation of the the evaluation would create more overhead than use. Also a parallelization on the level of different antipattern and metrics would need synchronized or copied ressources which makes it less performant.
    * Another possibility is to use caching of metamodel information. Many antipattern and metrics use the same partsinformation of the metamodel e.g. the list of all `EClass`. Since antipattern and metrics are all realized on their own this information needs to be calculated several times which is really time consuming. Caching can reduce this by saving the list of `EClass` per metamodel upon the first computation and reusing it multiple times. It then will be deleted after the analysis of the metamodel is completed. This enhancement reduces the time for the evaluation of ~85'000 metamodels from 1min44sec to 1min2sec which is a factor of 1.7.
5. The fourth level is the code level. Here, already optimised libraries (EMF) are used. Efficient programming like reusing already tested and improved code from thrid-party libraries or the built-in Java libraries is the best way to have efficient code. Apart from this and some genereral performance code style guidlines like using `Stream` and saving interim results to not recalculate them, any other optimizations should be looked at with caution since here the impact of writing bad code might become bigger than the actual benefit of improved performance.

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

## Setup... 
### ...for Reproduction

1. Clone this repository 
2. Install maven
3. Install the analyzerApi by calling `mvn clean install` inside the directory
4. Package the other three projects by calling `mvn package` inside the other three directories
5. Inside the main directory execute the analysis or the benchmarks as shown in [Synopsis](#synopsis)
6. Use the options as needed from the [CMD-Options table](#cmd-options)

### ...for own Antipattern and Metrics
You do not need to package the analyzerImpl and analyzerImplAdvanced projects but your own maven project. You can use one of them as template. Then in step 4, package your own project and add it to the classpath in step 5.

## CMD-Options...
### ...for main analysis
|         Parameter                  |                                 Description                                | Notes on Input   | Default Value |
|:----------------------------------:|:--------------------------------------------------------------------------:|------------------|--------------:|
| -csv_seperator,<br>-csv_sep        | Separator for the csv output                                               | String           | ","           |
| --help,<br>--h                     | Display help and exit                                                      |                  |               |
| -log_level,<br>-log                | Set logger level: trace(0), debug(1), info(2), warn(3), error(4), fatal(5) | 0-5              | 3             |
| -header                            | Print header into result csv                                               |                  |               |
| -sequential,<br>-seq               | Execute sequentially                                                       |                  |               |
| selection,<br>-sel                 | Selection and order of antipattern and metrics to analyze given by ID      | separated by "," |               |
| -inputDirectory,<br>-in            | Directory from which all metamodels should be analysed                     | directory path   |               |
| -outputDirectory,<br>-out          | Directory in which the result csv file schould be saved                    | directory path   |               |
| --v,<br>--version                  | Print version information and exit                                         |                  |               |
| -no_caching                        | Deactivates caching during the metamodel analysis to improve performance   |                  |               |
| -no_progresss_bar,<br>-no_progress | Deactivates printing the progress bar during execution                     |                  |               |
### ...for the benchmarks
|         Parameter                  |                                 Description                                | Notes on Input   | Default Value |
|:----------------------------------:|:--------------------------------------------------------------------------:|------------------|--------------:|
| --help,<br>--h                     | Display help and exit                                                      |                  |               |
| -repetitions,<br>-rep              | Number of repetitions of each execution version for comparisson            | int              | 10            |
| -inputDirectory,<br>-in            | Directory from which all metamodels should be analysed                     | directory path   |               |
| -no_progresss_bar,<br>-no_progress | Deactivates printing the progress bar during execution                     |                  |               |
