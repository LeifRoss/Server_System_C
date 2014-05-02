package gc.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.AllTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAsciiOrByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAsciiOrRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByteOrShort;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDouble;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoLong;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShortOrLong;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import connectivity.utility.Utility;

/**
 * Class handles EXIF MetaData
 * @author Leif Andreas Rudlang
 * @date 25.03.2014
 */
public class ExifHandler implements MetadataHandler{

	private File file;
	private String path;
	private TiffImageMetadata exif;
	private TiffOutputSet outputSet;
	private TiffOutputDirectory exifDir;
	private boolean valid;
	private boolean loop;

	public ExifHandler(String path){

		this.path = path;

		setup();
	}

	private void setup(){

		buildLibrary();
		file = new File(path);

		try {
			IImageMetadata metadata_generic = Imaging.getMetadata(file);


			if (metadata_generic instanceof JpegImageMetadata) {
				exif = ((JpegImageMetadata)metadata_generic).getExif();
				
			} else if (metadata_generic instanceof TiffImageMetadata) {
				exif = (TiffImageMetadata)metadata_generic;
			}		

			valid = (exif != null);

			if(valid){
				outputSet = exif.getOutputSet();
				exifDir = outputSet.findDirectory(TiffDirectoryConstants.DIRECTORY_TYPE_ROOT);
			}else{
				createMetadata();
			}

		} catch (ImageReadException | IOException | ImageWriteException e) {
			e.printStackTrace();
		}


	}


	private void createMetadata(){

		if(loop){
			valid = false;
			return;
		}

		loop = true;
		outputSet = new TiffOutputSet();

		try {

			exifDir = outputSet.getOrCreateRootDirectory();
			valid = true;

			write();
			setup();

		} catch (ImageWriteException e) {
			e.printStackTrace();
		}
	}

	public void sort(){

		exifDir.sortFields();	
	}

	@Override
	public boolean updateTag(String key, String value){

		if(!valid){
			return false;
		}


		try {

			if(library.containsKey(key)){

				TagInfo info = library.get(key);

				exifDir.removeField(info);

				if(info instanceof TagInfoAscii){
					exifDir.add((TagInfoAscii)info, value);	
				}else if(info instanceof TagInfoAsciiOrByte){
					exifDir.add((TagInfoAsciiOrByte)info, value);		
				}else if(info instanceof TagInfoShort){
					exifDir.add((TagInfoShort)info, Short.parseShort(value));		
				}else if(info instanceof TagInfoLong){
					exifDir.add((TagInfoLong)info, Integer.parseInt(value));		
				}else if(info instanceof TagInfoByte){
					exifDir.add((TagInfoByte)info, value.getBytes());		
				}else if(info instanceof TagInfoShortOrLong){
					exifDir.add((TagInfoShortOrLong)info, Short.parseShort(value));		
				}else if(info instanceof TagInfoByteOrShort){
					exifDir.add((TagInfoByteOrShort)info, value.getBytes());		
				}else if(info instanceof TagInfoRational){
					exifDir.add((TagInfoRational)info, parseRational(value));		
				}else if(info instanceof TagInfoDouble){
					exifDir.add((TagInfoDouble)info, Double.parseDouble(value));		
				}else if(info instanceof TagInfoAsciiOrRational){
					exifDir.add((TagInfoAsciiOrRational)info, value);		
				}else{
					return false;
				}
			}

		} catch (ImageWriteException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean removeTag(String key){

		if(library.containsKey(key)){

			TagInfo info = library.get(key);
			exifDir.removeField(info);	
			return true;
		}

		return false;
	}


	@Override
	public void write(){

		if(!valid){
			return;
		}

		FileOutputStream output = null;

		try {

			ExifRewriter rewriter = new ExifRewriter();		

			byte[] data = Utility.readBytes(path);
			output = new FileOutputStream(file);
			rewriter.updateExifMetadataLossy(data, output, outputSet);


		}catch(Exception e) {
			e.printStackTrace();

		}finally {

			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}


	public void setGPS(double x, double y){

		if(!valid){
			return;
		}

		try {
			outputSet.setGPSInDegrees(x, y);
		} catch (ImageWriteException e) {
			e.printStackTrace();
		}
	}


	private RationalNumber parseRational(String value){

		if(value.contains("/")){

			int divisor = Integer.parseInt( value.substring(0,value.indexOf('/')) );
			int numerator = Integer.parseInt( value.substring(value.indexOf('/')+1,value.length()));

			return new RationalNumber( divisor, numerator);
		}else{
			
			return new RationalNumber(1,1);
		}

	}

	@Override
	public String toString(){


		if(!valid){
			return "'invalid format'";
		}	

		StringBuilder str = new StringBuilder();

		List<TiffField> fields = exif.getAllFields();

		for(TiffField f : fields){

			String name = f.getTagName().replace("'", "");
			String value = f.getValueDescription().replace("'", "");

			str.append("'"+name+"','"+value+"'\n");					
		}

		return str.toString();
	}


	
	
	private static HashMap<String,TagInfo> library;

	private static void buildLibrary(){

		if(library != null){
			return;
		}

		library = new HashMap<String,TagInfo>();

		List<TagInfo> list = AllTagConstants.ALL_TAGS;

		for(TagInfo info : list){
			library.put(info.name, info);
		}

	}

	@Override
	public String getSearchData() {
		
		if(!valid){
			return "";
		}	

		StringBuilder str = new StringBuilder();

		List<TiffField> fields = exif.getAllFields();

		for(TiffField f : fields){

			String value = f.getValueDescription().replace("'", "").toLowerCase();

			str.append(value + " ");					
		}

		return str.toString();
	}





}
