package visualization.result.reducer;

/**
 * A Reducer1D can reduce a Result1D in its size.
 * It will modify the interior data so it represents the same
 * data, but then with a smaller size
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Reducer1D extends Reducer {

	private double xScale;

	/**
	 * Sets the scale in x dimension.
	 *
	 * @param scale the scale (original / reduction)
	 */	
	public void setScale(double scale) {
		xScale = scale;
	}

	/**
	 * Sets the scale only in x dimension.
	 *
	 * @param scale the scale (original / reduction)
	 */	
	public void setXScale(double scale) {
		xScale = scale;
	}

	/**
	 * GSets the scale in x dimension.
	 *
	 * @return scale the scale (original / reduction)
	 */	
	public double getXScale() {
		return xScale;
	}

}
