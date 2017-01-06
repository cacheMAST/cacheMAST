package cacheManagement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import networkGraph.Edge;

/**
 * This class represents server selection approaches
 * 
 * @author Daphne Tuncer
 *
 */

public class ServerSelection {
	
	/**
	 * Constructor
	 */
	public ServerSelection(){
	}
	
	
	/**
	 * Get the closest cache-server in terms of distance
	 * @param cacheRequestor Cache
	 * @param listCaches ArrayList<Cache>
	 * @param routingMap HashMap<Cache[],ArrayList<Edge>>
	 * @return Cache
	 */
	public Cache getClostestCacheServerLocation(Cache cacheRequestor, ArrayList<Cache> listCaches, 
							HashMap<String,ArrayList<Edge>> routingMap, HashMap<String, Cache> cacheIDMap){
		int distance = Integer.MAX_VALUE; 
		Cache closestCacheServer = new Cache();
		for(int i = 0; i < listCaches.size(); i++){
			//If locally available, the closest cache is the cache-requestor
			if(cacheRequestor.getCacheID()==listCaches.get(i).getCacheID()){
				closestCacheServer = cacheRequestor;
				break;
			}
			else{
				if(cacheRequestor.getCacheID()!=listCaches.get(i).getCacheID()){
					String pairCaches = "ID:"+cacheRequestor.getCacheID() + "-ID:" + listCaches.get(i).getCacheID();
					if(routingMap.containsKey(pairCaches)){	
						if(routingMap.get(pairCaches).size() < distance){
							closestCacheServer = cacheIDMap.get("ID:"+pairCaches.substring(pairCaches.indexOf("-")+4));
							distance = routingMap.get(pairCaches).size();
						}
					}
				}
			}
		}
		return closestCacheServer;
	}//end getClostestCacheServerLocation
	
	
	/**
	 * Server selection based on the distance factor: the cache-sever location is the closest one 
	 * @param routingMap HashMap<Cache[], ArrayList<Edge>>
	 * @param inNetwContentAvailabilityMap HashMap<Content,ArrayList<Cache>>
	 * @param demandMap HashMap<Cache, HashMap<Content,Double>>
	 * @return serverSelectionMap based on closest distance
	 */
	public HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> closestDistanceBasedServerSelection(HashMap<String, ArrayList<Edge>> routingMap, 
									HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap,
										HashMap<Cache, HashMap<Content,Double>> demandMap, HashMap<String, Cache> cacheIDMap){
		
		HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap = new HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>();
			
		Iterator<Cache> iterC = demandMap.keySet().iterator();
		Cache cacheReq = new Cache();
		while(iterC.hasNext()){
			cacheReq = (Cache)iterC.next();
			//Create the local redirection map
			HashMap<Content,ArrayList<Object[]>> localRedirectionMap = new  HashMap<Content,ArrayList<Object[]>>();
			Iterator<Content> iter = demandMap.get(cacheReq).keySet().iterator();
			Content content = new Content();
			while(iter.hasNext()){
				content = (Content)iter.next();
				//Get the list of caches from where the content is available
				ArrayList<Cache> listCaches = inNetwContentAvailabilityMap.get(content);
				//Find the server cache which is closest to requestor cache
				Cache closestCacheServer = this.getClostestCacheServerLocation(cacheReq, listCaches, routingMap, cacheIDMap);
				if(closestCacheServer.getCacheID()!=0){
					//Get the demand for content at cacheReq
					double demand = demandMap.get(cacheReq).get(content);
					//Create 2-tuple objects and ArrayList of 2-tuples
					Object[] tuple = new Object[2];
					tuple[0] = closestCacheServer;
					tuple[1] = demand;
					ArrayList<Object[]> listTuples = new ArrayList<Object[]>();
					listTuples.add(tuple);
					//Add an entry in the local redirection map
					localRedirectionMap.put(content, listTuples);
				}
			}
			serverSelectionMap.put(cacheReq, localRedirectionMap);
		}
	
		return serverSelectionMap;
		
	}//end closestDistanceBasedServerSelection
	
		
	/**
	 * Server selection based on round robin: the demand for a content is equally divided between all cache-sever locations  
	 * @param inNetwContentAvailabilityMap HashMap<Content,ArrayList<Cache>>
	 * @param demandMap HashMap<Cache, HashMap<Content,Double>>
	 * @return serverSelectionMap based on round robin
	 */
	public HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> roundRobinBasedServerSelection(HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap,
										HashMap<Cache, HashMap<Content,Double>> demandMap){
		
		HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap = new HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>();
		
		Iterator<Cache> iterC = demandMap.keySet().iterator();
		Cache cacheReq = new Cache();
		while(iterC.hasNext()){
			cacheReq = (Cache)iterC.next();
			//Create the local redirection map
			HashMap<Content,ArrayList<Object[]>> localRedirectionMap = new  HashMap<Content,ArrayList<Object[]>>();
			Iterator<Content> iter = demandMap.get(cacheReq).keySet().iterator();
			Content content = new Content();
			while(iter.hasNext()){
				content = (Content)iter.next();
				//Get the list of caches from where the content is available
				ArrayList<Cache> listCaches = inNetwContentAvailabilityMap.get(content);
				//Get the demand for content at cacheReq
				double demand = demandMap.get(cacheReq).get(content);
				//Create a tuple for all caches and equally divide the volume of requests to be redirected to each cache 
				ArrayList<Object[]> listTuples = new ArrayList<Object[]>();
				for(int i = 0; i < listCaches.size(); i++){
					Cache cacheServer = listCaches.get(i);
					if(cacheServer.getCacheID()!=0){
						//Create 2-tuple objects and ArrayList of 2-tuples
						Object[] tuple = new Object[2];
						tuple[0] = cacheServer;
						tuple[1] = (double)demand/listCaches.size();
						listTuples.add(tuple);
					}
				}
				//Add an entry in the local redirection map
				if(listTuples.size()>0){
					localRedirectionMap.put(content, listTuples);	
				}
			}
			serverSelectionMap.put(cacheReq, localRedirectionMap);
		}
	
		return serverSelectionMap;
		
	}//end roundRobinBasedServerSelection
	
	/**
	 * Log server selection configuration
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @param serverSelectionConfigurationFile String root directory to the configuration file
	 */
	public void logServerSelectionConfiguration(HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap,
								String serverSelectionConfigurationFile){
		
		JSONArray listCacheRequestors = new JSONArray();
		Iterator<Cache> iterC = serverSelectionMap.keySet().iterator();
		Cache cacheReq = new Cache();
		while(iterC.hasNext()){
			cacheReq = (Cache)iterC.next();
			JSONArray listLocalContent = new JSONArray();
			Iterator<Content> iterCC = serverSelectionMap.get(cacheReq).keySet().iterator();
			Content content = new Content();
			while(iterCC.hasNext()){
				JSONObject objContent = new JSONObject();
				content = (Content)iterCC.next();
				JSONArray listCacheServers = new JSONArray();
				for(int i = 0; i < serverSelectionMap.get(cacheReq).get(content).size(); i++){
					JSONObject objCacheRequestor = new JSONObject();
					objCacheRequestor.put("nodeID", ((Cache)serverSelectionMap.get(cacheReq).get(content).get(i)[0]).getCacheID());
					//objCacheRequestor.put("demand", (double)serverSelectionMap.get(cacheReq).get(content).get(i)[1]);
					objCacheRequestor.put("demand", ((Double)serverSelectionMap.get(cacheReq).get(content).get(i)[1]).doubleValue());
					listCacheServers.add(objCacheRequestor);
				}
				objContent.put("contentID", content.getContentID());
				objContent.put("node", listCacheServers);
				listLocalContent.add(objContent);
			}//end while all local content
			JSONObject objCacheReq = new JSONObject();
			objCacheReq.put("nodeID", cacheReq.getCacheID());
			objCacheReq.put("content", listLocalContent);
			listCacheRequestors.add(objCacheReq);
		}//end while all cache requestors
		
		JSONObject objPlacement = new JSONObject();
		objPlacement.put("serverSelection", listCacheRequestors);
		
		try {
			FileWriter file = new FileWriter(serverSelectionConfigurationFile);
			file.write(objPlacement.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}//end logServerSelectionConfiguration
	

}
