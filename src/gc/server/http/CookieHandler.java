package gc.server.http;

import gc.server.database.DBTable;

import java.util.Random;

import connectivity.utility.Crypter;

/**
 * Cookie handler class
 * 
 * @author Leif Andreas Rudlang
 * @date 15.02.2014
 */
public class CookieHandler {


	private static final String COOKIE_TABLE = "cookies";
	private static final String COOKIE_TABLE_STRUCT = "id text not null primary key, expires integer not null, session_name text";
	private static final String COOKIE_INSERT = "insert";
	private static final String COOKIE_SELECT = "select";
	private static final String COOKIE_VALID = "valid";
	private static final String COOKIE_DELETE = "delete";
	private static final String COOKIE_DELETE_OLD = "delete_old";
	
	private static final int COOKIE_EXPIRING = 1000;

	private static Random r;
	private static DBTable table;

	/**
	 * Initiates the cookie handler
	 * This method must be called before anything else in this class
	 */
	public static void init(){

		r = new Random();

		table = new DBTable(COOKIE_TABLE, COOKIE_TABLE_STRUCT);
		table.createTable();

		table.createSQL(COOKIE_INSERT, "insert into cookies values(?,?,?)");
		table.createSQL(COOKIE_SELECT, "select * from cookies where id=?");
		table.createSQL(COOKIE_VALID, "select id from cookies where id=? and expires > ? and session_name = ?");
		table.createSQL(COOKIE_DELETE, "delete from cookies where id = ?");
		table.createSQL(COOKIE_DELETE_OLD, "delete from cookies where expires < ?");
		
	}

	/**
	 * Creates a new cookie
	 * @return
	 */
	public static String createCookie() {

		String hash =  Crypter.hash(Integer.toString(r.nextInt()), Crypter.SHA512);
		String expiring =  Integer.toString((int)(System.currentTimeMillis() / 1000) + COOKIE_EXPIRING);
		String session_name = "";

		table.update(COOKIE_INSERT, hash, expiring, session_name);
		String cookie = "name=id; value="+hash+"; Expires="+expiring+";";


		return cookie;
	}


	/**
	 * Checks if a cookie is still valid
	 * @param cookie
	 * @return
	 */
	public static boolean isValid(String cookie){


		String id = seperate("id",cookie);	
		String time = Long.toString(System.currentTimeMillis()/1000);	
		String session_name = seperate("value",cookie);
		
		String[][] data = table.query(COOKIE_VALID, id,time,session_name);	
		
		if(data!=null && data.length > 0){
			return true;
		}

		return false;
	}

	/**
	 * Deletes old cookies
	 */
	public static void deleteOldCookies(){
				
		String time = Long.toString(System.currentTimeMillis()/1000);	
		table.update(COOKIE_DELETE_OLD, time);		
	}

	/**
	 * Deletes one cookie
	 * @param cookie
	 */
	public static void deleteCookie(String cookie){
		
		String id = seperate("id",cookie);
		table.update(COOKIE_DELETE, id);
		
	}

	/**
	 * Seperates a cookie string into the given value
	 * @param name
	 * @param cookie
	 * @return
	 */
	private static String seperate(String name, String cookie){

		if(!cookie.contains(name)){
			return null;
		}
		
		int idx0 = cookie.indexOf(name);
		int idx1 = cookie.indexOf('=', idx0);
		int idx2 = cookie.indexOf(';',idx1);
		
		String value = cookie.substring(Math.min(cookie.length(), idx1+1),Math.min(cookie.length(), idx2));

		return value;
	}




}
