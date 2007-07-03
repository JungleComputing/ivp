package visualization.result.reducer;

/**
 * A Reducer3D can reduce a Result3D in its size.
 * It will modify the interior data so it represents the same
 * data, but then with a smaller size
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Reducer3D extends Reducer2D {

	private double zScale;

	/**
	 * Sets the scale in the x, y and z dimensions.
	 *
	 * @param scale the scale (original / reduction)
	 */	
	public void setScale(double scale) {
		super.setScale(scale);
		zScale = scale;
	}

	/**
	 * Sets the scale only in z dimension.
	 *
	 * @param scale the scale (original / reduction)
	 */	
	public void setZScale(double scale) {
		zScale = scale;
	}

	/**
	 * GSets the scale in z dimension.
	 *
	 * @return scale the scale (original / reduction)
	 */	
	public double getZScale() {
		return zScale;
	}
}
