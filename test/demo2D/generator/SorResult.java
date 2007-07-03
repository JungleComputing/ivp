import visualization.result.Result2D;

public class SorResult extends Result2D {

	private double stopdiff, maxdiff;
	private int iteration;

	public SorResult() {}

	public void setStopDiff(double stopdiff) {
		this.stopdiff = stopdiff;
	}

	public void setMaxDiff(double maxdiff) {
		this.maxdiff = maxdiff;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public double getStopDiff() {
		return stopdiff;
	}

	public double getMaxDiff() {
		return maxdiff;
	}

	public int getIteration() {
		return iteration;
	}
}