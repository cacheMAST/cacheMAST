package cacheMAsT;

//Imported Packages from standard library
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//Swing and Image Processing Packages
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

//External Package Imports
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//JUNG Package Imports
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.picking.PickedState;
import systemMetrics.SystemMetricsProgram;

//Cache management package
import cacheManagement.CacheManagementProgram;


/**
* 
*@authors: maria_anagnostopoulou, Tonny Duong, Thomas Sherborne, DT
*
*/

public class GraphicalInterface extends JPanel implements ActionListener {
	
	//GUI Objects for interface
	JTextPane log;
	JTextArea info;
	static JEditorPane info1;
	JFileChooser fc;
	static JMenuBar menubar;
	static JTabbedPane GUItabs;
	static JProgressBar pbar;
	static JProgressBar pbar1;
	JScrollPane logScrollPane; //bottom window
	JScrollPane[] dataScrollPane = new JScrollPane[tabLimit]; //right window
	JLayeredPane[] lpane		 = new JLayeredPane[tabLimit]; //left window

	//Limit on the number of concurrent tabs
	static int tabLimit = 100;
	
	//Graph Pane to be set in lpane
	GraphZoomScrollPane[] graphPane = new GraphZoomScrollPane[tabLimit];
	static int tabIndex = 0; //default tabIndex. Changes on user switching

	JDialog graphPropertiesEditorJD = null;

	static JFrame frame; 									//server frame object
	static VisualizationViewer<String, String> server; 		//network topology object returned from JUNG
	static PickedState<String> pickedState; 				//object for topology selection
	
	//File handle storage arrays
	File[] nodeTypeCurrent	= new File[tabLimit];

	//File arrays to store the program configurations (in JSON format)
	File[] netTopFileJson						= new File[tabLimit];
	File[] cacheConfFileJson					= new File[tabLimit];
	File[] demFJson								= new File[tabLimit];
	File[] conFJson								= new File[tabLimit];
	File[] nodeTypeJson							= new File[tabLimit];
	File[] pathFJson 							= new File[tabLimit];
	File[] contentPlacementConfigurationFile 	= new File[tabLimit];
	File[] serverSelectionConfigurationFile 	= new File[tabLimit];
	File[] cacheMetricsFile 					= new File[tabLimit];
	File[] networkMetricsFile 					= new File[tabLimit];
	File[] topologicalMetricsFile 				= new File[tabLimit];
	File[] contentMetricsFile 					= new File[tabLimit];
	static File tmpFolder 						= null; //file handle for created tmp folder

	//Global boolean variables for every major user action 
	private static boolean[] graphPropertiesEditorOpenedBefore 	= new boolean[tabLimit];
	private static boolean[] graphHasCreated 					= new boolean[tabLimit];
	private static boolean[] startCachingPressed 				= new boolean[tabLimit];
	private static boolean[] startMetricPressed 				= new boolean[tabLimit];
	private static boolean[] graphViewChanged 					= new boolean[tabLimit];
	private static boolean[] contentConfHasImported 			= new boolean[tabLimit];
	private static boolean[] cacheConfHasImported 				= new boolean[tabLimit];
	private static boolean[] topologyConfHasImported 			= new boolean[tabLimit];
	private static boolean[] nodeConfHasImported				= new boolean[tabLimit];
	private static boolean[] pathConfHasImported				= new boolean[tabLimit];
	private static boolean[] placementConfHasImported			= new boolean[tabLimit];
	private static boolean[] serverSelectionConfHasImported		= new boolean[tabLimit];
	
	//GLOBAL JMENUITEMS & JBUTTONS COMPONENTS 
	JMenuItem showTopologicalMetrics 	= new JMenuItem("Topological Metrics");
	JMenuItem showNodeTypes 			= new JMenuItem("Node Atrributes");
	JMenuItem showCacheStatistics 		= new JMenuItem("Cache Statistics");
	JMenuItem showContentStatistics 	= new JMenuItem("Content Statistics");
	JMenuItem sortBy 					= new JMenuItem("Sort Nodes");
	JMenuItem nodeattrDefault 			= new JMenuItem("DEFAULT");
	JMenuItem nodeattrCoreNode 			= new JMenuItem("CORE_NODE");
	JMenuItem nodeattrEdgeNode 			= new JMenuItem("EDGE_NODE");
	JMenuItem createNewLink				= new JMenuItem("Create/Remove Links");
	JMenuItem addNode					= new JMenuItem("Add Node");
	JMenuItem deleteNode				= new JMenuItem("Delete Node");

	//Variables for Topological Metrics display
	private JComboBox nodeBox; 											//selection box for choosing node options
	private String[] nodeName; 											//object to get node name list
	private Boolean[] nodeStatus; 										//object to get node status: true if acts as a server, false otherwise
	private int TMnode = 1;	 											//default node selection option
	private JLabel TMmsgPart1, TMmsgPart2; 								//message labels
	private JComboBox tmVariables; 										//variable selection box
	private int stringIndexTM; 											//metric selection index
	private JLabel tmTable = new JLabel();	 							//label for metrics table 

	//Variables for NodeTypes display
	static NodeType[] nt = new NodeType[tabLimit]; 						//array of NodeType class
	private JComboBox nodeTag; 											//Selection box
	private JComboBox nodeboxID; 										//Selection box
	private int nodeID; 												//ID for node selection
	private int stringIndex;											//index variable
	private JLabel NTmsg;												//message box label
	private String exportFolderName;									//export folder name
	private String logFolderName;										//log folder name
	private Boolean folderPermitted;									//conditonal value for allowed folder to write to
	private Boolean saveFile;											//conditional value for folder writing
	private int nodeID1 = 0; 											//ID values for new links set to default
	private int nodeID2 = 0;
	private static int[] topologyInstance 	= new int[tabLimit];		//counter to inhibit export overwriting

	//Variables for topology interactions (add/delete nodes and links)
	private int newNodeID = 0; 											//ID value for new created node
	private int connectedNodeID = 0; 									//ID value for node to connect to new node, as 1 link must have been specified
	private int removeNodeID = 1;										//default value for node ID to be removed 
	private double newNodeCapacity = 0; 								//new node caching capacity
	private Boolean newNodeStatus = false;								//new node cache-status: whether it is a server node or not
	private String newNodeType = null; 									//type of new node, EDGE, CORE etc. SERVER will be a future addition
	private Integer newLinkCost = 0; 									//new link administrative cost
	private Integer newLinkCapacity = 0;								//new link kbps capacity
	private Integer newLinkDelay = 0; 									//new link delay

	//Variables for cache management operations
	static String[] selectedConPlaceStrategy 	= new String[tabLimit]; 
	static String[] selectedServerSelStrategy 	= new String[tabLimit]; 
	static String[] selectedRoutingScheme 		= new String[tabLimit]; 
	
	//Variables for cache management metrics and parameters
	static ArrayList<Double>[] netwMetricsResults 			= (ArrayList<Double>[]) new ArrayList[tabLimit]; //network metrics
	static ArrayList<Double>[] cacheMetricsResults 			= (ArrayList<Double>[]) new ArrayList[tabLimit]; //cache metrics
	static ArrayList<Double>[] topologicalMetricsResults 	= (ArrayList<Double>[]) new ArrayList[tabLimit]; //topological metrics
	static ArrayList<Double>[] contentMetricsResults 		= (ArrayList<Double>[]) new ArrayList[tabLimit]; //content metrics
	static Map<Integer,Double>[] contentLists 				= (HashMap<Integer,Double>[])new HashMap[tabLimit];	//ID:size map for content
	static Map<Integer,Double>[] cacheLists 				= (HashMap<Integer,Double>[])new HashMap[tabLimit];	//ID:capacity map for nodes
	static Map<Integer,Boolean>[] cacheListsStatus 			= (HashMap<Integer,Boolean>[])new HashMap[tabLimit];	//cache status of each node-cache
	private static ArrayList<String>[] listCaches 			= (ArrayList<String>[]) new ArrayList[tabLimit]; //list for cache IDs only	
	
	//Parameter to control the manegemtn operations allowed depending on the user inputs
	static int[] managementOption 							= new int[tabLimit];
	
	private static HashMap<String,HashMap<String,String>>[] contentPlacementMap = (HashMap<String,HashMap<String,String>>[])new HashMap[tabLimit];
	private static Hashtable<String,ArrayList<Double>>[] edgeTable      		= (Hashtable<String,ArrayList<Double>>[])new Hashtable[tabLimit];//per edge statistics
	private static Hashtable<String,ArrayList<Double>>[] cacheTable 			= (Hashtable<String,ArrayList<Double>>[])new Hashtable[tabLimit];//per cache statistics
	private static Hashtable<String,ArrayList<Double>>[] nodeTable 				= (Hashtable<String,ArrayList<Double>>[])new Hashtable[tabLimit];//per node topological statistics
	private static Hashtable<String,ArrayList<Double>>[] contentTable 			= (Hashtable<String,ArrayList<Double>>[])new Hashtable[tabLimit];//per content statistics
		
	//Variables for topology display 
	static String selectedLayout 				= "FR Layout"; //default display 
	
	//Variables for imported scenario characteristics 
	private static String[] fileLocation 		= new String[tabLimit];				//configuration files location
	static String[] topologyName 				= new String[tabLimit];				//topologyName array
	double[] alpha 								= new double[tabLimit];				//alpha value array
	double[] beta 								= new double[tabLimit]; 			//beta value array
	static int[] nbNodes 						= new int[tabLimit];				//number of nodes used only in initialisation
	//TOM: 2D versions of graph statistics arrays to store actual IDs not indexes
	static List<List<Integer>> nbNodes_			= new ArrayList<List<Integer>>(tabLimit); 			//store list of node IDs for each tab
	
	//Visualiser variables
	static List<JungGraphVisualisation> jung 	= new ArrayList<JungGraphVisualisation>(tabLimit); 	//Jung class objects for each topology
	static List<EdgePainter> edgePaint			= new ArrayList<EdgePainter>(tabLimit); 			//class object for each edge
	
	//Display settings variables
	static Dimension screenSize 		= Toolkit.getDefaultToolkit().getScreenSize();
	static double screenwidth 			= screenSize.getWidth();
	static double screenheight 			= screenSize.getHeight();
	private int graphWidth 				= (int) ((screenwidth*945)/1366); //screen scaling
	private int graphHeight 			= (int) ((screenheight*537)/768);
	private int colourTableWidth 		= 163; 								//default scaling for edge colour key in viewpane
	private int colourTableHeight 		= 142;
	private int logWidth 				= (int) ((screenwidth*100)/1366);	//message log sizing
	private int logHeight 				= (int) ((screenheight*150)/768);
	private int infoWidth 				= (int) ((screenwidth*420)/1366);	//information (rpane) sizing
	private int infoHeight 				= (int) ((screenheight*600)/768);
	private int widthDividerLocation 	= (int) (screenwidth - infoWidth);	//draggable divider default location
	private int heightDividerLocation 	= (int) (screenheight - (logHeight+76));

	//Help diaolog box
	JDialog helpDialog;
	static final String instructions = 
			"<html>"+
					"<b><h2><center>Instructions for Annotations</center></h2></b>"+
					"<p>The Annotation Controls allow you to select:"+
					"<ul>"+
					"<li>Shape"+
					"<li>Color"+
					"<li>Fill (or outline)"+
					"<li>Above or below (UPPER/LOWER) the graph display"+
					"</ul>"+
					"<p>Mouse Button one press starts a Shape,"+
					"<p>drag and release to complete."+
					"<p>Mouse Button three pops up an input dialog"+
					"<p>for text. This will create a text annotation."+
					"<p>You may use html for multi-line, etc."+
					"<p>You may even use an image tag and image url"+
					"<p>to put an image in the annotation."+
					"<p><p>"+
					"<p>To remove an annotation, shift-click on it"+
					"<p>in the Annotations mode."+
					"<p>If there is overlap, the Annotation with center"+
					"<p>closest to the mouse point will be removed.";

	
	///////////////////////////////////////////////Object constructor////////////////////////////////
	
	/**
	 * Boolean constructor required for internal functions
	 * @param giveData Boolean
	 */
	public GraphicalInterface(boolean giveData){
	} 
	

	/**
	 * Default constructor
	 */
	public GraphicalInterface() {
		
		super(new BorderLayout()); //Create general layout

		//ArrayList initialisations
		for(int i = 0; i < tabLimit; i++) {
			nbNodes_.add(new ArrayList<Integer>());							//initialise lists of nodes
			edgePaint.add(new EdgePainter());								//edge painter objects initialise
			jung.add(new JungGraphVisualisation());							//JUNG graph objects initialise
		}
		
		//Initialise the strategy selection
		selectedServerSelStrategy[tabIndex] 	= "minimum_distance";
		selectedConPlaceStrategy[tabIndex] 		= "lps";  
		selectedRoutingScheme[tabIndex] 		= "shortest_path";  
		
		//Initialise all structures
		netwMetricsResults[tabIndex] 			= new ArrayList<Double>(); 
		cacheMetricsResults[tabIndex] 			= new ArrayList<Double>();
		topologicalMetricsResults[tabIndex] 	= new ArrayList<Double>();
		contentMetricsResults[tabIndex] 		= new ArrayList<Double>();
		contentLists[tabIndex] 					= new HashMap<Integer,Double>();
		cacheLists[tabIndex] 					= new HashMap<Integer,Double>();
		cacheListsStatus[tabIndex]				= new HashMap<Integer,Boolean>();	
		listCaches[tabIndex] 					= new ArrayList<String>();		
		contentPlacementMap[tabIndex] 			= new HashMap<String,HashMap<String,String>>();
		edgeTable[tabIndex] 					= new Hashtable<String,ArrayList<Double>>();
		cacheTable[tabIndex] 					= new Hashtable<String,ArrayList<Double>>(); 
		nodeTable[tabIndex] 					= new Hashtable<String,ArrayList<Double>>();
		contentTable[tabIndex] 					= new Hashtable<String,ArrayList<Double>>();
		managementOption[tabIndex]				= -1; //default value
		
		//Check if tmp folder exists in root location. Create if not present
		tmpFolder = new File(fileLocation[tabIndex]+File.separator+"tmp");			
		if(!tmpFolder.exists()) 
			tmpFolder.mkdir();

		//Create the log first, because the action listeners need to refer to it.
		log = new JTextPane(); 
		log.setMargin(new Insets(10,10,10,10));
		log.setEditable(false);

		//Create scrollable dialog log
		logScrollPane = new JScrollPane(log);								
		logScrollPane.setPreferredSize(new Dimension(logWidth, logHeight));	//set size
		logScrollPane.setMinimumSize(new Dimension(100,40));				//set min size

		//Tab topology viewpane
		GUItabs = new JTabbedPane();										
		JPanel empty = new JPanel();										//create empty JPanel to refer to 
		GUItabs.addTab(topologyName[tabIndex], empty);						//add first topology to tabbed viewpane
		GUItabs.setPreferredSize(new Dimension(300,300));					//set def size

		//Listener for tab changing
		GUItabs.addChangeListener(new ChangeListener(){						
			public void stateChanged(ChangeEvent e) {
				tabIndex = GUItabs.getSelectedIndex();						//get New tab Index
				System.out.println("Tab : "+tabIndex);						//Print Current tab Index
			}
		});

		//Create a file chooser for file import selection
		fc = new JFileChooser();													
		lpane[tabIndex] = new JLayeredPane();										//create new layer for graph
		lpane[tabIndex].setPreferredSize(new Dimension(graphWidth, graphHeight));	//set sizes
		lpane[tabIndex].setSize(new Dimension(graphWidth, graphHeight));
		
		//Create menubar object
		menubar = new JMenuBar();													
		///Main JMenu components
		JMenu file 			= new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		JMenu Select 		= new JMenu("Select");
		JMenu preferences 	= new JMenu("Preferences");
		JMenu show 			= new JMenu("Show");
		JMenu Topo 			= new JMenu("Topology");
		JMenuItem mntmHelp 	= new JMenuItem("Help");
		mntmHelp.setMnemonic(KeyEvent.VK_H);

		///Inner JMenu components
		JMenu 		setMultiNodeas 					= new JMenu("Set Highlighted Nodes as:");
		JMenu 		showGraph 						= new JMenu("Show network topology as:");
		JMenuItem 	editing 						= new JMenuItem("Edit topology properties");
		JMenuItem   showEdgeColorExplanationTable 	= new JMenuItem ("Edge Color Explanation Table");
		JMenuItem 	topoProperties 					= new JMenuItem("Topology Properties");

		//Import components and shortcut keys 
		JMenu imp 									= new JMenu("Import");
		imp.setMnemonic(KeyEvent.VK_M);
		
		JMenu infraConfig 							= new JMenu("Infrastructure Configuration");
		infraConfig.setMnemonic(KeyEvent.VK_I);
		
		JMenu serviceConfig 							= new JMenu("Service Configuration");
		serviceConfig.setMnemonic(KeyEvent.VK_S);
		
		JMenu mgtConfig 							= new JMenu("Management Configuration");
		mgtConfig.setMnemonic(KeyEvent.VK_I);
		
		
		JMenuItem netTop 							= new JMenuItem("Network Topology");
		netTop.setMnemonic(KeyEvent.VK_1);
		netTop.setAccelerator(KeyStroke.getKeyStroke('1', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem cacheConf 						= new JMenuItem("Caching Configuration");
		cacheConf.setMnemonic(KeyEvent.VK_3);
		cacheConf.setAccelerator(KeyStroke.getKeyStroke('3', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem conFile 							= new JMenuItem("Content Configuration");
		conFile.setMnemonic(KeyEvent.VK_4);
		conFile.setAccelerator(KeyStroke.getKeyStroke('4', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem nodeAttributeFile 				= new JMenuItem("Node Attribute Configuration");
		nodeAttributeFile.setMnemonic(KeyEvent.VK_2);
		nodeAttributeFile.setAccelerator(KeyStroke.getKeyStroke('2', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem demFile 							= new JMenuItem("Demand Profile");
		demFile.setMnemonic(KeyEvent.VK_5);
		demFile.setAccelerator(KeyStroke.getKeyStroke('5', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem pathFile 		    				= new JMenuItem("Path Configuration");
		pathFile.setMnemonic(KeyEvent.VK_6);
		pathFile.setAccelerator(KeyStroke.getKeyStroke('6', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem placementFile 		    		= new JMenuItem("Placement Configuration");
		placementFile.setMnemonic(KeyEvent.VK_7);
		placementFile.setAccelerator(KeyStroke.getKeyStroke('7', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenuItem serverSelectionFile 		    	= new JMenuItem("Server Selection Configuration");
		serverSelectionFile.setMnemonic(KeyEvent.VK_8);
		serverSelectionFile.setAccelerator(KeyStroke.getKeyStroke('8', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		//Server Selection Setting components
		JMenu server_Selection 				= new JMenu("Server Selection Strategy");
		final JCheckBoxMenuItem SSstrategy1 = new JCheckBoxMenuItem("Closest Cache - Minimum Distance");
		final JCheckBoxMenuItem SSstrategy2 = new JCheckBoxMenuItem("Round Robin");

		//Content Placement Setting components
		JMenu content_Placement 			= new JMenu("Content Placement Strategy");
		final JCheckBoxMenuItem lps 		= new JCheckBoxMenuItem("Local Popularity Strategy (LPS)");

		//Routing Scheme Setting components
		JMenu routing_scheme 				 = new JMenu("Routing Scheme");
		final JCheckBoxMenuItem shortestPath = new JCheckBoxMenuItem("Shortest Path");

		//Topology Display Setting components
		final JCheckBoxMenuItem circleLayout = new JCheckBoxMenuItem("Circle Layout");
		final JCheckBoxMenuItem FRLayout	 = new JCheckBoxMenuItem("FR Layout");
		final JCheckBoxMenuItem ISOMLayout 	 = new JCheckBoxMenuItem("ISOM Layout");
		final JCheckBoxMenuItem KKLayout 	 = new JCheckBoxMenuItem("KK Layout");

		//Instantiated the File Chooser Object, disabling multiple selection and set initial directory
		fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File(fileLocation[tabIndex])); 	//Set initial location to be user defined root
		fc.addChoosableFileFilter(new FileFilter() { 		//Filter to limit user to select JSON files
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".json");
			}
			@Override
			public String getDescription() {
				return "JSON files";
			}
		});
		
		//Action Listener to import topology file
		netTop.addActionListener(new ActionListener() {							
			public void actionPerformed(ActionEvent event) {
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);	//user option selection confirm/cancel
				if(returnVal == JFileChooser.APPROVE_OPTION) {					//user approved a file
					netTopFileJson[tabIndex] = fc.getSelectedFile();				//get file from file chooser
					int filecheck = checkFileType(netTopFileJson[tabIndex]);		//get file classification (JSON, TXT or Invalid)
					if(filecheck==1){              								//if Text file print error 
						appendError("Import using JSON format");
					}
					else if(filecheck==2) { 									//if JSON selected
						append("The JSON file you selected is "+netTopFileJson[tabIndex].toString()+"\n");
						//netTopFileJson[tabIndex] = netTopFile[tabIndex]; 	//keep the Json location
						topologyConfHasImported[tabIndex] = true;			//set imported as true
						getNodeIDs(netTopFileJson[tabIndex]); 					//get list of Node IDs
						nt[tabIndex] = new NodeType(nbNodes_.get(tabIndex));//Set each node as a new object of default type
						//Enable new menu options
						showNodeTypes.setEnabled(true);
						sortBy.setEnabled(true);
						createNewLink.setEnabled(true);
						addNode.setEnabled(true);
						deleteNode.setEnabled(true);
					}
					else{
						appendError("Error: The network topology file you imported is not correct. Check the extension. (Only .json type files accepted). \n");
					}
				}
				else if(returnVal == JFileChooser.CANCEL_OPTION){
					append("Action cancelled\n");
				}
			}
		});//end Action Listener to import topology file

		//Action Listener for importing caching configuration
		cacheConf.addActionListener(new ActionListener() {							
			public void actionPerformed(ActionEvent event) {						
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);		//User selection result
				if(returnVal == JFileChooser.APPROVE_OPTION) {						//if file is approved
					cacheConfFileJson[tabIndex] = fc.getSelectedFile();					//get file
					if(checkFileType(cacheConfFileJson[tabIndex])==2) { 				//if JSON
						cacheConfHasImported[tabIndex] = true;						//set imported as true
						append("The JSON file you selected is "+cacheConfFileJson[tabIndex].toString()+"\n");
						try {	
							readCacheListFromFileJson(cacheConfFileJson[tabIndex]);		//read list of node objects
						}
						catch(Exception e) {
							e.printStackTrace();
							cacheConfHasImported[tabIndex] = false;					//set imported to false due to error
						}
					}//end if JSON
					else if(checkFileType(cacheConfFileJson[tabIndex])==1) { //if user chooses txt file
						appendError("Import using JSON format"); //Only allow JSON format
					}//end if .txt
					else {
						appendError("Error: The cache configuration file you imported is not correct. Check the extension. (Only .json type files accepted). \n");
						cacheConfHasImported[tabIndex] = false;
					}

				}//end if correct option
				else if(returnVal == JFileChooser.CANCEL_OPTION){
					append("Action cancelled\n");
				} //end else if cancel
			}//end action performed
		}); //end action listener for import caching configuration

		//Action Listener for importing demand configuration
		demFile.addActionListener(new ActionListener() {							
			public void actionPerformed(ActionEvent event) {						
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);		//get return option
				if (returnVal == JFileChooser.APPROVE_OPTION) {						//if approved
					demFJson[tabIndex] = fc.getSelectedFile();							//get file
					append("The file you selected is " + demFJson[tabIndex].getAbsolutePath()+ "\n");
					try{
						if((demFJson[tabIndex].getName().substring(demFJson[tabIndex].getName().lastIndexOf("."))).equals(".json")) { //if JSON
							JSONParser parser = new JSONParser();					//parse JSON
							Object obj = parser.parse(new FileReader(demFJson[tabIndex]));

							JSONObject jsonObject = (JSONObject) obj; //object parsed is whole demand file
							Double alphaJ = Double.parseDouble((String)jsonObject.get("alpha")); 	//get JSON alpha, cast to string and parse to double
							Double betaJ = Double.parseDouble((String)jsonObject.get("beta"));		//get Beta
							alpha[tabIndex] = alphaJ.doubleValue();
							beta[tabIndex] = betaJ.doubleValue();
						}
						else {		                       
							appendError("Import using JSON format");
							//text version of file disabled
						}

					}catch(Exception e){
						appendError("Error: The demand file you imported has an incorrect format. Check the filename of the path file. \n");
						e.printStackTrace();
					}
				}
				else if(returnVal == JFileChooser.CANCEL_OPTION){
					append("Action cancelled\n");
				}//end else if
			}//end action perfomed
		});//end lAction Listener for importing demand configuration

		//Action Listener for importing content configuration
		conFile.addActionListener(new ActionListener() {					
			public void actionPerformed(ActionEvent event) { 				
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);//user selection
				if (returnVal == JFileChooser.APPROVE_OPTION) {				//if approved
					conFJson[tabIndex] = fc.getSelectedFile();					//get file
					if(checkFileType(conFJson[tabIndex])==2) { //if JSON
						contentConfHasImported[tabIndex] = true;			//set imported to true
						append("The JSON file you selected is " + conFJson[tabIndex].toString() + "\n");
						try {
							readContentListFromFileJson(conFJson[tabIndex]);//read content list
						}
						catch (Exception e) {
							e.printStackTrace();
							appendError("Error: The content configuration file you imported has an incorrect format. Check the contents of the .json file. \n");
							contentConfHasImported[tabIndex] = false;		//set imported to false due to error
						}
					}
					else if(checkFileType(conFJson[tabIndex])==1)
					{
						appendError("Import using JSON format");
						//txt file importing disabled
					}
					else{
						appendError("Error: The content configuration file you imported is not correct. Check the extension. \n");
					}
				}
				else if(returnVal == JFileChooser.CANCEL_OPTION) {
					append("Action cancelled\n");
				}     
			}
		});//end action listener for importing content configuration

		//Action Listener for importing path configuration
		pathFile.addActionListener(new ActionListener() {					
			public void actionPerformed(ActionEvent event) {
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);//get user option	
				if (returnVal == JFileChooser.APPROVE_OPTION) {				
					pathFJson[tabIndex] = fc.getSelectedFile();					//get file	
					if(checkFileType(pathFJson[tabIndex])==2){ 					//if JSON
						pathConfHasImported[tabIndex] = true;	
						append("The JSON file you selected is " + pathFJson[tabIndex].getAbsolutePath() + "\n");
					}//end if file
					else {
						appendError("Import a valid Path JSON file");
					}
				}
				else if(returnVal == JFileChooser.CANCEL_OPTION){
					append("You pressed cancel\n");
				}
			}
		});//end Action Listener for importing path configuration

		//Action Listener for importing node attribute configuration
		nodeAttributeFile.addActionListener(new ActionListener() 				
		{
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					nodeTypeJson[tabIndex] = fc.getSelectedFile();					//get file
					if(checkFileType(nodeTypeJson[tabIndex])==2)					//if JSON
					{
						append("The JSON file you selected is " + nodeTypeJson[tabIndex].toString() + "\n");
						nt[tabIndex].readAttributeListJson(nodeTypeJson[tabIndex]);//read node type list
						nodeConfHasImported[tabIndex]=true;							//set imported to true
						if(graphHasCreated[tabIndex])								//repaint graph with new node types
							repaint();
					}
					else if(checkFileType(nodeTypeJson[tabIndex])==1)					//txt file import is disabled
					{
						appendError("Import using JSON format");
					}
					else{
						appendError("Error: The content configuration file you imported is not correct. Check the extension. (Only .json type files accepted). \n");
					}
				}
				else if(returnVal == JFileChooser.CANCEL_OPTION){
					append("You pressed cancel\n");
				}                  
			}
		});//end listener
		
		//Action Listener for importing content placement configuration
		placementFile.addActionListener(new ActionListener() {					
			public void actionPerformed(ActionEvent event) {
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);//get user option	
				if (returnVal == JFileChooser.APPROVE_OPTION) {				
					contentPlacementConfigurationFile[tabIndex] = fc.getSelectedFile();					//get file	
					if(checkFileType(contentPlacementConfigurationFile[tabIndex])==2){ 					//if JSON
						placementConfHasImported[tabIndex] = true;
						append("The JSON file you selected is " + contentPlacementConfigurationFile[tabIndex].getAbsolutePath() + "\n");
						try {	
							readContentPlacementFromFileJson(contentPlacementConfigurationFile[tabIndex]);		//create the content placement map
						}
						catch(Exception e) {
							e.printStackTrace();
							placementConfHasImported[tabIndex] = false;					//set imported to false due to error
						}
					}//end if file
					else {
						appendError("Import a valid Path JSON file");
					}
				}
				else if(returnVal == JFileChooser.CANCEL_OPTION){
					append("You pressed cancel\n");
				}
			}
		});//end Action Listener for importing content placement configuration
		
		//Action Listener for importing server selection configuration
		serverSelectionFile.addActionListener(new ActionListener() {					
			public void actionPerformed(ActionEvent event) {
				int returnVal = fc.showOpenDialog(GraphicalInterface.this);//get user option	
				if (returnVal == JFileChooser.APPROVE_OPTION) {				
					serverSelectionConfigurationFile[tabIndex] = fc.getSelectedFile();					//get file	
					if(checkFileType(serverSelectionConfigurationFile[tabIndex])==2){ 					//if JSON
						serverSelectionConfHasImported[tabIndex] = true;
						append("The JSON file you selected is " + serverSelectionConfigurationFile[tabIndex].getAbsolutePath() + "\n");
					}//end if file
					else {
						appendError("Import a valid Path JSON file");
					}
				}
				else if(returnVal == JFileChooser.CANCEL_OPTION){
					append("You pressed cancel\n");
				}
			}
		});//end Action Listener for importing server selection configuration
		
		//Add import menu options to import menu
		infraConfig.add(netTop);
		infraConfig.add(nodeAttributeFile);
		infraConfig.add(cacheConf);
		serviceConfig.add(conFile);
		serviceConfig.add(demFile);
		mgtConfig.add(pathFile);
		mgtConfig.add(placementFile);
		mgtConfig.add(serverSelectionFile);

		imp.add(infraConfig);
		imp.add(serviceConfig);
		imp.add(mgtConfig);
		
		
		//Local Popularity Strategy is selected by default
		lps.setSelected(true);
		lps.setHorizontalTextPosition(JMenuItem.RIGHT); //set keyboard shortcut
		lps.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		lps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				selectedConPlaceStrategy[tabIndex] = "lps"; //set string to lps
				append("Content Placement Strategy changed to Local Popularity Strategy (LPS)\n" );
				if(graphHasCreated[tabIndex]){
					updateInfo();					//update info panel
				}
			}
		});//end listener lps    
		
		
		//Closest cache - Minimum Distance
		SSstrategy1.setSelected(true); //set as default value 
		SSstrategy1.setHorizontalTextPosition(JMenuItem.RIGHT); //set shortcut
		SSstrategy1.setAccelerator(KeyStroke.getKeyStroke('M', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		SSstrategy1.addActionListener(new ActionListener() { //listener
			public void actionPerformed(ActionEvent event) {
				
				//set other strategies to false
				if (SSstrategy2.isSelected()){ 	
					SSstrategy2.setSelected(false);
				}
				selectedServerSelStrategy[tabIndex] = "minimum_distance";
				append("Server Selection Strategy changed to Closest Cache - Minimum Distance\n" );
				if(graphHasCreated[tabIndex]){
					updateInfo();				//update info panel
				}
			}
		});//end listener

		//Round Robin Strategy
		SSstrategy2.setHorizontalTextPosition(JMenuItem.RIGHT); //Set shortcut
		SSstrategy2.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		SSstrategy2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				//set other options to false
				if (SSstrategy1.isSelected()){
					SSstrategy1.setSelected(false);
				}
				selectedServerSelStrategy[tabIndex] = "round_robin";
				append("Server Selection Strategy changed to Round Robin\n" );
				if(graphHasCreated[tabIndex])
				{
					updateInfo();			//update user info
				}
			}
		});  //end listener


		//Set shortest path routing scheme as default
		shortestPath.setSelected(true);
		shortestPath.setHorizontalTextPosition(JMenuItem.RIGHT);//set keyboard shortcut
		shortestPath.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		shortestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				selectedRoutingScheme[tabIndex] = "shortest_path";
				append("Routing Scheme changed to Shortest Path\n" );
				if(graphHasCreated[tabIndex])
				{
					updateInfo();
				}
			}
		});  //end listener

		//Layout options for topology
		circleLayout.setHorizontalTextPosition(JMenuItem.RIGHT); //set keyboard shortcuts
		circleLayout.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		circleLayout.addActionListener(new ActionListener() { //listener
			public void actionPerformed(ActionEvent event) {
				//set other options to false
				if (FRLayout.isSelected()){
					FRLayout.setSelected(false);
				}
				else if(ISOMLayout.isSelected()){
					ISOMLayout.setSelected(false);
				}
				else if(KKLayout.isSelected()){
					KKLayout.setSelected(false);
				}      
				selectedLayout = "Circle Layout";
				append("Topology layout set to Circle\n" );

				if(graphHasCreated[tabIndex]) {        			//if there is a graph set                   
					if(startMetricPressed[tabIndex] == true){	//if the caching algorithm has been run
						lpane[tabIndex].removeAll();			//remove all objects
						updateUI();								//update interface
						
						//get new topology object
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						//set edge painting scheme
						edgePaint.get(tabIndex).setTable(edgeTable[tabIndex]);
						server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint.get(tabIndex));
						repaint();				//recolour objects
						updateGUI();			//update and relayout
						updateInfoStatistics();	//update information panel
						updateUI();   			//update interface
					}
					else if(startMetricPressed[tabIndex] == false){ //if caching algo not run 
						lpane[tabIndex].removeAll();				//remove lpane grap
						updateUI();									
						//get new graph object
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						//udpate GUI routine				
						updateGUI();
						updateInfo();
						updateUI();
					}
				} 
				else{
					appendWarning("Warning: Import the network topology file and press the \"Create Network Topology\" button to see the result. \n");
				}
				graphViewChanged[tabIndex] = true; 
			}
		}); //end listener for circle layout          

		FRLayout.setSelected(true);
		FRLayout.setHorizontalTextPosition(JMenuItem.RIGHT);
		FRLayout.setAccelerator(KeyStroke.getKeyStroke('F', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		FRLayout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (circleLayout.isSelected()){
					circleLayout.setSelected(false);
				}
				else if(ISOMLayout.isSelected()){
					ISOMLayout.setSelected(false);
				}
				else if(KKLayout.isSelected()){
					KKLayout.setSelected(false);
				}
           
				selectedLayout = "FR Layout";
				append("Topology layout set to Circle\n" );
				if(graphHasCreated[tabIndex]){
					if(startMetricPressed[tabIndex] == true){
						lpane[tabIndex].removeAll();
						updateUI();
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						edgePaint.get(tabIndex).setTable(edgeTable[tabIndex]);
						server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint.get(tabIndex));
						repaint();
						updateGUI();
						updateInfoStatistics();
						updateUI();
					}
					else if(startMetricPressed[tabIndex] == false){
						lpane[tabIndex].removeAll();
						updateUI();
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						updateGUI();
						updateInfo();
						updateUI();
					}
				}
				else{
					appendWarning("Warning: Import the network topology file and press the \"Create Network Topology\" button to see the result. \n");
				}
				graphViewChanged[tabIndex] = true;
			}
		});  //end listener for FR layout

		ISOMLayout.setHorizontalTextPosition(JMenuItem.RIGHT);
		ISOMLayout.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		ISOMLayout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (circleLayout.isSelected()){
					circleLayout.setSelected(false);
				}
				else if(FRLayout.isSelected()){
					FRLayout.setSelected(false);
				}
				else if(KKLayout.isSelected()){
					KKLayout.setSelected(false);
				}
            
				selectedLayout = "ISOM Layout";
				append("Topology layout set to ISOM\n" );
				if(graphHasCreated[tabIndex]) {
					if(startMetricPressed[tabIndex] == true){
						lpane[tabIndex].removeAll();
						updateUI();
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						edgePaint.get(tabIndex).setTable(edgeTable[tabIndex]);
						server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint.get(tabIndex));
						repaint();
						updateGUI();
						updateInfoStatistics();
						updateUI();
					}
					else if(startMetricPressed[tabIndex] == false){
						lpane[tabIndex].removeAll();
						updateUI();
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						updateGUI();
						updateInfo();
						updateUI();
					}
				}
				else{
					appendWarning("Warning: Import the network topology file and press the \"Create Network Topology\" button to see the result. \n");

				}
				graphViewChanged[tabIndex] = true;
			}
		}); //end listener for ISOM layout      

		KKLayout.setHorizontalTextPosition(JMenuItem.RIGHT);
		KKLayout.setAccelerator(KeyStroke.getKeyStroke('K', Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		KKLayout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (circleLayout.isSelected()){
					circleLayout.setSelected(false);
				}
				else if(FRLayout.isSelected()){
					FRLayout.setSelected(false);
				}

				else if(ISOMLayout.isSelected()){
					ISOMLayout.setSelected(false);
				}             
				selectedLayout = "KK Layout";
				append("Topology layout set to KK\n" );
				if(graphHasCreated[tabIndex]){
					if(startMetricPressed[tabIndex] == true){
						lpane[tabIndex].removeAll();
						updateUI();
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						edgePaint.get(tabIndex).setTable(edgeTable[tabIndex]);
						server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint.get(tabIndex));
						repaint();
						updateGUI();
						updateInfoStatistics();
						updateUI();
					}
					else if(startMetricPressed[tabIndex] == false){
						lpane[tabIndex].removeAll();
						updateUI();
						server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
						updateGUI();
						updateInfo();
						updateUI();
					} 
				}
				else{
					appendWarning("Warning: Import the network topology file and press the \"Create Network Topology\" button to see the result. \n");
				}
				graphViewChanged[tabIndex] = true;
			}
		});  //end listener for KK layout

		
		
		/////////////////////////Topology properties editor menu//////////////////////////////////////
		
		editing.addActionListener(new ActionListener() { 			
			public void actionPerformed(ActionEvent event) {
				if(graphHasCreated[tabIndex] == false){				//disable editing until graph is generated
					appendError("Error: Import the network topology and press \"Create Network Topology\" button first\n");
				}
				else{
					if ((graphPropertiesEditorOpenedBefore[tabIndex] == false) || (graphViewChanged[tabIndex] == true)) {
						//Setup dialog box to edit topology
						graphPropertiesEditorJD = new JDialog(frame, "Topology Properties Editor");
						graphPropertiesEditorJD.getContentPane().setLayout(new BorderLayout());
						graphPropertiesEditorJD.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);  
						graphPropertiesEditorJD.getContentPane().add(new GraphPropertiesEditor<String,String>(server),BorderLayout.NORTH);
						JPanel buttonPane = new JPanel();
						//create close button and listener to close pane
						JButton button = new JButton("Close");  
						buttonPane.add(button);
						button.addActionListener(new ActionListener() {                 	 
							public void actionPerformed(ActionEvent e)
							{                   	
								graphPropertiesEditorJD.setVisible(false);
							}
						});  
						//Layout and make visible the editor
						graphPropertiesEditorJD.add(buttonPane, BorderLayout.SOUTH);              
						graphPropertiesEditorJD.pack();
						graphPropertiesEditorJD.setVisible(true);
						//set opened before as true
						graphPropertiesEditorOpenedBefore[tabIndex] = true;  
						graphViewChanged[tabIndex] = false;
					}//if
					else if ((graphPropertiesEditorOpenedBefore[tabIndex] == true) && (graphViewChanged[tabIndex] == false)){
						graphPropertiesEditorJD.setVisible(true);
					}
				}//else     	
			}
		});//end listener for editing mode

		
		//Create a new window showing all the obtained Topological Metrics
		showTopologicalMetrics.addActionListener(new ActionListener(){ //here
			public void actionPerformed(ActionEvent e) {
				
				System.out.println(topologyConfHasImported[tabIndex]);
				
				if(topologyConfHasImported[tabIndex]==true)				//if topology has imported
				{
					nodeName = new String[nbNodes_.get(tabIndex).size()];	//get list of nodes
					for(int i = 0; i<nbNodes_.get(tabIndex).size();i++){
						nodeName[i] = String.valueOf(nbNodes_.get(tabIndex).get(i));
					}
					nodeBox = new JComboBox(nodeName);				//selection box from list of current nodes		
					
					double maxpl = topologicalMetricsResults[tabIndex].get(1);	//return max path length
					double minpl = topologicalMetricsResults[tabIndex].get(0);	//return min path length
					double avgpl = topologicalMetricsResults[tabIndex].get(2);//avg path length
					//message HTML string
					String msg1 = "<html><body>" +
							"<font size=\"5\" color=\"red\"> <b><u>Network Level</u></b></font><br></br> <br></br>"
							+ "<table border=\"1\" style=\"width:300px\">"
							+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Maximum Path Length: </u></b></td><td>" +  maxpl +"</td></tr>"
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Minimum Path Length: </u></b></td><td>" + minpl + "</td></tr>" 
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Path Length:  </u></b></td><td>" + avgpl +"</td></tr>"
							+ "</font>" 
							+ "</body></html>";
					//Change listener for new node selection
					nodeBox.addItemListener(
							new ItemListener()
							{
								public void itemStateChanged(ItemEvent e)
								{
									if(e.getStateChange()==ItemEvent.SELECTED)
									{
										TMnode = Integer.parseInt((nodeName[nodeBox.getSelectedIndex()])); //get index of currently selected node
										//get and format various node stats
										double dc = nodeTable[tabIndex].get("["+TMnode+"]").get(0);
										double cc = nodeTable[tabIndex].get("["+TMnode+"]").get(1);
										double adf = nodeTable[tabIndex].get("["+TMnode+"]").get(2);
										//set HTML message string of statistics box
										TMmsgPart2.setText("<html><body>" +
												"<font size=\"5\" color=\"red\"> <b><u>Node Level</u></b></font><br></br> <br></br>"
												+ "<table border=\"1\" style=\"width:300px\">"
												+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Degree of Connectivity of Node "+TMnode+":  </u></b></td><td>" + dc +"</td></tr>"
												+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Clustering Coefficient of Node "+TMnode+":  </u></b></td><td>" + cc +"</td></tr>"
												+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Distance Factor of Node "+TMnode+":  </u></b></td><td>" + adf +"</td></tr>" +
												"</table> <br></br>"
												+ "</font>" 
												+ "</body></html>"
												);
									}
								}
							});//end change listener for node box 
					//get statistics for node
					double dc = nodeTable[tabIndex].get("["+TMnode+"]").get(0);
					double cc = nodeTable[tabIndex].get("["+TMnode+"]").get(1);
					double adf = nodeTable[tabIndex].get("["+TMnode+"]").get(2);
					//set HTML message of stats
					String msg2 = "<html><body>" +
							"<font size=\"5\" color=\"red\"> <b><u>Node Level</u></b></font><br></br> <br></br>"
							+ "<table border=\"1\" style=\"width:300px\">"
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Degree of Connectivity of Node "+TMnode+":  </u></b></td><td>" + dc +"</td></tr>"
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Clustering Coefficient of Node "+TMnode+":  </u></b></td><td>" + cc +"</td></tr>"
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Distance Factor of Node "+TMnode+":  </u></b></td><td>" + adf +"</td></tr>" +
							"</table> <br></br>"
							+ "</font>" 
							+ "</body></html>";
					//make labels of these node stats
					TMmsgPart1 = new JLabel(msg1);
					TMmsgPart2 = new JLabel(msg2);
					JLabel nodeLabel = new JLabel("Select Node:");

					JFrame showTM = new JFrame("Topological Metrics");
					//show mesage
					showTM.add(TMmsgPart1, BorderLayout.NORTH);
					showTM.add(TMmsgPart2, BorderLayout.CENTER);
					showTM.add(nodeLabel, BorderLayout.SOUTH);
					showTM.add(nodeBox, BorderLayout.SOUTH);
					//set layout and design of this Topological Message stats report for each node
					showTM.setLayout(new FlowLayout());
					showTM.setSize(new Dimension(420,360));
					showTM.setResizable(false);
					showTM.setVisible(true);
					showTM.setAlwaysOnTop(true);
					int wTM = (int) (screenwidth-420)/2; //set size and width of TM box
					int hTM = (int) (screenheight-360)/2;
					showTM.setLocation(wTM, hTM);
				}else
				{
					appendError("Error: Import the Network topology file first\n");
				}
			}	
		});//end listener for topological metrics box. 
		
		
		//Create a new window all the available NodeID with their respective Node types
		showNodeTypes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(topologyConfHasImported[tabIndex]==true)
				{
					nodeID = 1;												//default selection box value
					stringIndex = 0;										//index value default 
					JFrame showNT = new JFrame("Node Attribute Editor");	//attribute editor title
					JPanel pNT = new JPanel(new FlowLayout());				//set layout and establish JPanel
					nodeTag = new JComboBox(nt[tabIndex].getTypes()); 		//get list of possible node types
					nodeTag.addItemListener(new ItemListener(){				//listener for changing node type index
						public void itemStateChanged(ItemEvent e) {
							if(e.getStateChange()==ItemEvent.SELECTED){
								stringIndex = nodeTag.getSelectedIndex();
							}
						}
					});
					nodeName = new String[nbNodes_.get(tabIndex).size()];	//get list of ndoes
					for(int i = 0; i<nbNodes_.get(tabIndex).size();i++){
						nodeName[i] = String.valueOf(nbNodes_.get(tabIndex).get(i));
					}
					nodeboxID = new JComboBox(nodeName);					//dropdown box for choosing a node
					nodeboxID.addItemListener(new ItemListener(){			//listener for getting a new node ID from drop down box
						public void itemStateChanged(ItemEvent e){
							nodeID = nodeboxID.getSelectedIndex();
							nodeID = nbNodes_.get(tabIndex).get(nodeID);
						}
					});
					JButton confirm = new JButton("Apply");					//change node type confirm button and listener
					confirm.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							append(nodeID+" changed\n");					
							//set node type according to index selected
							switch(stringIndex) {
							case 0:
								nt[tabIndex].setNodeType(nodeID, "DEFAULT");
								break;
							case 1:
								nt[tabIndex].setNodeType(nodeID, "CORE_NODE");
								break;
							case 2:
								nt[tabIndex].setNodeType(nodeID, "EDGE_NODE");
								break;
							}
							//Create HTML box text
							String message = "<html><body>" + "<table border=\"1\" style=\"width:300px\">";
							for(int i = 0; i<nbNodes_.get(tabIndex).size(); i++)
							{
								message += "<tr><td><font size=\"4\" color=\"black\"><b><u>Node ID "+nbNodes_.get(tabIndex).get(i)+":  </u></b></td><td>" + nt[tabIndex].getNodeType(nbNodes_.get(tabIndex).get(i)) +"</td></tr>";
							}
							message +="</table><br></br>"+ "</font>"+"</body></html>";
							//set dialog box text as table
							NTmsg.setText(message);		
							repaint();//repaint topology 
						}
					});
					//Set HTML message string
					String message = "<html><body>"+ "<table border=\"1\" style=\"width:300px\">";
					for(int i = 0; i<nbNodes_.get(tabIndex).size(); i++)
					{
						message += "<tr><td><font size=\"4\" color=\"black\"><b><u>Node ID "+nbNodes_.get(tabIndex).get(i)+":  </u></b></td><td>" + nt[tabIndex].getNodeType(nbNodes_.get(tabIndex).get(i)) +"</td></tr>";
					}
					message +="</table><br></br>"+ "</font>"+"</body></html>";
					//Create option export button 
					JButton exportNode = new JButton("Export");
					exportNode.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							JFileChooser chooser  = new JFileChooser(tmpFolder); //choose location
							FileFilter filter = new FileNameExtensionFilter("JSON file", "json"); //set as JSON
							chooser.addChoosableFileFilter(filter);
							chooser.setFileFilter(filter);
							chooser.setSelectedFile(new File("NodeExportFile"));	//suggest name
							int option = chooser.showSaveDialog(null);
							if(option == JFileChooser.APPROVE_OPTION) 				//if user selection location
							{
								File file= chooser.getSelectedFile();
								if(!(checkFileType(file)==(1|2))){					//check file is valid
									file = new File(file+".json");					//create file object
								}
								nt[tabIndex].printAttributeListJson(file);			//print JSON attribute list of current node config
							}
						}
					});//end export listener

					int w = 450;	//set default window size
					int h = 380;	
					NTmsg = new JLabel(message);
					JScrollPane scrNT = new JScrollPane(NTmsg); //Create scroll panel of message
					scrNT.setPreferredSize(new Dimension(w, h));//set default size
					JLabel nodeLabel = new JLabel("Node ID: ");	//set labels
					JLabel TagLabel = new JLabel("Node Type: ");
					pNT.add(nodeLabel);							//add all label items to window
					pNT.add(nodeboxID);
					pNT.add(TagLabel);
					pNT.add(nodeTag);
					pNT.add(confirm);
					//format window
					showNT.add(pNT, BorderLayout.NORTH);
					showNT.add(scrNT, BorderLayout.SOUTH);
					showNT.add(exportNode, BorderLayout.SOUTH);
					showNT.setLayout(new FlowLayout());
					showNT.setSize(new Dimension(w+20,h+125));
					showNT.setResizable(true);
					showNT.setVisible(true);
					int wNT = (int) (screenwidth-(w+20))/2;
					int hNT = (int) (screenheight-(h+100))/2;
					showNT.setLocation(wNT, hNT);
				}else
				{
					appendError("Error: Import the Network topology file first\n");
				}
			}
		});//end show node types listener


		showCacheStatistics.addActionListener(new ActionListener() { //Caching stats message box
			public void actionPerformed(ActionEvent event) {
				if(cacheConfHasImported[tabIndex]){
					//get various caching metrics
					double minCache = cacheMetricsResults[tabIndex].get(5);
					double maxCache = cacheMetricsResults[tabIndex].get(6);
					double avgCache = cacheMetricsResults[tabIndex].get(3);
					//set HTML message of caching metrics
					String msg = "<html><body>" +
							"<font size=\"5\" color=\"red\"> <b><u>Cache statistics</u></b></font><br></br> <br></br>"
							+ "<table border=\"1\" style=\"width:300px\">"
							+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Maximum Cache Capacity:</u></b></td><td>" +  maxCache +" Mbits"+"</td></tr>"
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Minimum Cache Capacity: </u></b></td><td>" + minCache +" Mbits" + "</td></tr>" 
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Cache Capacity:  </u></b></td><td>" + avgCache +" Mbits" +"</td></tr>" +
							"</table> <br></br>"
							+ "</font>" 
							+ "</body></html>";
					//create JLable of HTML string
					JLabel message = new JLabel(msg);
					JOptionPane.showMessageDialog(null, message, "Cache Statistics",JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					appendError("Error: Import the cache configuration file first\n");
				}
			}
		});//end caching stats listener
		
		showContentStatistics.addActionListener(new ActionListener() {//content stats listener
			public void actionPerformed(ActionEvent event) {
				if(contentConfHasImported[tabIndex]) {
					//get content metrics
					double minContent = contentMetricsResults[tabIndex].get(2);
					double maxContent = contentMetricsResults[tabIndex].get(3);
					double avgContent = contentMetricsResults[tabIndex].get(0);
					//set HTML string of content metrics
					String msg = "<html><body>" +
							"<font size=\"5\" color=\"red\"> <b><u>Content statistics</u></b></font><br></br> <br></br>"
							+ "<table border=\"1\" style=\"width:300px\">"
							+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Maximum Content Size:</u></b></td><td>" +  maxContent +" Mbits"+"</td></tr>"
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Minimum Content Size: </u></b></td><td>" + minContent +" Mbits" + "</td></tr>" 
							+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Content Size:  </u></b></td><td>" + avgContent +" Mbits" +"</td></tr>" +
							"</table> <br></br>" 
							+ "</body></html>";
					//set JLAbel of HTML string
					JLabel message = new JLabel(msg);
					JOptionPane.showMessageDialog(null, message, "Content Statistics",JOptionPane.INFORMATION_MESSAGE);
				}//if
				else{
					appendError("Error: Import the content configuration file first\n");
				}
			}
		});//end content stats listener

		showEdgeColorExplanationTable.addActionListener(new ActionListener() {//Listener for edge table
			public void actionPerformed(ActionEvent event) {            
				EdgeColorExplanationTable ecet = new EdgeColorExplanationTable();
				ecet.createColorExplanationTable(); //create table
			}
		});

		//Create a new window showing all the available Node level topological metrics + allows sorting
		sortBy.addActionListener(new ActionListener(){ 
			public void actionPerformed(ActionEvent e) {
				stringIndexTM = 0;
				JFrame sortFrame = new JFrame("Sort Nodes");
				JPanel sortPanel = new JPanel();
				sortPanel.setPreferredSize(new Dimension(500,500));
				JLabel sortLabel = new JLabel("Sort by: ");
				String[] metrics = new String[4];
				metrics[0] = "Node ID";
				metrics[1] = "Connectivity Degree";
				metrics[2] = "Clustering Coefficient";
				metrics[3] = "Average Distance Factor";
				tmVariables = new JComboBox(metrics);
				tmVariables.addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange()==ItemEvent.SELECTED){
							stringIndexTM = tmVariables.getSelectedIndex();
						}
					}
				});
				JButton orderUP = new JButton("Ascending");
				JButton orderDOWN = new JButton("Descending");
				orderUP.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						SortingMethods sort = new SortingMethods();
						List<NodeScore> listSortedNodes = sort.sortNodeByTopoCharacteristicsUP(nodeTable[tabIndex], stringIndexTM);
						String message = "<html><body>"+ "<table border=\"1\" style=\"width:300px\">"
								+"<tr>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Node ID </u></b></td>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Connectivity Degree</u></b></td>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Clustering Coefficient</u></b></td>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Average Distance Factor</u></b></td>"
								+"</tr>";
						for(int i = 0; i< listSortedNodes.size(); i++)
						{
							message += "<tr>"
									+"<td>"+ listSortedNodes.get(i).getNodeID() +"</td>"
									+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(0) +"</td>"
									+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(1)+"</td>"
									+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(2)+"</td>"
									+"</tr>";
						}
						message +="</table><br></br>"+ "</font>"+"</body></html>";
						tmTable.setText(message);
					}
				});
				orderDOWN.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						SortingMethods sort = new SortingMethods();
						List<NodeScore> listSortedNodes = sort.sortNodeByTopoCharacteristicsDOWN(nodeTable[tabIndex], stringIndexTM);
						String message = "<html><body>"+ "<table border=\"1\" style=\"width:300px\">"
								+"<tr>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Node ID </u></b></td>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Connectivity Degree</u></b></td>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Clustering Coefficient</u></b></td>"
								+"<td><font size=\"4\" color=\"black\"><b><u>Average Distance Factor</u></b></td>"
								+"</tr>";
						for(int i = 0; i< listSortedNodes.size(); i++)
						{
							message += "<tr>"
									+"<td>"+ listSortedNodes.get(i).getNodeID() +"</td>"
									+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(0) +"</td>"
									+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(1)+"</td>"
									+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(2)+"</td>"
									+"</tr>";
						}
						message +="</table><br></br>"+ "</font>"+"</body></html>";
						tmTable.setText(message);
					}
				});
				//Default display
				SortingMethods sort = new SortingMethods();
				List<NodeScore> listSortedNodes = sort.sortNodeByTopoCharacteristicsUP(nodeTable[tabIndex], stringIndexTM);
				String message = "<html><body>"+ "<table border=\"1\" style=\"width:300px\">"
						+"<tr>"
						+"<td><font size=\"4\" color=\"black\"><b><u>Node ID </u></b></td>"
						+"<td><font size=\"4\" color=\"black\"><b><u>Connectivity Degree</u></b></td>"
						+"<td><font size=\"4\" color=\"black\"><b><u>Clustering Coefficient</u></b></td>"
						+"<td><font size=\"4\" color=\"black\"><b><u>Average Distance Factor</u></b></td>"
						+"</tr>";
				for(int i = 0; i< listSortedNodes.size(); i++)
				{
					message += "<tr>"
							+"<td>"+ listSortedNodes.get(i).getNodeID() +"</td>"
							+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(0) +"</td>"
							+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(1)+"</td>"
							+"<td>"+nodeTable[tabIndex].get(listSortedNodes.get(i).getNodeID()).get(2)+"</td>"
							+"</tr>";
				}
				message +="</table><br></br>"+ "</font>"+"</body></html>";
				tmTable.setText(message);
				sortPanel.add(sortLabel);
				sortPanel.add(tmVariables);
				sortPanel.add(orderUP);
				sortPanel.add(orderDOWN);
				JScrollPane scrollpane = new JScrollPane(tmTable);
				sortPanel.add(scrollpane);
				scrollpane.setPreferredSize(new Dimension(410,450));
				sortFrame.add(sortPanel);
				sortFrame.setSize(new Dimension(500,500));
				sortFrame.setVisible(true);
			}
		});//end action listener sortBy
		
		nodeattrDefault.addActionListener(new ActionListener(){ //Listener to set node to default
			public void actionPerformed(ActionEvent e) {
				int[][] table = jung.get(tabIndex).getVertices(); //get list of selected vertices
				for(int i = 0; i<table.length;i++){ //loop through list
					if(table[i][1]==1) //if node is selected then [i][1]==1 
					{
						nt[tabIndex].setNodeType(table[i][0], "DEFAULT"); //set to default 
					}
				}
				repaint(); //repaint node
			}
		});//end listener to set to DEFAULT
		
		nodeattrCoreNode.addActionListener(new ActionListener(){ //Set node to core
			public void actionPerformed(ActionEvent e) {
				int[][] table = jung.get(tabIndex).getVertices();//get selected nodes
				for(int i = 0; i<table.length;i++){ //loop through nodes
					if(table[i][1]==1)//if selected 
					{
						nt[tabIndex].setNodeType(table[i][0], "CORE_NODE"); //set to CORE
					}
				}
				repaint();
			}
		});//end set to CORE
		
		nodeattrEdgeNode.addActionListener(new ActionListener(){ //listener to set to EDGE
			public void actionPerformed(ActionEvent e) { 
				int[][] table = jung.get(tabIndex).getVertices(); //get nodes
				for(int i = 0; i<table.length;i++){ //loop through nodes
					if(table[i][1]==1) //if node is selected
					{ 
						nt[tabIndex].setNodeType(table[i][0], "EDGE_NODE"); //set to edge node
					}
				}
				repaint();
			}
		});//end set to EGDE

		//Creates a small window showing all the input configuration variables 
		topoProperties.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFrame propFrame = new JFrame("Topology Properties");//set titles
				propFrame.setLayout(new FlowLayout());
				//set information HTML string
				String message = "<html><body>"+ "<table border=\"1\" style=\"width:300px\">"
						+"<tr><td><font face=\"helvetica\" size=\"4\" color=\"black\"><b>Topology Name</b></td><td>"+topologyName[tabIndex]+"</td></tr>"
						+"<tr><td><font size=\"4\" color=\"black\"><b>Number of Nodes</b></td><td>"+nbNodes_.get(tabIndex).size()+"</td></tr>"
						+"<tr><td><font size=\"4\" color=\"black\"><b>Number of Contents</b></td><td>"+contentLists[tabIndex].size()+"</td></tr>"
						+"<tr><td><font size=\"4\" color=\"black\"><b>Alpha value</b></td><td>"+alpha[tabIndex]+"</td></tr>"
						+"<tr><td><font size=\"4\" color=\"black\"><b>Beta value</b></td><td>"+beta[tabIndex]+"</td></tr>"
						+"</table><br></br>"+ "</font>"+"</body></html>";
				int h = 410; // default sizes
				int w = 225;
				//format new window
				JLabel propLabel = new JLabel(message);
				propFrame.add(propLabel, BorderLayout.CENTER);
				propFrame.setSize(h, w);
				propFrame.setLocation((int) (screenwidth)/2-w, (int) (screenheight-h)/2);
				propFrame.setVisible(true);
				propFrame.setResizable(false);
			}
		});//end import information popup box listener

		//Disable certain components until specific files have been imported    
		showContentStatistics.setEnabled(false);
		showCacheStatistics.setEnabled(false);
		showTopologicalMetrics.setEnabled(false); 
		showNodeTypes.setEnabled(false);
		sortBy.setEnabled(false);
		nodeattrDefault.setEnabled(false);    
		nodeattrCoreNode.setEnabled(false);
		nodeattrEdgeNode.setEnabled(false);
		createNewLink.setEnabled(false);
		addNode.setEnabled(false);
		deleteNode.setEnabled(false);

		server_Selection.add(SSstrategy1);
		server_Selection.add(SSstrategy2);
		content_Placement.add(lps);

		routing_scheme.add(shortestPath);

		JMenuItem fileNew = new JMenuItem("New");
		fileNew.setMnemonic(KeyEvent.VK_N);

		JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.setMnemonic(KeyEvent.VK_O);

		JMenu fileSave = new JMenu("Save Topology");
		fileSave.setMnemonic(KeyEvent.VK_S);

		JMenuItem saveAsImage = new JMenuItem("as Image file");
		fileSave.add(saveAsImage);

		JMenuItem print = new JMenuItem("Print");
		print.setMnemonic(KeyEvent.VK_P);

		JMenuItem closeTab = new JMenuItem("Close Tab");

		JMenuItem fileExit = new JMenuItem("Exit");
		fileExit.setMnemonic(KeyEvent.VK_C);
		
		//Listener to create new tab
		fileNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(GUItabs.getTabCount() < tabLimit){
					createNewWindow();
				}else{
					appendError("Tab Error 1 : You have reached the tab limit");
				}
			}
		});
		
		//Close a tab
		closeTab.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int i = GUItabs.getSelectedIndex();
				if (i != -1) //may need to remove more data to prevent conflict
				{
					graphPropertiesEditorOpenedBefore[i] 	= false;
					graphHasCreated[i] 						= false;
					startCachingPressed[i] 					= false;
					graphViewChanged[i] 					= false;
					contentConfHasImported[i]				= false;
					cacheConfHasImported[i]					= false;
					topologyConfHasImported[i]				= false;
					pathConfHasImported[i]     				= false;
					placementConfHasImported[i] 			= false;
					serverSelectionConfHasImported[i]		= false;
					netTopFileJson[i]						= null;
					cacheConfFileJson[i] 					= null;
					demFJson[i] 							= null;
					conFJson[i] 							= null;
					nodeTypeJson[i]							= null;
					pathFJson[i]							= null;
					contentPlacementConfigurationFile[i] 	= null;
					serverSelectionConfigurationFile[i] 	= null; 
					cacheMetricsFile[i]						= null;
					networkMetricsFile[i] 					= null;
					topologicalMetricsFile[i] 				= null;
					contentMetricsFile[i] 					= null;
					graphPane[i] 							= null;
					nt[i] 									= null;
					managementOption[i]						= -1;
					GUItabs.remove(i);
				}
				if(i==0){
					System.exit(0);
				}
			}
		});//end close tabs listener


		fileExit.setToolTipText("Exit application"); //close program option
		fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				ActionEvent.CTRL_MASK));
		fileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);//exist safely
			}
		});

		//Option to allow the user to export the topology as an image
		saveAsImage.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent event) {
				if(graphHasCreated[tabIndex]){                 
					JFileChooser chooser  = new JFileChooser();
					int option = chooser.showSaveDialog(null);
					if(option == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();
						writeJPEGImage(file);//write the topology as an image file.
					}
				}
				else{
					appendError("Error: Import the network topology file and press the \"Create Network Topology\" button first. \n");
				}
			}
		});//end JPEG write listener

		//Print topology option
		print.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent event) {
				PrinterJob printJob = PrinterJob.getPrinterJob();
				printJob.setPrintable(null);			//setup printing options
				if (printJob.printDialog()) {
					try {
						printJob.print();				//print topology 
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}  
			}     
		});//end printing option 

		//Creates a new window to allow the user to add/remove/override links from the imported topology
		createNewLink.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame(createNewLink.getActionCommand());
				frame.setLayout(new FlowLayout());								//setup new window
				frame.setSize(300, 150);										//set size
				JLabel node1 = new JLabel("Node ID 1:");						//first node label
				JLabel node2 = new JLabel("Node ID 2:");						//second node label
				JLabel adminCost = new JLabel("Administritive Cost:");			//cost label	
				JLabel linkCapLabel = new JLabel("Link Capacity:");				//capacity label
				JLabel linkCapUnit = new JLabel("KBPS");						//units for capacity
				JButton create = new JButton("Create Link");					//confirm button
				JButton deleteLink = new JButton("Delete Link");				//delete button
				final JTextField adCostText = new JTextField("861", 10);		//text field for cost
				final JTextField linkCapText = new JTextField("9920000", 10);	//text field for capacity
				final JTextField linkDelayText = new JTextField("1", 10);		//text field for delay
				nodeName = new String[nbNodes_.get(tabIndex).size()];			//get node list
				for(int i = 0; i<nbNodes_.get(tabIndex).size(); i++)
				{
					nodeName[i] = String.valueOf(nbNodes_.get(tabIndex).get(i));
				}
				final JComboBox nodeBox1 = new JComboBox(nodeName);				//dropdown for node 1 for link
				final JComboBox nodeBox2 = new JComboBox(nodeName);				//dropdown for node 2 for link
				nodeID1 = nodeID2 = 1;	//default values
				nodeBox1.addItemListener(new ItemListener(){					//listener for change in node 1 selection
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange()==ItemEvent.SELECTED){
							nodeID1 = Integer.parseInt((nodeName[nodeBox1.getSelectedIndex()]));//get id of new selected first node
						}
					}
				});//end listener for new first node

				nodeBox2.addItemListener(new ItemListener(){					//listener for change in node 2 
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange()==ItemEvent.SELECTED){
							nodeID2 = Integer.parseInt((nodeName[nodeBox2.getSelectedIndex()]));//get id of second node
						}
					}
				});//end node 2 listener

				create.addActionListener(new ActionListener(){					//create link 	
					public void actionPerformed(ActionEvent e) {
						try{
							if(nodeID1==nodeID2){//do not allow loops in topology 
								appendError("Error: Node IDs must not be the same\n");
							}
							else
							{
								int adCost = Integer.parseInt(adCostText.getText());	//get cost
								int linkCap = Integer.parseInt(linkCapText.getText());	//get capacity
								int linkDelay = Integer.parseInt(linkDelayText.getText()); //get delay
								
								JFileChooser chooser  = new JFileChooser(tmpFolder);	//create file chooser for new topology file locaiton 
								FileFilter filter = new FileNameExtensionFilter("JSON file", "json");
								chooser.addChoosableFileFilter(filter);
								chooser.setFileFilter(filter);
								chooser.setSelectedFile(new File("newTopology.json"));	
								int option = chooser.showSaveDialog(null);
								if(option == JFileChooser.APPROVE_OPTION) 				//user has approved a new file name 
								{
									File file = chooser.getSelectedFile();				//get file
									if(!(checkFileType(file)==(1|2))){					//check file is valid
										file = new File(file.toString());
									}
									//add link and return new file object
									netTopFileJson[tabIndex]=addLinkToJsonTopology(nodeID1,nodeID2,adCost,linkCap,linkDelay,netTopFileJson[tabIndex]);
									//copy routine to export topology for users external location. working version is default put in the TMP folder
									Path srcPath = Paths.get(netTopFileJson[tabIndex].getAbsolutePath());
									Path dstPath = Paths.get(file.getAbsolutePath());
									Files.copy(srcPath,dstPath, StandardCopyOption.REPLACE_EXISTING);	
									append("New JSON topology file with new links saved in file "+netTopFileJson[tabIndex]+"\n");
									//get text version of new topology 
									netTopFileJson[tabIndex] = JSONC.topologyJsonToFile(netTopFileJson[tabIndex],topologyName[tabIndex],tmpFolder);
									//redraw topology with new links 
									lpane[tabIndex].removeAll();
									updateUI();
									server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
									updateGUI();
									updateInfo();
									updateUI();
									append("Link Created \n");
								}//end if approve
							} //end if nodes are not the same
						}//end try
						catch(NumberFormatException error){
							appendError("Error: Admin Cost and Cache Capacity must be Integers\n");
						} catch(IOException e1) {
							appendError("Exception thrown writing new topology file\n");
							e1.printStackTrace();
						}
					}
				});//end new link listener

				deleteLink.addActionListener(new ActionListener(){	//delete link listener
					public void actionPerformed(ActionEvent e) {
						try {
							if(nodeID1==nodeID2){
								appendError("Error: Node IDs must not be the same\n");
							}
							else
							{
								JFileChooser chooser  = new JFileChooser(fileLocation[tabIndex]+File.separator+"tmp");	//location for new topology
								FileFilter filter = new FileNameExtensionFilter("JSON File", "json");			//filter for JSON
								chooser.addChoosableFileFilter(filter);
								chooser.setFileFilter(filter);
								chooser.setSelectedFile(new File("newTopology.json"));							//suggested topology name
								int option = chooser.showSaveDialog(null);

								if(option == JFileChooser.APPROVE_OPTION) 										//user has selected a new file
								{
									File file= chooser.getSelectedFile();//get file
									if(!(checkFileType(file)==(1|2))){	//check type
										file = new File(file.toString());
									}
									//remove link from topology file
									netTopFileJson[tabIndex]=removeLinkFromJsonTopology(nodeID1,nodeID2,netTopFileJson[tabIndex]);
									//get paths for copy to export destination set by user
									Path srcPath = Paths.get(netTopFileJson[tabIndex].getAbsolutePath());
									Path dstPath = Paths.get(file.getAbsolutePath());
									Files.copy(srcPath,dstPath, StandardCopyOption.REPLACE_EXISTING);
									//working topology is in tmp folder. user location of new topology is a copy. 
									append("New JSON topology file with removed links saved at "+netTopFileJson[tabIndex]+"\n");
									//redraw topology
									lpane[tabIndex].removeAll();
									updateUI();
									server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString(),selectedLayout);
									updateGUI();
									updateInfo();
									updateUI();
									append("Link Deleted \n");
								}//end if approve
							}//end else
						}//end try 
						catch(IOException e1) {
							e1.printStackTrace();
						}
					}//end action performer
				}); //end listener
				
				//Add all elements to add/delete node box
				frame.add(node1);
				frame.add(nodeBox1);
				frame.add(node2);
				frame.add(nodeBox2);
				frame.add(adminCost);
				frame.add(adCostText);
				frame.add(linkCapLabel);
				frame.add(linkCapText);
				frame.add(linkDelayText);
				frame.add(linkCapUnit);
				frame.add(create);
				frame.add(deleteLink);
				frame.setLocation((int) screenwidth/2, (int) screenheight/4);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});//Add/Delete link listener

		/**
		 * Add node listener
		 * @author Tom
		 */	
		addNode.addActionListener(new ActionListener(){							
			public void actionPerformed(ActionEvent e) {
				final JFrame frame = new JFrame(addNode.getActionCommand());			//create new frame
				newNodeID = Collections.max(nbNodes_.get(tabIndex))+1;			//assign node ID to be 1 more than maximum assigned ID
				frame.setLayout(new FlowLayout(FlowLayout.CENTER));				//set layout 
				frame.setMinimumSize(new Dimension(420, 275));					//default size
				frame.setTitle("Add Node");										//title box
				JButton confirm = new JButton("Confirm");						//buttons for confirm and cancel
				JButton cancel = new JButton("Cancel");
				JLabel setupForNode = new JLabel("Enter configuration for Node: ");		//Title
				JTextField newNodeIDBox = new JTextField(String.valueOf(newNodeID),2);	//fill in text box with non editable new node ID
				newNodeIDBox.setEditable(false);
				JLabel cacheCapLabel = new JLabel("Enter the cache capacity of the node in bytes:"); //message for capacity
				final JTextField cacheCapBox = new JTextField("0",5);									//option box for cache capacity
				newNodeStatus=false;
				nodeStatus = new Boolean[2];
				nodeStatus[0] = false;
				nodeStatus[1] = true;
				JLabel nodeStatusLabel = new JLabel("The node is a server:"); //message for capacity
				final JComboBox nodeStatusBox = new JComboBox(nodeStatus);	
				JLabel nodeTypeLabel = new JLabel("Select the attribute type of the new node:");		//type option label
				newNodeType="DEFAULT";//default value
				final JComboBox nodeTypeBox = new JComboBox(nt[tabIndex].getTypes());						//get node types
				nodeTypeBox.addItemListener(new ItemListener() {									//listener for change in node type selection
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange()==ItemEvent.SELECTED) {
							newNodeType = (String)nodeTypeBox.getSelectedItem();					//get changed node type selection
						}	
					}
				});//end type change selection
				JLabel linkPrompt = new JLabel("New node must be connected via 1 link in the topology");	//link option messages
				JLabel connectNodePrompt = new JLabel("Select the node to connect to the new node:");
				//get list of current nodes
				nodeName = new String[nbNodes_.get(tabIndex).size()];	
				for(int i = 0; i<nbNodes_.get(tabIndex).size(); i++)
				{
					nodeName[i] = String.valueOf(nbNodes_.get(tabIndex).get(i));
				}
				final JComboBox connectNodeBox = new JComboBox(nodeName);					//set combobox of possible nodes
				JLabel adminCostPrompt = new JLabel("    Administrative Cost of Link:");	//admin cost message
				final JTextField adminCostBox = new JTextField("0",10);					//admin cost option for user
				JLabel linkCapacityPrompt = new JLabel("               Link Capacity in kBPS:");	//capacity message
				final JTextField linkCapacityBox = new JTextField("0",10);				//capacity option 
				JLabel linkDelayPrompt = new JLabel("               Link Delay in ms:");	//capacity message
				final JTextField linkDelayBox = new JTextField("0",10);				//capacity option 
				connectedNodeID = 1;//Default value
				connectNodeBox.addItemListener(new ItemListener(){ //get chosen node id to link to 
					public void itemStateChanged(ItemEvent e){
						if(e.getStateChange()==ItemEvent.SELECTED){
							connectedNodeID = Integer.parseInt((nodeName[connectNodeBox.getSelectedIndex()])); //get id of selected node
						}
					}
				});

				confirm.addActionListener(new ActionListener() {		//confirm add new node
					public void actionPerformed(ActionEvent e) {
						if((topologyConfHasImported[tabIndex]==true)&&(cacheConfHasImported[tabIndex]==true)&&(nodeConfHasImported[tabIndex]==true)){
							nbNodes_.get(tabIndex).add(newNodeID);//add new node to ID list
							newNodeCapacity=Double.parseDouble(cacheCapBox.getText()); //get new node capacity
							if(nodeStatusBox.getSelectedItem().equals("true")){//get node status 
								newNodeStatus=true;
							}
							else{
								newNodeStatus=false;
							}
							newLinkCost = Integer.parseInt(adminCostBox.getText()); //get new link cost
							newLinkCapacity = Integer.parseInt(linkCapacityBox.getText()); //get new link capacity
							newLinkDelay = Integer.parseInt(linkDelayBox.getText()); //get new link delay
							//Modify appropriate files for new node
							cacheConfFileJson[tabIndex] = GraphicalInterface.addNodeToCachingFileJson((int)newNodeID,(double)newNodeCapacity,newNodeStatus, cacheConfFileJson[tabIndex]);
							nodeTypeJson[tabIndex] = GraphicalInterface.addNodeToTypeFileJson(newNodeID, newNodeType, nodeTypeJson[tabIndex]);
							nt[tabIndex].addNewNode(newNodeID,newNodeType); //add node to nodetype structure
							//generate new topology file
							netTopFileJson[tabIndex]=GraphicalInterface.addLinkToJsonTopology(newNodeID, 
									connectedNodeID, newLinkCost, newLinkCapacity, newLinkDelay, netTopFileJson[tabIndex]);
							//relayout topology
							lpane[tabIndex].removeAll();
							updateUI();
							server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
							updateGUI();
							updateInfo();
							updateUI();
							append("Add Node Confirmed\n");
						}//end if imported
						else {
							appendError("Import topology, caching and type files before modifying topology\n");
						}
						frame.dispose();
					}
				});
				cancel.addActionListener(new ActionListener() { //Cancel new node option 
					public void actionPerformed(ActionEvent e) {
						append("Add Node Cancelled\n");
						frame.dispose();
					}
				});
				//Add all option to new node frame
				frame.add(setupForNode);
				frame.add(newNodeIDBox);
				frame.add(cacheCapLabel);
				frame.add(cacheCapBox);
				frame.add(nodeStatusLabel);
				frame.add(nodeStatusBox);
				frame.add(nodeTypeLabel);
				frame.add(nodeTypeBox);
				frame.add(linkPrompt);
				frame.add(connectNodePrompt);
				frame.add(connectNodeBox);
				frame.add(adminCostPrompt);
				frame.add(adminCostBox);
				frame.add(linkCapacityPrompt);
				frame.add(linkCapacityBox);
				frame.add(linkDelayPrompt);
				frame.add(linkDelayBox);
				frame.add(confirm);
				frame.add(cancel);
				//format new node box
				frame.setLocation((int) screenwidth/2, (int) screenheight/4);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});

		/**
		 * Delete node action listener
		 * @author Tom
		 */
		deleteNode.addActionListener(new ActionListener(){	//delete node option box
			public void actionPerformed(ActionEvent e) {
				final JFrame frame = new JFrame(addNode.getActionCommand());
				frame.setLayout(new FlowLayout(FlowLayout.CENTER)); //layout new dialog box
				frame.setSize(400, 100);	
				frame.setTitle("Delete Node");	//title
				JButton confirm = new JButton("Confirm"); //option buttons
				JButton cancel = new JButton("Cancel");
				JLabel setupForNode = new JLabel("Choose node to be removed from topology"); //label
				//get list of current nodes
				nodeName = new String[nbNodes_.get(tabIndex).size()];	
				for(int i = 0; i<nbNodes_.get(tabIndex).size(); i++)
				{
					nodeName[i] = String.valueOf(nbNodes_.get(tabIndex).get(i));
				}
				final JComboBox nodeDeleteBox = new JComboBox(nodeName); //option box for node to delete
				//get user selection of node to delete (default is 1)
				nodeDeleteBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						if(e.getStateChange()==ItemEvent.SELECTED) {
							removeNodeID = Integer.parseInt((nodeName[nodeDeleteBox.getSelectedIndex()])); //get id of selected node
						}
					}
				});

				confirm.addActionListener(new ActionListener() {	//confirm delete a node
					public void actionPerformed(ActionEvent e) {
						if((topologyConfHasImported[tabIndex]==true)&&(cacheConfHasImported[tabIndex]==true)&&(nodeConfHasImported[tabIndex]==true)){
							//update nbNodes_
							nbNodes_.get(tabIndex).remove((Object)removeNodeID);//cast to remove specific id not index
							//update nt[] matrix
							nt[tabIndex].deleteNode(removeNodeID);
							System.out.println("removing node id "+removeNodeID);
							//update topology file - remove all links associated with deleted node
							netTopFileJson[tabIndex]=GraphicalInterface.removeAllLinksFromTopologyJson(removeNodeID,netTopFileJson[tabIndex]);
							//update caching file
							cacheConfFileJson[tabIndex] = GraphicalInterface.removeNodeFromCachingFileJson(removeNodeID, cacheConfFileJson[tabIndex]);
							//update type file
							nodeTypeJson[tabIndex] = GraphicalInterface.removeNodeFromTypeFileJson(removeNodeID, nodeTypeJson[tabIndex]);	
							//get new topology visual object
							lpane[tabIndex].removeAll();
							updateUI();
							server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() , selectedLayout);
							updateGUI();
							updateInfo();
							updateUI();
							append("Delete Node Confirmed\n");
						}
						else {
							appendError("Import topology, caching and type files before modifying topology\n");
						}
						frame.dispose();
					}//end action performed
				});//end listener

				cancel.addActionListener(new ActionListener() { //cancel delete node
					public void actionPerformed(ActionEvent e) {
						append("Delete Node Cancelled\n");
						frame.dispose();
					}//end action perf
				});//end listener
				
				//layout delete node
				frame.add(setupForNode);
				frame.add(nodeDeleteBox);
				frame.add(confirm);
				frame.add(cancel);

				frame.setLocation((int) screenwidth/2, (int) screenheight/4);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		}); //end delete listener

		//add menu options to file options
		file.add(fileNew);
		file.add(fileSave);
		file.add(closeTab);
		file.addSeparator();
		file.add(imp);
		file.addSeparator();
		file.add(print);
		file.addSeparator();
		file.add(fileExit);

		Select.add(server_Selection);
		Select.add(content_Placement);
		Select.add(routing_scheme);

		preferences.add(showGraph);
		preferences.add(editing);

		Topo.add(setMultiNodeas);
		Topo.add(topoProperties);
		Topo.add(sortBy);
		Topo.add(createNewLink);
		Topo.add(addNode);
		Topo.add(deleteNode);

		show.add(showTopologicalMetrics);
		show.add(showNodeTypes);
		show.add(showCacheStatistics);
		show.add(showContentStatistics);
		show.add(showEdgeColorExplanationTable);

		showGraph.add(circleLayout);
		showGraph.add(FRLayout);
		showGraph.add(ISOMLayout);
		showGraph.add(KKLayout);

		setMultiNodeas.add(nodeattrDefault);
		setMultiNodeas.add(nodeattrCoreNode);
		setMultiNodeas.add(nodeattrEdgeNode);

		menubar.add(file);
		menubar.add(Select);
		menubar.add(show);
		menubar.add(preferences);
		menubar.add(Topo);
		
		
		//////////////////////////////CREATE NETWORK TOPOLOGY///////////////////////////////////////////
		
		JButton btnNewButton = new JButton("Create Network Topology");		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (netTopFileJson[tabIndex] == null){							//cannot plot topology without file
					appendError("Error: Import the network topology file first\n");
				}
				else{ 
					//relayout lpane,get new server object and revalidate/layout
					lpane[tabIndex].removeAll();	
					updateUI();
					server = jung.get(tabIndex).createGraphNew(nbNodes_.get(tabIndex),netTopFileJson[tabIndex].toString() ,selectedLayout);
					repaint();
					updateGUI();
					updateInfo();
					frame.revalidate();
					frame.repaint();
					append("Topology generated\n");
					graphHasCreated[tabIndex] = true;
					//enable relevant JMenuItems
					nodeattrDefault.setEnabled(true);    
					nodeattrCoreNode.setEnabled(true);
					nodeattrEdgeNode.setEnabled(true);
					createNewLink.setEnabled(true);
				}
			}
		});//end create network topology
		//add help dialog
		menubar.add(mntmHelp);
		menubar.add(btnNewButton);

		helpDialog = new JDialog();
		JLabel JLbl = new JLabel(usageInstructions());
		JScrollPane scrollPane = new JScrollPane(JLbl,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		helpDialog.getContentPane().add(scrollPane);

		mntmHelp.addActionListener(new ActionListener() { //listener for new help dialog
			public void actionPerformed(ActionEvent event) {
				helpDialog.pack();
				helpDialog.setVisible(true);
			}
		}); //end listener for help option 
		
		///////////////////////////////////START RESOURCE CONFUGURATION////////////////////////////////////////////////////
		
		JButton btnStartCaching = new JButton("Configure Resources"); //start resource configurations 
		menubar.add(btnStartCaching);//add to menu
		btnStartCaching.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//only allow caching when every file is imported
				if ((netTopFileJson[tabIndex] == null) && (conFJson[tabIndex]  == null) && (cacheConfFileJson[tabIndex]  == null) && (demFJson[tabIndex]  == null) && (nodeTypeJson[tabIndex]  == null)){
					appendError("Error: Import the appropriate configuration files first\n");
				}
				else{ 
					
					//If only infrastructure configuration files are imported, routing only can be computed
					if (((netTopFileJson[tabIndex]!=null) && (cacheConfFileJson[tabIndex]!=null) && (nodeTypeJson[tabIndex]!=null)) && ((demFJson[tabIndex]==null)||(conFJson[tabIndex]==null))){
						
						managementOption[tabIndex] = 0; //value allowing routing configuration only
						
						Long t1 = System.currentTimeMillis(); //time before algorithm start
						startCachingPressed[tabIndex]	= true;//set has cached as true
						nodeTypeCurrent[tabIndex] = new File(tmpFolder+File.separator+topologyName[tabIndex]+"nodeTypefile.txt"); //as PathF and NodeTypeFile are in the same folder
						nt[tabIndex].printAttributeList(nodeTypeCurrent[tabIndex]); //Mechanism for ensuring current node types are passed to algorithm
						
						String pathFile = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"PathConfiguration.json";
						
						CacheManagementProgram cacheMgtProg = new CacheManagementProgram(
								selectedRoutingScheme[tabIndex],
								topologyName[tabIndex],
								netTopFileJson[tabIndex].toString(),
								cacheConfFileJson[tabIndex].toString(),
								pathFile,
								managementOption[tabIndex]);
						
						cacheMgtProg.startConfigureResources(managementOption[tabIndex]);
						
						pathFJson[tabIndex] = new File(pathFile);
						pathConfHasImported[tabIndex] = true;
						append("Routing configuration completed in "+((System.currentTimeMillis()-t1)/1000)+" seconds\n"); //print elapsed time to exec algo
						nodeTypeCurrent[tabIndex].delete();//remove temp nodeType file
					}//end if management option 0
					else{
						if (((netTopFileJson[tabIndex]!=null) && (cacheConfFileJson[tabIndex]!=null) && (nodeTypeJson[tabIndex]!=null)) && ((demFJson[tabIndex]!=null)&&(conFJson[tabIndex]!=null))){
							
							managementOption[tabIndex] = 1; //value allowing all management configurations
							
							Long t1 = System.currentTimeMillis(); //time before algorithm start
							startCachingPressed[tabIndex]	= true;//set has cached as true
							nodeTypeCurrent[tabIndex] = new File(tmpFolder+File.separator+topologyName[tabIndex]+"nodeTypefile.txt"); //as PathF and NodeTypeFile are in the same folder
							nt[tabIndex].printAttributeList(nodeTypeCurrent[tabIndex]); //Mechanism for ensuring current node types are passed to algorithm
							
							String pathFile = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"pathConfiguration.json";
							String contentPlacementConfiguration = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"ContentPlacementConfiguration_" + alpha[tabIndex] + "_" + beta[tabIndex] +".json";
							String serverSelectionConfiguration = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"ServerSelectionConfiguration_" + alpha[tabIndex] + "_" + beta[tabIndex] +".json";
							
							CacheManagementProgram cacheMgtProg = new CacheManagementProgram(
									selectedConPlaceStrategy[tabIndex],
									selectedServerSelStrategy[tabIndex],
									selectedRoutingScheme[tabIndex],
									topologyName[tabIndex],
									netTopFileJson[tabIndex].toString(),
									cacheConfFileJson[tabIndex].toString(),
									conFJson[tabIndex].toString(),
									demFJson[tabIndex].toString(),
									pathFile,
									contentPlacementConfiguration,
									serverSelectionConfiguration,
									managementOption[tabIndex]);
							
							cacheMgtProg.startConfigureResources(managementOption[tabIndex]);
							
							pathFJson[tabIndex] = new File(pathFile);
							contentPlacementConfigurationFile[tabIndex] = new File(contentPlacementConfiguration);
							serverSelectionConfigurationFile[tabIndex] = new File(serverSelectionConfiguration);
							
							pathConfHasImported[tabIndex] = true;
							placementConfHasImported[tabIndex] = true;
							serverSelectionConfHasImported[tabIndex] = true;
							
							append("Routing and caching configurations completed in "+((System.currentTimeMillis()-t1)/1000)+" seconds\n"); //print elapsed time to exec algo
							nodeTypeCurrent[tabIndex].delete();//remove temp nodeType file
						}
					}//end esle management option 1
				}//else
			}
		});//end start caching button press listener. 
		
		///////////////////////////////////COMPUTE METRICS////////////////////////////////////////////////////
		
		JButton btnComputeSystMetrics = new JButton("Compute Metrics"); //start compute system metrics algorithm option 
		menubar.add(btnComputeSystMetrics);//add to menu
		btnComputeSystMetrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//only allow metrics computation when every file is imported
				if (managementOption[tabIndex] == -1){
					appendError("Error: Import the appropriate configuration files first\n");
				}
				else{ 
					if(managementOption[tabIndex] == 0){
						
						startMetricPressed[tabIndex]	= true;//set computed system metrics as true
						
						String topologicalMetrics = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"topologicalMetrics.json";
						
						SystemMetricsProgram systMetricsProg = new SystemMetricsProgram(
								netTopFileJson[tabIndex].toString(),
								pathFJson[tabIndex].toString(),
								topologicalMetrics,
								managementOption[tabIndex]);
						
						systMetricsProg.computeSystemMetrics(managementOption[tabIndex]);
						
						topologicalMetricsFile[tabIndex] = new File(topologicalMetrics);
						
						//Parse the topological metrics
						parseNodeMetrics(topologicalMetrics, topologicalMetricsResults[tabIndex], nodeTable[tabIndex]);
						
						//Enable display statistics
						showTopologicalMetrics.setEnabled(true);
						
						updateInfoStatisticsTopo();//update topo stats 
						
						//set edge painter as cached (allows not black edges) and set table of this algorithm run then set render context of server 
						edgePaint.get(tabIndex).setCached(true);
						edgePaint.get(tabIndex).setTable(edgeTable[tabIndex]);
						server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint.get(tabIndex));
						repaint();//repaint figure						
					}//end if management option 0
					else{
						if(managementOption[tabIndex] == 1){
							
							startMetricPressed[tabIndex]	= true;//set computed system metrics as true

							String cacheMetrics = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"CacheMetrics_" + alpha[tabIndex] + "_" + beta[tabIndex] +".json";
							String networkMetrics = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"NetworkMetrics_" + alpha[tabIndex] + "_" + beta[tabIndex] +".json";
							String topologicalMetrics = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"TopologicalMetrics_" + alpha[tabIndex] + "_" + beta[tabIndex] +".json";
							String contentMetrics = fileLocation[tabIndex]+File.separator+topologyName[tabIndex]+"ContentMetrics_" + alpha[tabIndex] + "_" + beta[tabIndex] +".json";
							
							SystemMetricsProgram systMetricsProg = new SystemMetricsProgram(
									topologyName[tabIndex],
									netTopFileJson[tabIndex].toString(),
									cacheConfFileJson[tabIndex].toString(),
									conFJson[tabIndex].toString(),
									demFJson[tabIndex].toString(),
									pathFJson[tabIndex].toString(),
									contentPlacementConfigurationFile[tabIndex].toString(),
									serverSelectionConfigurationFile[tabIndex].toString(),
									cacheMetrics,
									networkMetrics,
									topologicalMetrics,
									contentMetrics,
									managementOption[tabIndex]);
							
							systMetricsProg.computeSystemMetrics(managementOption[tabIndex]);
							
							cacheMetricsFile[tabIndex]  = new File(cacheMetrics); 
							networkMetricsFile[tabIndex] = new File(networkMetrics);
							topologicalMetricsFile[tabIndex] = new File(topologicalMetrics);
							contentMetricsFile[tabIndex] = new File(contentMetrics);
							
							//Parse the network metrics
							parseNetworkMetrics(networkMetrics, netwMetricsResults[tabIndex], edgeTable[tabIndex]);
							//Parse the topological metrics
							parseNodeMetrics(topologicalMetrics, topologicalMetricsResults[tabIndex], nodeTable[tabIndex]);
							//Parse the cache metrics
							parseCacheMetrics(cacheMetrics, cacheMetricsResults[tabIndex], cacheTable[tabIndex]);
							//Parse the content metrics
							parseContentMetrics(contentMetrics, contentMetricsResults[tabIndex], contentTable[tabIndex]);
							
							//Enable display statistics
							showCacheStatistics.setEnabled(true);
							showContentStatistics.setEnabled(true);
							showTopologicalMetrics.setEnabled(true);
							
							updateInfoStatistics();//update new stats from algorithm
							
							readContentPlacementFromFileJson(contentPlacementConfigurationFile[tabIndex]);
							readCacheListFromFileJson(cacheConfFileJson[tabIndex]);
							
							//set edge painter as cached (allows not black edges) and set table of this algorithm run then set render context of server 
							edgePaint.get(tabIndex).setCached(true);
							edgePaint.get(tabIndex).setTable(edgeTable[tabIndex]);
							server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint.get(tabIndex));
							repaint();//repaint figure
							
							exportFile();//write output files to disk 
							
						}
					}//end if management option 1
			
				}//else
			}
		});//end start caching button press listener. 
		
			///////////////////////////////////DISPLAY////////////////////////////////////////////////////
		
		JSplitPane logSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, GUItabs, logScrollPane);
		logSplitPane.setDividerLocation(heightDividerLocation);
		add(logSplitPane);
		appendIntro( "Welcome to CacheMAsT...\nFor more information about the usage click on Help from the menu.\n" );
	
	
	}//end GraphicalInterface constructor

	
	
	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("Caching Visualisation");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GraphicalInterface fcd = new GraphicalInterface();
		frame.setJMenuBar(menubar);
		frame.getContentPane().add(fcd);
		frame.setVisible(true);
	}
	
	/**
	 * Updates the GUI presenting all the current data and panels (Called by pressing the two JButtons or my changing the layout).
	 * 
	 * @author Tonny Duong
	 */
	private void updateGUI()
	{
		int i = GUItabs.getSelectedIndex(); //get index
		String topologyTitle = GUItabs.getTitleAt(i);	//get title
		String message ="<html><body><font face=\"helvetica\" size=\"10\" color=\"blue\"><b>"+topologyTitle+"</b></body></html>"; //get tab title
		final JLabel topoName = new JLabel(message);
		topoName.setSize(150, 35);
		topoName.setLocation(400, 1);
		topoName.setVisible(true);
		final JPanel edgeColorExplanation = EdgeColorExplanationTableOnGraph.createColorExplanationTable(); //get edge colour key panel 
		edgeColorExplanation.setOpaque(true);																//set as opaque
		edgeColorExplanation.setLocation(graphWidth-colourTableWidth,graphHeight-colourTableHeight);		//set position
		
		graphPane[tabIndex] = new GraphZoomScrollPane(server);												//set graph pane as this server
		lpane[tabIndex] = new JLayeredPane();																//create new layered pane as lpane
		lpane[tabIndex].setPreferredSize(new Dimension(graphWidth, graphHeight));							//set size
		lpane[tabIndex].setSize(new Dimension(graphWidth, graphHeight));

		graphPane[tabIndex].setSize(lpane[tabIndex].getSize());
		graphPane[tabIndex].setLocation(0, 0);

		JButton plus = new JButton("+");	//zoom buttons
		JButton minus = new JButton("-");
		final ScalingControl scaler = new CrossoverScalingControl();
		//set plus zoom as enlarging topology
		plus.setFont(new Font("Arial", Font.PLAIN, 12));
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(server, 1.1f, server.getCenter());
			}
		});
		//set minus zoom as shrinking topology 
		minus.setFont(new Font("Arial", Font.PLAIN, 12));
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(server, 1/1.1f, server.getCenter());
			}
		});
		int a = 25; //default sizes
		int b = 43;
		plus.setLocation(1, 1);	//set locations 
		minus.setLocation(1, 5+a);
		plus.setSize(b, a);
		minus.setSize(b, a);
		//add all elements to lpane 
		lpane[tabIndex].add(graphPane[tabIndex], JLayeredPane.DEFAULT_LAYER);
		lpane[tabIndex].add(topoName,  new Integer(1), 1);
		lpane[tabIndex].add(edgeColorExplanation,  new Integer(1), 0);
		lpane[tabIndex].add(plus, JLayeredPane.DRAG_LAYER);
		lpane[tabIndex].add(minus, JLayeredPane.DRAG_LAYER);

		info1 =  new JEditorPane("text/html", "");
		info1.setEditable(false);
		Color mycolor = new Color(213,213,213); //set background colour
		info1.setBackground(mycolor);
		dataScrollPane[tabIndex] = new JScrollPane(info1); //set data pane with info
		dataScrollPane[tabIndex].setPreferredSize(new Dimension(infoWidth, infoHeight));

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lpane[tabIndex], dataScrollPane[tabIndex]); //split general upper pane between graph and data
		splitPane.setDividerLocation(widthDividerLocation);		//place divider
		splitPane.setContinuousLayout(true);				
		splitPane.addPropertyChangeListener(new PropertyChangeListener(){ //listener for divider location
			public void propertyChange(PropertyChangeEvent evt) {
				if(tabIndex>-1){
					if(graphPane[tabIndex]!=null){
						//adjust sizes of lpane and datapane due to changed divider location 
						widthDividerLocation  = splitPane.getDividerLocation();
						graphPane[tabIndex].setSize(lpane[tabIndex].getWidth(), GUItabs.getHeight()-32);
						dataScrollPane[tabIndex].setSize(dataScrollPane[tabIndex].getWidth(), GUItabs.getHeight()-32);
						graphWidth = (int) graphPane[tabIndex].getWidth();
						graphHeight = (int) graphPane[tabIndex].getHeight();
						edgeColorExplanation.setLocation(graphWidth-colourTableWidth,graphHeight-colourTableHeight);
						topoName.setLocation((graphWidth/2)-50, 1);
					}
				}
			}
		});//end divider change listener. 

		JPanel p1 = (JPanel) GUItabs.getComponentAt(i);
		p1.removeAll();
		p1.add(splitPane);
		final JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, GUItabs, logScrollPane);
		splitPane2.setDividerLocation(heightDividerLocation);
		splitPane2.setContinuousLayout(true);
		splitPane2.addPropertyChangeListener(new PropertyChangeListener(){ //upper and lower divider location listener
			public void propertyChange(PropertyChangeEvent evt) {
				heightDividerLocation = splitPane2.getDividerLocation();
			}
		});
		removeAll();
		add(splitPane2);
	}//end update GUI

	//class main 
	public static void main(final String[] args) {
		UIManager.put("swing.boldMetal", Boolean.FALSE); 
		final JFrame frame = new JFrame("Topology Variables"); //first prompt message frame
		frame.setLayout(new FlowLayout());						//set layout

		JLabel rootLabel = new JLabel("Set Topologies directory: "); //root directory prompt
		final JTextField rootField = new JTextField("",20);				//root directory field
		JLabel nameLabel = new JLabel("Set the name of the first topology: "); //name prompt	
		final JTextField nameField = new JTextField("",20);						//first topology name field
		JButton confirm = new JButton("Confirm");	//confirm and cancel option s
		JButton cancel = new JButton("Cancel");
		//add to frame 
		frame.add(rootLabel);
		frame.add(rootField);
		frame.add(nameLabel);
		frame.add(nameField);
		frame.add(confirm);
		frame.add(cancel);

		cancel.addActionListener(new ActionListener(){ //cancel and close program
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		confirm.addActionListener(new ActionListener(){ //confirm program begin 
			public void actionPerformed(ActionEvent e) {
				try
				{
					fileLocation[tabIndex] = rootField.getText().replace(" ","");					//Set Initial file search location and strip whitespace
					topologyName[tabIndex] = nameField.getText();						//Set Name of the topology 
				}
				catch (NumberFormatException e1)
				{
					e1.printStackTrace();
				}
				frame.setVisible(false);												//remove frame
				frame.dispose();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UIManager.put("swing.boldMetal", Boolean.FALSE); 
						createAndShowGUI();//begin remaining program execution
					}
				}); //end invokeLater
			}//end actionperformed
		});//end listener
		//format frame 
		frame.setSize(300, 170);
		frame.setResizable(false);
		int wNT = (int) (screenwidth-(300))/2;
		int hNT = (int) (screenheight-(170))/2;
		frame.setLocation(wNT, hNT);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}//end main
	
	
	
	///////////////////////////////////STATISTICS PARSER////////////////////////////////////////////////////
	
	/**
	 * Parse the network metrics file
	 * @param inputNewtMetricsJSON String
	 * @param netwMetricsResults ArrayList<Double>
	 * @param edgeTableStats Hashtable<String,Double>
	 */
	public void parseNetworkMetrics(String inputNewtMetricsJSON, ArrayList<Double> netwMetricsResults, Hashtable<String,ArrayList<Double>> edgeTableStats){
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputNewtMetricsJSON));

			JSONObject jsonObject = (JSONObject) obj;
			
			//double minUtil = (double)jsonObject.get("minUtil");
			double minUtil = ((Double)jsonObject.get("minUtil")).doubleValue();
			//double maxUtil = (double)jsonObject.get("maxUtil");
			double maxUtil = ((Double)jsonObject.get("maxUtil")).doubleValue();
			//double avgUtil = (double)jsonObject.get("avgUtil");
			double avgUtil = ((Double)jsonObject.get("avgUtil")).doubleValue();
			//double totNetwLoad = (double)jsonObject.get("totNetwLoad");
			double totNetwLoad = ((Double)jsonObject.get("totNetwLoad")).doubleValue();
			//double avgRetrievalDelay = (double)jsonObject.get("avgRetrievalDelay");
			double avgRetrievalDelay = ((Double)jsonObject.get("avgRetrievalDelay")).doubleValue();
			
			netwMetricsResults.add(minUtil);
			netwMetricsResults.add(maxUtil);
			netwMetricsResults.add(avgUtil);
			netwMetricsResults.add(totNetwLoad);
			netwMetricsResults.add(avgRetrievalDelay);
			
			JSONArray listLinks = (JSONArray) jsonObject.get("linkStats");
			Iterator<JSONObject> iterator = listLinks.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectLink = (JSONObject)iterator.next();
				//int startID = (int)(long)jsonObjectLink.get("startID");
				int startID = ((Long)jsonObjectLink.get("startID")).intValue();
				//int endID = (int)(long)jsonObjectLink.get("endID");
				int endID = ((Long)jsonObjectLink.get("endID")).intValue();
				//double linkWeight = (double)jsonObjectLink.get("linkWeight");
				double linkWeight = ((Double)jsonObjectLink.get("linkWeight")).doubleValue();
				//double linkCapacity = (double)jsonObjectLink.get("linkCapacity");
				double linkCapacity = ((Double)jsonObjectLink.get("linkCapacity")).doubleValue();
				//double linkLoad = (double)jsonObjectLink.get("linkLoad");
				double linkLoad = ((Double)jsonObjectLink.get("linkLoad")).doubleValue();
				//double linkUtil = (double)jsonObjectLink.get("linkUtil");
				double linkUtil = ((Double)jsonObjectLink.get("linkUtil")).doubleValue();
				ArrayList<Double> listDouble = new ArrayList<Double>();
				listDouble.add(linkWeight);
				listDouble.add(linkCapacity);
				listDouble.add(linkLoad);
				listDouble.add(linkUtil);
				edgeTableStats.put("["+startID+","+endID+"]", listDouble);
			}
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}		

	}//end parseNetworkMetrics
	
	
	/**
	 * Parse the cache metrics file
	 * @param inputCacheMetricsJSON String
	 * @param cacheMetricsResults ArrayList<Double>
	 * @param cacheTableStats Hashtable<String,ArrayList<Double>>
	 */
	public void parseCacheMetrics(String inputCacheMetricsJSON, ArrayList<Double> cacheMetricsResults, Hashtable<String,ArrayList<Double>> cacheTableStats){
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputCacheMetricsJSON));

			JSONObject jsonObject = (JSONObject) obj;
			
			//double avgCacheHitRatio = (double)jsonObject.get("avgCacheHitRatio");
			double avgCacheHitRatio = ((Double)jsonObject.get("avgCacheHitRatio")).doubleValue();
			//double avgCacheOccupancy = (double)jsonObject.get("avgCacheOccupancy");
			double avgCacheOccupancy = ((Double)jsonObject.get("avgCacheOccupancy")).doubleValue();
			//double avgContentReplicationDegree = (double)jsonObject.get("avgContentReplicationDegree");
			double avgContentReplicationDegree = ((Double)jsonObject.get("avgContentReplicationDegree")).doubleValue();
			//double avgCacheCapacity = (double)jsonObject.get("avgCacheCapacity");
			double avgCacheCapacity = ((Double)jsonObject.get("avgCacheCapacity")).doubleValue();
			//double nbCaches = (int)(long)jsonObject.get("nbCaches");
			double nbCaches = ((Long)jsonObject.get("nbCaches")).intValue();
			//double minCacheCapacity = (double)jsonObject.get("minCacheCapacity");
			double minCacheCapacity = ((Double)jsonObject.get("minCacheCapacity")).doubleValue();
			//double maxCacheCapacity = (double)jsonObject.get("maxCacheCapacity");
			double maxCacheCapacity = ((Double)jsonObject.get("maxCacheCapacity")).doubleValue();
			
			cacheMetricsResults.add(avgCacheHitRatio);
			cacheMetricsResults.add(avgCacheOccupancy);
			cacheMetricsResults.add(avgContentReplicationDegree);
			cacheMetricsResults.add(avgCacheCapacity);
			cacheMetricsResults.add(nbCaches);
			cacheMetricsResults.add(minCacheCapacity);
			cacheMetricsResults.add(maxCacheCapacity);
			
			JSONArray listCaches = (JSONArray) jsonObject.get("cacheStats");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectLink = (JSONObject)iterator.next();
				//int cacheID = (int)(long)jsonObjectLink.get("cacheID");
				int cacheID = ((Long)jsonObjectLink.get("cacheID")).intValue();
				//double localCHR = (double)jsonObjectLink.get("localCHR");
				double localCHR = ((Double)jsonObjectLink.get("localCHR")).doubleValue();
				//double localCO = (double)jsonObjectLink.get("localCO");
				double localCO = ((Double)jsonObjectLink.get("localCO")).doubleValue();
				//double localCCapa = (double)jsonObjectLink.get("localCCapa");
				double localCCapa = ((Double)jsonObjectLink.get("localCCapa")).doubleValue();
				//double localNbContents = (double)jsonObjectLink.get("localNbContents");
				double localNbContents = ((Double)jsonObjectLink.get("localNbContents")).doubleValue();
				ArrayList<Double> listDouble = new ArrayList<Double>();
				listDouble.add(localCHR);
				listDouble.add(localCO);
				listDouble.add(localCCapa);
				listDouble.add(localNbContents);
				cacheTableStats.put("["+cacheID+"]", listDouble);
			}
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}		

	}//end parseCacheMetrics
	
	
	/**
	 * Parse the node metrics file
	 * @param inputTopologicalMetricsJSON String
	 * @param nodeMetricsResults ArrayList<Double>
	 * @param nodeTableStats Hashtable<String,ArrayList<Double>>
	 */
	public void parseNodeMetrics(String inputNodeMetricsJSON, ArrayList<Double> nodeMetricsResults, Hashtable<String,ArrayList<Double>> nodeTableStats){
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputNodeMetricsJSON));

			JSONObject jsonObject = (JSONObject) obj;
			
			//double minPathLength = (double)jsonObject.get("minPathLength");
			double minPathLength = ((Double)jsonObject.get("minPathLength")).doubleValue();
			//double maxPathLength = (double)jsonObject.get("maxPathLength");
			double avgPathLength = ((Double)jsonObject.get("avgPathLength")).doubleValue();
			//double maxPathLength = (double)jsonObject.get("maxPathLength");
			double maxPathLength = ((Double)jsonObject.get("maxPathLength")).doubleValue();
						
			nodeMetricsResults.add(minPathLength);
			nodeMetricsResults.add(maxPathLength);
			nodeMetricsResults.add(avgPathLength);
	
			JSONArray listNodes = (JSONArray) jsonObject.get("nodeStats");
			Iterator<JSONObject> iterator = listNodes.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//int nodeID = (int)(long)jsonObjectNode.get("nodeID");
				int nodeID = ((Long)jsonObjectNode.get("nodeID")).intValue();
				//double nodeDegConnect = (double)jsonObjectNode.get("nodeDegConnect");
				double nodeDegConnect = ((Double)jsonObjectNode.get("nodeDegConnect")).doubleValue();
				//double nodeClusteringCoeff = (double)jsonObjectNode.get("nodeClusteringCoeff");
				double nodeClusteringCoeff = ((Double)jsonObjectNode.get("nodeClusteringCoeff")).doubleValue();
				//double nodeAvgDistFact = (double)jsonObjectNode.get("nodeAvgDistFact");
				double nodeAvgDistFact = ((Double)jsonObjectNode.get("nodeAvgDistFact")).doubleValue();
				ArrayList<Double> listDouble = new ArrayList<Double>();
				listDouble.add(nodeDegConnect);
				listDouble.add(nodeClusteringCoeff);
				listDouble.add(nodeAvgDistFact);
				nodeTableStats.put("["+nodeID+"]", listDouble);
			}
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}		
		
	}//end parseNodeMetrics
	
	
	/**
	 * Parse the content metrics file
	 * @param inputTopologicalMetricsJSON String
	 * @param contentTableStats Hashtable<String,ArrayList<Double>>
	 */
	public void parseContentMetrics(String inputNodeMetricsJSON, ArrayList<Double> contentMetricsResults, Hashtable<String,ArrayList<Double>> contentTableStats){
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(inputNodeMetricsJSON));

			JSONObject jsonObject = (JSONObject) obj;
			
			//double avgContentSize = (double)jsonObject.get("avgContentSize");
			double avgContentSize = ((Double)jsonObject.get("avgContentSize")).doubleValue();
			//double nbContents = (int)(long)jsonObject.get("nbContents");
			double nbContents = ((Long)jsonObject.get("nbContents")).intValue();
			//double minContentSize = (double)jsonObject.get("minContentSize");
			double minContentSize = ((Double)jsonObject.get("minContentSize")).doubleValue();
			//double maxContentSize = (double)jsonObject.get("maxContentSize");
			double maxContentSize = ((Double)jsonObject.get("maxContentSize")).doubleValue();
			
			contentMetricsResults.add(avgContentSize);
			contentMetricsResults.add(nbContents);
			contentMetricsResults.add(minContentSize);
			contentMetricsResults.add(maxContentSize);
			
			JSONArray listContents = (JSONArray) jsonObject.get("contentStats");
			Iterator<JSONObject> iterator = listContents.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//int contentID = (int)(long)jsonObjectNode.get("contentID");
				int contentID = ((Long)jsonObjectNode.get("contentID")).intValue();
				//double contentRepDeg = (double)jsonObjectNode.get("contentRepDeg");
				double contentRepDeg = ((Double)jsonObjectNode.get("contentRepDeg")).doubleValue();
				//double contentRank = (double)jsonObjectNode.get("contentRank");
				double contentRank = ((Double)jsonObjectNode.get("contentRank")).doubleValue();
				ArrayList<Double> listDouble = new ArrayList<Double>();
				listDouble.add(contentRepDeg);
				listDouble.add(contentRank);
				contentTableStats.put("["+contentID+"]", listDouble);
			
			}
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}		
		
	}//end parseContentMetrics
	
	
	
	/**
	 * Prompt the user for only the new topology name for labelling purposes
	 * 
	 * @author Tom
	 */

	public void createNewWindow() //new tab user prompt
	{     
		UIManager.put("swing.boldMetal", Boolean.FALSE); 
		final JFrame frame = new JFrame("Topology Variables"); //frame title
		frame.setLayout(new FlowLayout()); //set layout

		JLabel nameLabel = new JLabel("Set the name of the new topology: "); //name prompt
		final JTextField nameField = new JTextField("",20);					//name box
		JButton confirm = new JButton("Confirm");	// confirm and cancel buttons
		JButton cancel = new JButton("Cancel");
		//add to box
		frame.add(nameLabel);
		frame.add(nameField);
		frame.add(confirm);
		frame.add(cancel);

		cancel.addActionListener(new ActionListener(){	//cancel new tab
			public void actionPerformed(ActionEvent e) {			
				frame.setVisible(false);				//remove pane
				frame.dispose();
			}
		});
		confirm.addActionListener(new ActionListener(){	//confirm new tab
			public void actionPerformed(ActionEvent e) {
				try
				{
					int i = GUItabs.getTabCount();		
					topologyName[i] = nameField.getText();//get new name
					frame.setVisible(false);				//remove pane
					frame.dispose();						//dispose of pane
					JPanel p = new JPanel();				//setup new topology panel
					lpane[i] = new JLayeredPane();
					lpane[i].setPreferredSize(new Dimension(graphWidth, graphHeight)); 
					lpane[i].setSize(new Dimension(graphWidth, graphHeight));
					GUItabs.add(topologyName[i], p);		//add to tabs
				}
				catch (NumberFormatException e1)
				{
					e1.printStackTrace();
				}
				frame.setVisible(false);
				frame.dispose();
			}//end actionPerformed
		});//end listener
		//format frame
		frame.setSize(300, 120);	
		frame.setResizable(false);
		int wNT = (int) (screenwidth-(300))/2;
		int hNT = (int) (screenheight-(100))/2;
		frame.setLocation(wNT, hNT);
		frame.setVisible(true);
	}//end create new window

	@Override
	public void actionPerformed(ActionEvent e) {}

	
	
	public void cacheInfo(Object v) { //Listener for clicking on a node to retrieve cache statistics 
		if(startMetricPressed[tabIndex] == true)  {  
			final JDialog jd = new JDialog(frame, "Information for " + v);
			jd.setSize(500, 300);
			jd.setLocationRelativeTo(null);
			jd.setVisible(true);
			jd.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			jd.getContentPane().setLayout(new GridLayout(1, 1));
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			
			tabbedPane.addTab("Cache", makePanelCache(v));
			tabbedPane.addTab("Contents", makePanelContents(v));
			tabbedPane.addTab("Characteristics / Statistics", makePanelStatistics(v));
			jd.getContentPane().add(tabbedPane);
		}              		
	}

	
	private static JPanel makePanelCache(Object cacheNode) {
		final int MY_MINIMUM = 0;
		final int MY_MAXIMUM = 100;
		final int MY_MAXIMUM1 = contentLists[tabIndex].size();
		int percent = 0;
		
		JPanel p = new JPanel(new BorderLayout());
		pbar = new JProgressBar(JProgressBar.VERTICAL);
		Dimension prefSize = pbar.getPreferredSize();
		prefSize.width = (int) ((screenwidth*150)/1366);		
		prefSize.height = (int) ((screenheight*230)/768);	
		pbar.setPreferredSize(prefSize); 
		pbar.setMinimum(MY_MINIMUM);
		pbar.setMaximum(MY_MAXIMUM);
		pbar.setStringPainted(true);
		Border border = BorderFactory.createTitledBorder(null,"Cache Occupancy",TitledBorder.CENTER,TitledBorder.CENTER);  
		pbar.setBorder(border);

		int selectedCacheID = Integer.parseInt(cacheNode.toString().substring(5));

		int numberContentsStoredLocally = (int)(cacheTable[tabIndex].get("["+selectedCacheID+"]").get(3)/1);
		percent = (int)Math.ceil(cacheTable[tabIndex].get("["+selectedCacheID+"]").get(1));
		pbar.setValue(percent);
		p.add(pbar,BorderLayout.WEST);
		pbar1 = new JProgressBar(JProgressBar.VERTICAL);
		Dimension prefSize1 = pbar1.getPreferredSize();
		prefSize1.width = (int) ((screenwidth*250)/1366);		
		prefSize1.height = (int) ((screenheight*230)/768);	
		pbar1.setPreferredSize(prefSize1);
		pbar1.setMinimum(MY_MINIMUM);
		pbar1.setMaximum(MY_MAXIMUM1);
		pbar1.setStringPainted(true);
		Border border1 = BorderFactory.createTitledBorder(null,"Locally cached content",TitledBorder.CENTER,TitledBorder.CENTER);  
		pbar1.setBorder(border1);
		pbar1.setValue(numberContentsStoredLocally);
		pbar1.setString(numberContentsStoredLocally + "/" + contentLists[tabIndex].size());
		p.add(pbar1,BorderLayout.EAST);
		p.setBackground(new Color(211,211,211));

		return p;    
	}

	private static JPanel makePanelContents(Object cacheNode) {
		JPanel p = new JPanel(new BorderLayout());
		ArrayList<String> listContent = new ArrayList<String>();
		int selectedCacheID = Integer.parseInt(cacheNode.toString().substring(5));
		
		if(contentPlacementMap[tabIndex].get("ID:"+selectedCacheID).size()>0){
			Iterator<String> iter = contentPlacementMap[tabIndex].get("ID:"+selectedCacheID).keySet().iterator();
			String contentID = new String();
			while(iter.hasNext()){
				contentID = (String)iter.next();
				listContent.add(contentID);
			}	
		}
		String[] colName = new String[] { "Content Name" ,"Rank (Global Popularity)" , " Replication Degree"};
		if(listContent.size() != 0) {
			Object[][] contents = new Object [listContent.size()][3];
			for(int i = 0; i< listContent.size(); i++){
				contents[i][0] = "Content " + listContent.get(i).substring(3);
				//Content rank
				contents[i][1] = contentTable[tabIndex].get("["+listContent.get(i).substring(3)+"]").get(1).intValue();
				//Content replication degree
				contents[i][2] = contentTable[tabIndex].get("["+listContent.get(i).substring(3)+"]").get(0);
			}
			JTable table = new JTable( contents, colName );
			table.setBackground(Color.LIGHT_GRAY);
			table.setRowHeight(25);
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment( JLabel.CENTER );
			table.setDefaultRenderer(Object.class, centerRenderer);
			table.setEnabled(false); //stop the table being editable
			JTableHeader header = table.getTableHeader();
			header.setDefaultRenderer(new HeaderRenderer(table));

			// create scroll pane for wrapping the table and add
			// it to the frame
			p.add( new JScrollPane( table ),BorderLayout.WEST );
		}
		return p;		
	}

	
	private static JPanel makePanelStatistics(Object cacheNode) {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 1));
		JEditorPane cacheInfo =  new JEditorPane("text/html", "");
		cacheInfo.setEditable(false);
		Color mycolor = new Color(213,213,213);
		cacheInfo.setBackground(mycolor);
		
		int selectedCacheID = Integer.parseInt(cacheNode.toString().substring(5));
		
		cacheInfo.setText("<font size=\"5\" color=\"red\"> <b><u>Cache Characteristics</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\">"
				+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Cache Capacity:</u></b></td><td>" +  cacheTable[tabIndex].get("["+selectedCacheID+"]").get(2) +" Mbits"+"</td></tr>" +
				"</table> <br></br>"
				+ "</font>" + 
				"<font size=\"5\" color=\"red\"> <b><u>Cache Statistics</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\">"
				+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Cache Occupancy:</u></b></td><td>" + Math.ceil(cacheTable[tabIndex].get("["+selectedCacheID+"]").get(1)) + " %" +"</td></tr>" +
				"<tr><td><b><u>Number of Contents Locally Stored:</u></b></td><td>"+ cacheTable[tabIndex].get("["+selectedCacheID+"]").get(3).intValue() +"</td></tr>  <tr><td><b><u>Requests Served Locally:</u></b></td><td>" 
				+ (double)Math.round((double)cacheTable[tabIndex].get("["+selectedCacheID+"]").get(0)*100 * 100) / 100 
				+ " %"  +"</td></tr>" + " </table>" + "</font>");
		p.add( new JScrollPane( cacheInfo ));
		return p;
	}

	public static void readContentListFromFileJson(File inFile) throws IOException,ParseException { //read content list
		JSONParser parser = new JSONParser(); //parser object
		contentLists[tabIndex] = new HashMap<Integer,Double>(); //hashmap object to return to user
		Integer contentID; 
		Double contentSize;

		Object obj = (Object)parser.parse(new FileReader(inFile));//read file
		JSONObject jobj = (JSONObject)obj;						//get json object
		JSONArray contentArrayJ = (JSONArray)jobj.get("content");	//get content array
		Iterator<JSONObject> it = contentArrayJ.iterator();			//iterator for content array
		while(it.hasNext()) {
			JSONObject contentObj = it.next();								//get ID and size of each content object and add to contentLists
			contentID = Math.toIntExact((Long)contentObj.get("contentID"));
			contentSize = (Double)contentObj.get("sizeBytes");
			contentLists[tabIndex].put(contentID, contentSize);
		}//end while
		System.out.println("The total number of contents is: " + contentLists[tabIndex].size());

	}
	//Same procedure as previous function for cache list
	public static void readCacheListFromFileJson(File inFile){
		JSONParser parser = new JSONParser();
		
		cacheLists[tabIndex] = new HashMap<Integer,Double>();
		cacheListsStatus[tabIndex] = new HashMap<Integer,Boolean>(); 
		Integer nodeID;
		Double cacheCapacity;
		boolean isServer;
		
		try {
		
		Object obj = (Object)parser.parse(new FileReader(inFile));
		JSONObject jobj = (JSONObject)obj;
		JSONArray cacheArrayJ = (JSONArray)jobj.get("node");
		Iterator<JSONObject> it = cacheArrayJ.iterator();
		while(it.hasNext()) {
			JSONObject cacheObj = it.next();						//get ID and capacity of each cache and add to tab's cacheLists[] hashmap 
			nodeID = Math.toIntExact((Long)cacheObj.get("nodeID"));	
			cacheCapacity = (Double)cacheObj.get("capacityBytes");
			//isServer = (boolean)cacheObj.get("isServer");
			isServer = ((Boolean)cacheObj.get("isServer")).booleanValue();
			cacheLists[tabIndex].put(nodeID, cacheCapacity);
			cacheListsStatus[tabIndex].put(nodeID, isServer);
		}//end while
		System.out.println("The total number of caches is: " + cacheLists[tabIndex].size());
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	//create the content placement map
	public static void readContentPlacementFromFileJson(File contentPlacementConfigurationFile){
		
		HashMap<String,HashMap<String,String>> placementMap = new HashMap<String,HashMap<String,String>>();
		
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(contentPlacementConfigurationFile.toString()));

			JSONObject jsonObject = (JSONObject) obj;

			//Loop on all nodes
			JSONArray listCaches = (JSONArray) jsonObject.get("placement");
			Iterator<JSONObject> iterator = listCaches.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObjectNode = (JSONObject)iterator.next();
				//Get the cache ID
				//int cacheID = (int)(long)jsonObjectNode.get("nodeID");
				int cacheID = ((Long)jsonObjectNode.get("nodeID")).intValue();
				//Get the cache status (is active or not)
				//boolean cacheActive = (boolean)jsonObjectNode.get("cacheActive");
				boolean cacheActive = ((Boolean)jsonObjectNode.get("cacheActive")).booleanValue();
				if(cacheActive == true){
					//Get the list of contents
					JSONArray listContents = (JSONArray)jsonObjectNode.get("content");
					//Create a new HashMap<Content,Boolean> object 
					HashMap<String,String> localContentMap = new HashMap<String,String>(); 
					Iterator<JSONObject> iteratorC = listContents.iterator();
					while (iteratorC.hasNext()) {
						JSONObject jsonObjectContent = (JSONObject)iteratorC.next();
						//Get content String ID
						//int contentID = (int)(long)jsonObjectContent.get("contentID");
						int contentID = ((Long)jsonObjectContent.get("contentID")).intValue();
						//Add a new entry in localContentMap
						localContentMap.put("ID:"+contentID,""+contentID);	
					}
					placementMap.put("ID:"+cacheID, localContentMap);
				}
				else{
					if(cacheActive == false){//add an empty entry
						placementMap.put("ID:"+cacheID, new HashMap<String,String>());
					}
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		contentPlacementMap[tabIndex] = placementMap;
		
	}//end readContentPlacementFromFileJson
	
	
	

	public int checkFileType(File file){ //checks files for valid extensions (txt or JSON)
		String extension = "";
		String fileName = file.getName();
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i+1);
		}
		//TOM: Modified here to permit JSON files. 
		if(extension.equalsIgnoreCase("txt")){
			return 1; //signifies text file input
		}
		else if(extension.equalsIgnoreCase("json")) {
			return 2; //signifies json file input
		}
		else{
			return 0;
		}
	}

	public static double maxValue(Map<Integer,Double> inMap){
		Double max = Collections.max(inMap.values());
		return max;
	}

	public static double minValue(Map<Integer,Double> inMap){
		Double min = Collections.min(inMap.values());
		return min;
	}

	public static double getAverageValue(Map<Integer,Double> inMap){
		double average;
		double sum = 0;
		double counter = 0;
		for (Entry<Integer, Double> entry : inMap.entrySet()){
			Double value = entry.getValue();
			sum += value;
			counter++;              
		}     
		average = sum / counter; 
		return average;	    
	}

	/**
	 * copy the visible part of the graph to a file as a jpeg image
	 * @param file
	 */
	public void writeJPEGImage(File file) {
		int width = server.getWidth();
		int height = server.getHeight();
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		server.paint(graphics);
		graphics.dispose();
		try {
			ImageIO.write(bi, "jpeg", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int print(java.awt.Graphics graphics,
			java.awt.print.PageFormat pageFormat, int pageIndex)
					throws java.awt.print.PrinterException {
		if (pageIndex > 0) {
			return (Printable.NO_SUCH_PAGE);
		} else {
			java.awt.Graphics2D g2d = (java.awt.Graphics2D) graphics;
			server.setDoubleBuffered(false);
			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());
			server.paint(g2d);
			server.setDoubleBuffered(true);
			return (Printable.PAGE_EXISTS);
		}
	}

	public static String usageInstructions(){ //Help menu option string 
		String usageInstructions =
				"<html>"+
						"<b><h2><center><u><font color=\"blue\">General usage tool instructions:</u></center></h2></b>"+
						"<p>&emsp; Initially, from the menu go to File->Import in order to import the appropriate files. "
						+ "These include:"+"<ul>"+
						"<li>The network topology file"+
						"<li>The content configuration file"+
						"<li>The caching configuration file"+
						"<li>The demand file"+
						"</ul>"+
						"<p style=\"color:red\"><b>&emsp;All the files above need to be in .json file format</b>"+
						"<p>"
						+ "<p>&emsp;After that, click on \"Create Network Topology\" button for the network topology visualisation."
						+ "<p>"+
						"<p>&emsp;From the menu you can select a server selection strategy by clicking to Select->Server Selection"
						+ " Strategy.<p>&emsp;The available options are:"
						+ "<ul>"
						+ "<li> Closest Cache - Minimum Distance"
						+ "<li>Round Robin"
						+ "<li> Optimum - GLPK."
						+ "</ul> "+
						"<p>&emsp;Also, you can select a content placement strategy from the menu by clicking to Select->Content Placement"
						+ " Strategy.<p>&emsp;The available options are:"
						+ "<ul>"
						+ "<li> Global Popularity Strategy (GPS)"
						+ "<li> Local Popularity Strategy (LPS)"
						+ "</ul> <p>"+
						"<p>&emsp;There is only one routing scheme option that is used and this is the \"Shorthest Path\". "+
						"<p>"+
						"<p style=\"color:red\">&emsp;<b>The default values for the above selection options are:</b>"
						+ "<ul>"
						+ "<li> Server Selection Strategy: Closest Cache - Minimum Distance"
						+ "<li> Content Placement Strategy: Local Popularity Strategy (LPS)"
						+ "<li> Routing Scheme: Shortest Path"+
						"</ul> <p>"+
						"<p>&emsp;By clicking on the \"Start Caching \" button the caching visualisation starts. <p>"+
						"<p>&emsp;On the right part of the screen a summary of the caching configuration that has selected / imported "
						+ "is appeared with <p>&emsp; the overall network and caching statistics."
						+ "<p>&emsp;More detailed network and caching statistics can be seen by clicking on the network nodes and "
						+ " edges or by hovering <p>&emsp;the mouse over these.<p>"
						+ "<p>&emsp;By selection Show->Cache Statistics or Show->Content Statistics from the menu, the "
						+ "<ul>"
						+ "<li>Maximum"
						+ "<li>Minimum"
						+ "<li>Average"
						+ "</ul>"
						+ "&emsp;values of the capacity of the caches installed or the size of the contents inserted are shown respectively. <p>"
						+ "<p>&emsp;Finally, from the Preferences on the Menu you can change the layout, the mouse mode and the zoom"
						+ "<p>&emsp;of the visualised network topology."+
						"<p><p>" + 
						"</html>";
		return usageInstructions;
	}
	//Print new info message of non cached info
	public static void updateInfo(){
		info1.setText("<font face=\"helvetica\" size=\"8\" color=\"blue\"> <b><u>"+ topologyName[tabIndex] +" Topology</u></b></font><br></br> <br></br>"
				+ "<font size=\"5\" color=\"red\"> <b><u>System Configuration</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\">"
				+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Selected Content Placement Strategy:</u></b></td><td>" + selectedConPlaceStrategy[tabIndex] +"</td></tr>" +
				"<tr><td><b><u>Selected Server Selection Strategy:</u></b></td><td>" + selectedServerSelStrategy[tabIndex] +"</td></tr>"
				+ "<tr><td><b><u>Selected Routing Sheme:</u></b></td><td>" + selectedRoutingScheme[tabIndex] +"</td></tr> </table> <br></br> <br></br>"
				+ "<i>Press Start Caching to start </i> </font>");
	}

	/**
	 * Update display statistics
	 */
	public static void updateInfoStatistics(){ //Set info panel as cached stats
		
		//set info text as cached stats
		info1.setText("<font face = face=\"helvetica\" size=\"8\" color=\"blue\"> <b><u>"+ topologyName[tabIndex] +" Topology</u></b></font><br></br> <br></br>"
				+"<font size=\"5\" color=\"red\"> <b><u>System Configuration</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\">"
				+ " <tr><td><font size=\"4\" color=\"black\"><b ><u>Selected Content Placement Strategy:</u></b></td><td>" + selectedConPlaceStrategy[tabIndex] +"</td></tr>" +
				"<tr><td><b><u>Selected Server Selection Strategy:</u></b></td><td>" + selectedServerSelStrategy[tabIndex] +"</td></tr>"
				+ "<tr><td><b><u>Selected Routing Sheme:</u></b></td><td>" + selectedRoutingScheme[tabIndex] +"</td></tr>"
				+ "<tr><td><b><u>Number of caching nodes:</u></b></td><td>" + nbNodes_.get(tabIndex).size() +"</td></tr>  "
				+ "<tr><td><b><u>Number of contents:</u></b></b></td><td>" + contentMetricsResults[tabIndex].get(1).intValue()  +"</td></tr>  "
				+ "<tr><td><b><u>Average Cache capacity:</u></b></b></td><td>" + cacheMetricsResults[tabIndex].get(2) +" Mbits"+"</td></tr>  "
				+ "<tr><td><b><u>Average Content Size:</u></b></b></td><td>" + contentMetricsResults[tabIndex].get(0) +" Mbits"+"</td></tr> </table>  "
				+ "<br></br> </font>"
				+ "<font size=\"5\" color=\"red\"> <b><u>Topological Metrics</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\">"
				+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Maximum Path Length: </u></b></td><td>" +  topologicalMetricsResults[tabIndex].get(1) +"</td></tr>"
				+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Minimum Path Length: </u></b></td><td>" + topologicalMetricsResults[tabIndex].get(0) + "</td></tr>" 
				+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Path Length:  </u></b></td><td>" + topologicalMetricsResults[tabIndex].get(2) +"</td></tr>" 
				+ "</table><br></br></font>"      
				+ "<font size=\"5\" color=\"red\"> <b><u>Network Statistics</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\"> "
				+ "<tr><td><font size=\"4\" color=\"black\"> <b><u>Maximum Network Utilisation:</u></b></td><td>" + netwMetricsResults[tabIndex].get(1) +" %"+ "</td></tr>"
				+ "<tr><td><b><u>Average Network Utilisation:</u></b></td><td>" + netwMetricsResults[tabIndex].get(2) + " %"+" </td></tr>"
				+ "<tr><td><b><u>Average Retrieval Delay (Hop Count):</u></b></td><td>" + netwMetricsResults[tabIndex].get(4) +" </td></tr>"
				+ "<tr><td><b><u>Total Network Load:</u></b></td><td>" + netwMetricsResults[tabIndex].get(3) + " Mbits"+" </td></tr>"
				+ "</table><br></br></font>"
				+ "<font size=\"5\" color=\"red\"> <b><u>Caching Statistics</u></b></font><br></br> <br></br> "
				+ "<table border=\"1\" style=\"width:300px\"> "
				+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Replication Degree:</u></b></td><td>" + cacheMetricsResults[tabIndex].get(2) + " </td></tr>"
				+ "<tr><td><b><u>Average Cache Hit Ratio:</u></b></td><td>" + cacheMetricsResults[tabIndex].get(0) + "</td></tr>"
				+ "<tr><td><b><u>Average Cache Occupancy:</u></b></td><td>" + Math.ceil(cacheMetricsResults[tabIndex].get(1)) + "</td></tr>"
				+ "</table></font>"
				);
	}
	
	/**
	 * Update display topology-related statistics
	 */
	public static void updateInfoStatisticsTopo(){ 
		
		//set info text as cached stats
		info1.setText("<font face = face=\"helvetica\" size=\"8\" color=\"blue\"> <b><u>"+ topologyName[tabIndex] +" Topology</u></b></font><br></br> <br></br>"
				+"<font size=\"5\" color=\"red\"> <b><u>Caching Configuration</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\">"
				+ "<tr><td><b><u>Selected Routing Sheme:</u></b></td><td>" + selectedRoutingScheme[tabIndex] +"</td></tr>"
				+ "<tr><td><b><u>Number of caching nodes:</u></b></td><td>" + nbNodes_.get(tabIndex).size() +"</td></tr> </table> "
				+ "<br></br> </font>"
				+ "<font size=\"5\" color=\"red\"> <b><u>Topological Metrics</u></b></font><br></br> <br></br>"
				+ "<table border=\"1\" style=\"width:300px\">"
				+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Maximum Path Length: </u></b></td><td>" +  topologicalMetricsResults[tabIndex].get(1) +"</td></tr>"
				+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Minimum Path Length: </u></b></td><td>" + topologicalMetricsResults[tabIndex].get(0) + "</td></tr>" 
				+ "<tr><td><font size=\"4\" color=\"black\"><b><u>Average Path Length:  </u></b></td><td>" + topologicalMetricsResults[tabIndex].get(2) +"</td></tr>" 
				+ "</table><br></br></font>"    
				+ "</table></font>"
				);
	}
	

	private static double format(double input){
		DecimalFormat df = new DecimalFormat("############.000");
		String str = df.format(input);
		double output = Double.parseDouble(str.replace(',', '.'));
		return output;
	}


	/**
	 * Function to print info box when hovering over a certain node
	 * @param v String hovered node identifier
	 * @return node statistics information
	 */
	public String Hover(String v){ 

		//ArrayList<String> listContent = new ArrayList<String>();
		int selectedCacheID = Integer.parseInt(v.toString().substring(5));
		if(startMetricPressed[tabIndex] == true)  {  		
			return("<html><center><b> NodeID:</b> "+ selectedCacheID 
					+"<p><b>Node Attribute:</b> "+ nt[tabIndex].getNodeType(selectedCacheID)
					+"<p><b>Cache Capacity:</b> " + cacheTable[tabIndex].get("["+selectedCacheID+"]").get(2) + " Mbits"
					+"<p><b>Cache Occupancy:</b> " + Math.ceil(cacheTable[tabIndex].get("["+selectedCacheID+"]").get(1)) + "%"
					+"<p><b>Number of Contents Stored Locally:</b> " + cacheTable[tabIndex].get("["+selectedCacheID+"]").get(3).intValue()
					+"<p><b>Requests Served Locally:</b> " + (double)Math.round((double)cacheTable[tabIndex].get("["+selectedCacheID+"]").get(0)*100 * 100) / 100 + " %" 
					+"<p><b>Degree of Connectivity:</b> " + nodeTable[tabIndex].get("["+selectedCacheID+"]").get(0) 
					+"<p><b>Clustering Coefficient:</b> " + nodeTable[tabIndex].get("["+selectedCacheID+"]").get(1) 
					+"<p><b>Average Distance Factor:</b> " + nodeTable[tabIndex].get("["+selectedCacheID+"]").get(2)
					+"</center></html>");
		}//if
		else{			
			return("<html><center><b> NodeID:</b> "+ selectedCacheID + "</center></html>");
		}
	}//end hover

	/**
	 * Dialog box for selecting an edge
	 * @param edge String selected edge identifier
	 */
	public void edgeInfo(String edge) {
		
		if(startMetricPressed[tabIndex] == true)  {  		
			
			Iterator<String> iter = edgeTable[tabIndex].keySet().iterator();
			String edgeID = new String();
			while(iter.hasNext()){
				edgeID = (String)iter.next();
				if(edge.equalsIgnoreCase(edgeID)){
					JDialog edgeInfoWindow = new JDialog(frame, "Information for link " + edge);
					edgeInfoWindow.setSize(500, 300);
					edgeInfoWindow.setLocationRelativeTo(null);
					edgeInfoWindow.getContentPane().setLayout(new GridLayout(1,1));
					edgeInfoWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
					JEditorPane edgeInfo =  new JEditorPane("text/html", "");
					edgeInfo.setEditable(false);
					Color mycolor = new Color(213,213,213);
					edgeInfo.setBackground(mycolor);

					edgeInfo.setText("<font size=\"5\" color=\"red\"> <b><u>Link Characteristics</u></b></font><br></br> <br></br>"
							+ "<table border=\"1\" style=\"width:300px\">"
							+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Administrative cost:</u></b></td><td>" +  edgeTable[tabIndex].get(edgeID).get(0) +"</td></tr>" +
							"<tr><td><b><u>Link Capacity:</u></b></td><td>" + edgeTable[tabIndex].get(edgeID).get(1)  +" Mbit"+"</td></tr></table> <br></br>"
							+ "</font>" + 
							"<font size=\"5\" color=\"red\"> <b><u>Link Statistics</u></b></font><br></br> <br></br>"
							+ "<table border=\"1\" style=\"width:300px\">"
							+ "<tr><td><font size=\"4\" color=\"black\"><b ><u>Link Utilisation:</u></b></td><td>" +  edgeTable[tabIndex].get(edgeID).get(3) +" %"+"</td></tr>" +
							"<tr><td><b><u>Link Load:</u></b></td><td>" + edgeTable[tabIndex].get(edgeID).get(2)  +" Mbits"+"</td></tr>  </table>"
							+ "</font>");
					edgeInfoWindow.add(edgeInfo);	
					edgeInfoWindow.pack();
					edgeInfoWindow.setVisible(true);
				}
			}//end while iter

		}//if  
		
	}//EdgeInfo

	/**
	 * Dialog box for hovering on an edge
	 * @param arg0 String hovered edge identifier
	 * @return hovered edge statistics information
	 */
	public String EdgeHover(String arg0){
		
		if(startMetricPressed[tabIndex] == true)  {  		
			
			Iterator<String> iter = edgeTable[tabIndex].keySet().iterator();
			String edgeID = new String();
			while(iter.hasNext()){
				edgeID = (String)iter.next();
				if(arg0.equals(edgeID)){
					return("<html><center><b> Link:</b> "+ arg0 + "<p><b>Administrative cost:</b> "  + edgeTable[tabIndex].get(edgeID).get(0) 
					+  " <p><b> Link Capacity:</b> "  + edgeTable[tabIndex].get(edgeID).get(1) + " Mbit"+" <p><b> Link Utilisation:</b> "
					+ edgeTable[tabIndex].get(edgeID).get(3) +" %" + " <p><b> Link Load:</b> "  + edgeTable[tabIndex].get(edgeID).get(2) + " Mbits"+"</center></html>");
				}
				
				
			}//for
			return ("<html><center><b> Link:</b> "+ arg0 +"</center></html>");
		}else{
			return ("<html><center><b> Link:</b> "+ arg0 +"</center></html>");
		}
	}//edgeHover

	
	/////////////////////////////////////////Append text functions/////////////////////////////////////
	/**
	 * Append to log
	 * @param s String appended text
	 */
	public void append(String s) {
		try {
			//Always add a newline if not present
			if(!s.contains("\n"))
				s+="\n";
			Document doc = log.getDocument();
			doc.insertString(doc.getLength(), s, null);

		} catch(BadLocationException exc) {
			exc.printStackTrace();
		}
	}
	/**
	 * Append to intro
	 * @param s String appended text
	 */
	public void appendIntro(String s) {
		try {
			//Always add a newline if not present
			if(!s.contains("\n"))
				s+="\n";
			Document doc = log.getDocument();
			StyleContext context = new StyleContext();
			// build a style
			Style style = context.addStyle("test", null);
			// set some style properties
			StyleConstants.setForeground(style, Color.BLUE);
			doc.insertString(doc.getLength(), s, style);
		} catch(BadLocationException exc) {
			exc.printStackTrace();
		}
	}
	/**
	 * Append to error
	 * @param s String appended text
	 */
	public void appendError(String s) {
		try {
			if(!s.contains("\n"))
				s+="\n";
			Document doc = log.getDocument();
			StyleContext context = new StyleContext();
			// build a style
			Style style = context.addStyle("test", null);
			// set some style properties
			StyleConstants.setForeground(style, Color.RED);
			doc.insertString(doc.getLength(), s, style);
		} catch(BadLocationException exc) {
			exc.printStackTrace();
		}
	}
	/**
	 * Append to warning
	 * @param s String appended text
	 */
	public void appendWarning(String s) {
		try {
			if(!s.contains("\n"))
				s+="\n";
			Document doc = log.getDocument();
			StyleContext context = new StyleContext();
			// build a style
			Style style = context.addStyle("test", null);
			// set some style properties
			StyleConstants.setForeground(style, new Color(0,153,76));
			doc.insertString(doc.getLength(), s, style);
		} catch(BadLocationException exc) {
			exc.printStackTrace();
		}
	}

	/////////////////////////////////////////Export functions/////////////////////////////////////

	/**
	 * @author tom
	 * New exportFile method saving text and JSON into 1 folder
	 */

	private void exportFile() {
		int simulCount = 0; //store a variable to keep each run of the simulation in a different folder (for different links etc);
		try {
			logFolderName = new File(fileLocation[tabIndex])+File.separator+"JSONLog"+File.separator; //create log folder handle
			File logFolder = new File(logFolderName); //log folder
			if(!logFolder.exists()) { //if folder does not exist then mkdir
				logFolder.mkdirs();
			}
			//create log folder for individual topology run 
			exportFolderName = topologyName[tabIndex] 
					+"_"+nbNodes_.get(tabIndex).size() +"N"
					+"_"+alpha[tabIndex]
							+"_"+beta[tabIndex]
									+"_"+selectedConPlaceStrategy[tabIndex]
									+"_"+selectedServerSelStrategy[tabIndex]
									+"_"+selectedRoutingScheme[tabIndex]
									+"_RUN"+ ++simulCount;
			saveFile = true;
			folderPermitted = false; //boolean for while loop
			File exportFolder = new File(logFolderName+exportFolderName); //create original file handle
			//if file exists ask to rename and then open new exportFolder
			while(!folderPermitted) { //loop until folder is an allowed/approved name
				if(!exportFolder.exists()) { //if does not exist/no naming issue then create
					exportFolder.mkdirs(); //make directory
					folderPermitted = true; //exit while
				}//end if
				else { //export folder already exists, prompt user if desired to rename
					JPanel fr = new JPanel();
					final JDialog jd = new JDialog();
					jd.setTitle("Previous Export of Caching Configuration Detected");
					fr.setLayout(new FlowLayout());
					JLabel text = new JLabel("Please change the folder name if you do not want to override previous data");
					final JTextField tf = new JTextField(exportFolderName);
					JButton confirm = new JButton("Confirm Name Change");
					JButton cancel = new JButton("Skip Name Change");
					JButton dns = new JButton("Do Not Save");
					confirm.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							String prevName = exportFolderName; //store previous name for "no change made" check
							exportFolderName = tf.getText();
							//routine to check if the user changed anything (effectively makes confirm and skip the same button)
							if(exportFolderName.equals(prevName)) {
								folderPermitted=true;
							}
							jd.setVisible(false);
							jd.dispose();
						}
					});//end confirm 

					dns.addActionListener(new ActionListener() { //user has elected not to save algo config
						public void actionPerformed(ActionEvent e) {
							jd.setVisible(false);
							jd.dispose();
							folderPermitted = true;
							saveFile = false; //does not permit folders to be exported
						}
					});

					cancel.addActionListener(new ActionListener(){ //save as standard name and overwrite potential previous data
						public void actionPerformed(ActionEvent e) {
							jd.setVisible(false);
							jd.dispose();
							folderPermitted = true;
							return;
						}
					});
					//add labels and boxes to text
					fr.add(text);
					fr.add(tf);
					fr.add(confirm);
					fr.add(cancel);
					fr.add(dns);
					jd.add(fr);
					jd.setSize(520,120);
					jd.setLocation(500,300);
					jd.setModalityType(ModalityType.APPLICATION_MODAL);
					jd.setResizable(false);
					jd.setVisible(true);
					exportFolder = new File(logFolderName+exportFolderName);
				} //end else
			}//end while
			//Folder is now valid - can proceed to save files
			if(saveFile) {
				File[] dataFile = new File[6];
				//filenames for each file to be written
				dataFile[0] = new File(exportFolder.getAbsolutePath()+File.separator+"TopologyVariables.txt");
				dataFile[1] = new File(exportFolder.getAbsolutePath()+File.separator+"ImportFiles.txt");
				dataFile[2] = new File(exportFolder.getAbsolutePath()+File.separator+"NetworkStatistics.txt");
				dataFile[3] = new File(exportFolder.getAbsolutePath()+File.separator+"NodeLevelData.txt");
				dataFile[4] = new File(exportFolder.getAbsolutePath()+File.separator+"LinkLevelData.txt");
				dataFile[5] = new File(exportFolder.getAbsolutePath()+File.separator+"CachingProfile.json");

				String output = null; //generic string to append output to
				BufferedWriter bw1 = new BufferedWriter(new FileWriter(dataFile[0]));
				output = ";NodeNumber="+String.valueOf(nbNodes_.get(tabIndex).size())
				+";ContentNumber="+String.valueOf(contentLists[tabIndex].size())
				+";ContentSize="+String.valueOf(contentLists[tabIndex].get(1))
				+";CacheCapacity="+String.valueOf(cacheLists[tabIndex].get(1))
				+";Alpha="+alpha[tabIndex]
						+";Beta="+beta[tabIndex]
								+";ContentPlacementStrategy="+selectedConPlaceStrategy[tabIndex]
								+";ServerSelectionStrategy="+selectedServerSelStrategy[tabIndex]
								+";RoutingScheme="+selectedRoutingScheme[tabIndex]
								+";Layout="+selectedLayout;
				
				bw1.write(output); bw1.close();
				String jsonOutput1 = JSONC.topologyVariableTxtToJson(dataFile[0].getAbsolutePath());
				append("JSON Output file "+jsonOutput1+" written to disk\n");
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(dataFile[1]));

				output = ";TopologyFile="+netTopFileJson[tabIndex].getAbsolutePath()
						+";CachingConfigurationFile="+cacheConfFileJson[tabIndex].getAbsolutePath()
						+";ContentConfigurationFile="+conFJson[tabIndex].getAbsolutePath()
						+ ";DemandFile="+demFJson[tabIndex].getAbsolutePath();
				
				bw2.write(output); bw2.close();
				String jsonOutput2 = JSONC.importFilesTxtToJson(dataFile[1].getAbsolutePath());
				append("JSON Output file "+jsonOutput2+" written to disk\n");

				BufferedWriter bw3 = new BufferedWriter(new FileWriter(dataFile[2]));

				output = ";MaximumPathLength="+topologicalMetricsResults[tabIndex].get(1)
				+";MinimumPathLength="+topologicalMetricsResults[tabIndex].get(0)
				+";AveragePathLength="+topologicalMetricsResults[tabIndex].get(2)
				+";MaximumNetworkUtilisation%="+netwMetricsResults[tabIndex].get(1)
				+";AverageNetworkDelay="+netwMetricsResults[tabIndex].get(4)
				+";TotalNetworkLoadMBits="+netwMetricsResults[tabIndex].get(3)
				+";AverageNetworkUtilisation="+netwMetricsResults[tabIndex].get(2)
				+";AverageReplicationDegree="+cacheMetricsResults[tabIndex].get(2)
				+";AverageCacheHitRatio="+cacheMetricsResults[tabIndex].get(0)
				;
				bw3.write(output); bw3.close();
				String jsonOutput3 = JSONC.networkStatisticsTxtToJson(dataFile[2].getAbsolutePath());
				append("JSON Output file "+jsonOutput3+" written to disk\n");

				BufferedWriter bw4 = new BufferedWriter(new FileWriter(dataFile[3].getAbsolutePath()));

				String[][] nodeAttributeData = nt[tabIndex].getData();
				//ArrayList<String> listContent = new ArrayList<String>();
				int numberContentsStoredLocally = 0;

				for(int i = 0; i<nbNodes_.get(tabIndex).size(); i++)
				{
					output = "|NodeID="+nodeAttributeData[0][i]
							+";NodeAttribute="+nodeAttributeData[1][i]
									+";CacheCapacityMBits="+cacheTable[tabIndex].get(2)+"Mbits"
									+";CacheOccupancy%="+cacheTable[tabIndex].get(0)
									+";NumberOfContentsStoredLocally="+numberContentsStoredLocally
									+";CacheHitRatioPercentage%="+cacheTable[tabIndex].get(1)
									+";ConnectivityDegree="+nodeTable[tabIndex].get(0) 
									+";ClusteringCoefficient="+nodeTable[tabIndex].get(1) 
									+";AverageDistanceFactor="+nodeTable[tabIndex].get(2)
									;
					bw4.write(output); 
				}
				bw4.close();
				String jsonOutput4 = JSONC.nodeLevelDataTxtToJson(dataFile[3].getAbsolutePath());
				append("JSON Output file "+jsonOutput4+" written to disk\n");

				BufferedWriter bw5 = new BufferedWriter(new FileWriter(dataFile[4]));
				
				Iterator<String> iter = edgeTable[tabIndex].keySet().iterator();
				String edgeID = new String();
				while(iter.hasNext()){
					edgeID = (String)iter.next();
					output = "|LinkStart="+edgeID.substring(1, edgeID.indexOf(","));
					output+= ";LinkEnd="+edgeID.substring(edgeID.indexOf(",")+1, edgeID.indexOf("]"));
					output+=";AdministrativeCost="+edgeTable[tabIndex].get(edgeID).get(0);
					output+=";LinkCapacityMBit="+edgeTable[tabIndex].get(edgeID).get(1);
					output+=";LinkUtilisation%="+edgeTable[tabIndex].get(edgeID).get(3);
					output+=";LinkLoad="+edgeTable[tabIndex].get(edgeID).get(2);
					bw5.write(output); 
				}

				bw5.close();
				String jsonOutput5 = JSONC.linkLevelDataTxtToJson(dataFile[4].getAbsolutePath());
				append("JSON Output file "+jsonOutput5+" written to disk\n");

				//Create nodeLevelData with caching profile
				File cacheStatInputFile = new File(jsonOutput4);
				cacheStatAppend(cacheStatInputFile,dataFile[5]);

			}//end if saveFile

		}//end try
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the previously created NodeLevelData.json and appends the cache statistics to the file
	 * @author Tom 
	 */
	@SuppressWarnings("unchecked")
	private static void cacheStatAppend(File nodeFile,File targetFile) {
		JSONParser parser = new JSONParser();
		try {
			Object o = parser.parse(new FileReader(nodeFile));
			JSONObject jobj = (JSONObject)o;
			JSONArray nodeArray = (JSONArray)jobj.get("node");
			Iterator<JSONObject> it = nodeArray.iterator();
			while(it.hasNext()) {
				JSONObject node = it.next(); //has ID value and contentArray will be appended to it
				JSONArray contentArray = new JSONArray(); //array to append to this object
				Integer selectedCacheID = Integer.parseInt((String)node.get("NodeID"));
				
				ArrayList<String> listContent = new ArrayList<String>(); //retrieve list of content for cache
				for(int i = 0; i< listCaches[tabIndex].size(); i++){
					if(selectedCacheID == Integer.parseInt(listCaches[tabIndex].get(i).substring(3))){
						if(contentPlacementMap[tabIndex].get(listCaches[tabIndex].get(i)).size()>0){
							Iterator<String> iter = contentPlacementMap[tabIndex].get(listCaches[tabIndex].get(i)).keySet().iterator();
							String contentID = new String();
							while(iter.hasNext()){
								contentID = (String)iter.next();
								listContent.add(contentID);
							}
						}
					}
				}
							
				for(int i = 0; i<listContent.size();i++) { //for every content in the list for this cache
					JSONObject content = new JSONObject();
					content.put("contentID",contentTable[tabIndex].get(listContent.get(i).substring(3)));
					content.put("globalRank",contentTable[tabIndex].get(listContent.get(i).substring(3)).get(1));
					content.put("replicationDegree",contentTable[tabIndex].get(listContent.get(i).substring(3)).get(0));
					contentArray.add(content);
				}//end for
				node.put("cachedContent", contentArray);
			}//end while
			PrintWriter pw = new PrintWriter(targetFile,"UTF-8");
			pw.write(jobj.toJSONString()); //write to file 
			if(pw.checkError()) {
				System.err.println("Program Exit: Printwriter Error");
				System.exit(1);
			}
			pw.flush(); //flush buffer 
			pw.close(); //close writer object 
		}//end try
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
	}

	/////////////////////////////////////////Add/Remove node/link JSON reformat functions/////////////////////////////////////

	public boolean isNumeric(String str)  
	{  
		try  
		{  
			Integer.parseInt(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}

	public static String[][] getNodeAttribute()
	{
		return nt[tabIndex].getData();
	}

	/**
	 * Extract the list of Node IDs from the Topology file and assign to nbNodes_[tabIndex]
	 * @author tom
	 * @param inFile
	 */

	public void getNodeIDs(File inFile) {
		Integer newNodeID = null;
		Set<Integer> uniqueNodeIDs = new HashSet<Integer>();
		nbNodes_.add(new ArrayList<Integer>()); //add ArrayList of nodes for this tab
		assert nbNodes_.get(tabIndex).isEmpty(); //ensure this index has no nodes
		try {
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject)parser.parse(new FileReader(inFile));
			JSONArray graph = (JSONArray)jObj.get("graph");
			Iterator<JSONObject> it = graph.iterator();
			while(it.hasNext()) {
				JSONObject link = it.next();
				newNodeID = Math.toIntExact((Long)link.get("start"));
				uniqueNodeIDs.add(newNodeID);
				newNodeID = Math.toIntExact((Long)link.get("end"));
				uniqueNodeIDs.add(newNodeID);
			}
			nbNodes_.get(tabIndex).addAll(uniqueNodeIDs);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ParseException e) {
			e.printStackTrace();;
		}
	}

	/**
	 * JSON Native version of link appending for new links 
	 * 
	 * @author Tom
	 */
	public static File addLinkToJsonTopology(int n1,int n2,int cost,int cap, int delay, File oldTopology) {
		String oldFileName = oldTopology.getName().replaceAll("\\d","");
		String newFileName = oldFileName.substring(0,oldFileName.lastIndexOf("."))+ ++topologyInstance[tabIndex]+".json";
		File newTopology = new File(tmpFolder+File.separator+newFileName);

		try  {//JSON Native version of Link appending
			PrintWriter pw = new PrintWriter(newTopology,"UTF-8");
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject) parser.parse(new FileReader(oldTopology));
			JSONArray graph = (JSONArray)jObj.get("graph");
			JSONObject newLink = new JSONObject();

			//build new link object and add to graph
			newLink.put("start",n1);
			newLink.put("end",n2);
			newLink.put("linkWeight",cost);
			newLink.put("linkCapacityKbps",cap);
			newLink.put("delay", delay);
			graph.add(newLink);

			//create new json topology file and write to disk
			JSONObject newJsonTopo = new JSONObject();
			newJsonTopo.put("name",jObj.get("name"));
			newJsonTopo.put("graph",graph);
			pw.write(newJsonTopo.toJSONString());
			pw.flush();
			pw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return newTopology;
	}
	/**
	 * JSON Native version of link removal 
	 * @author tom
	 *
	 */
	public static File removeLinkFromJsonTopology(int n1, int n2,File oldTopology) {
		String oldFileName = oldTopology.getName().replaceAll("\\d","");
		String newFileName = oldFileName.substring(0,oldFileName.lastIndexOf("."))+ ++topologyInstance[tabIndex]+".json";
		File newTopology = new File(tmpFolder+File.separator+newFileName);
		try {
			PrintWriter pw = new PrintWriter(newTopology,"UTF-8");
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject) parser.parse(new FileReader(oldTopology));
			JSONArray graph = (JSONArray)jObj.get("graph");
			JSONArray newGraph = new JSONArray();
			JSONObject newJsonTopo = new JSONObject(); //new jsonfile to be output
			newJsonTopo.put("name",jObj.get("name"));

			//now loop through graph array and add all links that aren't the n1-n2 link
			Iterator<JSONObject> it = graph.iterator();
			while(it.hasNext()) {
				JSONObject currentLink = it.next();
				int linkStart = Math.toIntExact((Long)currentLink.get("start"));
				int linkEnd = Math.toIntExact((Long)currentLink.get("end"));
				if(((linkStart==n1)&&(linkEnd==n2))||((linkStart==n2)&&(linkEnd==n1))){
					System.out.println("Link "+n1+" to "+n2+" removed from Topology");
				}//do nothing
				else {
					newGraph.add(currentLink);
				}
			}//end while
			newJsonTopo.put("graph",newGraph);
			pw.write(newJsonTopo.toJSONString());
			pw.flush();
			pw.close();
			jung.get(tabIndex).removeEdge(n1, n2); //remove the edges from the jung class edge object
		} catch(IOException e) {
			e.printStackTrace();
		} catch(ParseException e) {
			e.printStackTrace();
		}
		return newTopology;
	}

	/**
	 * Removes all links associated with nodeID from topology JSON file (netTopFileJson[])
	 * Returns true on success. Following usage the appropriate txt file must be generated
	 * New graph must then be generated
	 * @param nodeID
	 * @return
	 */
	public static File removeAllLinksFromTopologyJson(Integer nodeID,File oldTopology) {
		String oldFileName = oldTopology.getName().replaceAll("\\d","");
		String newFileName = oldFileName.substring(0,oldFileName.lastIndexOf("."))+ ++topologyInstance[tabIndex]+".json";
		File newTopologyFile = new File(tmpFolder+File.separator+newFileName);
		try {
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject)parser.parse(new FileReader(oldTopology));
			JSONObject newTopology = new JSONObject();
			newTopology.put("name",jObj.get("name"));//get name object from old topology
			JSONArray graph = (JSONArray)jObj.get("graph");
			JSONArray newGraph = new JSONArray();
			JSONObject edge; //object for current .next() object
			Iterator<JSONObject> it = graph.iterator();
			while(it.hasNext()) {
				edge = it.next();//get current edge
				Integer start = Math.toIntExact((Long)edge.get("start"));
				Integer end = Math.toIntExact((Long)edge.get("end"));
				if((start==nodeID)||(end==nodeID)) {
					jung.get(tabIndex).removeEdge(start,end); //remove link from JUNG Edge list
				} 
				else { //else add to topology
					newGraph.add(edge);
				}
			}
			newTopology.put("graph",newGraph);
			PrintWriter pw = new PrintWriter(newTopologyFile,"UTF-8");
			pw.write(newTopology.toJSONString());
			pw.flush();
			pw.close();
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		catch(ParseException e2) {
			e2.printStackTrace();
		}
		return newTopologyFile;
	}

	/**
	 * Method to add new node to caching file 
	 * Following usage the new text caching file must be generated
	 * Returns the file object of the new caching file
	 * @param nodeID
	 * @param capacity
	 * @param oldCachingFile
	 * @return
	 */
	public static File addNodeToCachingFileJson(int nodeID,double capacity, Boolean isServer, File oldCachingFile) {

		File newCachingFile = new File(tmpFolder+File.separator+oldCachingFile.getName());
		try {
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject) parser.parse(new FileReader(oldCachingFile));
			JSONArray cachingArray = (JSONArray)jObj.get("node");//get node array
			jObj.remove("node");//remove old caching array
			JSONObject newCache = new JSONObject();
			newCache.put("nodeID",nodeID);
			newCache.put("capacityBytes",capacity);
			newCache.put("isServer", isServer);
			cachingArray.add(newCache);
			jObj.put("node",cachingArray);//add new caching array with extra node
			PrintWriter pw = new PrintWriter(newCachingFile,"UTF-8");
			pw.write(jObj.toJSONString());
			pw.flush();
			pw.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
		return newCachingFile;
	}

	/**
	 * Method to remove node from caching file
	 * Following usage new text caching file must be created
	 * Returns file object of new caching file
	 * @param nodeID
	 * @param oldCachingFile
	 * @return
	 */
	public static File removeNodeFromCachingFileJson(int nodeID,File oldCachingFile) {
		File newCachingFile = new File(tmpFolder+File.separator+oldCachingFile.getName());
		try {
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject)parser.parse(new FileReader(oldCachingFile));
			JSONArray cachingArray = (JSONArray)jObj.get("node");
			jObj.remove("node");//remove old array
			Iterator<JSONObject> it = cachingArray.iterator();
			JSONObject currentNode;
			while(it.hasNext()) {
				currentNode=it.next();
				Integer currentID = Math.toIntExact((Long)currentNode.get("nodeID"));//get ID of current node
				if(currentID==nodeID) {
					it.remove(); //remove this element from array
				}
			}
			jObj.put("node",cachingArray); //add back to json object
			PrintWriter pw = new PrintWriter(newCachingFile,"UTF-8");
			pw.write(jObj.toJSONString());
			pw.flush();
			pw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return newCachingFile;
	}
	/**
	 * Method to add new node to node type file 
	 * Following usage the new text node type file must be generated
	 * Returns the file object of the new type file
	 * @param nodeID
	 * @param type
	 * @param oldTypeFile
	 * @return
	 */
	public static File addNodeToTypeFileJson(int nodeID,String type, File oldTypeFile) {
		File newTypeFile = new File(tmpFolder+File.separator+oldTypeFile.getName());
		try {
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject) parser.parse(new FileReader(oldTypeFile));
			JSONArray typeArray = (JSONArray)jObj.get("node");//get node array
			jObj.remove("node");//remove old type array
			JSONObject newType = new JSONObject();
			newType.put("nodeID",nodeID);
			newType.put("type",type);
			typeArray.add(newType);
			jObj.put("node",typeArray);//add new caching array with extra node
			PrintWriter pw = new PrintWriter(newTypeFile,"UTF-8");
			pw.write(jObj.toJSONString());
			pw.flush();
			pw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return newTypeFile;
	}

	/**
	 * Method to remove node from node type file
	 * Following usage the new text node type file must be generated
	 * Returns the file object of the new type file
	 * @param nodeID
	 * @param oldTypeFile
	 * @return
	 */
	public static File removeNodeFromTypeFileJson(int nodeID,File oldTypeFile) {
		File newTypeFile = new File(tmpFolder+File.separator+oldTypeFile.getName());
		try {
			JSONParser parser = new JSONParser();
			JSONObject jObj = (JSONObject)parser.parse(new FileReader(oldTypeFile));
			JSONArray typeArray = (JSONArray)jObj.get("node");
			jObj.remove("node");//remove old array
			Iterator<JSONObject> it = typeArray.iterator();
			JSONObject currentNode;
			while(it.hasNext()) {
				currentNode=it.next();
				Integer currentID = Math.toIntExact((Long)currentNode.get("nodeID"));//get ID of current node
				if(currentID==nodeID) {
					it.remove(); //remove this element from array
				}
			}
			jObj.put("node",typeArray); //add back to json object
			PrintWriter pw = new PrintWriter(newTypeFile,"UTF-8");
			pw.write(jObj.toJSONString());
			pw.flush();
			pw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return newTypeFile;
	}

	
}//class Graphical Interface

