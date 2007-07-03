package visualization.event;

/**
 * Using an enable event the {@link visualization.visualizer.VisualizerXY} can inform the
 * {@link visualization.generator.GeneratorXY} about whether the data of a particular
 * measurement variable is needed (i.e. whether that variable is enabled or disabled).
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
 public class EnableEvent extends Event {
	private boolean enable;
	private String name;

	/**
	 * Constructs an <code>EnableEvent</code> for the measurement variable specified by the 
	 * <code>name</code> parameter. Depending on the <code>enable</code> parameter it is 
	 * either enabled or disabled
	 *
	 * @param	name the name of the measurement variable that should be disabled or enabled
	 * @param	enable the boolean indicating whether it should be enabled (TRUE) or disabled
	 *			(FALSE)
	 */	
	public EnableEvent(String name, boolean enable) {
		super("Enable");
		this.enable = enable;
		this.name = name;
	}

	/**
	 * Gets whether the measurement variable should be enabled or disabled
	 *
	 * @return	the boolean indicating whether it should be enabled (TRUE) or disabled
	 */	
	public boolean getEnable() {
		return enable;
	}

	/**
	 * Gets the name of the measurement variable
	 *
	 * @return	the name of the measurement variable
	 */	
	public String getName() {
		return name;
	}
}
