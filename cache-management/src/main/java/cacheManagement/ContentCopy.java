package cacheManagement;

/**
 * This class represents an Object ContentCopy. It is a 2-tuple of Content and demand. It represents the copy
 * of a content item which serves demand request. 
 * 
 * @author Daphne Tuncer
 *
 */

public class ContentCopy implements Comparable<ContentCopy>{

	
	private Content content;
	private double demand;
	
	/**
	 * Constructor
	 */
	public ContentCopy(){
	}
	/**
	 * Constructor
	 */	
	public ContentCopy(Content content, double demand){
		this.content = content;
		this.demand = demand;
	}
	
	/**
	 * Setters
	 */
	public void setContent(Content content){
		this.content = content;
	}
	public void setDemand(double demand){
		this.demand = demand;
	}
	/**
	 * Getters
	 */	
	public Content getContent(){
		return this.content;
	}
	public double getDemand(){
		return this.demand;
	}
	
	/**
	 * Override the compareTo method
	 */
	public int compareTo(ContentCopy contentCopyToCompare) {
		double demandContentCopyToCompare = contentCopyToCompare.getDemand();
		if (demandContentCopyToCompare < this.demand)
			  return -1;
			else if (demandContentCopyToCompare > this.demand)
			  return 1;
			else
			  return 0;
	}
	
	
	
}
