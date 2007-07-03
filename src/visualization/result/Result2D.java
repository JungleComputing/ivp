package visualization.result;

/**
 * A Generator2D generates Result2Ds that can be visualized by a Visualizer2D.
 * The Result2D class offers methods for a single 2 dimensional matrix of 
 * type double. The data internally is stored in a 1 dimensional matrix.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Result2D extends Result1D {

	private int startY, endY;

	/**
	 * Sets the value of the start position of the result in the total result.
	 *
	 * @param x	the x coordinate of the start position
	 * @param y	the y coordinate of the start position
	 */
	public void setStartPosition(int x, int y) {
		super.setStartPosition(x);
		this.startY = y;
	}

	/**
	 * Sets the value of the end position of the result in the total result.
	 *
	 * @param x	the x coordinate of the end position
	 * @param y	the y coordinate of the end position
	 */
	public void setEndPosition(int x, int y) {
		super.setEndPosition(x);
		this.endY = y;
	}

	/**
	 * Gets the value of the y coordinate of the start position.
	 *
	 * @return the y coordinate of the start position
	 */
	public int getStartY() {
		return startY;
	}

	/**
	 * Gets the value of the y coordinate of the end position.
	 *
	 * @return the y coordinate of the end position
	 */
	public int getEndY() {
		return endY;
	}
}

