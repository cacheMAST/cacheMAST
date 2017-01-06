package cacheMAsT;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


public class EdgeColorExplanationTableOnGraph {

    private static Object[] columnName = {"Link Color", "Utilisation"};
    private static Object[][] data = {
            {"dodger blue", "0% - 10%"},
            {"lemon chiffon", "10% - 20%"},
            {"yellow", "20% - 30%"},
            {"gold", "30% - 40%"},
            {"dark orange", "40% - 50%"},
            {"tomato", "50% - 60%"},
            {"oranged red", "60% - 70%"},
            {"chocolate", "70% - 80%"},
            {"fire brick", "80% - 90%"},
            {"maroon", "90% - 100%"}
    };


    public static JPanel createColorExplanationTable() {
    	  
    	JPanel panel = new JPanel();
    	JTable table = new JTable(data, columnName);               
    	table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
    	table.getColumnModel().getColumn(0).setCellRenderer(new CustomRendererNew());
    	table.getColumnModel().getColumn(1).setCellRenderer(new CustomRendererNew());
    	table.setRowHeight(10);
    	TableColumn column = table.getColumnModel().getColumn(0);
    	column.setPreferredWidth(60);
               
    	final TableCellRenderer tcrOs = table.getTableHeader().getDefaultRenderer();
    	table.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
    		@Override
    		public Component getTableCellRendererComponent(JTable table, 
    				Object value, boolean isSelected, boolean hasFocus, 
    				int row, int column) {
    			JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table, 
    					value, isSelected, hasFocus, row, column);
    			if(column == 0){
    				lbl.setHorizontalAlignment(SwingConstants.RIGHT);
    			}
    			else{
    				lbl.setHorizontalAlignment(SwingConstants.LEFT);
    			}
    			return lbl;
    		}
    	});

    	panel.add(new JScrollPane(table));
    	panel.setSize(145,125);
    	panel.setVisible(true);
                       
		return panel;
    }
}//end class EdgeColorExplanationTableOnGraph


class CustomRendererNew extends DefaultTableCellRenderer 
{

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if(table.getValueAt(row, column).equals("dodger blue")){
            cellComponent.setBackground(new Color(30,144,255));
        } else if(table.getValueAt(row, column).equals("lemon chiffon")){
            cellComponent.setBackground(new Color(255,250,205));
        }
        else if(table.getValueAt(row, column).equals("yellow")){
            cellComponent.setBackground(new Color(255,255,0));
        }
        else if(table.getValueAt(row, column).equals("gold")){
            cellComponent.setBackground(new Color(255,215,0));
        }
        else if(table.getValueAt(row, column).equals("dark orange")){
            cellComponent.setBackground(new Color(255,153,51));
        }
        else if(table.getValueAt(row, column).equals("tomato")){
            cellComponent.setBackground(new Color(255,111,111));
        }
        else if(table.getValueAt(row, column).equals("oranged red")){
            cellComponent.setBackground(new Color(255,69,0));
        }
        else if(table.getValueAt(row, column).equals("chocolate")){
            cellComponent.setBackground(new Color(210,105,30));
        }
        else if(table.getValueAt(row, column).equals("fire brick")){
            cellComponent.setBackground(new Color(178,34,34));
        }
        else if(table.getValueAt(row, column).equals("maroon")){
            cellComponent.setBackground(new Color(128,0,0));
        }
        return cellComponent;
    }
}//end class CustomRendererNew



