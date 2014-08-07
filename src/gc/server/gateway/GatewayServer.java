package gc.server.gateway;

import java.util.Random;

import connectivity.utility.Crypter;
import gc.server.com.MainFrame;
import gc.server.interserver.InterServer;

public class GatewayServer {

	
	private static final String URI_GATEWAY = "/gateway";
	
	private GatewayHandler handler;
	private GatewayDatabase database;
	private Random random;
	
	private InterServer interServer;
	
	public GatewayServer(){
		
		setup();	
	}
	
	
	private void setup(){
		
		random = new Random();
		
		handler = new GatewayHandler(this);
		database = new GatewayDatabase();
				
		interServer = new InterServer(new GatewayInterHandler(this),"gateway");
		
		// establish connection to the other servers
		
		
		
		MainFrame.addHandler(handler, URI_GATEWAY);
	}
	
	
	public void connect(String address, int port){
		
		interServer.connect(address, port);
	}
	
	
	
	public void startServer(){
		
		interServer.startServer();
	}
	
	public void stopServer(){
		
		interServer.stopServer();
	}
	
	
	public InterServer getInterServer(){
		return interServer;	
	}
	
	
	public GatewayDatabase getDatabase(){
		return database;
	}
	
	
	/**
	 * Generate a ticket
	 * @param data
	 * @return
	 */
	public String generateTicket(String data){					
		return Crypter.hash(data+random.nextInt(), Crypter.SHA512);
	}
	
	
	/**
	 * Return next server address
	 * @param username
	 * @return
	 */
	public String getAddress(String username){
		
		return interServer.getAddressByName("gameserver");
	}
	
	
}
