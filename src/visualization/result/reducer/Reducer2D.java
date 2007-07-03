package visualization.result.reducer;

/**
 * A Reducer2D can reduce a Result2D in its size.
 * It will modify the interior data so it represents the same
 * data, but then with a smaller size
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Reducer2D extends Reducer1D {

	private double yScale;

	/**
	 * Sets the scale in both x and y dimensions.
	 *
	 * @param scale the scale (original / reduction)
	 */	
	public void setScale(double scale) {
		super.setScale(scale);
		yScale = scale;
	}

	/**
	 * Sets the scale only in y dimension.
	 *
	 * @param scale the scale (original / reduction)
	 */	
	public void setYScale(double scale) {
		yScale = scale;
	}

	/**
	 * Gets the scale in y dimension.
	 *
	 * @return the scale (original / reduction)
	 */	
	public double getYScale() {
		return yScale;
	}

}
