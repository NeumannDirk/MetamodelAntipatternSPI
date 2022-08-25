package analyzerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EcorePackage;

import mainanalyzer.MainAnalyzer;

public final class MetamodelLoader {
	private static Logger logger = LogManager.getLogger(MainAnalyzer.class.getName());
	
	public static List<String> findAllEcoreMetamodelsInDirectory(String directoryToLoadFrom) {
		List<String> ecoreFiles = new ArrayList<String>();
		File directory = new File(directoryToLoadFrom);
		if (directory.exists()) {
			MetamodelLoader.getAllEcoreFiles(directory, ecoreFiles);			
		} else {
			logger.error(String.format("The directory \'%s\' does not exist.", directoryToLoadFrom));			
		}
		return ecoreFiles;
	}

	private static void getAllEcoreFiles(File curDir, List<String> list) {
		File[] filesList = curDir.listFiles();
		for (File f : filesList) {
			if (f.isDirectory())
				MetamodelLoader.getAllEcoreFiles(f, list);
			if (f.isFile() && f.getName().endsWith("." + EcorePackage.eNAME)) {
				list.add(f.getAbsolutePath());
			}
		}
	}
}
