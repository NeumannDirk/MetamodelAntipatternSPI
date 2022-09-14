package analyzerInterfaces;

public abstract class AbstractAntipattern implements Antipattern {

	@Override
	public String toString() {
		return "AbstractAntipattern [name=" + name + ", shortcut=" + shortcut + ", description=" + description + "]";
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
	public AbstractAntipattern(String name, String shortcut, String description) {
		this.name = name;
		this.shortcut = shortcut;
		this.description = description;
	}
}
