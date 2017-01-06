package cacheManagement;

import java.util.ArrayList;
import java.util.HashMap;

import networkGraph.Edge;
import networkGraph.GraphGenerator;

public class CacheManagementProgram {
	
	String contentPlacementStrategy;
	String serverSelectionStrategy;
	String routingStrategy;
	String topoName;
	String inputTopologyFile;
	String inputCacheFile;
	String inputContentFile;
	String inputDemandFile;
	String pathFile;
	String contentPlacementConfigurationFile;
	String serverSelectionConfigurationFile;
	int managementOption;
	
	/**
	 * Constructor
	 */
	public CacheManagementProgram(){
	}
	
	/**
	 * Constructor
	 * @param contentPlacementStrategy
	 * @param serverSelectionStrategy
	 * @param routingStrategy
	 * @param topoName
	 * @param inputTopologyFile
	 * @param inputCacheFile
	 * @param inputContentFile
	 * @param inputDemandFile
	 * @param pathFile
	 * @param contentPlacementConfigurationFile
	 * @param serverSelectionConfigurationFile
	 * @param managementOption
	 */
	public CacheManagementProgram(String contentPlacementStrategy,
										String serverSelectionStrategy,
										String routingStrategy,
										String topoName,
										String inputTopologyFile,
										String inputCacheFile,
										String inputContentFile,
										String inputDemandFile,
										String pathFile,
										String contentPlacementConfigurationFile,
										String serverSelectionConfigurationFile,
										int managementOption){
		this.contentPlacementStrategy = contentPlacementStrategy;
		this.serverSelectionStrategy = serverSelectionStrategy;
		this.routingStrategy = routingStrategy;
		this.topoName = topoName;
		this.inputTopologyFile = inputTopologyFile;
		this.inputCacheFile = inputCacheFile;
		this.inputContentFile = inputContentFile;
		this.inputDemandFile = inputDemandFile;
		this.pathFile = pathFile;
		this.contentPlacementConfigurationFile = contentPlacementConfigurationFile;
		this.serverSelectionConfigurationFile = serverSelectionConfigurationFile;
		this.managementOption = managementOption;
	}
	
	
	/**
	 * Constructor
	 * @param routingStrategy
	 * @param topoName
	 * @param inputTopologyFile
	 * @param inputCacheFile
	 * @param pathFile
	 * @param managementOption
	 */
	public CacheManagementProgram(String routingStrategy,
										String topoName,
										String inputTopologyFile,
										String inputCacheFile,
										String pathFile,
										int managementOption){
		this.routingStrategy = routingStrategy;
		this.topoName = topoName;
		this.inputTopologyFile = inputTopologyFile;
		this.inputCacheFile = inputCacheFile;
		this.pathFile = pathFile;
		this.managementOption = managementOption;
	}
	
	
	//Getters
	public String getContentPlacementStrategy(){
		return this.contentPlacementStrategy;
	}
	public String getServerSelectionStrategy(){
		return this.serverSelectionStrategy;
	}
	public String getRoutingStrategy(){
		return this.routingStrategy;
	}
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
	public int getManagementOption(){
		return this.managementOption;
	}
	
	//Setters
	public void setContentPlacementStrategy(String contentPlacementStrategy){
		this.contentPlacementStrategy = contentPlacementStrategy;
	}
	public void setServerSelectionStrategy(String serverSelectionStrategy){
		this.serverSelectionStrategy = serverSelectionStrategy;
	}
	public void setRoutingStrategy(String routingStrategy){
		this.routingStrategy = routingStrategy;
	}
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
	public void setManagementOption(int managementOption){
		this.managementOption = managementOption;
	}
	
	/**
	 * Start configure resources functionality
	 */
	public void startConfigureResources(int managementOption){
		
        switch (managementOption) {
            case 0:  this.startConfigureResourcesOption0();
                     break;
            case 1:  this.startConfigureResourcesOption1();
                     break;
            default: System.out.println("Invalid management option");
                     break;
        }
		
	}//end startConfigureResources
	
	
	/**
	 * Configure resources based on management option 0, i.e. configure routing only
	 */
	public void startConfigureResourcesOption0(){
		
		//Network graph objects
		GraphGenerator graphGenerator = new GraphGenerator();
		HashMap<String,Edge> edgeMap = graphGenerator.createEdgeMap(inputTopologyFile);
								
		//Configuration maps initialization
		ConfigurationMaps configurationMaps = new ConfigurationMaps();
		HashMap<Cache, Cache> cacheMap = configurationMaps.createCacheMap(inputCacheFile);		
		HashMap<String, Cache> cacheIDMap = configurationMaps.createCacheIDMap(cacheMap);

		System.out.println(routingStrategy);
				
		//Routing
		Routing routing = new Routing();   
		if(routingStrategy.equalsIgnoreCase("shortest_path")){
			routing.shortestPathBasedRouting(pathFile, inputTopologyFile, topoName);
			configurationMaps.createRoutingMap(pathFile, edgeMap, cacheIDMap);
		}
		else{
			System.out.println("Invalid routing strategy");
		}
				
	}//end startConfigureResourcesOption0
	
	
	/**
	 * Configure resources based on management option 1, i.e. routing + content placement + server selection
	 */
	public void startConfigureResourcesOption1(){
		
		//Network graph objects
		GraphGenerator graphGenerator = new GraphGenerator();
		HashMap<String,Edge> edgeMap = graphGenerator.createEdgeMap(inputTopologyFile);
						
		//Configuration maps initialization
		ConfigurationMaps configurationMaps = new ConfigurationMaps();
		
		HashMap<Cache, Cache> cacheMap = configurationMaps.createCacheMap(inputCacheFile);		
		int nbCaches = cacheMap.size();
		HashMap<String, Cache> cacheIDMap = configurationMaps.createCacheIDMap(cacheMap);
		HashMap<Content, Content> contentMap = configurationMaps.createContentMap(inputContentFile);
		HashMap<String, Content> contentIDMap = configurationMaps.createContentIDMap(contentMap);
		HashMap<Cache, HashMap<Content,Double>> demandMap = configurationMaps.createDemandMap(inputDemandFile, contentIDMap, cacheIDMap);
		HashMap<Cache, Double[]> cacheStatisticsMap = configurationMaps.createCacheStatisticsMap(inputDemandFile, cacheIDMap);
			
		System.out.println(routingStrategy + " " + contentPlacementStrategy + " " + serverSelectionStrategy);
		
		
		//Routing
		Routing routing = new Routing();   
		HashMap<String, ArrayList<Edge>> routingMap = new HashMap<String, ArrayList<Edge>>();
		if(routingStrategy.equalsIgnoreCase("shortest_path")){
        	routing.shortestPathBasedRouting(pathFile, inputTopologyFile, topoName);
			routingMap = configurationMaps.createRoutingMap(pathFile, edgeMap, cacheIDMap);
        }
        else{
        	System.out.println("Invalid routing strategy");
        }
		
		//Content placement
		ContentPlacement contentPlacement = new ContentPlacement();
		HashMap<Cache, HashMap<Content,Boolean>> contentPlacementMap = new HashMap<Cache, HashMap<Content,Boolean>>();
		if(contentPlacementStrategy.equalsIgnoreCase("lps")){
        	contentPlacementMap = contentPlacement.lpsBasedContentPlacement(demandMap, cacheStatisticsMap, nbCaches, contentMap);
    		contentPlacement.logContentPlacementConfiguration(contentPlacementMap, contentPlacementConfigurationFile);
            //break;
        }
		else{
        	System.out.println("Invalid content placement strategy");
        }
		
		HashMap<Content,ArrayList<Cache>> inNetwContentAvailabilityMap = configurationMaps.createInNetwContentAvailabilityMap(contentPlacementMap, contentIDMap, cacheIDMap);	
				
		//Server selection
		ServerSelection serverSelection = new ServerSelection();
		HashMap<Cache, HashMap<Content,ArrayList<Object[]>>> serverSelectionMap = new HashMap<Cache, HashMap<Content,ArrayList<Object[]>>>();
		if(serverSelectionStrategy.equalsIgnoreCase("minimum_distance")) {
    		serverSelectionMap = serverSelection.closestDistanceBasedServerSelection(routingMap, inNetwContentAvailabilityMap, demandMap, cacheIDMap);
    		serverSelection.logServerSelectionConfiguration(serverSelectionMap, serverSelectionConfigurationFile);
		}
		else{
			if(serverSelectionStrategy.equalsIgnoreCase("round_robin")){
				serverSelectionMap = serverSelection.roundRobinBasedServerSelection(inNetwContentAvailabilityMap, demandMap);
	    		serverSelection.logServerSelectionConfiguration(serverSelectionMap, serverSelectionConfigurationFile); 
			}
			else{
				System.out.println("Invalid server selection strategy");
			}
		}
		
	}//end startConfigureResourcesOption1
	
	
	

}
