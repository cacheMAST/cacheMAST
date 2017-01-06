package cacheMAsT;

/**
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.*;
import org.json.simple.parser.*;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
/**
 * @author tom
 *
 */
public class JSONC {

	public static void main(String[] args) { //convert to external function for usage in CachingVisualization
	}
	
	static String topologyJsonToTxt(String jsonfile) {			
		JSONParser parser = new JSONParser();
		String txtfile = jsonfile.substring(0, jsonfile.lastIndexOf('.'))+".txt";
		try {
			Object obj = parser.parse(new FileReader(jsonfile));

			JSONObject jsonObject = (JSONObject) obj;

			PrintWriter pw = new PrintWriter(txtfile,"UTF-8"); //actual package will use same file name .txt and return .txt file 
			pw.printf("graph{;");
			JSONArray link = (JSONArray) jsonObject.get("graph");
			Iterator<JSONObject> iterator = link.iterator();
			while (iterator.hasNext()) {
				JSONObject item = 	iterator.next();
				pw.printf("%s--%s[label=\"%s,%s\"];",item.get("start"),item.get("end"),item.get("linkWeight"),item.get("linkCapacityKbps"));
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.print("}");
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return txtfile;
	}//end method
	
	static File topologyJsonToFile(File jsonfile,String topologyName,File targetFolder) {			
		JSONParser parser = new JSONParser();
		String txtfile = jsonfile.getName().substring(0, jsonfile.getName().lastIndexOf('.'));
		if(txtfile.contains(topologyName))
			txtfile+=".txt";
		else
			txtfile+=(topologyName+".txt");
		try {
			Object obj = parser.parse(new FileReader(jsonfile));

			JSONObject jsonObject = (JSONObject) obj;

			PrintWriter pw = new PrintWriter(targetFolder.getAbsolutePath()+File.separator+txtfile,"UTF-8"); //actual package will use same file name .txt and return .txt file 
			pw.printf("graph{;");
			JSONArray link = (JSONArray) jsonObject.get("graph");
			Iterator<JSONObject> iterator = link.iterator();
			while (iterator.hasNext()) {
				JSONObject item = 	iterator.next();
				pw.printf("%s--%s[label=\"%s,%s\"];",item.get("start"),item.get("end"),item.get("linkWeight"),item.get("linkCapacityKbps"));
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.print("}");
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jsonfile = new File(targetFolder.getAbsolutePath()+"/"+txtfile);
		return jsonfile;
	}//end method
	
	
	static File topologyFileToJson(File txtFile,File targetFolder) { //returns new JSON file on success
		String txtFileName = txtFile.getName();
		String jsonFileName = targetFolder.toString()+File.separator+txtFileName.substring(0,txtFileName.lastIndexOf('.'))+".json";
		try {
			BufferedReader br = new BufferedReader(new FileReader(txtFile)); //to read txt file
			PrintWriter pw = new PrintWriter(jsonFileName,"UTF-8"); //print writer for json object
			JSONObject jsonObj = new JSONObject(); //object to become file of topology
			JSONArray graphArray = new JSONArray(); //array of paths
			
			String topologyTxt = br.readLine(); //get entire topology file
			String[] tmpStringArray = topologyTxt.split(";"); //split on comma
			List<String> paths = new ArrayList<String>(Arrays.asList(tmpStringArray)); //convert to non fixed size list
			paths.remove(0); //remove first and last unneeded elements. 
			paths.remove(paths.size()-1);
			
			Iterator<String> pathIt = paths.iterator();
			while(pathIt.hasNext()) {
				String[] extractedPath = (pathIt.next()).split("\\D");
				List<String> singlePath = new ArrayList<String>(Arrays.asList(extractedPath));
				singlePath.removeAll(Arrays.asList("", null));
				JSONObject singlePathJ = new JSONObject(); //object for individual paths
				singlePathJ.put("start",Integer.parseInt(singlePath.get(0)));
				singlePathJ.put("end",Integer.parseInt(singlePath.get(1)));
				singlePathJ.put("linkWeight",Integer.parseInt(singlePath.get(2)));
				singlePathJ.put("linkCapacityKbps",Integer.parseInt(singlePath.get(3)));
				graphArray.add(singlePathJ);
			}
			jsonObj.put("graph",graphArray);
			pw.print(jsonObj.toString());
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				return null;
			}
			pw.flush(); //flush buffer 
			pw.close(); //close writer object 
			br.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return new File(jsonFileName);
	}
	
	//outdated
	static String cachingConfigJsonToTxt(String jsonfile) {
		JSONParser parser = new JSONParser();
		String txtfile = jsonfile.substring(0, jsonfile.lastIndexOf('.'))+".txt";
		try {
			Object obj = parser.parse(new FileReader(jsonfile));

			JSONObject jsonObject = (JSONObject) obj;

			PrintWriter pw = new PrintWriter(txtfile,"UTF-8"); //actual package will use same file name .txt and return .txt file 
			JSONArray link = (JSONArray) jsonObject.get("Node");
			Iterator<JSONObject> iterator = link.iterator();
			while (iterator.hasNext()) {
				JSONObject item = 	iterator.next();
				pw.printf("Node %s %s\n",item.get("nodeID"),item.get("capacityBytes"));
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return txtfile;
	}	

	static File cachingConfigJsonToFile(File jsonfile,String topologyName, File targetFolder) {
		JSONParser parser = new JSONParser();
		String txtfile = jsonfile.getName().substring(0, jsonfile.getName().lastIndexOf('.'));
		if(txtfile.contains(topologyName))
			txtfile+=".txt";
		else
			txtfile+=(topologyName+".txt");
		
		try {
			Object obj = parser.parse(new FileReader(jsonfile));

			JSONObject jsonObject = (JSONObject) obj;

			PrintWriter pw = new PrintWriter(targetFolder.getAbsolutePath()+"/"+txtfile,"UTF-8"); //actual package will use same file name .txt and return .txt file 
			JSONArray link = (JSONArray) jsonObject.get("node");
			Iterator<JSONObject> iterator = link.iterator();
			while (iterator.hasNext()) {
				JSONObject item = 	iterator.next();
				pw.printf("Node %s %s\n",item.get("nodeID"),item.get("capacityBytes"));
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jsonfile = new File(targetFolder.getAbsolutePath()+"/"+txtfile);
		return jsonfile;
	}	

	//outdated
	static String contentConfigJsonToTxt(String jsonfile) {
	JSONParser parser = new JSONParser();
	String txtfile = jsonfile.substring(0, jsonfile.lastIndexOf('.'))+".txt";
	try {
		Object obj = parser.parse(new FileReader(jsonfile));

		JSONObject jsonObject = (JSONObject) obj;

		PrintWriter pw = new PrintWriter(txtfile,"UTF-8"); //actual package will use same file name .txt and return .txt file 
		JSONArray link = (JSONArray) jsonObject.get("content");
		Iterator<JSONObject> iterator = link.iterator();
		while (iterator.hasNext()) {
			JSONObject item = 	iterator.next();
			pw.printf("Content %s %s\n",item.get("contentID"),item.get("sizeBytes"));
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
		}
		pw.flush();
		pw.close();

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (ParseException e) {
		e.printStackTrace();
	}
	return txtfile;
}	
	
	static File contentConfigJsonToFile(File jsonfile,String topologyName, File targetFolder) {
		JSONParser parser = new JSONParser();
		String txtfile = jsonfile.getName().substring(0, jsonfile.getName().lastIndexOf('.'));
		if(txtfile.contains(topologyName))
			txtfile+=".txt";
		else
			txtfile+=(topologyName+".txt");
		try {
			Object obj = parser.parse(new FileReader(jsonfile));

			JSONObject jsonObject = (JSONObject) obj;

			PrintWriter pw = new PrintWriter(targetFolder.getAbsolutePath()+"/"+txtfile,"UTF-8"); //actual package will use same file name .txt and return .txt file 
			JSONArray link = (JSONArray) jsonObject.get("content");
			Iterator<JSONObject> iterator = link.iterator();
			while (iterator.hasNext()) {
				JSONObject item = 	iterator.next();
				pw.printf("Content %s %s\n",item.get("contentID"),item.get("sizeBytes"));
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.flush();
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jsonfile = new File(targetFolder.getAbsolutePath()+"/"+txtfile);
		return jsonfile;
	}	

	static File pathExecToJson(File execfile,String topologyName) {
		String jfilename = execfile+topologyName+".json";
		System.out.println(jfilename);
		try {
			String pathstring = new String(readAllBytes(get(execfile.toString()))); //read path file to string
			List<String> paths = new ArrayList<String>(Arrays.asList(pathstring.split("\\|"))); //split paths into array
			paths.remove(0); //remove leading null entry
			paths.remove(paths.size()-1); //remove last null entry
			Iterator<String> it = paths.iterator(); //iterator of paths 
			JSONArray jpathfull = new JSONArray();
			while(it.hasNext()) { //while additional paths 
				String subpath = it.next(); //get next path
				List<String> hops = Arrays.asList(subpath.split("\\s*;|->\\s*")); //split on ; and -> chars
				List<Integer> nodeList = new ArrayList<Integer>(); //create list of nodes
				
				//Unique nodes visited list and JSON Array
				Set<Integer> visited = new HashSet<Integer>();
				JSONArray nl = new JSONArray();
				
				Iterator<String> hit = hops.iterator(); //iterator through one path
				while(hit.hasNext()) { //while more hops 
					String h = hit.next(); //get current node 

					if(!h.isEmpty()&&!visited.contains(Integer.parseInt(h))) {  //if not empty, required due to path file formatting
						nl.add(Integer.parseInt(h));  //add to list of nodes 
						visited.add(Integer.parseInt(h));
					}
				} //end while
				if(!nl.isEmpty()) {
		        	//System.out.println(nl+"\tSTART:"+nl.get(0)+"\tEND:"+nl.get(nl.size()-1)+"\tHOPCOUNT:"+(nl.size()-1));//
		        	JSONObject jpath = new JSONObject(); //json object for individual path
		        	jpath.put("start",nl.get(0)); //add start node
		        	jpath.put("end",nl.get(nl.size()-1)); //add end node
		        	jpath.put("route",nl); //add route array from JSONArray constructed above
		        	jpath.put("hopcount",nl.size()-1); //add number of 
		        	//System.out.println(jpath);
		        	jpathfull.add(jpath); //add this path to array of all paths 
				}//end if
			}//end while
			//System.out.println(jpathfull);
			JSONObject jfile = new JSONObject(); //object to be written to file
			jfile.put("path", jpathfull); //add array of paths
			PrintWriter pw = new PrintWriter(jfilename,"UTF-8"); //actual package will use same file name .txt and return .txt file 
			pw.write(jfile.toJSONString()); //write to file
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush(); //flush buffer 
			pw.close(); //close writer object 
		}//end try
		catch (IOException e) {
			e.printStackTrace();
		}
		return new File(jfilename);
	}

	static File pathJsonToExec(File jsonfile,String topologyName, File targetFolder) {
		String execfilename = targetFolder+File.separator+jsonfile.getName().substring(0,jsonfile.getName().lastIndexOf('.'));
		if(execfilename.contains(topologyName))
			execfilename+="_fromJSON";
		else
			execfilename+=(topologyName+"_fromJSON");
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(jsonfile));
			JSONObject jsonObject = (JSONObject) obj;
			PrintWriter pw = new PrintWriter(execfilename,"UTF-8");
			JSONArray paths = (JSONArray) jsonObject.get("path"); //get array of network paths
			Iterator<JSONObject> it = paths.iterator(); //it contains a single path from start to end
			while(it.hasNext()) {
				JSONObject fullpath = it.next();
				Long start = (Long)fullpath.get("start");
				Long end = (Long)fullpath.get("end");
				Long hopcount = (Long)fullpath.get("hopcount");

				JSONArray route = (JSONArray) fullpath.get("route");
				//System.out.println("Start Node:"+start+"\tEnd Node:"+end+"\tHopCount:"+hopcount+"\tRoute:"+route);
				String pathstring = "|;";
				for(int i = 0;i<hopcount;i++) {
					pathstring+=route.get(i)+"->"+route.get(i+1)+";";
				}
				//System.out.println(pathstring);
				pw.write(pathstring);
			}
			pw.print("|");
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new File(execfilename);
	}//end method
	
	@SuppressWarnings("unchecked")
	static String demandExecToJson(String execfile,String topology) {
		final String contentdelimiter = "---------------------------------------------------------"; //string between content lists
		String jfilename = topology+execfile.replace(".","")+".json"; //filename now includes topology
		try {
			List<String> ab = Arrays.asList(execfile.split("_")); //get alpha and beta values from file name
			JSONObject jfile = new JSONObject(); //create object to be written to file
			jfile.put("alpha",ab.get(0)); //add alpha values to json
			jfile.put("beta",ab.get(1)); //add beta value
			JSONArray networkdemand = new JSONArray(); //array of all different network demand configurations
			FileInputStream fs = new FileInputStream(execfile); //input reader for demand file
			BufferedReader br = new BufferedReader(new InputStreamReader(fs)); //create buffered reader for line by line evaluation
			String dline; //string to store each line of demand
			JSONObject cache = null; // null initialized object for each cach JSON object
			JSONArray contentarray = null; //array for content demand values
			JSONObject contentdemand = null; //object for specific ID:demand content pairs
			while((dline = br.readLine()) != null) { //read line 
					String[] splitline = dline.split("\\s"); //split each line on space
					if(splitline[0].equals("Local")) { //if line is indicating new cache listing from "local demand for cache #"
						cache = new JSONObject(); //assign cache to a new JSONObject for this cache 
						contentarray = new JSONArray(); //assign contentarray to new value
						cache.put("nodeID",splitline[4]); //add the nodeID to cache
					}
					else if(splitline[0].equals("Total")) { //if line is "total demand = #" then store total demand
						cache.put("totalDemand",splitline[3]);
					}
					else if(splitline[0].equals("Content")) { //if line is a ContentID:demand pair
						contentdemand = new JSONObject(); //new object for this pair
						contentdemand.put("contentID",splitline[1]); //add ID
						contentdemand.put("demand",splitline[3]); //add demand
						contentarray.add(contentdemand); //add pair object to content demand
					}
					else if (splitline[0].equals(contentdelimiter)) { //if delimiter, indicating new cache, reached 
						cache.put("contentDemand",contentarray);//add this demand array to cache object
						networkdemand.add(cache); //add cache object to network demand
					}
			}
			jfile.put("networkDemand",networkdemand);//add network demand to jfile
			PrintWriter pw = new PrintWriter(jfilename,"UTF-8"); //new PW object
			pw.write(jfile.toJSONString()); //write to file 
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush(); //flush buffer 
			pw.close(); //close writer object 
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return jfilename;
	}
	/*
	@SuppressWarnings("unchecked")
	//static String demandJsonToExec(String jsonfile,File targetFolder) {
		String execfilename = null;
		final String contentdelimiter = "---------------------------------------------------------"; //string between content lists
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(jsonfile)); //Abstract object of JSON file
			JSONObject jsonObject = (JSONObject) obj; //cast to JSONObject
			String alpha = (String)jsonObject.get("alpha"); //Get alpha values
			String beta = (String)jsonObject.get("beta"); //Get beta values
			
			execfilename = alpha+"_"+beta+"_fromJSON"; //set executablefilename
			File execfile = new File(targetFolder.getAbsolutePath()+"/"+execfilename); //create file handle
			PrintWriter pw = new PrintWriter(execfile); //new PW object of file 
			JSONArray nwd = (JSONArray)jsonObject.get("networkDemand"); //get network demand structure
			
			Iterator<JSONObject> it = nwd.iterator(); //create iterator for network demand 
			while(it.hasNext()) { 
				JSONObject cache = it.next(); //get current cache 
				String nodeID = (String)cache.get("nodeID"); //get node of cache
				String totalDemand = (String)cache.get("totalDemand"); //get demand array
				pw.printf("Local demand for cache %s\nTotal demand = %s\n",nodeID,totalDemand); //write node and total demand
				JSONArray cd = (JSONArray) cache.get("contentDemand"); //get content demand for node
				Iterator<JSONObject> cdit = cd.iterator(); //iterator of content for node
				while(cdit.hasNext()){ 
					JSONObject contentpair = cdit.next(); //get pair of contentID and demand for each element in content list for node
					pw.printf("Content %s = %s\n",contentpair.get("contentID"),contentpair.get("demand")); //write content and node to file
				}
				pw.printf("%s\n",contentdelimiter); //after each cache config written set 57- chars as delimiter
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.flush(); //flush and close pw object
			pw.close();
			
			//Matching original file executable structure but could need tweaking
			execfile.setReadable(true,false);
			execfile.setWritable(true,false);
			execfile.setExecutable(true,false);
		}
		//must create execfilename
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return execfilename;
	}
	*/

	
	@SuppressWarnings("unchecked")
	static File demandJsonToFile(File jsonfile,String topologyName, File targetFolder) {
		String execfilename = null;
		final String contentdelimiter = "---------------------------------------------------------"; //string between content lists
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(jsonfile)); //Abstract object of JSON file
			JSONObject jsonObject = (JSONObject) obj; //cast to JSONObject
			String alpha = (String)jsonObject.get("alpha"); //Get alpha values
			String beta = (String)jsonObject.get("beta"); //Get beta values
			execfilename = alpha+"_"+beta; //set executablefilename
			if(!execfilename.contains(topologyName))
				execfilename+=(topologyName+"_fromJSON");
			else
				execfilename+="_fromJSON";
			File execfile = new File(targetFolder.getAbsolutePath()+"/"+execfilename); //create file handle
			PrintWriter pw = new PrintWriter(execfile); //new PW object of file 
			JSONArray nwd = (JSONArray)jsonObject.get("networkDemand"); //get network demand structure
			Iterator<JSONObject> it = nwd.iterator(); //create iterator for network demand 
			while(it.hasNext()) { 
				JSONObject cache = it.next(); //get current cache 
				String nodeID = (String)cache.get("nodeID"); //get node of cache
				String totalDemand = (String)cache.get("totalDemand"); //get demand array
				pw.printf("Local demand for cache %s\nTotal demand = %s\n",nodeID,totalDemand); //write node and total demand
				JSONArray cd = (JSONArray) cache.get("contentDemand"); //get content demand for node
				Iterator<JSONObject> cdit = cd.iterator(); //iterator of content for node
				while(cdit.hasNext()){ 
					JSONObject contentpair = cdit.next(); //get pair of contentID and demand for each element in content list for node
					pw.printf("Content %s = %s\n",contentpair.get("contentID"),contentpair.get("demand")); //write content and node to file
				}
				pw.printf("%s\n",contentdelimiter); //after each cache config written set 57- chars as delimiter
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.flush(); //flush and close pw object
			pw.close();
			
			//Matching original file executable structure but could need tweaking
			execfile.setReadable(true,false);
			execfile.setWritable(true,false);
			execfile.setExecutable(true,false);
			jsonfile = execfile;
		}
		//must create execfilename
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return jsonfile;
	}
	
	static File nodeTypeJsonToFile(File jsonfile,String topologyName, File targetFolder) {
		JSONParser parser = new JSONParser();
		String txtfile = jsonfile.getName().substring(0, jsonfile.getName().lastIndexOf('.'));
		if(txtfile.contains(topologyName))
			txtfile+=".txt";
		else
			txtfile+=(topologyName+".txt");
		try {
			Object obj = parser.parse(new FileReader(jsonfile));		
			JSONObject jsonObject = (JSONObject) obj;
			PrintWriter pw = new PrintWriter(targetFolder.getAbsolutePath()+"/"+txtfile,"UTF-8"); //actual package will use same file name .txt and return .txt file 
			JSONArray nodeArray = (JSONArray) jsonObject.get("node");
			Iterator<JSONObject> iterator = nodeArray.iterator();
			pw.print(";");
			while (iterator.hasNext()) {
				JSONObject item = 	iterator.next();
				pw.printf("Node%s=%s;",item.get("nodeID"),item.get("type"));
				if(pw.checkError()) {
					System.err.println("Program Exit: Printwriter Error");
					System.exit(1);
				}
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jsonfile = new File(targetFolder.getAbsolutePath()+"/"+txtfile);
		return jsonfile;
	}
	
	static String topologyVariableTxtToJson(String filename) {
		String jfilename = null;
		try {
			jfilename = filename.substring(0,filename.lastIndexOf('.'))+".json";
			String inputvars = new String(readAllBytes(get(filename))); //read topology variable file to string
			
			File jfile = new File(jfilename); //create file handle
			JSONObject jobj = new JSONObject();
			PrintWriter pw = new PrintWriter(jfile); //new PW object of file 
			
			List<String> var = Arrays.asList(inputvars.split(";")); //split variables into array
			Iterator<String> it = var.iterator(); //iterator of variables exported
			
			//TODO - split each var into 3 on whitespace, add 0th and 2nd to JSON object and write jSON object to file.
			while(it.hasNext()){
				String[] v = ((String)it.next()).split("=");
				if(!v[0].isEmpty()) {
					jobj.put(v[0],v[1]);
				}
			}
			pw.write(jobj.toString());
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush();
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return jfilename; 
	}
	
	static String importFilesTxtToJson(String filename) {
		String jfilename = null;
		try {
			jfilename = filename.substring(0,filename.lastIndexOf('.'))+".json";
			String inputvars = new String(readAllBytes(get(filename))); //read topology variable file to string
			
			File jfile = new File(jfilename); //create file handle
			JSONObject jobj = new JSONObject();
			PrintWriter pw = new PrintWriter(jfile); //new PW object of file 
			
			List<String> var = Arrays.asList(inputvars.split(";")); //split variables into array
			Iterator<String> it = var.iterator(); //iterator of variables exported
			
			//TODO - split each var into 3 on whitespace, add 0th and 2nd to JSON object and write jSON object to file.
			while(it.hasNext()){
				String[] v = ((String)it.next()).split("=");
				if(!v[0].isEmpty()) {
					jobj.put(v[0],v[1]);
					//TOM: In JSON the '/' == '\/' char due to html tag constraints. this is an open issue and when 
					//converting back to txt must be taken into account with .replace("\/","/") on the string
				}
			}
			pw.write(jobj.toString());
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush();
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return jfilename; 	
	}
	
	static String networkStatisticsTxtToJson(String filename) {
		String jfilename = null;
		try {
			jfilename = filename.substring(0,filename.lastIndexOf('.'))+".json";
			String inputvars = new String(readAllBytes(get(filename))); //read topology variable file to string
			
			File jfile = new File(jfilename); //create file handle
			JSONObject jobj = new JSONObject();
			PrintWriter pw = new PrintWriter(jfile); //new PW object of file 
			
			List<String> var = Arrays.asList(inputvars.split(";")); //split variables into array
			Iterator<String> it = var.iterator(); //iterator of variables exported
			
			//TODO - split each var into 3 on whitespace, add 0th and 2nd to JSON object and write jSON object to file.
			while(it.hasNext()){
				String[] v = ((String)it.next()).split("=");
				if(!v[0].isEmpty()) {
					jobj.put(v[0],v[1]);
				}
			}
			pw.write(jobj.toString());
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush();
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return jfilename; 	
	}	
	
	static String nodeLevelDataTxtToJson(String filename) {
		String jfilename = null;
		try {
			jfilename = filename.substring(0,filename.lastIndexOf('.'))+".json";
			String inputvars = new String(readAllBytes(get(filename))); //read topology variable file to string
			
			File jfile = new File(jfilename); //create file handle
			JSONObject jobj = new JSONObject();
			JSONArray nodearray = new JSONArray();
			PrintWriter pw = new PrintWriter(jfile); //new PW object of file 
			
			List<String> var = Arrays.asList(inputvars.split("\\|")); //split variables into array
			Iterator<String> it = var.iterator(); //iterator of variables exported
			
			//TODO - split each var into 3 on whitespace, add 0th and 2nd to JSON object and write jSON object to file.
			while(it.hasNext()){
				String nodeSolo = it.next();
				if(!nodeSolo.isEmpty()){
					JSONObject jnode = new JSONObject();
					List<String> v = Arrays.asList(nodeSolo.split(";"));
					Iterator<String> vit = v.iterator();
					while(vit.hasNext()) {
						List<String> nodeAttr = Arrays.asList(vit.next().split("="));
						jnode.put(nodeAttr.get(0),nodeAttr.get(1));
					}
					nodearray.add(jnode);
				}
			}
			jobj.put("node",nodearray);
			
			//System.out.println(jobj);
			pw.write(jobj.toString());
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush();
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return jfilename; 		
	}
	
	static String linkLevelDataTxtToJson(String filename) {
		String jfilename = null;
		try {
			jfilename = filename.substring(0,filename.lastIndexOf('.'))+".json";
			String inputvars = new String(readAllBytes(get(filename))); //read topology variable file to string
			
			File jfile = new File(jfilename); //create file handle
			JSONObject jobj = new JSONObject();
			JSONArray linkarray = new JSONArray();
			PrintWriter pw = new PrintWriter(jfile); //new PW object of file 
			
			List<String> var = Arrays.asList(inputvars.split("\\|")); //split variables into array
			Iterator<String> it = var.iterator(); //iterator of variables exported
			
			//TODO - split each var into 3 on whitespace, add 0th and 2nd to JSON object and write jSON object to file.
			while(it.hasNext()){
				String linkSolo = it.next();
				if(!linkSolo.isEmpty()){
					JSONObject jlink = new JSONObject();
					List<String> v = Arrays.asList(linkSolo.split(";"));
					Iterator<String> vit = v.iterator();
					while(vit.hasNext()) {
						List<String> linkAttr = Arrays.asList(vit.next().split("="));
						jlink.put(linkAttr.get(0),linkAttr.get(1));
					}
					linkarray.add(jlink);
				}
			}
			jobj.put("link",linkarray);
			
			pw.write(jobj.toString());
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush();
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return jfilename; 	
	}
	
}//end class