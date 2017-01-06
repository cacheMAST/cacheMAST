package cacheMAsT;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.annotations.AnnotatingModalGraphMouse;
import edu.uci.ics.jung.visualization.annotations.AnnotationControls;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;

 
  @SuppressWarnings("serial")
  public class GraphPropertiesEditor<V, E> extends JApplet {
  
      String instructionsMouseMode =
    	        "<html>"+
    	        "<b><h2><center><u>Instructions for Picking Mode:</u></center></h2></b>"+
    	        "<ul>"+
    	        "<li>Mouse1 on a Vertex selects the vertex"+
    	        "<li>Mouse1 elsewhere unselects all Vertices"+
    	        "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"+
    	        "<li>Mouse1+drag on a Vertex moves all selected Vertices"+
    	        "<li>Mouse1+drag elsewhere selects Vertices in a region"+
    	        "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"+
    	        "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it"+
    	        "<li>Mouse1 double-click on a vertex or edge allows you to edit the label"+
    	        "</ul>"+
    	        "<b><h2><center><u>Instructions for Transforming Mode:</u></center></h2></b>"+
    	        "<ul>"+
    	        "<li>Mouse1+drag pans the graph"+
    	        "<li>Mouse1+Shift+drag rotates the graph"+
    	        "<li>Mouse1+CTRL(or Command)+drag shears the graph"+
    	        "<li>Mouse1 double-click on a vertex or edge allows you to edit the label"+
    	        "</ul>"+
    	        "<b><h2><center><u>Instructions for Annotations:</u></center></h2></b>"+
    	          "<p>&emsp;The Annotation Controls allow you to select:"+
    	          "<ul>"+
    	          "<li>Shape"+
    	          "<li>Color"+
    	          "<li>Fill (or outline)"+
    	          "<li>Above or below (UPPER/LOWER) the graph display"+
    	          "</ul>"+
    	          "<p>&emsp;Mouse Button one press starts a Shape,"+
    	          "drag and release to complete."+
    	          "<p>&emsp;Mouse Button three pops up an input dialog "+
    	          "for text. This will create a text annotation."+
    	          "<p>&emsp;You may use html for multi-line, etc."+
    	          "<p>&emsp;You may even use an image tag and image url "+
    	          "to put an image in the annotation."+
    	          "<p>"+
    	          "<p>&emsp;To remove an annotation, shift-click on it "+
    	          "in the Annotations mode."+
    	          "<p>&emsp;If there is overlap, the Annotation with center "+
    	          "closest to the mouse point will be removed."+
    	          "<p><p>" + 
    	        "</html>";
      
      JDialog helpDialog;
      
      Paintable viewGrid;
      
      /**
       * create an instance of a simple graph in two views with controls to
       * demo the features.
       * 
       */
      public GraphPropertiesEditor(final VisualizationViewer<String,String> vv) {
          
         final Container content = getContentPane();
         
         helpDialog = new JDialog();
         JLabel JLbl = new JLabel(instructionsMouseMode);
         JScrollPane scrollPane = new JScrollPane(JLbl);
         helpDialog.getContentPane().add(scrollPane);

         RenderContext<String,String> rc = vv.getRenderContext();
         AnnotatingGraphMousePlugin<String,String> annotatingPlugin =
         	new AnnotatingGraphMousePlugin<String,String>(rc);
        
         final AnnotatingModalGraphMouse<String,String> graphMouse = 
         	new AnnotatingModalGraphMouse<String,String>(rc, annotatingPlugin);
         vv.setGraphMouse(graphMouse);
         vv.addKeyListener(graphMouse.getModeKeyListener());
         
         final ScalingControl scaler = new CrossoverScalingControl();
 
         JButton plus = new JButton("+");
         plus.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 scaler.scale(vv, 1.1f, vv.getCenter());
             }
         });
         JButton minus = new JButton("-");
         minus.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 scaler.scale(vv, 1/1.1f, vv.getCenter());
             }
         });
         
         JComboBox modeBox = graphMouse.getModeComboBox();
         modeBox.setSelectedItem(ModalGraphMouse.Mode.PICKING);
         
         JButton help = new JButton("Help");
         help.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 helpDialog.pack();
                 helpDialog.setVisible(true);
             }
         });
 
         JPanel controls = new JPanel();
         JPanel zoomControls = new JPanel();
         zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
         zoomControls.add(plus);
         zoomControls.add(minus);
         controls.add(zoomControls);
         
         JPanel modeControls = new JPanel();
         modeControls.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
         modeControls.add(graphMouse.getModeComboBox());
         controls.add(modeControls);
         
         JPanel annotationControlPanel = new JPanel();
         annotationControlPanel.setBorder(BorderFactory.createTitledBorder("Annotation Controls"));
         
         AnnotationControls<String,String> annotationControls = 
             new AnnotationControls<String,String>(annotatingPlugin);
         
         annotationControlPanel.add(annotationControls.getAnnotationsToolBar());
         controls.add(annotationControlPanel);
         
         JPanel helpControls = new JPanel();
         helpControls.setBorder(BorderFactory.createTitledBorder("Help"));
         helpControls.add(help);
         controls.add(helpControls);
         content.add(controls, BorderLayout.WEST);
     }
     
 }

