package gc.server.gateway;

import gc.server.interserver.InterServerHandler;
import gc.server.interserver.InterServerPacket;

public class GatewayInterHandler implements InterServerHandler {

	
	private GatewayServer server;
	
	public GatewayInterHandler(GatewayServer server){		
		this.server = server;
	}
	
	
	
	@Override
	public InterServerPacket handle(InterServerPacket packet) {
		
		return null;
	}

	
	
	
}
