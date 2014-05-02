package gc.server.gui;

import gc.server.gui.views.GCTabbedView;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * GCServer GUIFactory
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GUIFactory {

	// views

	public static GCView frame(String id, String name){
		return frame(id,name,false);
	}

	public static GCView frame(String id, String name, boolean extended){

		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		if(GUIHandler.getIcon()!=null){
			frame.setIconImage(GUIHandler.getIcon());
		}

		if(extended){
			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		}

		GCView view = new GCView(id, frame);

		return view;
	}

	
	public static GCView splitpane(String id){
		return splitpane(id, 0.5, false);
	}
	
	public static GCView splitpane(String id, boolean split){
		return splitpane(id, 0.5, split);
	}
	
	public static GCView splitpane(String id, double pos, boolean split){

		int dir = split ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT;
		
		JSplitPane pane = new JSplitPane(dir);
	
		pane.setOneTouchExpandable(true);
		pane.setResizeWeight(pos); 

		GCView view = new GCView(id, pane);

		return view;
	}

	
	public static GCView scrollpane(String id, GCComponent c){

		GCView view = new GCView(id, new JScrollPane(c.getComponent()));

		return view;
	}
	
	
	public static GCView scrollpane(String id, GCView v){

		GCView view = new GCView(id, new JScrollPane(v.getContainer()));

		return view;
	}
	
	public static GCView scrollpane(String id){

		GCView view = new GCView(id, new JScrollPane());

		return view;
	}

	public static GCView menubar(String id, String name){

		GCView view = new GCView(id, new JMenuBar());

		return view;
	}

	public static GCView menu(String id, String name){

		GCView view = new GCView(id, new JMenu(name));

		return view;
	}

	
	
	public static GCView view(String id){

		GCView view = new GCView(id, new JPanel());
		view.getContainer().setVisible(true);

		return view;
	}


	public static GCView tabview(String id){
			
		GCView view = new GCTabbedView(id);		
		return view;
	}
	

	// components

	public static GCComponent menuitem(String id, String name, String command){

		JMenuItem item = new JMenuItem(name);
		item.setActionCommand(command);
		item.addActionListener(GUIHandler.getActionHandler());		
		GCComponent comp = new GCComponent(id, item);

		return comp;
	}

	public static GCComponent button(String id, String name, String command){

		JButton button = new JButton(name);
		button.setActionCommand(command);
		button.addActionListener(GUIHandler.getActionHandler());
		GCComponent comp = new GCComponent(id, button);

		return comp;
	}


	public static GCComponent textfield(String id){
	
		JTextField text = new JTextField(20);
		GCComponent comp = new GCComponent(id, text);

		return comp;
	}


	public static GCComponent textfield(String id, String command){

		JTextField text = new JTextField(20);
		text.setActionCommand(command);
		text.addActionListener(GUIHandler.getActionHandler());
		GCComponent comp = new GCComponent(id, text);

		return comp;	
	}

	public static GCComponent textarea(String id){

		JTextArea text = new JTextArea();
		GCComponent comp = new GCComponent(id, text);

		return comp;	
	}


	public static GCComponent label(String id, String name){

		JLabel label = new JLabel(name);
		GCComponent comp = new GCComponent(id, label);

		return comp;	
	}

	
	public static GCComponent combobox(String id, String... values){
			
		JComboBox box = new JComboBox(values);				
		GCComponent comp = new GCComponent(id, box);

		return comp;
	}

	public static GCComponent checkbox(String id, String name){
		
		JCheckBox box = new JCheckBox(name);				
		GCComponent comp = new GCComponent(id, box);

		return comp;
	}
	
}
