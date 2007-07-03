package visualization.event;

/**
 * Using a sync event the {@link visualization.visualizer.Visualizer} can inform the {@link visualization.generator.Generator} 
 * that it's ready for new data.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class SyncEvent extends Event {

	/**
	 * Constructs a <code>SyncEvent</code> 
	 */
	public SyncEvent() {
		super("Sync");
	}

}
