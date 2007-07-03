package visualization.generator;

import java.awt.event.ActionListener;

import javax.swing.Timer;

import visualization.event.EnableEvent;
import visualization.event.Event;
import visualization.event.IntervalEvent;
import visualization.result.Result;
import visualization.result.ResultXY;

/**
 * GeneratorXY is an abstract base class for all applications that
 * generate XY data that should be visualized using a {@link visualization.visualizer.VisualizerXY}.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public abstract class GeneratorXY extends Generator {

	private int nrSources;
	private String[] name;
	private ActionListener[] actionListener;
	private int[] delay;
	private boolean[] enable;
	private Timer[] timer;

	/**
	 * Sets the number of sources that generate {@link visualization.result.ResultXY}s.
	 *
	 * @param nrSources	the number of sources
	 */	
	public void setSources(int nrSources) {
		this.nrSources = nrSources;
		actionListener = new ActionListener[nrSources];
		delay = new int[nrSources];
		enable = new boolean[nrSources];
		timer = new Timer[nrSources];
		name = new String[nrSources];
	}

	/**
	 * Sets the task performer for a given source.
	 * The number of sources should be set before.
	 *
	 * @param source the identifier of the source
	 * @param taskPerformer the task that should be performed for that source
	 */	
	public void setTaskPerformer(int source, ActionListener taskPerformer) {
		actionListener[source] = taskPerformer;
	}

	/**
	 * Sets the name of a given source.
	 *
	 * @param source the identifier of the source
	 * @param name the name of the source
	 */	
	public void setName(int source, String name) {
		this.name[source] = name;
	}

	/**
	 * Sets the delay of a given source.
	 *
	 * @param source the identifier of the source
	 * @param delay the delay in ms
	 */	
	public void setDelay(int source, int delay) {
		this.delay[source] = delay;
	}

	/**
	 * Sets whether the given source is enabled.
	 *
	 * @param source the identifier of the source
	 * @param enable TRUE: enabled, FALSE: disabled
	 */	
	public void setEnable(int source, boolean enable) {
		this.enable[source] = enable;
	}

	/**
	 * Start all sources (note that each source must have a taskPerformer,
	 * a delay and a name.)
	 */	
	public void startAllSources() {
		for (int i = 0; i < nrSources; i++) {
			start(i);
		}
	}

	/**
	 * Starts a specific source
	 *
	 * @param source the identifier of the source
	 */	
	public void start(int source) {
		timer[source] = new Timer(delay[source], actionListener[source]);
		timer[source].start();
	}

	/**
	 * Sends the result to the visualizer only if the source is enabled
	 *
	 * @param result the result to be sent
	 */	
	public void sendData(Result result) {
		ResultXY resultXY = (ResultXY) result;
		int source = getSourceByName(resultXY.getName());
		if (enable[source]) {
			super.sendData(result);
		}
	}

	/**
	 * Gets the source id for a given name
	 *
	 * @param name the name of the source
	 * @return the identifier of the source
	 */	
	private int getSourceByName(String name) {
		for (int i = 0; i < nrSources; i++) {
			if (this.name[i].equals(name)) return i;
		}
		return -1;
	}

	/**
	 * Handles an {@link ibis.ipl.Upcall}. 
	 * The GeneratorXY class can handle the following events by itself:
	 * <li>{@link visualization.event.IntervalEvent}</li>
	 * <li>{@link visualization.event.EnableEvent}</li>
	 *
	 * @param event		an arrived event
	 */	
	public void doUpcall(Event event)  {
		super.doUpcall(event);
		if (event instanceof IntervalEvent) {
			int source = getSourceByName(((IntervalEvent) event).getName());
			timer[source].setDelay(((IntervalEvent) event).getInterval());
		} else if (event instanceof EnableEvent) {
			int source = getSourceByName(((EnableEvent) event).getName());
			setEnable(source, ((EnableEvent) event).getEnable());
		}
	}

}
