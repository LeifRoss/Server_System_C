package gc.server.util;

import gc.server.com.MainFrame;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

import connectivity.utility.Utility;

public class ShowHandler {

	private static CommandRouter router;

	public static void init(){

		router = new CommandRouter("show");
		router.setDescription("show functions");

		buildConfigRouters();
		buildNetRouters();


		MainFrame.add(router);
	}


	private static void buildConfigRouters(){

		CommandRouter boot = new CommandRouter("boot");
		boot.setDescription("show boot script");
		boot.setFunction(new Runnable(){

			@Override
			public void run() {
				showBoot();		
			}

		});

		CommandRouter config = new CommandRouter("config");
		config.setDescription("show config");
		config.setFunction(new Runnable(){

			@Override
			public void run() {
				showConfig();
			}

		});


		CommandRouter system = new CommandRouter("system");
		system.setDescription("show system properties");
		system.setFunction(new Runnable(){

			@Override
			public void run() {
				showSystem();
			}

		});

		final CommandRouter file = new CommandRouter("file");
		file.setDescription("show file system");
		file.setFunction(new Runnable(){

			@Override
			public void run() {
				showFile(file.getParameter());
				file.setParameter("");
			}

		});

		CommandRouter file_all = new CommandRouter("all");
		file_all.setDescription("Show all files");
		file_all.setFunction(new Runnable(){

			@Override
			public void run() {
				showFileAll();
			}

		});
		file.addRouter(file_all);

		router.addRouter(boot);
		router.addRouter(config);
		router.addRouter(system);
		router.addRouter(file);
	}

	private static void buildNetRouters(){

		CommandRouter netstat = new CommandRouter("netstat");
		netstat.setDescription("show interfaces and addresses");
		netstat.setFunction(new Runnable(){

			@Override
			public void run() {
				try {
					showNet(false);
				} catch (SocketException e) {
					MainFrame.error(e.getMessage());
					e.printStackTrace();
				}	
			}

		});

		CommandRouter brief = new CommandRouter("brief");
		brief.setFunction(new Runnable(){

			@Override
			public void run() {
				try {
					showNet(true);
				} catch (SocketException e) {
					MainFrame.error(e.getMessage());
					e.printStackTrace();
				}
			}

		});
		netstat.addRouter(brief);

		router.addRouter(netstat);								

	}


	private static void showFileAll(){

		String path = Util.getAssetsLocation();
		File f = new File(path);
		StringBuilder str = new StringBuilder();
		showFileAllRecursive(str,f,-1);

		MainFrame.print(str.toString());
	}

	private static void showFileAllRecursive(StringBuilder str, File f, int depth){

		if(depth >= 0){
			str.append(Util.repeat("\t", depth)+f.getName()+"\n");
		}

		if(f.isDirectory()){

			for(File child: f.listFiles()){
				showFileAllRecursive(str,child,depth+1);
			}	
		}


	}



	private static void showFile(String in){

		if(in == null){
			in = "";
		}

		StringBuilder str = new StringBuilder();
		File f = new File(Util.getAssetsLocation()+in);

		if(!f.exists()){
			MainFrame.error("No such location <"+in+">");
			return;
		}

		if(f.isDirectory()){


			for(File child: f.listFiles()){
				str.append(child.getName()+"\n");
			}

		}else if(f.isFile()){

			String data = Utility.readFile(f.getPath());
			str.append(data);

		}


		MainFrame.print(str.toString());

	}

	private static void showSystem(){


		StringBuilder str = new StringBuilder();
		Properties properties = System.getProperties();


		for(String key: properties.stringPropertyNames()){

			str.append(key+" "+properties.getProperty(key)+"\n");	
		}

		MainFrame.print(str.toString());

	}


	private static void showConfig(){


		String path = MainFrame.getConfigPath();
		MainFrame.print("Config at location <"+path+">\n");
		MainFrame.print(Utility.readFile(path));		


	}

	private static void showBoot(){

		if(MainFrame.getScriptEnv() == null){
			return;
		}

		String boot = MainFrame.getScriptEnv().getBoot();

		if(boot == null){
			return;
		}

		boot = Util.getAssetsLocation()+"scripts/"+boot;

		MainFrame.print("Boot at location <"+boot+">\n");
		String data = Utility.readFile(boot);		
		MainFrame.print(data);


	}


	/**
	 * Show the machines network interfaces and addresses
	 * @throws SocketException
	 */
	private static void showNet(boolean brief) throws SocketException{

		StringBuilder str = new StringBuilder();


		if(brief){

			try {
				InetAddress inet = InetAddress.getLocalHost();
				str.append("[Host]: "+inet.getHostName()+"\n");
				str.append("\t[IP]: "+inet.getHostAddress()+"\n");

			} catch (UnknownHostException e) {
				MainFrame.error(e.getMessage());
				e.printStackTrace();
			}            


		}else{

			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<InetAddress> ee = n.getInetAddresses();

				str.append(n.getName()+ " <"+n.getDisplayName() +">\n");  

				while (ee.hasMoreElements())
				{
					InetAddress inet =  ee.nextElement();

					str.append("\t[Host]: "+inet.getHostName()+"\n");
					str.append("\t\t[IP]: "+inet.getHostAddress()+"\n");
				}
			}	
		}

		MainFrame.print(str.toString());
	}

}
