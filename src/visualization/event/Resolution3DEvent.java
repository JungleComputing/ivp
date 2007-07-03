package visualization.event;

/**
 * Using a 3D resolution event the {@link visualization.visualizer.Visualizer3D} can inform the
 * {@link visualization.generator.Generator3D} that its three dimensional resolution has changed.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Resolution3DEvent extends Resolution2DEvent {
	private int zResolution;

	/**
	 * Constructs a <code>ResolutionEvent</code> informing the resolution changed to 
	 <code>[xResolution]</code> * <code>[yResolution]</code> * <code>[zResolution]</code>.
	 *
	 * @param xResolution		the resolution of the x dimension
	 * @param yResolution		the resolution of the y dimension
	 * @param zResolution		the resolution of the z dimension
	 */	
	public Resolution3DEvent(int xResolution, int yResolution, int zResolution) {
		super(xResolution, yResolution);
		this.zResolution = zResolution;
	}

	/**
	 * Get the resolution of the z dimension.
	 *
	 * @return zResolution		the resolution of the z dimension
	 */	
	public int getZResolution() {
		return zResolution;
	}
}
