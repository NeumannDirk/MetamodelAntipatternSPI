package analyzerUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

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

	public static Optional<Resource> loadEcoreMetamodelFromFile(String ecoreFile) {
		Resource myMetaModel = null;
		try {
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
					new EcoreResourceFactoryImpl());
			myMetaModel = resourceSet.getResource(URI.createFileURI(ecoreFile), true);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return Optional.ofNullable(myMetaModel);
	}
}
