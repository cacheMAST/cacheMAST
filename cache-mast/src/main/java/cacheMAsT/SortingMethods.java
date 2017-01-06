package cacheMAsT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;



/**
 * This class deals with sorting
 * 
 * @author DT
 */

public class SortingMethods {
	
	
	/**
	 * Method to sort the node according to the selected topological metric in ascending order
	 * @param nodeTableStats Hashtable<String,ArrayList<Double>>
	 * @param sortArguments Integer selected topological metric index
	 * @return sorted list of nodes according to the selected topological metric
	 */
	public List<NodeScore> sortNodeByTopoCharacteristicsUP(Hashtable<String,ArrayList<Double>> nodeTableStats, int sortArguments){
		
		List<NodeScore> nodeList = new ArrayList();
		Iterator<String> iterCC = nodeTableStats.keySet().iterator();
		String cc = new String();
		while(iterCC.hasNext()){
			cc = (String)iterCC.next();
	        switch (sortArguments) {
	            case 0:  nodeList.add(new NodeScore(cc,Integer.parseInt(cc.substring(1, cc.indexOf("]"))))); //by node ID
	                     break;
	            case 1:  nodeList.add(new NodeScore(cc,nodeTableStats.get(cc).get(0))); // by connectivity degree
	                     break;
	            case 2:  nodeList.add(new NodeScore(cc,nodeTableStats.get(cc).get(1))); // by clustering coefficient
	                     break;
	            case 3:  nodeList.add(new NodeScore(cc,nodeTableStats.get(cc).get(2))); //by average distance factor
	            		 break;
	            default: nodeList.add(new NodeScore(cc,nodeTableStats.get(cc).get(0)));
	                     break;
	        }
	    }
	
		Collections.sort(nodeList);
		
		return nodeList;
		
	}//end sortNodeByTopoCharacteristicsUP
	
	
	
	/**
	 * Method to sort the node according to the selected topological metric in descending order
	 * @param nodeTableStats Hashtable<String,ArrayList<Double>>
	 * @param sortArguments Integer selected topological metric index
	 * @return sorted list of nodes according to the selected topological metric
	 */
	public List<NodeScore> sortNodeByTopoCharacteristicsDOWN(Hashtable<String,ArrayList<Double>> nodeTableStats, int sortArguments){
		
		List<NodeScore> nodeList = new ArrayList();
		Iterator<String> iterCC = nodeTableStats.keySet().iterator();
		String cc = new String();
		while(iterCC.hasNext()){
			cc = (String)iterCC.next();
	        switch (sortArguments) {
	            case 0:  nodeList.add(new NodeScore(cc,-Integer.parseInt(cc.substring(1, cc.indexOf("]"))))); //by node ID
	                     break;
	            case 1:  nodeList.add(new NodeScore(cc,-nodeTableStats.get(cc).get(0))); // by connectivity degree
	                     break;
	            case 2:  nodeList.add(new NodeScore(cc,-nodeTableStats.get(cc).get(1))); // by clustering coefficient
	                     break;
	            case 3:  nodeList.add(new NodeScore(cc,-nodeTableStats.get(cc).get(2))); //by average distance factor
	            		 break;
	            default: nodeList.add(new NodeScore(cc,-nodeTableStats.get(cc).get(0)));
	                     break;
	        }
	    }
	
		Collections.sort(nodeList);
		
		return nodeList;
		
	}//end sortNodeByTopoCharacteristicsUP
	
}
