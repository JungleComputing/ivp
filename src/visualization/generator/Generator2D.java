package visualization.generator;

import visualization.event.*;
import visualization.result.reducer.*;
import visualization.result.*;

/**
 * Generator2D is an abstract base class for all applications that
 * generate 2-dimensional data that should be visualized using a {@link visualization.visualizer.Visualizer2D}.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public abstract class Generator2D extends Generator1D {

	private int visualizerYResolution;
	private int generatorYResolution;

	/**
	 * Sets the dimension of the 2-dimensional data set.
	 *
	 * @param generatorXResolution	the resolution in the x dimension
	 * @param generatorYResolution	the resolution in the y dimension
	 */	
	public void setCalculationDimension(int generatorXResolution, int generatorYResolution) {
		super.setCalculationDimension(generatorXResolution);
		this.generatorYResolution = generatorYResolution;
	}

	/**
	 * Gets the scale between the generator and visualizer for the y dimension.
	 * (note: generator / visualizer)
	 *
	 * @return the scale between the generator and visualizer for the y dimension
	 */	
	public double getYScale() {
		return generatorYResolution / (double) visualizerYResolution;
	}
	
	/**
	 * Gets the resolution of the visualizer for the y dimension.
	 *
	 * @return the resolution of the visualizer for the y dimension
	 */	
	public int getVisualizerYResolution() {
		return visualizerYResolution;
	}


	
	/**
	 * Handles an {@link ibis.ipl.Upcall}. 
	 * The Generator2D class can handle the following events by itself:
	 * <li>{@link visualization.event.Resolution2DEvent}</li>
	 * <li>{@link visualization.event.StartEvent}</li>
	 * <li>{@link visualization.event.PauseEvent}</li>
	 * <li>{@link visualization.event.ResumeEvent}</li>
	 * <li>{@link visualization.event.IterationSkipEvent}</li>
 	 * <li>{@link visualization.event.ReducerEvent}</li>
	 * <li>{@link visualization.event.ResetEvent}</li>
	 * <li>{@link visualization.event.SyncEvent}</li>
	 * <li>{@link visualization.event.ExitEvent}</li>
	 *
	 * @param event		an arrived event
	 */	
	public void doUpcall(Event event)  {
		super.doUpcall(event);
		if (event instanceof Resolution2DEvent) {
			visualizerYResolution = ((Resolution2DEvent) event).getYResolution();
			((Reducer2D) getReducer()).setYScale(getYScale());
		} else if (event instanceof ReducerEvent) {
			((Reducer2D) getReducer()).setYScale(getYScale());
		}
	}

}
