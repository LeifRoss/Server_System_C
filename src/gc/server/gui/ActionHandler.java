package gc.server.gui;


import gc.server.com.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * 
 * GCServer ActionHandler
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class ActionHandler implements ActionListener{
	

	
	public ActionHandler(){
		super();
		
	}

		

	@Override
	public void actionPerformed(ActionEvent e) {

		MainFrame.execute(e.getActionCommand());
	}

	
	
	public void handle(String in){
		

		
	}


}
