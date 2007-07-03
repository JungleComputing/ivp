package visualization.result.reducer;

import visualization.result.*;

/**
 * A Reducer3DMaximum can reduce a Result3D in its size.
 * It will modify the interior data so it represents the same
 * data, but then with a smaller size. It uses the maximum function to 
 * achieve reduction.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Reducer3DMaximum extends Reducer3D {

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
		Result3D result3D = (Result3D) result;
		int newStartX = (int) (result3D.getStartX() / getXScale());
		int newStartY = (int) (result3D.getStartY() / getYScale());
		int newStartZ = (int) (result3D.getStartZ() / getZScale());
		int newEndX = (int) (result3D.getEndX() / getXScale());
		int newEndY = (int) (result3D.getEndY() / getYScale());
		int newEndZ = (int) (result3D.getEndZ() / getZScale());
		double[] newMatrix = new double[(newEndX - newStartX + 1) * (newEndY - newStartY + 1) * (newEndZ - newStartZ + 1)];
		for (int z = newStartZ; z <= newEndZ; z++) {
			for (int y = newStartY; y <= newEndY; y++) {
				for (int x = newStartX; x <= newEndX; x++) {
					double max = Double.NEGATIVE_INFINITY;
					for (int j = Math.max(result3D.getStartZ(), (int) (z * getZScale())); j < Math.min(result3D.getEndZ() + 1, (int) ((z + 1) * getZScale())); j++) {
						for (int k = Math.max(result3D.getStartY(), (int) (y * getYScale())); k < Math.min(result3D.getEndY() + 1, (int) ((y + 1) * getYScale())); k++) {
							for (int l = Math.max(result3D.getStartX(), (int) (x * getXScale())); l < Math.min(result3D.getEndX() + 1, (int) ((x + 1) * getXScale())); l++) {
								max = Math.max(max, result3D.getMatrixValue((j - result3D.getStartZ()) * (result3D.getEndY() - result3D.getStartY() + 1) * (result3D.getEndX() - result3D.getStartX() + 1) +
									(k - result3D.getStartY()) * (result3D.getEndX() - result3D.getStartX() + 1) + l - result3D.getStartX()));
							}
						}
					}
					newMatrix[pos++] = max;
				}
			}
		}
		result3D.setMatrixSize(newMatrix.length);
		for (int i = 0; i < result3D.getMatrixSize(); i++) {
			result3D.setMatrixValue(i, newMatrix[i]);
		}
		result3D.setStartPosition(newStartX, newStartY, newStartZ);
		result3D.setEndPosition(newEndX, newEndY, newEndZ);
		return result3D;
	}

	public double singleReduction(double a, double b) {
		return Math.max(a, b);
	}


}
