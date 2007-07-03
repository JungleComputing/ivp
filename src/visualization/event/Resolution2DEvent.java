package visualization.event;

/**
 * Using a 2D resolution event the {@link visualization.visualizer.Visualizer2D} can inform the
 * {@link visualization.generator.Generator2D} that its two dimensional resolution has changed.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Resolution2DEvent extends Resolution1DEvent {
	private int yResolution;

	/**
	 * Constructs a <code>Resolution2DEvent</code> informing the resolution changed to <code>[xResolution]</code> * <code>[yResolution]</code>.
	 *
	 * @param xResolution		the resolution of the x dimension
	 * @param yResolution		the resolution of the y dimension
	 */	
	public Resolution2DEvent(int xResolution, int yResolution) {
		super(xResolution);
		this.yResolution = yResolution;
	}

	/**
	 * Get the resolution of the y dimension.
	 *
	 * @return yResolution		the resolution of the y dimension
	 */	
	public int getYResolution() {
		return yResolution;
	}

}
