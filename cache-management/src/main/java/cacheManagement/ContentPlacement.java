package cacheManagement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * This class represents content placement approaches
 * 
 * @author Daphne Tuncer
 *
 */

public class ContentPlacement {

	/**
	 * Constructor
	 */
	public ContentPlacement(){
	}
	
	
	/**
	 * Check that all content items in the map are marked as cached
	 * @param contentAvailabilityMap HashMap<Content,Boolean>
	 * @return true if all content items in the map are marked as cached
	 */
	public boolean areAllContentItemsCached(HashMap<Content,Boolean> contentAvailabilityMap){
		boolean allContentCached = true; 
		Iterator<Content> iter = contentAvailabilityMap.keySet().iterator();
		Content c = new Content();
		while(iter.hasNext()){
			c = (Content)iter.next();
			if(contentAvailabilityMap.get(c)==false){
				allContentCached = false;
				break;
			}
		}
		return allContentCached;
	}
	
	/**
	 * Content placement computation based on Locally Popularity Strategy (LPS) algorithm
	 * @param demandMap HashMap<Cache, HashMap<Content,Double>>
	 * @param cacheStatisticsMap HashMap<Cache, Double[]>
	 * @param nbCaches int
	 * @param contentMap HashMap<Content, Content>
	 * @return content placement map
	 */
	public HashMap<Cache, HashMap<Content,Boolean>> lpsBasedContentPlacement(HashMap<Cache, HashMap<Content,Double>> demandMap, 
			HashMap<Cache, Double[]> cacheStatisticsMap, int nbCaches, HashMap<Content, Content> contentMap){
		
		HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap = new HashMap<Cache, HashMap<Content,Boolean>>();
		
		//Create a map indicating the availability of content in the network caches
		//Originally none is cached
		HashMap<Content,Boolean> contentAvailabilityMap = new HashMap<Content,Boolean>();
		Iterator<Content> iter = contentMap.keySet().iterator();
		Content c = new Content();
		while(iter.hasNext()){
			c = (Content)iter.next();
			contentAvailabilityMap.put(c, false);
		}
		
		//Create a map indicating whether a decision-making is still active
		//Originally they are all active
		HashMap<String,Boolean> decisionPointStatusMap = new HashMap<String,Boolean>();
		Iterator<Cache> iterCDM = cacheStatisticsMap.keySet().iterator();
		Cache ccDM = new Cache();
		while(iterCDM.hasNext()){
			ccDM = (Cache)iterCDM.next();
			decisionPointStatusMap.put("ID"+ccDM.getCacheID(), true);
		}
		
		//Create a map associating each cache to the list of content requested locally ranked by decreasing 
		//order of demand
		HashMap<Cache,List<ContentCopy>> orderDemandMap = new HashMap<Cache,List<ContentCopy>>();
		Iterator<Cache> iterC = demandMap.keySet().iterator();
		Cache cache = new Cache();
		while(iterC.hasNext()){
			cache = (Cache)iterC.next();
			List<ContentCopy> copyList = new ArrayList();
			HashMap<Content,Double> localContentMap = demandMap.get(cache);
			Iterator<Content> iterCC = localContentMap.keySet().iterator();
			Content cc = new Content();
			while(iterCC.hasNext()){
				cc = (Content)iterCC.next();
				copyList.add(new ContentCopy(cc,localContentMap.get(cc)));
			}
			Collections.sort(copyList);
			orderDemandMap.put(cache, copyList);
		}
	
		//Phase 1 of the placement decisions
		boolean canContinuePhaseOne = true;
		int nbDecisionMakingPoints = nbCaches;
		while(canContinuePhaseOne){
			Iterator<Cache> iterDM = orderDemandMap.keySet().iterator();
			Cache cacheDM = new Cache();
			while(iterDM.hasNext()){
				cacheDM = (Cache)iterDM.next();
				//Check if the cache is still an active decision-making point
				if(decisionPointStatusMap.get("ID"+cacheDM.getCacheID())==true){
					Content contentT = new Content();
					boolean  zeroDemand = false;
					boolean zeroCapacity = false;
					//Get the first content in the ordered list of ContentCopy objects that is not already cached and for which demand is not null
					for(int j = 0; j < orderDemandMap.get(cacheDM).size(); j++){
						if(orderDemandMap.get(cacheDM).get(j).getDemand()>0 && cacheDM.getCacheCapacity()>0.0){
							//If not already cached somewhere in the network, cache it here
							contentT = orderDemandMap.get(cacheDM).get(j).getContent();
							if(contentAvailabilityMap.get(contentT)==false){
								if(contentPlacementMap.containsKey(cacheDM)){
									contentPlacementMap.get(cacheDM).put(contentT,true);
									contentAvailabilityMap.put(contentT, true);
								}
								else{
									if(!contentPlacementMap.containsKey(cacheDM)){
										contentPlacementMap.put(cacheDM, new HashMap<Content,Boolean>());
										contentPlacementMap.get(cacheDM).put(contentT,true);
										contentAvailabilityMap.put(contentT, true);
									}

								}
								//Remove the content from the local list for not being considered at next step
								orderDemandMap.get(cacheDM).remove(j);
								double occupancy = contentT.getContentSize() + cacheStatisticsMap.get(cacheDM)[2];  
								cacheStatisticsMap.get(cacheDM)[2] = occupancy;
								break;
							}//if the content is not already cached in the network
						}
						else{
							decisionPointStatusMap.put("ID"+cacheDM.getCacheID(), false);
							//Create a default empty entry
							contentPlacementMap.put(cacheDM, new HashMap<Content,Boolean>());
							zeroDemand = true;
							zeroCapacity = true;
							break;
						}
					}//end for
					//Update the occupancy status of cacheDM
					if(zeroDemand == false && zeroCapacity == false){
						if((cacheStatisticsMap.get(cacheDM)[0] - cacheStatisticsMap.get(cacheDM)[2]) == 0){
							nbDecisionMakingPoints = nbDecisionMakingPoints - 1;
						}	
					}
					else{//if no decisions were taken by cacheDM at that iteration, it cannot take any more decisions
						nbDecisionMakingPoints = nbDecisionMakingPoints - 1;
					}
				}
				else{//decrement the number of decision making points
					nbDecisionMakingPoints = nbDecisionMakingPoints - 1;
				}
			}//end while cacheDM
		
			//Conditions to stop the loop
			//Condition 1: there is one copy of each content in the network 
			//Condition 2: the number of decision-making points that can still take decisions is not zero
			if(nbDecisionMakingPoints<=0 || this.areAllContentItemsCached(contentAvailabilityMap)==true){
				canContinuePhaseOne = false;
			}
			
		}//end while canContinuePhaseOne
		
		//Phase 2 of the placement decisions
		Iterator<Cache> iterDM2 = orderDemandMap.keySet().iterator();
		Cache cacheDM2 = new Cache();
		while(iterDM2.hasNext()){
			cacheDM2 = (Cache)iterDM2.next();
			Content contentT = new Content();
			//Get content items in the ordered list of ContentCopy objects that are not already cached locally			
			for(int j = 0; j < orderDemandMap.get(cacheDM2).size(); j++){
				if(orderDemandMap.get(cacheDM2).get(j).getDemand()>0 && cacheDM2.getCacheCapacity()>0.0){
					contentT = orderDemandMap.get(cacheDM2).get(j).getContent();
					if(contentPlacementMap.containsKey(cacheDM2)){
						if(!contentPlacementMap.get(cacheDM2).containsKey(contentT)){
							double space =  cacheStatisticsMap.get(cacheDM2)[0] - (cacheStatisticsMap.get(cacheDM2)[2]+contentT.getContentSize()); 
							if(space>=0){
								contentPlacementMap.get(cacheDM2).put(contentT,true);
							}
						}
					}
					else{
						if(!contentPlacementMap.containsKey(cacheDM2)){
							double space =  cacheStatisticsMap.get(cacheDM2)[0] - (cacheStatisticsMap.get(cacheDM2)[2]+contentT.getContentSize()); 
							if(space>=0){
								contentPlacementMap.put(cacheDM2, new HashMap<Content,Boolean>());
								contentPlacementMap.get(cacheDM2).put(contentT,true);
							}
						}
					}
					//Update the occupancy status of cacheDM
					double occupancy = contentT.getContentSize() + cacheStatisticsMap.get(cacheDM2)[2];  
					cacheStatisticsMap.get(cacheDM2)[2] = occupancy;
				}
				else{
					break;
				}
			}//end for
		}//end while cacheDM

		return contentPlacementMap;
		
	}//end lpsBasedContentPlacement
	
	/**
	 * Log the content placement map in JSON format
	 * @param contentPlacementMap HashMap<Cache, HashMap<Content,Boolean>>
	 * @param contentPlacementConfigurationFile String root directory to the log file
	 */
	public void logContentPlacementConfiguration(HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap, 
							String contentPlacementConfigurationFile){
			
		JSONArray listCacheRequestors = new JSONArray();
		Iterator<Cache> iterC = contentPlacementMap.keySet().iterator();
		Cache cacheReq = new Cache();
		while(iterC.hasNext()){
			cacheReq = (Cache)iterC.next();
			JSONArray listLocalContent = new JSONArray();
			if(contentPlacementMap.get(cacheReq).size() > 0){
				Iterator<Content> iterCC = contentPlacementMap.get(cacheReq).keySet().iterator();
				Content content = new Content();
				while(iterCC.hasNext()){
					JSONObject objContent = new JSONObject();
					content = (Content)iterCC.next();
					objContent.put("contentID", content.getContentID());
					listLocalContent.add(objContent);
				}//end while all local content
				JSONObject objCacheReq = new JSONObject();
				objCacheReq.put("nodeID", cacheReq.getCacheID());
				objCacheReq.put("cacheActive", true);
				objCacheReq.put("content", listLocalContent);
				listCacheRequestors.add(objCacheReq);
			}
			else{
				JSONObject objCacheReq = new JSONObject();
				objCacheReq.put("nodeID", cacheReq.getCacheID());
				objCacheReq.put("cacheActive", false);
				listCacheRequestors.add(objCacheReq);
			}
		}//end while all cache requestors
		
		JSONObject objPlacement = new JSONObject();
		objPlacement.put("placement", listCacheRequestors);
		
		try {
			FileWriter file = new FileWriter(contentPlacementConfigurationFile);
			file.write(objPlacement.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//end logContentPlacementConfiguration
	
	
	
	
}
