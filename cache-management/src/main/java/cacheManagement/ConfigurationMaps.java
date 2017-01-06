package cacheManagement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import networkGraph.Edge;
import networkGraph.Node;


/**
 * This class represents a set of HashMaps indicating the caching configuration
 * 
 * @author Daphne Tuncer
 *
 */

public class ConfigurationMaps {
	
	//All caches in the caching infrastructure
	private HashMap<Cache, Cache> cacheMap;
	//Mapping cacheID - cache
	private HashMap<String, Cache> cacheIDMap;
	//All content items in the catalogue
	private HashMap<Content, Content> contentMap;
	//Mapping contentID - content
	private HashMap<String, Content> contentIDMap;
	//Statistics of each cache: total local demand, occupancy
	private HashMap<Cache, ArrayList<Double>> cacheStatisticsMap;
	//Demand for each content at each cache
	private HashMap<Cache, HashMap<Content,Double>> demandMap;
	//Placement status of each content at each cache (is cached or not)
	private HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap;
	//Location from where each requested content at a specific cache is retrieved	
	private HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap;
	//In-network locations from where each content is available (empty if no in-network copies)  
	private HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap;
	//Route between any pair of caches
	private HashMap<String, ArrayList<Edge>> routingMap; 
	//Characteristics of the route between any pair of caches
	private HashMap<Cache[], ArrayList<Double>> routeCharactericticsMap; 
	//Pairs of caches with route traversing edge
	private HashMap<Edge, ArrayList<Cache[]>> edgeInvolvementMap;
	
	/**
	 * Constructor
	 */
	public ConfigurationMaps(){
	}
	
	/**
	 * Setters
	 */
	public void setCacheMap(HashMap<Cache, Cache> cacheMap){	
		this.cacheMap = cacheMap;
	}
	public void setCacheIDMap(HashMap<String, Cache> cacheIDMap){	
		this.cacheIDMap = cacheIDMap;
	}
	public void setContentMap(HashMap<Content,Content> contentMap){	
		this.contentMap = contentMap;
	}
	public void setContentIDMap(HashMap<String, Content> contentIDMap){	
		this.contentIDMap = contentIDMap;
	}
	public void setCacheStatisticsMap(HashMap<Cache, ArrayList<Double>> cacheStatisticsMap){	
		this.cacheStatisticsMap = cacheStatisticsMap;
	}
	public void setDemandMap(HashMap<Cache, HashMap<Content,Double>> demandMap){
		this.demandMap = demandMap;
	}
	public void setPlacementMap(HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap){
		this.contentPlacementMap = contentPlacementMap;
	}
	public void setServerSelectionMap(HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap){
		this.serverSelectionMap = serverSelectionMap;
	}
	public void setInNetwContentAvailabilityMap(HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap){
		this.inNetwContentAvailabilityMap = inNetwContentAvailabilityMap;
	}
	public void setRoutingMap(HashMap<String, ArrayList<Edge>> routingMap){
		this.routingMap = routingMap;
	}
	public void setEdgeInvolvementMap(HashMap<Edge, ArrayList<Cache[]>> edgeInvolvementMap){
		this.edgeInvolvementMap = edgeInvolvementMap;
	}
	public void setRouteCharactericticsMap(HashMap<Cache[], ArrayList<Double>> routeCharacteristicsMap){
		this.routeCharactericticsMap = routeCharacteristicsMap;
	}
	
	
	/**
	 * Getters
	 */
	public HashMap<Cache, Cache> getCacheMap(){	
		return this.cacheMap;
	}
	public HashMap<String, Cache> getCacheIDMap(){	
		return this.cacheIDMap;
	}
	public HashMap<Content,Content> getContentMap(){	
		return this.contentMap;
	}
	public HashMap<String, Content> getContentIDMap(){	
		return this.contentIDMap;
	}
	public HashMap<Cache, ArrayList<Double>> getCacheStatisticsMap(){	
		return this.cacheStatisticsMap;
	}
	public HashMap<Cache, HashMap<Content,Double>> getDemandMap(){
		return this.demandMap;
	}
	public HashMap<Cache, HashMap<Content,Boolean>> getPlacementMap(){
		return this.contentPlacementMap;
	}
	public HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> getServerSelectionMap(){
		return this.serverSelectionMap;
	}
	public HashMap<Content,ArrayList<Cache>> getInNetwContentAvailabilityMap(){
		return this.inNetwContentAvailabilityMap;
	}
	public HashMap<String, ArrayList<Edge>> getRoutingMap(){
		return this.routingMap;
	}
	public HashMap<Edge, ArrayList<Cache[]>> getEdgeInvolvementMap(){
		return this.edgeInvolvementMap;
	}
	public HashMap<Cache[], ArrayList<Double>> getRouteCharactericticsMap(){
		return this.routeCharactericticsMap;
	}
	

	/**
	 * Create cacheMap
	 * @param inputCacheFile root directory to the json cachingConfig file 
	 * @return cacheMap 
	 */
	public HashMap<Cache, Cache> createCacheMap(String inputCacheFile){
		
		HashMap<Cache, Cache> cacheMap = new HashMap<Cache, Cache>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputCacheFile));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("node");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache ID
				//int cacheID = (int)(long)jsonObjectNode.get("nodeID");
				int cacheID = ((Long)jsonObjectNode.get("nodeID")).intValue();
				//Get the cache capacity (in Bytes)
				double capacityBytes = (Double)jsonObjectNode.get("capacityBytes");
				//Get the cache status (server or not)
				//boolean isServer =(boolean)jsonObjectNode.get("isServer");
				boolean isServer =((Boolean)jsonObjectNode.get("isServer")).booleanValue();
				//Create new cache and add it to the hashMap
				Cache cache = new Cache(cacheID,capacityBytes,isServer);
				cacheMap.put(cache,cache);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return cacheMap;
		
	}//end createCacheMap
	
	
	/**
	 * Create cacheIDMap
	 * @param inputContentFile root directory to the json contentConfig file 
	 * @return contentMap 
	 */
	public HashMap<String, Cache> createCacheIDMap(HashMap<Cache, Cache> cacheMap){
		
		HashMap<String, Cache> cacheIDMap = new HashMap<String, Cache>();
		Iterator<Cache> iterC = cacheMap.keySet().iterator();
		Cache cache = new Cache();
		while(iterC.hasNext()){
			cache = (Cache)iterC.next();
			cacheIDMap.put("ID:"+cache.getCacheID(), cache);
		}
		return cacheIDMap;
		
	}//end createCacheIDMap
	
	
	
	/**
	 * Create contentMap
	 * @param inputContentFile root directory to the json contentConfig file 
	 * @return contentMap 
	 */
	public HashMap<Content, Content> createContentMap(String inputContentFile){
		
		HashMap<Content, Content> contentMap = new HashMap<Content, Content>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputContentFile));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all content
			JSONArray listContents = (JSONArray) jsonObject.get("content");
			Iterator<JSONObject> iterator = listContents.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the content ID
				//int contentID = (int)(long)jsonObjectNode.get("contentID");
				int contentID = ((Long)jsonObjectNode.get("contentID")).intValue();
				//Get the content size (in Bytes)
				//double sizeBytes = (double)jsonObjectNode.get("sizeBytes");
				double sizeBytes = ((Double)jsonObjectNode.get("sizeBytes")).doubleValue();
				//Create new content and add it to the hashMap
				Content content = new Content(contentID,sizeBytes);
				contentMap.put(content,content);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return contentMap;
		
	}//end createContentMap
	
	
	/**
	 * Create contentIDMap
	 * @param contentMap HashMap<Content, Content>
	 * @return contentIDMap 
	 */
	public HashMap<String, Content> createContentIDMap(HashMap<Content, Content> contentMap){
		
		HashMap<String, Content> contentIDMap = new HashMap<String, Content>();
		Iterator<Content> iterC = contentMap.keySet().iterator();
		Content content = new Content();
		while(iterC.hasNext()){
			content = (Content)iterC.next();
			contentIDMap.put("ID:"+content.getContentID(), content);
		}
		return contentIDMap;
		
	}//end createContentIDMap
	
	

	/**
	 * Create demandMap
	 * @param inputDemandFile root directory to the json demand file 
	 * @param contentIDMap HashMap<String, Content>
	 * @param cacheIDMap HashMap<String, Content>
	 * @return demandMap
	 */
	public HashMap<Cache, HashMap<Content,Double>> createDemandMap(String inputDemandFile, 
						HashMap<String, Content> contentIDMap, HashMap<String, Cache> cacheIDMap){
		
		HashMap<Cache, HashMap<Content,Double>> demandMap = new HashMap<Cache, HashMap<Content,Double>>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputDemandFile));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("networkDemand");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache ID
				//int cacheID = (int)(long)jsonObjectNode.get("nodeID");
				int cacheID = ((Long)jsonObjectNode.get("nodeID")).intValue();
				//Get the list of contents
				JSONArray listContents = (JSONArray)jsonObjectNode.get("contentDemand");
				//Create a new HashMap<Content,Double> object for the local demand associated with each content 
				HashMap<Content,Double> localContentMap = new HashMap<Content,Double>(); 
				Iterator<JSONObject> iteratorC = listContents.iterator();
				while (iteratorC.hasNext()){
					JSONObject jsonObjectContent = (JSONObject)iteratorC.next();
					//Get content String ID
					//int contentID = (int)(long)jsonObjectContent.get("contentID");
					int contentID = ((Long)jsonObjectContent.get("contentID")).intValue();
					//Get local demand 
					//double demand = (double)jsonObjectContent.get("demand");
					double demand = ((Double)jsonObjectContent.get("demand")).doubleValue();
					//Add a new entry in localContentMap					
					localContentMap.put(contentIDMap.get("ID:"+contentID),demand);	
				}
				demandMap.put(cacheIDMap.get("ID:"+cacheID), localContentMap);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return demandMap;
		
	}//end createDemandMap
	

	/**
	 * Create cacheStatisticsMap
	 * @param inputDemandFile root directory to the json demand file 
	 * @param contentIDMap HashMap<String, Content>
	 * @param cacheIDMap HashMap<String, Content>
	 * @return demandMap
	 */
	public HashMap<Cache, Double[]> createCacheStatisticsMap(String inputDemandFile, HashMap<String, Cache> cacheIDMap){
		
		HashMap<Cache, Double[]> cacheStatMap = new HashMap<Cache, Double[]>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputDemandFile));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("networkDemand");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache ID
				//int cacheID = (int)(long)jsonObjectNode.get("nodeID");
				int cacheID = ((Long)jsonObjectNode.get("nodeID")).intValue();
				//Get the total local demand
				//double demand = (double)jsonObjectNode.get("totalDemand");
				double demand = ((Double)jsonObjectNode.get("totalDemand")).doubleValue();
				//ArrayList of cache stats: cache capacity, total local demand, occupancy
				Double[] stats = new Double[25];
				stats[0] = cacheIDMap.get("ID:"+cacheID).getCacheCapacity();
				stats[1] = demand;
				double occupancy = 0;
				stats[2] = occupancy;
				cacheStatMap.put(cacheIDMap.get("ID:"+cacheID), stats);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return cacheStatMap;
		
	}//end cacheStatisticsMap
	
	
	/**
	 * Create placementMap
	 * @param inputPlacementFile root directory to the json demand file 
	 * @param contentIDMap HashMap<String, Content>
	 * @param cacheIDMap HashMap<String, Content>
	 * @return placementMap
	 */
	public HashMap<Cache, HashMap<Content,Boolean>> createContentPlacementMap(String inputPlacementFile, 
			HashMap<String, Content> contentIDMap, HashMap<String, Cache> cacheIDMap){
		
		HashMap<Cache, HashMap<Content,Boolean>> placementMap = new HashMap<Cache, HashMap<Content,Boolean>>();
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputPlacementFile));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("placement");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache ID
				//int cacheID = (int)(long)jsonObjectNode.get("nodeID");
				int cacheID = ((Long)jsonObjectNode.get("nodeID")).intValue();
				//Get the cache status (is active or not)
				//boolean cacheActive = (boolean)jsonObjectNode.get("cacheActive");
				boolean cacheActive = ((Boolean)jsonObjectNode.get("cacheActive")).booleanValue();
				if(cacheActive == true){
					//Get the list of contents
					JSONArray listContents = (JSONArray)jsonObjectNode.get("content");
					//Create a new HashMap<Content,Boolean> object 
					HashMap<Content,Boolean> localContentMap = new HashMap<Content,Boolean>(); 
					Iterator<JSONObject> iteratorC = listContents.iterator();
					while (iteratorC.hasNext()) {
						JSONObject jsonObjectContent = (JSONObject)iteratorC.next();
						//Get content String ID
						//int contentID = (int)(long)jsonObjectContent.get("contentID");
						int contentID = ((Long)jsonObjectContent.get("contentID")).intValue();
						//Add a new entry in localContentMap
						localContentMap.put(contentIDMap.get("ID:"+contentID),true);	
					}
					placementMap.put(cacheIDMap.get("ID:"+cacheID), localContentMap);
				}
				else{
					if(cacheActive == false){//add an empty entry
						placementMap.put(cacheIDMap.get("ID:"+cacheID), new HashMap<Content,Boolean>());
					}
				}	
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return placementMap;
		
		
	}//end createContentPlacementMap
	
	
	/**
	 * Create serverSelectionMap
	 * @param inputPlacementFile root directory to the json server selection file 
	 * @param contentIDMap HashMap<String, Content>
	 * @param cacheIDMap HashMap<String, Content>
	 * @return serverSelectionMap
	 */
	public HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> createServerSelectionMap(String inputServerSelectionFile, 
											HashMap<String, Content> contentIDMap, HashMap<String, Cache> cacheIDMap){
		
		HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> ssMap = new HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputServerSelectionFile));

			JSONObject jsonObject = (JSONObject) obj;
		
			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("serverSelection");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache ID
				//int cacheID = (int)(long)jsonObjectNode.get("nodeID");
				int cacheID = ((Long)jsonObjectNode.get("nodeID")).intValue();
				//Get the total local demand
				//double demand = (double)jsonObjectNode.get("totalDemand");
				//Get the list of contents
				JSONArray listContents = (JSONArray)jsonObjectNode.get("content");
				//Create a new HashMap<Content,Boolean> object 
				HashMap<Content,ArrayList<Object[]>> localSSMap = new HashMap<Content,ArrayList<Object[]>>(); 
				Iterator<JSONObject> iteratorC = listContents.iterator();
				while (iteratorC.hasNext()) {
					JSONObject jsonObjectContent = (JSONObject)iteratorC.next();
					//Get content String ID
					//int contentID = (int)(long)jsonObjectContent.get("contentID");
					int contentID = ((Long)jsonObjectContent.get("contentID")).intValue();
					JSONArray listCacheSevers = (JSONArray)jsonObjectContent.get("node");
					ArrayList<Object[]> listServers = new ArrayList<Object[]>();
					Iterator<JSONObject> iteratorCS = listCacheSevers.iterator();
					while (iteratorCS.hasNext()) {
						JSONObject jsonObjectCS = (JSONObject)iteratorCS.next();
						//Get server-cache String ID
						//int serverCacheID = (int)(long)jsonObjectCS.get("nodeID");
						int serverCacheID = ((Long)jsonObjectCS.get("nodeID")).intValue();
						//Get demand to server-cache String ID
						//double redirectDemand = (double)jsonObjectCS.get("demand");
						double redirectDemand = ((Double)jsonObjectCS.get("demand")).doubleValue();
						Object[] pair = new Object[2];
						pair[0] = cacheIDMap.get("ID:"+serverCacheID);
						pair[1] = redirectDemand;
						listServers.add(pair);
					}
					//Add a new entry in localContentMap
					localSSMap.put(contentIDMap.get("ID:"+contentID),listServers);	
				}
				ssMap.put(cacheIDMap.get("ID:"+cacheID), localSSMap);
			}
	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		return ssMap;
		
	}//end createServerSelectionMap
	
	
	
	/**
	 * Create InNetwContentAvailabilityMap
	 * @param placementFile HashMap<Cache, HashMap<Content,Boolean>> 
	 * @param contentIDMap HashMap<String, Content>
	 * @param cacheIDMap HashMap<String, Content>
	 * @return serverSelectionMap
	 */
	public HashMap<Content,ArrayList<Cache>> createInNetwContentAvailabilityMap(HashMap<Cache, HashMap<Content,Boolean>> placementMap,
			HashMap<String, Content> contentIDMap, HashMap<String, Cache> cacheIDMap){
		
		HashMap<Content,ArrayList<Cache>> inNetwAvailMap = new HashMap<Content,ArrayList<Cache>>();
		
		Iterator<String> iterC = contentIDMap.keySet().iterator();
		String contentID = new String();
		while(iterC.hasNext()){
			contentID = (String)iterC.next();
			ArrayList<Cache> listCaches = new ArrayList<Cache>();
			Iterator<Cache> iter = placementMap.keySet().iterator();
			Cache cache= new Cache();
			while(iter.hasNext()){
				cache = (Cache)iter.next();
				if(placementMap.get(cache).size()>0){
					if(placementMap.get(cache).containsKey(contentIDMap.get(contentID))){
						listCaches.add(cache);
					}
				}
			}
			inNetwAvailMap.put(contentIDMap.get(contentID), listCaches);	
		}
		
		return inNetwAvailMap;
		
	}//end createInNetwContentAvailabilityMap
	
	
	/**
	 * Create RoutingMap
	 * @param inputPathFile root directory to the json path file
	 * @param edgeMap HashMap<int[],Edge>
	 * @param cacheIDMap HashMap<String, Cache>
	 * @return routingMap
	 */
	public HashMap<String, ArrayList<Edge>> createRoutingMap(String inputPathFile, HashMap<String,Edge> edgeMap, 
			HashMap<String, Cache> cacheIDMap){
		
		HashMap<String, ArrayList<Edge>> routingMap = new HashMap<String, ArrayList<Edge>>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputPathFile));

			JSONObject jsonObject = (JSONObject) obj;
		
			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("path");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache-requestor ID
				//int cacheReqID = (int)(long)jsonObjectNode.get("start");
				int cacheReqID = ((Long)jsonObjectNode.get("start")).intValue();
				//Get the cache-server ID
				//int cacheServID = (int)(long)jsonObjectNode.get("end");
				int cacheServID = ((Long)jsonObjectNode.get("end")).intValue();
				//Pair of caches
				String pairCaches = "ID:"+cacheReqID +"-ID:"+ cacheServID;
				//Get the path between the two caches
				String path = (String)jsonObjectNode.get("route");
				//Extract the list of nodes in the path
				ArrayList<Integer> listNodesID = new ArrayList<Integer>();
				StringTokenizer st0 = new StringTokenizer (path,",");
				LinkedList<String> ll0 = new LinkedList<String>();
				while (st0.hasMoreTokens()) {
					ll0.add(st0.nextToken());
				}
				for(int i = 0; i < ll0.size(); i++){
					if(i==0){
						listNodesID.add(Integer.parseInt(ll0.get(i).substring(ll0.get(i).indexOf("[")+1)));
					}
					else{
						if(i==ll0.size()-1){
							listNodesID.add(Integer.parseInt(ll0.get(i).substring(0,ll0.get(i).indexOf("]"))));
						}
						else{
							listNodesID.add(Integer.parseInt(ll0.get(i)));
						}
					}
				}
				//Compute the list of edges between the two caches
				ArrayList<Edge> listEdges = new ArrayList<Edge>();
				for(int j = 0; j < listNodesID.size()-1; j++){
					String edgeExtremities = "ID:" + listNodesID.get(j) + "-ID:" + listNodesID.get(j+1);
					listEdges.add(edgeMap.get(edgeExtremities));
				}
				routingMap.put(pairCaches, listEdges);
			}
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}		
		return routingMap;
	}//end createRoutingMap
	
	
	
	
	/**
	 * Create RouteCharacteristicsMap
	 * @param inputPathFile root directory to the json path file
	 * @param cacheIDMap HashMap<String, Cache>
	 * @return routingMap
	 */
	public HashMap<String, ArrayList<Double>> createRouteCharactericticsMap(String inputPathFile, HashMap<String, Cache> cacheIDMap){
		
		HashMap<String, ArrayList<Double>> routeCharactericticsMap = new HashMap<String, ArrayList<Double>>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputPathFile));

			JSONObject jsonObject = (JSONObject) obj;
		
			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("path");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache-requestor ID
				//int cacheReqID = (int)(long)jsonObjectNode.get("start");
				int cacheReqID = ((Long)jsonObjectNode.get("start")).intValue();
				//Get the cache-server ID
				//int cacheServID = (int)(long)jsonObjectNode.get("end");
				int cacheServID = ((Long)jsonObjectNode.get("end")).intValue();
				//Get the hopcount
				//double hopcount = (int)(long)jsonObjectNode.get("hopcount");
				double hopcount = ((Long)jsonObjectNode.get("hopcount")).doubleValue();
				//Pair of caches
				//Cache[] pairCaches = new Cache[2];
				//pairCaches[0] = cacheIDMap.get("ID:"+cacheReqID);
				//pairCaches[1] = cacheIDMap.get("ID:"+cacheServID);
				String pairCaches = "ID:"+cacheReqID +"-ID:"+ cacheServID;
				
				//List path metrics
				ArrayList<Double> listPathMetrics = new ArrayList<Double>();
				listPathMetrics.add(hopcount);
				
				routeCharactericticsMap.put(pairCaches, listPathMetrics);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}		
					
		return routeCharactericticsMap;
	}//end createRouteCharactericticsMap
	
	
	/**
	 * Check if an edge is in a list of edges
	 * @param listEdges ArrayList<Edge>
	 * @param edge Edge
	 * @return boolean
	 */
	public boolean isEdgeInList(ArrayList<Edge> listEdges, Edge edge){
		boolean is = false;
		for(int i = 0; i < listEdges.size(); i++){
			if(listEdges.get(i).equals(edge)){
				is= true;
				break;
			}
		}
		return is;
	}//end isEdgeInList
	
	
	/**
	 * Create edgeInvolvementMap
	 * @param routingMap HashMap<Cache[], ArrayList<Edge>>
	 * @param edgeMap HashMap<int[],Edge>
	 * @return edgeInvolvementMap
	 */
	public HashMap<Edge, ArrayList<String>> createEdgeInvolvementMap(HashMap<String, ArrayList<Edge>> routingMap, 
			HashMap<String,Edge> edgeMap){
		
		HashMap<Edge, ArrayList<String>> edgeInvolMap = new HashMap<Edge, ArrayList<String>>();
		
		Iterator<String> iterC = edgeMap.keySet().iterator();
		String edgeExtremities = new String();
		while(iterC.hasNext()){
			edgeExtremities = (String)iterC.next();
			Edge edge = edgeMap.get(edgeExtremities);
			ArrayList<String> listPairCaches = new ArrayList<String>();
			Iterator<String> iter = routingMap.keySet().iterator();
			String pairCaches = new String();
			while(iter.hasNext()){
				pairCaches = (String)iter.next();
				if(this.isEdgeInList(routingMap.get(pairCaches),edge)){
					listPairCaches.add(pairCaches);
				}
			}
			edgeInvolMap.put(edge, listPairCaches);
		}
		return edgeInvolMap;
		
	}//end createEdgeInvolvementMap
	
	
	
	
}
