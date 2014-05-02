package gc.server.gui.views;

import gc.server.com.MainFrame;
import gc.server.gui.GCView;
import gc.server.util.CommandRouter;


import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.text.DefaultCaret;

/**
 * 
 * GCServer GCServerLog
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GCServerLog extends GCView{


	public static final String VIEW_ID = "serverlog";
	public static final String POSTOFFICE_ADDRESS = "SERVERLOG";
	private CommandRouter router, print;
	private boolean enabled;

	private JTextArea server_log;


	public GCServerLog(){
		super(VIEW_ID, new JPanel());


		router = new CommandRouter("log");
		router.setDescription("Log functions");

		print = new CommandRouter("print");
		print.setFunction(new Runnable(){

			@Override
			public void run() {
				println(print.getParameter());
			}

		});
		router.addRouter(print);


		CommandRouter enable = new CommandRouter("enable");
		enable.setDescription("enable the log");
		enable.setFunction(new Runnable(){

			@Override
			public void run() {
				enabled = true;

			}

		});
		router.addRouter(enable);


		CommandRouter disable = new CommandRouter("disable");
		disable.setDescription("disable and clear the log");
		disable.setFunction(new Runnable(){

			@Override
			public void run() {
				enabled = false;
				if(server_log!=null){
					server_log.setText("");
				}
			}

		});
		router.addRouter(disable);



		MainFrame.getCommandHandler().getRootRouter().addRouter(router);
	}



	@Override
	public void setup() {

		this.getContainer().setLayout(new BorderLayout());


		server_log = new JTextArea();
		server_log.setBackground(Color.BLACK);
		server_log.setForeground(Color.GREEN);
		server_log.setEditable(false);

		DefaultCaret caret = (DefaultCaret)server_log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


		this.getContainer().add(new JScrollPane(server_log), BorderLayout.CENTER);
	}


	/**
	 * Print a line to the server log
	 * @param text
	 */
	public void println(String text){

		if(!enabled){
			return;
		}
		
		try	{

			server_log.append(text+"\n");

		} catch(Exception e) {
			System.out.println(e); 
		}

	}






}
