package gc.server.gateway;

import gc.server.database.DBTable;

import java.util.Random;

import connectivity.utility.Crypter;

public class GatewayDatabase {


	private DBTable users, coordinates;
	private Random random;

	public GatewayDatabase(){

	
		setup();
	}

	private void setup(){

		
		users = new DBTable("user",
				"id integer primary key autoincrement,"+
						"username text not null,"+
						"password text not null,"+
						"salt text not null"
				);
		
		
		coordinates = new DBTable("coordinate",
				"id integer primary key autoincrement,"+
				"worldx integer, worldy integer,"+
				"subx integer, suby integer,"+
				"relx integer, rely integer"
				);
		
		
		
		users.createTable();
		coordinates.createTable();
		

		users.createSQL("get credentials", "select password, salt from user where username = ?");

		users.createSQL("check username", "select id from user where username = ?");
		users.createSQL("register", "insert into user(id,username,password,salt) values(null,?,?,?)");
		
		
		
		random = new Random();
	}


	public boolean checkCredentials(String username, String password){

		String[][] table = users.query("get credentials", username);

		String db_password = table[0][0];
		String salt = table[0][1];

		String hash = Crypter.hash(password+salt, Crypter.SHA512);

		if(db_password.equals(hash)){
			return true;
		}		

		return false;
	}


	public boolean usernameExists(String username){
		
		String[][] table = users.query("check username", username);
		
		if(table.length == 0){
			return false;
		}
			
		return true;
	}
	

	public boolean registerUser(String username, String password){
						
		
		String salt = Crypter.hash(""+random.nextInt(), Crypter.SHA512);
		String db_password = Crypter.hash(password+salt, Crypter.SHA512);
		
		return users.update("register", username, db_password, salt);
	}
	

}
