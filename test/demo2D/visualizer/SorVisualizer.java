import visualization.visualizer.*;
import visualization.result.*;
import visualization.event.*;
import vtk.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.border.TitledBorder;
import javax.swing.*;

public class SorVisualizer extends Visualizer2D implements ActionListener {

	static { 
		System.loadLibrary("vtkCommonJava"); 
		System.loadLibrary("vtkFilteringJava"); 
		System.loadLibrary("vtkIOJava"); 
		System.loadLibrary("vtkImagingJava"); 
		System.loadLibrary("vtkGraphicsJava"); 
		System.loadLibrary("vtkRenderingJava"); 
	}

	public static final long serialVersionUID = 1L;
	private static final int RENDER_MODE_SYNC = 1;
	private static final int RENDER_MODE_SYNC_STEP = 2;
	private static final int RENDER_MODE_ASYNC = 3;

	/**
	 * Global variables of the Sor class
	 */
	private final JFileChooser fc = new JFileChooser(); // shared GUI variables
	JLabel iterationLabel, allowedLabel, currentLabel, resolutionLabel, fpsLabel;
	JFrame frame;
	JPanel propertiesPane;
	private boolean recording = false, resolution = false;

	int xResolution, yResolution, iterationSkip, reductionMethod, nrFrames;
	boolean synchronous;
	
	vtkDoubleArray array;								// shared Visualization variables
	vtkImageData imageData;
	vtkCanvas renWin;
	vtkMPEG2Writer mpeg2Writer;
	vtkWindowToImageFilter mpeg2Filter;

	/********************************************
	 * GUI methods
	 ********************************************/
	private static void createAndShowGUI(int xResolution, int yResolution, int iterationSkip, int reductionMethod, boolean synchronous) {
		/* 
		 * JPopupMenu.setDefaultLightWeightPopupEnabled(false) is necessary to make the vtkPanel (awt.Canvas) and swing work together 
		 * for further information see: http://public.kitware.com/pipermail/vtkusers/2002-March/059595.html 
		 */
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		SorVisualizer sor = new SorVisualizer(xResolution, yResolution, iterationSkip, reductionMethod, synchronous);
		
		sor.frame = new JFrame("Sor Visualizer");
		sor.frame.setJMenuBar(sor.createMenuBar());

		sor.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sor.frame.setSize(500, 500);
		sor.frame.setContentPane(sor);
		sor.frame.setVisible(true);
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

		menu.add(createMenuItem("Export...", KeyEvent.VK_E, KeyEvent.VK_E, ActionEvent.CTRL_MASK, "Export matrix"));
		menu.add(createMenuItem("Exit", KeyEvent.VK_X, KeyEvent.VK_F4, ActionEvent.ALT_MASK, "Exit application"));

		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("Viewing menu");
		menuBar.add(menu);

		menu.add(createMenuItem("Reset", KeyEvent.VK_R, KeyEvent.VK_R, ActionEvent.CTRL_MASK, "Reset camera"));

		menu = new JMenu("Render Mode");
		menu.setMnemonic(KeyEvent.VK_R);
		menu.getAccessibleContext().setAccessibleDescription("Render Mode menu");
		menuBar.add(menu);

		menu.add(createMenuItem("Synchronous", KeyEvent.VK_S, 0, 0, "Synchronous"));
		menu.add(createMenuItem("Synchronous - stepped...", KeyEvent.VK_Y, 0, 0, "Synchronous - stepped"));
		menu.addSeparator();
		menu.add(createMenuItem("Asynchronous", KeyEvent.VK_A, 0, 0, "Asynchronous"));

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
		propertiesPane = new JPanel();
		propertiesPane.setLayout(new BoxLayout(propertiesPane, BoxLayout.PAGE_AXIS));
		propertiesPane.setPreferredSize(new Dimension(200,0));
		JPanel calcPropertiesPane = new JPanel();
		calcPropertiesPane.setLayout(new BoxLayout(calcPropertiesPane, BoxLayout.PAGE_AXIS));
		calcPropertiesPane.setBorder(new TitledBorder("calculation"));
		calcPropertiesPane.add(iterationLabel = new JLabel("iteration:"));
		calcPropertiesPane.add(allowedLabel = new JLabel("allowed diff:"));
		calcPropertiesPane.add(currentLabel = new JLabel("current diff:"));
		calcPropertiesPane.add(new Box.Filler(null, new Dimension(100, 0), null));
		propertiesPane.add(calcPropertiesPane);

		JPanel visPropertiesPane = new JPanel();
		visPropertiesPane.setLayout(new BoxLayout(visPropertiesPane, BoxLayout.PAGE_AXIS));
		visPropertiesPane.setBorder(new TitledBorder("visualization"));
		visPropertiesPane.add(resolutionLabel = new JLabel(String.format("Resolution: %d x %d", xResolution, yResolution)));
		visPropertiesPane.add(Box.createRigidArea(new Dimension(0, 15)));
		visPropertiesPane.add(fpsLabel = new JLabel("0 fps"));
		visPropertiesPane.add(Box.createRigidArea(new Dimension(0, 15)));
		visPropertiesPane.add(new JLabel("Reduction method:"));
		
		JRadioButton method1 = new JRadioButton("Average");
		method1.addActionListener(this);
		visPropertiesPane.add(method1);
		if (reductionMethod == REDUCER_AVG) method1.setSelected(true);
		
		JRadioButton method2 = new JRadioButton("Max");
		method2.addActionListener(this);
		visPropertiesPane.add(method2);
		if (reductionMethod == REDUCER_MAX) method2.setSelected(true);
		
		JRadioButton method3 = new JRadioButton("Min");
		method3.addActionListener(this);
		visPropertiesPane.add(method3);
		if (reductionMethod == REDUCER_MIN) method3.setSelected(true);
		
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


	public SorVisualizer(int xResolution, int yResolution, int iterationSkip, int reductionMethod, boolean synchronous) {
		this.xResolution = xResolution;
		this.yResolution = yResolution;
		this.iterationSkip = iterationSkip;
		this.reductionMethod = reductionMethod;
		this.synchronous = synchronous;
		
		setStartResolution(xResolution, yResolution); 

		/* the frames per second accounting thread */
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
		vtkPlaneSource plane = new vtkPlaneSource();
		plane.SetXResolution(1);
		plane.SetYResolution(1);
		plane.SetOrigin(0, 0, 0);
		plane.SetPoint1(1, 0, 0);
		plane.SetPoint2(0, 1, 0);

		// create the mapper
		vtkPolyDataMapper mapper = new vtkPolyDataMapper();
		mapper.SetInput(plane.GetOutput());

		// create the data array
		array = new vtkDoubleArray();
		array.SetJavaArray(getDataArray());

		// put it in an ImageData object
		imageData = new vtkImageData();
		imageData.SetDimensions(xResolution, yResolution, 1);
		imageData.SetScalarTypeToDouble();
		imageData.SetOrigin(0, 0, 0);
		imageData.GetPointData().SetScalars(array);

		vtkLookupTable lut = new vtkLookupTable();
		lut.SetTableRange(0, 10);
		lut.SetHueRange(0.25, 1.0);
		lut.SetSaturationRange(1, 1);
		lut.SetValueRange(1, 1);
		lut.Build(); 

		vtkTexture texture = new vtkTexture();
		texture.SetInput(imageData);
		texture.InterpolateOn();
		texture.SetLookupTable(lut);

		vtkActor actor = new vtkActor();
		actor.SetMapper(mapper);
		actor.SetTexture(texture);
		actor.SetPosition(10,70,0);
		renWin.GetRenderer().AddActor(actor);

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

	public synchronized void doRender() {
		renWin.lock();
		if (recording) {
			mpeg2Filter.Modified();
			mpeg2Writer.Write();
		}
		renWin.unlock();				
		renWin.Render(); 
		if (resolution) {
			imageData.SetDimensions(xResolution, yResolution, 1); // if resolution changed...
			resolutionLabel.setText(String.format("Resolution: %d x %d", xResolution, yResolution));
			resolution = false;
		}
	}

	public void doLastUpcall(Result result) {
		super.doLastUpcall(result);
		SorResult sorResult = (SorResult) result;
		iterationLabel.setText(String.format("iteration: %d", sorResult.getIteration()));
		currentLabel.setText(String.format("current diff: %.6f", sorResult.getMaxDiff()));
		allowedLabel.setText(String.format("allowed diff: %.6f", sorResult.getStopDiff()));
		array.SetJavaArray(getDataArray());
		imageData.Modified();
		nrFrames++;
	}


	public void actionPerformed(ActionEvent e) {
		if ("ResolutionIncrease".equals(e.getActionCommand())) {
			xResolution *= 2;
			yResolution *= 2;
			setResolution(xResolution, yResolution);
			resolution = true;
		} else if ("ResolutionDecrease".equals(e.getActionCommand())) {
			xResolution /= 2;
			yResolution /= 2;
			setResolution(xResolution, yResolution);
			resolution = true;
		} else if ("Average".equals(e.getActionCommand())) {
			reductionMethod = REDUCER_AVG;
			setReductionMethod(REDUCER_AVG);
		} else if ("Max".equals(e.getActionCommand())) {
			reductionMethod = REDUCER_MAX;
			setReductionMethod(REDUCER_MAX);
		} else if ("Min".equals(e.getActionCommand())) {
			reductionMethod = REDUCER_MIN;
			setReductionMethod(REDUCER_MIN);
		} else if ("ButtonStart".equals(e.getActionCommand())) {
			((JButton) e.getSource()).setIcon(createImageIcon("media-playback-pause.png", "medium"));
			((JButton) e.getSource()).setActionCommand("ButtonPause");
			((JButton) e.getSource()).setToolTipText("Pause the visualization");
			if (synchronous) sendEvent(new visualization.event.Event("Synchronous"));
			else sendEvent(new visualization.event.Event("Asynchronous"));
			startGenerator(xResolution, yResolution, iterationSkip, reductionMethod);
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
			if (renWin.getHeight() % 2 != 0) {
				JOptionPane.showMessageDialog(frame, String.format("Capturing a movie can only when\nthe vertical size is even.\nCurrent vertical size: %d\nPlease resize!", renWin.getHeight()), "General error", JOptionPane.ERROR_MESSAGE);
				return;
			}
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
		} else if ("Synchronous".equals(e.getActionCommand())) {
			sendEvent(new visualization.event.Event("Synchronous"));
		} else if ("Asynchronous".equals(e.getActionCommand())) {
			sendEvent(new visualization.event.Event("Asynchronous"));
		} else if ("Synchronous - stepped...".equals(e.getActionCommand())) {
			String s = (String)JOptionPane.showInputDialog(frame, "Iteration skip:\n", "Set iteration skip", JOptionPane.QUESTION_MESSAGE, null, null, null);
			try {
				sendEvent(new visualization.event.Event("Synchronous"));
				setIterationSkip(Integer.parseInt(s));
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(frame, String.format("Wrong input: \"%s\"\nPlease provide an integer!", s), "Input error", JOptionPane.ERROR_MESSAGE);
			}
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
			" CTRL + s - save the data to a file\n" +
			" CTRL + o - open a datafile\n" +
			" CTRL + e - export the data into an image\n" +
			" ALT + F4 - exit application\n";
		JOptionPane.showMessageDialog(frame, helpMessage);
	}

	public static void main(String[] args) {
		int xResolution = 10, yResolution = 10, iterationSkip = 1, reductionMethod = REDUCER_DEFAULT;
		boolean synchronous = true;
		for (int i = 0; i < args.length; i++) {
			if (false) {
			} else if (args[i].equals("-resolution")) {
				++i;
				xResolution = Integer.parseInt(args[i]);
				++i;
				yResolution = Integer.parseInt(args[i]);
			} else if (args[i].equals("-synchronous")) {
				synchronous = true;
			} else if (args[i].equals("-asynchronous")) {
				synchronous = false;				
			} else if (args[i].equals("-iterationskip")) {
				++i;
				iterationSkip = Integer.parseInt(args[i]);
			} else if (args[i].equals("-reduction")) {
				++i;
				reductionMethod = Integer.parseInt(args[i]);
			} else {
				System.out.println("Illegal command line parameters");
				System.exit(33);
			}
		}
		final int x = xResolution;
		final int y = yResolution;
		final int i = iterationSkip;
		final int r = reductionMethod;
		final boolean s = synchronous;
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(x, y, i, r, s);
			}
		});
	}
}
