package metamodelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
 * Set of helper methods to efficiently retrieve (sets of) certain elements from
 * a metamodel.
 * 
 * @author DirkNeumann
 *
 */
public class MetamodelHelper {
	private static Logger logger = LogManager.getLogger(MetamodelHelper.class.getName());

	/**
	 * Decide whether already retrieved metamodel elements should be cached in case
	 * they are needed again.
	 */
	public static boolean useCaching = false;

	public static Map<Resource, Map<Class<Object>, List<Object>>> cache = new HashMap<Resource, Map<Class<Object>, List<Object>>>();

	/**
	 * Filters all elements of a given type from the provided metamodel and returns
	 * them in an <strong>unmodifiable</strong> list.
	 * 
	 * @param <T>         Type of which all instances should be retrieved.
	 * @param clazz       Class of the type &ltT&gt.
	 * @param myMetaModel Provided metamodel to filter from.
	 * @return <strong>Unmodifiable view</strong> on the list of the filtering
	 *         result.
	 */
	public static <T extends Object> List<T> getAllModelElementsOfGivenType(Class<T> clazz, Resource myMetaModel) {
		List<T> returnList = new ArrayList<T>();
		if (useCaching && cache.containsKey(myMetaModel) && cache.get(myMetaModel).containsKey(clazz)) {
			returnList = (List<T>) cache.get(myMetaModel).get(clazz);
		} else {
			returnList = retrieveAndCacheModelElements(clazz, myMetaModel);	
		}
		return Collections.unmodifiableList(returnList);
	}

	private static <T> List<T> retrieveAndCacheModelElements(Class<T> clazz, Resource myMetaModel) {
		if(containsRecursiveElement(myMetaModel)) {
			return Collections.emptyList();
		}
		TreeIterator<EObject> iter = myMetaModel.getAllContents();
		Stream<EObject> targetStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED), false);	
		List<T> returnList = targetStream.filter(eObject -> clazz.isInstance(eObject)).map(t -> clazz.cast(t)).toList();
		if(useCaching) {
			try {
				cache.get(myMetaModel).put((Class<Object>)clazz,(List<Object>)returnList);				
			}catch (Exception e) {
				Map<Class<Object>, List<Object>> newMap = new HashMap<Class<Object>, List<Object>>();
				newMap.put((Class<Object>)clazz,(List<Object>)returnList);
				cache.put(myMetaModel, newMap);
			}
		}
		return Collections.unmodifiableList(returnList);
	}
		
	private static boolean containsRecursiveElement(Resource myMetaModel) {
		TreeIterator<EObject> iter1 = myMetaModel.getAllContents();
		Stream<EObject> targetStream1 = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter1, Spliterator.ORDERED), false);
		if(targetStream1.anyMatch(x -> x.eContents().contains(x))) {
			return true;
		}
		TreeIterator<EObject> iter2 = myMetaModel.getAllContents();
		Stream<EObject> targetStream2 = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter2, Spliterator.ORDERED), false);
		if(targetStream2.anyMatch(x -> x.eContents().stream().anyMatch(y -> y.eContents().contains(x)))) {
			return true;
		}
		return false;
		
	}

	public static void dropCache(Resource myMetaModel) {
		cache.remove(myMetaModel);
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
