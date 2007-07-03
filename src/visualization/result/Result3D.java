package visualization.result;

/**
 * A Generator3D generates Result3Ds that can be visualized by a Visualizer3D.
 * The Result3D class offers methods for a single 3 dimensional matrix of 
 * type double. The data internally is stored in a 1 dimensional matrix.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class Result3D extends Result2D {

	private int startZ, endZ;
	
	/**
	 * Sets the value of the start position of the result in the total result.
	 *
	 * @param x	the x coordinate of the start position
	 * @param y	the y coordinate of the start position
	 * @param z	the z coordinate of the start position
	 */
	public void setStartPosition(int x, int y, int z) {
		super.setStartPosition(x, y);
		this.startZ = z;
	}

	/**
	 * Sets the value of the end position of the result in the total result.
	 *
	 * @param x	the x coordinate of the end position
	 * @param y	the y coordinate of the end position
	 * @param z	the z coordinate of the end position
	 */
	public void setEndPosition(int x, int y, int z) {
		super.setEndPosition(x, y);
		this.endZ = z;
	}

	/**
	 * Gets the value of the z coordinate of the start position.
	 *
	 * @return the z coordinate of the start position
	 */
	public int getStartZ() {
		return startZ;
	}

	/**
	 * Gets the value of the z coordinate of the end position.
	 *
	 * @return the z coordinate of the end position
	 */
	public int getEndZ() {
		return endZ;
	}

}

