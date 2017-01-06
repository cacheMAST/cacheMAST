package cacheManagement;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import networkGraph.*;

/**
 * This class represents the set of methods to compute the routing configuration
 * 
 * @author D T
 *
 */


public class Routing {
	
	/**
	 * Constructor
	 */
	public Routing(){
	}
	
	/**
	 * 
	 * @param args
	 */
	public void shortestPathBasedRouting(String pathFile, String topologyFile, String topoName) {
		
		GraphGenerator graphGenerator = new GraphGenerator();
	
		Graph g = graphGenerator.createGraphStructure(topologyFile);
		
		ArrayList<Node> listNode = g.getListNodes();
		ArrayList<Node[]> listNodePair = new ArrayList<Node[]>();
		for (int i = 0; i < listNode.size(); i++){
			for (int j = 0; j < listNode.size(); j++){
				if (listNode.get(i).getNodeId() != listNode.get(j).getNodeId()){
					Node[] nodePair = new Node[2]; 
					nodePair[0] = listNode.get(i);
					nodePair[1] = listNode.get(j);
					listNodePair.add(nodePair);
				}
			}
		}
		
		this.computeShortestPath(g, listNodePair, pathFile);
		
	}//end shortestPathBasedRouting
	
	
	/**
	 * Compute the shortest path between any source-destination pairs of nodes.
	 * @param g input graph 
	 * @param listNodePair input list of node pairs 
	 * @param pathFile input root directory to log file
	 */
	public void computeShortestPath(Graph g, ArrayList<Node[]> listNodePair, String pathFile){
		
		JSONArray listPaths = new JSONArray();
		for (int k = 0; k < listNodePair.size(); k++){	
			int[] pred = dijkstra(g, listNodePair.get(k)[0], listNodePair.get(k)[1]);
			String res = storeSP(pred, g, listNodePair.get(k)[0], listNodePair.get(k)[1]);
			StringTokenizer st = new StringTokenizer (res,";");
			LinkedList<String> ll = new LinkedList<String>();
			while (st.hasMoreTokens()) {
				ll.add(st.nextToken());
			}
			//Get the start node
			int start = Integer.parseInt(ll.get(0).substring(0, ll.get(0).indexOf("-")));
			int end = Integer.parseInt(ll.get(ll.size()-1).substring(ll.get(ll.size()-1).indexOf(">")+1));
			int hopcount = ll.size();
			String route = "[";
			if(ll.size()==1){
        		route = route + ll.get(0).substring(0, ll.get(0).indexOf("-")) + "," + ll.get(0).substring(ll.get(0).indexOf(">")+1);
        	}
			else{
				for(int j = 0; j < ll.size(); j++){
		        	route = route + ll.get(j).substring(0, ll.get(j).indexOf("-")) + ",";
		        }
				route = route + ll.get(ll.size()-1).substring(ll.get(ll.size()-1).indexOf(">")+1);
			}
			route = route + "]";
			
			JSONObject objPath = new JSONObject();
			objPath.put("hopcount", hopcount);
			objPath.put("route", route);
			objPath.put("start", start);
			objPath.put("end", end);
			listPaths.add(objPath);
		}
		JSONObject objPathConfig = new JSONObject();
		objPathConfig.put("path", listPaths);
		try {
			FileWriter file = new FileWriter(pathFile);
			file.write(objPathConfig.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//end computeShortestPath
	
	
	/**
	 * Dijkstra-based computation of the shortest path between a pair source-destination nodes.
	 * @param g input graph
	 * @param sourceN input source node 
	 * @param destinationN input destination node 
	 * @return returns the list nodes in the shortest path between sourceN and destinationN 
	 */
	public int[] dijkstra(Graph g, Node sourceN, Node destinationN){
	 
		int source = g.findIndexNode(g, sourceN);
		int destination = g.findIndexNode(g, destinationN);
		int [] distanceC = new int [g.getListNodes().size()];
		boolean [] visited = new boolean [g.getListNodes().size()];
		int [] predecessor = new int [g.getListNodes().size()];
	 
		//Initialise the array of distance to infinity
		for (int i = 0; i < distanceC.length; i++){
		 distanceC[i] = Integer.MAX_VALUE;
		}
	 
		//Initialise the array of visited nodes to false
		for (int j = 0; j < visited.length; j++){
		 visited[j] = false;
		}
	 
		//Initialise the array of predecessor nodes to 0
		for (int k = 0; k < predecessor.length; k++){
		 predecessor[k] = 0;
		}
	 
		//Distance to the source 0, source is visited, predecessor of the source is the source
		distanceC[source] = 0;
		visited[source] = true;
		predecessor[source] = source;
			 
		//Distance between neighbours of the source and the source in the distance array
		for (int h = 0; h < g.getListNodes().get(source).getListNodes().size();h++){
			//index of the neighbour to consider in the set of nodes of graph g
			int index = g.findIndexNode(g,g.getListNodes().get(source).getListNodes().get(h));
			
			if (distanceC[index]== Integer.MAX_VALUE){
				distanceC[index]= g.findEdge(g.getListNodes().get(source), 
						g.getListNodes().get(source).getListNodes().get(h)).getWeight();
				predecessor[index] = source;
			}
			else {
				distanceC[index]= distanceC[index] + g.findEdge(g.getListNodes().get(source), 
						g.getListNodes().get(source).getListNodes().get(h)).getWeight();
				predecessor[index] = source;
			}
		 
		}//end for
	 
		//Find the closest neighbours of the source node
		int indexMin = this.isMinimum(distanceC, visited);
		visited[indexMin] = true;
		
		if (!g.getListNodes().get(indexMin).equals(g.getListNodes().get(destination))){
			Node end = new Node();
			while (!end.equals(g.getListNodes().get(destination))){
			 indexMin = this.neighbourCheckOne(g, indexMin, distanceC, visited, predecessor);
			 end = g.getListNodes().get(indexMin);
			}
		}
	 return predecessor;
	
	}//end dijkstra
	
	
	/**
	 * Find index of the node with minimum distance
	 * @param distanceC array of distances 
	 * @param visited array of boolean indicating whether the node has been visited or not
	 * @return returns the index of the node with the minimum distance and which has not been visited yet
	 */
	public int isMinimum(int [] distanceC, boolean [] visited){
		  int x =Integer.MAX_VALUE; 
		  int y = 0; 
		  for (int i = 0; i < distanceC.length; i++) {
			  if (!visited[i] && distanceC[i]< x) {
				  y=i;
				  x=distanceC[i];    
			  }
		  }  
		  return y;
	 }//end isMinimum
	
	
	/**
	 * Find the neighbour node so that it is the one that minimises the distance from a source node. 
	 * @param g input graph
	 * @param indexMin input index of node with minimum distance
	 * @param distanceC array of distances between any nodes and the source given predecessor is indexMin
	 * @param visited array of boolean indicating whether a node is already in the path (to avoid loops)
	 * @param predecessor array of predecessors of each node in the path (based on node identifier)
	 */
	public int neighbourCheckOne(Graph g, int indexMin, int[] distanceC, boolean[] visited, int[] predecessor){
		int nextIndex = 0;
		for (int h = 0; h < g.getListNodes().get(indexMin).getListNodes().size();h++){
			if (g.getListNodes().get(indexMin).getListNodes().size()!= 0){
				int index = g.findIndexNode(g,g.getListNodes().get(indexMin).getListNodes().get(h));
				int new_distance = distanceC[indexMin] + g.findEdge(g.getListNodes().get(indexMin), 
								g.getListNodes().get(indexMin).getListNodes().get(h)).getWeight();
				if (new_distance < distanceC[index]){
					distanceC[index]= distanceC[indexMin] + g.findEdge(g.getListNodes().get(indexMin), 
							g.getListNodes().get(indexMin).getListNodes().get(h)).getWeight();
					predecessor[index] = indexMin;
				}
			}
		}
		nextIndex = this.isMinimum(distanceC, visited);
		visited[nextIndex] = true;
		return nextIndex;
	}//end neighbourCheckOne
	
	/**
	 * Convert output of dijkstra into a String.
	 * @param predecessor array of predecessors of each node in the path (based on node identifier)
	 * @param g input graph 
	 * @param sourceN input source node 
	 * @param destinationN input destination node 
	 */
	public String storeSP(int[] predecessor, Graph g, Node sourceN, Node destinationN){
		
		String sp = "";

		int destination = g.findIndexNode(g, destinationN);
		
		String end = "" + g.getListNodes().get(destination).getNodeId();
		int pred = destination;
		String listLinks = end+"->";
	
		while (!g.getListNodes().get(pred).equals(sourceN)){
			if(g.getListNodes().get(predecessor[pred]).equals(sourceN)){
				listLinks = listLinks + g.getListNodes().get(predecessor[pred]).getNodeId() + ";";
					pred = predecessor[pred];
			}
			else {
				listLinks = listLinks + g.getListNodes().get(predecessor[pred]).getNodeId() + ";"
				+ g.getListNodes().get(predecessor[pred]).getNodeId() + "->";
				pred = predecessor[pred];
			}
		}
		//return sp = sp + "|;" + listLinks;
		return sp = sp + listLinks;
	}//end storeSP
	
	
	
}
