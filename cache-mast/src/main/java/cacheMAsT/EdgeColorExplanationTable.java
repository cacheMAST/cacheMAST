package cacheMAsT;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class EdgeColorExplanationTable {

    private static Object[] columnName = {"Link Color", "Utilisation"};
    private static Object[][] data = {
            {"dodger blue", " Utilisation 0.0 - 10.0"},
            {"lemon chiffon", " Utilisation 10.0 - 20.0"},
            {"yellow", " Utilisation 20.0 - 30.0"},
            {"gold", " Utilisation 30.0 - 40.0"},
            {"dark orange", " Utilisation 40.0 - 50.0"},
            {"tomato", " Utilisation 50.0 - 60.0"},
            {"oranged red", " Utilisation 60.0 - 70.0"},
            {"chocolate", " Utilisation 70.0 - 80.0"},
            {"fire brick", " Utilisation 80.0 - 90.0"},
            {"maroon", " Utilisation 90.0 - 100.0"}
    };


    public static void createColorExplanationTable() {
        Runnable r = new Runnable() {

            @Override
            public void run() {

                JFrame frame = new JFrame();
                JTable table = new JTable(data, columnName);
                table.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer());
                table.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
                table.setRowHeight(30);
                table.getTableHeader().setReorderingAllowed(false);

                frame.add(new JScrollPane(table));
                frame.setTitle("Edge Color Explanation Table");
                frame.setSize(350, 350);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);
                //frame.pack();
                frame.setVisible(true);
            }
        };

        EventQueue.invokeLater(r);
    }
}


class CustomRenderer extends DefaultTableCellRenderer 
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

}

