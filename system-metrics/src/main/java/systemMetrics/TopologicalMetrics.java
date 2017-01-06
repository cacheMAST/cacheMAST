package systemMetrics;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import networkGraph.Graph;
import networkGraph.Node;


/**
 * This class deals with processing the topological metrics using the data 
 * obtained from the provided path file and total number of nodes
 * 
 * @author Tonny Duong, DT
 */
public class TopologicalMetrics {
	

	/**
	 * Compute the characteristics of in-network paths
	 * @param inputPathFile String
	 * @return table with minPathLength, maxPathLength, avgPathLength
	 */
	public double[] computePathStatistics(String inputPathFile){
		
		double minPathLength = Double.MAX_VALUE;
		double maxPathLength = Double.MIN_VALUE;
		double avgPathLength = 0;
		int nbEntries = 0;
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputPathFile));

			JSONObject jsonObject = (JSONObject) obj;
		
			//Loop on all nodes
			JSONArray listPaths = (JSONArray) jsonObject.get("path");
			Iterator<JSONObject> iterator = listPaths.iterator();
			while (iterator.hasNext()) {
				nbEntries++;
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//double hopcount = (int)(long)jsonObjectNode.get("hopcount");
				double hopcount = ((Long)jsonObjectNode.get("hopcount")).intValue();
				if(hopcount<minPathLength){
					minPathLength = hopcount;
				}
				else{
					if(hopcount>maxPathLength){
						maxPathLength = hopcount;
					}	
				}
				avgPathLength = avgPathLength + hopcount;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		
		avgPathLength = (double)avgPathLength/(double)nbEntries;
		
		double[] pathCharacteristics = new double[3];
		pathCharacteristics[0] = (double)Math.round(minPathLength * 100) / 100;
		pathCharacteristics[1] = (double)Math.round(maxPathLength * 100) / 100;
		pathCharacteristics[2] = (double)Math.round(avgPathLength * 100) / 100;
		return pathCharacteristics;
		
	}//computePathStatistics
	
	/**
	 * Compute the connectivity degree of a node
	 * @param node Node
	 * @return connectivity degree of the node
	 */
	public double computeNodeDegreeOfConnectivity(Node node){
		return node.getListNodes().size();
	}
	
	/**
	 * Compute the clustering coefficient of a node
	 * @param node Node
	 * @return clustering coefficient of the node
	 */
	public double computeNodeClusteringCoefficient(Node node){
		
		double neighboursLink = 0;
		
		for(int i = 0; i < node.getListNodes().size()-1; i++){
			Node currN = node.getListNodes().get(i);
			for(int k = i+1; k < node.getListNodes().size(); k++){
				if(currN.checkIsAdjacentNode(node.getListNodes().get(k))==true){
					neighboursLink++;
				}
			}
		}
		return (double)2*neighboursLink/(double)(node.getListNodes().size()*(node.getListNodes().size()-1));
	}//end computeNodeClusteringCoefficient
	
	/**
	 * Compute the average distance factor of a node
	 * @param inputPathFile String
	 * @param node Node
	 * @param nbNodes int
	 * @return average distance factor of the node
	 */
	public double computeNodeAverageDistanceFactor(String inputPathFile, Node node, int nbNodes){
		
		double avgDistanceFactor = 0;
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputPathFile));

			JSONObject jsonObject = (JSONObject) obj;
		
			//Loop on all nodes
			JSONArray listPaths = (JSONArray) jsonObject.get("path");
			Iterator<JSONObject> iterator = listPaths.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//double hopcount = (int)(long)jsonObjectNode.get("hopcount");
				double hopcount = ((Long)jsonObjectNode.get("hopcount")).intValue();
				//int startNodeID = (int)(long)jsonObjectNode.get("start");
				int startNodeID = ((Long)jsonObjectNode.get("start")).intValue();
				if(startNodeID == node.getNodeId()){
					avgDistanceFactor = avgDistanceFactor + hopcount;
				}		
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		
		return (double)avgDistanceFactor/(double)nbNodes;
	}//end computeNodeAverageDistanceFactor
	
	/**
	 * Compute node metrics map
	 * @param inputPathFile String 
	 * @param graph Graph
	 * @return node metrics map
	 */
	public HashMap<String,ArrayList<Double>> computeNodeMetrics(String inputPathFile, Graph graph){
		
		HashMap<String,ArrayList<Double>> nodeMetricsMap = new HashMap<String,ArrayList<Double>>();
		
		for(int i = 0; i < graph.getListNodes().size(); i++){
			Node node = graph.getListNodes().get(i);
			ArrayList<Double> listMetrics = new ArrayList<Double>();
			double nodeDegConnect = this.computeNodeDegreeOfConnectivity(node);
			double nodeClusteringCoeff = this.computeNodeClusteringCoefficient(node);
			double nodeAvgDistFact = this.computeNodeAverageDistanceFactor(inputPathFile, node, graph.getListNodes().size());
			listMetrics.add((double)Math.round(nodeDegConnect * 100) / 100);
			listMetrics.add((double)Math.round(nodeClusteringCoeff * 100) / 100);
			listMetrics.add((double)Math.round(nodeAvgDistFact * 100) / 100);
			nodeMetricsMap.put("ID:"+node.getNodeId(), listMetrics);
		}
		return nodeMetricsMap;
	}//end computeNodeMetrics
	
	/**
	 * Log topological metrics in JSON format
	 * @param graph Graph
	 * @param pathFile String input path file
	 * @param topologicalMetrics String output topological metrics file (JSON)
	 */
	public void logJSONTopologicalMetrics(Graph graph, String pathFile, String topologicalMetrics){
		
		double[] pathCharateristics = computePathStatistics(pathFile);
		HashMap<String,ArrayList<Double>> nodeMetricsMap = computeNodeMetrics(pathFile, graph);
		
		JSONObject objTopoStats = new JSONObject();
		objTopoStats.put("minPathLength", pathCharateristics[0]);
		objTopoStats.put("maxPathLength", pathCharateristics[1]);
		objTopoStats.put("avgPathLength", pathCharateristics[2]);
		
		JSONArray listNodeStats = new JSONArray();
		Iterator<String> iterN = nodeMetricsMap.keySet().iterator();
		String n = new String();
		while(iterN.hasNext()){
			n = (String)iterN.next();
			int nodeID = Integer.parseInt(n.substring(3));
			double nodeDegConnect = nodeMetricsMap.get(n).get(0);
			double nodeClusteringCoeff = nodeMetricsMap.get(n).get(1);
			double nodeAvgDistFact = nodeMetricsMap.get(n).get(2);
			JSONObject objNode = new JSONObject();
			objNode.put("nodeID", nodeID);
			objNode.put("nodeDegConnect", nodeDegConnect);
			objNode.put("nodeClusteringCoeff", nodeClusteringCoeff);
			objNode.put("nodeAvgDistFact", nodeAvgDistFact);
			listNodeStats.add(objNode);
		}
		objTopoStats.put("nodeStats", listNodeStats);
		
		try {
			FileWriter file = new FileWriter(topologicalMetrics);
			file.write(objTopoStats.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//end logJSONTopologicalMetrics
	
	
	
	
}

