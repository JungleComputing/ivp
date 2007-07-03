package visualization.event;

/**
 * Using a start event the {@link visualization.visualizer.Visualizer} can inform the {@link visualization.generator.Generator} 
 * that it the generation may start.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class StartEvent extends Event {

	/**
	 * Constructs a <code>StartEvent</code> 
	 */
	public StartEvent() {
		super("Start");
	}

}
