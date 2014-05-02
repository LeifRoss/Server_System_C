package gc.server.util;

import gc.server.com.MainFrame;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import connectivity.utility.Utility;


/**
 * 
 * GCServer Util
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class Util {


	private static String assetsLocation;

	/**
	 * Returns the location to the asset folder. brute force time..
	 * @return
	 */
	public static String getAssetsLocation(){


		if(assetsLocation != null && !assetsLocation.isEmpty()){
			return assetsLocation;			
		}


		String path = "";

		try {

			File jarpath = new File(MainFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			
			path = jarpath.getPath();
			path = path.replace("\\", "/");

			if(path.endsWith("bin/")){
				path = path.replaceAll("bin/", "");
			}else if(path.endsWith("bin//")){
				path = path.replaceAll("bin//", "");
			}else if(path.endsWith("bin")){
				path = path.replaceAll("bin", "");
			}

			if(path.endsWith(".jar")){			
				path = path.substring(0, path.lastIndexOf("/")+1);		
			}
						
			assetsLocation = path + "assets//";		

		}  catch (URISyntaxException e) {
			e.printStackTrace();
		}


		return assetsLocation;
	}

	/**
	 * Convert a string in the csv format to a string table with the format [row][column]
	 * @param csv
	 * @return
	 */
	public static String[][] toTable(String csv){

		char escape = '\\';
		char string_sep = '\'';
		char column_sep = ',';


		char[] data = csv.toCharArray();
		String value = "";


		ArrayList<String> row = new ArrayList<String>();
		ArrayList<String[]> table = new ArrayList<String[]>();

		boolean inLine = false;
		int nColumns = 0;

		for(int i = 0; i < data.length; i++){

			char c = data[i];

			// optimize this!!
			if(c == escape && !inLine || i==data.length-1 || c == '\n'){	

				if(i < data.length-1 && data[i+1] == 'n' || i==data.length-1 || c == '\n'){

					row.add(value);
					value = "";

					nColumns = Math.max(nColumns, row.size());
					String[] row_array = row.toArray(new String[row.size()]);

					table.add(row_array);
					row.clear();	

				}

			}else if(c == column_sep && !inLine){	

				row.add(value);
				value = "";

			}else if(c == string_sep){			
				inLine = !inLine;

			}else{		
				value += c;
			}

		}


		String[][] result = new String[table.size()][nColumns];

		for(int r = 0; r < table.size(); r++){

			String[] row_array = table.get(r);

			for(int c = 0; c < row_array.length; c++){
				result[r][c] = row_array[c];
			}		

		}		

		return result;		
	}
	
	/**
	 * Reat a string n times
	 * @param s
	 * @param n
	 * @return
	 */
	public static String repeat(String s, int n){
		
		if(n <= 0){
			return "";
		}
		
		String result  = new String(new char[n]).replace("\0", s);
		
		return result;
	}


}
