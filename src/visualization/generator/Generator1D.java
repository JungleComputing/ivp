package visualization.generator;

import visualization.event.*;
import visualization.result.reducer.*;
import visualization.result.*;

/**
 * Generator1D is an abstract base class for all applications that
 * generate 1-dimensional data that should be visualized using a {@link visualization.visualizer.Visualizer1D}.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public abstract class Generator1D extends Generator {

	private int visualizerXResolution, iterationskip = 1;
	private int generatorXResolution;

	private boolean synced = true;

	/**
	 * Sets the dimension of the 1-dimensional data set.
	 *
	 * @param generatorXResolution	the resolution in the x dimension
	 */	
	public void setCalculationDimension(int generatorXResolution) {
		this.generatorXResolution = generatorXResolution;
	}

	/**
	 * Gets the scale between the generator and visualizer for the x dimension.
	 * (note: generator / visualizer)
	 *
	 * @return the scale between the generator and visualizer for the x dimension
	 */	
	public double getXScale() {
		return generatorXResolution / (double) visualizerXResolution;
	}

	/**
	 * Gets the resolution of the visualizer for the x dimension.
	 *
	 * @return the resolution of the visualizer for the x dimension
	 */	
	public int getVisualizerXResolution() {
		return visualizerXResolution;
	}

	/**
	 * Gets the number of iterations to be skipped between frames. Iteration skip of 1 skips no frames.
	 *
	 * @return iterationskip		the number of iterations to be skipped between frames
	 */	
	public int getIterationSkip() {
		return iterationskip;
	}
	
	/**
	 * Sends the result data to the {@link visualization.visualizer.Visualizer1D}
	 *
	 * @param result	the result of the data generation.
	 */	
	public void sendData(Result result) {
		super.sendData(result);
		synced = false;
	}
	
	
	/**
	 * Blocks the thread until a {@link visualization.event.SyncEvent} is received
	 */		
	public void waitForSyncEvent() {
		while (!synced){
			try {
				wait();
			} catch (Exception e) {}
		}
	}

	/**
	 * Use this function for asynchronous visualizations
	 *
	 * @return	TRUE: if the Visualizer is in sync with the Generator. FALSE: otherwise  
	 */		
	public boolean isSynced() {
		return synced;
	}

	/**
	 * Invoked after the receipt of a {@link visualization.event.ResetEvent}.
	 * Should be overwritten for an actual implementation.
	 */
	public abstract void doReset();

	/**
	 * Handles an {@link ibis.ipl.Upcall}. 
	 * The Generator1D class handles the following events by itself:
	 * <li>{@link visualization.event.Resolution1DEvent}</li>
	 * <li>{@link visualization.event.IterationSkipEvent}</li>
 	 * <li>{@link visualization.event.ReducerEvent}</li>
	 * <li>{@link visualization.event.ResetEvent}</li>
	 * <li>{@link visualization.event.SyncEvent}</li>
	 *
	 * @param event		an arrived event
	 */	
	public void doUpcall(Event event)  {
		super.doUpcall(event);
		if (event instanceof Resolution1DEvent) {
			visualizerXResolution = ((Resolution1DEvent) event).getXResolution();
			((Reducer1D) getReducer()).setXScale(getXScale());
		} else if (event instanceof IterationSkipEvent) {
			iterationskip = ((IterationSkipEvent) event).getIterationSkip();
		} else if (event instanceof ResetEvent) {
			doReset();
		} else if (event instanceof ReducerEvent) {
			setReducer(((ReducerEvent) event).getReducer());
			((Reducer1D) getReducer()).setXScale(getXScale());
		} else if (event instanceof SyncEvent) {
			synced = true;
			try {
				notify();
			} catch (Exception e) {}
		}

	}

}
