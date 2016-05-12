package game;

/**
 * @author Christian Seely
 * The Node class represents a node in the binary
 * search tree each node has a room and left and right child
 * as you traverse the tree the deeper the level is the more broken
 * up the overall map is. For example the top of the tree (the root)
 * is the starting map while all the leaf nodes contains the
 * final set of rooms. 
 *
 */
public class Node {

	private Rectangle room; 
	private Node leftChild;
	private Node rightChild;
	
	/**
	 * 
	 * @param Each node in the tree represents a room
	 * (where the leaf nodes are the final rooms we want)
	 */
	Node(Rectangle room)
	{
		this.room = room;
		this.leftChild = null;
		this.rightChild = null;
	}
	/**
	 * 
	 * @return The room associated with this node.
	 */
	public Rectangle getRoom()
	{
		return this.room;
	}
	/**
	 * 
	 * @return Nodes left child
	 */
	public Node getLeftChild()
	{
		return leftChild;
	}
	/**
	 * 
	 * @return Nodes right child.
	 */
	public Node getRightChild()
	{
		return rightChild;
	}
	/**
	 * 
	 * @param left child node. 
	 */
	public void setLeftChild(Node left)
	{
		this.leftChild = left;
	}
	/**
	 * 
	 * @param right child node. 
	 */
	public void setRightChild(Node right)
	{
		this.rightChild = right;
	}
	
	
}