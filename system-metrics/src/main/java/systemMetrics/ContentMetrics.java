package systemMetrics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import cacheManagement.Cache;
import cacheManagement.Content;
import cacheManagement.ContentCopy;
import networkGraph.Edge;

/**
 * This class represents represents the set of functions to compute content metrics
 * 
 * @author Daphne Tuncer
 *
 */

public class ContentMetrics {
	
	/**
	 * Constructor
	 */
	public ContentMetrics(){
	}
	
	/**
	 * Compute the in-network replication degree of each content
	 * @param inNetwContentAvailabilityMap
	 * @return map of content metrics
	 */
	public HashMap<String,ArrayList<Double>> computeContentInNetwReplicationDegree(HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap){
		
		HashMap<String,ArrayList<Double>> contentMetricsMap = new HashMap<String,ArrayList<Double>>();
		
		Iterator<Content> iter = inNetwContentAvailabilityMap.keySet().iterator();
		Content c = new Content();
		while(iter.hasNext()){
			c = (Content)iter.next();
			ArrayList<Double> listDouble = new ArrayList<Double>();
			listDouble.add((double)inNetwContentAvailabilityMap.get(c).size());
			contentMetricsMap.put("ID:"+c.getContentID(), listDouble);
		}
		return contentMetricsMap;
	}//end computeContentInNetwReplicationDegree
	
	/**
	 * Compute the popularity rank of each content requested in the network
	 * @param demandMap HashMap<Cache, HashMap<Content,Double>>
	 * @param contentMetricsMap HashMap<String,ArrayList<Double>>
	 * @param contentMap HashMap<Content, Content>
	 * @return map of content metrics
	 */
	public HashMap<String,ArrayList<Double>> computeContentRank(HashMap<Cache, HashMap<Content,Double>> demandMap, HashMap<String,ArrayList<Double>> contentMetricsMap, HashMap<Content, Content> contentMap){
		
		List<ContentCopy> copyList = new ArrayList();
		
		Iterator<Content> iter = contentMap.keySet().iterator();
		Content content = new Content();
		while(iter.hasNext()){
			content = (Content)iter.next();
			double contentDemand = 0;
			Iterator<Cache> iterD = demandMap.keySet().iterator();
			Cache c = new Cache();
			while(iterD.hasNext()){
				c = (Cache)iterD.next();
				//If there is an entry for current content
				if(demandMap.containsKey(content)){
					contentDemand = contentDemand + demandMap.get(c).get(content);
				}
			}
			copyList.add(new ContentCopy(content,contentDemand));
		}
		//Sort the content lists by decreasing order of demand
		Collections.sort(copyList);
		
		for(int k = 0; k < copyList.size(); k++){
			contentMetricsMap.get("ID:"+copyList.get(k).getContent().getContentID()).add((double)k);
		}
		
		return contentMetricsMap;
		
	}//end computeContentRank
	
	
	/**
	 * Compute average content size
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
		
		return (double)Math.round((double)avgContentSize/(double)contentMap.size() * 100) / 100;
		
	}//end computeAvgContentSize
	
	
	
	/**
	 * Compute minimum content size
	 * @param contentMap HashMap<Content, Content>
	 * @return minimum content size
	 */
	public double computeMinContentSize(HashMap<Content, Content> contentMap){
		
		double minContentSize = Double.MAX_VALUE;
		
		Iterator<Content> iter = contentMap.keySet().iterator();
		Content content = new Content();
		while(iter.hasNext()){
			content = (Content)iter.next();
			if(content.getContentSize() < minContentSize){
				minContentSize = content.getContentSize();
			}
		}
		
		return minContentSize;
		
	}//end computeMinContentSize
	
	
	/**
	 * Compute maximum content size
	 * @param contentMap HashMap<Content, Content>
	 * @return maximum content size
	 */
	public double computeMaxContentSize(HashMap<Content, Content> contentMap){
		
		double maxContentSize = Double.MIN_VALUE;
		
		Iterator<Content> iter = contentMap.keySet().iterator();
		Content content = new Content();
		while(iter.hasNext()){
			content = (Content)iter.next();
			if(content.getContentSize() > maxContentSize){
				maxContentSize = content.getContentSize();
			}
		}
		
		return maxContentSize;
		
	}//end computeMaxContentSize
	
	
	
	/**
	 * Compute the catalog size
	 * @param contentMap HashMap<Content, Content>
	 * @return catalog size
	 */
	public int computeCatalogSize(HashMap<Content, Content> contentMap){
		return contentMap.size();
	}//end computeCatalogSize
	
	/**
	 * Log content metrics in JSON format
	 * @param inNetwContentAvailabilityMap HashMap<Content,ArrayList<Cache>>
	 * @param demandMap HashMap<Cache, HashMap<Content,Double>>
	 * @param contentMap HashMap<Content, Content>
	 * @param contentMetrics String output content metrics file (in JSON)
	 */
	public void logJSONContentMetrics(HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap, HashMap<Cache, HashMap<Content,Double>> demandMap, 
										HashMap<Content, Content> contentMap, String contentMetrics){
		
		HashMap<String,ArrayList<Double>> contentMetricsMap = computeContentInNetwReplicationDegree(inNetwContentAvailabilityMap);
		computeContentRank(demandMap, contentMetricsMap, contentMap);
		double avgContentSize = computeAvgContentSize(contentMap);
		int nbContents = computeCatalogSize(contentMap);
		double minContentSize = computeMinContentSize(contentMap);
		double maxContentSize = computeMaxContentSize(contentMap);
		
		JSONObject objContentStats = new JSONObject();
		objContentStats.put("avgContentSize", avgContentSize);
		objContentStats.put("nbContents", nbContents);
		objContentStats.put("minContentSize", minContentSize);
		objContentStats.put("maxContentSize", maxContentSize);
		
		JSONArray listContentStats = new JSONArray();
		Iterator<String> iterCC = contentMetricsMap.keySet().iterator();
		String cc = new String();
		while(iterCC.hasNext()){
			cc = (String)iterCC.next();
			int contentID = Integer.parseInt(cc.substring(3));
			double contentRepDeg = contentMetricsMap.get(cc).get(0);
			double contentRank = contentMetricsMap.get(cc).get(1);
			JSONObject objContent = new JSONObject();
			objContent.put("contentID", contentID);
			objContent.put("contentRepDeg", contentRepDeg);
			objContent.put("contentRank", contentRank);
			listContentStats.add(objContent);
		}
		
		objContentStats.put("contentStats", listContentStats);
		
		try {
			FileWriter file = new FileWriter(contentMetrics);
			file.write(objContentStats.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}//end logJSONContentMetrics
	
	
	
	

}
