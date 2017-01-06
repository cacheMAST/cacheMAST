package cacheMAsT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import static java.lang.Math.toIntExact;



public class JungGraphVisualisation {
	private DirectedSparseGraph<String, String> g ;
	private String selectedL;
	private ArrayList<String[]> listEdges;
	public static int[][] nodeAttributes;

	//def constructor
	public JungGraphVisualisation() {
		this.g = null;
		this.listEdges= new ArrayList<String[]>();
	}
	
	
	/**New version of graph creation not relying on the graphGenerator class
	 * @author tom
	 * @param nodeList is an array of the current node IDs
	 * @param filename is the JSON file of edges
	 * @param selectedLayout is the layout format for the visualize method
	 *
	 * @return VisualizationViewer<> object which is displayed in the GUI
	 */

	//CURRENT MASTER CONSTRUCTOR
	public VisualizationViewer<String, String> createGraphNew(List<Integer> nodeList, String filename,String selectedLayout) {

		//Parse JSON object to get nodes and edges.
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(new InputStreamReader(new FileInputStream(filename)));
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (org.json.simple.parser.ParseException e1) {
			e1.printStackTrace();
		}

		JSONObject jsonObject = (JSONObject)obj;
		JSONArray graph = (JSONArray)jsonObject.get("graph");
		Iterator<JSONObject> it = graph.iterator();

		nodeAttributes = new int[nodeList.size()][2];
		final ArrayList<String> listNodes = new ArrayList<String>();
		for(int i = 0; i < nodeList.size(); i++) {
			nodeAttributes[i][0] = nodeList.get(i); //get list of current nodes
			listNodes.add(new String("ID:"+nodeList.get(i))); //add node of id nodeList.get(i) to list of Node objects
		}


		g = new DirectedSparseGraph<String, String>();
		VisualizationViewer<String, String> server;
		selectedL = selectedLayout;

		//In loop of JSONArray elements get edge, cost, weight and start and end and add to edge.
		//Check if start and end are in arraylist of nodes and if so add, or update adjacency
		while(it.hasNext()) {
			String startNode = new String();
			String endNode = new String();
			String[] newEdge = new String[4];
			
			JSONObject currentEdge = it.next();
			int startID = Math.toIntExact((Long)currentEdge.get("start"));
			int endID = Math.toIntExact((Long)currentEdge.get("end"));
			//System.out.printf("Start %d, end %d\n", startID,endID);

			int weight = toIntExact((Long)currentEdge.get("linkWeight"));
			int capacity = toIntExact((Long)currentEdge.get("linkCapacityKbps"));
			
			//must then get corresponding node objects from nodeList
			for(int i = 0;i<listNodes.size();i++) {
				if(listNodes.get(i).equalsIgnoreCase("ID:"+startID))
					startNode = listNodes.get(i); //get Node object from list
				else if(listNodes.get(i).equalsIgnoreCase("ID:"+endID))
					endNode = listNodes.get(i); //get Node object from list
			}
			newEdge[0]=startNode;
			newEdge[1]=endNode;
			newEdge[2]=""+weight;
			newEdge[3]=""+capacity;
			listEdges.add(newEdge);
		}

		for(int i = 0; i <listNodes.size(); i++)
			g.addVertex("Node " + Integer.parseInt(listNodes.get(i).substring(3)));			
		
		for(int i = 0; i <listEdges.size(); i++){
			String[] currEdge = listEdges.get(i);
			int startNode = Integer.parseInt(currEdge[0].substring(3));
			int endNode = Integer.parseInt(currEdge[1].substring(3));
			g.addEdge("[" +startNode + "," + endNode + "]" , "Node " + startNode, "Node " +endNode);
		}

		server = visualize(g);

		final PickedState<String> pickedState = server.getPickedVertexState();
		pickedState.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object subject = e.getItem();
				// The graph uses Integers for vertices.
				if (subject instanceof String) {
					String vertex = (String) subject;
					int nodeNumber = Integer.parseInt(vertex.substring(5,vertex.length()));
					int nodeIndex=0;//used to get the index of nodeNumber
					for(int i= 0;i<listNodes.size();i++) {
						if(Integer.parseInt(listNodes.get(i).substring(3))==nodeNumber) //if ID nodenumber is at i
							nodeIndex = i;
					}
					if (pickedState.isPicked(vertex)) {
						nodeAttributes[nodeIndex][1] = 1;
					} else {
						nodeAttributes[nodeIndex][1] = 0;
					}
				}
			}
		});

		return server;  
	}//end new visualizer

	/**
	 * Remove the specified edge from the global list of Edges during the delete link routine from GraphicalInterface
	 * @author tom
	 * @param n1 first node 
	 * @param n2 second node
	 */
	public void removeEdge(int n1, int n2) {
		Iterator<String[]> it = listEdges.iterator();
		while(it.hasNext()) {
			String[] currentEdge = it.next();
			int startEdgeID = Integer.parseInt(currentEdge[0].substring(3));
			int endEdgeID = Integer.parseInt(currentEdge[1].substring(3));
			if((startEdgeID==n1)&&(endEdgeID==n2)){
				it.remove();
			}//end if
			else if((startEdgeID==n2)&&(endEdgeID==n1)){
				it.remove();
			}//end else if
		}//end while
	}//end removeEdge

	public VisualizationViewer<String, String> visualize(DirectedSparseGraph<String, String> graph) {
		Layout<String,String> layout = null;

		if(selectedL.equalsIgnoreCase("Circle Layout")){
			layout = new CircleLayout<String, String>(graph);
			layout.setSize(new Dimension(800, 500)); // sets the initial size of the space
		}
		else if(selectedL.equalsIgnoreCase("FR Layout")){
			layout = new FRLayout<String,String>(graph);
			layout.setSize(new Dimension(800, 380)); // sets the initial size of the space
		}
		else if(selectedL.equalsIgnoreCase("KK Layout")){
			layout = new KKLayout<String,String>(graph);
			layout.setSize(new Dimension(800, 550)); // sets the initial size of the space
		}
		else if(selectedL.equalsIgnoreCase("ISOM Layout")){
			layout = new ISOMLayout<String,String>(graph);
			layout.setSize(new Dimension(800, 550)); // sets the initial size of the space
		}

		
		final VisualizationViewer<String, String> server = new VisualizationViewer<String, String>(layout);
		server.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());

		server.setVertexToolTipTransformer(new Transformer<String,String>(){

			@Override
			public String transform(String arg0) {
				return new GraphicalInterface(true).Hover(arg0);
			}

		});

		DefaultModalGraphMouse<String,String> gm = new DefaultModalGraphMouse<String,String>();
		gm.setMode(Mode.PICKING);
		server.setGraphMouse(gm);
		//server.setBackground(Color.LIGHT_GRAY);  	  
		server.setBackground(new Color(229,229,229));

		//draw the link, setting them pink when selected and black when not
		server.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<String>(server.getPickedEdgeState(), Color.black, Color.pink));
		//draw the node, setting them yellow when selected and red when not
		VertexPaintTransformer test = new VertexPaintTransformer(server.getPickedVertexState());
		server.getRenderContext().setVertexFillPaintTransformer(test);


		server.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);


		//MODIFY THIS FOR NODE SHAPE
		Transformer<String,Shape> vertexSize = new Transformer<String,Shape>(){
			public Shape transform(String i){
				Ellipse2D circle = new Ellipse2D.Double(-8, -8, 23, 23);
				// in this case, the vertex is twice as large
				return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
				//else return circle;
			}
		};

		server.getRenderContext().setVertexShapeTransformer(vertexSize);

		//Here you add the listener for the node
		server.addGraphMouseListener(new GraphMouseListener() {
			public void graphClicked(final Object v, MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {  
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {	
							new GraphicalInterface(true).cacheInfo(v);
						}
					}); //end runnable         
				}//endif

				me.consume();

			}        
			public void graphPressed(Object v, MouseEvent me) {
			}

			public void graphReleased(Object v, MouseEvent me) {
			}
		});

		final PickedState<String> pickedState = server.getPickedEdgeState();

		//Attach the listener that will print when the vertices selection changes.
		//Acts when a link has been clicked
		pickedState.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object subject = e.getItem();
				// The graph uses Integers for vertices.
				if (subject instanceof String) {
					String edge = (String) subject;
					if (pickedState.isPicked(edge)) {
						System.out.println(edge+" is now selected");
						new GraphicalInterface(true).edgeInfo(edge);
					}//end if picked
				}//end if subject is string
			}//end statechange
		});//end listener

		server.setEdgeToolTipTransformer(new Transformer<String,String>(){

			@Override
			public String transform(String arg0) {
				return new GraphicalInterface(true).EdgeHover(arg0);
			}

		});


		Transformer<String, Stroke> edgeStroke = new Transformer<String, Stroke>() {
			public Stroke transform(String s) {
				return new BasicStroke(6.0f, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
			}
		};

		server.getRenderContext().setEdgeStrokeTransformer(edgeStroke);

		return server;

	} //end visualize

	/**
	 * Returns the Array containing all the node ID with their respective Node Type
	 * @author Tonny Duong
	 */
	public int[][] getVertices()
	{
		return nodeAttributes;
	}

	/**
	 * New class which deals with painting the node (i.e. vertices) based on their Node Type
	 * @author Tonny Duong
	 */
	private class VertexPaintTransformer implements Transformer<String,Paint>{

		private final PickedInfo<String> info;
		private String[][] data = GraphicalInterface.getNodeAttribute();

		VertexPaintTransformer (PickedInfo info) { 
			super();
			if (info == null)
				throw new IllegalArgumentException("PickedInfo instance must be non-null");
			this.info = info;
		}

		@Override
		public Paint transform(String node) {

			int indexOfID = 0; //used to find the index of the specified node in the parameter

			if(info.isPicked(node)) 
				return Color.yellow;

			Integer nodeNumber = Integer.parseInt(node.substring(5,node.length()));

			for(int i = 0; i<data[0].length;i++){
				if(data[0][i].equals(nodeNumber.toString())) { //if this index is the nodeNumber ID
					indexOfID = i;
				}
			}

			if(data[1][indexOfID]=="CORE_NODE"){
				return (new Color(80,80,249));
			}
			else if(data[1][indexOfID]=="EDGE_NODE"){
				return (new Color(80,249,80));
			}
			return (new Color(249,80,80));
		}
	}
}//end class

