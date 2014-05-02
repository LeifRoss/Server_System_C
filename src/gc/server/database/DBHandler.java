package gc.server.database;

import gc.server.com.MainFrame;
import gc.server.util.CommandRouter;
import gc.server.util.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


/**
 * 
 * GCServer DBHandler
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class DBHandler {



	private static final String DATABASE_LOCATION = "jdbc:sqlite:"+Util.getAssetsLocation()+"config//database.db";
	private static Connection connection;
	private static HashMap<String,DBTable> tables;
	private static CommandRouter router;

	public static void init(){

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		connection = null;
		try {

			connection = DriverManager.getConnection(DATABASE_LOCATION);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		tables = new HashMap<String,DBTable>();		

		buildRouter();
	}


	public static void destroy(){

		try {

			if(connection!=null){
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public static Connection getConnection(){
		return connection;
	}


	public static void remove(String name){

		if(tables.containsKey(name)){
			tables.remove(name);
		}
	}

	public static void add(DBTable table){

		tables.put(table.name(), table);
	}

	public static DBTable get(String table){

		if(tables.containsKey(table)){
			return tables.get(table);
		}

		MainFrame.print("Table ("+table+") not found");
		return null;
	}

	private static void buildRouter(){

		router = new CommandRouter("database");
		router.setDescription("database functions");

		final CommandRouter update = new CommandRouter("update");
		update.setDescription("Executes a sql statement(INSERT, UPDATE, DELETE..)");
		update.setFunction(new Runnable(){

			@Override
			public void run() {
				update(update.getParameter());				
			}

		});


		final CommandRouter query = new CommandRouter("query");
		query.setDescription("Executes a sql query");
		query.setFunction(new Runnable(){

			@Override
			public void run() {
				query(query.getParameter());				
			}

		});

		router.addRouter(query);
		router.addRouter(update);
		MainFrame.getCommandHandler().getRootRouter().addRouter(router);

	}


	public static DBTable[] getTables(){
		int size = tables.size();
		DBTable[] result = new DBTable[size];

		int i = 0;
		for(DBTable t : tables.values()){
			result[i++] = t;				
		}


		return result;
	}

	public static void query(String in){


		if(connection==null){
			return;
		}


		try {

			Statement s = connection.createStatement();

			ResultSet result = s.executeQuery(in);
			String[][] data = DBTable.toTable(result);
			result.close();		

			String csv = DBTable.toCSVString(data);

			MainFrame.print(csv);

		} catch (SQLException e) {
			MainFrame.print("DATABASE: "+e.getMessage());
			e.printStackTrace();
		}

	}


	public static void update(String in){


		if(connection==null){
			return;
		}

		try {
			Statement s = connection.createStatement();

			int result = s.executeUpdate(in);


			String resultcode = "";

			switch(result){

			case Statement.SUCCESS_NO_INFO:
				resultcode = "Success";
				break;

			case Statement.EXECUTE_FAILED:
				resultcode = "Execute failed";
				break;

			default:
				resultcode = "Success";
			}

			MainFrame.print("DATABASE UPDATE: "+resultcode);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public static void beginTransaction(){

		if(connection==null){
			return;
		}

		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void commitTransaction(){

		if(connection==null){
			return;
		}

		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * Check if a table exists in the database
	 * @param name
	 * @return true if the table exists
	 */
	public static boolean tableExists(String name){

		if(connection == null){
			return false;
		}

		Statement s = null;
		boolean exists = true;
		try {
			s = connection.createStatement();


			ResultSet result = s.executeQuery("SELECT DISTINCT name FROM sqlite_master WHERE type='table' AND name='"+name+"'");
			exists = result.isBeforeFirst();
			result.close();

		} catch (SQLException e) {
			e.printStackTrace();
			MainFrame.error(e.getMessage());
			exists = false;
		}finally{
			try {
				if(s != null){
					s.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


		return exists;
	}



}
