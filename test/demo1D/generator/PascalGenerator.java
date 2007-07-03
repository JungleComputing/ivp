import visualization.generator.*;
import visualization.event.*;
import visualization.result.reducer.*;
import visualization.result.*;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class PascalGenerator extends Generator1D {

	Timer timer;

	private Result1D previousResult = new Result1D();

	PascalGenerator() {
		ibisInit(null);
		setReducer(new Reducer1DAverage());
		setCalculationDimension(100);
		previousResult.setMatrixSize(100);
		previousResult.setStartPosition(0);
		previousResult.setEndPosition(99);
		previousResult.setMatrixValue(49, 1);
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Result1D result = new Result1D();
				result.setMatrixSize(100);
				result.setStartPosition(0);
				result.setEndPosition(99);
				for (int i = 0; i < result.getMatrixSize(); i++) {
					int ones = 0;
					for (int j = Math.max(0, i - 1); j < Math.min(i + 2, result.getMatrixSize()); j++) {
						ones += (int) previousResult.getMatrixValue(j);
					}
					if (ones == 1 && i != 49) result.setMatrixValue(i, 1);
					else result.setMatrixValue(i, 0);
				}
				for (int i = 0; i < result.getMatrixSize(); i++) {
					previousResult.setMatrixValue(i, result.getMatrixValue(i));
				}
				sendData(result);
			}
		}); 
	}

	public void start() {
		waitForStartEvent();
		timer.start();
		waitForExitEvent();
		ibisExit();
		try {
			ibis.end();
		} catch (Exception e) {}
		System.exit(1);
	}

	public void doReset() {}

	public static void main(String[] args) {
		new PascalGenerator().start();
	}

}
