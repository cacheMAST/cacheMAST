package cacheMAsT;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.collections15.Transformer;

public class EdgePainter implements Transformer<String, Paint>
{
	private Hashtable<String, ArrayList<Double>> mNodeDataTable;
	private boolean hasCached;
	
	public EdgePainter(Hashtable<String, ArrayList<Double>> edgeTable)
	{
		this.mNodeDataTable = edgeTable;

	}
	
	public EdgePainter() {
		mNodeDataTable = new Hashtable<String,ArrayList<Double>>();
		hasCached = false;
	}
	
	public void setCached(boolean opt) {
		hasCached = opt;
	}

	public void setTable(Hashtable<String, ArrayList<Double>> edgeTable) {
		mNodeDataTable = edgeTable;
	}
	
	public Paint transform(String v) //So for each node that we draw...
	{
		//We check the member variable, mColor, of the node. By first sending our key, in this case v
		// to our nodeDataTable to get our value from the table. Immediately we can call .getColor() 
		// to get the mColor value of our node, which in our case is actually a String, so we do a string compare.
		if(mNodeDataTable.containsKey(v)&&hasCached) {
			if ((mNodeDataTable.get(v).get(3) >= 0.0) && (mNodeDataTable.get(v).get(3) <= 10.0))
				return new Color(30,144,255); //If the edge's utilisation is 0-10 the edge color is dodger blue
			else if ((mNodeDataTable.get(v).get(3) > 10.0) && (mNodeDataTable.get(v).get(3) <= 20.0))
				return new Color(255,250,205);//If the edge's utilisation is 0-10 the edge color is lemon chiffon
			else if ((mNodeDataTable.get(v).get(3) > 20.0) && (mNodeDataTable.get(v).get(3) <= 30.0))
				return new Color(255,255,0);//If the edge's utilisation is 0-10 the edge color is yellow	
			else if ((mNodeDataTable.get(v).get(3) > 30.0) && (mNodeDataTable.get(v).get(3) <= 40.0))
				return new Color(255,215,0);//If the edge's utilisation is 0-10 the edge color is gold
			else if ((mNodeDataTable.get(v).get(3) > 40.0) && (mNodeDataTable.get(v).get(3) <= 50.0))
				return new Color(255,153,51);//If the edge's utilisation is 0-10 the edge color is dark orange
			else if ((mNodeDataTable.get(v).get(3) > 50.0) && (mNodeDataTable.get(v).get(3) <=60.0))
				return new Color(255,111,111);//If the edge's utilisation is 0-10 the edge color is tomato
			else if ((mNodeDataTable.get(v).get(3) > 60.0) && (mNodeDataTable.get(v).get(3) <=70.0))
				return new Color(255,69,0);//If the edge's utilisation is 0-10 the edge color is oranged red
			else if ((mNodeDataTable.get(v).get(3) > 70.0) && (mNodeDataTable.get(v).get(3) <=80.0))
				return new Color(210,105,30);//If the edge's utilisation is 0-10 the edge color is chocolate
			else if ((mNodeDataTable.get(v).get(3) > 80.0) && (mNodeDataTable.get(v).get(3) <=90.0))
				return new Color(178,34,34);//If the edge's utilisation is 0-10 the edge color is fire brick
			else if ((mNodeDataTable.get(v).get(3) > 90.0) && (mNodeDataTable.get(v).get(3) <=100.0))
				return new Color(128,0,0);//If the edge's utilisation is 90-100 the edge color is maroon
			else
				return (Color.black);
		}
		else 
			return (Color.black);
	}
}//class EdgePainter
