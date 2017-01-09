package cacheManagement;

import java.util.ArrayList;


/**
 * This class represents a Cache object and is represented by:
 * an ID 
 * a capacity
 * the local requests rate
 * a boolean indicating whether it acts as a server or not
 *  
 *  
 *  
 * @author Daphne Tuncer
 *
 */

public class Cache implements Comparable<Cache>{
	
	/**
	 * ID of the cache 
	 */
	private int cacheID;
	/**
	 * Capacity of the cache
	 */
	private double capacity;
	/**
	 * Local request rate of the cache
	 */
	private double demand;
	/**
	 * Boolean indicating whether the cache is a server or not
	 */
	private boolean isServer;

	/**
	 * Constructor
	 */
	public Cache(){
	}
	/**
	 * Constructor
	 * @param cacheID ID of the cache
	 * @param isServer status of the cache
	 */
	public Cache(int cacheID, double capacity, boolean isServer){
		this.cacheID = cacheID;
		this.isServer = isServer;
		this.demand = 0;
		this.capacity = capacity;
	}
	
	/**
	 * Method to set the ID of the Cache 
	 * @param contentID ID of the Content
	 */
	public void setCacheID(int id){
		this.cacheID = id;
	}
	
	/**
	 * Method to set the status of the Cache 
	 * @param status Boolean indicating whether the cache is a server or not
	 */
	public void setCacheIStatus(boolean isServer){
		this.isServer = isServer;
	}
	
	/**
	 * Method to set the capacity of the Cache
	 * @param size Size of the Content
	 */
	public void setCapacity(double capacity){
		this.capacity = capacity;
	}
	
	/**
	 * Method to set the demand rate at the Cache
	 * @param demand Demand Rate at the Content
	 */
	public void setDemand(double demand){
		this.demand = demand;
	}
	
	/**
	 * Method to return the ID of the Cache
	 * @return returns the ID of the Cache
	 */
	public int getCacheID(){
		return this.cacheID;
	}
	
	/**
	 * Method to return the status of the Cache, i.e. is a server or not
	 * @return returns the status of the Cache
	 */
	public boolean getCacheStatus(){
		return this.isServer;
	}
	
	/**
	 * Method to return the capacity of the Cache
	 * @return returns the capacity of the Cache
	 */
	public double getCacheCapacity(){
		return this.capacity;
	}

	/**
	 * Method to return the demand rate at the Cache
	 * @return returns the demand rate at the Cache
	 */
	public double getCacheDemand(){
		return this.demand;
	}
	
	/**
	 * Override equals method 
	 * Two objects Cache are said to be equal if they have the same cacheID.
	 * @param obj Object  
	 * @return returns true if the ID of the two objects Cache are equal. 
	 */
	 public boolean equals(Object obj){
		 if(this == obj) {
	             return true;
			 }
		 if (!(obj instanceof Cache)) {
	             return false; 
			 }
		 Cache cache = (Cache)obj;
         return cacheID == cache.getCacheID();
	 }
	 
	   /**
	    * Method to determine whether the cache is in a list of Caches
	    * @param listCaches list of caches
	    * @return returns true if the cache is in the list
	    */
	   public boolean isInListCache(ArrayList<Cache> listCaches){
		   boolean isIn = false;
		   for (int i = 0; i < listCaches.size(); i++){
			   if(listCaches.get(i).getCacheID() == this.getCacheID()){
				   isIn = true;
				   break;
			   }
		   }
		   return isIn;
	   }//end isInListCache
	   
	   /**
	    * Override the toString method
	    */
	   public String toString () {
	        String output = new String();
	        output = "Cache " + this.cacheID;
	        return output;
	    }
	   
	   
	   /**
	    * Override the compareTo method
	    */
	   public int compareTo(Cache cacheToCompare) {
			double demandCacheToCompare = cacheToCompare.getCacheDemand();
			int diff = (int)(this.demand - demandCacheToCompare);
			return diff;
		}
	   
	   
	
	
}//end of class
