package gc.server.http;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.reactor.IOReactorException;

import connectivity.httpserver.HttpServer;
import gc.server.com.MainFrame;
import gc.server.gui.GUIHandler;
import gc.server.util.CommandRouter;

/**
 * 
 * GCServer ServerHandler
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class ServerHandler {



	private int port;
	private int timeout;
	private int pool_size;
	private boolean override;
	private boolean enable_discovery;

	private HttpServer server;
	private ServerRequestHandler request_handler;
	private GCServerLogger server_logger;
	private CommandRouter command_router;
	private DiscoveryServer discovery;

	public ServerHandler(int port, int timeout, int pool_size, boolean override, boolean enable_discovery){
		super();

		this.port = port;
		this.timeout = timeout;
		this.pool_size = pool_size;
		this.override = override;
		this.enable_discovery = enable_discovery;

		setup();
	}

	private void setup(){
		
		try {

			discovery = new DiscoveryServer("port="+port+";");
			server_logger = new GCServerLogger();

			if(!override){
				request_handler = new ServerRequestHandler();
			}else{
				request_handler = null;
			}

			server = new HttpServer(port,request_handler,false,server_logger,null,null,timeout,pool_size);		

			buildRouter();


		} catch (IOReactorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}






	private void buildRouter(){


		CommandRouter func = null;
		command_router = new CommandRouter("server");
		command_router.setDescription("http server functions");

		// start server
		func = new CommandRouter("start");
		func.setDescription("Starts the server");
		func.setFunction(new Runnable(){

			@Override
			public void run() {

				if(server.isRunning()){
					MainFrame.error("Server is already running..\n");
					return;
				}

				server.startThreaded();
				startDiscovery();

				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if(server.isRunning()){
					MainFrame.print("HttpServer Started on Port["+server.getPort()+"]");
					GUIHandler.setTray_On();
				}else{
					MainFrame.error("Error starting HttpServer..");
					server.reset();
				}
			}

		});

		command_router.addRouter(func);

		// shut down server
		func = new CommandRouter("shutdown");
		func.setDescription("Shuts the server down");
		func.setFunction(new Runnable(){

			@Override
			public void run() {

				try {
					server.shutdown();		
					server.reset();
					stopDiscovery();

					MainFrame.print("HttpServer stopped");
					GUIHandler.setTray_Off();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		command_router.addRouter(func);

		/*
		// pause server
		func = new CommandRouter("pause");
		func.setDescription("Pauses the server");
		func.setFunction(new Runnable(){

			@Override
			public void run() {

				try {
					server.pause();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		command_router.addRouter(func);

		// shut down server
		func = new CommandRouter("resume");
		func.setDescription("Resumes the server");
		func.setFunction(new Runnable(){

			@Override
			public void run() {

				try {
					server.resume();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		command_router.addRouter(func);
		 */

		MainFrame.getCommandHandler().getRootRouter().addRouter(command_router);

	}


	public void shutdown(){
		try {
			server.shutdown();
			stopDiscovery();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public boolean isRunning(){
		return server.isRunning();
	}

	public void addHandler(HttpAsyncRequestHandler<HttpRequest> in, String uri){

		server.registerHandler(in, uri);		
	}

	public void addUniformHandler(HttpAsyncRequestHandler<HttpRequest> in){

		server.registerUniformHandler(in);		
	}


	public void startDiscovery(){

		if(enable_discovery){
			discovery.startServer();
		}
	}

	public void stopDiscovery(){

		if(enable_discovery){
			discovery.stopServer();
		}
	}



}
