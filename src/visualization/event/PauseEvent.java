package visualization.event;

/**
 * Using a pause event the {@link visualization.visualizer.Visualizer} can inform the
 * {@link visualization.generator.Generator} that it should pause the generation.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class PauseEvent extends Event {

	/**
	 * Constructs a <code>PauseEvent</code> 
	 */
	public PauseEvent() {
		super("Pause");
	}

}
