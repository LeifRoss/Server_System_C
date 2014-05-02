package gc.server.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import connectivity.utility.Utility;

/**
 * Class handles XMP MetaData
 * @author Leif Andreas Rudlang
 * @date 25.03.2014
 */
public class XmpHandler implements MetadataHandler{

	private static final String XMP_ROOT = "tEXt";
	private static final String XMP_ROOT_ENTRY = "tEXtEntry";
	private static final String XMP_KEYWORD = "keyword";
	private static final String XMP_VALUE = "value";
	
	private String path;
	private File file;
	private boolean valid;
	private String selected_format;

	private Element root;
	private IIOMetadataNode xmp;

	private IIOMetadata data;


	public XmpHandler(String path){
		this.path = path;
		
		setup();
	}

	private void setup(){

		this.file = new File(path);

		ImageInputStream input = null;
		try {
			input = ImageIO.createImageInputStream(file);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

			while(readers.hasNext()){

				ImageReader reader = readers.next();
				reader.setInput(input, true);

				this.data = reader.getImageMetadata(0);

				if(data!=null){
					valid = true;
					findXMPnode();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(input != null){
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void findXMPnode(){

		
		for(String names : data.getMetadataFormatNames()){

			root = (Element)data.getAsTree(names);			
			NodeList list = root.getElementsByTagName(XMP_ROOT);


			if(list.getLength() > 0){

				xmp = (IIOMetadataNode)list.item(0);
			}else{

				xmp = new IIOMetadataNode(XMP_ROOT);
				root.appendChild(xmp);
			}			

			if(xmp != null){
				selected_format = names;
				break;
			}		
		}	

	}


	@Override
	public boolean updateTag(String key, String value) {

		if(!valid){
			return false;
		}
		
		NodeList list = xmp.getChildNodes();
		IIOMetadataNode select = null;
		
		for(int i = 0; i < list.getLength(); i++){
			
			IIOMetadataNode n = (IIOMetadataNode)list.item(i);
			
			String keyword = n.getAttribute(XMP_KEYWORD);
			
			if(keyword != null && keyword.equals(key)){
				select = n;
				break;
			}	
		}
		
		
		if(select == null){
			
		    select = new IIOMetadataNode(XMP_ROOT_ENTRY);
		    select.setAttribute(XMP_KEYWORD, key);
		    xmp.appendChild(select);
		}
		
		select.setAttribute(XMP_VALUE, value);
		return true;
	}

	@Override
	public boolean removeTag(String key) {

		if(!valid){
			return false;
		}
		
		NodeList list = xmp.getChildNodes();

		
		for(int i = 0; i < list.getLength(); i++){
			
			IIOMetadataNode n = (IIOMetadataNode)list.item(i);
			
			String keyword = n.getAttribute(XMP_KEYWORD);
			
			if(keyword != null && keyword.equals(key)){			
				xmp.removeChild(n);				
				return true;
			}	
		}
		
		
		
		return false;
	}

	@Override
	public void write() {

		if(!valid){
			return;
		}
		
		String suffix = getSuffix(file.getName());

		if(suffix == null || suffix.isEmpty() || data.isReadOnly()){
			return;
		}

		ByteArrayOutputStream bao = null;
		ImageOutputStream output = null;

		try{

			data.setFromTree(selected_format, root);

			BufferedImage img = ImageIO.read(file);

			Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(suffix);

			bao = new ByteArrayOutputStream(4096);
			output = ImageIO.createImageOutputStream(bao);

			while(writers.hasNext()){

				ImageWriter writer = writers.next();

				writer.setOutput(output);

				ImageWriteParam writeParam = writer.getDefaultWriteParam();
				writer.write(data, new IIOImage(img, null, data), writeParam);
			}

			Utility.writeFile(file.getPath(), bao.toByteArray());

		}catch(Exception e){
			e.printStackTrace();
		}finally{

			try {
				if(output != null){
					output.close();
				}
				if(bao != null){
					bao.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}


	}

	@Override
	public String toString(){
		
		if(!valid){
			return "'Invalid Format'";
		}
		
		StringBuilder str = new StringBuilder();
		
		NodeList list = xmp.getChildNodes();
		
		
		for(int i = 0; i < list.getLength(); i++){
			
			IIOMetadataNode n = (IIOMetadataNode)list.item(i);
		
			String name = n.getAttribute(XMP_KEYWORD);
			String value = n.getAttribute(XMP_VALUE);
			
			str.append("'"+name+"','"+value+"'\n");		
		}		
		
		return str.toString();
	}


	private String getSuffix(String in){

		int idx0 = in.lastIndexOf(".")+1;
		int idx1 = in.length();

		if(idx0 > 0 && idx1 > idx0){

			return in.substring(idx0);		
		}			

		return null;
	}

	@Override
	public String getSearchData() {
		
		if(!valid){
			return "";
		}
		
		StringBuilder str = new StringBuilder();
		
		NodeList list = xmp.getChildNodes();
		
		
		for(int i = 0; i < list.getLength(); i++){
			
			IIOMetadataNode n = (IIOMetadataNode)list.item(i);
		
			String value = n.getAttribute(XMP_VALUE).toLowerCase();
			
			str.append(value + " ");		
		}		
		
		return str.toString();
	}

}
