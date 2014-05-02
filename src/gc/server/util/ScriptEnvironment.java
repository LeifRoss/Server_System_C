package gc.server.util;


import gc.server.com.MainFrame;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.CompiledScript;
import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.tools.SourceReader;
import org.mozilla.javascript.tools.shell.Global;

import connectivity.utility.Utility;


/**
 * 
 * GCServer ScriptEnvironment v2
 * 
 * @author Leif Andreas Rudlang
 * @date 16.03.2014
 */
public class ScriptEnvironment implements Runnable{

	private static final String ARG_SEPERATOR = ";";
	private static final String SCRIPT_PATH = Util.getAssetsLocation()+"scripts//";
	private static final String SUFFIX_JAVASCRIPT = ".js";
	private static final String SUFFIX_BATCH = ".cmd";

	private HashMap<String,Script> compiled_scripts;

	private ArrayDeque<String> args;
	private ArrayDeque<String> buffer;
	private boolean running;

	private Scriptable scope;
	private Context cx;
	private String boot;

	private String running_arg;


	/**
	 * Creates a new scripting environment
	 */
	public ScriptEnvironment(){

		Thread t = new Thread(this);
		t.start();
	}


	/**
	 * Creates a new scripting environment
	 * Sets the boot script
	 * @param boot
	 */
	public ScriptEnvironment(String boot){

		this.boot = boot;

		Thread t = new Thread(this);
		t.start();
	}


	private void setup(){


		ArrayList<String> modulePaths = new ArrayList<String>();
			
		modulePaths.add("/"+SCRIPT_PATH.replace(" ", "%20"));

		try {
			URI uri = new URI("file:///"+SCRIPT_PATH.replace(" ", "%20"));
			
			String fpath = uri.toString();
			modulePaths.add(fpath);
			
		} catch (URISyntaxException e) {
			MainFrame.print(e.getMessage());
			e.printStackTrace();
		}


		cx = Context.enter();
		cx.setLanguageVersion(Context.VERSION_1_8);

		Global global = new Global();
		global.initStandardObjects(cx, true);

		cx.setGeneratingDebug(MainFrame.isDebug());
		cx.setGeneratingSource(MainFrame.isDebug());
		cx.setOptimizationLevel(2);

		
		Require require = global.installRequire(cx, modulePaths, false);		
		
		Scriptable prototype = cx.initStandardObjects();
		Scriptable topLevel = new ImporterTopLevel(cx);
		require.install(topLevel);
		
		
		prototype.setParentScope(topLevel);
		scope = cx.newObject(prototype);
		scope.setPrototype(prototype);	

		buffer = new ArrayDeque<String>();
		args = new ArrayDeque<String>();
		compiled_scripts = new HashMap<String,Script>();

		running_arg = "";
	}


	/**
	 * Runs a batch script (Console commands, line by line).
	 * @param in
	 */
	public void runBatch(String in){

		CommandHandler cmdHandler = MainFrame.getCommandHandler();

		String[] commands = in.split(Utility.getLineSeparator(in));

		try{

			for(String command : commands){

				if(command == null || command.isEmpty()){
					continue;
				}

				cmdHandler.execute(command);
			}	

		}catch(Exception e){
			MainFrame.error("Executing batch script");
		}

	}


	/**
	 * Runs a compiled JavaScript with the Rhino Engine
	 * @param name
	 * @param source
	 */
	public void runJavaScript(String name, String source){

		Script script = null;

		if(compiled_scripts.containsKey(name)){

			script = compiled_scripts.get(name);		
		}else{

			script = compileScript(name, source);
		}

		if(script!=null){
			script.exec(cx, scope);	
		}
	}


	/**
	 * Evaluates a JavaScript with the Rhino Engine
	 * @param name
	 * @param source
	 */
	public void evalJavaScript(String name, String source){

		cx.evaluateString(scope, source, "Line", 1, null);
	}


	/**
	 * Sets the path of the boot script
	 * @param path
	 */
	public void setBoot(String path){
		boot = path;
	}


	/**
	 * Run a script, either .cmd or .js
	 * @param in Path relative to assets//scripts
	 */
	public void runScript(String in){

		
		if(in == null || in.isEmpty()){
			return;
		}

		try{

			String path = SCRIPT_PATH+in;
			String script = Utility.readFile(path);

			if(in.endsWith(SUFFIX_JAVASCRIPT)){

				if(MainFrame.isDebug()){
					evalJavaScript(in,script);
				}else{
					runJavaScript(in,script);
				}

			}else if(in.endsWith(SUFFIX_BATCH)){
				runBatch(script);
			}


		}catch(Exception e){
			MainFrame.error(e.toString());
			e.printStackTrace();
		}
	}


	/**
	 * Compiled a script with the given name and source.
	 * @param name
	 * @param source
	 * @return
	 */
	private Script compileScript(String name, String source){

		Script script = null;

		script = cx.compileString(source, name, 0, null);

		if(script!=null){
			compiled_scripts.put(name, script);
		}

		return script;
	}


	public void destroy(){

		Context.exit();
	}



	/**
	 * Boot
	 */
	public void boot() {

		final CommandRouter exec = new CommandRouter("execute");
		exec.setDescription("Execute a javascript (Parameter = path relative to assets/scripts)");
		exec.setFunction(new Runnable(){

			@Override
			public void run() {
				buffer.add(exec.getParameter());
			}

		});

		final CommandRouter include = new CommandRouter("include");
		include.setDescription("Includes a script as a function: <command; path to script; description> ");

		include.setFunction(new Runnable(){

			@Override
			public void run() {

				String param = include.getParameter();
				includeRouter(param);			
			}

		});


		MainFrame.getCommandHandler().getRootRouter().addRouter(exec);
		MainFrame.getCommandHandler().getRootRouter().addRouter(include);

		
		setup();
		
		if(boot == null || boot.isEmpty()){
			MainFrame.print("Boot script not set");
			return;
		}


		runScript(boot);		
	}



	/**
	 * 
	 * @param param
	 */
	private void includeRouter(String param){

		boolean hasScript = param.contains(ARG_SEPERATOR);

		String subroot = "";
		String routername = param;
		String description = "";
		final String script;

		if(hasScript){

			int idx = param.indexOf(ARG_SEPERATOR);

			String cmd0 = param.substring(0,idx);
			String cmd1 = param.substring(idx+1);

			if(cmd1.contains(ARG_SEPERATOR)){

				int idx_split = cmd1.indexOf(ARG_SEPERATOR);
				description = cmd1.substring(idx_split+1);
				cmd1 = cmd1.substring(0,idx_split);

			}


			param = cmd0;
			script = cmd1.replaceAll(" ", "");
			routername = param;
		}else{
			script = null;
		}

		if(param.contains(" ")){				
			int idx = param.lastIndexOf(" ");
			routername = param.substring(idx+1);	
			subroot = param.substring(0, idx);
		}


		addFunction(routername,script,description,subroot);
	}

	/**
	 * 
	 * @param name
	 * @param script
	 * @param description
	 * @param root
	 */
	public void addFunction(String name, final String script, String description, String root){

		final CommandRouter router = new CommandRouter(name);
		router.setDescription(description);

		if(script!=null){

			router.setFunction(new Runnable(){

				@Override
				public void run() {

					if(router.getParameter()!=null){
						args.add(router.getParameter());
					}else{
						args.add("");
					}

					buffer.add(script);			
				}

			});

		}


		MainFrame.getCommandHandler().getRootRouter().addRouter(router,root);		
	}

	public String getBoot(){	
		return boot;
	}
	

	/**
	 * 
	 * @return
	 */
	public String getArgs(){

		return running_arg;
	}	

	@Override
	public void run() {	

		try{
			boot();		
		}catch(Exception e){
			MainFrame.error("Unexpected error while booting the script runtime.\n");
			e.printStackTrace();
		}
		
		running = true;
		
		while(running){

			
			while(!buffer.isEmpty()){

				if(!args.isEmpty()){
					running_arg = args.pop();
				}

				runScript(buffer.pop());
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		destroy();
	}

	public void shutdown(){
		running = false;
	}



}
