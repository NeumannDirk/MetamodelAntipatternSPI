package analyzerUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MetamodelLoader {
	public static List<String> findAllEcoreMetamodelsInDirectory(String directoryToLoadFrom) throws IOException {
		List<String> ecoreFiles = new ArrayList<String>();
		File directory = new File(directoryToLoadFrom);
		if (directory.exists()) {
			MetamodelLoader.getAllEcoreFiles(directory, ecoreFiles);
			return ecoreFiles;
		} else {
			throw new IOException("The directory \'" + directoryToLoadFrom + "\' does not exist.");
		}
	}

	private static void getAllEcoreFiles(File curDir, List<String> list) {
		File[] filesList = curDir.listFiles();
		for (File f : filesList) {
			if (f.isDirectory())
				MetamodelLoader.getAllEcoreFiles(f, list);
			if (f.isFile() && f.getName().endsWith(".ecore")) {
				list.add(f.getAbsolutePath());
			}
		}
	}
}
