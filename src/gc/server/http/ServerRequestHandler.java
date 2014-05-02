package gc.server.http;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import gc.server.util.Util;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;

import connectivity.httpserver.AdvancedRequestHandler;
import connectivity.utility.Utility;


/**
 * 
 * GCServer ServerRequestHandler
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class ServerRequestHandler extends AdvancedRequestHandler {



	private final String web_root;


	public ServerRequestHandler(){
		super();

		web_root = Util.getAssetsLocation()+"webroot//";		
	}




	@Override
	public void onCONNECT(HttpRequest request, HttpResponse response, HttpContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDELETE(HttpRequest request, HttpResponse response, HttpContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGET(HttpRequest request, HttpResponse response, HttpContext context) {

		String target = request.getRequestLine().getUri();
		File file = null;
		try {
			file = new File(this.web_root, URLDecoder.decode(target, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			// error		
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			NStringEntity entity = new NStringEntity( "<html><body><h1>Error</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);

			return;
		}


		if (!file.exists()) {

			// file does not exist
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			NStringEntity entity = new NStringEntity( "<html><body><h1>File" + file.getPath() + " not found</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);

		} else if (!file.canRead() || file.isDirectory()) {

			// access to file forbidden
			response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			NStringEntity entity = new NStringEntity( "<html><body><h1>Access denied</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			response.setEntity(entity);

		} else {

			// serve the file
			response.setStatusCode(HttpStatus.SC_OK);
			NFileEntity body = new NFileEntity(file);
			response.setEntity(body);

		}

	}

	@Override
	public void onHEAD(HttpRequest request, HttpResponse response, HttpContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOPTIONS(HttpRequest request, HttpResponse response, HttpContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPATCH(HttpRequest request, HttpResponse response, HttpContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPOST(HttpRequest request, HttpResponse response, HttpContext context) {



		PostHandler handler = new PostHandler(request);

		if(handler.hasFile()){

			try {
				Utility.writeFile(web_root+"images//"+handler.getFilename(), handler.getFileContent());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		
		response.setStatusCode(handler.getStatus());
		response.setEntity(new NStringEntity("<img src=\""+handler.getFilename()+"\" alt=\"Smiley face\"> ", ContentType.create("text/html", "UTF-8")));		
		
		handler.clear();
}	




	@Override
	public void onPUT(HttpRequest request, HttpResponse response, HttpContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTRACE(HttpRequest request, HttpResponse response, HttpContext context) {
		// TODO Auto-generated method stub

	}

}
