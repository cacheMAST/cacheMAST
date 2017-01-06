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


/**
 * This class represents represents the set of functions to compute caching metrics
 * 
 * @author Daphne Tuncer
 *
 */

public class CachingMetrics {
	
	/**
	 * Constructor
	 */
	public CachingMetrics(){
	}
	
	/**
	 * Compute the average in-network cache capacity
	 * @param cacheMap HashMap<Cache, Cache>
	 * @return average in-network cache capacity
	 */
	public double computeAvgCacheCapacity(HashMap<Cache, Cache> cacheMap){
		double avgCacheCapa = 0;
		Iterator<Cache> iter = cacheMap.keySet().iterator();
		Cache cache = new Cache();
		while(iter.hasNext()){
			cache = (Cache)iter.next();
			avgCacheCapa = avgCacheCapa + cache.getCacheCapacity();
		}
		return (double)Math.round((double)avgCacheCapa/(double)cacheMap.size()*100)/100;
	}
	
	
	/**
	 * Compute the minimum in-network cache capacity
	 * @param cacheMap HashMap<Cache, Cache>
	 * @return minimum in-network cache capacity
	 */
	public double computeMinCacheCapacity(HashMap<Cache, Cache> cacheMap){
		double minCacheCapa = Double.MAX_VALUE;
		Iterator<Cache> iter = cacheMap.keySet().iterator();
		Cache cache = new Cache();
		while(iter.hasNext()){
			cache = (Cache)iter.next();
			if(cache.getCacheCapacity() < minCacheCapa){
				minCacheCapa = cache.getCacheCapacity();
			}
		}
		return minCacheCapa;
	}
	
	
	/**
	 * Compute the maximum in-network cache capacity
	 * @param cacheMap HashMap<Cache, Cache>
	 * @return maximum in-network cache capacity
	 */
	public double computeMaxCacheCapacity(HashMap<Cache, Cache> cacheMap){
		double maxCacheCapa = Double.MIN_VALUE;
		Iterator<Cache> iter = cacheMap.keySet().iterator();
		Cache cache = new Cache();
		while(iter.hasNext()){
			cache = (Cache)iter.next();
			if(cache.getCacheCapacity() > maxCacheCapa){
				maxCacheCapa = cache.getCacheCapacity();
			}
		}
		return maxCacheCapa;
	}
	
	
	/**
	 * Compute the average content size
	 * @param contentMap HashMap<Content, Content>
	 * @return average content size
	 */
	public double computeAvgContentSize(HashMap<Content, Content> contentMap){
		double avgContentSize = 0;
		Iterator<Content> iter = contentMap.keySet().iterator();
		Content content = new Content();
		while(iter.hasNext()){
			content = (Content)iter.next();
			avgContentSize = avgContentSize + content.getContentSize();
		}
		return (double)Math.round((double)avgContentSize/(double)contentMap.size()*100)/100;
	}
		
	/**
	 * Compute the average cache hit ratio at the network level
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @return average cache hit ratio at the network level
	 */
	public double computeAverageCacheHitRatio(HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap){
		
		HashMap<Cache, Double> cacheMap = new HashMap<Cache, Double>();
		
		Iterator<Cache> iter = serverSelectionMap.keySet().iterator();
		Cache cacheReq = new Cache();
		while(iter.hasNext()){
			cacheReq = (Cache)iter.next();
			double cacheHitRatio = 0;
			double localDemand = 0;
			//From serverSelection, get all content transferred between the two caches
			Iterator<Content> iterC = serverSelectionMap.get(cacheReq).keySet().iterator();
			Content content = new Content();
			while(iterC.hasNext()){
				content = (Content)iterC.next();
				//Check whether the content is retrieved from cacheServer
				for(int k = 0; k < serverSelectionMap.get(cacheReq).get(content).size(); k++){
					if(((Cache)serverSelectionMap.get(cacheReq).get(content).get(k)[0]).equals(cacheReq)){
						//localDemand = localDemand + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1];
						localDemand = localDemand + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue();
						//cacheHitRatio = cacheHitRatio + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1];
						cacheHitRatio = cacheHitRatio + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue();
					}	
					else{
						//localDemand = localDemand + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1];
						localDemand = localDemand + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue();
					}
				}
			}//end while all local contents
			
			cacheMap.put(cacheReq, (double)cacheHitRatio/(double)localDemand);
			
		}//end while all requestor caches
		
		double globalCHR = 0;
		Iterator<Cache> iterC = cacheMap.keySet().iterator();
		Cache cache = new Cache();
		while(iterC.hasNext()){
			cache = (Cache)iterC.next();
			globalCHR = globalCHR + cacheMap.get(cache);
		}
		
		return (double)Math.round((double)globalCHR/(double)cacheMap.size() * 100) / 100;
		
	}//end computeAverageCacheHitRatio
	
	
	
	/**
	 * Compute the local cache hit ratio at each cache
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @return map of local cache hit ratios
	 */
	public HashMap<String,ArrayList<Double>> computeLocalCacheHitRatio(HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap){
		
		HashMap<String,ArrayList<Double>> cacheMap = new HashMap<String,ArrayList<Double>>();
		
		Iterator<Cache> iter = serverSelectionMap.keySet().iterator();
		Cache cacheReq = new Cache();
		while(iter.hasNext()){
			cacheReq = (Cache)iter.next();
			double cacheHitRatio = 0.0;
			double localDemand = 0.0;
			//From serverSelection, get all content transferred between the two caches
			Iterator<Content> iterC = serverSelectionMap.get(cacheReq).keySet().iterator();
			Content content = new Content();
			while(iterC.hasNext()){
				content = (Content)iterC.next();
				//Check whether the content is retrieved from cacheServer
				for(int k = 0; k < serverSelectionMap.get(cacheReq).get(content).size(); k++){
					if(((Cache)serverSelectionMap.get(cacheReq).get(content).get(k)[0]).equals(cacheReq)){
						//localDemand = localDemand + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1];
						localDemand = localDemand + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue();
						//cacheHitRatio = cacheHitRatio + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1];
						cacheHitRatio = cacheHitRatio + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue();
					}	
					else{
						//localDemand = localDemand + (double)serverSelectionMap.get(cacheReq).get(content).get(k)[1];
						localDemand = localDemand + ((Double)serverSelectionMap.get(cacheReq).get(content).get(k)[1]).doubleValue();
					}
				}
			}//end while all local contents
			ArrayList<Double> listDouble = new ArrayList<Double>();
			
			listDouble.add((double)Math.round((double)cacheHitRatio/(double)localDemand * 100) / 100);
			cacheMap.put("ID:"+cacheReq.getCacheID(), listDouble);
			
		}//end while all requestor caches
		
		return cacheMap;
		
	}//end computeLocalCacheHitRatio
	
	
	
	/**
	 * Compute the average occupancy of the caching space at the network level
	 * @param contentPlacementMap HashMap<Cache, HashMap<Content,Boolean>>
	 * @return average occupancy of the caching space at the network level
	 */
	public double computeAverageCacheOccupancy(HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap){
		
		double globalOccupancy = 0;
		int div = 0;
		Iterator<Cache> iterC = contentPlacementMap.keySet().iterator();
		Cache cache = new Cache();
		while(iterC.hasNext()){
			cache = (Cache)iterC.next();
			double localOccupancy = 0;
			if(contentPlacementMap.get(cache).size()>0){
				Iterator<Content> iterCC = contentPlacementMap.get(cache).keySet().iterator();
				Content content = new Content();
				while(iterCC.hasNext()){
					content = (Content)iterCC.next();
					localOccupancy = localOccupancy + content.getContentSize();
				} 
				globalOccupancy = globalOccupancy + (double)localOccupancy / (double)cache.getCacheCapacity() * 100;
			}
		}
		
		return (double)Math.round((double)globalOccupancy/(double)contentPlacementMap.size() * 100) / 100;	
		
	}//end computeAverageCacheOccupancy
	
	
	
	/**
	 * Compute the local occupancy of each in-network cache
	 * @param contentPlacementMap HashMap<Cache, HashMap<Content,Boolean>>
	 * @param cacheStatsMap HashMap<String,Double>
	 * @return map of local occupancy 
	 */
	public HashMap<String,ArrayList<Double>> computeLocalCacheOccupancy(HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap, HashMap<String,ArrayList<Double>> cacheStatsMap){
		
		Iterator<Cache> iterC = contentPlacementMap.keySet().iterator();
		Cache cache = new Cache();
		while(iterC.hasNext()){
			cache = (Cache)iterC.next();
			double localOccupancy = 0;
			int nbContents = 0;
			if(contentPlacementMap.get(cache).size() > 0.0){
				Iterator<Content> iterCC = contentPlacementMap.get(cache).keySet().iterator();
				Content content = new Content();
				while(iterCC.hasNext()){
					content = (Content)iterCC.next();
					localOccupancy = localOccupancy + content.getContentSize();
					nbContents++;
				} 
			}
			localOccupancy = (double)localOccupancy / (double)cache.getCacheCapacity() * 100;
			cacheStatsMap.get("ID:"+cache.getCacheID()).add((double)Math.round(localOccupancy*100)/100);
			//Also add the cache capacity
			cacheStatsMap.get("ID:"+cache.getCacheID()).add(cache.getCacheCapacity());
			//Finally add the number of locally cached contents
			cacheStatsMap.get("ID:"+cache.getCacheID()).add((double)nbContents);
		}
		
		return cacheStatsMap;	
		
	}//end computeAverageCacheOccupancy
	
	
	/**
	 * Compute the average content replication degree at the network level
	 * @param inNetwContentAvailabilityMap HashMap<Content,ArrayList<Cache>>
	 * @return average content replication degree at the network level
	 */
	public double computeAverageContentReplicationDegree(HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap){
		
		double replicationDegree = 0;
		Iterator<Content> iterC = inNetwContentAvailabilityMap.keySet().iterator();
		Content content = new Content();
		while(iterC.hasNext()){
			content = (Content)iterC.next();
			replicationDegree = replicationDegree + inNetwContentAvailabilityMap.get(content).size();
		}
		return ((double)Math.round((double)replicationDegree/(double)inNetwContentAvailabilityMap.size() * 100) / 100);
	}//end computeAverageContentReplicationDegree
	
	
	/**
	 * Log caching metrics in JSON format
	 * @param serverSelectionMap HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>
	 * @param contentPlacementMap HashMap<Cache, HashMap<Content,Boolean>>
	 * @param inNetwContentAvailabilityMap HashMap<Content,ArrayList<Cache>>
	 * @param cacheMap HashMap<Cache, Cache>
	 * @param cacheMetricsFile String 
	 */
	public void logJSONCachingMetrics(HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap, HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap,
								HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap, HashMap<Cache, Cache> cacheMap, String cacheMetricsFile){
		
		double avgCHR = computeAverageCacheHitRatio(serverSelectionMap);
		double avgCO = computeAverageCacheOccupancy(contentPlacementMap);
		double avgCR = computeAverageContentReplicationDegree(inNetwContentAvailabilityMap);
		double avgCacheCapacity = computeAvgCacheCapacity(cacheMap);
		double minCacheCapa = computeMinCacheCapacity(cacheMap);
		double maxCacheCapa = computeMaxCacheCapacity(cacheMap);
		HashMap<String,ArrayList<Double>> cacheStatsMap = computeLocalCacheHitRatio(serverSelectionMap);
		cacheStatsMap = computeLocalCacheOccupancy(contentPlacementMap, cacheStatsMap);
		int nbCaches = cacheMap.size();
		
		JSONObject objCacheStats = new JSONObject();
		objCacheStats.put("avgCacheHitRatio", avgCHR);
		objCacheStats.put("avgCacheOccupancy", avgCO);
		objCacheStats.put("avgContentReplicationDegree", avgCR);
		objCacheStats.put("avgCacheCapacity", avgCacheCapacity);
		objCacheStats.put("nbCaches", nbCaches);
		objCacheStats.put("minCacheCapacity", minCacheCapa);
		objCacheStats.put("maxCacheCapacity", maxCacheCapa);
		
		JSONArray listCacheStats = new JSONArray();
		Iterator<String> iterC = cacheStatsMap.keySet().iterator();
		String s = new String();
		while(iterC.hasNext()){
			s = (String)iterC.next();
			int cacheID = Integer.parseInt(s.substring(3));
			double localCHR = cacheStatsMap.get(s).get(0);
			double localCO = cacheStatsMap.get(s).get(1);
			double localCCapa = cacheStatsMap.get(s).get(2);
			double localNbContents = cacheStatsMap.get(s).get(3);
			JSONObject objCache = new JSONObject();
			objCache.put("cacheID", cacheID);
			objCache.put("localCHR", localCHR);
			objCache.put("localCO", localCO);
			objCache.put("localCCapa", localCCapa);
			objCache.put("localNbContents", localNbContents);
			listCacheStats.add(objCache);
		}
		objCacheStats.put("cacheStats", listCacheStats);
		
		try {
			FileWriter file = new FileWriter(cacheMetricsFile);
			file.write(objCacheStats.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}//end logJSONCachingMetrics
	

}
