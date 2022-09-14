package analyzerInterfaces;

public abstract class AbstractMetric implements Metric {	

	@Override
	public String toString() {
		return "AbstractMetric [name=" + name + ", shortcut=" + shortcut + ", description=" + description + "]";
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getShortcut() {
		return this.shortcut;
	}
	
	@Override
	public String getDescription() {
		return this.description;
	}

	private final String name;
	private final String shortcut;
	private final String description;
	
	/** 
	 * @param name 
	 * @param shortcut Should not contain any separator chars to ensure correct interpretation in the results csv.
	 * @param description
	 */
	public AbstractMetric(String name, String shortcut, String description) {
		this.name = name;
		this.shortcut = shortcut;
		this.description = description;
	}
}
