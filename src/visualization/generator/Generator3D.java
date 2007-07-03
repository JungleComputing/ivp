package visualization.generator;

import visualization.event.Event;
import visualization.event.ReducerEvent;
import visualization.event.Resolution3DEvent;
import visualization.result.reducer.Reducer3D;

/**
 * Generator3D is an abstract base class for all applications that
 * generate 3-dimensional data that should be visualized using a {@link visualization.visualizer.Visualizer3D}.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public abstract class Generator3D extends Generator2D {

	private int visualizerZResolution;
	private int generatorZResolution;

	/**
	 * Sets the dimension of the 3-dimensional data set.
	 *
	 * @param generatorXResolution	the resolution in the x dimension
	 * @param generatorYResolution	the resolution in the y dimension
	 * @param generatorZResolution	the resolution in the z dimension
	 */	
	public void setCalculationDimension(int generatorXResolution, int generatorYResolution, int generatorZResolution) {
		super.setCalculationDimension(generatorXResolution, generatorYResolution);
		this.generatorZResolution = generatorZResolution;
	}

	/**
	 * Gets the scale between the generator and visualizer for the z dimension.
	 * (note: generator / visualizer)
	 *
	 * @return the scale between the generator and visualizer for the z dimension
	 */	
	public double getZScale() {
		return generatorZResolution / (double) visualizerZResolution;
	}
	
	/**
	 * Gets the resolution of the visualizer for the z dimension.
	 *
	 * @return the resolution of the visualizer for the z dimension
	 */	
	public int getVisualizerZResolution() {
		return visualizerZResolution;
	}

	/**
	 * Handles an {@link ibis.ipl.Upcall}. 
	 * The Generator3D class handles the following events by itself:
	 * <li>{@link visualization.event.Resolution3DEvent}</li>
	 *
	 * @param event		an arrived event
	 */	
	public void doUpcall(Event event)  {
		super.doUpcall(event);
		if (event instanceof Resolution3DEvent) {
			visualizerZResolution = ((Resolution3DEvent) event).getZResolution();
			((Reducer3D) getReducer()).setZScale(getZScale());
		} else if (event instanceof ReducerEvent) {
			((Reducer3D) getReducer()).setZScale(getZScale());
		}
	}

}
