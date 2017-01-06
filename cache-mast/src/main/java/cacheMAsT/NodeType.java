package cacheMAsT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * This class deals with the applying different node types to the nodes in the topology
 * @authors Tom, Tonny Duong
 */
public class NodeType 
{
	private int nbNodes = 0;
	private String[][] nodeType;
	private ArrayList<ArrayList<String>> nodeType_; 
	private String[] type = {"DEFAULT", "CORE_NODE", "EDGE_NODE"};


	
	/**
	 * Constructor argument for default nodes using list of node IDs
	 * @author tom
	 * @param nodeList
	 */
	public NodeType(List<Integer> nodeList) { 
		this.nbNodes = nodeList.size();
		nodeType = new String[2][nbNodes];
		
		nodeType_ = new ArrayList<ArrayList<String>>(); //construct new arraylist for this instance of the class
		//Initialise arraylist nodeType
		nodeType_.add(new ArrayList<String>(nodeList.size())); //nodeType_.get(0) is ID array
		nodeType_.add(new ArrayList<String>(nodeList.size())); //nodeType_.get(1) is Type array

		for(int i = 0;i<nodeList.size();i++) {
			nodeType[0][i] = nodeList.get(i).toString();
			nodeType[1][i] = type[0];
			nodeType_.get(0).add(nodeList.get(i).toString()); //add this ID to nodeType
			nodeType_.get(1).add(type[0]); //set this node to default type
		}
	}

	/**
	 * Returns an arraylist of node IDs
	 * @author tom
	 */
	public List<Integer> getIDList() {
		List<Integer> intList = new ArrayList<Integer>();
		for(String s: nodeType_.get(0)) //loop through list and return integer type 
			intList.add(Integer.valueOf(s));
		return intList;
	}
	
    /**
     * Returns a String array containing all the available node types.
     * 
     * @author Tonny Duong
     */
	public String[] getTypes()
	{
		return type;
	}
	/**
	 * Adds new node to class object of node type
	 * @author tom
	 * @param newNodeID
	 * @param newNodeID
	 */
	public void addNewNode(int newNodeID, String newNodeType) {
		nodeType_.get(0).add(String.valueOf(newNodeID));
		nodeType_.get(1).add(newNodeType);
		//nbNodes++; UNUSED
	}
	
	/**
	 * Deletes node from class object of nodetype
	 * @author tom
	 * @param deleteNode
	 */
	public void deleteNode(int deleteNode) {
		int indexRemove = nodeType_.get(0).indexOf(String.valueOf(deleteNode));//get index of the ID to be deleted
		nodeType_.get(0).remove(indexRemove);
		nodeType_.get(1).remove(indexRemove);
		//nbNodes--; UNUSED
	}
	
    /**
     * Returns the node type of a provided node ID
     * 
     * @author Tonny Duong
     * @author tom
     */
	public String getNodeType(int nodeID)
	{

		int nodeIdx = nodeType_.get(0).indexOf(String.valueOf(nodeID));
		return nodeType_.get(1).get(nodeIdx);
	}

    /**
     * Sets the provided node ID based on one of the available node types strings (ignores cases)
     * 
     * @author Tonny Duong
     */
	public void setNodeType(int nodeID, String newType)
	{
		int nodeIdx = nodeType_.get(0).indexOf(String.valueOf(nodeID));

		String type = newType.toUpperCase();
		type = type.replace(" ", "");
		type = type.replace("_", "");
		switch(type){
		case "DEFAULT": 
			nodeType_.get(1).set(nodeIdx,"DEFAULT"); 
			break;
		case "CORENODE": 
			nodeType_.get(1).set(nodeIdx,"CORE_NODE"); 
			break;
		case "EDGENODE": 
			nodeType_.get(1).set(nodeIdx,"EDGE_NODE"); 
			break;
		default:
			System.out.println("nodeType ERROR 1: nodeType string is invalid!");
			break;
		}
	}
	
    /**
     * Sets the provided node ID based on the provided node type index
     * <li><code>0 = DEFAULT</code>,
     * <li><code>1 = CORE_NODE</code>,
     * <li><code>2 = EDGE_NODE</code>,
     * 
     * SET TO PRIVATE - USE (INT, STRING) SIGNATURE VERSION FOR EXTERNAL USAGE
     * @author Tonny Duong
     * @author tom
     */
	private void setNodeType(int nodeIdx, int nodeTypeIndex) //set a node ID's attribute to a different string in the tag array
	{
		nodeType_.get(1).set(nodeIdx,type[nodeTypeIndex]);
	}
	

	/**
	 * New getData() function which returns the same String[][] object but uses list structure
	 * @author tom
	 * @return
	 */
	public String[][] getData()
	{
		String[][] returnData = new String[nodeType_.size()][nodeType_.get(0).size()];
		//Loop through nodeType_ List and assign to String[][] object
		for(int i = 0;i<nodeType_.size();i++) {
			for(int j = 0;j<nodeType_.get(0).size();j++) {
				returnData[i][j]=nodeType_.get(i).get(j);
			}
		}
		return returnData;
	}
	
	/**
	 * Reads the given JSON file and sets the node type in accordance with the file
	 * This is a JSON Native version of the previous readAttributeList() method.
	 * @author Tom
	 */
	public void readAttributeListJson(File newFile) {
		Integer nodeID;
		String type;
		try {
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject)parser.parse(new FileReader(newFile));
			JSONArray attrArray = (JSONArray)jObj.get("node");
			Iterator<JSONObject> it = attrArray.iterator();
			while(it.hasNext()) {
				JSONObject attr = it.next();
				nodeID = Math.toIntExact((Long)attr.get("nodeID"));
				type = (String)attr.get("type");
				setNodeType(nodeID,type);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Writes the current node type settings for all nodes into the provided text file 
     * 
     * @author Tonny Duong
     */
	public void printAttributeList(File newFile)
	{
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
			bw.write(printAttributeString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Returns the string for the current node type settings for all nodes
     * which would have been written into a file for import/export 
     * 
     * @author Tonny Duong
     */
	public String printAttributeString()
	{
		String output = ";";
		for(int i=0; i<nodeType_.get(0).size(); i++){
			output+="Node"+nodeType_.get(0).get(i)+"="+nodeType_.get(1).get(i)+";";
		}
		return output;
	}
	
	/**
	 * New version of Attribute export in JSON format
	 * Outputs the same type of file which can be used as the Node Attribute Config
	 * @author tom
	 * @param targetFile
	 */
	public void printAttributeListJson(File targetFile) {
		JSONObject jObj = new JSONObject(); //object for whole file
		JSONArray nodeArray = new JSONArray(); //array object for nodes
		JSONObject currentNode; //object for each node in graph
		for(int i = 0;i<nodeType_.get(0).size();i++) {
			currentNode = new JSONObject();
			currentNode.put("nodeID",Integer.parseInt(nodeType_.get(0).get(i)));
			currentNode.put("type",(nodeType_.get(1).get(i)));
			nodeArray.add(currentNode);
		}
		jObj.put("node",nodeArray);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(targetFile));
			bw.write(jObj.toJSONString());
			bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
