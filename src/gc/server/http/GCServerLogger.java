package gc.server.http;


import gc.server.com.MainFrame;

import org.apache.http.nio.NHttpServerConnection;

import connectivity.httpserver.ServerLogger;

/**
 * 
 * GCServer GCServerLogger
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GCServerLogger implements ServerLogger {

	@Override
	public void logConnected(NHttpServerConnection c) {
			MainFrame.execute("log print \"Connected: "+c.toString()+"\"");
	}

	@Override
	public void logDisconnected(NHttpServerConnection c) {
		MainFrame.execute("log print \"Disconnected: "+c.toString()+"\"");
	}

}
