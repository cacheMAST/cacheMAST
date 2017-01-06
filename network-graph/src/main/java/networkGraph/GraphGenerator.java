package networkGraph;

import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Set of methods to create a Graph Object.  
 * 
 *@author DT
 */

public class GraphGenerator {

	/**
	 *  Constructor of an object GraphGenerator
	 */
	public GraphGenerator(){
	}
	
	/**
	 * Graph object based on input graph file  
	 * @param file_name root directory of input graph file
	 * @return returns the graph object
	 */
	public Graph createGraphStructure(String file_name){
	
		//Create a new graph g
		Graph g = new Graph();
		
		//Parse the input
		HashMap<String,int[]> mapGraph = this.readGraphFile(file_name);
			
		//Get list of nodes in the graph
		ArrayList<Node> listNodes = this.getListNodes(mapGraph);
		g.setSetNodes(listNodes);
				
		//Create list of links in the graph
		Iterator<String> iter = mapGraph.keySet().iterator();
		String entry = new String();
		while(iter.hasNext()){
			entry = (String)iter.next();
			//Bidirectional edges
			g.insertEdge(g.getNode(mapGraph.get(entry)[0]),g.getNode(mapGraph.get(entry)[1]));
			g.insertEdge(g.getNode(mapGraph.get(entry)[1]), g.getNode(mapGraph.get(entry)[0]));
			g.findEdge(g.getNode(mapGraph.get(entry)[0]),g.getNode(mapGraph.get(entry)[1])).setWeight(mapGraph.get(entry)[2]);
		  	g.findEdge(g.getNode(mapGraph.get(entry)[1]),g.getNode(mapGraph.get(entry)[0])).setWeight(mapGraph.get(entry)[2]);
		  	long capacityBps = Long.parseLong("" + mapGraph.get(entry)[3])*1000;
		  	g.findEdge(g.getNode(mapGraph.get(entry)[0]),g.getNode(mapGraph.get(entry)[1])).setCapacity(capacityBps);
		  	g.findEdge(g.getNode(mapGraph.get(entry)[1]),g.getNode(mapGraph.get(entry)[0])).setCapacity(capacityBps);
		  	double delay = mapGraph.get(entry)[4];
		  	g.findEdge(g.getNode(mapGraph.get(entry)[0]),g.getNode(mapGraph.get(entry)[1])).setDelay(delay);
		  	g.findEdge(g.getNode(mapGraph.get(entry)[1]),g.getNode(mapGraph.get(entry)[0])).setDelay(delay);
		}
		
		return g;
		
	}//end createGraph

	/**
	 * Read the input graph json file
	 * @param file_name root directory of input graph file
	 * @return returns a hash map structure of the input file
	 */
	public HashMap<String,int[]> readGraphFile(String file_name){

		//Get links and nodes from the input file
		HashMap<String,int[]> map = new HashMap<String,int[]>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(file_name));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all links
			JSONArray listLinks = (JSONArray) jsonObject.get("graph");
			Iterator<JSONObject> iterator = listLinks.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectLink = (JSONObject)iterator.next();
				//int startNode = (int)(long)jsonObjectLink.get("start");
				int startNode = ((Long)jsonObjectLink.get("start")).intValue();
				//int endNode = (int)(long)jsonObjectLink.get("end");
				int endNode = ((Long)jsonObjectLink.get("end")).intValue();
				//int linkWeight = (int)(long)jsonObjectLink.get("linkWeight");
				int linkWeight = ((Long)jsonObjectLink.get("linkWeight")).intValue();
				//int linkCapacityKbps = (int)(long)jsonObjectLink.get("linkCapacityKbps");
				int linkCapacityKbps = ((Long)jsonObjectLink.get("linkCapacityKbps")).intValue();
				//int linkDelay = (int)(long)jsonObjectLink.get("delay");
				int linkDelay = ((Long)jsonObjectLink.get("delay")).intValue();
				int[] array = new int[5];
				array[0] = startNode;
				array[1] = endNode;
				array[2] = linkWeight;
				array[3] = linkCapacityKbps;
				array[4] = linkDelay;
				map.put("E"+startNode+"-"+endNode, array);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return map;
	}//end readGraphFile

	/**
	 * Extract the list of nodes in the graph
	 * @param inputGraphMap hash map structure of the input graph
	 * @return returns the list of nodes in the graph
	 */
	public ArrayList<Node> getListNodes(HashMap<String,int[]> inputGraphMap){
		
		HashMap<String,Node> mapNode = new HashMap<String,Node>(10000);
		//Extract the list of nodes from the graph input
		Iterator<String> iter = inputGraphMap.keySet().iterator();
		String entry = new String();
		while(iter.hasNext()){
			entry = (String)iter.next();
			//Get the identifier of the nodes in the current entry
			int n1 = inputGraphMap.get(entry)[0];
			int n2 = inputGraphMap.get(entry)[1];
			//Check that the nodes are not already referenced in the node map
			if(!mapNode.containsKey("N"+n1)){
				Node node1 = new Node(n1);
				mapNode.put("N"+n1, node1);
			}
			if(!mapNode.containsKey("N"+n2)){
				Node node2 = new Node(n2);
				mapNode.put("N"+n2, node2);
			}
		}
		//Create list of nodes for the graph
		ArrayList<Node> listNodes = new ArrayList<Node>();
		Iterator<String> iter1 = mapNode.keySet().iterator();
		String entry1 = new String();
		while(iter1.hasNext()){
			entry1 = (String)iter1.next();
			listNodes.add(mapNode.get(entry1));
		}
		
		return listNodes;
		
	}//end getListNodes
	
	/**
	 * Create edgeMap mapping edge extremity points to object Edge
	 * @param inputGraphFile String
	 * @return edgeMap
	 */
	public HashMap<String,Edge> createEdgeMap(String inputGraphFile){
		Graph graph = this.createGraphStructure(inputGraphFile);
		HashMap<String,Edge> edgeMap = new HashMap<String,Edge>();
		ArrayList<Edge> listEdges = graph.getListEdges();
		for(int i = 0; i < listEdges.size(); i++){
			String extremities = "ID:" + listEdges.get(i).getBegNode().getNodeId() + "-ID:" + listEdges.get(i).getEndNode().getNodeId();		
			edgeMap.put(extremities, listEdges.get(i));
		}
		return edgeMap;
	}//end createEdgeMap
	
	
	
	
}//end of class
