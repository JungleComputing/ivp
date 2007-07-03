package visualization.result;

/**
 * A Generator1D generates Result1Ds that can be visualized by a Visualizer1D.
 * The Result1D class offers methods for a single 1 dimensional matrix of 
 * type double. 
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Result1D extends Result {

	private double[] matrix;
	private int startX, endX;

	
	/**
	 * Sets the dimension of the 1-dimensional matrix.
	 *
	 * @param size	the size
	 */	
	public void setMatrixSize(int size) {
		matrix = new double[size];
	}

	/**
	 * Gets the dimension of the 1-dimensional matrix.
	 *
	 * @return the size
	 */	
	public int getMatrixSize() {
		return matrix.length;
	}

	/**
	 * Sets the value of position <code>pos</code> to <code>value</value>.
	 *
	 * @param pos	the position in the matrix
	 * @param value the value
	 */	
	public void setMatrixValue(int pos, double value) {
		matrix[pos] = value;
	}

	/**
	 * Gets the value of position <code>pos</code>
	 *
	 * @param pos	the position in the matrix
	 * @return the value
	 */	
	public double getMatrixValue(int pos) {
		return matrix[pos];
	}

	/**
	 * Sets the value of the start position of the result in the total result.
	 *
	 * @param x	the x coordinate of the start position
	 */
	public void setStartPosition(int x) {
		this.startX = x;
	}

	/**
	 * Sets the value of the end position of the result in the total result.
	 *
	 * @param x	the x coordinate of the end position
	 */
	public void setEndPosition(int x) {
		this.endX = x;
	}

	/**
	 * Gets the value of the x coordinate of the start position.
	 *
	 * @return the x coordinate of the start position
	 */
	public int getStartX() {
		return startX;
	}

	/**
	 * Gets the value of the x coordinate of the end position.
	 *
	 * @return the x coordinate of the end position
	 */
	public int getEndX() {
		return endX;
	}

}

