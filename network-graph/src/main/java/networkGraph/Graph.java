package networkGraph;

import java.util.ArrayList;

/**
 * Structure of a Graph defined as 
 * - a set of Nodes 
 * - a set of Edges
 * - a graph identifier
 * 
 * @author DT
 */

public class Graph {

	/**
	 * A Graph has an ID of type String
	 */
	private String graphID;
	/**
	 * A Graph contains a set of Nodes
	 */
	private ArrayList<Node> setNodes;
	/**
	 * A Graph contains a set of Edges
	 */
	private ArrayList<Edge> setEdges;
	
	
	/**
	 * Constructor of an empty Graph. 
	 * The set of Nodes and the set of Edges are empty. 
	 */
	public Graph(){
		this.setNodes = new ArrayList<Node>();
		this.setEdges = new ArrayList<Edge>();
	}
	
	/**
	 * Method to set a list of nodes of the Graph 
	 * @param listNodes list of nodes to set in the Graph
	 */
	public void setSetNodes(ArrayList<Node> listNodes){
		this.setNodes = listNodes;
	}
	
	/**
	 * Method to add a node in the Graph, with an ID depending on the current number of nodes in the graph. 
	 * The ID of the Node to insert is equal to (SetNodes.size()+1). 
	 */
	public void addNodeInGraph(){
		this.setNodes.add(new Node(this.setNodes.size()+1));
	}

	/**
	 * Method to insert a node in the set of nodes of the Graph
	 * @param n Node to insert to the list of nodes of the graph
	 */
	public void insertNodeInGraph(Node n){
		this.setNodes.add(n);
	}
	
	/**
	 * Method to create a list of nodes given an expected number of nodes. 
	 * Each created Node has an ID equals to an Integer. All IDs are unique.  
	 * @param nbDesiredNodes Number of nodes expected in the graph
	 * @return returns the list of created Nodes
	 */
	public ArrayList<Node> createNewNodes(int nbDesiredNodes){
		ArrayList<Node> nodes = new ArrayList<Node>(); 
		for (int i = 1 ; i <= nbDesiredNodes ; i++){
			Node n = new Node(i);
			nodes.add(n);
		}
		return nodes;
	}
	
	/**
	 * Method to get the ID of a Node node in the Graph.
	 * @param node Node to consider
	 * @return returns the ID of the targeted node
	 */
	public int findNodeID(Node n){
		int target = 0;
		for (int i = 0; i < this.setNodes.size(); i++){
			if (n.equals(this.setNodes.get(i))){
				target = i;
			}
		}
		return target;
	}
	
	/**
	 * Method to get the Node object mapped to the identifier ID in the Graph.
	 * @param id Identifier to consider 
	 * @return returns the Node mapped to the identifier ID 
	 */
	public Node findNodeFromID(int id){
		Node node = new Node();
		for (int i = 0; i < this.setNodes.size(); i++){
			if (id == this.setNodes.get(i).getNodeId()){
				node = this.setNodes.get(i);
			}
		}
		return node;
	}
	
	/**
	 * Method to find the index of a Node n in the set of nodes of a Graph g
	 * NB: the graph to consider may differ from the graph from which the method is called 
	 * @param g - graph in which to find the index of the node 
	 * @param n - node for which the index is searched
	 * @return the index of the node in the list. 
	 */
	public int findIndexNode(Graph g, Node n){
		return g.setNodes.indexOf(n);
	}
	
	/**
	 * Method to get the Node in the Graph that has the ID id.
	 * @param id ID of the Node to consider
	 * @return returns the Node with ID id
	 */
	public Node getNode(int id){
		Node node = new Node();
		for(int i = 0; i < this.getListNodes().size(); i++){
			if(this.getListNodes().get(i).getNodeId() == id){
				node = this.getListNodes().get(i);
				break;
			}
		}
		return node;
	}
	
	/**
	 * Method to return the set of nodes of the current Graph g
	 * @return returns the set of Nodes in the Graph 
	 */
	public ArrayList<Node> getListNodes(){
		return this.setNodes;
   	}
	
	/**
	 * Method to display the list of nodes in the Graph
	 * @param setNodes Set of nodes in the Graph
	 */
	public void displayListNodes(ArrayList<Node> setNodes){
		for (int i=0;i<setNodes.size();i++){
			System.out.print(this.getListNodes().get(i).toString() + "; ");
		}
		System.out.println();
	}
	
	/**
	 * Method to display the number of nodes in the graph
	 * @param listNodes list of nodes in the graph
	 */
	public void displayNbNodes(ArrayList<Node> listNodes){
		System.out.println("Number of nodes in the graph " + listNodes.size());
	}//end displayNbNodes
		
	/**
	 * Method to add an edge in the Graph
	 * @param e Edge to add in the graph
	 */
	public void addEdge(Edge e){
		this.setEdges.add(e);
	}
	
	/**
	 * Method to return the set of edges in the current Graph
	 * @return returns the set of Edges in the Graph
	 */
	public ArrayList<Edge> getListEdges(){
		return this.setEdges;
		}
	
	
	/**
	 * Method to display the list of Edges in the Graph
	 * @param setEdges Set of edges in the Graph
	 */
	public void displayEdges(ArrayList<Edge> setEdges){
		for (int i = 0; i< setEdges.size();i++)
                System.out.println(setEdges.get(i).toString());
	}
	
	/**
	 * Method to display the number of edges in the graph
	 * @param listEdges list of edges in the graph
	 */
	public void displayNbEdges(ArrayList<Edge> listEdges){
		System.out.println("Number of edges in the graph " + listEdges.size());
	}//end displayNbEdges
	
	/**
	 * Method to insert a new edge of Node extremities v and w in the Graph
	 * @param v Beginning node of the Edge to insert
	 * @param w End node of the Edge to insert
	 */
	public void insertEdge(Node v, Node w){
		v.addAdjacentNode(w); 
		Edge e = new Edge(v,w);
		this.addEdge(e);
	}
	
	/**
	 * Method that returns the Edge of Node extremities v and w in the Graph
	 * @param v Beginning node of the edge to retrieve
	 * @param w End node of the edge to retrieve
	 * @return returns the Edge so that v is the Beginning Node and w is the End Node. 
	 */
	public Edge findEdge(Node v, Node w){
		Edge e = new Edge();
		for (int i = 0; i < this.setEdges.size();i++){
			if(this.setEdges.get(i).getBegNode().equals(v) && this.setEdges.get(i).getEndNode().equals(w)){
				e = this.setEdges.get(i);
			}
		}
		return e;
	}
	
	/**
	 * Method that removes an Edge e from the graph
	 * @param e Edge to remove from the graph
	 */
	public void removeEdge(Edge e){
		Node v = e.getBegNode();
		Node w = e.getEndNode();
		v.removeAdjacentNode(w);
		w.removeAdjacentNode(v);
		for (int i = 0; i < this.setEdges.size();i++){
			if(this.setEdges.get(i).equals(e)){
				this.setEdges.remove(i);
			}
		}
	}
	
	/**
	 * Method to get the list of nodes in the graph with a degree of connectivity equal to x
	 * @param x Integer degree of connectivity to consider
	 * @return returns the list of nodes in the graph with a degree of connectivity equal to x
	 */
	public ArrayList<Node> getAllNodesDegreeX(int x){
		ArrayList<Node> listNodesDegX = new ArrayList<Node>();
		for(int i = 0; i < this.setNodes.size();i++){
			if(this.setNodes.get(i).getListNodes().size()==x){
				listNodesDegX.add(this.setNodes.get(i));
			}
		}
		return listNodesDegX;
	}
	

	/**
	 * Method to set the ID of the Graph
	 * @param id ID to set
	 */
	public void setGraphID(String id){
		this.graphID = id;
	}
	
	/**
	 * Method to get the ID of the Graph
	 * @return returns the ID of the Graph
	 */
	public String getGraphID(){
		return this.graphID;
	}
	
}//end class