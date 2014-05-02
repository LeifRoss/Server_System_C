package gc.server.ssh;

import gc.server.com.MainFrame;
import gc.server.util.CommandRouter;
import gc.server.util.Util;

import java.io.File;
import java.util.HashMap;

import connectivity.utility.Crypter;
import connectivity.utility.Utility;

public class SSHHandler {


	private static HashMap<String,String> remote_user;
	private static final String SSH_USER_CONF_LOCATION = Util.getAssetsLocation()+"config//ssh_user_config.txt";

	private static CommandRouter router, add_user, remove_user;
	private static CommandRouter ssh_router, ssh_start, ssh_stop;
	private static CommandRouter sftp_router, sftp_start, sftp_stop;

	private static SSHServer ssh_server;
	private static SFTPServer sftp_server;

	public static void init(){		


		remote_user = new HashMap<String,String>();

		String ssh_port = MainFrame.readConfig("ssh_port");
		if(ssh_port.isEmpty()){
			ssh_server = new SSHServer();
		}else{
			int port = 22;

			try{
				port = Integer.parseInt(ssh_port);
			}catch(Exception e){
				MainFrame.error(e.getMessage());
			}			

			ssh_server = new SSHServer(port);
		}

		
		String sftp_port = MainFrame.readConfig("sftp_port");
		if(sftp_port.isEmpty()){
			sftp_server = new SFTPServer();
		}else{
			int port = 21;

			try{
				port = Integer.parseInt(sftp_port);
			}catch(Exception e){
				MainFrame.error(e.getMessage());
			}			

			sftp_server = new SFTPServer(port);
		}

		

		loadUsers();
		buildRouter();
	}


	private static void loadUsers(){

		File f = new File(SSH_USER_CONF_LOCATION);

		if(!f.exists()){
			Utility.writeFile(SSH_USER_CONF_LOCATION, "");
			return;
		}

		String csv = Utility.readFile(SSH_USER_CONF_LOCATION);
		String[][] table = Util.toTable(csv);

		if(table == null || table.length == 0 || table[0].length < 2){
			return;
		}

		for(int row = 0; row < table.length; row++){

			String username = table[row][0];
			String password = table[row][1];

			if(username == null || username.isEmpty() || password == null || password.isEmpty()){
				continue;
			}

			remote_user.put(username, password);
		}			

	}

	private static void addUser(String in){

		String[] split = in.split(";");

		String username = split[0];
		String password = split[1];

		if(remote_user.containsKey(username)){
			MainFrame.error("SSH: User <"+username+"> already exists!");
			return;
		}

		password = Crypter.hash(password, Crypter.SHA512);

		String csv = "'"+username+"','"+password+"'\n";

		String data = Utility.readFile(SSH_USER_CONF_LOCATION)+csv;
		Utility.writeFile(SSH_USER_CONF_LOCATION, data);
		remote_user.put(username, password);


		MainFrame.print("SSH: User <"+username+"> created!");
	}


	private static void removeUser(String username){

		if(username == null || username.isEmpty() || !remote_user.containsKey(username)){
			MainFrame.error("SSH: No such user!");
			return;
		}

		StringBuilder str = new StringBuilder();

		String csv = Utility.readFile(SSH_USER_CONF_LOCATION);
		String[][] table = Util.toTable(csv);

		if(table == null || table.length == 0 || table[0].length < 2){
			return;
		}

		for(int row = 0; row < table.length; row++){

			String user = table[row][0];
			String password = table[row][1];

			if(user == null || user.isEmpty() || password == null || password.isEmpty()){
				continue;
			}

			if(!user.equals(username)){
				str.append("'"+user+"','"+password+"'\n");
			}

		}	

		Utility.writeFile(SSH_USER_CONF_LOCATION, str.toString());



		remote_user.remove(username);		
		MainFrame.print("SSH: User <"+username+"> removed!");
	}


	public static boolean verify(String username, String password){

		if(username == null || username.isEmpty() || password == null || password.isEmpty()){
			return false;
		}


		if(remote_user.containsKey(username)){

			String pw = remote_user.get(username);
			String crypt = Crypter.hash(password, Crypter.SHA512);			
			if(pw.equals(crypt)){
				return true;
			}
		}






		return false;
	}

	private static void buildRouter(){


		// Remote management router

		router = new CommandRouter("remote");
		router.setDescription("Remote management (ssh/sftp)");

		add_user = new CommandRouter("add");
		add_user.setDescription("Add a user to remote \"username;password\"");
		add_user.setFunction(new Runnable(){

			@Override
			public void run() {
				addUser(add_user.getParameter());
			}

		});


		remove_user = new CommandRouter("remove");
		remove_user.setDescription("Remove a user from remote \"username\"");
		remove_user.setFunction(new Runnable(){

			@Override
			public void run() {
				removeUser(remove_user.getParameter());
			}

		});


		router.addRouter(add_user);
		router.addRouter(remove_user);


		// SSH routers

		ssh_router = new CommandRouter("ssh");
		ssh_router.setDescription("ssh functions");



		ssh_start = new CommandRouter("start");
		ssh_start.setDescription("start the ssh server");
		ssh_start.setFunction(new Runnable(){

			@Override
			public void run() {
				ssh_server.start();
			}

		});

		ssh_stop = new CommandRouter("stop");
		ssh_stop.setDescription("stop the ssh server");
		ssh_stop.setFunction(new Runnable(){

			@Override
			public void run() {
				ssh_server.stop();
			}

		});


		ssh_router.addRouter(ssh_start);
		ssh_router.addRouter(ssh_stop);
		router.addRouter(ssh_router);

		// SFTP routers

		sftp_router = new CommandRouter("sftp");
		sftp_router.setDescription("sftp functions");

		sftp_start = new CommandRouter("start");
		sftp_start.setDescription("start the sftp server");
		sftp_start.setFunction(new Runnable(){

			@Override
			public void run() {
				sftp_server.start();
			}

		});


		sftp_stop = new CommandRouter("stop");
		sftp_stop.setDescription("stop the sftp server");
		sftp_stop.setFunction(new Runnable(){

			@Override
			public void run() {
				sftp_server.stop();
			}

		});


		sftp_router.addRouter(sftp_start);
		sftp_router.addRouter(sftp_stop);
		router.addRouter(sftp_router);


		MainFrame.add(router);

	}


}
