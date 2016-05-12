package game;

import java.util.Comparator;
/**
 * 
 * @author Christian Seely
 * Class to act as a comparator between two nodes, it is used
 * for a priority queue which sorts graph nodes from least to most cost. 
 */
public class CostComparator implements Comparator<GraphNode>
{
    //Used to compare the cost between two graphNodes 
    public int compare(GraphNode o1, GraphNode o2) 
    {
      return o1.getCost() - o2.getCost();

    }
  }

