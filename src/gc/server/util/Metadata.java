package gc.server.util;

import gc.server.com.MainFrame;

import java.io.File;


/**
 * Class handles generic Image MetaData
 * @author Leif Andreas Rudlang
 * @date 25.03.2014
 */
public class Metadata {

	private File file;
	private String path;


	private boolean valid;
	private MetadataHandler handler;


	public Metadata(String path){

		this.path = path;
		valid = false;

		setup();
	}

	private void setup(){

		file = new File(path);

		if(!file.exists() || !file.isFile()){
			valid = false;
			return;
		}

		String suffix = path.toLowerCase();

		if(suffix.endsWith(".jpg") || suffix.endsWith(".jpeg") || suffix.endsWith(".tif")){
			handler = new ExifHandler(path);
		}else{
			handler = new XmpHandler(path);
		}

		valid = true;
	}

	/**
	 * Updates a tag if it exists, creates a new tag if it does not.
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean updateTag(String key, String value){

		if(!valid){
			return false;
		}

		return handler.updateTag(key, value);
	}


	/**
	 * Removes a tag
	 * @param key
	 * @return
	 */
	public boolean removeTag(String key){

		if(!valid){
			return false;
		}

		return handler.removeTag(key);
	}


	/**
	 * Writes the MetaData back to the image
	 */
	public void write(){

		if(!valid){
			return;
		}

		handler.write();
	}

	@Override
	public String toString(){

		if(!valid){
			return "'Invalid Format'";
		}

		return handler.toString();
	}

	public String getSearchData(){

		if(!valid){
			return "";
		}

		return handler.getSearchData();
	}

	/**
	 * Imports data from a csv
	 * @param csv
	 */
	public void importData(String csv){

		if(!valid){
			return;
		}
		
		try{

			String[][] table = Util.toTable(csv);


			for(int row = 0; row < table.length; row++){

				String key = table[row][0];
				String value = table[row][1];

				if(key == null || value == null){
					continue;
				}

				this.updateTag(key, value);

			}
		}catch(Exception e){
			MainFrame.error(e.getMessage());
		}
	}


	/*
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub


		String path = "c://bike.jpg";

		Metadata meta = new Metadata(path);	
		meta.updateTag("Rating", "4");

		//System.out.println(meta.updateTag("Rating", "4"));
		//meta.write();
		System.out.println(meta);
		//System.out.println(AllTagConstants.EXIF_TAG_RATING);
	}
	 */
}
