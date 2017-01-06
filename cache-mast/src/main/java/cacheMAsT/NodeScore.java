package cacheMAsT;

/**
 * The class represents a 2-tupe NString nodeID - double score
 * 
 * @author D T
 *
 */

public class NodeScore implements Comparable<NodeScore>{

	
	private String nodeID;
	private double score;
	
	/**
	 * Constructor
	 */
	public NodeScore(){
	}
	/**
	 * Constructor
	 */	
	public NodeScore(String nodeID, double score){
		this.nodeID = nodeID;
		this.score = score;
	}
	
	/**
	 * Setters
	 */
	public void setNodeID(String nodeID){
		this.nodeID = nodeID;
	}
	public void setScore(double score){
		this.score = score;
	}
	/**
	 * Getters
	 */	
	public String getNodeID(){
		return this.nodeID;
	}
	public double getScore(){
		return this.score;
	}
	
	/**
	 * Override the compareTo method
	 */
	public int compareTo(NodeScore nodeScoreToCompare) {
		double scoreToCompare = nodeScoreToCompare.getScore();
		if (this.score < scoreToCompare)
			  return -1;
			else if (scoreToCompare > this.score)
			  return 1;
			else
			  return 0;
	}
	
	
	
}

