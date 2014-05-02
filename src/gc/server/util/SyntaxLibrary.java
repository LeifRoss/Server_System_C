package gc.server.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SyntaxLibrary {

	private HashMap<String,Color> syntaxGroup;	
	private HashMap<String,Color> reserved;
	private HashMap<String,Color> group;
	private HashMap<String,String> groupTerminator;
	
	private Set<String> groupSet;
	
	private char escape;
	private int max_length;
	
	public SyntaxLibrary(){
		
		syntaxGroup = new HashMap<String,Color>();
		reserved = new HashMap<String,Color>();
		group = new HashMap<String,Color>();
		groupTerminator = new HashMap<String,String>();
		escape = '\\';
		
		max_length = 0;
	}
	
	
	public int maxLength(){
		return max_length;
	}
	
	public boolean isGroup(String in){
		return group.containsKey(in);
	}
	
	public boolean isReserved(String in){
		return reserved.containsKey(in);
	}
	
	public boolean exists(String in){
		
		return group.containsKey(in) || reserved.containsKey(in);
	}
	
	public char getEscape(){
		return escape;
	}
	
	
	public void inserted(){
		
		groupSet = group.keySet();		
	}
	
	public Set<String> getGroupList(){		
		return groupSet;
	}
	
	public void createGroup(String in, Color color){
		
		syntaxGroup.put(in, color);
	}
	

	
	public void addReserved(String in, String group){
		
		if(syntaxGroup.containsKey(group)){		
			reserved.put(in,syntaxGroup.get(group));	
			max_length = Math.max(in.length(), max_length);
		}
	}
	
	public void addGroup(String in, String terminator, String gr){
		
		if(syntaxGroup.containsKey(gr)){		
			group.put(in,syntaxGroup.get(gr));	
			groupTerminator.put(in, terminator);

			max_length = Math.max(in.length(), max_length);
			max_length = Math.max(terminator.length(), max_length);
			
		}
	}
	

	public Color getGroup(String in){
		return group.get(in);
	}
	
	public String getGroupTerminator(String in){
		return groupTerminator.get(in);
	}
	
	public Color getReserved(String in){
		return reserved.get(in);
	}
	
	
}
