package analyzerInterfaces;

import org.eclipse.emf.ecore.resource.Resource;

public interface Evaluable<T> {

	String getDescription();

	String getName();

	String getShortcut();

	T evaluate(Resource resource);
}
