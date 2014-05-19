package gc.server.com;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;

import gc.server.database.DBHandler;
import gc.server.gui.GUIHandler;
import gc.server.http.CookieHandler;
import gc.server.http.ServerHandler;
import gc.server.ssh.SSHHandler;
import gc.server.util.CommandHandler;
import gc.server.util.CommandRouter;
import gc.server.util.ScriptEnvironment;
import gc.server.util.ShowHandler;
import gc.server.util.Util;
import connectivity.utility.ConfigReader;

/**
 * 
 * GCServer MainFrame
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class MainFrame {


	private static final String CONFIG_LOCATION = Util.getAssetsLocation()+"//config//config.txt";
	private static final String WEBROOT = Util.getAssetsLocation()+"webroot//";

	private static final String CONFIG_PORT = "port";
	private static final String CONFIG_POOL = "thread_pool_size";
	private static final String CONFIG_TIMEOUT = "timeout";
	private static final String CONFIG_BOOT = "boot";
	private static final String CONFIG_REQUEST_OVERRIDE = "request_handler_override";
	private static final String CONFIG_ENABLE_DISCOVERY = "network_discovery";

	private static ConfigReader config;

	private static CommandHandler command_handler;
	private static ServerHandler server_handler;
	private static ScriptEnvironment script_environment;

	private static boolean DEBUG = false;

	/**
	 * Initiates the system
	 */
	public static void initMainFrame(){

		config = new ConfigReader(CONFIG_LOCATION);

		initCommandHandler();
		initDatabaseHandler();
		GUIHandler.init();
		initServer();
		SSHHandler.init();
		initScriptEnvironment();
		ShowHandler.init();
	}


	/**
	 * Initiates the server and cookie handler
	 */
	private static void initServer(){

		int port = 8080;
		int timeout = 3000;
		int poolSize = 2;

		boolean override = false;
		boolean discovery = false;

		try{
			if(config.hasConfiguration()){

				try{			

					if(config.valueExists(CONFIG_PORT))
						port = Integer.parseInt(config.getValue(CONFIG_PORT));

					if(config.valueExists(CONFIG_TIMEOUT))
						timeout = Integer.parseInt(config.getValue(CONFIG_TIMEOUT));

					if(config.valueExists(CONFIG_POOL))
						poolSize = Integer.parseInt(config.getValue(CONFIG_POOL));

					if(config.valueExists(CONFIG_REQUEST_OVERRIDE))
						override = Boolean.parseBoolean(config.getValue(CONFIG_REQUEST_OVERRIDE));

					if(config.valueExists(CONFIG_ENABLE_DISCOVERY))
						discovery = Boolean.parseBoolean(config.getValue(CONFIG_ENABLE_DISCOVERY));

				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}

			CookieHandler.init();
			server_handler = new ServerHandler(port, timeout, poolSize, override, discovery);

		}catch(Exception e){
			print("MainFrame: Unable to initiate HttpServer");
		}

	}

	/**
	 * Initiates the database handler
	 */
	private static void initDatabaseHandler(){

		try{

			DBHandler.init();

		}catch(Exception e){
			print("MainFrame: Unable to initate the database driver");
		}
	}

	/**
	 * Initiates the command handler
	 */
	private static void initCommandHandler(){

		try{
			command_handler = new CommandHandler();
		}catch(Exception e){
			print("MainFrame: Unable to initiate Command Handler");
		}
	}



	/**
	 * Initiates the scripting environment
	 */
	private static void initScriptEnvironment(){

		try{
			
			if(config.hasConfiguration() && config.valueExists(CONFIG_BOOT)){

				String boot = config.getValue(CONFIG_BOOT);
				script_environment = new ScriptEnvironment(boot);	
				return;
			}
			
			
			script_environment = new ScriptEnvironment();		
		}catch(Exception e){
			print("MainFrame: Unable to initiate Script Environment");
		}
	}




	/**
	 * 
	 * @return
	 */
	public static CommandHandler getCommandHandler(){
		return command_handler;
	}

	/**
	 * 
	 * @return
	 */
	public static ServerHandler getServerHandler(){
		return server_handler;
	}

	/**
	 * 
	 * @return
	 */
	public static ScriptEnvironment getScriptEnv(){
		return script_environment;
	}


	/**
	 * 
	 * @param in
	 * @param uri
	 */
	public static void addHandler(HttpAsyncRequestHandler<HttpRequest> in, String uri){

		if(server_handler==null){
			return;
		}

		server_handler.addHandler(in, uri);		
	}

	/**
	 * 
	 * @param in
	 */
	public static void addUniformHandler(HttpAsyncRequestHandler<HttpRequest> in){

		if(server_handler==null){
			return;
		}

		server_handler.addUniformHandler(in);		
	}


	public static String getConfigPath(){
		return CONFIG_LOCATION;
	}
	
	public static String getWebroot(){
		return WEBROOT;
	}

	public static void shutdown(){

		script_environment.shutdown();
		server_handler.shutdown();
		System.exit(0);
	}

	
	public static void restart(){


		script_environment.shutdown();
		server_handler.shutdown();

		GUIHandler.getWindow().dispose();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		initMainFrame();		
	}

	public static void openGUI(){
		GUIHandler.open();		
	}

	public static void closeGUI(){
		GUIHandler.close();
	}

	public static void print(String in){

		execute("console print \""+in+"\"");		
	}

	public static void error(String in){
		
		execute("console error \""+in+"\"");	
	}

	/**
	 * Todo: this!!
	 * @param in
	 * @param persistent
	 */
	public synchronized static void log(String in, boolean persistent){
		
	
	}

	
	public static void execute(String in){
		
		getCommandHandler().execute(in);		
	}
	
	
	public static String readConfig(String key){

		if(config!=null && config.hasConfiguration() && config.valueExists(key)){
			return config.getValue(key);
		}

		return "";
	}

	public static void writeConfig(String key, String value){

		if(config!=null){
			config.setValue(key, value);
		}
	}

	public static void saveConfig(){
		if(config!=null && config.hasConfiguration()){
			config.write(CONFIG_LOCATION);
		}
	}


	/**
	 * Adds a commandrouter to root
	 * @param router
	 */
	public static void add(CommandRouter router){
		
		command_handler.getRootRouter().addRouter(router);
	}
	
	
	/**
	 * Adds a function to root
	 * @param path
	 * @param name
	 * @param r
	 */
	public static void addFunction(String path, String name, Runnable r){

		CommandRouter router = new CommandRouter(name, true);
		if(r!=null){
			router.setFunction(r);	
		}
		command_handler.getRootRouter().addRouter(router, path);	

	}

	public static void removeFunction(String path){

		command_handler.getRootRouter().removeRouter(path);		
	}


	public static void setDebug(boolean in){
		DEBUG = in;
	}

	public static boolean isDebug(){
		return DEBUG;
	}

}
