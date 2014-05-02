package gc.server.gui.views;


import gc.server.gui.GCComponent;
import gc.server.gui.GCView;
import gc.server.gui.GUIFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;


/**
 * 
 * GCServer GCMenuBar
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GCMenuBar extends GCView{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String VIEW_ID = "menubar";

	private GCView menubar;	
	private GCView file, edit;
		
	
	public GCMenuBar(){
		super(VIEW_ID, new JPanel());
		
	}

	@Override
	public void setup() {

		this.getContainer().setLayout(new BorderLayout());
		
		// top menu bar
		menubar = GUIFactory.menubar("menubar.top", "menubar");
		
		file = GUIFactory.menu("menubar.top.file", "File");
		edit = GUIFactory.menu("menubar.top.edit", "Edit");		
		
		menubar.add(file);
		menubar.add(edit);
				
		// bottom menu bar
		
		GCView panel = GUIFactory.view(VIEW_ID+".bottom");
		panel.getContainer().setLayout(new FlowLayout(FlowLayout.LEFT));
		
		GCComponent button = null;
		
		
		
		button = GUIFactory.button("menubar.bottom.bstart", "Start", "server start");
		panel.add(button);
		
		button = GUIFactory.button("menubar.bottom.bstop", "Stop", "server shutdown");
		panel.add(button);
				
		this.add(menubar,BorderLayout.NORTH);
		this.add(panel,BorderLayout.SOUTH);
		
		this.setRaisedBorder();
	}


	
	
}
