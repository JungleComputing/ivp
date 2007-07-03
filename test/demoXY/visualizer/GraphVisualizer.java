import visualization.visualizer.*;
import visualization.result.*;
import visualization.event.*;

import vtk.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphVisualizer extends VisualizerXY implements ActionListener {

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
	JFrame frame;
	vtkCanvas renwin;

	Plot[] plots;

	/********************************************
	 * GUI methods
	 ********************************************/
	private static void createAndShowGUI() {
		/* 
		 * JPopupMenu.setDefaultLightWeightPopupEnabled(false) is necessary to make the vtkPanel (awt.Canvas) and swing work together 
		 * for further information see: http://public.kitware.com/pipermail/vtkusers/2002-March/059595.html 
		 */
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		GraphVisualizer graphVisualizer = new GraphVisualizer();
		
		graphVisualizer.frame = new JFrame("Graph");
		graphVisualizer.frame.setJMenuBar(graphVisualizer.createMenuBar());

		graphVisualizer.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphVisualizer.frame.setSize(600, 600);
		graphVisualizer.frame.setContentPane(graphVisualizer);
		graphVisualizer.frame.setVisible(true);
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

	
	public GraphVisualizer() {
//		http://www.vtk.org/pipermail/vtkusers/2005-February/078581.html
		// GUI init
		renwin = new vtkCanvas();
		renwin.GetRenderer().SetBackground(0.75, 0.75, 0.75);
		plots = new Plot[3];
		plots[0] = new Plot(0, plots.length);
		plots[0].setNrVars(2);
		plots[0].setVariable(0, "cos", 1000, this);
		plots[0].setVariable(1, "sin", 1000, this);
		plots[0].setColor(0.0, 0.0, 0.0);
		plots[0].setTitles("Math functions", "time (s)" , "y");
		plots[1] = new Plot(1, plots.length);
		plots[1].setNrVars(2);
		plots[1].setVariable(0, "cpu", 1000, this);
		plots[1].setVariable(1, "mem", 1000, this);
		plots[1].setColor(0.0, 0.0, 0.0);
		plots[1].setTitles("CPU and Memory usage", "time (s)" , "y");
		plots[2] = new Plot(2, plots.length);
		plots[2].setNrVars(1);
		plots[2].setVariable(0, "var", 1000, this);
		plots[2].setColor(0.0, 0.2, 0.2);
		plots[2].setTitles("A single value (cos)", "time (s)" , "y");
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(plots.length, 1));
		setLayout(new BorderLayout());
		for (Plot plot : plots) {
			renwin.GetRenderer().AddViewProp(plot.getPlotActor());
			buttons.add(plot.getPane());
		}
		renwin.GetRenderer().ResetCamera();
		add(renwin, BorderLayout.CENTER);
		add(buttons, BorderLayout.EAST);
		createButtonPane();
		ibisInit();
	}

	public void createButtonPane() {
		/*
		 * icon images from http://tango.freedesktop.org/Tango_Icon_Gallery
		 */
		String iconSize = "medium";
		JPanel result = new JPanel();
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.PAGE_AXIS));
		buttonPane.add(createButton("Start", "media-playback-start.png", iconSize, "Start the visualization"));
		buttonPane.add(Box.createRigidArea(new Dimension(0, 10)));
		buttonPane.add(createButton("ButtonSnapshot", "camera-photo.png", iconSize, "Take a snapshot of the visualization"));
		result.setLayout(new BorderLayout());
		result.add(buttonPane, BorderLayout.NORTH);
		add(result, BorderLayout.WEST);
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

	public void doRender() {
		renwin.Render(); 
	}

	public void doSync() {}

	public void doUpcall(Result result) {
		ResultXY resultXY = (ResultXY) result;
		for (Plot plot : plots) {
			plot.addValueToVar(resultXY.getX(), resultXY.getY(), resultXY.getName());
		}
	}

	public void doLastUpcall(Result result) {
	}

	public void actionPerformed(ActionEvent e) {
		if ("Exit".equals(e.getActionCommand())) {
			sendEvent(new ExitEvent());
			ibisExit();
			System.exit(1);
		} else if ("How to use...".equals(e.getActionCommand())) {
			doHelp();
		} else if ("Start".equals(e.getActionCommand())) {
			sendEvent(new StartEvent());
		}
	}

	public void doHelp() {
		String helpMessage = "help message\n";
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
