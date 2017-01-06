package systemMetrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cacheManagement.Cache;
import cacheManagement.Content;
import networkGraph.Edge;

/**
 * This class represents represents the set of functions to compute network metrics
 * 
 * @author Daphne Tuncer
 *
 */

public class NetworkMetrics {
	
	/**
	 * Constructor
	 */
	public NetworkMetrics(){
	}
	
		
	/**
	 * Create link metrics map (link associated with link_load and link_utilization)
	 * @param edgeInvolvementMap HashMap<Edge, ArrayList<Cache[]>>
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @return linkMetricsMap
	 */
	public HashMap<Edge, ArrayList<Double>> computeLinkMetrics(HashMap<Edge, ArrayList<String>> edgeInvolvementMap, 
						HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap, HashMap<String, Cache> cacheIDMap){
		
		HashMap<Edge, ArrayList<Double>> linkStatisticsMap = new HashMap<Edge, ArrayList<Double>>();
		
		Iterator<Edge> iterE = edgeInvolvementMap.keySet().iterator();
		Edge edge = new Edge();
		while(iterE.hasNext()){
			edge = (Edge)iterE.next();
			double edgeLoad = 0;
			for(int i = 0; i < edgeInvolvementMap.get(edge).size(); i++){
				String cachePair = edgeInvolvementMap.get(edge).get(i);
				Cache cacheReq = cacheIDMap.get(cachePair.substring(0, cachePair.indexOf("-")));
				Cache cacheServer = cacheIDMap.get(cachePair.substring(cachePair.indexOf("-")+1));
				//From serverSelection, get all content transferred between the two caches
				Iterator<Content> iterC = serverSelectionMap.get(cacheReq).keySet().iterator();
				Content content = new Content();
				while(iterC.hasNext()){
					content = (Content)iterC.next();
					//Check whether the content is retrieved from cacheServer
					for(int k = 0; k < serverSelectionMap.get(cacheReq).get(content).size(); k++){
						if(((Cache)serverSelectionMap.get(cacheReq).get(content).get(k)[0]).equals(cacheServer)){
							//edgeLoad = edgeLoad + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1] * content.getContentSize();
							edgeLoad = edgeLoad + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue() * content.getContentSize();
							break;
						}	
					}
				}
			}//for all pairs of caches involved over the link
			double edgeUtilization = (double)edgeLoad / (double)edge.getCapacity() * 100;
			ArrayList<Double> listLinkMetrics = new ArrayList<Double>();
			listLinkMetrics.add((double)Math.round(edgeLoad * 100) / 100);
			listLinkMetrics.add((double)Math.round(edgeUtilization * 100) / 100);
			listLinkMetrics.add((double)Math.round(edge.getWeight() * 100) / 100);
			listLinkMetrics.add(Double.parseDouble(""+edge.getCapacity()));
			linkStatisticsMap.put(edge, listLinkMetrics);
		}//end while for each edge
		
		return linkStatisticsMap;
		
	}//end computeLinkMetrics
	
	
	/**
	 * Compute utilization metrics at the network level (min-u, max-u, avg-u)
	 * @param linkStatisticsMap HashMap<Edge, ArrayList<Double>>
	 * @return double[] array with the utilization metrics at the network level
	 */
	public double[] computeGlobalUtilizationMetrics(HashMap<Edge, ArrayList<Double>> linkStatisticsMap){
		
		double minU = Double.MAX_VALUE;
		double maxU = Double.MIN_VALUE;
		double avgU = 0;
		
		Iterator<Edge> iterE = linkStatisticsMap.keySet().iterator();
		Edge edge = new Edge();
		while(iterE.hasNext()){
			edge = (Edge)iterE.next();
			avgU = avgU + linkStatisticsMap.get(edge).get(1);
			if(linkStatisticsMap.get(edge).get(1) < minU){
				minU = linkStatisticsMap.get(edge).get(1);
			}
			else{
				if(linkStatisticsMap.get(edge).get(1) > maxU){
					maxU = linkStatisticsMap.get(edge).get(1);
				}
			}
		}
		avgU = (double)avgU/linkStatisticsMap.size();
		
		double[] tableUMetrics = new double[3];
		tableUMetrics[0] = (double)Math.round(minU * 100) / 100;
		tableUMetrics[1] = (double)Math.round(maxU * 100) / 100;
		tableUMetrics[2] = (double)Math.round(avgU * 100) / 100;
		
		return tableUMetrics;
		
	}//end computeGlobalUtilizationMetrics
	
	/**
	 * Compute the load incurred in the network
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @return total network load
	 */
	public double computeTotalNetworkLoad(HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap){
		
		double netwLoad = 0;
		
		Iterator<Cache> iterC = serverSelectionMap.keySet().iterator();
		Cache cacheReq = new Cache();
		while(iterC.hasNext()){
			cacheReq = (Cache)iterC.next();
			Iterator<Content> iter = serverSelectionMap.get(cacheReq).keySet().iterator();
			Content content = new Content();
			while(iter.hasNext()){
				content = (Content)iter.next();
				//Check whether the content is served locally, i.e. cacheReq == cacheServer
				for(int k = 0; k < serverSelectionMap.get(cacheReq).get(content).size(); k++){
					if(!((Cache)serverSelectionMap.get(cacheReq).get(content).get(k)[0]).equals(cacheReq)){
						//netwLoad = netwLoad + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1] * content.getContentSize();
						netwLoad = netwLoad + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue() * content.getContentSize();
						break;
					}	
				}
			}//end while all content items
		}//end while all caches
		return (double)Math.round(netwLoad * 100) / 100;
	}//end computeTotalNetworkLoad
	
	
	/**
	 * Compute the average in-network retrieval delay
	 * @param routingMap HashMap<String, ArrayList<Edge>>
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @return average retrieval delay
	 */
	public double computeAverageRetrievalDelay(HashMap<String, ArrayList<Edge>> routingMap, HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap){
		
		double avgRetrieval = 0;
		double denominator = 0;
		
		Iterator<Cache> iterC = serverSelectionMap.keySet().iterator();
		Cache cache = new Cache();
		while(iterC.hasNext()){
			cache = (Cache)iterC.next();
			Iterator<Content> iterCC = serverSelectionMap.get(cache).keySet().iterator();
			Content content = new Content();
			while(iterCC.hasNext()){
				content = (Content)iterCC.next();
				for(int j = 0; j < serverSelectionMap.get(cache).get(content).size(); j++){
					if(cache.getCacheID() != ((Cache)serverSelectionMap.get(cache).get(content).get(j)[0]).getCacheID()){
						//Get the path ID
						String path = "ID:" + cache.getCacheID() + "-ID:" + ((Cache)serverSelectionMap.get(cache).get(content).get(j)[0]).getCacheID();
						double pathDelay = 0;
						for(int k = 0; k < routingMap.get(path).size(); k++){
							pathDelay = pathDelay + routingMap.get(path).get(k).getDelay();
						}
						//denominator = denominator + ((double)serverSelectionMap.get(cache).get(content).get(j)[1]);
						denominator = denominator + (((Double)serverSelectionMap.get(cache).get(content).get(j)[1])).doubleValue();
						//avgRetrieval = avgRetrieval + pathDelay * ((double)serverSelectionMap.get(cache).get(content).get(j)[1]);
						avgRetrieval = avgRetrieval + pathDelay * (((Double)serverSelectionMap.get(cache).get(content).get(j)[1])).doubleValue();
					}
				}
			}
		}//end while all paths
		
		return (double)Math.round(((double)avgRetrieval/(double)denominator)*100/100);
	}//end computeAverageRetrievalDelay
	
	/**
	 * Log network metrics in JSON format
	 * @param edgeInvolvementMap HashMap<Edge, ArrayList<Double>>
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @param cacheIDMap HashMap<String, Cache>
	 * @param routingMap HashMap<String, ArrayList<Edge>>
	 * @param networkMetricsFile String output network metrics file (in JSON)
	 */
	public void logJSONNetworkMetrics(HashMap<Edge, ArrayList<String>> edgeInvolvementMap, HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap,
									HashMap<String, Cache> cacheIDMap, HashMap<String, ArrayList<Edge>> routingMap, String networkMetricsFile){
		
		HashMap<Edge, ArrayList<Double>> linkStatisticsMap = computeLinkMetrics(edgeInvolvementMap, serverSelectionMap, cacheIDMap);
		double[] util = computeGlobalUtilizationMetrics(linkStatisticsMap);
		double totNetwLoad = computeTotalNetworkLoad(serverSelectionMap);
		double avgRetrievalDelay = computeAverageRetrievalDelay(routingMap, serverSelectionMap);
		
		JSONObject objNetworkStats = new JSONObject();
		objNetworkStats.put("minUtil", util[0]);
		objNetworkStats.put("maxUtil", util[1]);
		objNetworkStats.put("avgUtil", util[2]);
		objNetworkStats.put("totNetwLoad", totNetwLoad);
		objNetworkStats.put("avgRetrievalDelay", avgRetrievalDelay);
		JSONArray listLinkStats = new JSONArray();
		Iterator<Edge> iter = linkStatisticsMap.keySet().iterator();
		Edge c = new Edge();
		while(iter.hasNext()){
			c = (Edge)iter.next();
			int startID = c.getBegNode().getNodeId();
			int endID = c.getEndNode().getNodeId();
			double linkWeight = linkStatisticsMap.get(c).get(2);
			double linkCapacity = linkStatisticsMap.get(c).get(3);
			double linkLoad = linkStatisticsMap.get(c).get(0);
			double linkUtil = linkStatisticsMap.get(c).get(1);
			JSONObject objLink = new JSONObject();
			objLink.put("startID", startID);
			objLink.put("endID", endID);
			objLink.put("linkWeight", linkWeight);
			objLink.put("linkCapacity", linkCapacity);
			objLink.put("linkLoad", linkLoad);
			objLink.put("linkUtil", linkUtil);
			listLinkStats.add(objLink);
		}
		objNetworkStats.put("linkStats", listLinkStats);
		
		try {
			FileWriter file = new FileWriter(networkMetricsFile);
			file.write(objNetworkStats.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//end logJSONNetworkMetrics
	
	

}
