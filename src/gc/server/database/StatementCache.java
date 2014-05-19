package gc.server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class StatementCache {

	private ConcurrentHashMap<String,String> query_cache;
	private ConcurrentHashMap<Long,HashMap<String,PreparedStatement>> thread_statements;
	private ArrayList<PreparedStatement> statements;


	public StatementCache(){

		query_cache = new ConcurrentHashMap<String,String>();
		thread_statements = new ConcurrentHashMap<Long,HashMap<String,PreparedStatement>>();
		statements = new ArrayList<PreparedStatement>();
	}


	/**
	 * Create a statement
	 * @param id
	 * @param query
	 */
	public synchronized void create(String id, String query){
		query_cache.put(id, query);
	}


	/**
	 * Retrieve a statement
	 * @param id
	 * @return
	 */
	public PreparedStatement get(String id){

		HashMap<String,PreparedStatement> statements = getMap();

		if(statements.containsKey(id)){
			return statements.get(id);
		}		

		if(!query_cache.containsKey(id)){
			return null;
		}

		String query = query_cache.get(id);

		PreparedStatement statement = null;
		try {
			statement = DBHandler.getConnection().prepareStatement(query);
			statements.put(id, statement);	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return statement;
	}


	private HashMap<String,PreparedStatement> getMap(){

		Long thread_id = Thread.currentThread().getId();

		if(thread_statements.containsKey(thread_id)){
			return thread_statements.get(thread_id);
		}

		HashMap<String,PreparedStatement> map = new HashMap<String,PreparedStatement>();
		thread_statements.put(thread_id, map);

		return map;
	}


	/**
	 * Does the statement exist
	 * @param id
	 * @return
	 */
	public boolean contains(String id){
		return query_cache.containsKey(id);
	}


	/**
	 * Release all resources
	 */
	public void destroy(){

		for(PreparedStatement p: statements){
			try {
				p.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		statements.clear();
		statements = null;

		query_cache.clear();
		query_cache = null;

		thread_statements.clear();
		thread_statements = null;		
	}

}
