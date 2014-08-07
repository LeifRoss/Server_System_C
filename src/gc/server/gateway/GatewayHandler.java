package gc.server.gateway;

import gc.server.com.MainFrame;
import gc.server.http.PostHandler;
import gc.server.interserver.InterServerPacket;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;

import connectivity.httpserver.AdvancedRequestHandler;

public class GatewayHandler extends AdvancedRequestHandler {

	private static final String HEADER_FUNCTION = "function";
	private static final String HEADER_USERNAME = "username", HEADER_PASSWORD = "password";

	private static final String FUNCTION_LOGIN = "login";
	private static final String FUNCTION_REGISTER = "register";


	private NStringEntity success, username_exists, incomplete_request, unknown_error, invalid_username_password;


	private GatewayServer server;

	public GatewayHandler(GatewayServer server){
		super();

		this.server = server;

		success = new NStringEntity("1", ContentType.create("text/plain", "UTF-8"));
		username_exists = new NStringEntity("2", ContentType.create("text/plain", "UTF-8"));
		incomplete_request = new NStringEntity("3", ContentType.create("text/plain", "UTF-8"));
		unknown_error = new NStringEntity("4", ContentType.create("text/plain", "UTF-8"));
		invalid_username_password = new NStringEntity("5", ContentType.create("text/plain", "UTF-8"));

	}


	@Override
	public void onPOST(HttpRequest request, HttpResponse response, HttpContext context) {

		PostHandler post = new PostHandler(request);

		if(!post.isset(HEADER_FUNCTION)){
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			return;
		}
		
		response.setStatusCode(HttpStatus.SC_OK);
		MainFrame.print(post.post(HEADER_FUNCTION));
		
		switch(post.post(HEADER_FUNCTION)){

		case FUNCTION_LOGIN:
			handleLogin(post, response);
			break;		

		case FUNCTION_REGISTER:
			handleRegister(post, response);
			break;

		}


	}


	private void handleLogin(PostHandler post, HttpResponse response){

		if(!post.isset(HEADER_PASSWORD) || !post.isset(HEADER_USERNAME)){
			response.setEntity(incomplete_request);
			return;
		}

		String username = post.post(HEADER_USERNAME);
		String password = post.post(HEADER_PASSWORD);


		boolean result = server.getDatabase().checkCredentials(username, password);
		

		if(result){

			String ticket = server.generateTicket(username);
			
			String address = server.getAddress(username);
			
			NStringEntity entity = new NStringEntity("1,"+ticket+","+address, ContentType.create("text/plain", "UTF-8"));
			response.setEntity(entity);
			
			
			// send packet with ticket to gameserver
			
			InterServerPacket packet = new InterServerPacket("");
			server.getInterServer().send("gameserver", packet);	
			
			

		}else{

			response.setEntity(invalid_username_password);
		}

	}


	private void handleRegister(PostHandler post, HttpResponse response){

		if(!post.isset(HEADER_PASSWORD) || !post.isset(HEADER_USERNAME)){
			response.setEntity(incomplete_request);
			return;
		}

		String username = post.post(HEADER_USERNAME);
		String password = post.post(HEADER_PASSWORD);


		if(server.getDatabase().usernameExists(username)){
			response.setEntity(username_exists);
			return;
		}

		if(server.getDatabase().registerUser(username, password)){
			// success
			response.setEntity(success);
		}else{
			// unknown error	
			response.setEntity(unknown_error);
		}

	}


	@Override
	public void onCONNECT(HttpRequest request, HttpResponse response, HttpContext context) {}

	@Override
	public void onDELETE(HttpRequest request, HttpResponse response, HttpContext context) {}

	@Override
	public void onGET(HttpRequest request, HttpResponse response, HttpContext context) {
		
		response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		response.setEntity(new NStringEntity("Invalid Request", ContentType.create("text/plain", "UTF-8")));
	}

	@Override
	public void onHEAD(HttpRequest request, HttpResponse response, HttpContext context) {}

	@Override
	public void onOPTIONS(HttpRequest request, HttpResponse response, HttpContext context) {}

	@Override
	public void onPATCH(HttpRequest request, HttpResponse response, HttpContext context) {}

	@Override
	public void onPUT(HttpRequest request, HttpResponse response, HttpContext context) {}

	@Override
	public void onTRACE(HttpRequest request, HttpResponse response, HttpContext context) {}



}
