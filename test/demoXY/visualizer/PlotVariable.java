import visualization.visualizer.*;
import visualization.result.*;
import visualization.event.*;

import vtk.*;

import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlotVariable implements ActionListener {

	static { 
		System.loadLibrary("vtkCommonJava"); 
		System.loadLibrary("vtkFilteringJava"); 
		System.loadLibrary("vtkIOJava"); 
		System.loadLibrary("vtkImagingJava"); 
		System.loadLibrary("vtkGraphicsJava"); 
		System.loadLibrary("vtkRenderingJava"); 
	}
	
	public static final int FIXED_TUPLES = 1;
	public static final int APPEND_TUPLES = 2;
	
	private String name;
	private int interval, nrTuples;
	private boolean enabled;
	private double colorR, colorG, colorB;
	private vtkDataArray xData, yData;
	private VisualizerXY visualizer;

	public PlotVariable(String name, int interval, VisualizerXY visualizer) {
		this(name, interval, true, 1.0, 1.0, 1.0, null, null, 10, visualizer);
	}

	public PlotVariable(String name, int interval, boolean enabled, double colorR, double colorG, double colorB, vtkDataArray xData, vtkDataArray yData, int nrTuples, VisualizerXY visualizer) {
		this.name = name;
		this.interval = interval;
		this.enabled = enabled;
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
		this.xData = xData;
		this.yData = yData;
		this.nrTuples = nrTuples;
		this.visualizer = visualizer;
	}

	public void setColor(double r, double g, double b) {
		this.colorR = r;
		this.colorG = g;
		this.colorB = b;
	}

	public void setEnable(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled(){
		return enabled;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getName() {
		return name;
	}

	public double[] getColors() {
		double[] result = new double[3];
		result[0] = colorR;
		result[1] = colorG;
		result[2] = colorB;
		return result;
	}

	public vtkDataObject getDataObject () {
		vtkFloatArray xFloat = new vtkFloatArray();
		vtkFloatArray yFloat = new vtkFloatArray();
		xData = xFloat.CreateDataArray(10); //VTK_FLOAT
		xData.SetNumberOfTuples(nrTuples);
		yData = yFloat.CreateDataArray(10); //VTK_FLOAT
		yData.SetNumberOfTuples(nrTuples);
		for (int d = 0; d < nrTuples; d++) {
			xData.SetTuple1(d, 0.0);
			yData.SetTuple1(d, 0.0);
		}
		vtkFieldData fieldData = new vtkFieldData();
		fieldData.AllocateArrays(2);
		fieldData.AddArray(xData);
		fieldData.AddArray(yData);
		vtkDataObject dataObject = new vtkDataObject();
		dataObject.SetFieldData(fieldData);
		return dataObject;
	}

	public JPanel getPane() {
		/*
		 * icon images from http://tango.freedesktop.org/Tango_Icon_Gallery
		 */
		String iconSize = "small";
		JPanel result = new JPanel();
		JCheckBox checkBox = new JCheckBox(name, true);
		checkBox.addActionListener(this);
		checkBox.setActionCommand("Enable");
		result.setLayout(new BoxLayout(result, BoxLayout.LINE_AXIS));
		result.add(checkBox);
		result.add(Box.createRigidArea(new Dimension(0, 5)));
		result.add(createButton("Increase", "list-add.png", iconSize, "Higher granularity"));
		result.add(Box.createRigidArea(new Dimension(0, 5)));
		result.add(createButton("Decrease", "list-remove.png", iconSize, "Lower granularity"));
		result.setAlignmentX(Component.RIGHT_ALIGNMENT);
		return result;
	}
	
	public JButton createButton(String name, String iconFileName, String iconSize, String toolTip) {
		JButton button = new JButton(createImageIcon(iconFileName, iconSize));
		button.setActionCommand(name);
		button.setToolTipText(toolTip);
		button.addActionListener(this);
		return button;
	}

	public ImageIcon createImageIcon(String iconFileName, String iconSize) {
		return new ImageIcon("../images/" + iconSize + "/" + iconFileName);
	}

	public void addValue(double x, double y) {
		int mode = FIXED_TUPLES;
		if (mode == FIXED_TUPLES) {
			for (int i = 1; i < nrTuples; i++) {
				yData.SetTuple1(i - 1, yData.GetTuple1(i));
				xData.SetTuple1(i - 1, xData.GetTuple1(i));
			}
			yData.SetTuple1(nrTuples - 1, y);
			xData.SetTuple1(nrTuples - 1, x);
			yData.Modified();
			xData.Modified();
		} else if (mode == APPEND_TUPLES) {
			nrTuples++;
			yData.InsertTuple1(nrTuples - 1, y);
			xData.InsertTuple1(nrTuples - 1, x);
			yData.Modified();
			xData.Modified();
		}
	}

	public double getXMin() {
		return xData.GetTuple1(0);
	}

	public double getXMax() {
		return xData.GetTuple1(nrTuples - 1);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Increase")) {
			interval = interval / 2;
			visualizer.sendEvent(new IntervalEvent(name, interval));
		} else if (e.getActionCommand().equals("Decrease")) {
			interval = interval * 2;
			visualizer.sendEvent(new IntervalEvent(name, interval));
		} else if (e.getActionCommand().equals("Enable")) {
			enabled = !enabled;
			visualizer.sendEvent(new EnableEvent(name, enabled));
		}
	}



}
