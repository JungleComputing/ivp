package visualization.event;

/**
 * Using a iteration skip event the {@link visualization.visualizer.Visualizer} can inform the
 * {@link visualization.generator.Generator} that it's only interested in each <i>n</i>-th iteration.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
 public class IterationSkipEvent extends Event {
	private int iterationskip;

	/**
	 * Constructs a <code>IterationSkipEvent</code> which requests each <code>iteration</code> a result
	 *
	 * @param iterationskip		the number of iterations to be skipped
	 */	
	public IterationSkipEvent(int iterationskip) {
		super("Iterationskip");
		this.iterationskip = iterationskip;
	}

	/**
	 * Get the number of iterations to be skipped
	 *
	 * @return the number of iterations to be skipped
	 */	
	public int getIterationSkip() {
		return iterationskip;
	}
}
