import visualization.visualizer.*;
import visualization.result.reducer.*;
import visualization.result.*;
import visualization.event.*;
import vtk.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.border.TitledBorder;
import javax.swing.*;

public class PascalVisualizer extends Visualizer1D implements ActionListener {

	static { 
		System.loadLibrary("vtkCommonJava"); 
		System.loadLibrary("vtkFilteringJava"); 
		System.loadLibrary("vtkIOJava"); 
		System.loadLibrary("vtkImagingJava"); 
		System.loadLibrary("vtkGraphicsJava"); 
		System.loadLibrary("vtkRenderingJava"); 
	}

	public static final long serialVersionUID = 1L;

	private final JFileChooser fc = new JFileChooser(); // shared GUI variables
	JLabel resolutionLabel, fpsLabel;
	JFrame frame;
	boolean recording = false, resolution = false;

	int xResolution, nrResults, nrFrames;

	vtkDoubleArray array[];								// shared Visualization variables
	vtkImageData imageData[];
	vtkActor actor[];
	vtkTexture texture[];
	vtkLookupTable lut;
	vtkCanvas renWin;
	vtkMPEG2Writer mpeg2Writer;
	vtkWindowToImageFilter mpeg2Filter;

	/********************************************
	 * GUI methods
	 ********************************************/
	private static void createAndShowGUI() {
		/* 
		 * JPopupMenu.setDefaultLightWeightPopupEnabled(false) is necessary to make the vtkPanel (awt.Canvas) and swing work together 
		 * for further information see: http://public.kitware.com/pipermail/vtkusers/2002-March/059595.html 
		 */
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		PascalVisualizer pascalVisualizer = pascalVisualizer = new PascalVisualizer();
		
		pascalVisualizer.frame = new JFrame("Pascal Visualizer");
		pascalVisualizer.frame.setJMenuBar(pascalVisualizer.createMenuBar());

		pascalVisualizer.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pascalVisualizer.frame.setSize(500, 500);
		pascalVisualizer.frame.setContentPane(pascalVisualizer);
		pascalVisualizer.frame.setVisible(true);
    }

	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("File menu");
		menuBar.add(menu);

		menu.add(createMenuItem("Export...", KeyEvent.VK_E, KeyEvent.VK_E, ActionEvent.CTRL_MASK, "Export to image"));
		menu.add(createMenuItem("Exit", KeyEvent.VK_X, KeyEvent.VK_F4, ActionEvent.ALT_MASK, "Exit application"));

		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("Viewing menu");
		menuBar.add(menu);

		menu.add(createMenuItem("Reset", KeyEvent.VK_R, KeyEvent.VK_R, ActionEvent.CTRL_MASK, "Reset camera"));

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Help menu");
		menuBar.add(menu);

		menu.add(createMenuItem("How to use...", KeyEvent.VK_U, KeyEvent.VK_F1, 0, "help"));

		return menuBar;
	}
	
	public JMenuItem createMenuItem(String name, int keyEvent, int acceleratorKey, int acceleratorMask, String description) {
		JMenuItem menuItem = new JMenuItem(name, keyEvent);
		if (acceleratorKey != 0) menuItem.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, acceleratorMask));
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		menuItem.addActionListener(this);
		return menuItem;
	}

	
	public void createPropertiesPane() {
		JPanel propertiesPane = new JPanel();
		propertiesPane.setLayout(new BoxLayout(propertiesPane, BoxLayout.PAGE_AXIS));
		propertiesPane.setPreferredSize(new Dimension(200,0));
		JPanel visPropertiesPane = new JPanel();
		visPropertiesPane.setLayout(new BoxLayout(visPropertiesPane, BoxLayout.PAGE_AXIS));
		visPropertiesPane.setBorder(new TitledBorder("visualization"));
		visPropertiesPane.add(resolutionLabel = new JLabel(String.format("Resolution: %d", xResolution)));
		visPropertiesPane.add(Box.createRigidArea(new Dimension(0, 15)));
		visPropertiesPane.add(fpsLabel = new JLabel("0 fps"));
		visPropertiesPane.add(Box.createRigidArea(new Dimension(0, 15)));
		visPropertiesPane.add(new JLabel("Reduction method:"));
		JRadioButton method1 = new JRadioButton("Average");
		method1.addActionListener(this);
		visPropertiesPane.add(method1);
		JRadioButton method2 = new JRadioButton("Max");
		method2.addActionListener(this);
		visPropertiesPane.add(method2);
		method2.setSelected(true);
		JRadioButton method3 = new JRadioButton("Min");
		method3.addActionListener(this);
		visPropertiesPane.add(method3);
		ButtonGroup group = new ButtonGroup();
		group.add(method1);
		group.add(method2);
		group.add(method3);
		visPropertiesPane.add(new Box.Filler(null, new Dimension(100, 0), null));
		propertiesPane.add(visPropertiesPane);

		add(propertiesPane, BorderLayout.EAST);
	}

	public void createButtonPane() {
		/*
		 * icon images from http://tango.freedesktop.org/Tango_Icon_Gallery
		 */
		String iconSize = "medium";
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(createButton("ButtonReset", "media-skip-backward.png", iconSize, "Reset data to initial"));
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(createButton("ButtonRecord", "media-record.png", iconSize, "Start recording a movie"));
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(createButton("ButtonStart", "media-playback-start.png", iconSize, "Start the visualization"));
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(createButton("ButtonSnapshot", "camera-photo.png", iconSize, "Take a snapshot of the visualization"));
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(createButton("ResolutionIncrease", "list-add.png", iconSize, "Higher resolution"));
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(createButton("ResolutionDecrease", "list-remove.png", iconSize, "Lower resolution"));
		add(buttonPane, BorderLayout.SOUTH);
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


	public PascalVisualizer() {
		xResolution = 50;
		nrResults = 30;
		setStartResolution(xResolution);


		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fpsLabel.setText(String.format("%d fps", nrFrames));
				nrFrames = 0;
			}
		};
		Timer t = new Timer(1000, taskPerformer);
		t.start();

		// GUI init
		setLayout(new BorderLayout());
		createPropertiesPane();
		createButtonPane();
		renWin = new vtkCanvas();

		// visualization init
		// create plane where sor field is mapped onto
		vtkPlaneSource plane[] = new vtkPlaneSource[nrResults];
		vtkPolyDataMapper mapper[] = new vtkPolyDataMapper[nrResults];
		array = new vtkDoubleArray[nrResults];
		imageData = new vtkImageData[nrResults];
		texture = new vtkTexture[nrResults];
		actor = new vtkActor[nrResults];

		lut = new vtkLookupTable();
		lut.SetTableRange(0, 1);
		lut.SetHueRange(0.25, 1.0);
		lut.SetSaturationRange(1, 1);
		lut.SetValueRange(1, 1);
		lut.Build(); 

		for (int i = 0; i < nrResults; i++) {
			plane[i] = new vtkPlaneSource();
			plane[i].SetXResolution(1);
			plane[i].SetYResolution(1);
			plane[i].SetOrigin(0, 0, 0);
			plane[i].SetPoint1(0, nrResults, 0);
			plane[i].SetPoint2(1, 0, 0);

			// create the mapper
			mapper[i] = new vtkPolyDataMapper();
			mapper[i].SetInput(plane[i].GetOutput());

			// create the data array
			array[i] = new vtkDoubleArray();
			array[i].SetJavaArray(duplicate(getDataArray()));

			// put it in an ImageData object
			imageData[i] = new vtkImageData();
			imageData[i].SetDimensions(xResolution, 2, 1);
			imageData[i].SetScalarTypeToDouble();
			imageData[i].SetOrigin(0, 0, 0);
			imageData[i].GetPointData().SetScalars(array[i]);

			texture[i] = new vtkTexture();	
			texture[i].SetInput(imageData[i]);
			texture[i].InterpolateOn();
			texture[i].SetLookupTable(lut);

			actor[i] = new vtkActor();
			actor[i].SetMapper(mapper[i]);
			actor[i].SetTexture(texture[i]);
			actor[i].SetPosition(10 + i,70,0);
			renWin.GetRenderer().AddActor(actor[i]);
		}

		vtkScalarBarActor scalarBar = new vtkScalarBarActor();
		scalarBar.SetLookupTable(lut);
		scalarBar.GetPositionCoordinate().SetCoordinateSystemToNormalizedViewport();
		scalarBar.GetPositionCoordinate().SetValue(0.1, 0.01);
		scalarBar.GetPosition2Coordinate().SetCoordinateSystemToNormalizedViewport();
		scalarBar.SetWidth(0.8);
		scalarBar.SetHeight(0.1);
		scalarBar.SetOrientationToHorizontal();
		renWin.GetRenderer().AddViewProp(scalarBar);
		
		renWin.GetRenderer().ResetCamera();

		// Modify some bindings, use the interactor style 'switch'
		vtkGenericRenderWindowInteractor iren = renWin.getIren();
		vtkInteractorStyleSwitch istyle = new vtkInteractorStyleSwitch();

		iren.SetInteractorStyle(istyle);
		istyle.SetCurrentStyleToTrackballCamera();

		add(renWin, BorderLayout.CENTER);

		ibisInit();
	}

	public void doRender() {
		renWin.lock();
		if (recording) {
			mpeg2Filter.Modified();
			mpeg2Writer.Write();
		}
		renWin.unlock();				
		renWin.Render();
		/* if the resolution has changed in the timeslot before, apply this changes now (right after the render) */
		if (resolution) {
			imageData[nrResults - 1].SetDimensions(xResolution, 2, 1);
			resolutionLabel.setText(String.format("Resolution: %d", xResolution));
			resolution = false;
		}
	}

	public void doLastUpcall(Result result) {
		Result1D result1D = (Result1D) result;
		for (int i = 0; i < nrResults - 1; i++) {
			array[i].SetJavaArray(array[i + 1].GetJavaArray());
			imageData[i].SetDimensions(imageData[i + 1].GetDimensions());
			imageData[i].Modified();
		}
		
		array[nrResults - 1].SetJavaArray(duplicate(getDataArray()));
		imageData[nrResults - 1].SetDimensions(xResolution, 2, 1);
		imageData[nrResults - 1].Modified();
		nrFrames++;
	}

	public double[] duplicate (double[] d) {
		double[] result = new double[2 * d.length];
		System.arraycopy(d, 0, result, 0, d.length);
		System.arraycopy(d, 0, result, d.length, d.length);
		return result;
	}

	public void actionPerformed(ActionEvent e) {
		if ("ResolutionIncrease".equals(e.getActionCommand())) {
			xResolution *= 2.0;
			setResolution(xResolution);
			resolution = true;
		} else if ("ResolutionDecrease".equals(e.getActionCommand())) {
			xResolution /= 2.0;
			setResolution(xResolution);
			resolution = true;
		} else if ("Average".equals(e.getActionCommand())) {
			setReductionMethod(REDUCER_AVG);
		} else if ("Max".equals(e.getActionCommand())) {
			setReductionMethod(REDUCER_MAX);
		} else if ("Min".equals(e.getActionCommand())) {
			setReductionMethod(REDUCER_MIN);
		} else if ("ButtonStart".equals(e.getActionCommand())) {
			((JButton) e.getSource()).setIcon(createImageIcon("media-playback-pause.png", "medium"));
			((JButton) e.getSource()).setActionCommand("ButtonPause");
			((JButton) e.getSource()).setToolTipText("Pause the visualization");
			sendEvent(new Resolution1DEvent(xResolution));
			startGenerator();
		} else if ("ButtonPause".equals(e.getActionCommand())) {
			((JButton) e.getSource()).setIcon(createImageIcon("media-playback-start.png", "medium"));
			((JButton) e.getSource()).setActionCommand("ButtonPlay");
			((JButton) e.getSource()).setToolTipText("Resume the visualization");
			pauseGenerator();
		} else if ("ButtonPlay".equals(e.getActionCommand())) {
			((JButton) e.getSource()).setIcon(createImageIcon("media-playback-pause.png", "medium"));
			((JButton) e.getSource()).setActionCommand("ButtonPause");
			((JButton) e.getSource()).setToolTipText("Pause the visualization");
			resumeGenerator();
		} else if ("ButtonReset".equals(e.getActionCommand())) {
			resetGenerator();
		} else if ("ButtonSnapshot".equals(e.getActionCommand())) {
			doSnapshot();
		} else if ("ButtonRecord".equals(e.getActionCommand())) {
			((JButton) e.getSource()).setIcon(createImageIcon("media-playback-stop.png", "medium"));
			((JButton) e.getSource()).setActionCommand("ButtonStop");
			((JButton) e.getSource()).setToolTipText("Stop recording");
			String s = (String)JOptionPane.showInputDialog(frame, "Recording name:\n", "Set filename", JOptionPane.QUESTION_MESSAGE, null, null, null);
			mpeg2Writer = new vtkMPEG2Writer();
			mpeg2Filter = new vtkWindowToImageFilter();
			mpeg2Filter.SetInput(renWin.GetRenderWindow());
			mpeg2Writer.SetInput(mpeg2Filter.GetOutput());
			mpeg2Writer.SetFileName(s + ".mpg");
			mpeg2Writer.Start();
			recording = true;
		} else if ("ButtonStop".equals(e.getActionCommand())) {
			((JButton) e.getSource()).setIcon(createImageIcon("media-record.png", "medium"));
			((JButton) e.getSource()).setActionCommand("ButtonRecord");
			((JButton) e.getSource()).setToolTipText("Start recording");
			mpeg2Writer.End();
			recording = false;
		} else if ("Export...".equals(e.getActionCommand())) {
			doSnapshot();
		} else if ("Exit".equals(e.getActionCommand())) {
			ibisExit();
			System.exit(1);
		} else if ("Reset".equals(e.getActionCommand())) {
			renWin.GetRenderer().ResetCamera();
			renWin.Render();
		} else if ("How to use...".equals(e.getActionCommand())) {
			doHelp();
		} 
	}


	public void doSnapshot() {
		fc.addChoosableFileFilter(new PNGFileFilter());
		fc.addChoosableFileFilter(new BMPFileFilter());
		fc.addChoosableFileFilter(new TIFFFileFilter());
		fc.addChoosableFileFilter(new PSFileFilter());
		fc.addChoosableFileFilter(new JPGFileFilter());
		fc.setCurrentDirectory(new java.io.File("."));
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (fc.getFileFilter() instanceof PNGFileFilter) {
				doPNGSnapshot();
			} else if (fc.getFileFilter() instanceof BMPFileFilter) {
				doBMPSnapshot();
			} else if (fc.getFileFilter() instanceof TIFFFileFilter) {
				doTIFFSnapshot();
			} else if (fc.getFileFilter() instanceof PSFileFilter) {
				doPSSnapshot();
			} else if (fc.getFileFilter() instanceof JPGFileFilter) {
				doJPGSnapshot();
			} else {
				doJPGSnapshot();
			}
		}
		fc.resetChoosableFileFilters();
	}

	public void doTIFFSnapshot() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vtkTIFFWriter writer = new vtkTIFFWriter();
				vtkWindowToImageFilter filter = new vtkWindowToImageFilter();
				renWin.lock();
				filter.SetInput(renWin.GetRenderWindow());
				writer.SetInput(filter.GetOutput());
				writer.SetFileName(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".tiff");
				writer.Write();
				renWin.unlock();
			}
		});
	}

	public void doPNGSnapshot() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vtkPNGWriter writer = new vtkPNGWriter();
				vtkWindowToImageFilter filter = new vtkWindowToImageFilter();
				renWin.lock();
				filter.SetInput(renWin.GetRenderWindow());
				writer.SetInput(filter.GetOutput());
				writer.SetFileName(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".png");
				writer.Write();
				renWin.unlock();
			}
		});
	}


	public void doBMPSnapshot() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vtkBMPWriter writer = new vtkBMPWriter();
				vtkWindowToImageFilter filter = new vtkWindowToImageFilter();
				renWin.lock();
				filter.SetInput(renWin.GetRenderWindow());
				writer.SetInput(filter.GetOutput());
				writer.SetFileName(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".bmp");
				writer.Write();
				renWin.unlock();
			}
		});
	}
	
	
	public void doPSSnapshot() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vtkPostScriptWriter writer = new vtkPostScriptWriter();
				vtkWindowToImageFilter filter = new vtkWindowToImageFilter();
				renWin.lock();
				filter.SetInput(renWin.GetRenderWindow());
				writer.SetInput(filter.GetOutput());
				writer.SetFileName(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".ps");
				writer.Write();
				renWin.unlock();
			}
		});
	}
	
	
	public void doJPGSnapshot() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vtkJPEGWriter writer = new vtkJPEGWriter();
				vtkWindowToImageFilter filter = new vtkWindowToImageFilter();
				renWin.lock();
				filter.SetInput(renWin.GetRenderWindow());
				writer.SetInput(filter.GetOutput());
				writer.SetFileName(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".jpg");
				writer.SetQuality(100);
				writer.Write();
				renWin.unlock();
			}
		});
	}		

	public void doHelp() {
		String helpMessage = 
			"How to use...\n\n" +
			"Help functions\n" +
			" F1 - help\n\n" + 
			"Visualization functions\n" +
			" Left mouse button - rotate\n" +
			" Right mouse button - zoom\n" +
			" Middle mouse button - pan\n" +
			" SHIFT + Left mouse button - pan\n" +
			" w - wireframe viewing mode\n" +
			" s - surface viewing mode\n" +
			" j - joystick control mode\n" +
			" t - trackball control mode\n" +
			" CTRL + r - reset viewing\n\n" +
			"File functions\n" +
			" CTRL + e - export the data into an image\n" +
			" ALT + F4 - exit application\n";
		JOptionPane.showMessageDialog(frame, helpMessage);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}
