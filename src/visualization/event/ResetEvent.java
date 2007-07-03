package visualization.event;

/**
 * Using a reset event the {@link visualization.visualizer.Visualizer} can inform the
 * {@link visualization.generator.Generator} that it should reset the generation.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class ResetEvent extends Event {

	/**
	 * Constructs a <code>ResetEvent</code> 
	 */
	public ResetEvent() {
		super("Reset");
	}

}
