package gc.server.gui;


import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


/**
 * 
 * GCServer ComponentFactory
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 * @deprecated use GUIFactory with GCView / GCComponent instead
 */
public class ComponentFactory {

	
	
	public static final Font STANDARD_FONT = new Font("Arial",Font.PLAIN, 12);
	public static final Font STANDARD_FONT_BOLD = new Font("Arial",Font.BOLD, 12);
	
	
	/**
	 * 
	 * @return
	 */
	public static JTextPane createTextPane(){
		
		JTextPane pane = new JTextPane();
		pane.setFont(STANDARD_FONT);
		pane.setEditable(false);
		pane.setVisible(true);
				
		
		return pane;
	}
	
	/**
	 * 
	 * @param length
	 * @param view
	 * @return
	 */
	public static JTextField createTextField(int length){
		
		JTextField textField = new JTextField(length);
		textField.setFont(STANDARD_FONT);
		textField.setVisible(true);
		
		return textField;
	}

	/**
	 * 
	 * @param title
	 * @param action
	 * @param view
	 * @return
	 */
	public static JButton createButton(String title, String action){
		
		JButton button = new JButton(title);
		button.setActionCommand(action);
		button.addActionListener(GUIHandler.getActionHandler());
		button.setFont(STANDARD_FONT);
		button.setVisible(true);
		
		
		return button;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static JPanel createPanel(){
		
		JPanel panel = new JPanel();
		panel.setVisible(true);
		
		return panel;
	}
	
	
	
	/**
	 * 
	 * @param foreground
	 * @param bold
	 * @return
	 */
	public static SimpleAttributeSet createAttributeSet(Color foreground, boolean bold){
		
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, foreground);
		StyleConstants.setBold(set, bold);

		return set;
	}
	
	/**
	 * 
	 * @param background
	 * @param foreground
	 * @param bold
	 * @return
	 */
	public static SimpleAttributeSet createAttributeSet(Color background, Color foreground, boolean bold){
		
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, foreground);
		StyleConstants.setBackground(set, background);
		StyleConstants.setBold(set, bold);

		return set;
	}
	
	
	
}
