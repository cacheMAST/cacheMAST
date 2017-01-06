package cacheManagement;

import java.util.ArrayList;


/**
 * This class represents a Server object. The Server object is a structure that maintains the list of all contents available in the 
 * caching infrastructure. The ID of the cache associated to the server is equal to the MAXIMUM value. 
 * 
 * @author Daphne Tuncer
 *
 */

public class Server {
	
	/**
	 * A Server object is a Cache
	 */
	private Cache cache;
	/**
	 * A Server object maintains a list of available contents in the network. 
	 */
	private ArrayList<Content> listContent;
	
	/**
	 * Constructor - default capacity is infinite
	 */
	public Server(){
		this.cache = new Cache(Integer.MAX_VALUE, Double.MAX_VALUE, true);
		this.listContent = new ArrayList<Content>();
	}
	
	/**
	 * Constructor - input capacity
	 * @param capacity Capacity of the server
	 */
	public Server(double capacity){
		this.cache = new Cache(Integer.MAX_VALUE, capacity, true);
		this.listContent = new ArrayList<Content>();
	}
	
	/**
	 * Method to set the cache the Server 
	 * @param cache Cache to set
	 */
	public void setCache(Cache cache){
		this.cache = cache;
	}
	
	/**
	 * Method to set the list of contents in the server
	 * @param listContents list of contents to set
	 */
	public void setListContents(ArrayList<Content> listContent){
		this.listContent = listContent;
	}
	
	/**
	 * Method to return the cache of the Server
	 * @return returns the cache of the server
	 */
	public Cache getCache(){
		return this.cache;
	}
	
	/**
	 * Method to return the list of contents
	 * @return returns the list of the Content
	 */
	public ArrayList<Content> getListContent(){
		return this.listContent;
	}
	
	/**
	 * Method to display the list of contents available from the server
	 */
	public void displayListContents(){
		for (int i = 0; i < this.listContent.size(); i++){
			System.out.print("Content " + this.listContent.get(i).getContentID() + "; ");
		}
		System.out.println();
	}
	
	/**
	 * Method to display the number of contents in the server
	 */
	public void displayNbContents(){
			System.out.println("Number of contents in the server " + this.getListContent().size());
	}

}//end of class

	