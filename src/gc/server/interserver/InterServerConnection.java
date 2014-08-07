package gc.server.interserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InterServerConnection implements Runnable {

	private Socket connection;
	private InterServer server;
	private boolean running;


	private ObjectOutputStream output;
	private ObjectInputStream input;

	private String name;

	/**
	 * Full-duplex connection between to InterServers
	 * @param connection
	 * @param server
	 */
	public InterServerConnection(Socket connection, InterServer server){	

		this.name = "";
		this.connection = connection;
		this.server = server;

		try {
			
			output = new ObjectOutputStream(connection.getOutputStream());
			input = new ObjectInputStream(connection.getInputStream());

			retrieveServerName();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	private void retrieveServerName() throws ClassNotFoundException, IOException{
		
		InterServerPacket packet = (InterServerPacket)input.readObject();
		this.name = packet.getServerName();
	}
	
	
	public Socket getSocket(){
		return connection;
	}
	
	/**
	 * Send a packet
	 * @param packet
	 * @return
	 */
	public boolean send(InterServerPacket packet){

		try {
			output.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}



	@Override
	public void run() {


		while(running && connection.isConnected()){
			try {

				InterServerPacket packet = (InterServerPacket)input.readObject();
				InterServerPacket response = server.getResponse(packet);

				if(response != null){
					output.writeObject(response);	
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Close the connection
	 */
	public void close(){

		if(output != null){
			try {
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(input != null){
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	@Override
	public String toString(){
		return name;
	}

}
