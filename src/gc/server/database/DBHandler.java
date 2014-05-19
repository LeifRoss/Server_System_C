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
import java.util.concurrent.ConcurrentHashMap;


/**
 * 
 * GCServer DBHandler
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class DBHandler {



	private static final String DATABASE_LOCATION = "jdbc:sqlite:"+Util.getAssetsLocation()+"config//database.db";
	private static final String CONFIG_POOL = "database_pooling";
	private static Connection connection;
	private static HashMap<String,DBTable> tables;
	private static ConcurrentHashMap<Long,Connection> connection_pool;
	private static CommandRouter router;
	private static boolean pooling_enabled;

	public static void init(){

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		connection = null;
		try {
			connection = DriverManager.getConnection(DATABASE_LOCATION);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		tables = new HashMap<String,DBTable>();		

		String chk_pool = MainFrame.readConfig(CONFIG_POOL);

		if(chk_pool != null && !chk_pool.isEmpty() && chk_pool.contains("true")){

			pooling_enabled = true;
			connection_pool = new ConcurrentHashMap<Long,Connection>();
		}

		buildRouter();
	}


	/**
	 * Releases all resources held by the handler
	 */
	public static void destroy(){

		try {

			for(DBTable table: tables.values()){
				if(table != null){
					table.destroy();
				}
			}

			if(connection!=null){
				connection.close();
			}

			if(pooling_enabled){

				for(Connection c: connection_pool.values()){
					c.close();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Return the connection belonging to the current thread
	 * @return
	 */
	public static Connection getConnection(){

		if(!pooling_enabled){
			return connection;
		}


		Long thread_id = Thread.currentThread().getId();

		if(connection_pool.containsKey(thread_id)){
			return connection_pool.get(thread_id);
		}

		Connection nConnection = null;

		try {
			nConnection = DriverManager.getConnection(DATABASE_LOCATION);
			connection_pool.put(thread_id, nConnection);
			MainFrame.log("Database connection["+thread_id+"] established", false);
		} catch (SQLException e) {
			e.printStackTrace();
			MainFrame.error(e.getMessage());
		}

		if(nConnection != null){
			return nConnection;
		}

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
			e.printStackTrace();
		}
	}


	/**
	 * Begins a transaction
	 */
	public static void beginTransaction(){

		Connection tConnection = getConnection();

		if(tConnection == null){
			return;
		}

		try {
			tConnection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			MainFrame.error("Begin: "+e.getMessage());
		}

	}


	/**
	 * Commits the current transaction
	 */
	public static void commitTransaction(){

		Connection tConnection = getConnection();

		if(tConnection==null){
			return;
		}

		try {
			tConnection.commit();
		} catch (SQLException e) {

			e.printStackTrace();
			MainFrame.error("Commit: "+e.getMessage());		
		}finally{

			try {
				tConnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}


	/**
	 * Aborts the current transaction
	 */
	public static void abortTransaction(){

		Connection tConnection = getConnection();

		if(tConnection==null){
			return;
		}

		try {
			tConnection.rollback();
		} catch (SQLException e) {

			e.printStackTrace();
			MainFrame.error("Rollback: "+e.getMessage());		
		}finally{

			try {
				tConnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
