package metamodelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

/**
 * Set of helper methods to efficiently retrieve (sets of) certain elements from a metamodel. 
 * @author DirkNeumann
 *
 */
public class MetamodelHelper {
	private static Logger logger = LogManager.getLogger(MetamodelHelper.class.getName());
	
	/**
	 * Filters all elements of a given type from the provided metamodel and returns them in an <strong>unmodifiable</strong> list.
	 * @param <T> Type of which all instances should be retrieved. 
	 * @param clazz Class of the type &ltT&gt.
	 * @param myMetaModel Provided metamodel to filter from.
	 * @return <strong>Unmodifiable view</strong> on the list of the filtering result. 
	 */
	public static <T> List<T> getAllModelElementsOfGivenType(Class<T> clazz, Resource myMetaModel) {
		List<T> returnList = new ArrayList<T>();
		
		TreeIterator<EObject> iter = myMetaModel.getAllContents();
		while(iter.hasNext()) {
			EObject content = iter.next();
			if(clazz.isInstance(content)) {
				returnList.add(clazz.cast(content));
			}
		}				
		return Collections.unmodifiableList(returnList);
	}
	
	/**
	 * Tries to loads an ecore metamodel from the filesystem. 
	 * @param ecoreFile Path of the ecore metamodel
	 * @return The Resource of the metamodel if success, an empty optional if not. 
	 */
	public static Optional<Resource> loadEcoreMetamodelFromFile(String ecoreFile) {
		Resource myMetaModel = null;
		try {
			ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(EcorePackage.eNAME,
					new EcoreResourceFactoryImpl());
			myMetaModel = resourceSet.getResource(URI.createFileURI(ecoreFile), true);
		} catch (Exception e) {
			logger.info(String.format("Unable to load the ecore metamodel from the file \"%s\". Errormessage: %s", ecoreFile, e.getMessage()));
		}
		return Optional.ofNullable(myMetaModel);
	}
}
