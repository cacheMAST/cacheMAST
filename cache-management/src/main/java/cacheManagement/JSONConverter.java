package cacheManagement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import networkGraph.Edge;

/**
 * This class represents the set of methods to convert JSON files into proper format for the CacheMAsT tool
 * 
 * @author Daphne Tuncer
 *
 */

public class JSONConverter {
	
	/**
	 * Constructor
	 */
	public JSONConverter(){
	}
	
	
	/**
	 * Create a map of the demand based on the original json file
	 * @param inputDemandFile root directory to the json demand file 
	 * @return return the created demandMap
	 */
	public HashMap<String[], HashMap<String,Double>> originalDemandMap(String inputDemandFile){
		
		HashMap<String[], HashMap<String,Double>> demandMap = new HashMap<String[], HashMap<String,Double>>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputDemandFile));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("networkDemand");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get totalDemand
				String totalDemand = "" + (double)Math.round((double)Double.parseDouble((String)jsonObjectNode.get("totalDemand"))*100)/100;
				//Get the cache ID
				String cacheID = (String)jsonObjectNode.get("nodeID");
				if(!cacheID.equals("2147483647")){
					//Create an array with this two parameters
					String[] arrayKey = new String[2];
					arrayKey[0] = totalDemand;
					arrayKey[1] = cacheID;
					//Get the list of contents
					JSONArray listContents = (JSONArray)jsonObjectNode.get("contentDemand");
					//Create a new HashMap<Content,Double> object for the local demand associated with each content 
					HashMap<String,Double> localContentMap = new HashMap<String,Double>(); 
					Iterator<JSONObject> iteratorC = listContents.iterator();
					while (iteratorC.hasNext()){
						JSONObject jsonObjectContent = (JSONObject)iteratorC.next();
						//Get content String ID
						String contentID = (String)jsonObjectContent.get("contentID");
						//Get local demand 
						double demand = (double)Math.round((double)Double.parseDouble((String)jsonObjectContent.get("demand"))*100)/100;
						//Add a new entry in localContentMap
						localContentMap.put("ID:"+contentID,demand);	
					}
					demandMap.put(arrayKey, localContentMap);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return demandMap;
		
	}//end originalDemandMap
	
	
	/**
	 * Create a map of the demand based on the original json file
	 * @param inputDemandFile root directory to the json demand file 
	 * @return return the created demandMap
	 */
	public String[] extractAlphaBetaValues(String inputDemandFile){
		
		String[] demandParam = new String[2];
		
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader(inputDemandFile));
			JSONObject jsonObject = (JSONObject) obj;
			String alpha = (String)jsonObject.get("alpha");
			String beta = (String)jsonObject.get("beta");
			demandParam[0] = alpha;
			demandParam[1] = beta;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return demandParam;
		
	}//end extractAlphaBetaValues
	
	/**
	 * Method to log the converted demand into the proper JSON format
	 * @param ouputDemandFile String full-access path to the output file
	 * @param demandParam String[] demand parameters
	 * @param demandMap HashMap<String[], HashMap<String,Double>> demand map
	 */
	public void logDemandMap(String ouputDemandFile, String[] demandParam, HashMap<String[], HashMap<String,Double>> demandMap){
		
		JSONObject objDemandStats = new JSONObject();
		objDemandStats.put("alpha", demandParam[0]);
		objDemandStats.put("beta", demandParam[1]);
		
		JSONArray networkDemand = new JSONArray();
		
		Iterator<String[]> iter = demandMap.keySet().iterator();
		String[] c = new String[2];
		while(iter.hasNext()){
			c = (String[])iter.next();
			double totalDemand = Double.parseDouble(c[0]);
			int nodeID = Integer.parseInt(c[1]);
			
			JSONArray perNodeNetworkDemand = new JSONArray();
			
			Iterator<String> iterC = demandMap.get(c).keySet().iterator();
			String cc = new String();
			while(iterC.hasNext()){
				cc = (String)iterC.next();
				int contentID = Integer.parseInt(cc.substring(3));
				double demand = demandMap.get(c).get(cc);
				JSONObject objContent = new JSONObject();
				objContent.put("contentID", contentID);
				objContent.put("demand", demand);
				perNodeNetworkDemand.add(objContent);
			}
			JSONObject objNode = new JSONObject();
			objNode.put("totalDemand", totalDemand);
			objNode.put("nodeID", nodeID);
			objNode.put("contentDemand", perNodeNetworkDemand);
			networkDemand.add(objNode);
		}
		objDemandStats.put("networkDemand", networkDemand);
		
		try {
			FileWriter file = new FileWriter(ouputDemandFile);
			file.write(objDemandStats.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//end logDemandMap
	

}//end of class
