package visualization.result.reducer;

import visualization.result.*;

/**
 * A Reducer can reduce a Result in its size.
 * It will modify the interior data so it represents the same
 * data, but then with a smaller size
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Reducer implements java.io.Serializable {

	/**
	 * Reduces the result.
	 * This method just reduces the result to itself, so it's
	 * practically not very useful. Therefore, one should create
	 * an extension of this Reducer.
	 *
	 * @param result the data to be reduced
	 * @return the reduced data
	 */	
	public Result reduce(Result result) {
		return result;
	}

	/**
	 * Reduces two values to a single value.
	 *
	 * Should be used by the Visualizer to do a local final reduction
	 *
	 * @param a the first of the two values to be reduced
	 * @param b the second of the two values to be reduced
	 * @return the reduced value
	 */
	public double singleReduction(double a, double b) {
		return a;
	}

}
