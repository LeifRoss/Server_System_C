package gc.server.interserver;

public class InterServerPacket {

	private String serverName;
	
	
	public InterServerPacket(String name){
		
		this.serverName = name;
	}
	
	public String getServerName(){
		return serverName;
	}
	
}
