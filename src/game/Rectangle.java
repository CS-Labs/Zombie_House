package game;



/**
 * @author Christian Seely
 * Rectangle class represents each room or region.
 * it contains the x/y coordinates of the start of 
 * the room (top left corner) and the height and
 * width of the room. 
 * 
 *
 */
public class Rectangle {
	private int x;
	private int y;
	private int width;
	private int height;


	/**
	 * 
	 * @param x start x location in top left corner. 
	 * @param y start y location in top left corner. 
	 * @param width of room
	 * @param height of room
	 */
	Rectangle(int x,int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		

		
	}
	/**
	 * 
	 * @param width of room/region
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}
	/**
	 * 
	 * @param height of room/region
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}
	/**
	 * 
	 * @param x Top left corner x. 
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	/**
	 * 
	 * @param y Top left corner y. 
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	/**
	 * 
	 * @return x from top left corner. 
	 */
	public int getX()
	{
		return this.x;
	}
	/**
	 * 
	 * @return y from top left corner. 
	 */
	public int getY()
	{
		return this.y;
	}
	/**
	 * 
	 * @return width of region/room
	 */
	public int getWidth()
	{
		return this.width;
	}
	/**
	 * 
	 * @return height of region/room
	 */
	public int getHeight()
	{
		return this.height;
	}
}