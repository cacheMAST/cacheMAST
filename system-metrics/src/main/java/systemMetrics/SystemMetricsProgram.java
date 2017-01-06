package systemMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cacheManagement.Cache;
import cacheManagement.ConfigurationMaps;
import cacheManagement.Content;
import networkGraph.Edge;
import networkGraph.Graph;
import networkGraph.GraphGenerator;

public class SystemMetricsProgram {
	
	String topoName;
	String inputTopologyFile;
	String inputCacheFile;
	String inputContentFile;
	String inputDemandFile;
	String pathFile;
	String contentPlacementConfigurationFile;
	String serverSelectionConfigurationFile;
	String cacheMetricsFile;
	String networkMetricsFile;
	String topologicalMetricsFile;
	String contentMetricsFile;
	int managementOption;
	
	/**
	 * Constructor
	 */
	public SystemMetricsProgram(){
	}
	
	/**
	 * Constructor
	 * @param topoName
	 * @param inputTopologyFile
	 * @param inputCacheFile
	 * @param inputContentFile
	 * @param inputDemandFile
	 * @param pathFile
	 * @param contentPlacementConfigurationFile
	 * @param serverSelectionConfigurationFile
	 * @param cacheMetricsFile
	 * @param networkMetricsFile
	 * @param topologicalMetricsFile
	 * @param contentMetricsFile
	 * @param managementOption
	 */
	public SystemMetricsProgram(String topoName,
										String inputTopologyFile,
										String inputCacheFile,
										String inputContentFile,
										String inputDemandFile,
										String pathFile,
										String contentPlacementConfigurationFile,
										String serverSelectionConfigurationFile,
										String cacheMetricsFile,
										String networkMetricsFile,
										String topologicalMetricsFile,
										String contentMetricsFile,
										int managementOption){
		this.topoName = topoName;
		this.inputTopologyFile = inputTopologyFile;
		this.inputCacheFile = inputCacheFile;
		this.inputContentFile = inputContentFile;
		this.inputDemandFile = inputDemandFile;
		this.pathFile = pathFile;
		this.contentPlacementConfigurationFile = contentPlacementConfigurationFile;
		this.serverSelectionConfigurationFile = serverSelectionConfigurationFile;
		this.cacheMetricsFile = cacheMetricsFile;
		this.networkMetricsFile = networkMetricsFile;
		this.topologicalMetricsFile = topologicalMetricsFile;
		this.contentMetricsFile = contentMetricsFile;
		this.managementOption = managementOption;
	}
	
	/**
	 * Constructor
	 * @param inputTopologyFile
	 * @param pathFile
	 * @param topologicalMetricsFile
	 * @param managementOption
	 */
	public SystemMetricsProgram(String inputTopologyFile,
										String pathFile,
										String topologicalMetricsFile,
										int managementOption){
		this.inputTopologyFile = inputTopologyFile;
		this.pathFile = pathFile;
		this.topologicalMetricsFile = topologicalMetricsFile;
		this.managementOption = managementOption;
	}
	
	//Getters
	public String getTopoName(){
		return this.topoName;
	}
	public String getInputTopologyFile(){
		return this.inputTopologyFile;
	}
	public String getInputCacheFile(){
		return this.inputCacheFile;
	}
	public String getInputContentFile(){
		return this.inputContentFile;
	}
	public String getInputDemandFile(){
		return this.inputDemandFile;
	}
	public String getPathFile(){
		return this.pathFile;
	}
	public String getContentPlacementConfigurationFile(){
		return this.contentPlacementConfigurationFile;
	}
	public String getServerSelectionConfigurationFile(){
		return this.serverSelectionConfigurationFile;
	}
	public String getCacheMetricsFile(){
		return this.cacheMetricsFile;
	}
	public String getNetworkMetricsFile(){
		return this.networkMetricsFile;
	}
	public String getTopologicalMetricsFile(){
		return this.topologicalMetricsFile;
	}
	public String getContentMetricsFile(){
		return this.contentMetricsFile;
	}
	public int getManagemnentOption(){
		return this.managementOption;
	}
	
	//Setters
	public void getTopoName(String topoName){
		this.topoName = topoName;
	}
	public void setInputTopologyFile(String inputTopologyFile){
		this.inputTopologyFile = inputTopologyFile;
	}
	public void getInputCacheFile(String inputCacheFile){
		this.inputCacheFile = inputCacheFile;
	}
	public void setInputContentFile(String inputContentFile){
		this.inputContentFile = inputContentFile;
	}
	public void setInputDemandFile(String inputDemandFile){
		this.inputDemandFile = inputDemandFile;
	}
	public void setPathFile(String pathFile){
		this.pathFile = pathFile;
	}
	public void setContentPlacementConfigurationFile(String contentPlacementConfigurationFile){
		this.contentPlacementConfigurationFile = contentPlacementConfigurationFile;
	}
	public void setServerSelectionConfigurationFile(String serverSelectionConfigurationFile){
		this.serverSelectionConfigurationFile = serverSelectionConfigurationFile;
	}
	public void setCacheMetricsFile(String cacheMetricsFile){
		this.cacheMetricsFile = cacheMetricsFile;
	}
	public void setNetworkMetricsFile(String networkMetricsFile){
		this.networkMetricsFile = networkMetricsFile;
	}
	public void setTopologicalMetricsFile(String topologicalMetricsFile){
		this.topologicalMetricsFile = topologicalMetricsFile;
	}
	public void setContentMetricsFile(String contentMetricsFile){
		this.contentMetricsFile = contentMetricsFile;
	}
	public void setManagementOption(int managementOption){
		this.managementOption = managementOption;
	}
	
	/**
	 * Compute the system metrics
	 * @param managementOption int identifier of the management option to select the relevant metrics to compute
	 */
	public void computeSystemMetrics(int managementOption){
		
		switch (managementOption) {
        case 0:  this.computeSystemMetricsOption0();
                 break;
        case 1:  this.computeSystemMetricsOption1();
                 break;
        default: System.out.println("Invalid management option");
                 break;
		}
	
	}//end computeSystemMetrics 
	
	
	/**
	 * Compute system metrics relevant to management option 0 (routing only configured)
	 */
	public void computeSystemMetricsOption0(){
		
		//Network graph objects
		GraphGenerator graphGenerator = new GraphGenerator();
		Graph graph = graphGenerator.createGraphStructure(inputTopologyFile);
						
		//Topological metrics
		TopologicalMetrics topoMetrics = new TopologicalMetrics();
		topoMetrics.logJSONTopologicalMetrics(graph, pathFile, topologicalMetricsFile);
				
	}//end computeSystemMetricsOption0
	
	/**
	 * Compute system metrics relevant to management option 1 (routing + content placement + server selection configured)
	 */
	public void computeSystemMetricsOption1(){
		
		//Network graph objects
		GraphGenerator graphGenerator = new GraphGenerator();
		Graph graph = graphGenerator.createGraphStructure(inputTopologyFile);
		HashMap<String,Edge> edgeMap = graphGenerator.createEdgeMap(inputTopologyFile);
						
		//Configuration maps initialization
		ConfigurationMaps configurationMaps = new ConfigurationMaps();
		HashMap<Cache, Cache> cacheMap = configurationMaps.createCacheMap(inputCacheFile);	
		HashMap<String, Cache> cacheIDMap = configurationMaps.createCacheIDMap(cacheMap);
		HashMap<Content, Content> contentMap = configurationMaps.createContentMap(inputContentFile);
		HashMap<String, Content> contentIDMap = configurationMaps.createContentIDMap(contentMap);
		HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap = configurationMaps.createContentPlacementMap(contentPlacementConfigurationFile, contentIDMap, cacheIDMap);
		HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap = configurationMaps.createInNetwContentAvailabilityMap(contentPlacementMap, contentIDMap, cacheIDMap);	
		HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap = configurationMaps.createServerSelectionMap(serverSelectionConfigurationFile, contentIDMap, cacheIDMap);
		HashMap<String, ArrayList<Edge>> routingMap = configurationMaps.createRoutingMap(pathFile, edgeMap, cacheIDMap);
		HashMap<Edge, ArrayList<String>> edgeInvolvementMap = configurationMaps.createEdgeInvolvementMap(routingMap, edgeMap);
		HashMap<Cache, HashMap<Content,Double>> demandMap = configurationMaps.createDemandMap(inputDemandFile, contentIDMap, cacheIDMap);	

		//Network metrics
		NetworkMetrics netwMetrics = new NetworkMetrics();
		netwMetrics.logJSONNetworkMetrics(edgeInvolvementMap, serverSelectionMap, cacheIDMap, routingMap, networkMetricsFile);
				
		//Caching metrics
		CachingMetrics cachingMetrics = new CachingMetrics();
		cachingMetrics.logJSONCachingMetrics(serverSelectionMap, contentPlacementMap, inNetwContentAvailabilityMap, cacheMap, cacheMetricsFile);
				
		//Topological metrics
		TopologicalMetrics topoMetrics = new TopologicalMetrics();
		topoMetrics.logJSONTopologicalMetrics(graph, pathFile, topologicalMetricsFile);
				
		//Content metrics
		ContentMetrics contentMetrics = new ContentMetrics();
		contentMetrics.logJSONContentMetrics(inNetwContentAvailabilityMap, demandMap, contentMap, contentMetricsFile);
				
	}//end computeSystemMetricsOption1
	

}
