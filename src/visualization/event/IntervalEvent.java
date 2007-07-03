package visualization.event;

/**
 * Using an interval event the {@link visualization.visualizer.Visualizer} can inform the
 * {@link visualization.generator.Generator} that it does want a measurement after each <i>n</i> ms.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
 public class IntervalEvent extends Event {
	private int interval;
	private String name;

	/**
	 * Constructs a <code>IntervalEvent</code> which requests each <code>interval</code> ms a measurement
	 *
	 * @param interval		the interval between measurements in ms
	 */	
	public IntervalEvent(String name, int interval) {
		super("Interval");
		this.interval = interval;
		this.name = name;
	}

	/**
	 * Gets the interval between measurements
	 *
	 * @return interval		the interval between measurements in ms
	 */	
	public int getInterval() {
		return interval;
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
