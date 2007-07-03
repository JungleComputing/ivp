import visualization.visualizer.*;
import visualization.result.*;
import visualization.event.*;

import vtk.*;

import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Plot {

	static { 
		System.loadLibrary("vtkCommonJava"); 
		System.loadLibrary("vtkFilteringJava"); 
		System.loadLibrary("vtkIOJava"); 
		System.loadLibrary("vtkImagingJava"); 
		System.loadLibrary("vtkGraphicsJava"); 
		System.loadLibrary("vtkRenderingJava"); 
	}

	private PlotVariable[] vars;
	private double colorR, colorG, colorB;
	private vtkXYPlotActor xyplot;
	private vtkCanvas renwin;
	private int myPosition, totalPositions;
	private String xLabel, yLabel, titleLabel;

	public Plot(int myPosition, int totalPositions) {
		this.myPosition = myPosition;
		this.totalPositions = totalPositions;
		xyplot = new vtkXYPlotActor();
	}

	public void setTitles(String titleLabel, String xLabel, String yLabel) {
		this.titleLabel = titleLabel;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
	}

	public vtkXYPlotActor getPlotActor() {
		for (int i = 0; i < vars.length; i++) {
			xyplot.AddDataObjectInput(vars[i].getDataObject());
			xyplot.SetDataObjectXComponent(i, 0);
			xyplot.SetDataObjectYComponent(i, 1);
			xyplot.SetPlotColor(i, vars[i].getColors());
		}
		xyplot.SetYRange(-2.0, 2.0);
		xyplot.SetTitle(titleLabel);
		xyplot.SetXTitle(xLabel);
		xyplot.SetYTitle(yLabel);
		xyplot.SetXValuesToValue();

		xyplot.GetPositionCoordinate().SetCoordinateSystemToNormalizedViewport();
		xyplot.GetPositionCoordinate().SetValue(0.1, (double) (totalPositions - myPosition - 1) / (double) totalPositions);
		xyplot.GetPosition2Coordinate().SetCoordinateSystemToNormalizedViewport();
		xyplot.SetWidth(0.8);
		xyplot.SetHeight(0.8 / totalPositions);
		xyplot.GetProperty().SetColor(colorR, colorG, colorB);
		return xyplot;
	}

	public void setNrVars(int n) {
		vars = new PlotVariable[n];
	}

	public void setVariable(int i, String s, int interval, VisualizerXY visualizer) {
		vars[i] = new PlotVariable(s, interval, visualizer);
		if (i == 0) {
			vars[i].setColor(1.0, 0.5, 0.5);
		} else if (i == 1) {
			vars[i].setColor(0.5, 0.5, 1.0);
		} else if (i == 2) {
			vars[i].setColor(0.5, 1.0, 0.5);
		}

	}

	public void setColor(double r, double g, double b) {
		this.colorR = r;
		this.colorG = g;
		this.colorB = b;
	}

	public JPanel getPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.PAGE_AXIS));
		for (int i = 0; i < vars.length; i++) {
			buttonPane.add(vars[i].getPane());
		}
		return buttonPane;
	}

	private void generateBounds() {
		double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		for (PlotVariable var : vars) {
			if (var.isEnabled()) {
				xMin = Math.min(var.getXMin(), xMin);
				xMax = Math.max(var.getXMax(), xMax);
			}
		}
		xyplot.SetXRange(xMin, xMax);
	}


	public boolean addValueToVar(double x, double y, String name) {
		for (PlotVariable var : vars) {
			if (var.getName().equals(name)) {
				var.addValue(x, y);
				generateBounds();
				return true;
			}
		}
		return false;
	}

}
