package cacheManagement;


/**
 * This class represents a Content object and is represented by:  
 * an ID
 * a size
 * 
 *@author Daphne Tuncer
 */

public class Content implements Comparable<Content>{

	/**
	 * ID of the content 
	 */
	private int contentID;
	/**
	 * Size of the content
	 */
	private double size;

	/**
	 * Constructor
	 */
	public Content(){
	}
	
	/**
	 * Constructor
	 * @param contentID ID of the content
	 * @param size size of the content
	 */
	public Content(int contentID, double size){
		this.contentID = contentID;
		this.size = size;
	}
	
	/**
	 * Method to set the ID of the Content 
	 * @param contentID ID of the Content
	 */
	public void setContentID(int id){
		this.contentID = id;
	}
	
	/**
	 * Method to set the size of the Content
	 * @param size Size of the Content
	 */
	public void setSize(double size){
		this.size = size;
	}
	
	/**
	 * Method to return the ID of the Content
	 * @return returns the ID of the Content
	 */
	public int getContentID(){
		return this.contentID;
	}
	
	/**
	 * Method to return the size of the Content
	 * @return returns the size of the Content
	 */
	public double getContentSize(){
		return this.size;
	}
	
	/**
	 * Override equals method
	 * Two objects Content are said to be equal if they have the same contentID.
	 * @param obj Object  
	 * @return returns true if the ID of the two objects Content are equal. 
	 */
	 public boolean equals(Object obj){
		 if(this == obj) {
	             return true;
		 }
		 if (!(obj instanceof Content)) {
	             return false; 
		 }
		 Content content = (Content)obj;
         return contentID == content.getContentID();
	 }
	 
	 /**
	  * Override the compareTo method
	  */
	 public int compareTo(Content contentToCompare) {
		 double sizeContentToCompare = contentToCompare.getContentSize();
		 int diff = (int)(this.size - sizeContentToCompare);
		 return diff;
	 }

		
		
}//end of class
