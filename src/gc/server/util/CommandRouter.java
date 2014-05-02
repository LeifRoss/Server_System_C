package gc.server.util;


import gc.server.com.MainFrame;

import java.util.HashMap;

/**
 * 
 * GCServer CommandRouter
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class CommandRouter {


	private HashMap<String,CommandRouter> subrouters;

	private static final String HELP = "?";
	private static final String QUOTE = "\"";

	private final String name;
	private String description;

	private Runnable function;
	private String parameter;

	private boolean internal;


	public CommandRouter(String name, boolean internal){

		this.internal = internal;
		this.name = name;
		clear();	
	}

	public CommandRouter(String name){

		this.internal = false;
		this.name = name;
		clear();	
	}


	/**
	 * 
	 */
	public void clear(){

		description = "";
		subrouters = new HashMap<String,CommandRouter>();
	}


	/**
	 * 
	 * @param router
	 */
	public void addRouter(CommandRouter router){

		subrouters.put(router.name, router);
	}

	/**
	 * 
	 * @param router
	 * @param root
	 */
	public void addRouter(CommandRouter router, String root){

		if(root.isEmpty()){

			if(!containsRouter(router.getName())){
				addRouter(router);
				if(!internal)
					MainFrame.print("Included function <"+router.getName()+"> under "+name+"\n");
			}else{
				if(!internal)
					MainFrame.error("Function <"+router.getName()+"> Already Exists!\n");
			}

			return;
		}


		if(root.contains(" ")){

			int idx = root.indexOf(" ");
			String cmd0 = root.substring(0,idx);
			String cmd1 = root.substring(idx+1);

			cmd0 = autocomplete(cmd0);
			CommandRouter next = getRouter(cmd0);

			if(next!=null){
				next.addRouter(router, cmd1);
			}else{
				MainFrame.error("No such function <"+cmd0+">");
			}
		}else{

			root = autocomplete(root);
			CommandRouter next = getRouter(root);

			if(next!=null){
				next.addRouter(router, "");
			}else{
				MainFrame.error("No such function <"+root+">");
			}

		}


	}


	public boolean removeRouter(String in){

		boolean sub = in.contains(" ");

		if(sub){

			int idx = in.indexOf(" ");
			String cmd0 = autocomplete(in.substring(0,idx));
			String cmd1 = in.substring(idx+1);

			if(containsRouter(cmd0)){

				CommandRouter next = getRouter(cmd0);

				if(next!=null){
					return next.removeRouter(cmd1);
				}
			}

			MainFrame.error("No such function "+cmd0);
			return false;
		}


		in = autocomplete(in);

		if(containsRouter(in)){

			subrouters.remove(in);
			if(!internal){
				MainFrame.error("Removed function <"+in+"> under <"+name+">");
			}

			return true;
		}

		return false;
	}





	/**
	 * 
	 * @param in
	 * @return
	 */
	public boolean containsRouter(String in){

		return subrouters.containsKey(in);
	}

	/**
	 * 
	 * @param in
	 * @return
	 */
	public CommandRouter getRouter(String in){

		if(containsRouter(in)){	
			return subrouters.get(in);
		}

		return null;
	}


	/**
	 * Returns the autocompleted string based on this Routers Subrouters
	 * @param in
	 * @return
	 */
	public String autocomplete(String in){

		if(containsRouter(in)){
			return in;
		}

		String cmp = in.toLowerCase();

		for(CommandRouter r : subrouters.values()){

			if(r == null){
				continue;
			}

			String subname = r.getName().substring(0,Math.min(r.getName().length(),in.length())).toLowerCase();

			if(subname.equals(cmp)){
				return r.getName();
			}

		}			

		return in;
	}


	/**
	 * Routes the command
	 * @param command
	 */
	public void route(String command){


		try{
			if(command.endsWith(QUOTE)){

				int paramStart = command.indexOf(QUOTE);
				int paramEnd = command.lastIndexOf(QUOTE);

				this.setParameter(command.substring(paramStart+1, paramEnd));
			}
		}catch(Exception e){
			e.printStackTrace();
		}


		if(command.isEmpty() || command.startsWith(QUOTE)){


			if(function!=null){	
				function.run();	
			}

			return;

		}



		if(!command.contains(" ")){

			if(command.equals(HELP)){

				help();

			}else if(containsRouter(autocomplete(command))){

				CommandRouter nextRouter = subrouters.get(autocomplete(command));			
				nextRouter.route("");		

			}else{

				MainFrame.error("No such function <"+command+">");
			}

			return;
		}


		int space = command.indexOf(" ");

		String cmd = autocomplete(command.substring(0, space));

		if(containsRouter(cmd)){


			CommandRouter nextRouter = subrouters.get(cmd);			
			nextRouter.route(command.substring(space+1, command.length()));		

		}else{

			MainFrame.error("No such function <"+cmd+">");
		}

	}

	/**
	 * Sets the router command parameter
	 * @param in
	 */
	public void setParameter(String in){
		this.parameter = in;
	}

	/**
	 * Returns the router command parameter
	 * @return
	 */
	public String getParameter(){
		return parameter;
	}

	/**
	 * Sets the function that this router will execute if the query lands here.
	 * @param r
	 */
	public void setFunction(Runnable r){
		this.function = r;
	}


	/**
	 * Set the description of this router
	 * @param in
	 */
	public void setDescription(String in){
		this.description = in;
	}

	/**
	 * Return the description of this router
	 * @return
	 */
	public String getDescription(){
		return description;
	}


	/**
	 * Return the name of this router, this is also used for routing
	 * @return
	 */
	public String getName(){
		return name;
	}

	/**
	 * Get helptext
	 * @return
	 */
	public String getHelpText(){

		StringBuilder str = new StringBuilder();

		for(CommandRouter r : subrouters.values()){

			str.append("\t> "+r.toString()+"\n");
		}

		return str.toString();
	}

	private void help(){

		MainFrame.print(getHelpText());
	}

	@Override
	public String toString(){
		return getName()+"\t\t.."+getDescription();
	}

}
