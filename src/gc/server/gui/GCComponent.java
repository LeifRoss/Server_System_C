package gc.server.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;


/**
 * 
 * GCServer GCComponent
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GCComponent {

	private static final long serialVersionUID = 1L;
	private final String id;

	private Component component;


	public GCComponent(String id, Component in){
		super();
		this.id = id;
		setComponent(in);		
		GUIHandler.add(this);
	}

	public String getID(){
		return id;
	}

	public void setComponent(Component in){
		this.component = in;
	}

	public Component getComponent(){
		return component;
	}

	public void setColor(int r, int g, int b){
		component.setBackground(new Color(r,g,b));		
	}

	public void enable(boolean in){
		component.setEnabled(in);
	}


	public void setText(String in){

		if(component instanceof JTextField){
			((JTextField)component).setText(in);

		}else if(component instanceof JLabel){
			((JLabel)component).setText(in);
		}else if(component instanceof JTextArea){
			((JTextArea)component).setText(in);

		}else if(component instanceof JTextPane){
			((JTextPane)component).setText(in);	

		}else if(component instanceof JComboBox){

			JComboBox box = ((JComboBox)component);

			int count = box.getItemCount();
			String chk = in.toLowerCase();

			for(int i = 0; i < count; i++){

				String obj = box.getItemAt(i).toString().toLowerCase();

				if(obj.equals(chk)){
					box.setSelectedIndex(i);
					return;
				}	
			}

		}else if(component instanceof JCheckBox){


			((JCheckBox)component).setSelected(in.toLowerCase().equals("true"));


		}else{

			component.setName(in);
		}

	}


	public String getText(){

		if(component instanceof JTextField){
			return ((JTextField)component).getText();
			
		}else if(component instanceof JLabel){
			return ((JLabel)component).getText();
		}else if(component instanceof JTextArea){
			return ((JTextArea)component).getText();
		}else if(component instanceof JTextPane){
			return ((JTextPane)component).getText();
		}else if(component instanceof JComboBox){		
			JComboBox box = ((JComboBox)component);
			return box.getSelectedItem().toString();
		}else if(component instanceof JCheckBox){		
			JCheckBox box = ((JCheckBox)component);

			if(box.isSelected()){
				return "true";
			}

			return "false";
		}

		return component.getName();
	}

	public void setVisible(boolean in){
		component.setVisible(in);
	}

	
	public void addListener(final Runnable r){
		
		
		if(component instanceof JButton){
			
			((JButton)component).addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {			
					r.run();
				}
				
			});
			
		}else if( component instanceof JTextField ){
			((JTextField)component).addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {			
					r.run();
				}
				
			});		
		}else if( component instanceof JMenu ){
			((JMenu)component).addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {			
					r.run();
				}
				
			});		
		}
		
				
		
	}
	

}
