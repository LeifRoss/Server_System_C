package gc.server.util;

import java.util.LinkedList;

import gc.server.com.MainFrame;

/**
 * 
 * GCServer CommandHandler
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class CommandHandler{


	public static final String POSTOFFICE_ADDRESS = "COMMANDHANDLER";

	public static final String COMMAND_ROOT = "cmd";
	private static CommandRouter root;

	private LinkedList<CommandListener> listeners;

	public CommandHandler(){
		super();

		listeners = new LinkedList<CommandListener>();
		buildCommandLibrary();
	}

	public void execute(String c){

		try{

			root.route(c);

		}catch(Exception e){
			MainFrame.print(e.getMessage());
		}
	}



	public CommandRouter getRootRouter(){
		return root;
	}

	private void buildCommandLibrary(){

		root = new CommandRouter(COMMAND_ROOT, true);

		CommandRouter system = new CommandRouter("system");
		system.setDescription("system functions");
		
		CommandRouter exit = new CommandRouter("exit");

		exit.setFunction(new Runnable(){

			@Override
			public void run() {
				MainFrame.shutdown();		
			}

		});


		CommandRouter open = new CommandRouter("open");

		open.setFunction(new Runnable(){

			@Override
			public void run() {

				MainFrame.openGUI();
			}

		});
		
		
		CommandRouter restart = new CommandRouter("restart");

		restart.setFunction(new Runnable(){

			@Override
			public void run() {

				MainFrame.restart();
			}

		});
		
		

		final CommandRouter remove = new CommandRouter("remove");
		remove.setDescription("Removes a function: <command>");
		remove.setFunction(new Runnable(){

			@Override
			public void run() {

				String param = remove.getParameter();
				removeRouter(param);			
			}

		});

		final CommandRouter debug = new CommandRouter("debug");
		debug.setDescription("Enter debug mode: <true/false>");
		debug.setFunction(new Runnable(){

			@Override
			public void run() {

				String param = debug.getParameter();

				MainFrame.setDebug(param.contains("true"));
				MainFrame.print("[DEBUG]: "+MainFrame.isDebug());


			}

		});


		system.addRouter(exit);	
		system.addRouter(open);
		system.addRouter(restart);

		root.addRouter(debug);
		root.addRouter(system);
		root.addRouter(remove);

	}

	/**
	 * 
	 * @param in
	 */
	public synchronized void printToListeners(String in){
		
		for(CommandListener listener: listeners){		
			listener.print(in);
		}
		
	}
	
	

	public synchronized void add(CommandListener in){
		listeners.add(in);
	}

	public synchronized void remove(CommandListener in){
		listeners.remove(in);
	}


	private void removeRouter(String param){
		root.removeRouter(param);
	}






}
