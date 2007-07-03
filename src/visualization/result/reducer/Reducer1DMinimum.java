package visualization.result.reducer;

import visualization.result.*;

/**
 * A Reducer1DMinimum can reduce a Result1D in its size.
 * It will modify the interior data so it represents the same
 * data, but then with a smaller size. It uses the minimum function to 
 * achieve reduction.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Reducer1DMinimum extends Reducer1D {

	/**
	 * Reduces the result.
	 * Each cell in the reduced matrix is mapped unto the
	 * cells in the unreduced matrix that represent the same
	 * area. The minimum of that area is the value of the 
	 * reduced matrix
	 *
	 * @param result the data to be reduced
	 * @return the reduced data
	 */	
	public Result reduce(Result result) {
		int pos = 0;
		Result1D result1D = (Result1D) result;
		int newStartX = (int) (result1D.getStartX() / getXScale());
		int newEndX = (int) (result1D.getEndX() / getXScale());
		double[] newMatrix = new double[(newEndX - newStartX + 1)];
		for (int x = newStartX; x <= newEndX; x++) {
			double min = Double.POSITIVE_INFINITY;
			for (int l = Math.max(result1D.getStartX(), (int) (x * getXScale())); l < Math.min(result1D.getEndX() + 1, (int) ((x + 1) * getXScale())); l++) {
				min = Math.min(min, result1D.getMatrixValue(l - result1D.getStartX()));
			}
			newMatrix[pos++] = min;
		}
		result1D.setMatrixSize(newMatrix.length);
		for (int i = 0; i < result1D.getMatrixSize(); i++) {
			result1D.setMatrixValue(i, newMatrix[i]);
		}
		result1D.setStartPosition(newStartX);
		result1D.setEndPosition(newEndX);
		return result1D;
	}

	public double singleReduction(double a, double b) {
		return Math.min(a, b);
	}
}
