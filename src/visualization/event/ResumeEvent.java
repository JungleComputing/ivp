package visualization.event;

/**
 * Using a resume event the {@link visualization.visualizer.Visualizer} can inform the {@link visualization.generator.Generator} 
 * that it may resume the paused generation.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class ResumeEvent extends Event {

	/**
	 * Constructs a <code>ResumeEvent</code> 
	 */
	public ResumeEvent() {
		super("Resume");
	}

}
