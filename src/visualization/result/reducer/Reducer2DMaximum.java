package visualization.result.reducer;

import visualization.result.*;

/**
 * A Reducer2DMaximum can reduce a Result2D in its size.
 * It will modify the interior data so it represents the same
 * data, but then with a smaller size. It takes maximums to 
 * achieve reduction.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Reducer2DMaximum extends Reducer2D {

	/**
	 * Reduces the result.
	 * Each cell in the reduced matrix is mapped unto the
	 * cells in the unreduced matrix that represent the same
	 * area. The maximum of that area is the value of the 
	 * reduced matrix
	 *
	 * @param result the data to be reduced
	 * @return the reduced data
	 */	
	public Result reduce(Result result) {
		int pos = 0;
		Result2D result2D = (Result2D) result;
		int newStartX = (int) (result2D.getStartX() / getXScale());
		int newStartY = (int) (result2D.getStartY() / getYScale());
		int newEndX = (int) (result2D.getEndX() / getXScale());
		int newEndY = (int) (result2D.getEndY() / getYScale());
		double[] newMatrix = new double[(newEndX - newStartX + 1) * (newEndY - newStartY + 1)];
		for (int y = newStartY; y <= newEndY; y++) {
			for (int x = newStartX; x <= newEndX; x++) {
				double max = Double.NEGATIVE_INFINITY;
				for (int k = Math.max(result2D.getStartY(), (int) (y * getYScale())); k < Math.min(result2D.getEndY() + 1, (int) ((y + 1) * getYScale())); k++) {
					for (int l = Math.max(result2D.getStartX(), (int) (x * getXScale())); l < Math.min(result2D.getEndX() + 1, (int) ((x + 1) * getXScale())); l++) {
						max = Math.max(max, result2D.getMatrixValue((k - result2D.getStartY()) * (result2D.getEndX() - result2D.getStartX() + 1) + l - result2D.getStartX()));
					}
				}
				newMatrix[pos++] = max;
			}
		}
		result2D.setMatrixSize(newMatrix.length);
		for (int i = 0; i < result2D.getMatrixSize(); i++) {
			result2D.setMatrixValue(i, newMatrix[i]);
		}
		result2D.setStartPosition(newStartX, newStartY);
		result2D.setEndPosition(newEndX, newEndY);
		return result2D;
	}

	public double singleReduction(double a, double b) {
		return Math.max(a, b);
	}


}
