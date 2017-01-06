package networkGraph;

/**
 * Structure of an Edge defined as: 
 * - a beginning Node (first extremity of the Edge),
 * - an end Node (second extremity of the Edge), 
 * - a weight, 
 * - a capacity (units are in Bps) 
 * 
 *@author DT
 */

public class Edge {

	/**
	 * Beginning extremity of the Edge
	 */
	private Node beg;
	/**
	 * End extremity of the Edge
	 */
	private Node end;
	/**
	 * Weight of the Edge
	 */
	private int weight;
	/**
	 * Capacity of the Edge
	 */
	private long capacity;
	/**
	 * Delay of the Edge
	 */
	private double delay;
	

	/**
	 * Constructor of an Edge with no argument
	 */
	public Edge(){
	}
	
	/**
	 * Constructor of an Edge taken two arguments. By default, weight and capacity equal 0. 
	 * @param beg - beginning node of the edge
	 * @param end - end node of the edge
	 */
	public Edge(Node beg, Node end){
		this.beg = beg;
		this.end = end;
		this.weight = 0;
		this.capacity = 0;
		this.delay = 0;
	}
	
	/**
	 * Constructor of an Edge taken four arguments. 
	 * @param beg - beginning node of the edge
	 * @param end - end node of the edge
	 * @param weight - weight of the edge
	 * @param capacity - capacity of the edge
	 * @param delay - delay of the edge
	 */
	public Edge(Node beg, Node end, int weight, int capacity, double delay){
		this.beg = beg;
		this.end = end;
		this.weight = weight;
		this.capacity = capacity;
		this.delay = delay;
	}


	
	/**
	 *Method that returns the begin node of the edge
	 *@return the beginning Node of the Edge
	 */
	public Node getBegNode(){
		return this.beg;
	}
	
	/**
	 *Returns the end node of the edge
	 *@return the end Node of the Edge
	 */
	public Node getEndNode(){
		return this.end;
	}
	
	/**
	 *Returns the weight of the edge
	 *@return the weight of the Edge
	 */
	public int getWeight(){
		return this.weight;
	}
	
	/**
	 *Returns the capacity of the edge
	 *@return the capacity of the Edge
	 */
	public long getCapacity(){
		return this.capacity;
	}
	
	/**
	 *Returns the delay of the edge
	 *@return the delay of the Edge
	 */
	public double getDelay(){
		return this.delay;
	}
	
	/**
	 *Set the begin node of the edge
	 *@param beg - beginning node of the edge
	 */
	public void setBegNode(Node beg){
		this.beg = beg;
	}
	
	/**
	 *Set the end node of the edge
	 *@param end - end node of the edge
	 */
	public void setEndNode(Node end){
		this.end = end;
	}
	
	/**
	 *Set the weight of the edge
	 *@param weight - weight of the edge
	 */
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	/**
	 *Set the capacity of the edge
	 *@param capacity - capacity of the edge
	 */
	public void setCapacity(long capacity){
		this.capacity = capacity;
	}
	
	/**
	 *Set the delay of the edge
	 *@param delay - delay of the edge
	 */
	public void setDelay(double delay){
		this.delay = delay;
	}
	
	/**
	 *Creates a String representation of the Edge.
	 * @return the Edge is represented as (ID_Beg, ID_End, weight, capacity)
	 */
	public String toString(){
		return "(" + this.beg.toString() + "," + this.end.toString() + "," + this.weight + "," + this.capacity + "," + this.delay + ")"; 
	}
	
	/**
	 *Get the name of an Edge as a String from begin node and end node
	 *@return the name of the Edge as (ID_Beg, ID_End)
	 */
	public String getEdgeName(){
		return "(" + this.beg.toString() + "," + this.end.toString() + ")";
	}
	
	/**
	 * Method that overrides the equals method of Object for the Object Edge.
	 * Two objects Edge are said to be equal if they have the same Begin and End extremities.
	 * @param obj Object  
	 * @return returns true if the two extremities of the two Edges are equal. 
	 */
	 public boolean equals(Object obj){
		 if(this == obj) {
			 return true;
		 }	
		 if (!(obj instanceof Edge)) {
			 return false; 
		 }
		 Edge edge = (Edge)obj;
         return this.beg.equals(edge.beg) && this.end.equals(edge.end);
	 }


}//end of class