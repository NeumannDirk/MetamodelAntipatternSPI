package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * 
 * @author DirkNeumann
 *
 * @param <T> The return type of the Evaluation, most likely a basic numeric datatype.
 */
public interface Evaluable<T> {

	String getDescription();

	String getName();

	/**
	 * Shortcuts should not contain any seperator chars to ensure correct interpretation in the results csv.
	 * @return the shortcut
	 */
	String getShortcut();

	/** 
	 * @param resource The metamodel resource on which the Evaluable should be evaluated.
	 * @return The result of the metamodel with respect to the defined Evaluable.
	 */
	T evaluate(Resource resource);
}
