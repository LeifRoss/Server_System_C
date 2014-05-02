package gc.server.util;

public interface MetadataHandler {

	/**
	 * Updates a tag if it exists, creates the tag if it does not.
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean updateTag(String key, String value);
	
	/**
	 * Removes a tag
	 * @param key
	 * @return
	 */
	public boolean removeTag(String key);
	
	/**
	 * Writes the MetaData back to the Image
	 */
	public void write();
	
	/**
	 * Returns the searchable data
	 * @return
	 */
	public String getSearchData();	
}
