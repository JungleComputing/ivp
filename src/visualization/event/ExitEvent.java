package visualization.event;

/**
 * An ExitEvent indicates that the {@link visualization.visualizer.Visualizer} has finished executing.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class ExitEvent extends Event {

	/**
	 * Constructs an <code>ExitEvent</code> 
	 */
	public ExitEvent() {
		super("Exit");
	}

}
