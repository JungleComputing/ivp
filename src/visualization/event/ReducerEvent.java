package visualization.event;

import visualization.result.reducer.*;

/**
 * Using a reduction method event the {@link visualization.visualizer.Visualizer} can inform the
 * {@link visualization.generator.Generator} about which reducer it should use.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public class ReducerEvent extends Event {
	private Reducer reducer;

	/**
	 * Constructs a <code>ReducerEvent</code> which informs the Generator that it should use
	 * the Reducer <code>reducer</code>
	 *
	 * @param reducer		the new Reducer
	 */	
	public ReducerEvent(Reducer reducer) {
		super("Reducer");
		this.reducer = reducer;
	}

	/**
	 * Gets the reducer
	 *
	 * @return the reducer
	 */	
	public Reducer getReducer() {
		return reducer;
	}

}
