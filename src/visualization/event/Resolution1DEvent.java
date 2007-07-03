package visualization.event;

/**
 * Using a 1D resolution event the {@link visualization.visualizer.Visualizer1D} can inform the
 * {@link visualization.generator.Generator1D} that its 1 dimensional resolution has changed.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Resolution1DEvent extends Event {
	private int xResolution;

	/**
	 * Constructs a <code>Resolution1DEvent</code> informing the resolution changed to <code>[xResolution]</code>.
	 *
	 * @param xResolution		the resolution of the x dimension
	 */	
	public Resolution1DEvent(int xResolution) {
		super("Resolution");
		this.xResolution = xResolution;
	}

	/**
	 * Get the resolution of the x dimension.
	 *
	 * @return xResolution		the resolution of the x dimension
	 */	
	public int getXResolution() {
		return xResolution;
	}
}
