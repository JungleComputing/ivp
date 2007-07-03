package visualization.generator;

import visualization.event.*;
import visualization.result.reducer.*;
import visualization.result.*;
import ibis.ipl.*;
import ibis.util.PoolInfo;
import ibis.util.Timer;
import ibis.util.TypedProperties;
import java.io.IOException;

/**
 * Generator is an abstract base class for all applications that
 * generate data that should be visualized using a {@link visualization.visualizer.Visualizer}.
 * A Generator uses Ibis to communicate with the Visualizer.
 *
 * @author		Roelof Kemp
 * @version		0.1
 **/
public abstract class Generator {

    final static boolean TIMINGS = TypedProperties.booleanProperty("timing", false);

	private Timer ivp_reduce = Timer.createTimer();
	private Timer ivp_communicate = Timer.createTimer();

	private Result result;
	private SendPort sendPort;
	private ReceivePort receivePort;
	private Reducer reducer;
	public Ibis ibis;
	public int size, rank;
	private boolean started, resumed = true, exited;

	public void resetTimers() {
		ivp_reduce.reset();
		ivp_communicate.reset();
	}
	
	public void reportTimings() {
		if (!TIMINGS) return;
		System.err.format("%d   reduce:          %s\n%d   communicate:     %s\n", 
				rank,
				ivp_reduce.totalTime(),
				rank,
				ivp_communicate.totalTime());	
	}



	/**
	 * Initiates the Ibis environment.
	 *
	 * @param props		the properties Ibis uses using its initialization.
	 *					If <code>null</code>, default values are used
	 */	
	public void ibisInit(StaticProperties props) {
		try {
			PoolInfo info = PoolInfo.createPoolInfo();

			rank = info.rank() - 1;
			size = info.size() - 1;

			if (props == null) {
				props = new StaticProperties();
				props.add("serialization", "object");
				props.add("worldmodel", "closed");
				props.add("communication", "OneToMany, ManyToOne, Reliable, AutoUpcalls");
			}
			ibis = Ibis.createIbis(props, null);
			Registry registry = ibis.registry();

			StaticProperties recvProps = new StaticProperties();
			recvProps.add("communication", "OneToMany, Reliable, AutoUpcalls");
			recvProps.add("serialization", "object");
			StaticProperties sendProps = new StaticProperties();
			sendProps.add("communication", "ManyToOne, Reliable, AutoUpcalls");
			sendProps.add("serialization", "object");
			PortType sendType = ibis.createPortType("visualization-M2O", sendProps);
			PortType recvType = ibis.createPortType("visualization-O2M", recvProps);
			sendPort = sendType.createSendPort();
			receivePort = recvType.createReceivePort("visualization-Generator-" + rank, new DataUpcall());
			receivePort.enableConnections();
			receivePort.enableUpcalls();
			ReceivePortIdentifier server = registry.lookupReceivePort("visualization-Visualizer");
			sendPort.connect(server);
		} catch (Exception e) {}
	}

	/**
	 * Closes the Ibis environment.
	 */	
	public void ibisExit() {
		try {
			sendPort.close();
			receivePort.close();
		}
		catch (Exception e) {}
	}

	/**
	 * Sends generated data located in the <code>result</code> to the Visualizer.
	 * Reduces the data before sending if a {@link visualization.result.reducer.Reducer} is set.
	 *
	 * @param result	the result of the data generation
	 */	
	public void sendData(Result result) {
		this.result = result;
		new Thread() {
			public void run() {
				try {
					if (TIMINGS) ivp_reduce.start();
					Result reducedResult = reducer.reduce(Generator.this.result);
					if (TIMINGS) ivp_reduce.stop();
					if (TIMINGS) ivp_communicate.start();
					WriteMessage w = sendPort.newMessage();
					w.writeObject(reducedResult);
					if (TIMINGS) ivp_communicate.stop();
					w.finish();
				} catch (Exception e) {}
			}
		}.start();
	}

	/**
	 * Sets a Reducer that is used before data is actually sent
	 *
	 * @param reducer	a reducer
	 */	
	public void setReducer(Reducer reducer) {
		this.reducer = reducer;
	}

	/**
	 * Blocks the thread until a {@link visualization.event.StartEvent} is received
	 */		
	public void waitForStartEvent() {
		started = false;
		while (!started){
			try {
				wait();
			} catch (Exception e) {}
		}
	}

	/**
	 * Blocks the thread until an {@link visualization.event.ExitEvent} is received
	 */		
	public void waitForExitEvent() {
		exited = false;
		while (!exited){
			try {
				wait();
			} catch (Exception e) {}
		}
	}

	/**
	 * Blocks the thread until a {@link visualization.event.ResumeEvent} is received
	 */		
	public void waitForResumeEvent() {
		while (!resumed){
			try {
				wait();
			} catch (Exception e) {}
		}
	}

	
	/**
	 * Gets the Reducer
	 *
	 * @return the reducer
	 */	
	public Reducer getReducer() {
		return reducer;
	}

	/**
	 * Handles an {@link ibis.ipl.Upcall}. 
	 * The Generator class handles the following events by itself:
	 * <li>{@link visualization.event.StartEvent}</li>
	 * <li>{@link visualization.event.PauseEvent}</li>
 	 * <li>{@link visualization.event.ResumeEvent}</li>
	 * <li>{@link visualization.event.ExitEvent}</li>
	 *
	 * @param event		an arrived event
	 */	
	public void doUpcall(Event event) {
		if (event instanceof StartEvent) {
			started = true;
			try {
				notify();
			} catch (Exception e) {}
		} else if (event instanceof PauseEvent) {
			resumed = false;
		} else if (event instanceof ResumeEvent) {
			resumed = true;
			try {
				notify();
			} catch (Exception e) {}
		} else if (event instanceof ExitEvent) {
			exited = true;
			try {
				notify();
			} catch (Exception e) {}
		}
	}

	private class DataUpcall implements ibis.ipl.Upcall {

		DataUpcall() {}

		public void upcall(ReadMessage m) throws IOException {
			Event event = null; 
			try {
				event = (Event) m.readObject();
				m.finish();
			} catch (Exception e) {
				System.err.println("upcall: " + e.toString());
			}
			doUpcall(event);
		}
	}
	
}
