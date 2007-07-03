package visualization.result;

/**
 * A GeneratorXY generates ResultXYs that can be visualized by a VisualizerXY.
 * A ResultXY is a point specified by an x and y coordinate for a given variable
 * specified by its name.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class ResultXY extends Result {

	private String name;
	private double x, y;

	/**
	 * Constructs a new <code>ResultXY</code> for the variable specified by the
	 * name and with the coordinates [x, y].
	 *
	 * @param name	the name of the variable
	 * @param x		the x-coordinate
	 * @param y		the y-coordinate
	 */	
	public ResultXY(String name, double x, double y){
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Gets the name of the variable
	 *
	 * @return the name of the variable
	 */	
	public String getName() {
		return name;
	}

	/**
	 * Gets the x-coordinate.
	 *
	 * @return the x-coordinate
	 */	
	public double getX() {
		return x;
	}

	/**
	 * Gets the y-coordinate.
	 *
	 * @return the y-coordinate
	 */	
	public double getY() {
		return y;
	}
}

