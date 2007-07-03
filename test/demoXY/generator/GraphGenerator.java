import visualization.generator.*;
import visualization.event.*;
import visualization.result.reducer.*;
import visualization.result.*;

import java.util.Date;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class GraphGenerator extends GeneratorXY {

	GraphGenerator() {
		ibisInit(null);
		setReducer(new Reducer());
		setSources(5);
		setTaskPerformer(0,
			new ActionListener() {

			private long time = new Date().getTime();

			public void actionPerformed(ActionEvent evt) {
				double myValue = Math.cos((double) (evt.getWhen() - time) / 1000);
				sendData(new ResultXY("cos", (double) (evt.getWhen() - time) / 1000, myValue));
			}
		});
		setDelay(0, 1000);
		setEnable(0, true);
		setName(0, "cos");
		setTaskPerformer(1,
			new ActionListener() {

			private long time = new Date().getTime();

			public void actionPerformed(ActionEvent evt) {
				double myValue = Math.sin((double) (evt.getWhen() - time) / 1000);
				sendData(new ResultXY("sin", (double) (evt.getWhen() - time) / 1000, myValue));
			}
		});
		setDelay(1, 1000);
		setEnable(1, true);
		setName(1, "sin");
		setTaskPerformer(2,
			new ActionListener() {

			private long time = new Date().getTime();

			public void actionPerformed(ActionEvent evt) {
				double myValue = Math.random() * 1.0;
				sendData(new ResultXY("mem", (double) (evt.getWhen() - time) / 1000, myValue));
			}
		});
		setDelay(2, 1000);
		setEnable(2, true);
		setName(2, "mem");
		setTaskPerformer(3,
			new ActionListener() {

			private long time = new Date().getTime();

			public void actionPerformed(ActionEvent evt) {
				double myValue = Math.random() * 1.0;
				sendData(new ResultXY("cpu", (double) (evt.getWhen() - time) / 1000, myValue));
			}
		});
		setDelay(3, 1000);
		setEnable(3, true);
		setName(3, "cpu");
		setTaskPerformer(4,
			new ActionListener() {

			private long time = new Date().getTime();

			public void actionPerformed(ActionEvent evt) {
				double myValue = 2.0 * Math.cos((double) (evt.getWhen() - time) / 1000);
				sendData(new ResultXY("var", (double) (evt.getWhen() - time) / 1000, myValue));
			}
		});
		setDelay(4, 1000);
		setEnable(4, true);
		setName(4, "var");
	}

	public void start() {
		waitForStartEvent();
		startAllSources();
		waitForExitEvent();
		ibisExit();
		try {
			ibis.end();
		} catch (Exception e) {}
		System.exit(1);
	}

	public static void main(String[] args) {
		new GraphGenerator().start();
	}

}
