package gc.server.database;


import gc.server.com.MainFrame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * 
 * GCServer DBTable
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class DBTable {


	private String name, struct;
	private HashMap<String,PreparedStatement> statements;
	private PreparedStatement pragma;
	private int number_columns;


	/**
	 * 
	 * @param name
	 * @param struct
	 */
	public DBTable(String name, String struct){

		this.name = name;
		this.struct = struct;

		init();
	}


	/**
	 * Table name
	 * @return
	 */
	public String name(){
		return name;
	}


	/**
	 * Table structure
	 * @return
	 */
	public String struct(){
		return struct;
	}


	public void init(){

		statements = new HashMap<String,PreparedStatement>();

		Connection connection = DBHandler.getConnection();


		String[] columns = struct().split(",");
		number_columns = columns.length;


		try {

			connection.setAutoCommit(false);
			pragma = connection.prepareStatement("pragma table_info ("+name()+")");
			connection.setAutoCommit(true);

		} catch (SQLException e) {
			e.printStackTrace();
			MainFrame.print(e.getMessage());
		}

		DBHandler.add(this);
	}


	/**
	 * Return number of columns in the table
	 * @return
	 */
	public int getNumberOfColumns(){
		return number_columns;
	}

	/**
	 * Create the table
	 */
	public void createTable(){

		
		if(DBHandler.getConnection()==null){
			return;
		}

		try {

			Statement s = DBHandler.getConnection().createStatement();
			s.execute("create table if not exists "+name()+" ("+struct()+")");

		} catch (SQLException e) {
			e.printStackTrace();
			MainFrame.print(e.getMessage());
		}

	}

	
	/**
	 * Create the virtual table
	 */
	public void createVirtualTable(){

		
		if(DBHandler.getConnection()==null){
			return;
		}

		if(DBHandler.tableExists(name())){
			return;
		}
		
		try {
			Statement s = DBHandler.getConnection().createStatement();
			s.execute("create virtual table "+name()+" using fts4("+struct()+")");

		} catch (SQLException e) {
			e.printStackTrace();
			MainFrame.print(e.getMessage());
		}
	}
	
	
	public String[] getStructureArray(){

		return struct.split(",");
	}



	/**
	 * Drop the table
	 */
	public void dropTable(){

		if(DBHandler.getConnection()==null){
			return;
		}

		try {

			Statement s = DBHandler.getConnection().createStatement();
			s.execute("drop table if exists "+name());
	

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	/**
	 * 
	 * @return
	 */
	public String[][] pragma(){

		if(DBHandler.getConnection()==null || pragma==null){
			return null;
		}

		String[][] data = null;

		try {


			ResultSet result = pragma.executeQuery();
			data = toTable(result);
			result.close();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;
	}



	public void setStruct(String in){
		this.struct = in;
	}




	/**
	 * Run a update
	 * @param id
	 * @param params
	 */
	public void update(String id, String... params){

		if(DBHandler.getConnection()==null || !statements.containsKey(id)){
			return;
		}

		try {

			PreparedStatement statement = statements.get(id);

			for(int i = 0; i < params.length; i++){
				statement.setObject(i+1, params[i]);
			}

			statement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MainFrame.print(e.getMessage());
		}			

	}


	public String[] getColumns(){

		String[] columns = struct.split(",");

		for(int i = 0; i < columns.length; i++){

			String cname = columns[i].trim();

			if(cname.contains(" ")){				
				cname = cname.substring(0,cname.indexOf(" "));
			}

			columns[i] = cname;
		}


		return columns;
	}


	public String getColumnAttribute(String column, String ...attr){
		
		
		String[] columns = struct.split(",");
		String c = columns[0];
		
		for(int i = 0; i < columns.length; i++){

			String cname = columns[i].trim();

			if(cname.contains(" ")){				
				cname = cname.substring(0,cname.indexOf(" "));
			}
			
			if(cname.equals(column)){	
				c = columns[i].toLowerCase();
				break;
			}

		}
				
		for(String s : attr){
			if(c.contains(s.toLowerCase())){
				return s;
			}
		}
				
		return "false";
	}
	
	
	
	/**
	 * Run a query
	 * @param id
	 * @param params
	 * @return
	 */
	public String[][] query(String id, String... params){

		if(DBHandler.getConnection()==null || !statements.containsKey(id)){
			return null;
		}

		try {

			PreparedStatement statement = statements.get(id);

			for(int i = 0; i < params.length; i++){
				statement.setObject(i+1, params[i]);
			}

			
			ResultSet result = statement.executeQuery();
			String[][] data = toTable(result);
			result.close();

			return data;

		} catch (SQLException e) {
			e.printStackTrace();
		}			


		return null;
	}


	/**
	 * Create a sql preparedstatement
	 * @param id
	 * @param query
	 */
	public void createSQL(String id, String query){


		if(DBHandler.getConnection()==null){
			return;
		}

		try {

			PreparedStatement statement = DBHandler.getConnection().prepareStatement(query);
			statements.put(id, statement);		

		} catch (SQLException e) {
			e.printStackTrace();
			MainFrame.print(e.getMessage());
		}


	}


	/**
	 * Convert a result set to a String table
	 * @param rs
	 * @return
	 */
	public static String[][] toTable(ResultSet rs){

		ArrayList<String[]> rowdata = new ArrayList<String[]>();

		try {

			ResultSetMetaData meta = rs.getMetaData();
			int columns = meta.getColumnCount();


			while(rs.next()){

				String[] data = new String[columns];

				for(int i = 0; i < columns; i++){
					data[i] = rs.getString(i+1);				
				}

				rowdata.add(data);
			}


			String[][] table = new String[rowdata.size()][columns];

			for(int i = 0; i < rowdata.size(); i++){
				table[i] = rowdata.get(i);
			}

			return table;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}


	/**
	 * Converts a String table into a String in a semi-csv format
	 * @param table
	 * @return
	 */
	public static String toCSVString(String[][] table){

		if(table == null || table.length == 0){
			return "null";
		}
		
		StringBuilder str = new StringBuilder();

		for(int rows = 0; rows < table.length; rows++){
			for(int columns = 0; columns < table[0].length; columns++){
				str.append(table[rows][columns]+"\t");
			}

			str.append("\n");
		}

		return str.toString();
	}

	/**
	 * Converts a String table into a String in a csv format
	 * @param table
	 * @return
	 */
	public static String toCSV(String[][] table){

		if(table == null || table.length == 0){
			return "null";
		}
		
		
		StringBuilder str = new StringBuilder();

		for(int rows = 0; rows < table.length; rows++){
			for(int columns = 0; columns < table[0].length; columns++){
				str.append("'"+table[rows][columns]+"'");
				
				if(columns < table[0].length-1){
					str.append(',');
				}				
			}

			str.append("\n");
		}

		return str.toString();
	}

}
