package gc.server.http;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.imageio.ImageIO;

import gc.server.com.MainFrame;

import org.apache.http.Header;
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
 * Request handler to be overriden by a external source
 * @author Leif Andreas Rudlang
 * @date 02.02.2016
 * Last updated: 15.02.2014
 *
 */
public abstract class DynamicRequestHandler extends AdvancedRequestHandler{


	private final String webroot;
	
	public DynamicRequestHandler(){
		super();

		webroot = MainFrame.getWebroot();
	}


	@Override
	public void onCONNECT(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void onDELETE(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void onGET(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void onHEAD(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void onOPTIONS(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void onPATCH(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void onPOST(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}	

	@Override
	public void onPUT(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void onTRACE(HttpRequest request, HttpResponse response, HttpContext context) {
		response.setStatusCode(HttpStatus.SC_NOT_IMPLEMENTED);
	}


	/**
	 * Sets a new cookie to the response
	 * @param response
	 */
	protected void setCookie(HttpResponse response){

		response.setHeader("Set-Cookie", CookieHandler.createCookie());  	
	}

	/**
	 * Returns a cookie from the request
	 * @param request
	 * @return
	 */
	protected String getCookie(HttpRequest request){

		Header header = request.getFirstHeader("Cookie");

		if (header != null) {
			return header.getValue();
		}

		return "";
	}

	/**
	 * Save a file local to webroot
	 * @param path
	 * @param content
	 */
	protected void saveFile(String path, byte[] content){

		try {
			Utility.writeFile(webroot+path, content);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Deletes a file local to webroot
	 * @param path
	 */
	protected boolean deleteFile(String path){
		
		File f = new File(webroot,path.replaceAll("'", ""));	
		
		if(f.canWrite() && f.isFile()){
			return f.delete();
		}
			
		return false;
	}
	
	
	protected void createThumbnail(int x, int y, byte[] data, String path){
		
			
		try {
			
			BufferedImage img = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
			InputStream input = new ByteArrayInputStream(data);	
			img.createGraphics().drawImage(ImageIO.read(input).getScaledInstance(x, y, Image.SCALE_SMOOTH),0,0,null);
			ImageIO.write(img, "jpg", new File(webroot,path));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * Deletes a directory local to webroot
	 * @param path
	 * @return
	 */
	protected boolean deleteDirectory(String path){
		
		
		File f = new File(webroot,path);	
		
		if(f.canWrite() && f.isDirectory()){
			return f.delete();
		}
		
		return false;
	}
	
	/**
	 * Create a directory local to webroot
	 * @param path
	 * @return
	 */
	protected boolean createDirectory(String path){
		
		File f = new File(webroot,path);
				
		if(!f.exists()){
		 return	f.mkdirs();
		}
		
		return false;
	}
	
	
	/**
	 * Returns true if the current session is valid
	 * @param request
	 * @return
	 */
	protected boolean sessionValid(HttpRequest request){
		
		String cookie = getCookie(request);
		
		if(cookie!=null && !cookie.isEmpty()){
			
			return CookieHandler.isValid(cookie);
		}
		
		return false;
	}
	
	
	/**
	 * Return the GET Target URI as a String
	 * @param request
	 * @return
	 */
	protected String getTarget(HttpRequest request){
		
		String target = request.getRequestLine().getUri();
		
		try {
			return URLDecoder.decode(target, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	/**
	 * Serve a file from webroot to the client
	 * @param request
	 * @param response
	 */
	protected void serveFile(HttpRequest request, HttpResponse response){

		String target = request.getRequestLine().getUri();
		File file = null;
		try {
			file = new File(this.webroot, URLDecoder.decode(target, "UTF-8"));
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



	
	

}
