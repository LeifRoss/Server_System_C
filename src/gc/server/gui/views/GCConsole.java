package gc.server.gui.views;


import gc.server.com.MainFrame;
import gc.server.gui.ComponentFactory;
import gc.server.gui.GCView;
import gc.server.gui.GUIHandler;
import gc.server.util.CommandRouter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;


/**
 * 
 * GCServer GCConsole
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GCConsole extends GCView implements KeyListener, ActionListener{

	private static final long serialVersionUID = 1L;
	private static final int COMMANDS_LOG_MAX_LENGTH = 32;


	public static final String VIEW_ID = "console";
	public static final String ERROR = "ERROR";
	public static final String POSTOFFICE_ADDRESS = "CONSOLE";

	public static final String ACTION_BUTTON_GO = "console_go";
	public static final String ACTION_NAME = "Console";

	private CommandRouter command_router;

	private JTextPane console_log;
	private JTextField text_input;
	private JButton button;


	private LinkedList<String> previous_commands;
	private ListIterator<String> command_iterator;

	private StyledDocument doc;
	private SimpleAttributeSet error, keyword, regular;


	public GCConsole(){
		super(VIEW_ID, new JPanel());

	}

	@Override
	public void setup() {

		this.getContainer().setLayout(new BorderLayout());

		console_log = ComponentFactory.createTextPane();


		doc = console_log.getStyledDocument();

		JPanel panel = ComponentFactory.createPanel();
		panel.setLayout(new BorderLayout());

		button = ComponentFactory.createButton("Go", ACTION_BUTTON_GO);
		button.addActionListener(this);
		text_input = ComponentFactory.createTextField(64);
		text_input.addActionListener(this);
		text_input.addKeyListener(this);

		panel.add(text_input, BorderLayout.CENTER);
		panel.add(button, BorderLayout.EAST);

		this.getContainer().add(new JScrollPane(console_log), BorderLayout.CENTER);
		this.getContainer().add(panel, BorderLayout.SOUTH);

		error = ComponentFactory.createAttributeSet(Color.RED, true);
		keyword = ComponentFactory.createAttributeSet(Color.BLUE, false);
		regular = ComponentFactory.createAttributeSet(Color.BLACK, true);

		previous_commands = new LinkedList<String>();

		buildRouter();
	}





	/**
	 * Print a line to the console
	 * @param text
	 */
	public void println(String text){

		try	{

			doc.insertString(doc.getLength(), "\n"+text, regular );

		} catch(Exception e) { System.out.println(e); }

	}


	/**
	 * Print a blue line to the console
	 * @param text
	 */
	public void printKeyword(String text){

		try {

			doc.insertString(doc.getLength(), "\n"+text, keyword );
			MainFrame.getCommandHandler().printToListeners("\n"+text);
		} catch(Exception e) { System.out.println(e); }

	}


	/**
	 * Print a red line to the console
	 * @param text
	 */
	public void err(String text){

		try	{

			doc.insertString(doc.getLength(), "\n"+text, error );
			MainFrame.getCommandHandler().printToListeners("[Error/>\t"+text);

		} catch(Exception e) { System.out.println(e); }

	}



	/**
	 * Clear the console
	 */
	private void cls(){

		console_log.setText("");

	}


	private void buildRouter(){


		command_router = new CommandRouter("console");
		command_router.setDescription("console functions");

		final CommandRouter print = new CommandRouter("print");
		print.setFunction(new Runnable(){

			@Override
			public void run() {

				printKeyword(print.getParameter());
			}

		});

		command_router.addRouter(print);

		
		final CommandRouter error = new CommandRouter("error");
		error.setFunction(new Runnable(){

			@Override
			public void run() {

				err(error.getParameter());
			}

		});

		command_router.addRouter(error);
		
		


		CommandRouter clearConsole = new CommandRouter("cls");
		clearConsole.setFunction(new Runnable(){

			@Override
			public void run() {

				cls();
			}

		});

		command_router.addRouter(clearConsole);


		MainFrame.getCommandHandler().getRootRouter().addRouter(command_router);

	}



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

		switch(e.getKeyCode()){

		case KeyEvent.VK_UP:


			if(command_iterator!=null && command_iterator.hasNext()){
				text_input.setText(command_iterator.next());
			}

			break;

		case KeyEvent.VK_DOWN:

			if(command_iterator!=null && command_iterator.hasPrevious()){
				text_input.setText(command_iterator.previous());
			}

			break;


		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = text_input.getText();		
		text_input.setText("");

		if(!command.isEmpty()){


			this.println("Console:>\t"+command);

			MainFrame.execute(command);
			
			//Message m = new Message(MainFrame.getCommandHandler().getPostAddress(), GUIHandler.getActionHandler().getPostAddress(), ACTION_NAME, command);
			//MainFrame.getMessageBus().postMessage(m);
			
			previous_commands.addFirst(command);


			if(previous_commands.size() > COMMANDS_LOG_MAX_LENGTH){
				previous_commands.removeLast();
			}

			command_iterator = previous_commands.listIterator();
		}
	}



}
