package game;

import java.util.ArrayList;
/**
 * 
 * @author Christian Seely
 * This class represents a node in the navigational mesh which is used
 * for path finding in the game. 
 * 
 *
 */
public class GraphNode
{
    private int xLoc;
    private int zLoc;
    private int cost;
    //Neighboring nodes in the graph, a max possible of 8. 
    private ArrayList<GraphNode> neighbors = new ArrayList<>();
    /**
     * 
     * @param xLoc On the map
     * @param zLoc On the map
     */
    public GraphNode(int xLoc, int zLoc) 
    {
      this.cost = 0; 
      this.neighbors = new ArrayList<>();
      this.xLoc = xLoc;
      this.zLoc = zLoc;
    }

    @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(xLoc);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(zLoc);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

    
    
  @Override
  public boolean equals(Object obj)
  {
    //Two nodes are equal if they have the same x/z loc. 
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GraphNode other = (GraphNode) obj;
    if (Double.doubleToLongBits(xLoc) != Double.doubleToLongBits(other.xLoc))
      return false;
    if (Double.doubleToLongBits(zLoc) != Double.doubleToLongBits(other.zLoc))
      return false;
    return true;
  }


    
    @Override
    /**
     * @return formated coordinate string. 
     */
    public String toString()
    {
      return "("+xLoc + "," + zLoc+")";
    }
    /**
     * 
     * @param node Graph node in map
     * Add a graph node to a nodes neighbor list. 
     */
    public void addNeighbor(GraphNode node)
    {
      this.neighbors.add(node);
    }
    /**
     * 
     * @return A nodes neighbor list. 
     */
    public ArrayList<GraphNode> getNeighbors()
    {
      return this.neighbors;
    }
    /**
     * 
     * @return the X location. 
     */
    public int getXLoc()
    {
      return this.xLoc;
    }
    /**
     * 
     * @return the Z location. 
     */
    public int getZLoc()
    {
      return this.zLoc;
    }
    /**
     * 
     * @return Cost thus far in path associated with this node. 
     */
    public int getCost()  
    {
      return this.cost;
    }
    /**
     * 
     * @param cost of path to this node. 
     */
    public void setCost(int cost)
    {
      this.cost = cost;
    }
    
    


  }

  

