package networkGraph;

import java.util.ArrayList;


/**
 * Structure of a Node defined as 
 * - an ID 
 * - a list of adjacent nodes
 * 
 * @author DT
 */

public class Node {

	/**
	 * A Node is defined by an ID of type Integer
	 */
	private int id;
	
	/**
	 * A Node has a list of adjacent (or neighbours) nodes. 
	 */
	private ArrayList<Node> adjacencyNodes;

	/**
	 * Constructor of a Node. By default, the list of adjacent nodes is empty
	 * @param id - an Integer that will be the ID of the node
	 */

	public Node (int id) {
		  this.id= id;
		  this.adjacencyNodes = new ArrayList<Node>();
		 	  }
	/**
	 * Constructor of Node
	 */
	public Node() { 
	}

	/**
	 * Method to set the ID of the Node as an integer
	 * @param id - the ID of the node
	 */
	public void setNodeId(int id) {
		this.id = id;
		}
	
	/**
	 * Method to get the ID of the Node
	 * @return returns the id of the Node
	 */
	public int getNodeId() {
		return this.id;
		}
	
	/**
	 * Method to add a Node in the list of adjacent nodes (neighbours) of the node
	 * @param n - the node to add to the list of neighbours
	 */
	public void addAdjacentNode(Node n) {
		this.adjacencyNodes.add(n);
	}
	
	/**
	 * Method to remove a neighbour node/adjacent node from the list of adjacent nodes
	 * @param n Node to remove from the list of neighbours
	 */
	public void removeAdjacentNode(Node n){
		for (int i = 0; i < this.getListNodes().size(); i++){
			if(this.getListNodes().get(i).equals(n)){
				this.getListNodes().remove(i);
			}
		}
	}
	
	/**
	 * Method to get the list of adjacent nodes of the node 
	 * @return returns the adjacency list of neighbours nodes as an ArrayList 
	 */
	public ArrayList<Node> getListNodes(){
		return this.adjacencyNodes;
	}
	
	/**
	 * Method to check if a Node node is in the adjacency list
	 * @param node Node to consider
	 * @return returns true if Node node is in the adjency list
	 */
	public boolean checkIsAdjacentNode(Node node){
		boolean isAdjacentNode = false;
		for (int i = 0; i < this.adjacencyNodes.size(); i++){
			if (node.getNodeId() == this.adjacencyNodes.get(i).getNodeId()){
				isAdjacentNode = true;
				i = this.adjacencyNodes.size() + 1;
			}
		}
		return isAdjacentNode;
	}
	
	/**
	 * Method to display the list of the neighbours/adjacent nodes of the node
	 */
	public void displayAdjacentNodes(){
		for (int i=0;i<this.adjacencyNodes.size();i++){
			System.out.println(this.adjacencyNodes.get(i).toString());
		}
	}
	
	/**
	 * Method that overrides the equals method of Object for the Object Node.
	 * Two objects Node are said to be equal if they have the same ID.
	 * @param obj Object  
	 * @return returns true if the two extremities of the two Nodes are equal. 
	 */
	 public boolean equals(Object obj){
		 if(this == obj) {
			 return true;
		 }
		 if (!(obj instanceof Node)) {
			 return false; 
		 }
		 Node node = (Node)obj;
         return this.id == node.id;
	 }
		
	/**
	 * Creates a String representation of the Node ID.
	 * @return the name of the Node as String R plus String ID
	 */
	public String toString(){
		return "R"+this.getNodeId();
	}
	
}//end of class



